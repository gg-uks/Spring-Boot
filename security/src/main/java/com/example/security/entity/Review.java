package com.example.security.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //이리뷰를 누가 작성 했는가?
    // N(Review): 1(Customer)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id",nullable = false)//FK를 만들겠다는것이다  Customer에 있는 필드명 참고해서 어노테이션 id가 붙은것을 보고 외래키 생성
    private Customer customer; //오브젝트는 컬럼으로 만들어지지 않는다(FK)
    //양방향 연관관계란?
    //부모 → 자식, 자식 → 부모
    //양쪽에서 서로 참조가 가능한 관계!
    //부모에서 자식으로 설정하는 것이 일반적이고 더 자연스러운 설계입니다. 부모가 자식들의 목록을 가지고 관리하는 방식이기 때문입니다.
    //하지만 자식에서 부모로 접근하는 것도 가능합니다. 이 경우는 자식 객체가 외래키를 통해 부모 객체를 참조하고 있기 때문입니다.
    //따라서 원칙적으로는 부모에서 자식으로 설정하고, 자식에서는 부모를 참조하는 방식으로 설계를 하는 것이 좋다.

    //리뷰입장에서 어떤책의 리뷰가 작성이 되었는가
    // N(Review): 1(Book)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id",nullable = false)
    private Book book; //오브젝트는 컬럼으로 만들어지지 않는다(FK)

    private int cost; //평점
    private String content; //내용
    private Date createdAt;
}

/*
Hibernate:
    create table Review (
        cost integer not null,
        book_id bigint not null,
        createdAt datetime(6),
        customer_id bigint not null,
        id bigint not null auto_increment,
        content varchar(255),
        primary key (id)
    ) engine=InnoDB

Hibernate:
    alter table Review
       add constraint FK8d76td2o7d2d8foei85l93b9w
       foreign key (book_id)
       references Book (id)
Hibernate:
    alter table Review
       add constraint FKl3n8g20ecmr15g83y0g3evv2q
       foreign key (customer_id)
       references Customer (id)

*
*
* */
