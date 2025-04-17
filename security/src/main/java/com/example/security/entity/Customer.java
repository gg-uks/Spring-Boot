package com.example.security.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

//JPA Provider (Hibernate)
// 프로퍼티에 설정된 ddl-auto 와 이 클래스에 선언된 어노테이션을 보고 데이터 베이스의 테이블을 생성또는 업데이트함
//엔티티 매니저 JPA의 핵심 API로 런타임에 영속성 컨텍스트를 관리하면서 엔티티객체의  CRUD작업 을 수행
//Query DSL 은 JPQL 이나 SQL을 작성할때 타입 안전성 과 코드 자동 완성등을 지원하는 DSL(도메인 특정언어) 도구
// 내부적으로 JPAQueryFactory 같은 클래스를 통해 EntityManager 를 사용하여 쿼리 실행을 위임해주며 코드상에서 쿼리를 보다 편리하게 작성할 수 있도록해줌

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {  //고객(object) -> ORM -> TABLE : Review(리뷰)
    //           고객 : 1(PK, 부모)       리뷰 : N(FK ,자식)
    //         |--------------연관 관계----------------|(주인)
    //                                           외래키 필드가 자식에서 여기에 만들어짐
    //    부모는 자신이 작성한 리뷰목록(여러개)을 가지고 있어야 한다
    //    List<Review>
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Autoincrement
    private Long id ; //테이블의 일련번호 를 줘야 관리하기 편함

    @Column(name = "username",nullable = false, unique = true)    //컬럼 설정할수가있다 컬럼이름 , 널값허용여부  , 유니크허용여부
    private String username;  //고객의 아이디
    private String password; // 고객 비밀번호(암호화)
    private String customerName;//고객 이름  -> customer_name?   x  customerName
    private int age;  //나이
    private String rating;  //등급
    private String occupation;  //직업
    private String email;   //이메일

    @Column(columnDefinition = "int default 0")   //테이블이 만들어짐과 동시에 디폴트값으로 0으로 설정
    private int reserves;      //적립금

    //JPA에게 나는 연관관계의 주인이 아니다 (FK x)
    //mappedBy = "customer": Review 엔티티 안의 customer 필드가 외래 키 주인이다! (즉 Review가 주인)
    //1(Customer):N(Review)
    //CascadeType.ALL 고객이 삭제가 되면 연결된 리뷰 정보도 함께 삭제 되어야 한다
    //fetch=FetchType.EAGER : 즉시로딩 >자동으로 외래키가 있는 테이블을 참고해 외부 조인을 처리 (그해당 오브젝트 엔티티를 포함해 외래키있는 오브젝트와 같이 자동으로 조인되서 쓰고싶을때 사용) : 연관되어있는 클래스가 객체에있는 값들을 조인해서 가져온다  - 메모리를 비효율적 사용가능성
    //fetch=FetchType.LAZY :  지연로딩 > 외부조인이 자동으로되지않아 실제로 데이터를 가져와서 get키워드로 해야함(그해당 오브젝트 엔티티만 가지고오고 싶을때 사용)  : 지연로딩 : 연관되어있는 클래스가 객체에있는 값들을 가져오지 않음 - 메모리 절약  , 참조순환이 발생(N + 1)[데이터 베이스참조가 계속 발생] : 100명의 리뷰정보가져올때 반복문을 써서 계속 첫번째 디비접근 두번째도 계속 DB Access를 계속함
    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL ,fetch=FetchType.LAZY)
    private List<Review> reviews; //오브젝트는 컬럼 x ()   List<Review>니까 리뷰엔티티참고후 1대 N관계여서 한고객이 여러개 리뷰남길수 있음

    @OneToMany(mappedBy = "customer" ,cascade = CascadeType.ALL)
    private List<Cart> carts;

    //한명의 회원은 여러개의 권한(중복-Set)을 가지고있다
    //이 코드는 "내가 Member 클래스 안에서 roles를 선언하고 있으니까,
    //현재 엔티티는 Member다!" 라고 판단함.
    //여기서 @ManyToMany가 선언되는 순간:
    //현재 클래스는 Member
    //관계는 Set<Role>
    //그래서 joinColumns는 Member 기준, inverseJoinColumns는 Role 기준
    //JPA는 기본적으로 다대다(M:N) 관계를 "복합 기본키가 있는 조인 테이블"로 처리한다는 뜻  만약 중간테이블에서 컬럼을 추가하고 싶으면 OneToMany ManyToOne로 설정해야함
    //@JoinTable은 관계의 양쪽 중 한쪽에서만 설정해야 하며, mappedBy는 반대쪽에서 설정해야 함
    //결론적으로, 중간 테이블 설정은 다대다 관계의 어느 클래스에서 설정하든 상관없다. 다만, 관계의 주인이 되어야 하는 클래스에서 설정을 해야 하고, 다른 클래스에서는 이를 참조하는 방식으로 관계를 정의해야 함
    @ManyToMany//M:N(다:다) 별도의 비지니스 테이블이 필요함
    @JoinTable(
     name = "member_roles",      //고객 아이디와 롤의 아이디를 외래키로 테이블을 만들어준다 (중간테이블)
     joinColumns = @JoinColumn(name = "customer_id"),//현재 엔티티(Member)의 외래 키
     inverseJoinColumns = @JoinColumn(name = "role_id")//연결되는 엔티티(Role)의 외래 키 이름 지정
    )
    private Set<Role> roles; //중복이 안되는 리스트
//   컬렉션 프레임워크는 그 클래스를 참조하는 객체 필드를 만든다 그래서 조인할때도  Set<Role> roles; 여기에 저장된 것으로 조인을 해서 데이터를 가져온다
//    즉시로딩 (EAGER)	엔티티를 조회할 때 연관된 엔티티도 함께 즉시 조인해서 한 번에 가져옴 (JOIN FETCH처럼)
//    지연로딩 (LAZY)	엔티티를 먼저 가져오고, 연관된 엔티티는 나중에 실제로 접근할 때 가져옴 (프록시 객체로 대기)

//    create table Customer (
//            age integer not null,
//            reserves int default 0,
//            id bigint not null auto_increment,
//            customerName varchar(255),
//    occupation varchar(255),
//    password varchar(255),
//    rating varchar(255),
//    username varchar(255) not null,
//    primary key (id)
//    ) engine=InnoDB
//    Hibernate:
//    alter table Customer
//    add constraint UKmufchskagt7e1w4ksmt9lum5l unique (username)

}
