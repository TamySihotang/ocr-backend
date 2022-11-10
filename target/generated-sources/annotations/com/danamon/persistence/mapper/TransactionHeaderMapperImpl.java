package com.danamon.persistence.mapper;

import com.danamon.persistence.domain.CustomerInformation;
import com.danamon.persistence.domain.TransactionHeader;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-11-11T00:41:16+0700",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_261 (Oracle Corporation)"
)
public class TransactionHeaderMapperImpl implements TransactionHeaderMapper {

    @Override
    public TransactionHeader toDomain(String periode, CustomerInformation customerInformation, String username) {
        if ( periode == null && customerInformation == null && username == null ) {
            return null;
        }

        TransactionHeader transactionHeader = new TransactionHeader();

        if ( periode != null ) {
            transactionHeader.setPeriode( TransactionHeaderMapper.datePeriode( periode ) );
        }
        if ( customerInformation != null ) {
            transactionHeader.setCustomerInformation( customerInformation );
        }
        if ( username != null ) {
            transactionHeader.setModificationDate( TransactionHeaderMapper.dateCreated( username ) );
            transactionHeader.setCreatedBy( username );
            transactionHeader.setModifiedBy( username );
            transactionHeader.setCreationDate( TransactionHeaderMapper.dateCreated( username ) );
        }

        return transactionHeader;
    }
}
