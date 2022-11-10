package com.danamon.persistence.mapper;

import com.danamon.persistence.domain.CustomerInformation;
import com.danamon.vo.ResponseCustomerInfoBCAVO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Date;

@Mapper
public interface CustomerInformationMapper {
    CustomerInformationMapper INSTANCE = Mappers.getMapper(CustomerInformationMapper.class);

    @Named(value = "toDomain")
    @Mappings({
            @Mapping(source = "username", target = "createdBy"),
            @Mapping(source = "username", target = "creationDate", qualifiedByName = "dateCreated"),
            @Mapping(source = "username", target = "modifiedBy"),
            @Mapping(source = "username", target = "modificationDate", qualifiedByName = "dateCreated")
    })
    CustomerInformation toDomain(ResponseCustomerInfoBCAVO responseCustomerInfoBCAVO, String username);

    @Named("dateCreated")
    public static Date dateCreated(String value) {
        return new Date();
    }
}
