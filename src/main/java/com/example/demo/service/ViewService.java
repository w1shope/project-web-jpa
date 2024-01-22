package com.example.demo.service;

import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import com.example.demo.entity.View;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.ViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

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
}
