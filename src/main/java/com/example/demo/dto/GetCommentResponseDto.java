package com.example.demo.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GetCommentResponseDto {

    private String nickName;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime editDate;
    private long likeCnt;
}
