package com.danamon.vo;

import lombok.Data;

import java.util.List;

@Data
public class ResponseTransactionBCAVO {
    private String tanggal;
    private List<String> keterangan;
    private List<String> subKeterangan;
    private String cbg;
    private String mutasi;
    private String ketMutasi;
    private String saldo;
}
