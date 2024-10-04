package com.MomentumInvestments.MomentumInvestmentsApplication.dto.Responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RabbitMQResponse {
    private int statusCode;
    private int statusType;
    private String statusMessage;
    private List<Object> resultsList;
}