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
public class Cart { //장바구니 항목
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //일련번호
    //고객 정보를(FK)
    @ManyToOne// 한명의 고객은 여러 장바구니 항목(Cart) 을 가질 수 있음.
    @JoinColumn(name = "customer_id")
    private Customer customer;

    //어떤책을 샀는지?책의정보(FK)
    //하나의 책이 여러사람 장바구니에 담긴다
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    private int quantity; //구매수량
    private Date cartDate;//구매일자

}

//Hibernate:
//alter table Cart
//add constraint FKm5ku5ea0qccwca07ju2idkl8e
//foreign key (book_id)
//references Book (id)

//Hibernate:
//alter table Cart
//add constraint FKjh6lsweiw8ipasfagjwmmgi9u
//foreign key (customer_id)
//references Customer (id)

