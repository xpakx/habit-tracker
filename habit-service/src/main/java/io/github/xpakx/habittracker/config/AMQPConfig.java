package io.github.xpakx.habittracker.config;

import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AMQPConfig {
    private final String completionsTopic;

    public AMQPConfig(@Value("${amqp.exchange.completions}") final String completionsTopic) {
        this.completionsTopic = completionsTopic;
    }

    @Bean
    public TopicExchange completionsTopicExchange() {
        return ExchangeBuilder
                .topicExchange(completionsTopic)
                .durable(true)
                .build();
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
