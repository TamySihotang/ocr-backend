package com.danamon.service;

import com.danamon.enums.Bank;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface RekeningKoranService {
    String convertReportRekening(Bank bank, Set<MultipartFile> file);
}
