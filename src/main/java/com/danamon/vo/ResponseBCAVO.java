package com.danamon.vo;

import lombok.Data;

import java.util.List;

@Data
public class ResponseBCAVO {
    List<ResponseTransactionBCAVO> responseTransactionBCAVOList;
    ResponseCustomerInfoBCAVO responseCustomerInfoBCAVO;
}
