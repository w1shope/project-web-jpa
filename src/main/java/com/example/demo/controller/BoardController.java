package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Member;
import com.example.demo.entity.View;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.ViewRepository;
import com.example.demo.service.*;
import com.example.demo.vo.IncreaseLikeCntBoardVO;
import com.example.demo.vo.SearchBoardConditionVO;
import com.example.demo.vo.WriteCommentVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;
    private final ViewService viewService;
    private final ViewRepository viewRepository;
    private final MemberService memberService;
    private final BoardRepository boardRepository;
    private final FileService fileService;
    private final CommentService commentService;

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
        try {
            Board findBoard = boardService.findBoard(boardId); // 게시물 찾기
            boardService.increaseViewCnt(findBoard); // 조회수 증가
            List<ResponseCommentDto> comments = commentService.findCommentWithMemberAndBoard().stream()
                    .map(c -> new ResponseCommentDto(c.getMember().getNickName(),
                            c.getContent(), c.getCreatedDate(), c.getEditDate()))
                    .collect(Collectors.toList());
            ResponseViewBoardDto response = new ResponseViewBoardDto(findBoard.getId(), findBoard.getTitle()
                    , findBoard.getContent(), findBoard.getLikeCnt(), findBoard.getFile().getUploadFilename(), comments);
            model.addAttribute("board", response);
        } catch (NoSuchElementException ex) {
            Board findBoard = boardRepository.findById(boardId).get(); // 게시물 찾기
            boardService.increaseViewCnt(findBoard); // 조회수 증가
            List<ResponseCommentDto> comments = commentService.findCommentWithMemberAndBoard().stream()
                    .map(c -> new ResponseCommentDto(c.getMember().getNickName(),
                            c.getContent(), c.getCreatedDate(), c.getEditDate()))
                    .collect(Collectors.toList());
            ResponseViewBoardDto response = new ResponseViewBoardDto(findBoard.getId(), findBoard.getTitle()
                    , findBoard.getContent(), findBoard.getLikeCnt(), null, comments);
            model.addAttribute("board", response);
        } finally {
            return "/view";
        }
    }

    /**
     * 전체 게시물 조회
     */
    @GetMapping("/boards")
    public String getAllBoards(Model model) {
        List<ResponseBoardListDto> result = boardService.findAll().stream()
                .map(post -> new ResponseBoardListDto(post.getId(), post.getTitle(), post.getContent()
                        , post.getMember().getNickName(), post.getCreatedDate(), post.getViewCnt(),
                        post.getLikeCnt()))
                .collect(Collectors.toList());
        model.addAttribute("boards", result);
        return "/boardList";
    }

    /**
     * 게시물 등록
     */
    @PostMapping("/board")
    public String enrolBoard(@Valid @ModelAttribute("board") RequestEnrolBoardDto request, BindingResult bindingResult
            , Model model, @RequestParam("file") MultipartFile multipartFile) throws IOException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("enrolFailed", "등록 실패");
            return "/board";
        }
        Long savedId = boardService.save(request, multipartFile);
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

    /**
     * 특정 게시물 조회
     * 제목 기준
     */
    @ResponseBody
    @PostMapping("/boards/search")
    public List<ResponseSearchConditionDto> getSearchedBoards(@RequestBody SearchBoardConditionVO vo) {
        return boardService.getPostsBySearch(vo).stream()
                .map(post -> new ResponseSearchConditionDto(post.getId(), post.getTitle(), post.getContent()
                        , post.getMember().getNickName(), post.getCreatedDate()
                        , post.getViewCnt(), post.getLikeCnt()))
                .collect(Collectors.toList());
    }

    /**
     * 게시물 좋아요
     */
    @ResponseBody
    @PostMapping("/boards/like")
    public ResponseBoardLikeDto getBoardLikeCnt(@RequestBody IncreaseLikeCntBoardVO vo,
                                                @SessionAttribute("loginId") String loginId) {
        try {
            Board board = boardService.findBoard(vo.getBoardId());
            Member loginMember = memberService.getLoginMember(loginId);
            View findView = viewService.getAll().stream()
                    .filter(view -> view.getBoard().getId().equals(board.getId()))
                    .filter(view -> view.getMember().getId().equals(loginMember.getId()))
                    .findFirst()
                    .orElse(null);
            if (findView == null) {
                View createView = View.builder()
                        .board(board)
                        .member(loginMember)
                        .likeStatus(1)
                        .build();
                View savedView = viewRepository.save(createView);
                boardService.increaseLikeCnt(savedView.getBoard());
                return new ResponseBoardLikeDto(boardService.findBoard(savedView.getBoard().getId()).getLikeCnt());
            } else {
                if (findView.getLikeStatus() == 0) {
                    viewService.increaseLikeCnt(findView);
                } else {
                    viewService.cancelLikeCnt(findView);
                }
            }
            return new ResponseBoardLikeDto(boardService.findBoard(findView.getBoard().getId()).getLikeCnt());
        } catch (NoSuchElementException ex) {
            throw ex;
        }
    }

    @GetMapping("/boards/{boardId}/file")
    public ResponseEntity<Resource> downloadFile(@PathVariable("boardId") Long boardId) throws MalformedURLException {
        Board board = boardService.findBoard(boardId);
        String storedFilePath = board.getFile().getStoredFilePath();
        System.out.println("storedFilePath = " + storedFilePath);

        UrlResource resource = new UrlResource("file:" + storedFilePath);
        String encodeUploadFilename = UriUtils.encode(board.getFile().getUploadFilename(), StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodeUploadFilename + "\"";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }

    @GetMapping("/boards/{boardId}/update")
    public String editPage(@PathVariable("boardId") Long id, Model model) {
        try {
            Board existFileBoard = boardService.findBoard(id);
            RequestUpdateBoardDto request = new RequestUpdateBoardDto(existFileBoard.getId(), existFileBoard.getTitle()
                    , existFileBoard.getContent(), existFileBoard.getFile());
            model.addAttribute("board", request);
        } catch (NoSuchElementException ex) {
            Board notExistFileBoard = boardRepository.findById(id).get();
            RequestUpdateBoardDto request = new RequestUpdateBoardDto(notExistFileBoard.getId(), notExistFileBoard.getTitle()
                    , notExistFileBoard.getContent(), null);
            model.addAttribute("board", request);
        } finally {
            return "/updateBoard";
        }
    }

    @PatchMapping("/boards/{boardId}/update")
    public String updateBoard(RequestUpdateBoardDto request, RedirectAttributes redirectAttributes,
                              @RequestParam("uploadFile") MultipartFile file) throws IOException {
        boardService.updateBoard(request, file);
        redirectAttributes.addAttribute("boardId", request.getBoardId());
        return "redirect:/boards/{boardId}";
    }

    @ResponseBody
    @DeleteMapping("/boards/{boardId}")
    public String deleteBoard(@PathVariable("boardId") Long boardId,
                              @RequestBody RequestDeleteBoardDto request) {
        if (request.isDeleteCheck()) {
            try {
                Board findBoard = boardService.findBoard(boardId);
                if (findBoard != null) {
                    boardService.deleteBoard(boardId);
                    fileService.removeFile(findBoard.getFile().getId());
                }
            } catch (NoSuchElementException ex) {
                boardService.deleteBoard(boardId);
            }
        }
        return "ok";
    }

    @ResponseBody
    @PostMapping("/boards/comment")
    private String writeComment(@RequestBody WriteCommentVO vo,
                              @SessionAttribute("loginId") String loginId) {
        log.info("vo={}", vo);
        Member member = memberService.getLoginMember(loginId);
        Board board = boardRepository.findById(vo.getBoardId()).orElse(null);
        commentService.save(vo, board, member);
        return "ok";
    }
}










