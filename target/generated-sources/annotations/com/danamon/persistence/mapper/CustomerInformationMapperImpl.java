package com.danamon.persistence.mapper;

import com.danamon.persistence.domain.CustomerInformation;
import com.danamon.vo.ResponseCustomerInfoBCAVO;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-11-11T00:41:16+0700",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_261 (Oracle Corporation)"
)
public class CustomerInformationMapperImpl implements CustomerInformationMapper {

    @Override
    public CustomerInformation toDomain(ResponseCustomerInfoBCAVO responseCustomerInfoBCAVO, String username) {
        if ( responseCustomerInfoBCAVO == null && username == null ) {
            return null;
        }

        CustomerInformation customerInformation = new CustomerInformation();

        if ( responseCustomerInfoBCAVO != null ) {
            customerInformation.setNama( responseCustomerInfoBCAVO.getNama() );
            customerInformation.setAlamat( responseCustomerInfoBCAVO.getAlamat() );
            customerInformation.setBank( responseCustomerInfoBCAVO.getBank() );
            customerInformation.setNomorRekening( responseCustomerInfoBCAVO.getNomorRekening() );
        }
        if ( username != null ) {
            customerInformation.setModificationDate( CustomerInformationMapper.dateCreated( username ) );
            customerInformation.setModifiedBy( username );
            customerInformation.setCreationDate( CustomerInformationMapper.dateCreated( username ) );
            customerInformation.setCreatedBy( username );
        }

        return customerInformation;
    }
}
