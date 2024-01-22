package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.Board;
import com.example.demo.service.BoardService;
import com.example.demo.service.ViewService;
import com.example.demo.vo.IncreaseLikeCntBoardVO;
import com.example.demo.vo.SearchBoardConditionVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;
    private final ViewService viewService;

    @GetMapping("/board")
    public String writePage(@ModelAttribute("board") RequestEnrolBoardDto request,
                            Model model, @SessionAttribute(name = "loginId", required = false) String loginId
            , RedirectAttributes redirectAttributes) {
        if (loginId == null) {
            redirectAttributes.addAttribute("writeFailed", "잘못된 요청입니다");
            return "redirect:/home";
        }
        model.addAttribute("loginId", loginId);
        return "/board";
    }

    /**
     * 게시물 상세 보기
     */
    @GetMapping("/boards/{id}")
    public String viewBoard(@PathVariable("id") Long boardId, Model model) {
        Board findBoard = boardService.findBoard(boardId); // 게시물 찾기
        boardService.increaseViewCnt(findBoard); // 조회수 증가
        ResponseViewBoardDto response = new ResponseViewBoardDto(findBoard.getId(), findBoard.getTitle()
                , findBoard.getContent(), findBoard.getView().getLikeCnt());
        model.addAttribute("board", response);
        return "/view";
    }

    /**
     * 전체 게시물 조회
     */
    @GetMapping("/boards")
    public String getAllBoards(Model model) {
        List<ResponseBoardListDto> result = boardService.findAll().stream()
                .map(post -> new ResponseBoardListDto(post.getId(), post.getTitle(), post.getContent()
                        , post.getMember().getNickName(), post.getCreatedDate(), post.getViewCnt(),
                        post.getView().getLikeCnt()))
                .collect(Collectors.toList());
        model.addAttribute("boards", result);
        return "/boardList";
    }

    /**
     * 게시물 등록
     */
    @PostMapping("/board")
    public String enrolBoard(@Valid @ModelAttribute("board") RequestEnrolBoardDto request, BindingResult bindingResult
            , Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("enrolFailed", "등록 실패");
            return "/board";
        }
        Long savedId = boardService.save(request);
        return "redirect:/boards/" + savedId;
    }


    /**
     * 양방향 연관관계 그대로 반환X
     *
     * @ResponseBody
     * @PostMapping("/boards/search") public List<Board> getSearchedBoards(@RequestBody BoardSearchConditionVO vo) {
     * return boardService.getPostsBySearch(vo);
     * }
     */

    @ResponseBody
    @PostMapping("/boards/search")
    public List<ResponseSearchConditionDto> getSearchedBoards(@RequestBody SearchBoardConditionVO vo) {
        return boardService.getPostsBySearch(vo).stream()
                .map(post -> new ResponseSearchConditionDto(post.getId(), post.getTitle(), post.getContent()
                        , post.getMember().getNickName(), post.getCreatedDate()
                        , post.getViewCnt(), post.getView().getLikeCnt()))
                .collect(Collectors.toList());
    }

    @ResponseBody
    @PostMapping("/boards/like")
    public ResponseBoardLikeDto getBoardLikeCnt(@RequestBody IncreaseLikeCntBoardVO vo) {
        Board board = boardService.findBoard(vo.getBoardId());
        viewService.increaseLikeCnt(board);
        return new ResponseBoardLikeDto(board.getView().getLikeCnt());
    }

}










