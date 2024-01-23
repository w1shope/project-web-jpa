package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestUpdateFileDto {

    private Long fileId;
    private String uploadFilename;
    private String storedFilePath;
}
