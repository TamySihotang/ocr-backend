package com.danamon.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class HttpServiceLoggingInterceptor implements Interceptor {

    private Logger log = LoggerFactory.getLogger(HttpServiceLoggingInterceptor.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Response intercept(Chain chain) throws IOException {

        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        Request request = chain.request();
        Response response = chain.proceed(request);

        Map<String, Object> requestMap = new HashMap<>(4);
        requestMap.put("httpMethod", request.method());
        requestMap.put("endpoint", request.url().url());
        requestMap.put("body", request.body());
        requestMap.put("sentAt", DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochMilli(response.sentRequestAtMillis())));

        Map<String, Object> responseMap = new HashMap<>(3);
        responseMap.put("httpCode", response.code());
        responseMap.put("body", response.peekBody(100).string());
        responseMap.put("receivedAt", DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochMilli(response.receivedResponseAtMillis())));

        Map<String, Map<String, Object>> requestAndResponseMap = new HashMap<>(2);
        requestAndResponseMap.put("request", requestMap);
        requestAndResponseMap.put("response", responseMap);

        log.info(objectMapper.writeValueAsString(requestAndResponseMap));

        return response;
    }
}
