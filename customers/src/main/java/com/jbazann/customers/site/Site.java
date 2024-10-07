package com.jbazann.customers.site;

import com.jbazann.customers.site.dto.SiteDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true, fluent = true)
@Entity
@Table(name = "site", schema = "site")
@Builder
public class Site {

    @Id
    @NotNull
    private UUID id;
    @NotNull @NotEmpty
    private String address;
    @NotNull @NotEmpty
    private String coordinates;
    @NotNull @Min(0)
    private BigDecimal budget;
    @NotNull
    private SiteStatus status;
    @NotNull
    private UUID customer;

    public boolean canTransitionTo(@NotNull SiteStatus newStatus) {
        return status.equals(SiteStatus.PENDING) && newStatus.equals(SiteStatus.ACTIVE)
                ||
                status.equals(SiteStatus.ACTIVE) && newStatus.equals(SiteStatus.FINISHED);
    }

    public enum SiteStatus{
        ACTIVE, PENDING, FINISHED;
    }

    public SiteDTO toDto() {
        return new SiteDTO(id, address, coordinates, budget, status, customer);
    }

}