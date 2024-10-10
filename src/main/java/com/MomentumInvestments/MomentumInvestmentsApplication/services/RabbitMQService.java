package com.MomentumInvestments.MomentumInvestmentsApplication.services;

import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Request.MicroServiceRequest;
import com.MomentumInvestments.MomentumInvestmentsApplication.dto.Responses.RabbitMQResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@AllArgsConstructor
@Service
@Slf4j
public class RabbitMQService {
    private final RabbitTemplate rabbitTemplate;

    public CorrelationData getCorrelationData() {
        return new CorrelationData(UUID.randomUUID().toString());
    }

    /**
     * Dispatches Objects to RabbitMQ as a JSON string Handles Asynchronous
     * Processing Retries
     *
     * @param emailServiceRequest
     * @param correlationData Object
     * @return
     * @throws java.lang.InterruptedException
     * @throws java.util.concurrent.TimeoutException
     * @throws java.util.concurrent.ExecutionException
     */
    @Async("concurrentThreadPoolTaskExecutor")
    public CompletableFuture<RabbitMQResponse> dispatchToQueue(MicroServiceRequest emailServiceRequest,
                                                               CorrelationData correlationData) throws InterruptedException, TimeoutException, ExecutionException {
        try {
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.convertAndSend(
                    "MUTANGABENDETECHNOLOGIES" + '.' + "zw.test.test" + '.' + "emails",
                    "zw.test.test" + "." + "emails",
                    emailServiceRequest,
                    correlationData
            );
            log.info("Dispatched message to the queue");
            return CompletableFuture.completedFuture(new RabbitMQResponse(1,
                    4000, "", null));

        }
        catch (Exception ex){
            return CompletableFuture.completedFuture(new RabbitMQResponse(4,
                    4002, ex.getMessage(), null));
        }
    }
}
