package com.example.demo.controller;

import com.example.demo.dto.RequestQuestionEnrolDto;
import com.example.demo.dto.RequestUpdateQuestionDto;
import com.example.demo.dto.ResponseQuestionDto;
import com.example.demo.entity.Member;
import com.example.demo.entity.Question;
import com.example.demo.service.MemberService;
import com.example.demo.service.QuestionService;
import com.example.demo.vo.DeleteQuestionVO;
import com.example.demo.vo.UpdateQuestionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;

@Controller
@Slf4j
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final MemberService memberService;

    /**
     * 전체 게시물 가져오기
     */
    @GetMapping("/questions")
    public String getAllQuestion(Model model) {
        model.addAttribute("questions", questionService.getAllWithMember());
        return "/questionList";
    }

    /**
     * 특정 게시물 가져오기
     */
    @GetMapping("/questions/{id}")
    private String getQuestion(@PathVariable("id") Long questionId, Model model) {
        try {
            Question findQuestion = questionService.findQuestionWithMemberById(questionId)
                    .orElseThrow(() -> new NoSuchElementException("게시물을 읽어올 수 잆습니다."));
            model.addAttribute("question", new ResponseQuestionDto(
                    findQuestion.getId(), findQuestion.getTitle(), findQuestion.getContent(), findQuestion.getMember().getUsername(),
                    findQuestion.getCreatedDate(), findQuestion.getQuestionState()
            ));
            return "/questionView";
        } catch (NoSuchElementException ex) {
            // 게시물을 읽어올 수 없으면 전체 게시물로 이동
            return "redirect:/questions";
        }
    }

    /**
     * 질문 삭제
     */
    @ResponseBody
    @DeleteMapping("/questions/{id}")
    public String deleteQuestion(@RequestBody DeleteQuestionVO vo, RedirectAttributes redirectAttributes) {
        try {
            Question findQuestion = questionService.findQuestionWithMemberById(vo.getQuestionId())
                    .orElseThrow(() -> new NoSuchElementException("게시물이 존재하지 않습니다."));
            questionService.deleteQuestion(findQuestion);
            return "ok";
        }
        // 게시물 존재X
        catch(NoSuchElementException ex) {
            redirectAttributes.addAttribute("id", vo.getQuestionId());
            throw ex;
        }
    }

    /**
     * 게시물 등록
     */
    @GetMapping("/questions/enrol")
    public String enrolQuestionPage(@ModelAttribute("question")RequestQuestionEnrolDto request) {
        return "/question";
    }

    @PostMapping("/questions/enrol")
    public String enrolQuestion(@ModelAttribute("question") RequestQuestionEnrolDto request, BindingResult bindingResult,
                                @SessionAttribute("loginId") String loginId, RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            return "redirect:/question";
        }

        try {
            Member loginMember = memberService.getLoginMember(loginId);
            Question savedQuestion = questionService.save(request, loginMember);
            redirectAttributes.addAttribute("questionId", savedQuestion.getId());
            return "redirect:/questions/{questionId}";
        } catch (NoSuchElementException ex) {
            return "redirect:/home";
        }
    }

    @ResponseBody
    @PatchMapping("/questions")
    public String updateQuestion(@RequestBody UpdateQuestionVO vo) {
        RequestUpdateQuestionDto request = new RequestUpdateQuestionDto(vo.getQuestionId(), vo.getTitle(), vo.getContent());
        questionService.update(request);
        return "ok";
    }
}
