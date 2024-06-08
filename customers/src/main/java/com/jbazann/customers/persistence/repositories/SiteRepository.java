package com.jbazann.customers.persistence.repositories;

import com.jbazann.customers.domain.entities.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SiteRepository extends JpaRepository<Site, UUID> {
}
