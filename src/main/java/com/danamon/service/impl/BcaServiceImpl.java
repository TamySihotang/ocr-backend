package com.danamon.service.impl;

import com.danamon.enums.Bank;
import com.danamon.persistence.domain.CustomerInformation;
import com.danamon.persistence.domain.TransactionDetail;
import com.danamon.persistence.domain.TransactionHeader;
import com.danamon.persistence.mapper.CustomerInformationMapper;
import com.danamon.persistence.mapper.TransactionDetailMapper;
import com.danamon.persistence.mapper.TransactionHeaderMapper;
import com.danamon.persistence.repository.CustomerInformationRepository;
import com.danamon.persistence.repository.TransactionDetailRepository;
import com.danamon.persistence.repository.TransactionHeaderRepository;
import com.danamon.service.BcaService;
import com.danamon.service.FileService;
import com.danamon.utils.DateUtil;
import com.danamon.vo.ResponseBCAVO;
import com.danamon.vo.ResponseTransactionBCAVO;
import com.danamon.vo.ResponseCustomerInfoBCAVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BcaServiceImpl implements BcaService {
    private final FileService fileService;
    private final CustomerInformationRepository customerInformationRepository;
    private final TransactionHeaderRepository transactionHeaderRepository;
    private final TransactionDetailRepository transactionDetailRepository;
    private static final String SYSTEM = "SYSTEM";

    @Autowired
    public BcaServiceImpl(FileService fileService,
                          CustomerInformationRepository customerInformationRepository,
                          TransactionHeaderRepository transactionHeaderRepository,
                          TransactionDetailRepository transactionDetailRepository) {
        this.fileService = fileService;
        this.customerInformationRepository = customerInformationRepository;
        this.transactionHeaderRepository = transactionHeaderRepository;
        this.transactionDetailRepository = transactionDetailRepository;
    }

    @Override
    @Transactional
    public List<TransactionHeader> saveDataBca(Set<String> file) {
        List<TransactionHeader> transactionHeaderList = new ArrayList<>();
        Set<ResponseBCAVO> responseBCAVOSet = dataFromExcel(file);
        responseBCAVOSet.forEach(responseBCAVO -> {
            transactionHeaderList.add(processInsertData(responseBCAVO));
        });
        return transactionHeaderList;
    }

    @Transactional
    private TransactionHeader processInsertData(ResponseBCAVO responseBCAVO) {
        CustomerInformation customerInformation = customerInformationRepository.findByNomorRekeningAndBank(responseBCAVO.getResponseCustomerInfoBCAVO().getNomorRekening(), responseBCAVO.getResponseCustomerInfoBCAVO().getBank()).orElse(null);
        if (customerInformation == null) {
            customerInformation = customerInformationRepository.save(CustomerInformationMapper.INSTANCE.toDomain(responseBCAVO.getResponseCustomerInfoBCAVO(), SYSTEM));
        }
        TransactionHeader transactionHeader = transactionHeaderRepository.findByPeriodeAndCustomerInformation(DateUtil.stringPeriodeToDate(responseBCAVO.getResponseCustomerInfoBCAVO().getPeriode(), DateUtil.DATE_PATTERN_12), customerInformation).orElse(null);
        if (transactionHeader == null) {
            transactionHeader = transactionHeaderRepository.save(TransactionHeaderMapper.INSTANCE.toDomain(responseBCAVO.getResponseCustomerInfoBCAVO().getPeriode(), customerInformation, SYSTEM));
        }
        TransactionHeader finalTransactionHeader = transactionHeader;
        transactionDetailRepository.deleteByTransactionHeader(finalTransactionHeader);
        final int[] i = {0};

        responseBCAVO.setResponseTransactionBCAVOList(responseBCAVO.getResponseTransactionBCAVOList().stream()
                .filter(x -> x.getTanggal() != null)
                .collect(Collectors.toList()));

        responseBCAVO.getResponseTransactionBCAVOList().forEach(responseTransactionBCAVO -> {
            transactionDetailRepository.save(TransactionDetailMapper.INSTANCE.toDomain(responseTransactionBCAVO, SYSTEM, finalTransactionHeader));
            i[0]++;
        });
        return finalTransactionHeader;
    }

    private Set<ResponseBCAVO> dataFromExcel(Set<String> urlFiles) {
        Set<ResponseBCAVO> responseBCAVOSet = new HashSet<>();
        urlFiles.forEach(urlFile -> {
            List<XSSFSheet> sheetList = fileService.readFileExcel(urlFile);
            responseBCAVOSet.add(convertFileBCA(sheetList));
        });
        return responseBCAVOSet;
    }

    private ResponseBCAVO convertFileBCA(List<XSSFSheet> sheetList) {
        ResponseBCAVO responseBCAVO = new ResponseBCAVO();
        List<ResponseTransactionBCAVO> responseFromBCAVOList = new ArrayList<>();
        ResponseCustomerInfoBCAVO responseCustomerInfoBCAVO = new ResponseCustomerInfoBCAVO();
        sheetList.forEach(sheet -> {
            Iterator<Row> itr = sheet.iterator();    //iterating over excel file
            String alamat = "";
            boolean flagCustNo = false;
            int indexForTanggal = 0;
            loopRow:
            while (itr.hasNext()) {
                Row row = itr.next();
                if (row.getRowNum() < 4) continue;
//                ResponseTransactionBCAVO responseFromBCAVO = new ResponseTransactionBCAVO();

                if (row.getCell(row.getFirstCellNum()).getStringCellValue().equalsIgnoreCase("REKENING GIRO")) {
                    flagCustNo = true;
                    continue;
                }
                if (row.getCell(row.getFirstCellNum()).getStringCellValue().contains("Bersambung ke Halaman berikut"))
                    continue;
                if (flagCustNo) {
                    if (row.getCell(row.getFirstCellNum()).getStringCellValue().contains("KCU")) continue loopRow;
                    for (int i = row.getFirstCellNum(); i <= row.getLastCellNum(); i++) {
                        if (responseCustomerInfoBCAVO.getNama() == null) {
                            if (i == 21 || i == 22 || i == 24 || i == 24) {
                                if (row.getCell(i) != null && !row.getCell(i).getStringCellValue().contains(":")) {
                                    if (responseCustomerInfoBCAVO.getNomorRekening() == null) {
                                        responseCustomerInfoBCAVO.setNama(row.getCell(row.getFirstCellNum()).getStringCellValue());
                                        responseCustomerInfoBCAVO.setBank(Bank.BCA.toString());
                                        responseCustomerInfoBCAVO.setNomorRekening(row.getCell(i).getStringCellValue());
                                        continue loopRow;
                                    }

                                }
                            }

                            continue;
                        } else {
                            if (row.getCell(i) != null && row.getCell(i).getStringCellValue().contains("CATATAN")) {
                                responseCustomerInfoBCAVO.setAlamat(alamat);
                                flagCustNo = false;
                                itr.next();
                                itr.next();
                                itr.next();
                                itr.next();
                                continue loopRow;
                            }
                            if (responseCustomerInfoBCAVO.getAlamat() == null) {
                                if (i == row.getFirstCellNum() && row.getCell(row.getFirstCellNum()) != null && !StringUtils.isEmpty(row.getCell(row.getFirstCellNum()).getStringCellValue()))
                                    alamat = alamat.trim() + " " + row.getCell(row.getFirstCellNum()).getStringCellValue();
                                if (i == 21 || i == 22 || i == 24 || i == 24) {
                                    if (row.getCell(i) != null && !row.getCell(i).getStringCellValue().contains(":")) {
                                        if (responseCustomerInfoBCAVO.getHalaman() == null) {
                                            if (row.getCell(i) != null && !StringUtils.isEmpty(row.getCell(i).getStringCellValue()))
                                                responseCustomerInfoBCAVO.setHalaman(row.getCell(i).getStringCellValue());
                                            continue loopRow;
                                        }
                                        if (responseCustomerInfoBCAVO.getPeriode() == null) {
                                            if (row.getCell(i) != null && !StringUtils.isEmpty(row.getCell(i).getStringCellValue()))
                                                responseCustomerInfoBCAVO.setPeriode(row.getCell(i).getStringCellValue());
                                            continue loopRow;
                                        }
                                    }
                                }
                            }
                            continue;
                        }

                    }
                } else {
                    String tanggal = null;
                    List<String> keterangan = new ArrayList<>();
                    List<String> subKeterangan = new ArrayList<>();
                    ResponseTransactionBCAVO responseFromBCAVO = new ResponseTransactionBCAVO();
                    for (int i = 2; i <= row.getLastCellNum(); i++) {
                        if (i == 2 || i == 3) {
                            if (row.getCell(i) != null && !StringUtils.isEmpty(row.getCell(i).getStringCellValue())) {
                                tanggal = row.getCell(i).getStringCellValue();
                                keterangan = new ArrayList<>();
                                subKeterangan = new ArrayList<>();
                                responseFromBCAVO.setTanggal(tanggal);
                            }
                        }
                        if (i == 4 || i == 5 || i == 6 || i == 7) {
                            if (tanggal == null) {
                                indexForTanggal = responseFromBCAVOList.size() - 1;
                                if (row.getCell(i) != null && !StringUtils.isEmpty(row.getCell(i).getStringCellValue())) {
                                    if (responseFromBCAVOList.get(indexForTanggal).getKeterangan() != null)
                                        keterangan = responseFromBCAVOList.get(indexForTanggal).getKeterangan();
                                    keterangan.add(row.getCell(i).getStringCellValue());
                                    responseFromBCAVOList.get(indexForTanggal).setKeterangan(keterangan);
                                }
                                continue;
                            } else {
                                if (row.getCell(i) != null && !StringUtils.isEmpty(row.getCell(i).getStringCellValue())) {
                                    keterangan.add(row.getCell(i).getStringCellValue());
                                    responseFromBCAVO.setKeterangan(keterangan);
                                }
                            }
                        }
                        if (i == 8 || i == 9 || i == 10 || i == 11 || i == 12) {
                            if (tanggal == null) {
                                indexForTanggal = responseFromBCAVOList.size() - 1;
                                if (row.getCell(i) != null && !StringUtils.isEmpty(row.getCell(i).getStringCellValue())) {
                                    if (responseFromBCAVOList.get(indexForTanggal).getSubKeterangan() != null)
                                        subKeterangan = responseFromBCAVOList.get(indexForTanggal).getSubKeterangan();
                                    subKeterangan.add(row.getCell(i).getStringCellValue());
                                    responseFromBCAVOList.get(indexForTanggal).setSubKeterangan(subKeterangan);
                                }
                                continue loopRow;
                            } else {
                                if (row.getCell(i) != null && !StringUtils.isEmpty(row.getCell(i).getStringCellValue())) {
                                    subKeterangan.add(row.getCell(i).getStringCellValue());
                                    responseFromBCAVO.setSubKeterangan(subKeterangan);
                                }
                            }
                        }
                        if (i == 13 || i == 14) {
                            if (row.getCell(i) != null && !StringUtils.isEmpty(row.getCell(i).getStringCellValue())) {
                                responseFromBCAVO.setCbg(row.getCell(i).getStringCellValue());
                            }
                        }
                        if (i == 15 || i == 16 || i == 17 || i == 18 || i == 19 || i == 20) {
                            if (row.getCell(i) != null && !StringUtils.isEmpty(row.getCell(i).getStringCellValue())) {
                                responseFromBCAVO.setMutasi(row.getCell(i).getStringCellValue());
                            }
                        }
                        if (i == 21 || i == 22 || i == 23) {
                            if (row.getCell(i) != null && !StringUtils.isEmpty(row.getCell(i).getStringCellValue())) {
                                responseFromBCAVO.setKetMutasi(row.getCell(i).getStringCellValue());
                            }
                        }
                        if (i == 24 || i == 25 || i == 26) {
                            if (row.getCell(i) != null && !StringUtils.isEmpty(row.getCell(i).getStringCellValue())) {
                                responseFromBCAVO.setSaldo(row.getCell(i).getStringCellValue());
                            }
                        }
                    }
                    responseFromBCAVOList.add(responseFromBCAVO);
                    if (responseFromBCAVO.getKeterangan() != null && responseFromBCAVO.getKeterangan().get(0).equalsIgnoreCase("PAJAK BUNGA"))
                        break loopRow;
                }
            }
        });

        responseBCAVO.setResponseTransactionBCAVOList(responseFromBCAVOList);
        responseBCAVO.setResponseCustomerInfoBCAVO(responseCustomerInfoBCAVO);
        return responseBCAVO;
    }
}
