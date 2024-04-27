package com.example.demo.controller;

import com.example.demo.dto.GetBoardResponseDto;
import com.example.demo.dto.GetBoardsResponseDto;
import com.example.demo.dto.GetCommentResponseDto;
import com.example.demo.dto.RequestEnrolBoardDto;
import com.example.demo.dto.RequestUpdateBoardDto;
import com.example.demo.dto.ResponseBoardLikeDto;
import com.example.demo.dto.ResponseCommentLikeCntDto;
import com.example.demo.dto.SearchBoardResponseDto;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.ViewCommentRepository;
import com.example.demo.repository.ViewRepository;
import com.example.demo.service.BoardService;
import com.example.demo.service.CommentService;
import com.example.demo.service.FileService;
import com.example.demo.service.MemberService;
import com.example.demo.service.ViewCommentService;
import com.example.demo.service.ViewService;
import com.example.demo.vo.DeleteCommentRequestDto;
import com.example.demo.vo.EditCommentRequestDto;
import com.example.demo.vo.SearchBoardRequestDto;
import com.example.demo.vo.UpdateCommentLikeCntRequestDto;
import com.example.demo.vo.UpdateLikeCntRequestDto;
import com.example.demo.vo.WriteCommentRequestDto;
import jakarta.validation.Valid;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

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
    private final ViewCommentService viewCommentService;
    private final ViewCommentRepository viewCommentRepository;
    private final CommentRepository commentRepository;

    /**
     * 게시물 작성 페이지
     */
    @GetMapping("/boards/enroll")
    public String writePage(@ModelAttribute("board") RequestEnrolBoardDto request,
        Model model, @SessionAttribute(name = "loginId", required = false) String loginId
        , RedirectAttributes redirectAttributes) {

        if (loginId == null) {
            redirectAttributes.addAttribute("requireLogin", "로그인 후 이용 가능합니다");
            return "redirect:/home";
        }

        model.addAttribute("loginId", loginId);

        return "/board";
    }

    /**
     * 게시물 상세 보기
     */
    @GetMapping("/boards/{boardId}")
    public String viewBoard(@PathVariable("boardId") Long boardId, Model model,
        @SessionAttribute(value = "loginId", required = false) String loginId) {

        // 게시물 조회
        GetBoardResponseDto findBoard = boardService.findBoard(boardId);
        model.addAttribute("board", findBoard);

        // 댓글 조회
        List<GetCommentResponseDto> findComments = commentService.findComments(boardId);
        model.addAttribute("comments", findComments);

        model.addAttribute("loginMember",
            loginId == null ? null : memberService.getLoginMember(loginId));

        return "/view";
    }

    /**
     * 전체 게시물 조회
     */
    @GetMapping("/boards")
    public String getAllBoards(Model model, @PageableDefault(size = 10) Pageable pageable) {

        // 게시물 전체 조회
        List<GetBoardsResponseDto> boards = boardService.getBoards(pageable);
        model.addAttribute("boards", boards);

        // 페이지당 조회할 게시물 개수
        model.addAttribute("pages",
            IntStream.rangeClosed(0, boardService.getBoards().size() / (pageable.getPageSize() + 1))
                .boxed().collect(Collectors.toList()));

        return "/boardList";
    }

    @GetMapping("/boards/{boardId}/file")
    public ResponseEntity<Resource> downloadFile(@PathVariable("boardId") Long boardId)
        throws MalformedURLException {
        GetBoardResponseDto board = boardService.findBoard(boardId);
        String storedFilePath = board.getStoredFileName();
        UrlResource resource = new UrlResource("file:" + storedFilePath);
        String encodeUploadFilename = UriUtils.encode(board.getUploadFileName(),
            StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodeUploadFilename + "\"";
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
            .body(resource);
    }

    /**
     * 게시물 등록
     */
    @PostMapping("/boards/enroll")
    public String writeBoard(@Valid @ModelAttribute("board") RequestEnrolBoardDto request,
        BindingResult bindingResult
        , Model model, @RequestParam("file") MultipartFile multipartFile) throws IOException {

        if (bindingResult.hasErrors()) {
            model.addAttribute("enrolFailed", "등록 실패");
            return "/board";
        }

        Long savedId = boardService.save(request, multipartFile);
        return "redirect:/boards/" + savedId;
    }

    /**
     * 제목을 기준으로 게시물 필터링
     */
    @ResponseBody
    @GetMapping("/boards/search")
    public List<SearchBoardResponseDto> getSearchedBoards(
        @RequestBody SearchBoardRequestDto request) {

        return boardService.getBoardsByTitle(request);
    }

    /**
     * 게시물 좋아요 및 취소
     */
    @ResponseBody
    @PostMapping("/boards/like")
    public ResponseBoardLikeDto getBoardLikeCnt(@RequestBody UpdateLikeCntRequestDto request,
        @SessionAttribute("loginId") String loginId) {

        return boardService.updateLikeCnt(request, loginId);
    }

    /**
     * 게시물 수정 페이지
     */
    @GetMapping("/boards/{boardId}/update")
    public String editBoardPage(@PathVariable("boardId") Long id, Model model) {

        // 게시물 조회
        GetBoardResponseDto board = boardService.findBoard(id);
        model.addAttribute("board", board);

        return "/updateBoard";
    }

    /**
     * 게시물 수정
     */
    @PatchMapping("/boards/{boardId}/update")
    public String updateBoard(RequestUpdateBoardDto request, RedirectAttributes redirectAttributes,
        @RequestParam("uploadFile") MultipartFile file) throws IOException {

        boardService.updateBoard(request, file);
        redirectAttributes.addAttribute("boardId", request.getBoardId());

        return "redirect:/boards/{boardId}";
    }

    /**
     * 게시물 삭제
     */
    @ResponseBody
    @DeleteMapping("/boards/{boardId}")
    public String deleteBoard(@PathVariable("boardId") Long boardId) {

        boardService.deleteBoard(boardId);
        return "ok";
    }

    /**
     * 댓글 작성
     */
    @ResponseBody
    @PostMapping("/boards/comment")
    private String writeComment(@RequestBody WriteCommentRequestDto request,
        @SessionAttribute("loginId") String loginId) {

        commentService.writeComment(request, loginId);
        return "ok";
    }

    /**
     * 댓글 삭제
     */
    @ResponseBody
    @DeleteMapping("/boards/comment")
    public String deleteComment(@RequestBody DeleteCommentRequestDto request,
        @SessionAttribute("loginId") String loginId) {

        commentService.deleteComment(request, loginId);
        return "ok";
    }

    /**
     * 댓글 수정
     */
    @ResponseBody
    @PatchMapping("/boards/comment")
    public String updateComment(@RequestBody EditCommentRequestDto request,
        @SessionAttribute("loginId") String loginId) {

        commentService.updateComment(request, loginId);
        return "ok";
    }

    /**
     * 댓글 좋아요 및 취소
     */
    @ResponseBody
    @PostMapping("/comments/like")
    public ResponseCommentLikeCntDto updateCommentLikeCnt(
        @RequestBody UpdateCommentLikeCntRequestDto request,
        @SessionAttribute("loginId") String loginId) {

        long likeCnt = commentService.updateLikeCnt(request, loginId);
        return new ResponseCommentLikeCntDto(likeCnt);
    }
}










