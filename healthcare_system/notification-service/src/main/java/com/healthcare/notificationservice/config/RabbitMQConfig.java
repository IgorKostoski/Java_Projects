package com.healthcare.notificationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Inject properties from configuration file
    @Value("${healthcare.amqp.exchange.appointments:healthcare.appointments.exchange}")
    private String appointmentsExchangeName;

    @Value("${healthcare.amqp.queue.notifications:appointment-notifications-queue}")
    private String notificationsQueueName;

    @Value("${healthcare.amqp.routingkey.created:appointment.created}")
    private String createdRoutingKey;

    @Value("${healthcare.amqp.routingkey.cancelled:appointment.cancelled}")
    private String cancelledRoutingKey;

    // 1. Declare the Exchange (must match the publisher's declaration)
    @Bean
    TopicExchange appointmentsExchange() {
        // Declaring it here ensures it exists if this service starts first,
        // but it's idempotent, so declaring it in both publisher and consumer is safe.
        return new TopicExchange(appointmentsExchangeName);
    }

    // 2. Declare the Queue this service will consume from
    @Bean
    Queue notificationsQueue() {
        // Durable queue (survives broker restart)
        return QueueBuilder.durable(notificationsQueueName).build();
    }

    // 3. Create Bindings between the Exchange and the Queue
    // We want messages published with specific routing keys to go to our queue.

    @Bean
    Binding createdBinding(Queue notificationsQueue, TopicExchange appointmentsExchange) {
        // Bind notificationsQueue to appointmentsExchange using the createdRoutingKey
        // Messages published to the exchange with routing key "appointment.created" will be routed here.
        return BindingBuilder.bind(notificationsQueue).to(appointmentsExchange).with(createdRoutingKey);
    }

    @Bean
    Binding cancelledBinding(Queue notificationsQueue, TopicExchange appointmentsExchange) {
        // Also listen for cancellation events
        return BindingBuilder.bind(notificationsQueue).to(appointmentsExchange).with(cancelledRoutingKey);
    }

    // --- Message Converter Configuration (needed again for consumer) ---
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Optional: Configure RabbitTemplate if this service ALSO needs to send messages
    // @Bean
    // public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
    //     RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    //     rabbitTemplate.setMessageConverter(messageConverter);
    //     return rabbitTemplate;
    // }
}