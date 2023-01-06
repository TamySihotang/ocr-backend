package com.danamon.service;

import com.danamon.persistence.domain.CustomerInformation;
import com.danamon.persistence.domain.TransactionDetail;
import com.danamon.persistence.domain.TransactionHeader;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

public interface GenerateFileExcelService {
    void generateSheetSummaryAnalysisValue(List<TransactionHeader> transactionHeaders, Sheet sheet, Workbook workbook, List<TransactionDetail> transactionDetailList);
    void generateTransactionDetail(Sheet sheet, List<TransactionDetail> transactionDetails, Workbook workbook, CustomerInformation customerInformation);
    void generateSheetTop20Fund(List<TransactionHeader> transactionHeaders, List<TransactionDetail> transactionDetailList, Sheet sheet, Workbook workbook);

}
