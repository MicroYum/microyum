package com.microyum.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    String saveBlogPicture(MultipartFile file);
}
