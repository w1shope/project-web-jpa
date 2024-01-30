package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.ViewCommentRepository;
import com.example.demo.repository.ViewRepository;
import com.example.demo.service.*;
import com.example.demo.vo.*;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    @GetMapping("/boards/{id}")
    public String viewBoard(@PathVariable("id") Long boardId, Model model,
                            @SessionAttribute(value = "loginId", required = false) String loginId) {
        try {
            Board findBoard = boardService.findBoard(boardId); // 게시물 찾기
            model.addAttribute("board", new ResponseFindBoardDto(findBoard.getId(),
                    findBoard.getTitle(), findBoard.getContent(), findBoard.getLikeCnt(), findBoard.getFile().getUploadFilename(),
                    findBoard.getMember()));
            // 댓글 찾기
            List<ResponseCommentDto> findComments = commentService.findCommentWithMemberAndBoard().stream()
                    .map(comment -> new ResponseCommentDto(comment.getMember().getNickName(),
                            comment.getBoard().getContent(), comment.getCreatedDate(),
                            comment.getEditDate(), comment.getLikeCnt()))
                    .collect(Collectors.toList());
            model.addAttribute("comments", findComments);
            model.addAttribute("loginMember", loginId == null ? null : memberService.getLoginMember(loginId));
            // 조회수 증가
            boardService.increaseViewCnt(findBoard);
            return "/view";
        } catch(NoSuchElementException ex) {
            throw ex;
        }
    }

    /**
     * 전체 게시물 조회
     */
    @GetMapping("/boards")
    public String getAllBoards(Model model, @PageableDefault(size = 10) Pageable pageable) {
        List<ResponseBoardListDto> result = boardService.findAll(pageable).stream()
                .map(post -> new ResponseBoardListDto(post.getId(), post.getTitle(), post.getContent()
                        , post.getMember().getNickName(), post.getCreatedDate(), post.getViewCnt(),
                        post.getLikeCnt()))
                .collect(Collectors.toList());
        model.addAttribute("boards", result);
        model.addAttribute("pages", IntStream.rangeClosed(0, boardService.findAll().size() / (pageable.getPageSize() + 1)).boxed().collect(Collectors.toList()));
        return "/boardList";
    }

    /**
     * 게시물 등록
     */
    @PostMapping("/boards/enroll")
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
            Board board = boardRepository.findById(vo.getBoardId()).orElse(null);
            if(loginId == null) {
                throw new NoSuchElementException("로그인한 사용자만 좋아요를 누를 수 있습니다");
            }
            Member loginMember = memberService.getLoginMember(loginId);
            View findView = viewService.getAll().stream()
                    .filter(view -> view.getBoard().getId().equals(board.getId()))
                    .filter(view -> view.getMember().getId().equals(loginMember.getId()))
                    .findFirst()
                    .orElse(null);
            // 게시물의 좋아요가 0개일 때
            if (findView == null) {
                View createView = View.builder()
                        .board(board)
                        .member(loginMember)
                        .likeStatus(1)
                        .build();
                View savedView = viewRepository.save(createView);
                boardService.increaseLikeCnt(savedView.getBoard());
                View view = getViewWithBoard(vo);
                return new ResponseBoardLikeDto(view.getBoard().getLikeCnt());
            }
            // 게시물의 좋아요가 1개 이상일 때
            else {
                if (findView.getLikeStatus() == 0) {
                    viewService.increaseLikeCnt(findView);
                } else {
                    viewService.cancelLikeCnt(findView);
                }
            }
            View view = getViewWithBoard(vo);
            return new ResponseBoardLikeDto(view.getBoard().getLikeCnt());
        } catch (NoSuchElementException ex) {
            throw ex;
        }
    }

    private View getViewWithBoard(IncreaseLikeCntBoardVO vo) {
        View view = viewService.findViewWithBoard().stream()
                .filter(v -> v.getBoard().getId().equals(vo.getBoardId()))
                .findFirst()
                .orElse(null);
        return view;
    }

    @GetMapping("/boards/{boardId}/file")
    public ResponseEntity<Resource> downloadFile(@PathVariable("boardId") Long boardId) throws MalformedURLException {
        Board board = boardService.findBoard(boardId);
        String storedFilePath = board.getFile().getStoredFilePath();
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
        Member member = memberService.getLoginMember(loginId);
        Board board = boardRepository.findById(vo.getBoardId()).orElse(null);
        commentService.save(vo, board, member);
        return "ok";
    }

    @ResponseBody
    @DeleteMapping("/boards/comment")
    public String deleteComment(@RequestBody DeleteCommentVO vo,
                                @SessionAttribute("loginId") String loginId) {
        Comment comment = commentService.findCommentWithMemberAndBoard().stream()
                .filter(c -> c.getContent().equals(vo.getCommentContent()))
                .filter(c -> c.getCreatedDate().equals(vo.getCommentCreatedDate()))
                .filter(c -> c.getBoard().getId().equals(vo.getBoardId()))
                .filter(c -> c.getMember().getLoginId().equals(loginId))
                .findFirst()
                .orElse(null);
        // 해당 comment_id를 가지는 viewcomment가 있으면 먼저 제거후 comment 제거
        ViewComment viewComment = viewCommentRepository.findByViewCommentWithCommentByCommentId(comment.getId())
                .orElse(null);
        if (viewComment != null) {
            viewCommentRepository.deleteById(viewComment.getId());
        }
        commentService.deleteComment(comment);
        return "ok";
    }

    /**
     * 댓글 작성자 loginId, 게시물 id, 댓글 id
     * 댓글 수정 날짜 변경
     *
     * @return
     */
    @ResponseBody
    @PatchMapping("/boards/comment")
    public String editComment(@RequestBody EditCommentVO vo,
                              @SessionAttribute("loginId") String loginId) {
        try {
            Member loginMember = memberService.getLoginMember(loginId);
            Comment comment = commentService.findCommentWithMemberAndBoard().stream()
                    .filter(c -> c.getCreatedDate().equals(vo.getCommentCreatedDate()))
                    .filter(c -> c.getBoard().getId().equals(vo.getBoardId()))
                    .filter(c -> c.getMember().getLoginId().equals(loginMember.getLoginId()))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new);
            commentService.updateComment(comment, vo.getNewContent());
            return "ok";
        } catch (NoSuchElementException ex) {
            log.info("error={}", ex);
            throw ex;
        }
    }

    @ResponseBody
    @PostMapping("/comments/like")
    public ResponseCommentLikeCntDto updateCommentLikeCnt(@RequestBody UpdateCommentLikeCntVO vo,
                                                          @SessionAttribute("loginId") String loginId) {
        Member loginMember = memberService.getLoginMember(loginId);
        Comment findComment = commentService.findCommentWithMemberAndBoardForLikeCnt
                        (vo.getCommentWriter(), vo.getCommentContent(), vo.getCommentCreatedDate())
                .orElse(null);
        ViewComment findViewComment = viewCommentService.findViewCommentWithMemberAndComment(loginMember, findComment)
                .orElse(null);
        if (findViewComment == null) {
            ViewComment createViewComment = ViewComment.builder()
                    .likeStatus(0)
                    .comment(findComment)
                    .member(loginMember)
                    .build();
            viewCommentRepository.save(createViewComment);
            viewCommentService.changeLikeState(createViewComment); // 댓글 좋아요 상태 변경, 0 -> 1
            commentService.increaseLikeCnt(createViewComment.getComment()); // 댓글 좋아요 개수 1 감소
        } else {
            // 좋아요를 누른 상태
            if (findViewComment.getLikeStatus() == 1) {
                commentService.decreaseLikeCnt(findViewComment.getComment()); // 댓글 좋아요 개수 1 감소
                viewCommentService.changeLikeState(findViewComment); // 좋아요 상태 변경, 1 -> 0
            }
            // 좋아요 누르지 않은 상태
            else {
                commentService.increaseLikeCnt(findViewComment.getComment()); // 댓글 좋아요 개수 1 증가
                viewCommentService.changeLikeState(findViewComment); // 좋아요 상태 변경, 0 -> 1
            }
        }
        return new ResponseCommentLikeCntDto(findComment.getLikeCnt());
    }
}










