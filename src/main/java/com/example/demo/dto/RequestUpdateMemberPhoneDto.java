package com.example.demo.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RequestUpdateMemberPhoneDto {

    @NotEmpty(message = "전화번호를 입력하세요")
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$"
            , message = "올바른 형식의 전화번호를 입력하세요")
    private String updatePhone;
}
