package com.danamon.service.impl;

import com.danamon.enums.Bank;
import com.danamon.enums.SheetNameEnum;
import com.danamon.enums.StatusCode;
import com.danamon.exception.ApplicationException;
import com.danamon.persistence.domain.Company;
import com.danamon.persistence.domain.TransactionDetail;
import com.danamon.persistence.domain.TransactionHeader;
import com.danamon.persistence.repository.CompanyRepository;
import com.danamon.persistence.repository.TransactionDetailRepository;
import com.danamon.service.BcaService;
import com.danamon.service.FileService;
import com.danamon.service.RekeningKoranService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RekeningKoranServiceImpl implements RekeningKoranService {
    private final BcaService bcaService;
    private final FileService fileService;
    private final TransactionDetailRepository transactionDetailRepository;
    private final CompanyRepository companyRepository;
    @Autowired
    public RekeningKoranServiceImpl(BcaService bcaService,
                                    FileService fileService,
                                    TransactionDetailRepository transactionDetailRepository,
                                    CompanyRepository companyRepository){
        this.bcaService = bcaService;
        this.fileService = fileService;
        this.transactionDetailRepository = transactionDetailRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public ResponseEntity convertReportRekening(Bank bank, Set<MultipartFile> file) throws IOException {
        ByteArrayInputStream byteArrayInputStream = null;
        InputStreamResource inputStreamResource;
        Set<String> filename = fileService.convertPdfToExcel(file);
        log.info("BANK : {}", bank.toString());
        if(bank.equals(Bank.BCA)){
            List<TransactionHeader> transactionHeaderList = bcaService.saveDataBca(filename);
            transactionHeaderList = transactionHeaderList.stream()
                    .sorted(Comparator.comparing(TransactionHeader::getPeriode))
                    .collect(Collectors.toList());
            byteArrayInputStream = generateReport(transactionHeaderList);
        }
        if (null == byteArrayInputStream) {
            throw new ApplicationException("failed to create excel", StatusCode.ERROR);
        }
        Assert.notNull(byteArrayInputStream, "byte array must not null");

        inputStreamResource = new InputStreamResource(byteArrayInputStream);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "Report" + "_" + bank.toString() + ".xlsx" + "\"")
                .body(inputStreamResource);
    }

    private ByteArrayInputStream generateReport(List<TransactionHeader> transactionHeaderList){
        List<SheetNameEnum> sheetName = Arrays.asList(SheetNameEnum.values());
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.ASC, "tanggalTransaksi"));
        orders.add(new Sort.Order(Sort.Direction.ASC, "creationDate"));
        List<TransactionDetail> transactionDetailList = transactionDetailRepository.findByTransactionHeaderIn(transactionHeaderList, Sort.by(orders));
        return fileService.constructByteArrayExcel(sheetName, transactionHeaderList, transactionDetailList);
    }

    @Override
    public boolean uploadCompany(MultipartFile file) {
        String firstPath = "Files-upload";
        Path uploadDirectory = Paths.get(firstPath);
        if (Files.notExists(Paths.get(firstPath))) {
            new File(firstPath).mkdirs();
            log.info("Create folder: " + firstPath);
        }

        try {
            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            Path filePath = uploadDirectory.resolve(filename);
            InputStream inputStream = file.getInputStream();
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            com.aspose.pdf.LocaleOptions.setLocale(new Locale("en", "US"));
            Locale newLocale = Locale.ROOT;
            Locale.setDefault(newLocale);
            String nameExcel = firstPath + "/" + filename;

            List<XSSFSheet> sheetList = fileService.readFileExcel(nameExcel);
            sheetList.forEach(sheet -> {
                Iterator<Row> itr = sheet.iterator();    //iterating over excel file
                loopRow:
                while (itr.hasNext()) {
                    Row row = itr.next();
                    if (row.getRowNum() < 1) continue;
                    Company company = new Company();
                    company.setPerusahaan(row.getCell(0).getStringCellValue());
                    company.setKomoditi(row.getCell(1).getStringCellValue());
                    company.setCreateBaseDate("SYSTEM");
                    companyRepository.save(company);
                }
            });
        }catch (Exception e){
                e.printStackTrace();
        }
        return true;
    }
}
