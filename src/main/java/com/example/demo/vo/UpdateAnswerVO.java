package com.example.demo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateAnswerVO {

    private Long questionId;
    private String answerWriter;
    private String updateContent;
    private LocalDateTime answerCreatedDate;
}
