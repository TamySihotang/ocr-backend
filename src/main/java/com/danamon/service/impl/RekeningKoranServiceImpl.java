package com.danamon.service.impl;

import com.danamon.enums.Bank;
import com.danamon.enums.SheetNameEnum;
import com.danamon.persistence.domain.TransactionDetail;
import com.danamon.persistence.domain.TransactionHeader;
import com.danamon.persistence.repository.TransactionDetailRepository;
import com.danamon.service.BcaService;
import com.danamon.service.FileService;
import com.danamon.service.RekeningKoranService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RekeningKoranServiceImpl implements RekeningKoranService {
    private final BcaService bcaService;
    private final FileService fileService;
    private final TransactionDetailRepository transactionDetailRepository;
    @Autowired
    public RekeningKoranServiceImpl(BcaService bcaService,
                                    FileService fileService,
                                    TransactionDetailRepository transactionDetailRepository){
        this.bcaService = bcaService;
        this.fileService = fileService;
        this.transactionDetailRepository = transactionDetailRepository;
    }

    @Override
    public String convertReportRekening(Bank bank, Set<MultipartFile> file) {
        Set<String> filename = fileService.convertPdfToExcel(file);
        if(bank.equals(Bank.BCA)){
            List<TransactionHeader> transactionHeaderList = bcaService.saveDataBca(filename);
            transactionHeaderList = transactionHeaderList.stream()
                    .sorted(Comparator.comparing(TransactionHeader::getPeriode))
                    .collect(Collectors.toList());
            return generateReport(transactionHeaderList);
        }
        return null;
    }

    private String generateReport(List<TransactionHeader> transactionHeaderList){
        List<SheetNameEnum> sheetName = Arrays.asList(SheetNameEnum.values());
        List<TransactionDetail> transactionDetailList = transactionDetailRepository.findByTransactionHeaderIn(transactionHeaderList);
        return fileService.constructByteArrayExcel(sheetName, transactionHeaderList, transactionDetailList);
    }
}
