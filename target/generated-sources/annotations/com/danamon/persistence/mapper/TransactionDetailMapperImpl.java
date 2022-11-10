package com.danamon.persistence.mapper;

import com.danamon.persistence.domain.TransactionDetail;
import com.danamon.persistence.domain.TransactionHeader;
import com.danamon.vo.ResponseTransactionBCAVO;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-11-11T00:41:15+0700",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_261 (Oracle Corporation)"
)
public class TransactionDetailMapperImpl implements TransactionDetailMapper {

    @Override
    public TransactionDetail toDomain(ResponseTransactionBCAVO responseTransactionBCAVO, String username, TransactionHeader transactionHeader) {
        if ( responseTransactionBCAVO == null && username == null && transactionHeader == null ) {
            return null;
        }

        TransactionDetail transactionDetail = new TransactionDetail();

        if ( responseTransactionBCAVO != null ) {
            transactionDetail.setSaldo( TransactionDetailMapper.stringToDouble( responseTransactionBCAVO.getSaldo() ) );
            transactionDetail.setCbg( responseTransactionBCAVO.getCbg() );
        }
        if ( username != null ) {
            transactionDetail.setModificationDate( TransactionDetailMapper.dateCreated( username ) );
            transactionDetail.setCreatedBy( username );
            transactionDetail.setModifiedBy( username );
            transactionDetail.setCreationDate( TransactionDetailMapper.dateCreated( username ) );
        }
        if ( transactionHeader != null ) {
            transactionDetail.setTransactionHeader( transactionHeader );
        }

        afterMappingtoDomain( transactionDetail, responseTransactionBCAVO, username, transactionHeader );

        return transactionDetail;
    }
}
