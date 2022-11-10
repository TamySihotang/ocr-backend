package com.danamon.controller;

import com.danamon.enums.StatusCode;
import com.danamon.exception.ApplicationException;
import com.danamon.vo.ResultVO;
import com.danamon.utils.Constants;
import com.danamon.vo.ResultPageVO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRequestHandler {

    private static final String CONTENT_TYPE = "Content-Type";

    public static ResponseEntity<ResultPageVO> constructListResult(Map<String, Object> pageMap) {
        ResultPageVO result = new ResultPageVO();
        try {
            Collection list = constructPageResult(pageMap, result);
            result.setResult(list);
        } catch (Exception e) {
            result.setMessage(e.getMessage());
        }
        return getJsonResponse(result);
    }

    public static Collection constructPageResult(Map<String, Object> map, ResultPageVO result) {
        if (map == null) {
            result.setPages("0");
            result.setElements("0");
            result.setMessage(StatusCode.DATA_NOT_FOUND.name());
            return null;
        } else {
            Collection vos = (Collection) map.get(Constants.PageParameter.LIST_DATA);
            result.setPages(String.valueOf(map.get(Constants.PageParameter.TOTAL_PAGES)));
            result.setElements(String.valueOf(map.get(Constants.PageParameter.TOTAL_ELEMENTS)));
            result.setMessage(StatusCode.OK.name());
            return vos;
        }
    }
    public ResponseEntity<ResultPageVO> getResultPage() {
        ResultPageVO result = new ResultPageVO();
        Map<String, Object> pageMap;

        try {
            Object obj = processRequest();
            if (obj instanceof Map) {
                pageMap = (Map<String, Object>) obj;
                return constructListResult(pageMap);
            }

            constructResponse(result, obj);
        } catch (ApplicationException e) {
            result.setMessage(e.getStatusCode().name());
            result.setResult(e.getMessage());
        }
        return getJsonResponse(result);
    }

    public ResponseEntity<ResultVO> getResult() {
        ResultVO result = new ResultVO();
        try {
            Object obj = processRequest();

            if (obj instanceof ResultVO) {
                result = (ResultVO) obj;
                return getJsonResponse(result);
            }

            constructResponse(result, obj);
        } catch (ApplicationException e) {
            result.setMessage(e.getStatusCode().name());
            result.setResult(e.getMessage());
        }
        return getJsonResponse(result);
    }

    private void constructResponse(ResultVO result, Object obj) {
        if (obj != null) {
            result.setMessage(StatusCode.OK.name());
            result.setResult(obj);
        } else {
            result.setMessage(StatusCode.OK.name());
            result.setResult(null);
        }
    }

    public abstract Object processRequest();

    public static <T> ResponseEntity<T> getJsonResponse(T src) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return new ResponseEntity<>(src, headers, HttpStatus.OK);
    }

    public static Map<String, Object> mapObject(Integer page, Integer limit, String direction, String sortBy, String searchBy) {
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("page", page);
        parameter.put("limit", limit);
        parameter.put("direction", direction);
        parameter.put("sortBy", sortBy);
        parameter.put("searchBy", searchBy);
        return parameter;
    }
}
