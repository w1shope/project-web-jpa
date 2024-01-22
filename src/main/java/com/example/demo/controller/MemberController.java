package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import com.example.demo.service.BoardService;
import com.example.demo.service.MemberService;
import com.example.demo.vo.CheckJoinIdVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final BoardService boardService;

    @GetMapping("/join")
    public String joinPage(@ModelAttribute("joinMember") RequestJoinMemberDto requestJoinMemberDto) {
        return "/join";
    }

    @PostMapping("/join")
    public String joinMember(@Valid @ModelAttribute("joinMember") RequestJoinMemberDto request
            , BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/join";
        }
        memberService.save(request);
        return "redirect:/home";
    }

    @GetMapping("/login")
    public String loginPage(@ModelAttribute("loginMember") RequestLoginMemberDto request) {
        return "/login";
    }

    @PostMapping("/login")
    public String loginMember(@Valid @ModelAttribute("loginMember") RequestLoginMemberDto request
            , BindingResult bindingResult, Model model, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            return "/login";
        }

        try {
            Member loginMember = memberService.login(request);
            HttpSession session = httpServletRequest.getSession();
            session.setAttribute("loginId", loginMember.getLoginId());
        } catch (NoSuchElementException ex) { // 일치하는 회원정보 없음
            model.addAttribute("loginFail", "일치하는 회원이 존재하지 않습니다");
            return "/login";
        }
        return "redirect:/home";
    }

    @GetMapping("/logout")
    public String logoutMember(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();
        return "redirect:/home";
    }

    @GetMapping("/member/{id}")
    public String memberInfoPage(@PathVariable("id") Long id, Model model) {
        try {
            Member member = memberService.findMember(id);
            model.addAttribute("loginMember", member);
        } catch (NoSuchElementException ex) {
            model.addAttribute("findFailed", "회원정보를 찾을 수 없습니다");
            return "/home";
        }

        return "/myInfo";
    }

    @GetMapping("/member/{loginId}/email")
    public String updateEmailPage(@ModelAttribute("updateMember") RequestUpdateMemberEmailDto request,
                                  @PathVariable("loginId") String loginId, Model model) {
        Member loginMember = memberService.getLoginMember(loginId);
        model.addAttribute("loginMember", loginMember);
        return "/updateEmail";
    }

    @PatchMapping("/member/{loginId}/email")
    public String updateEmail(@PathVariable("loginId") String loginId
            , @Valid @ModelAttribute("updateMember") RequestUpdateMemberEmailDto request
            , BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("loginMember", memberService.getLoginMember(loginId));
            redirectAttributes.addAttribute("updateFailed", "회원정보 수정에 실패하였습니다");
            return "/updateEmail";
        }

        int result = memberService.updateEmail(loginId, request);
        if (result != 1) {
            redirectAttributes.addAttribute("updateFailed", "회원정보 변경에 실패하였습니다");
            redirectAttributes.addAttribute("loginId", loginId);
            return "redirect:/member/{loginId}/email";
        }
        Member updatedMember = memberService.getLoginMember(loginId);
        redirectAttributes.addAttribute("id", updatedMember.getId());
        return "redirect:/member/{id}";
    }

    @GetMapping("/member/{loginId}/phone")
    public String updatePhonePage(@PathVariable("loginId") String loginId
            , @ModelAttribute("updateMember") RequestUpdateMemberPhoneDto request
            , Model model, RedirectAttributes redirectAttributes) {
        try {
            Member loginMember = memberService.getLoginMember(loginId);
            model.addAttribute("loginMember", loginMember);
            return "/updatePhone";
        } catch (NoSuchElementException ex) {
            redirectAttributes.addAttribute("updateFailed", "올바른 요청이 아닙니다");
            return "redirect:/home";
        }
    }

    @PatchMapping("/member/{loginId}/phone")
    public String updatePhone(@PathVariable("loginId") String loginId
            , @Valid @ModelAttribute("updateMember") RequestUpdateMemberPhoneDto request
            , BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("loginMember", memberService.getLoginMember(loginId));
            return "/updatePhone";
        }

        int result = memberService.updatePhone(loginId, request);
        if (result != 1) {
            redirectAttributes.addAttribute("updateFailed", "회원정보 변경에 실패하였습니다");
            redirectAttributes.addAttribute("loginId", loginId);
            return "redirect:/member/{loginId}/phone";
        }
        Member updatedMember = memberService.getLoginMember(loginId);
        redirectAttributes.addAttribute("id", updatedMember.getId());
        return "redirect:/member/{id}";
    }

    @GetMapping("/member/{loginId}/boards")
    public String findAllBoardsWrittenByMe(@PathVariable("loginId") String loginId
            , RedirectAttributes redirectAttributes, Model model) {
        try {
//            Member member = memberService.findBoardWrittenByMe(loginId);
//            model.addAttribute("boards", member.getBoards());
            List<ResponseBoardListDto> result = boardService.getAllPosts(loginId).stream()
                    .map(post -> new ResponseBoardListDto(post.getId(), post.getTitle(), post.getContent(),
                            post.getMember().getNickName(), post.getCreatedDate(), post.getViewCnt(), post.getLikeCnt()))
                    .collect(Collectors.toList());
            model.addAttribute("boards", result);
            return "/myPost";
        } catch (NoSuchElementException ex) {
            redirectAttributes.addAttribute("findFailed", ex.getMessage());
            return "redirect:/home";
        }
    }

    @ResponseBody
    @PostMapping("/join/checkId")
    public ResponseAvailabilityJoinDto checkDuplicateId(@RequestBody CheckJoinIdVO vo) {
        int isDuplicate = memberService.checkDuplicateJoinId(vo);
        log.info("duplicate={}", isDuplicate);
        return new ResponseAvailabilityJoinDto(
                isDuplicate == 0 ? true : false
        );
    }
}
