package com.example.demo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeleteAnswerVO {

    private Long questionId;
    private String answerWriter;
    private LocalDateTime answerCreatedDate;
}
