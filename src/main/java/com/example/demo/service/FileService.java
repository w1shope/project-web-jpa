package com.example.demo.service;

import com.example.demo.dto.RequestUpdateFileDto;
import com.example.demo.entity.File;
import com.example.demo.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final FileRepository fileRepository;

    @Transactional
    public Long save(File file) {
        return fileRepository.save(file).getId();
    }

    @Transactional
    public void changeFile(RequestUpdateFileDto request) {
        fileRepository.changeFile(request.getFileId(), request.getUploadFilename(), request.getStoredFilePath());
    }

    @Transactional
    public void removeFile(Long fileId) {
        fileRepository.removeFile(fileId);
    }

}
