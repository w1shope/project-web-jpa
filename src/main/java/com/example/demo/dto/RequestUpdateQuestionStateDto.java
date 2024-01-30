package com.example.demo.dto;

import com.example.demo.entity.QuestionState;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestUpdateQuestionStateDto {

    private Long id;
    private QuestionState state;
}
