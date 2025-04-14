package com.example.jpa.repository;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.example.jpa.entity.Customer;
import com.example.jpa.entity.QCustomer;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomerQueryDSLRepository {

    //Query DSL

    private final JPAQueryFactory queryFactory;
    //Customer -> QCustomer
    public Customer getByUsernameAndPassword(String username , String password){
        QCustomer customer = QCustomer.customer; //빌드 그레들 컴파일 시점에 QCustomer 클래스가 하나더 생심 Q접두사 붙은걸로   ===QueryDSL에서 생성된 메타모델 클래스
        System.out.println("여기가 실행됨");
        // SQL
        return queryFactory.selectFrom(customer)
                .where(customer.username.eq(username)
                        .and(customer.password.eq(password)))
                .fetchOne(); //한가지정보를 가져오려면 리턴 타입맞춰서 // 이 시점에서 DB에 접근하여 데이터를 가져오고, 영속성 컨텍스트에 저장
    }

    public List<Customer> getRating(@Param("rating") String rating){    //vip,gold,silver
        QCustomer customer = QCustomer.customer;
        return queryFactory.selectFrom(customer)
                .where(customer.rating.eq(rating))
                .fetch(); //여러가지 정보 가져오기 등급
    }
}
