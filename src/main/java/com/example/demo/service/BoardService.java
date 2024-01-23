package com.example.demo.service;

import com.example.demo.dto.RequestEnrolBoardDto;
import com.example.demo.dto.RequestUpdateBoardDto;
import com.example.demo.dto.RequestUpdateFileDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.File;
import com.example.demo.entity.Member;
import com.example.demo.entity.View;
import com.example.demo.file.FileStore;
import com.example.demo.file.UploadFile;
import com.example.demo.repository.BoardRepository;
import com.example.demo.vo.SearchBoardConditionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
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
                        .storedFilePath(uploadedFile.getStoredFilePath())
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

    public Board findBoard(Long id) {
        return boardRepository.findBoardWithFileByBoardId(id)
                .orElseThrow(() -> new NoSuchElementException("게시물을 읽어올 수 없습니다"));
    }

    public List<Board> findAll() {
        return boardRepository.findBoardAll();
    }

    public List<Board> getPostsBySearch(SearchBoardConditionVO vo) {
        return boardRepository.getPostsBySearch(vo.getCondition());
    }

    public List<Board> getAllPosts(String loginId) {
        return boardRepository.getAllPosts(loginId);
    }

    @Transactional
    public void increaseViewCnt(Board board) {
        boardRepository.increaseViewCnt(board.getId());
    }

    @Transactional
    public void increaseLikeCnt(Board board) {
        boardRepository.increaseLikeCnt(board.getId());
    }

    @Transactional
    public void decreaseLikeCnt(Board board) {
        boardRepository.decreaseLikeCnt(board.getId());
    }

    @Transactional
    public void updateBoard(RequestUpdateBoardDto request, MultipartFile multipartFile) throws IOException {
        boardRepository.update(request.getBoardId(), request.getTitle(), request.getContent());

        UploadFile uploadedFile = fileStore.storeFile(multipartFile);
        if (uploadedFile != null) {
            File file = File.builder()
                    .uploadFilename(uploadedFile.getUploadFilename())
                    .storedFilePath(uploadedFile.getStoredFilePath())
                    .build();
            Board findBoard = boardRepository.findBoardWithFileByBoardId(request.getBoardId()).orElse(null);
            if (findBoard != null) {
                fileService.changeFile(new RequestUpdateFileDto(
                        boardRepository.findBoardWithFileByBoardId(request.getBoardId()).get().getFile().getId(),
                        file.getUploadFilename(),
                        file.getStoredFilePath()
                ));
            } else {
                fileService.save(file);
                Board board = boardRepository.findById(request.getBoardId()).orElse(null);
                board.changeFile(file);
            }
        } else {
            Board findBoard = boardRepository.findBoardWithFileByBoardId(request.getBoardId()).orElse(null);
            if (findBoard != null) {
                boardRepository.removeFile(request.getBoardId());
                fileService.removeFile(findBoard.getFile().getId());
            }
        }

    }

    @Transactional
    public void deleteBoard(Long id) {
        boardRepository.deleteBoardByBoardId(id);
    }

}
