package com.jbazann.orders.commons.async.transactions;

import com.jbazann.orders.commons.async.events.DomainEvent;
import com.jbazann.orders.commons.identity.ApplicationMember;
import com.jbazann.orders.commons.utils.TimeProvider;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Accessors(fluent = true)
public class TransactionCoordinatorData {

    private UUID id;
    private Map<ApplicationMember, QuorumStatus> quorumStatus;
    private List<ApplicationMember> rollback;
    private TransactionStatus status;
    private boolean isCommitted;
    private LocalDateTime expires;

    public static TransactionCoordinatorData from(DomainEvent event) {
        final Map<ApplicationMember, QuorumStatus> quorumStatus = new HashMap<>();
        event.transaction().quorum().members()
                .forEach(member -> quorumStatus.put(member, QuorumStatus.UNKNOWN));
        return new TransactionCoordinatorData()
                .id(event.transaction().id())
                .status(TransactionStatus.NOT_PERSISTED)
                .quorumStatus(quorumStatus)
                .rollback(new ArrayList<>())
                .isCommitted(false)
                .expires(event.transaction().expires());
    }

    public boolean isExpired() {
        return TimeProvider.localDateTimeNow().isAfter(expires);
    }

    public TransactionCoordinatorData addAccept(ApplicationMember member) {
        quorumStatus.put(member, QuorumStatus.ACCEPT);
        return this;
    }

    public TransactionCoordinatorData addCommit(ApplicationMember member) {
        quorumStatus.put(member, QuorumStatus.COMMIT);
        return this;
    }

    public TransactionCoordinatorData addReject(ApplicationMember member) {
        quorumStatus.put(member, QuorumStatus.REJECT);
        return this;
    }

    public TransactionCoordinatorData addRollback(ApplicationMember member) {
        rollback.add(member);
        return this;
    }

    public boolean isFullyRejected() {
        return quorumStatus.values().stream()
                .allMatch(v -> v == QuorumStatus.REJECT ||
                        v == QuorumStatus.ROLLBACK);
    }

    public boolean isFullyCommitted() {
        return quorumStatus.values().stream()
                .allMatch(v -> v == QuorumStatus.COMMIT);
    }

    public boolean isFullyAccepted() {
        return quorumStatus.values().stream()
                .allMatch(v -> v == QuorumStatus.ACCEPT);
    }

    public boolean isRejected() {
        return TransactionStatus.REJECTED.equals(status);
    }

    public boolean isConcluded() {
        return switch (status) {
            case CONCLUDED_REJECT, CONCLUDED_COMMIT, CONCLUDED_EXPIRED -> true;
            case NOT_PERSISTED, STARTED, ACCEPTED, REJECTED, COMMITTED -> false;
        };
    }

    public enum QuorumStatus {
        UNKNOWN,
        ACCEPT,
        REJECT,
        COMMIT,
        ROLLBACK,
    }

    public enum TransactionStatus {
        NOT_PERSISTED,
        STARTED,
        ACCEPTED,
        REJECTED,
        COMMITTED,
        CONCLUDED_EXPIRED,
        CONCLUDED_REJECT,
        CONCLUDED_COMMIT
    }

}
