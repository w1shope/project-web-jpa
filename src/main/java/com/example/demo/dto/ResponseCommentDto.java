package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ResponseCommentDto {

    private String writer;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime editDate;
    private long likeCnt;
}
