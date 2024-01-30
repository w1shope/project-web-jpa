package com.example.demo.vo;

import com.example.demo.entity.QuestionState;
import lombok.Data;

@Data
public class UpdateQuestionStateVO {

    private Long questionId;
    private QuestionState state;
}
