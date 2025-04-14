package com.example.jpa.repository;

import com.example.jpa.entity.Customer;
import com.example.jpa.entity.QCustomer;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomerQueryDSLRepository {

    private final JPAQueryFactory queryFactory;

    // Customer -> QCustomer
    public Customer getUsernameAndPassword(String username, String password){
        QCustomer customer=QCustomer.customer; //QCustomer는 QueryDSL이 컴파일 시점에 자동 생성해주는 클래스   >>>>> public static final QCustomer customer = new QCustomer("customer");
        System.out.println("여기가 실행됨");
       // SQL

        //JPAQueryFactory는 QueryDSL에서 제공되는 쿼리 빌더로, 메타모델 클래스를 사용하여 타입 안전하고 동적 쿼리를 작성하는데 사용. 이를 통해 SQL 쿼리를 작성할 때 직관적이고 안전하게 작업할 수 있음
        return queryFactory.selectFrom(customer)//customer는 QCustomer.customer라는 정적 필드로, QCustomer 클래스에서 Customer 엔티티의 메타데이터를 사용해 쿼리를 빌드
                .where(customer.username.eq(username) //여기에 username과 password에 맞는 조건이 추가
                        .and(customer.password.eq(password)))//SELECT * FROM customer WHERE username = ? AND password = ?
                .fetchOne();//쿼리 결과가 정확히 1개일 때만 사용
        // etchOne()이 실행되면, JPA는 데이터베이스에 실제로 이 쿼리를 실행하고, 결과를 매핑하여 Customer 객체로 반환
                                    //.fetchOne()이후에 즉, SQL 쿼리 결과는 Customer 객체에 자동으로 매핑되어 반환
            //QueryDSL은 내부적으로 JPQL 쿼리를 만듬 --- 이건 마치 entityManager.find() 또는 TypedQuery.getSingleResult()처럼 동작

        //.fetchOne() 내부적인 동작흐름  >
        //public <T> JPAQuery<T> selectFrom(EntityPath<T> from) {
        //    return new JPAQuery<>(entityManager).select(from).from(from);
        //}
        //즉, 결국 JPAQuery는 JPQL 쿼리 빌더

        //>> 한마디로 QueryDSL은 내부적으로 JPQL을 생성해서, JPA의 엔티티 매니저(EntityManager)를 통해 실행하는 방식
    }


    public List<Customer> getRating(String rating){
         QCustomer customer=QCustomer.customer;
         return queryFactory.selectFrom(customer)
                 .where(customer.rating.eq(rating))
                 .fetch(); //여러개 데이터를 받으려면 fetch 사용
    }
}



//메타모델을 사용하는 이유
//타입 안전성: customer.username.eq(username)와 같은 방식은 타입 안전합니다. 예를 들어, username이 문자열이 아니라면 컴파일 타임에서 오류가 발생할 수 있습니다. 컴파일 시점에 오류를 발견할 수 있기 때문에, 런타임 에러를 예방할 수 있습니다.
//SQL 쿼리 생성 자동화: 메타모델을 통해 엔티티의 필드를 객체 지향적으로 사용할 수 있으며, 쿼리 작성 시 직관적인 코드로 SQL을 생성할 수 있습니다. 또한, 쿼리 문법 오류나 타입 오류를 쉽게 잡을 수 있습니다.
