package com.jbazann.orders.commons.async.transactions;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true, fluent = true)
public class TransactionResult {

    private SimpleResult simpleResult;
    private TransactionData data;
    private String context;

    public enum SimpleResult {
        SUCCESS, FAILURE, CRITICAL_FAILURE, REGISTRY_FAILURE
    }

}
