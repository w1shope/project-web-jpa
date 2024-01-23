package com.example.demo.service;

import com.example.demo.dto.RequestUpdateFileDto;
import com.example.demo.entity.View;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.ViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ViewService {

    private final ViewRepository viewRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public Long save(View view) {
        return viewRepository.save(view).getId();
    }

    public List<View> getAll() {
        return viewRepository.getAllViews().orElseThrow(NoSuchFieldError::new);
    }

    @Transactional
    public void increaseLikeCnt(View view) {
        viewRepository.changeStatusToPress(view.getId());
        boardRepository.increaseLikeCnt(view.getBoard().getId());
    }

    @Transactional
    public void cancelLikeCnt(View view) {
        viewRepository.changeStatusToCancel(view.getId());
        boardRepository.decreaseLikeCnt(view.getBoard().getId());
    }

}
