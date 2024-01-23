package com.example.demo.service;

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
}
