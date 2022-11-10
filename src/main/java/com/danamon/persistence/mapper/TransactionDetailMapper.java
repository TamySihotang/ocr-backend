package com.danamon.persistence.mapper;

import com.danamon.persistence.domain.TransactionDetail;
import com.danamon.persistence.domain.TransactionHeader;
import com.danamon.utils.DateUtil;
import com.danamon.vo.ResponseTransactionBCAVO;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Mapper
public interface TransactionDetailMapper {
    TransactionDetailMapper INSTANCE = Mappers.getMapper(TransactionDetailMapper.class);

    @Named(value = "toDomain")
    @Mappings({
            @Mapping(source = "username", target = "createdBy"),
            @Mapping(source = "username", target = "creationDate", qualifiedByName = "dateCreated"),
            @Mapping(source = "username", target = "modifiedBy"),
            @Mapping(source = "username", target = "modificationDate", qualifiedByName = "dateCreated"),
            @Mapping(source = "responseTransactionBCAVO.saldo", target = "saldo", qualifiedByName = "stringToDouble"),
            @Mapping(target = "keterangan",ignore = true),
            @Mapping(target = "secureId",ignore = true),
            @Mapping(target = "version",ignore = true),
            @Mapping(target = "id",ignore = true),
            @Mapping(source = "transactionHeader", target = "transactionHeader")
    })
    TransactionDetail toDomain(ResponseTransactionBCAVO responseTransactionBCAVO, String username, TransactionHeader transactionHeader);

    @AfterMapping
    default void afterMappingtoDomain(@MappingTarget TransactionDetail target, ResponseTransactionBCAVO responseTransactionBCAVO, String username, TransactionHeader transactionHeader) {
        target.setTanggalTransaksi(stringToDate(responseTransactionBCAVO.getTanggal(), transactionHeader.getPeriode()));
        target.setKeterangan(joinKeterangan(responseTransactionBCAVO.getKeterangan(), responseTransactionBCAVO.getSubKeterangan()));
        if(responseTransactionBCAVO.getKetMutasi() != null && responseTransactionBCAVO.getKetMutasi().equalsIgnoreCase("DB")){
            target.setDebet(stringToDouble(responseTransactionBCAVO.getMutasi()));
        }else {
            target.setKredit(stringToDouble(responseTransactionBCAVO.getMutasi()));
        }
    }

    public static Date stringToDate(String tanggal, Date datePeriode) {
        LocalDate localDate = datePeriode.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String newTanggal = tanggal + "/" + localDate.getYear();
        return DateUtil.stringToDate(newTanggal, DateUtil.DATE_PATTERN_3);
    }
    public static String joinKeterangan(List<String> keterangan, List<String> subKeterangan) {
        String keteranganString = "";
        String subKeteranganString = "";
        String join = "";
        if(keterangan != null) keteranganString = StringUtils.join(keterangan, " ");
        if(subKeterangan != null) subKeteranganString = StringUtils.join(subKeterangan, "|");
        join = keteranganString + "|" + subKeteranganString;
        return join.trim();
    }
    @Named("stringToDouble")
    static Double stringToDouble(String value){
        if(value != null) {
            value = value.replace(",","");
            return Double.valueOf(value);
        }
        return null;
    }

    @Named("dateCreated")
    public static Date dateCreated(String value) {
        return new Date();
    }
}
