package com.danamon.controller;

import com.danamon.enums.Bank;
import com.danamon.service.RekeningKoranService;
import com.danamon.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@RequestMapping(value = "/api/rekening-koran")
public class RekeningKoranController {
    private final RekeningKoranService rekeningKoranService;

    @Autowired
    public RekeningKoranController(RekeningKoranService rekeningKoranService) {
        this.rekeningKoranService = rekeningKoranService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResultVO> uploadFile(@RequestParam("file") Set<MultipartFile> file,
                                               @RequestParam(value = "bank") Bank bank) {
        AbstractRequestHandler handler = new AbstractRequestHandler() {
            @Override
            public Object processRequest() {
                return rekeningKoranService.convertReportRekening(bank, file);
            }
        };
        return handler.getResult();
    }
}
