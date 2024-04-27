package com.example.demo.vo;

import lombok.Data;

@Data
public class WriteCommentRequestDto {

    private Long boardId;
    private String content;
}
