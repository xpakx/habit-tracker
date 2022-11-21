package io.github.xpakx.habitgame.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

@Configuration
public class AMQPConfig {

    @Bean
    public TopicExchange expeditionsTopicExchange(@Value("${amqp.exchange.expeditions}") final String exchangeName) {
        return ExchangeBuilder.topicExchange(exchangeName).durable(true).build();
    }

    @Bean
    public Queue expeditionQueue(@Value("${amqp.queue.expeditions}") final String queueName) {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    public Binding expeditionBinding(final Queue expeditionsQueue, final TopicExchange expeditionsTopicExchange) {
        return BindingBuilder.bind(expeditionsQueue)
                .to(expeditionsTopicExchange)
                .with("expedition");
    }

    @Bean
    public MessageHandlerMethodFactory messageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        final MappingJackson2MessageConverter jsonConverter = new MappingJackson2MessageConverter();
        jsonConverter.getObjectMapper().registerModule(
                new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
        factory.setMessageConverter(jsonConverter);
        return factory;
    }

    @Bean
    public RabbitListenerConfigurer rabbitListenerConfigurer(
            final MessageHandlerMethodFactory messageHandlerMethodFactory) {
        return (c) -> c.setMessageHandlerMethodFactory(messageHandlerMethodFactory);
    }

    @Bean
    public TopicExchange returningTopicExchange(@Value("${amqp.exchange.returning}") final String returningTopic) {
        return ExchangeBuilder
                .topicExchange(returningTopic)
                .durable(true)
                .build();
    }
}
