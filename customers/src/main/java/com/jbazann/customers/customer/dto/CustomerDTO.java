package com.jbazann.customers.customer.dto;

import com.jbazann.customers.customer.Customer;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true, fluent = true)
public class CustomerDTO {

    private UUID id;
    private String name;
    private String email;
    private String cuit;
    private BigDecimal maxDebt;
    private Integer maxActiveSites;
    private List<UUID> enabledUsers;
    private List<UUID> enabledSites;
    private Integer pendingSites;


    public Customer toEntity() {
        return new Customer(id, name, email, cuit, maxDebt, maxActiveSites, enabledUsers, enabledSites, pendingSites);
    }

}
