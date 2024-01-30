package com.example.demo.dto;

import lombok.Data;

@Data
public class RequestQuestionEnrolDto {

    private Long questionId;
    private String title;
    private String content;
}
