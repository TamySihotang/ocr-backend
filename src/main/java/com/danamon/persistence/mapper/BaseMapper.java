package com.danamon.persistence.mapper;

import java.util.ArrayList;
import java.util.List;

public interface BaseMapper<D,V> {

    D toDomain(V vo);
    V toVo(D domain);

    List<D> toDomains(List<V> vos);
    List<V> toVos(List<D> domains);

    ArrayList<D> toDomainArrayList(List<V> vos);
    ArrayList<V> toVoArrayList(List<D> domains);
}
