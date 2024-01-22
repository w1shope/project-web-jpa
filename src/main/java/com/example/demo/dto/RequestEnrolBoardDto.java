package com.example.demo.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RequestEnrolBoardDto {

    private String loginId;
    @NotEmpty(message = "제목을 입력하세요")
    private String title;
    @NotEmpty(message = "내용을 입력하세요")
    private String content;
}
