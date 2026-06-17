package com.example.demo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {

    public static final String EXCHANGE = "rooted.events";

    public static final String EMAIL_VERIFICATION_QUEUE = "rooted.email.verification";
    public static final String EMAIL_VERIFICATION_ROUTING_KEY = "email.verification";

    public static final String EMAIL_GREETING_QUEUE = "rooted.email.greeting";
    public static final String EMAIL_GREETING_ROUTING_KEY = "email.greeting";

    public static final String LEAF_STATS_QUEUE = "rooted.leaf.stats";
    public static final String LEAF_STATS_ROUTING_KEY = "leaf.stats";


    @Bean
    public DirectExchange rootedExchange() {
        return ExchangeBuilder
                .directExchange(EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue emailVerificationQueue() {
        return QueueBuilder
                .durable(EMAIL_VERIFICATION_QUEUE)
                .build();
    }

    @Bean
    public Queue emailGreatingQueue() {
        return QueueBuilder
                .durable(EMAIL_GREETING_QUEUE)
                .build();
    }

    @Bean
    public Queue leafStatsQueue() {
        return QueueBuilder
                .durable(LEAF_STATS_QUEUE)
                .build();
    }

    @Bean
    public Binding emailVerificationBinding(
            Queue emailVerificationQueue,
            DirectExchange rootedExchange
    ) {
        return BindingBuilder
                .bind(emailVerificationQueue)
                .to(rootedExchange)
                .with(EMAIL_VERIFICATION_ROUTING_KEY);
    }

    @Bean
    public Binding emailGreetingBinding(
            Queue emailGreatingQueue,
            DirectExchange rootedExchange
    ) {
        return BindingBuilder
                .bind(emailGreatingQueue)
                .to(rootedExchange)
                .with(EMAIL_GREETING_ROUTING_KEY);
    }

    @Bean
    public Binding leafStatsBinding(
            Queue leafStatsQueue,
            DirectExchange rootedExchange
    ) {
        return BindingBuilder
                .bind(leafStatsQueue)
                .to(rootedExchange)
                .with(LEAF_STATS_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        return factory;
    }
}