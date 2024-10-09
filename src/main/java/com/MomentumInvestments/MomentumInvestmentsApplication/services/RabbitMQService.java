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
                    "MUTANGABENDETECHNOLOGIES" + '.' + "ZW.TMS.OPENIT" + '.' + "EMAILNOTIFICATIONS",
                    "ZW.TMS.OPENIT" + "." + "EMAILNOTIFICATIONS",
                    emailServiceRequest,
                    correlationData
            );
            log.info("Dispatched message to the queue");

            // Listen For RabbitMQ Confirmations
            //boolean ack = correlationData.getFuture().get(120, TimeUnit.SECONDS).isAck();

            return CompletableFuture.completedFuture(new RabbitMQResponse(1,
                    4000, "", null));


            /*if (!ack) {
                log.error("RabbitMQ Has Returned a negative Confirmation...");

                return CompletableFuture.completedFuture(new RabbitMQResponse(4,
                        4001, "", null));
            }

            log.info("Received confirm from RabbitMQ with result: true. Correlation Data is: {}", correlationData.getId());
            this.rabbitTemplate.setMandatory(true);
            return CompletableFuture.completedFuture(new RabbitMQResponse(1,
                    4000, "", null));*/

        }/* catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Reset the interrupted status
            log.error("Thread interrupted while waiting for confirmation from RabbitMQ.");
            return CompletableFuture.completedFuture(new RabbitMQResponse(4,
                    4003, "Thread interrupted while waiting for confirmation from RabbitMQ", null));
        }catch (ExecutionException e) {
            log.error("Error occurred while waiting for confirmation from RabbitMQ.", e);
            return CompletableFuture.completedFuture(new RabbitMQResponse(4,
                    4004, "Error occurred while waiting for confirmation from RabbitMQ", null));
        } catch (TimeoutException e) {
            log.error("Timeout occurred while waiting for confirmation from RabbitMQ.");
            return CompletableFuture.completedFuture(new RabbitMQResponse(4,
                    4002, "Timeout occurred while waiting for confirmation from RabbitMQ", null));
        }*/
        catch (Exception ex){
            return CompletableFuture.completedFuture(new RabbitMQResponse(4,
                    4002, ex.getMessage(), null));
        }
    }
}
