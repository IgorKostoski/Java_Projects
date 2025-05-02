package com.healthcare.billingservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${healthcare.amqp.exchange.appointments:healthcare.appointments.exchange}")
    private String appointmentsExchangeName;

    @Value("${healthcare.amqp.queue.billing:billing-invoice-queue}") // Define queue name
    private String billingQueueName;

    @Value("${healthcare.amqp.routingkey.completed:appointment.completed}") // Listen for this key
    private String completedRoutingKey;

    // Declare the Exchange (idempotent)
    @Bean
    TopicExchange appointmentsExchange() {
        return new TopicExchange(appointmentsExchangeName);
    }

    // Declare the Queue for Billing
    @Bean
    Queue billingQueue() {
        return QueueBuilder.durable(billingQueueName).build();
    }

    // Bind the Billing Queue to the Exchange for completed events
    @Bean
    Binding completedBinding(Queue billingQueue, TopicExchange appointmentsExchange) {
        return BindingBuilder.bind(billingQueue).to(appointmentsExchange).with(completedRoutingKey);
    }

    // Configure JSON Message Converter (needed for consumer too)
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // No RabbitTemplate needed here unless Billing Service also sends messages
}