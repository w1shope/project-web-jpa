package com.example.demo;

import com.example.demo.entity.Member;
import com.example.demo.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final MemberService memberService;

    @GetMapping("/home")
    public String home(@SessionAttribute(name = "loginId", required = false) String loginId, Model model) {
        try {
            Member loginMember = memberService.getLoginMember(loginId);
            model.addAttribute("loginMember", loginMember);
        } catch(NoSuchElementException ex) {
        }
        log.info("session = {}", loginId);
        return "/home";
    }
}
