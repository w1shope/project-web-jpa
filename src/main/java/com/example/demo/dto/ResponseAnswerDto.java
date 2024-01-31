package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ResponseAnswerDto {

    private String writer;
    private String content;
    private LocalDateTime createdDate;
}
