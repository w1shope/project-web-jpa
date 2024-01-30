package com.example.demo.dto;

import com.example.demo.entity.QuestionState;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ResponseQuestionDto {

    private Long questionId;
    private String title;
    private String content;
    private String username;
    private LocalDateTime createdDate;
    private QuestionState questionState;
}
