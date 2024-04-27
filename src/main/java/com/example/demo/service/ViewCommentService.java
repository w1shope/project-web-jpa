package com.example.demo.service;

import com.example.demo.repository.ViewCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ViewCommentService {

    private final ViewCommentRepository viewCommentRepository;
}
