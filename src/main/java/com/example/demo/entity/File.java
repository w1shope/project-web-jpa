package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    private String uploadFilename; // 실제 파일명
    private String storedFileName; // 서버에 저장된 파일명
    private String storedFilePath; // 파일이 저장된 서버 경로

    @Builder
    public File(String uploadFilename, String storedFileName, String storedFilePath) {
        this.uploadFilename = uploadFilename;
        this.storedFileName = storedFileName;
        this.storedFilePath = storedFilePath;
    }
}
