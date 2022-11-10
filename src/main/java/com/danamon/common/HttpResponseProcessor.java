package com.danamon.common;

import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.Response;

import java.io.IOException;

public interface HttpResponseProcessor {

    <T> T process(TypeReference typeOfT, Response response) throws IOException;
}
