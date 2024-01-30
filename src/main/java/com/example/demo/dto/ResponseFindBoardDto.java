package com.example.demo.dto;

import com.example.demo.entity.Comment;
import com.example.demo.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ResponseFindBoardDto {

    private Long boardId;
    private String title;
    private String content;
    private long likeCnt;
    private String uploadFilename;
    private Member member;
}
