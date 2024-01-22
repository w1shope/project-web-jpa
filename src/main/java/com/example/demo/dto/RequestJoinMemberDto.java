package com.example.demo.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

@Data
public class RequestJoinMemberDto {

    @NotEmpty(message = "아이디를 입력하세요")
    private String loginId;
    @NotEmpty(message = "비밀번호를 입력하세요")
    private String password;
    @NotEmpty(message = "닉네임을 입력하세요")
    private String username;
    @NotEmpty(message = "별명을 입력하세요")
    private String nickName;
    @NotEmpty(message = "이메일을 입력하세요")
    @Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
            , message = "올바른 형식의 이메일을 입력하세요")
    private String email;
    @NotEmpty(message = "전화번호를 입력하세요")
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$"
            , message = "올바른 형식의 전화번호를 입력하세요")
    private String phone;
}
