package com.danamon.utils;

import org.springframework.core.io.ByteArrayResource;

public class MultipartByteArrayResource extends ByteArrayResource {
    private String fileName;

    public MultipartByteArrayResource(byte[] byteArray) {
        super(byteArray);
    }

    @Override
    public String getFilename() {
        return fileName;
    }

    public void setFilename(String fileName) {
        this.fileName= fileName;
    }
}
