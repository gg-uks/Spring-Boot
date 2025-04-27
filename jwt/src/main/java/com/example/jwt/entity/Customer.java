package com.example.jwt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;

    //한명의 회원이 여러 개의 권한을 가질수있다 - N - Customer
    //                                         member_roles(관계테이블)
    //하나의 권한에는 여려명의 회원이 존재 한다 - N - Role

    private String roles; //권한 : USER , MANAGER , ADMIN "USER,MANAGER"  ,를 기준으로 짜르면됨 배열로 리턴하면된다
    public List<String> getRoleList(){
        if(this.roles.length() > 0){
            return Arrays.asList(roles.split(","));
        }
        return new ArrayList<>();
    }

}
