package com.example.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {//One
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 일련번호
    private String title; // 제목
    private int price; // 가격
    private String author; // 저자
    private int page;  // 페이지

    // 책정보를 가져올께 리뷰정보(여러개)까지 함께 가져오기
    @OneToMany(mappedBy ="book", cascade = CascadeType.ALL) // 책:1  :  리뷰여러개
    private List<Review> reviews;//ToMany

    // 이첵에대한 이미지 정보를 함께 가져와야한다.
    @OneToMany(mappedBy ="book", cascade = CascadeType.ALL)// 책:1  :  여러개사진
    private List<BookImage> bookImages;

    @OneToMany(mappedBy ="book", cascade = CascadeType.ALL)// 책:1  :  장바구니에 여러개 책이 들어감
    private List<Cart> carts;

}
