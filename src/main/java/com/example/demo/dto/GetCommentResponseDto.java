package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GetCommentResponseDto {

    private String nickName;
    private String content;
    private long likeCnt;
}
