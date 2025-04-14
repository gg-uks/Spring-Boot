package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
@Entity //이 어노테이션을 걸어줘야 메인 어플리케이션이 실행될때 Book클래스가 어떤 클래스구나라는걸 보고 테이블을 생성해준다
@Data
public class Book { //Book(Object) --> ORM(Hibernate):JPA ---> book table 생성  오브젝트 릴레이션 매핑
    @Id //PK를 가르킨다
    @GeneratedValue(strategy = GenerationType.IDENTITY) //AUTO_INCREMENT 속성으로 붙음
    private Long id;//1 2 3 4

    @Column(length = 50,nullable = false,unique = true) //varchar(50) , notnull, 유니크속성거는뜻
    private String title;//COLUMN  default==varchar(255)

    private int price;

    private String author;  //authorName >카멜케이스다  실제디비에서는 author_name이렇게 만들어진다

    private int page;
}

//Hibernate:
//drop table if exists book
//Hibernate:
//create table book (
//        page integer not null,
//        price integer not null,
//        id bigint not null auto_increment,
//        title varchar(50) not null,
//author varchar(255),
//primary key (id)
//    ) engine=InnoDB
//Hibernate:
//alter table book
//add constraint UKg0286ag1dlt4473st1ugemd0m unique (title)



//Hibernate:
//select
//b1_0.id,
//b1_0.author,
//b1_0.page,
//b1_0.price,
//b1_0.title
//        from
//Book b1_0
//where
//b1_0.id=?     자동으로 제너레이션됨   아이디 값으로 가지고옴

