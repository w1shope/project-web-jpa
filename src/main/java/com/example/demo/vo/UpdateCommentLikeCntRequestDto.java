package com.example.demo.vo;

import lombok.Data;

@Data
public class UpdateCommentLikeCntRequestDto {

    private Long boardId;
    private String commentWriter;
    private String commentContent;
}
