package com.MomentumInvestments.MomentumInvestmentsApplication.config.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Momentum Investments API")
                        .description("API documentation for Momentum Investments")
                        .version("1.0.0"));
    }
}

