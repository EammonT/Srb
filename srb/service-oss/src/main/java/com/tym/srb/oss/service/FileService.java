package com.tym.srb.oss.service;

import java.io.InputStream;


public interface FileService {
    String upload(InputStream inputStream, String module, String originalFilename);

    boolean removeFile(String url);
}
