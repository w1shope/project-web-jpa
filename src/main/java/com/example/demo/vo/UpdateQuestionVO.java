package com.example.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class UpdateQuestionVO {

    private Long questionId;
    private String title;
    private String content;
}
