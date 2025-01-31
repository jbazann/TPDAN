package dev.jbazann.skwidl.customers.site;

import dev.jbazann.skwidl.customers.customer.CustomerService;
import dev.jbazann.skwidl.customers.site.exceptions.InvalidSiteException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
public class SiteService {

    private final SiteRepository siteRepository;
    private final SiteStatusService siteStatusService;

    public SiteService(final SiteRepository siteRepository, SiteStatusService siteStatusService) {
        this.siteRepository = siteRepository;
        this.siteStatusService = siteStatusService;
    }

    /**
     * Site ID provider. Values generated by this method should be
     * expected to be unique and valid for registering new Site instances.
     * @return a unique site entity identifier.
     */
    public UUID generateSiteId() {
        return UUID.randomUUID(); // TODO replace with safe alternative
    }

    /**
     * Persist a new valid site on the database.
     * @param site a site with an ID that is not already used.
     * @return the persisted instance.
     */
    public Site newSite(@Valid @NotNull Site site) {
        if (siteRepository.existsById(site.id())) {
            throw new InvalidSiteException("Site with id " + site.id() + " already exists.");
        }
        siteStatusService.setInitialStatus(site);
        return siteRepository.save(site);
    }

    /**
     * Refer to {@link SiteStatusService#updateSiteStatus(Site, Site.SiteStatus)}.
     * @param siteId a site ID
     * @param newStatus the status to try to update to
     */
    @Transactional()// TODO
    public void updateSiteStatus(@NotNull UUID siteId, @NotNull Site.SiteStatus newStatus) {
        Site site = fetchSite(siteId);
        siteStatusService.updateSiteStatus(site, newStatus);
        siteRepository.save(site);
    }

    /**
     * Find a list of up to 5 sites matching the provided example.
     * This was made this way for simplicity's sake, only intended for queries
     * expected to match with a single site.
     * @param site an example site with null fields, except for those intended to be matched against.
     * @return a size-limited list of matching results.
     */
    public List<Site> findSitesByExample(@NotNull Site site) {
        return siteRepository.findAll(Example.of(site), Pageable.ofSize(5)).toList();
    }

    /**
     * Temporary internal method to let {@link CustomerService} activate pending sites when capacity is made available.
     * @implNote Annotated as transactional due to self-invocation of {@link SiteService#updateSiteStatus(UUID, Site.SiteStatus)}.
     * This operation should be coordinated asynchronously through events in some final implementation.
     * @param customerId a customer ID which may or may not have spare capacity to activate new sites.
     */
    @Transactional
    public void activatePendingSites(@NotNull UUID customerId) {
        // fetch some pending sites (ideally go through all of them in the future)
        Example<Site> query = Example.of(new Site().customer(customerId).status(Site.SiteStatus.PENDING));
        Iterator<Site> sites = siteRepository.findAll(query, Pageable.ofSize(5)).iterator();

        // try activating sites until max capacity
        boolean previousSucceeded = true;
        while(sites.hasNext() && previousSucceeded) {
            final Site site = sites.next();
            // this is the only necessarily transactional operation
            updateSiteStatus(site.id(), Site.SiteStatus.ACTIVE);
            // if status was updated, there may be room for one more, so try another
            previousSucceeded = site.status().equals(Site.SiteStatus.ACTIVE);
        }
    }

    private Site fetchSite(@NotNull UUID id) {
        return siteRepository.findById(id).orElseThrow(
                () -> new InvalidSiteException("Site "+ id +" not found.")
        );
    }

}
