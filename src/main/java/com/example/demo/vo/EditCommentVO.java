package com.example.demo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EditCommentVO {

    private Long boardId;
    private String newContent;
    private LocalDateTime commentCreatedDate;
}
