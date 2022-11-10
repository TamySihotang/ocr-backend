package com.danamon.common;

import com.danamon.enums.StatusCode;
import com.danamon.exception.ApplicationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class HttpService {

    private Logger log = LoggerFactory.getLogger(HttpService.class);

    private static final MediaType JSON = MediaType.parse("application/json; charset=UTF-8");
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public HttpService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

        Interceptor loggingInterceptor = new HttpServiceLoggingInterceptor();

        this.okHttpClient = new OkHttpClient.Builder().
                connectionPool(new ConnectionPool(5,
                        15, TimeUnit.SECONDS))
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build();
    }

    public <T> T executePost(String url, Headers headers, Object payload, TypeReference typeOfT, HttpResponseProcessor httpResponseProcessor) {
        RequestBody body = createJsonRequestBody(payload);

        Request request = null != headers ? new Request.Builder().headers(headers).url(url).post(body).build() :
                new Request.Builder().url(url).post(body).build();

        return executeRequest(typeOfT, request, httpResponseProcessor);
    }

    public <T> T executePost(String url, Headers headers, Object payload, TypeReference typeOfT) {
        return executePost(url, headers, payload, typeOfT, null);
    }

    public <T> T executePost(String url, Object payload, TypeReference typeOfT) {
        return executePost(url, null, payload, typeOfT);
    }

    public <T> T executePut(String url, Object payload, TypeReference typeOfT) {
        return executePut(url, payload, typeOfT, null);
    }

    public <T> T executePut(String url, Object payload, TypeReference typeOfT, HttpResponseProcessor httpResponseProcessor) {
        RequestBody body = createJsonRequestBody(payload);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();
        return executeRequest(typeOfT, request, httpResponseProcessor);
    }

    public <T> T executeGet(String url, Headers headers, @Nullable Map<String, String> queryParam, TypeReference typeOfT, HttpResponseProcessor httpResponseProcessor) {
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();

        if (queryParam != null) {
            for (Map.Entry<String, String> entry : queryParam.entrySet()) {
                httpBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        Request request = null != headers ? new Request.Builder().headers(headers).url(httpBuilder.build()).get().build() :
                new Request.Builder().url(httpBuilder.build()).get().build();

        return executeRequest(typeOfT, request, httpResponseProcessor);
    }

    public <T> T executeGet(String url, Headers headers, @Nullable Map<String, String> queryParam, TypeReference typeOfT) {
        return executeGet(url, headers, queryParam, typeOfT, null);
    }

    public <T> T executeGet(String url, @Nullable Map<String, String> queryParam, TypeReference typeOfT) {
        return executeGet(url, null, queryParam, typeOfT, null);
    }

    private <T> T executeRequest(TypeReference typeOfT, Request request, HttpResponseProcessor httpResponseProcessor) {
        T responseObject;
        try (Response response = okHttpClient.newCall(request).execute()) {
            responseObject = null == httpResponseProcessor ? processHttpResponse(typeOfT, response) : httpResponseProcessor.process(typeOfT, response);

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ApplicationException(e.getMessage(), StatusCode.ERROR);
        }
        return responseObject;
    }

    private <T> T processHttpResponse(TypeReference typeOfT, Response response) throws IOException {
        T responseObject;
        ResponseBody body = response.body();
        if (response.isSuccessful() && body != null) {
            String stringBody = body.string();
            try {
                responseObject = (T) objectMapper.readValue(stringBody, typeOfT);
            } catch (IOException e) {
                log.error("something error when get data from api {}", e.getMessage());
                return (T) stringBody;
            }
        } else {
            log.error("something error when get data from api ");
            throw new ApplicationException("FIFApps API is Down.  Please call head office.", StatusCode.ERROR);
        }
        return responseObject;
    }


    private RequestBody createJsonRequestBody(Object payload) {
        try {
            return RequestBody.create(JSON, objectMapper.writeValueAsBytes(payload));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new ApplicationException(e.getMessage(), StatusCode.ERROR);
        }
    }
}
