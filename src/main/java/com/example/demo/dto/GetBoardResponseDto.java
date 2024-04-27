package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GetBoardResponseDto {

    private Long boardId;
    private String title;
    private String content;
    private Long fileId;
    private String uploadFileName;
    private String storedFileName;
    private long likeCnt;
    private Long memberId;
}
