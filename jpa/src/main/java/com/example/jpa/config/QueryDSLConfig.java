package com.example.jpa.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor //final 키워드가 붙은 필드(entityManager)에 대해 자동으로 생성자 주입을 만들어줌
public class QueryDSLConfig {

    private final EntityManager entityManager; //JPA의 핵심 객체인 EntityManager를 스프링이 자동으로 주입해줘

    @Bean  //이 메서드가 반환하는 객체를 스프링 컨테이너에 Bean으로 등록한다는 뜻
    public JPAQueryFactory jpaQueryFactory(){   //JPAQueryFactory는 QueryDSL 쿼리를 만드는 메인 객체
        return new JPAQueryFactory(entityManager);
    }



    //1. JPAQueryFactory가 하는 일
    //JPAQueryFactory는 QueryDSL에서 제공하는 쿼리 생성 도우미 객체야.
    //이 객체를 이용해서 우리는 자바로 쿼리를 짤수있다 순수 mysql이 아닌

//    2. 왜 EntityManager가 필요할까?
//    JPAQueryFactory는 내부적으로 JPA의 EntityManager를 이용해서 DB 접근을 해.
//    즉, 우리가 SQL을 쓰는 게 아니라, **JPA 엔진한테 "요 조건으로 데이터를 가져와줘"**라고 말하는 것.

//     3. 왜 build.gradle에 의존성 추가해야 하나?
//      덕분에 아래처럼 자바에서 쿼리를 작성할 수 있는 클래스들이 자동으로 생성돼:
//      QCustomer, QOrder 이런 클래스들
//      빌드할 때, QueryDSL이 @Entity 클래스들을 보고 Q 접두사 붙인 클래스를 만들어줘

//    EntityManager ->	JPA의 핵심 ORM 도구, DB 연결 담당
//    JPAQueryFactory ->	Java 코드로 쿼리 짤 수 있는 QueryDSL 도우미
//    Q도메인 클래스  ->	QueryDSL 전용으로 자동 생성된 쿼리 클래스  요것은 빌드.그레들 에 설정할때 컴파일기준으로(빌드) Q클래스접두사를 생성해준다
//    build.gradle 설정 ->	QueryDSL 사용을 위한 의존성과 코드 생성기 등록

//    JPAQueryFactory	QueryDSL에서 제공하는 쿼리 실행 도구. 내부적으로 EntityManager를 사용
//    EntityManager	JPA의 핵심 ORM 인터페이스. DB와 직접 통신하며 영속성 컨텍스트 관리
//    왜 주입하나?	JPAQueryFactory가 EntityManager 없이는 DB 연산을 할 수 없기 때문
//    EntityManager 왜 인터페이스 타입?	스프링이 내부에서 프록시 객체를 생성해서, 트랜잭션 처리나 지연 로딩 같은 기능을 추가해주기 때문 (AOP + DI)

//   JPQL이나 QueryDSL에서 프록시 객체는 메서드 호출을 가로채고, 실제 객체의 메서드를 실행할지 말지를 결정합니다.
//이들은 지연 로딩을 통해, 실제로 필요한 데이터를 DB에서 조회하거나 영속성 컨텍스트에서 재사용하여 성능을 최적화합니다.
//프록시 클래스는 DB 접근 최적화와 메서드 호출 관리에 중요한 역할을 하며, QueryDSL과 JPQL 모두에서 영속성 컨텍스트와의 상호작용을 관리합니다.
}
