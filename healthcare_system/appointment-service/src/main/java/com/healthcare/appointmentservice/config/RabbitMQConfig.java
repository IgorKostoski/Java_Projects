package com.healthcare.appointmentservice.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Inject exchange name from properties for flexibility
    @Value("${healthcare.amqp.exchange.appointments:healthcare.appointments.exchange}")
    private String appointmentsExchange;

    // We only define the Exchange here in the PUBLISHER service.
    // Queues and Bindings are typically defined by the CONSUMER services.
    @Bean
    TopicExchange appointmentsExchange() {
        return new TopicExchange(appointmentsExchange);
    }

    // Configure a MessageConverter to serialize/deserialize messages as JSON
    @Bean
    public MessageConverter jsonMessageConverter() {
        // Using Jackson for JSON conversion
        return new Jackson2JsonMessageConverter();
    }

    // Configure RabbitTemplate to use the JSON MessageConverter
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter); // Use JSON converter
        return rabbitTemplate;
    }
}