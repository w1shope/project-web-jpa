package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class GetBoardsResponseDto {

    private Long boardId;
    private String title;
    private String content;
    private String nickName;
    private LocalDateTime createdDate;
    private long viewCnt;
    private long likeCnt;
}
