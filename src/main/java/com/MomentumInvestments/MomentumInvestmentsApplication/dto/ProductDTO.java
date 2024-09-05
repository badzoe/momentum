package com.MomentumInvestments.MomentumInvestmentsApplication.dto;

import com.MomentumInvestments.MomentumInvestmentsApplication.constants.ProductType;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductDTO {
    private Long id;
    private ProductType type;
    private String name;

}
