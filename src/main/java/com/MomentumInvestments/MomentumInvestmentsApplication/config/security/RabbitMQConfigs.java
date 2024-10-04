package com.MomentumInvestments.MomentumInvestmentsApplication.config.security;

import jakarta.annotation.PostConstruct;
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
    @PostConstruct
    public RabbitAdmin getRabbitAdmin() {
        RabbitAdmin admin = new RabbitAdmin(cachingConnectionFactory);
        admin.declareQueue(createNotificationsQueue());
        admin.declareExchange(notificationsServiceExchange());
        admin.declareBinding(notificationsBinding());
        admin.initialize();
        return admin;
    }

    private org.springframework.amqp.core.Queue createNotificationsQueue() {
        return new Queue("ZW.TMS.OPENIT" + "." + "EMAILNOTIFICATIONS", true, false, false);
    }

    /**
     * Bind the request Queue to the Exchange and Routing Key
     *
     * @return Binding Object
     */
    private Binding notificationsBinding() {
        return BindingBuilder.
                bind(createNotificationsQueue()).
                to(notificationsServiceExchange()).
                with("ZW.TMS.OPENIT" + "." + "EMAILNOTIFICATIONS");
    }

    /**
     * Create a new Requests Topic Exchange
     *
     * @return Requests Object
     */
    private TopicExchange notificationsServiceExchange() {
        return new TopicExchange("CHIBINDITECHNOLOGIES" + "." + "ZW.TMS.OPENIT" + "." + "EMAILNOTIFICATIONS");
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
