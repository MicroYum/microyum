package com.microyum.service.impl;

import com.microyum.common.Constants;
import com.microyum.common.util.DateUtils;
import com.microyum.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Override
    public String saveBlogPicture(MultipartFile file) {

        String dir = Constants.BLOG_FILE_ACTUAL_PICTURE_DIR + DateUtils.formatDate(new Date(), DateUtils.DATE_FORMAT_COMP) + "/";
        File path = new File(dir);

        if (!path.exists()) {
            path.mkdirs();
        }

        String prefix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        String fileName = UUID.randomUUID().toString() + "." + prefix;

        try {
            file.transferTo(new File(dir + fileName));
        } catch (IOException e) {
            log.error("Save blog picture error.", e);
            return null;
        }

        return Constants.BLOG_FILE_VIRTUAL_PICTURE_DIR + DateUtils.formatDate(new Date(), DateUtils.DATE_FORMAT_COMP) + "/" + fileName;
    }
}
