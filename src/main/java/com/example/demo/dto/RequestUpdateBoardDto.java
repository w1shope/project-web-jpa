package com.example.demo.dto;

import com.example.demo.entity.File;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestUpdateBoardDto {

    private Long boardId;
    private String title;
    private String content;
    private File file;
}
