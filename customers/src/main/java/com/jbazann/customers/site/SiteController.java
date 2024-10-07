package com.jbazann.customers.site;

import com.jbazann.customers.site.dto.SiteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/site")
public class SiteController {

    private final SiteService siteService;

    @Autowired
    public SiteController(final SiteService siteService) {
        this.siteService = siteService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<SiteDTO>> getSite(@RequestParam("id") final UUID id,
                                                 @RequestParam("status") final Site.SiteStatus status,
                                                 @RequestParam("customer_id") final UUID customerId)
    {
        if (id == null && customerId == null && status == null) {
            throw new IllegalArgumentException("Must provide at least one of: id, status, customer_id.");
        }
        return ResponseEntity.ok(
                siteService.findSitesByExample(new Site().id(id).status(status).customer(customerId))
                        .stream().map(Site::toDto).toList()
        );
    }

    @PostMapping()
    public ResponseEntity<SiteDTO> createSite(@RequestBody final SiteDTO site) {
        site.id(siteService.generateSiteId());
        return ResponseEntity.ok(siteService.newSite(site.toEntity()).toDto());
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> updateSiteStatus(@PathVariable("id") final UUID id,
                                                    @RequestBody final Site.SiteStatus status) {
        siteService.updateSiteStatus(id, status);
        return ResponseEntity.ok().build();
    }

}