package com.jbazann.orders.commons.async.orchestration;

import com.jbazann.orders.commons.async.rabbitmq.RabbitConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({OrchestrationMemberConfiguration.class, RabbitConfiguration.class})
public @interface OrchestrationMember {
}
