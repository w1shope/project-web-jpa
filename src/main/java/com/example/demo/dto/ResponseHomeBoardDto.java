package com.example.demo.dto;

import com.example.demo.entity.File;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ResponseHomeBoardDto {

    private Long boardId; // 게시물 번호
    private String title; // 게시물 제목
    private String content; // 게시물 내용
    private long likeCnt; // 게시물 좋아요 개수
    private long viewCnt; // 게시물 조회수
    private File file; // 이미지 파일
}
