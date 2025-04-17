package com.example.security.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//일련번호
    private String title;//책제목
    private int price; //책 가격
    private String author; //책 저자
    private int page; //책 페이지수

    //책정보를 가져올때 리뷰정보까지 함께 가져오기
    @OneToMany(mappedBy = "book" ,cascade = CascadeType.ALL)
    private List<Review> reviews;

    //이책에대한 이미지정보를 함께 가져와야한다.
    @OneToMany(mappedBy = "book",cascade = CascadeType.ALL)
    private List<BookImage> bookImages;

    //한 권의 책이 여러사람 장바구니에 담길 수 있음
    //현실 모델링
    //하나의 장바구니(Cart) 에는 여러 개의 책(Book) 이 담길 수 있음 → Cart 1 : N Book
    //하나의 책(Book) 도 여러 사용자의 장바구니에 들어갈 수 있음 → Book 1 : N Cart
    @OneToMany(mappedBy = "book",cascade = CascadeType.ALL)
    private List<Cart> carts;
}

//mappedBy = "book"
//반대쪽 테이블(Review, BookImage, Cart) 클래스에 있는 book이라는 필드가 외래키 주인이라는 뜻입니다.
//
//외래키는 항상 N 쪽에 존재하며, 이 Book 클래스는 관계의 **주인이 아님(읽기 전용)**입니다.
//
//🔸 cascade = CascadeType.ALL
//책(Book)을 삭제하면, 연결된 리뷰, 이미지, 장바구니도 자동 삭제됨 (전파)