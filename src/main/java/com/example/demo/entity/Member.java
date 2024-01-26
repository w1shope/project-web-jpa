package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"loginId", "password", "username", "nickName", "email", "phone"})
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_id")
    private Long id;

    private String loginId;
    private String password;
    private String username;
    private String nickName;
    private String email;
    private String phone;
    private LocalDateTime createdDate;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Board> boards = new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<View> views = new ArrayList<>();
}



