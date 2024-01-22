package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.Board;
import com.example.demo.entity.Member;
import com.example.demo.repository.MemberRepository;
import com.example.demo.vo.CheckJoinIdVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Long save(RequestJoinMemberDto request) {
        Member member = Member.builder()
                .loginId(request.getLoginId())
                .password(request.getPassword())
                .username(request.getNickName())
                .nickName(request.getUsername())
                .email(request.getEmail())
                .phone(request.getPhone())
                .createdDate(LocalDateTime.now())
                .build();
        return memberRepository.save(member).getId();
    }

    public Member login(RequestLoginMemberDto request) {
        return memberRepository.findMemberByLoginIdAndPassword(request.getLoginId(), request.getLoginPassword())
                .orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다"));
    }

    public Member getLoginMember(String loginId) {
        return memberRepository.findMemberByLoginId(loginId)
                .orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다"));
    }

    public Member findMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다"));
    }

    @Transactional
    public int updateEmail(String loginId, RequestUpdateMemberEmailDto request) {
        try {
            memberRepository.findMemberByLoginId(loginId)
                    .orElseThrow(() -> new NoSuchElementException("회원정보를 찾을 수 없습니다"));
            return memberRepository.updateEmail(loginId, request.getUpdateEmail());
        } catch (NoSuchElementException ex) {
            throw ex;
        }
    }

    @Transactional
    public int updatePhone(String loginId, RequestUpdateMemberPhoneDto request) {
        try {
            memberRepository.findMemberByLoginId(loginId)
                    .orElseThrow(() -> new NoSuchElementException("회원정보를 찾을 수 없습니다"));
            return memberRepository.updatePhone(request.getUpdatePhone(), loginId);
        } catch (NoSuchElementException ex) {
            throw ex;
        }
    }

    public Member findBoardWrittenByMe(String loginId) {
        try {
            memberRepository.findMemberByLoginId(loginId)
                    .orElseThrow(() -> new NoSuchElementException("회원정보를 찾을 수 없습니다1"));
            Member member = memberRepository.findBoardsWrittenByMe(loginId)
                    .orElseThrow(() -> new NoSuchElementException("회원정보를 찾을 수 없습니다2"));
            return member;
        } catch (NoSuchElementException ex) {
            throw ex;
        }
    }

    public int checkDuplicateJoinId(CheckJoinIdVO vo) {
        return memberRepository.joinIdAvailability(vo.getJoinId());
    }
}
