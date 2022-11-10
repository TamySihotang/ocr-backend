package com.danamon.service;


import com.danamon.persistence.domain.TransactionHeader;

import java.util.List;
import java.util.Set;

public interface BcaService {
    List<TransactionHeader> saveDataBca(Set<String> file);
}
