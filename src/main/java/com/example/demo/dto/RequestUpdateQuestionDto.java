package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestUpdateQuestionDto {

    private Long id;
    private String title;
    private String content;
}
