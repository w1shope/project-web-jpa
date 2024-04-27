package com.example.demo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeleteCommentRequestDto {

    private Long boardId;
    private LocalDateTime commentCreatedDate;
    private String commentContent;
}
