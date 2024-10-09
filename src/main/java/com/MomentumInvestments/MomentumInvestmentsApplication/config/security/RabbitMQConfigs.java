package com.MomentumInvestments.MomentumInvestmentsApplication.config.security;

import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class RabbitMQConfigs {

    private final CachingConnectionFactory cachingConnectionFactory;

    @Bean
    public RabbitAdmin rabbitAdmin() {
        RabbitAdmin admin = new RabbitAdmin(cachingConnectionFactory);
        admin.declareQueue(createNotificationsQueue());
        admin.declareExchange(notificationsServiceExchange());
        admin.declareBinding(notificationsBinding());
        admin.initialize(); // Optional; initializes the RabbitAdmin if not done already
        return admin;
    }

    @Bean
    public Queue createNotificationsQueue() {
        return new Queue("ZW.TMS.OPENIT.EMAILNOTIFICATIONS", true, false, false);
    }

    @Bean
    public Binding notificationsBinding() {
        return BindingBuilder
                .bind(createNotificationsQueue())
                .to(notificationsServiceExchange())
                .with("ZW.TMS.OPENIT.EMAILNOTIFICATIONS");
    }

    @Bean
    public TopicExchange notificationsServiceExchange() {
        return new TopicExchange("MUTANGABENDETECHNOLOGIES.ZW.TMS.OPENIT.EMAILNOTIFICATIONS");
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
