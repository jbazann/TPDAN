package com.jbazann.commons.async.orchestration;

import com.jbazann.commons.async.events.DomainEventTracer;
import com.jbazann.commons.async.transactions.TransactionCoordinatorDataRepository;
import com.jbazann.commons.identity.ApplicationMember;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(OrchestrationBaseConfiguration.class)
public class OrchestrationCoordinatorConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TransactionCoordinatorDataRepository missingCoordinatorDataRepository() {
        throw new IllegalStateException("A TransactionCoordinatorDataRepository @Bean was not provided.");
    }

    @Bean
    @ConditionalOnMissingBean
    public TransactionCoordinatorService standardTransactionCoordinatorService(ApplicationMember member,
                                                                               TransactionCoordinatorDataRepository repository,
                                                                               DomainEventTracer tracer) {
        return new TransactionCoordinatorService(member, repository, tracer);
    }

}
