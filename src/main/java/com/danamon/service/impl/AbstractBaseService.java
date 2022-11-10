package com.danamon.service.impl;

import com.danamon.utils.Constants;
import com.danamon.utils.ExtendedSpringBeanUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractBaseService extends ExtendedSpringBeanUtil {

    public static Sort.Direction getSortBy(String sortBy) {
        if (sortBy.equalsIgnoreCase("asc")) {
            return Sort.Direction.ASC;
        } else {
            return Sort.Direction.DESC;
        }
    }

    public static Map<String, Object> constructMapReturn(Object voList, long totalElements, int totalPages) {
        Map<String, Object> map = new HashMap<>();

        map.put(Constants.PageParameter.LIST_DATA, voList);
        map.put(Constants.PageParameter.TOTAL_ELEMENTS, totalElements);
        map.put(Constants.PageParameter.TOTAL_PAGES, totalPages);

        return map;
    }

    protected Pageable setPagination(Integer page, Integer limit, String sortBy, String direction) {
        return PageRequest.of(page, limit, Sort.by(direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
    }

    protected String convertToLikeQuery(String value) {
        value = value.trim();
        if (StringUtils.isEmpty(value)) return "%%";
        else return  "%" + value.toUpperCase() + "%";
    }

    protected List<Object> setListStatus(String[] arrStatus) {
        List<Object> objectList = new ArrayList<>();
        for (int i = 0; i < arrStatus.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("status", arrStatus[i].toUpperCase());
            map.put("value", arrStatus[i]);
            objectList.add(map);
        }
        return objectList;
    }

    public static Integer getPage(Map<String, Object> param){
        return (Integer) param.get("page");
    }

    public static Integer getLimit(Map<String, Object> param){
        return (Integer) param.get("limit");
    }

    public static String getDirection(Map<String, Object> param){
        return (String) param.get("direction");
    }

    public static String getSortBy(Map<String, Object> param){
        return (String) param.get("sortBy");
    }

    public static String getSearchBy(Map<String, Object> param){
        return (String) param.get("searchBy");
    }

}