package dev.jbazann.skwidl.commons.async.transactions;

import dev.jbazann.skwidl.commons.async.transactions.entities.Transaction;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true, fluent = true)
public class TransactionResult {

    private SimpleResult simpleResult;
    private Transaction data;
    private String context;

    public enum SimpleResult {
        SUCCESS, WARNED_SUCCESS, FAILURE, CRITICAL_FAILURE, REGISTRY_FAILURE
    }

}
