package com.example.demo.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SearchBoardResponseDto {

    private Long boardId;
    private String title;
    private String content;
    private String nickName;
    private LocalDateTime createdDate;
    private long viewCnt;
    private long likeCnt;
}
