package com.example.demo.file;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class FileStore {

    @Value(value = "${store_dir_path}")
    private String storeDirPath;

    /**
     * 파일이 저장되는 서버 경로 반환
     */
    public String getStoredFullPath(String filename) {
        return storeDirPath + filename;
    }

    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storedFileName = getStoredFileName(originalFilename);
        multipartFile.transferTo(new File(getStoredFullPath(storedFileName)));
        return new UploadFile(originalFilename, storedFileName);
    }

    /**
     * 서버에 저장할 중복되지 않는 파일명 생성
     */
    private String getStoredFileName(String originalFilename) {
        return UUID.randomUUID().toString() + "."
                + getExtend(originalFilename);
    }

    /**
     * 파일 확장자 반환
     */
    private String getExtend(String uploadFilename) {
        return uploadFilename.substring(
                uploadFilename.lastIndexOf(".") + 1);
    }
}
