package com.jbazann.orders.commons.async.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.UUID;

@Data()
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode(callSuper = true)
public class DeliverOrderEvent extends DomainEvent {

    private UUID orderId;
    private UUID customerId;
    private BigDecimal returnedFunds;

    @Override
    protected DomainEvent copy() {
        return new DeliverOrderEvent()
                .orderId(orderId)
                .customerId(customerId)
                .returnedFunds(returnedFunds);
    }
}
