package com.example.demo.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RequestLoginMemberDto {

    @NotEmpty(message = "아이디를 입력하세요")
    private String loginId;
    @NotEmpty(message = "비밀번호를 입력하세요")
    private String loginPassword;
}
