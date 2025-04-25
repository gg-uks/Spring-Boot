package com.example.oauth2.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.Set;

@Entity
@Data
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, unique = true,nullable = false)
    private String username; //google_108176089712318726983  중복이 되면안되서
    private String password; // 임의값?(암호화)
    private String uname;  // name=홍길동
    private String email;  //email=aaaa12345@gmail.com

    //oAuth2에서 추가되는 부분
    private String provider; //google,naver,kakao
    private String providerId; //sub값 sub=108176089712318726983

    @CreationTimestamp
    private Timestamp createDate;  //생성날짜

    //권한 (USER,MANAGER,ADMIN) : Role
    @ManyToMany
    @JoinTable(
            name = "member_roles",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
}
