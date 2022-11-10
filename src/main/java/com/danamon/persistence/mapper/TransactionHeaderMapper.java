package com.danamon.persistence.mapper;

import com.danamon.persistence.domain.CustomerInformation;
import com.danamon.persistence.domain.TransactionHeader;
import com.danamon.utils.DateUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.YearMonth;
import java.util.Date;

@Mapper
public interface TransactionHeaderMapper {
    TransactionHeaderMapper INSTANCE = Mappers.getMapper(TransactionHeaderMapper.class);

    @Named(value = "toDomain")
    @Mappings({
            @Mapping(source = "username", target = "createdBy"),
            @Mapping(source = "username", target = "creationDate", qualifiedByName = "dateCreated"),
            @Mapping(source = "username", target = "modifiedBy"),
            @Mapping(source = "username", target = "modificationDate", qualifiedByName = "dateCreated"),
            @Mapping(source = "customerInformation", target = "customerInformation"),
            @Mapping(source = "periode", target = "periode", qualifiedByName = "datePeriode"),
            @Mapping(target = "secureId",ignore = true),
            @Mapping(target = "version",ignore = true),
            @Mapping(target = "id",ignore = true)
    })
    TransactionHeader toDomain(String periode, CustomerInformation customerInformation, String username);

    @Named("dateCreated")
    public static Date dateCreated(String value) {
        return new Date();
    }

    @Named("datePeriode")
    public static Date datePeriode(String value) {
        return DateUtil.stringPeriodeToDate(value, DateUtil.DATE_PATTERN_12);
    }
}
