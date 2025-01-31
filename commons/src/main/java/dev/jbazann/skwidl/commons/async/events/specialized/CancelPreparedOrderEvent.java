package dev.jbazann.skwidl.commons.async.events.specialized;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data()
@Accessors(chain = true, fluent = true)
@EqualsAndHashCode(callSuper = true)
public class CancelPreparedOrderEvent extends DomainEvent {

    private UUID orderId;
    private UUID customerId;
    private BigDecimal returnedFunds;
    private Map<UUID, Integer> returnedStock;

    @Override
    protected DomainEvent copy() {
        return new CancelPreparedOrderEvent()
                .orderId(orderId)
                .customerId(customerId)
                .returnedFunds(returnedFunds)
                .returnedStock(returnedStock);
    }

}
