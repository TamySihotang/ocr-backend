package com.danamon.service;

import com.danamon.enums.SheetNameEnum;
import com.danamon.persistence.domain.TransactionDetail;
import com.danamon.persistence.domain.TransactionHeader;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.List;
import java.util.Set;

public interface FileService {
    Set<String>  convertPdfToExcel(Set<MultipartFile> file);
    List<XSSFSheet> readFileExcel(String urlfile);
    String constructByteArrayExcel(List<SheetNameEnum> sheetName, List<TransactionHeader> transactionHeaders, List<TransactionDetail> transactionDetailList);
}
