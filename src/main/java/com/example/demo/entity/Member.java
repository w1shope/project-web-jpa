package com.example.demo.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"loginId", "password", "username", "nickName", "email", "phone"})
public class Member extends BaseEntity {

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

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member",
        cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Board> boards = new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member",
        cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<View> views = new ArrayList<>();
}



