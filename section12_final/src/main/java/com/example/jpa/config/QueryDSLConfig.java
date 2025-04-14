package com.example.jpa.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class QueryDSLConfig {

    private final EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory(){
         return new JPAQueryFactory(entityManager);
    }
    //EntityManager는 JPA에서 기본으로 제공하는 영속성 컨텍스트 관리 객체
    //JPAQueryFactory는 QueryDSL에서 타입 안전한 쿼리를 작성할 수 있게 도와주는 클래스
    //entityManager를 내부에서 사용하기 위해 QueryDSL의 JPAQueryFactory에 주입하는 것
    //@Bean 등록	한 번만 생성해서 스프링 컨테이너에 등록 → 필요한 클래스에 주입해서 전역적으로 재사용
}
