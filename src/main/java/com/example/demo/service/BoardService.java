package com.example.demo.service;

import com.example.demo.dto.GetBoardResponseDto;
import com.example.demo.dto.GetBoardsResponseDto;
import com.example.demo.dto.RequestEnrolBoardDto;
import com.example.demo.dto.RequestUpdateBoardDto;
import com.example.demo.dto.ResponseBoardLikeDto;
import com.example.demo.dto.SearchBoardResponseDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.File;
import com.example.demo.entity.Member;
import com.example.demo.entity.View;
import com.example.demo.file.FileStore;
import com.example.demo.file.UploadFile;
import com.example.demo.repository.BoardRepository;
import com.example.demo.vo.SearchBoardRequestDto;
import com.example.demo.vo.UpdateLikeCntRequestDto;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberService memberService;
    private final ViewService viewService;
    private final FileStore fileStore;
    private final FileService fileService;

    @Transactional
    public Long save(RequestEnrolBoardDto request, MultipartFile multipartFile) throws IOException {
        try {
            Member loginMember = memberService.getLoginMember(request.getLoginId());

            View view = View.builder()
                .member(loginMember)
                .build();
            viewService.save(view);

            UploadFile uploadedFile = fileStore.storeFile(multipartFile);

            if (uploadedFile != null) {
                File file = File.builder()
                    .uploadFilename(uploadedFile.getUploadFilename())
                    .storedFileName(uploadedFile.getStoredFileName())
                    .storedFilePath(fileStore.getStoredFullPath(uploadedFile.getStoredFileName()))
                    .build();
                fileService.save(file);

                Board board = Board.builder()
                    .title(request.getTitle())
                    .content(request.getContent())
                    .createdDate(LocalDateTime.now())
                    .editDate(LocalDateTime.now())
                    .viewCnt(0)
                    .likeCnt(0)
                    .member(loginMember)
                    .file(file)
                    .build();
                boardRepository.save(board);
                return board.getId();
            } else {

                Board board = Board.builder()
                    .title(request.getTitle())
                    .content(request.getContent())
                    .createdDate(LocalDateTime.now())
                    .editDate(LocalDateTime.now())
                    .viewCnt(0)
                    .likeCnt(0)
                    .member(loginMember)
                    .build();
                boardRepository.save(board);
                return board.getId();
            }

        } catch (NoSuchElementException ex) {
            throw ex;
        }
    }

    @Transactional
    public GetBoardResponseDto findBoard(Long boardId) {
        // 게시물 조회
        Board board = getBoard(boardId);
        // 조회수 증가
        board.increaseViewCnt();

        return new GetBoardResponseDto(board.getId(), board.getTitle(), board.getContent(),
            board.getFile() == null ? null : board.getFile().getId(),
            board.getFile() == null ? null : board.getFile().getUploadFilename(),
            board.getFile() == null ? null : board.getFile().getStoredFileName(),
            board.getLikeCnt(), board.getMember().getId());
    }

    @Transactional(readOnly = true)
    public Board findBoardById(Long boardId) {
        return getBoard(boardId);
    }

    private Board getBoard(Long boardId) {
        return boardRepository.findBoardWithFileByBoardId(boardId)
            .orElseThrow(() -> new NoSuchElementException("게시물을 읽어올 수 없습니다"));
    }

    public List<Board> getBoards() {
        return boardRepository.findBoardAll();
    }

    @Transactional(readOnly = true)
    public List<GetBoardsResponseDto> getBoards(Pageable pageable) {
        return boardRepository.findBoardAll(pageable).stream()
            .map(b -> new GetBoardsResponseDto(b.getId(), b.getTitle(),
                b.getContent(), b.getMember().getNickName(), b.getCreatedDate(), b.getViewCnt(),
                b.getLikeCnt())).toList();
    }

    public List<SearchBoardResponseDto> getBoardsByTitle(SearchBoardRequestDto vo) {
        return boardRepository.getPostsBySearch(vo.getCondition()).stream()
            .map(b -> new SearchBoardResponseDto(b.getId(), b.getTitle(), b.getContent(),
                b.getMember().getNickName(), b.getCreatedDate(), b.getViewCnt(), b.getLikeCnt()))
            .toList();
    }

    public List<Board> getAllPosts(String loginId) {
        return boardRepository.getAllPosts(loginId);
    }

    @Transactional
    public void updateBoard(RequestUpdateBoardDto request, MultipartFile multipartFile)
        throws IOException {

    }

    @Transactional
    public void deleteBoard(Long boardId) {
        Board board = getBoard(boardId);
        boardRepository.delete(board);
    }

    public List<Board> findBoardWithFile() {
        return boardRepository.findBoardWithFile();
    }

    public List<Board> findBoardWithFile(Pageable pageable) {
        return boardRepository.findBoardWithFile(pageable);
    }

    @Transactional
    public ResponseBoardLikeDto updateLikeCnt(UpdateLikeCntRequestDto request, String loginId) {

        // 게시물 조회
        Board board = findBoardById(request.getBoardId());

        // 로그인 여부 확인
        if (loginId == null) {
            throw new NoSuchElementException("로그인한 사용자만 좋아요를 누를 수 있습니다");
        }

        // 사용자 조회
        Member loginMember = memberService.getLoginMember(loginId);
        // View 조회
        View view = viewService.getView(board.getId(), loginMember.getId());

        // 사용자가 게시물에 좋아요를 누른 적이 없는 경우
        if (view == null) {
            // 좋아요 1개 증가
            board.increaseLikeCnt();
            // DB에 저장
            viewService.saveView(board.getId(), loginMember.getId());
        } else {
            // 좋아요 1개 감소
            board.decreaseLikeCnt();
            // DB 데이터 삭제
            viewService.deleteView(view.getId());
        }

        return new ResponseBoardLikeDto(board.getLikeCnt());
    }

}