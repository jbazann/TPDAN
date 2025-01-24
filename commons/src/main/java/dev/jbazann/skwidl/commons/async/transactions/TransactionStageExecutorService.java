package dev.jbazann.skwidl.commons.async.transactions;

import dev.jbazann.skwidl.commons.async.events.DomainEvent;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionLifecycleActions;
import dev.jbazann.skwidl.commons.async.transactions.api.TransactionStage;
import dev.jbazann.skwidl.commons.async.transactions.entities.Transaction;

public final class TransactionStageExecutorService {

    private final TransactionStageRegistrarService registrar;
    private final TransactionLifecycleActions transactionActions;
    private final TransactionLockingService lockingService;

    public TransactionStageExecutorService(TransactionStageRegistrarService registrar, TransactionLifecycleActions transactionActions, TransactionLockingService lockingService) {
        this.registrar = registrar;
        this.transactionActions = transactionActions;
        this.lockingService = lockingService;
    }

    public TransactionResult runStageFor(DomainEvent event) {
        TransactionStage stage = registrar.getStageForEvent(event);
        Transaction transaction = transactionActions.fetchOrCreateForEvent(event);
        lockingService.getLocks(stage, event);
        TransactionResult result = stage.runStage(event, transaction);
        lockingService.releaseLocks(stage, event);
        result.data(transaction);
        return result;
    }

}
