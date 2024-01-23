package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseViewBoardDto {

    private Long boardId;
    private String title;
    private String content;
    private long likeCnt;
    private String uploadFilename;
}
