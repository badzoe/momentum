package com.MomentumInvestments.MomentumInvestmentsApplication.mappers;

import com.MomentumInvestments.MomentumInvestmentsApplication.dto.InvestorDTO;
import com.MomentumInvestments.MomentumInvestmentsApplication.dto.ProductDTO;
import com.MomentumInvestments.MomentumInvestmentsApplication.entity.Investor;
import com.MomentumInvestments.MomentumInvestmentsApplication.entity.InvestorProducts;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface InvestorMapper {

    InvestorDTO mapInvestorEntityToDto(Investor investor);

    List<InvestorDTO> mapInvestorEntityListToDto(List<Investor> investors);

    Investor mapInvestorDTOToEntity(InvestorDTO investorDTO);

    @BeforeMapping
    default void mapExtraFields(InvestorProducts source, @MappingTarget ProductDTO destination) {
        destination.setId(source.getProductID().getId());
        destination.setName(source.getProductID().getName());
        destination.setType(source.getProductID().getType());
    }

    List<ProductDTO> mapInvestorProductEntityToDTO(List<InvestorProducts> products);
}
