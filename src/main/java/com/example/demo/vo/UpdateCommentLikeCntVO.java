package com.example.demo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateCommentLikeCntVO {

    private String commentWriter;
    private String commentContent;
    private LocalDateTime commentCreatedDate;
}
