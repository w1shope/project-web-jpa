package com.example.demo;

import com.example.demo.dto.ResponseHomeBoardDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import com.example.demo.repository.BoardRepository;
import com.example.demo.service.BoardService;
import com.example.demo.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final MemberService memberService;
    private final BoardService boardService;

    @GetMapping("/")
    public String index(RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("page", 0);
        redirectAttributes.addAttribute("size", 6);
        return "redirect:/home";
    }

    //    @GetMapping("/home")
    public String home(@SessionAttribute(name = "loginId", required = false) String loginId, Model model) {
        try {
            Member loginMember = memberService.getLoginMember(loginId);
            model.addAttribute("loginMember", loginMember);
        } catch (NoSuchElementException ex) {
        }
        List<ResponseHomeBoardDto> result = boardService.findBoardWithFile().stream()
            .map(board -> new ResponseHomeBoardDto(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getLikeCnt(),
                board.getViewCnt(),
                board.getFile()
            )).collect(Collectors.toList());
        model.addAttribute("boards", result);
        log.info("result={}", result.size());
        return "/home";
    }

    @GetMapping("/home")
    public String pagingHome(@SessionAttribute(name = "loginId", required = false) String loginId, Model model
        , @PageableDefault(size = 6) Pageable pageable) {
        try {
            Member loginMember = memberService.getLoginMember(loginId);
            model.addAttribute("loginMember", loginMember);
        } catch (NoSuchElementException ex) {
        }
        List<ResponseHomeBoardDto> result = boardService.findBoardWithFile(pageable).stream()
            .map(board -> new ResponseHomeBoardDto(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getLikeCnt(),
                board.getViewCnt(),
                board.getFile()
            )).collect(Collectors.toList());
        model.addAttribute("boards", result);
        model.addAttribute("pages", IntStream.rangeClosed(0, boardService.findBoardWithFile().size() / (pageable.getPageSize() + 1)).boxed().collect(Collectors.toList()));
        return "/home";
    }
}