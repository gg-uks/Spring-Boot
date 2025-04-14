package com.example.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {//Many

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 고객정보(FK)
    @ManyToOne
    @JoinColumn(name = "customer_id" , nullable = false)//Customer PK가르킴
     private Customer customer;//ToOne      장바구니항목은 한명의 고객이여러번 담을수있음

     // 책의정보(FK)
     @ManyToOne
     @JoinColumn(name = "book_id" , nullable = false) //Book PK가르킴
    private Book book;//ToOne          장바구니 항목에 1권의 책이 여러번 등록가능

    private int quantity; //  수량
    private Date cartDate; // 구매일자
}
/*
 create table Cart (
        quantity integer not null,
        book_id bigint not null,
        cartDate datetime(6),
        customer_id bigint not null,
        id bigint not null auto_increment,
        primary key (id)
    ) engine=InnoDB

       alter table Cart
       add constraint FKm5ku5ea0qccwca07ju2idkl8e
       foreign key (book_id)
       references Book (id)

       alter table Cart
       add constraint FKjh6lsweiw8ipasfagjwmmgi9u
       foreign key (customer_id)
       references Customer (id)

 */