package com.danamon.service;

import com.danamon.enums.Bank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

public interface RekeningKoranService {
    ResponseEntity convertReportRekening(Bank bank, Set<MultipartFile> file) throws IOException;
}
