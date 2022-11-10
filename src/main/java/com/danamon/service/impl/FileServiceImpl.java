package com.danamon.service.impl;

import com.danamon.enums.SheetNameEnum;
import com.danamon.exception.ApplicationException;
import com.danamon.persistence.domain.TransactionDetail;
import com.danamon.persistence.domain.TransactionHeader;
import com.danamon.service.FileService;
import com.danamon.service.GenerateFileExcelService;
import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    private final GenerateFileExcelService generateFileExcelService;
    @Autowired
    public FileServiceImpl(GenerateFileExcelService generateFileExcelService){
        this.generateFileExcelService = generateFileExcelService;
    }

    @Override
    public Set<String> convertPdfToExcel(Set<MultipartFile> files) {
        String firstPath = "Files-upload";
        Set<String> fileNameExcel = new HashSet<>();
        Path uploadDirectory = Paths.get(firstPath);
        if (Files.notExists(Paths.get(firstPath))) {
            new File(firstPath).mkdirs();
            log.info("Create folder: " + firstPath);
        }
        files.forEach(file -> {
            try {
                String filename = StringUtils.cleanPath(file.getOriginalFilename());
                Path filePath = uploadDirectory.resolve(filename);
                InputStream inputStream = file.getInputStream();
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                com.aspose.pdf.LocaleOptions.setLocale(new Locale("en", "US"));

                Locale newLocale = Locale.ROOT;
                Locale.setDefault(newLocale);

                PdfDocument pdf = new PdfDocument(filePath.toAbsolutePath().toString());
                pdf.loadFromFile(filePath.toAbsolutePath().toString());
                pdf.getConvertOptions().setConvertToOneSheet(true);
                String name = filePath.getFileName().toString().toUpperCase().replaceAll("PDF", "");
                String nameExcel = firstPath + "/" + name + "xlsx";
                pdf.saveToFile(nameExcel, FileFormat.XLSX);
                deleteFile(filePath.toAbsolutePath().toString());
                fileNameExcel.add(nameExcel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return fileNameExcel;
    }

    @Override
    public List<XSSFSheet> readFileExcel(String urlfile) {
        try {
            File file = new File(urlfile);   //creating a new file instance
            FileInputStream fis = new FileInputStream(file);   //obtaining bytes from the file
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            List<XSSFSheet> xssfSheetList = new ArrayList<>();
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                xssfSheetList.add(wb.getSheetAt(i));
            }
            return xssfSheetList;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteFile(String idfile) {
        try {
            Path path = Paths.get(idfile);
            if (path != null) Files.delete(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String constructByteArrayExcel(List<SheetNameEnum> sheetNames, List<TransactionHeader> transactionHeaders, List<TransactionDetail> transactionDetailList) {
        Workbook workbook = new XSSFWorkbook();
        sheetNames.forEach(sheetNameEnum -> workbook.createSheet(sheetNameEnum.getInternalValue()));
        String filePath = "Files-upload/Report.xlsx";

        try {
            for (int i = 0; i< workbook.getNumberOfSheets(); i++){
                if(workbook.getSheetAt(i).getSheetName().equalsIgnoreCase(SheetNameEnum.SUMMARY_OF_BS_ANALYSIS.getInternalValue())){
                    generateFileExcelService.generateSheetSummaryAnalysisValue(transactionHeaders, workbook.getSheetAt(i), workbook, transactionDetailList);
                    autoSizeColumns(workbook.getSheetAt(i), 20);
                }
                if(workbook.getSheetAt(i).getSheetName().equalsIgnoreCase(SheetNameEnum.WINDOW_DRESSING.getInternalValue())){
                    //TODO
                    autoSizeColumns(workbook.getSheetAt(i), 20);
                }
                if(workbook.getSheetAt(i).getSheetName().equalsIgnoreCase(SheetNameEnum.TRANSACTIOM_REKENING.getInternalValue())){
                    //TODO
                    autoSizeColumns(workbook.getSheetAt(i), 20);
                }
                if(workbook.getSheetAt(i).getSheetName().equalsIgnoreCase(SheetNameEnum.EOD_BALANCE.getInternalValue())){
                    //TODO
                    autoSizeColumns(workbook.getSheetAt(i), 20);
                }
                if(workbook.getSheetAt(i).getSheetName().equalsIgnoreCase(SheetNameEnum.LOAN_TRACK.getInternalValue())){
                    //TODO
                    autoSizeColumns(workbook.getSheetAt(i), 20);
                }
                if(workbook.getSheetAt(i).getSheetName().equalsIgnoreCase(SheetNameEnum.TOP_20_FUND.getInternalValue())){
//                    generateFileExcelService.generateSheetTop20Fund(transactionHeaders, transactionDetailList, workbook.getSheetAt(i), workbook);
                    autoSizeColumns(workbook.getSheetAt(i), 20);
                }
                if(workbook.getSheetAt(i).getSheetName().equalsIgnoreCase(SheetNameEnum.BOUNCED_PENAL_XNS.getInternalValue())){
                    //TODO
                    autoSizeColumns(workbook.getSheetAt(i), 20);
                }
                if(workbook.getSheetAt(i).getSheetName().equalsIgnoreCase(SheetNameEnum.RECURRING_CREDIT.getInternalValue())){
                    //TODO
                    autoSizeColumns(workbook.getSheetAt(i), 20);
                }
                if(workbook.getSheetAt(i).getSheetName().equalsIgnoreCase(SheetNameEnum.RECURRING_DEBIT.getInternalValue())){
                    //TODO
                    autoSizeColumns(workbook.getSheetAt(i), 20);
                }
                if(workbook.getSheetAt(i).getSheetName().equalsIgnoreCase(SheetNameEnum.TOP_20_FUND_CONSLD.getInternalValue())){
                    //TODO
                    autoSizeColumns(workbook.getSheetAt(i), 20);
                }
                if(workbook.getSheetAt(i).getSheetName().equalsIgnoreCase(SheetNameEnum.STATEMENTS_CONSIDERED.getInternalValue())){
                    //TODO
                    autoSizeColumns(workbook.getSheetAt(i), 20);
                }
                if(workbook.getSheetAt(i).getSheetName().equalsIgnoreCase(SheetNameEnum.FCU_INDICATOR.getInternalValue())){
                    //TODO
                    autoSizeColumns(workbook.getSheetAt(i), 20);
                }
                if(workbook.getSheetAt(i).getSheetName().equalsIgnoreCase(SheetNameEnum.FCU_IRREGULAR.getInternalValue())){
                    //TODO
                    autoSizeColumns(workbook.getSheetAt(i), 20);
                }
            }

            FileOutputStream out = new FileOutputStream(
                    new File(filePath));

            workbook.write(out);
            workbook.close();
            out.close();
        } catch (IOException e) {
            log.error("error crete excel :: {}", e.getMessage());
        } catch (ApplicationException ae) {
            log.error("error to validate setup data :: {}", ae.getMessage());
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Path uploadDirectory = Paths.get(filePath);
        return uploadDirectory.getFileName().toString();
    }

    private void autoSizeColumns(Sheet sheet, int n) {
        for (int i = 0; i < n; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
