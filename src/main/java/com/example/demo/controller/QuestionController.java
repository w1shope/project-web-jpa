package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.Answer;
import com.example.demo.entity.Member;
import com.example.demo.entity.Question;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.service.AnswerService;
import com.example.demo.service.MemberService;
import com.example.demo.service.QuestionService;
import com.example.demo.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@Slf4j
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final QuestionRepository questionRepository;
    private final MemberService memberService;
    private final AnswerService answerService;

    /**
     * 전체 게시물 가져오기
     */
    @GetMapping("/questions")
    public String getAllQuestion(Model model, @PageableDefault(size = 10) Pageable pageable) {
        model.addAttribute("questions", questionService.getAllWithMember(pageable));
        model.addAttribute("pages", IntStream.rangeClosed(0, questionRepository.findAll().size() / (pageable.getPageSize() + 1)).boxed().collect(Collectors.toList()));
        return "/questionList";
    }

    /**
     * 특정 게시물 가져오기
     */
    @GetMapping("/questions/{id}")
    private String getQuestion(@PathVariable("id") Long questionId, Model model,
                               @SessionAttribute(value = "loginId", required = false) String loginId) {
        try {
            Question findQuestion = questionService.findQuestionWithMemberById(questionId)
                    .orElseThrow(() -> new NoSuchElementException("게시물을 읽어올 수 잆습니다."));
            model.addAttribute("question", new ResponseQuestionDto(
                    findQuestion.getId(), findQuestion.getTitle(), findQuestion.getContent(), findQuestion.getMember().getUsername(),
                    findQuestion.getCreatedDate(), findQuestion.getQuestionState()
            ));
            List<ResponseAnswerDto> answers = answerService.findAnswerWithQuestionAll(questionId)
                    .orElse(null).stream()
                    .map(answer -> new ResponseAnswerDto(answer.getMember().getNickName(),
                            answer.getContent(), answer.getCreatedDate()))
                    .collect(Collectors.toList());
            model.addAttribute("answers", answers == null ? null : answers);
            model.addAttribute("loginMember", memberService.getLoginMember(loginId));
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
    public String enrolQuestionPage(@ModelAttribute("question")RequestQuestionEnrolDto request,
                                    @SessionAttribute(value = "loginId", required = false) String loginId,
                                    RedirectAttributes redirectAttributes) {
        try {
            if(loginId == null) {
                redirectAttributes.addAttribute("requireLogin", "로그인 후 이용 가능합니다");
                return "redirect:/home";
            }
            return "/question";
        } catch (NoSuchElementException ex) {
            return "redirect:/home";
        }
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

    /**
     * 질문 답변 상태 변경
     */
    @ResponseBody
    @PatchMapping("/questions/state")
    public String changeState(@RequestBody UpdateQuestionStateVO vo) {
        log.info("vo={},{}", vo.getQuestionId(), vo.getState());
        RequestUpdateQuestionStateDto request = new RequestUpdateQuestionStateDto(vo.getQuestionId(), vo.getState());
        questionService.updateState(request);
        return "ok";
    }

    @ResponseBody
    @PostMapping("/answers/enroll")
    public String enrollAnswer(@RequestBody EnrollAnswerVO vo,
                               @SessionAttribute(value = "loginId", required = false) String loginId) {
        Member loginMember = memberService.getLoginMember(loginId);
        Question findQuestion = questionRepository.findById(vo.getQuestionId()).orElse(null);
        answerService.enroll(loginMember, findQuestion, vo.getAnswerContent());
        return "ok";
    }

    @ResponseBody
    @PatchMapping("/answers")
    public String editAnswer(@RequestBody UpdateAnswerVO vo) {
        try {
            Answer findAnswer = answerService.findAnswer(vo.getQuestionId(), vo.getAnswerWriter(), vo.getAnswerCreatedDate())
                    .orElseThrow(() -> new NoSuchElementException("질문에 대한 답변을 찾을 수 없습니다."));
            answerService.updateContent(findAnswer, vo.getUpdateContent());
            return "ok";
        } catch(NoSuchElementException ex) {
            throw ex;
        }
    }

    @ResponseBody
    @DeleteMapping("/answers")
    public String deleteAnswer(@RequestBody DeleteAnswerVO vo) {
        try {
            Answer findAnswer = answerService.findAnswer(vo.getQuestionId(), vo.getAnswerWriter(), vo.getAnswerCreatedDate())
                    .orElseThrow(() -> new NoSuchElementException("질문에 대한 답변을 찾을 수 없습니다."));
            answerService.deleteAnswer(findAnswer);
            return "ok";
        } catch(NoSuchElementException ex) {
            throw ex;
        }
    }
}
