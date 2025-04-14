package com.example.demo.repository;

import com.example.demo.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
//Mybatis --> JPA 메소드를 안만들어도 된다
//부트 JPA는 레퍼지토리쪽이 바껴진다
//JPA는 레퍼지토리로 하는게 맞다 마이바티스사용은 Mapper고
@Repository//JpaRepository --> CRUD Method정의 (table정보를 알아야 하고, PK정보를 알아야검색해서 가져오고 다양한 기능을 할수 있어서)
public interface BookRepository extends JpaRepository<Book,Long> { //JpaRepository Jpa레퍼지토리는 스프링 데이터 JPA에서 제공해주는 인터페이스이다 JpaRepository는 CRUD를 할수있는 메소드 정의 됨
//    JpaRepository<Book,Long> 이부분이 진짜 중요하다 Book라는 테이블 즉 ORM 기술로 쓴 Book 클래스를 참고해서 쿼리가 작동한다 BookRepository이걸호출시에
//    JpaRepository<Book,Long>  여기서 Long는 PK타입을 가리킨다


    //CRUD기능 정의 --> x                      이렇게 Book DTO와 PK의 타입인 Long를 제네릭타입으로 받아야한다
    //1.전체리스트 가져오기-findAll()
    //2.데이터 저장하기 save()
    //3.특정 데이터 가져오기 - findById(Long id) //PK에 있는 id를 가리킴 -PK
    //4.특정 데이터 수정하기 - save()를 이용해서 수정하는방법, 기존의 PK값이 존재하면->update SQL
    //* 트렌젝션 처리(All or Nothing) 전체다 반영되던지 아니던지
    //데이터가 저장되는 공간 : 영속성(일관성,정보가항상일치) 메모리이다(자동으로 더티체킹)
    //Book(Object)(수정)<------------------->book(table)  오브젝트가 수정이되면 book테이블도 수정이 된다

    //5.특정 데이터 삭제하기 - deleteById()  void형


}





//interface BookRepository
/*
* public class EntityManagerFactory interface BookRepository{   >> 마이바티스는 SqlSessionFactory 가 구현채였다  JPA는 EntityManagerFactory이클래스가 BookRepository의 구현체이다
* SimpleJpaRepository  진짜 구현체 (save, findAll 같은 메서드 구현돼 있음)
*    프록시 객체: 이 구현체를 감싸고, Spring이 자동으로 만든 가짜 객체
* @Autowired

    public interface BookRepository extends JpaRepository<Book, Long> { }
        → 직접 구현 안 했는데, save(), findAll() 다 쓸 수 있음!
        *
        * 그 이유는?
        * 1. JpaRepository 상속 구조
        * JpaRepository → PagingAndSortingRepository → CrudRepository → Repository
        * 2. 구현체는 SimpleJpaRepository
            Spring Data JPA는 JpaRepository 인터페이스의 기본 구현체로
            SimpleJpaRepository 클래스를 만들어 놓음
          3. 프록시 객체가 진짜 핵심
        *   @Autowired
            BookRepository bookRepository;
            * 이렇게 주입되면,

            * 프록시 객체가 대신 받아서 동작함.
       *    bookRepository = new 프록시객체(SimpleJpaRepository 위임);
       *     4. 프록시가 해주는 일
       *    💼 트랜잭션 처리	@Transactional 기반으로 자동 커밋/롤백
            🔒 AOP 기능 삽입	보안, 로깅, 캐싱 등 공통기능 자동 삽입
            🧠 Lazy Loading	진짜로 사용할 때 DB 조회 실행
            *
                            * 우리가 만든 인터페이스]
                BookRepository (extends JpaRepository)

                          |
                          v

                [스프링이 만든 프록시 객체]
                프록시(BookRepository 인터페이스 타입)
                → 내부에 SimpleJpaRepository를 들고 있음
                → 실행 시 트랜잭션, AOP, 로깅 등 삽입

                          |
                          v

                [실제 구현체]
                SimpleJpaRepository
                → save(), findAll() 등의 로직이 구현됨
                        *
                   키워드	설명
            프록시 객체 : 진짜 객체를 감싸서 추가 기능을 삽입해주는 껍데기 객체
            SimpleJpaRepository	JpaRepository의 기본 구현체 (save 등 실제 코드 존재)
            다형성 :	BookRepository 타입으로 프록시를 주입받아 사용 가능
            @Autowired	스프링이 자동으로 프록시 객체를 주입해줌
            트랜잭션 / AOP	프록시가 중간에 끼워주는 기능 (자동 롤백, 로깅 등)
            *
            *
                        bookRepository.findAll(); // 너가 호출

                        ↓ (프록시가 중간에서 잡음)

                        프록시: "트랜잭션 시작!"
                        ↓
                        SimpleJpaRepository.findAll() 호출
                        ↓
                        프록시: "트랜잭션 커밋!"
                        ↓
                        결과 반환
                        *
                        *
                        * 구성 요소	역할
                        BookRepository	너가 만든 인터페이스
                        SimpleJpaRepository	스프링이 내부적으로 만든 구현체 (진짜 로직이 있음)
                        프록시 객체	BookRepository를 구현한 클래스, 내부에 SimpleJpaRepository를 들고 있음. 모든 메서드 호출을 이쪽으로 위임함
                        *
                        *
                        * 🎯 진짜 쉽게 비유하면?
                            프록시는 택배 회사의 기사 같은 역할이야.
                            너는 그냥 bookRepository.findAll()이라는 택배 요청을 보내면
                            프록시(택배 기사)가 그걸 받아서
                            중간에 검수하고(트랜잭션, AOP)
                            진짜 창고(SimpleJpaRepository)에서 물건 가져오고
                            다시 너한테 배달해주는 거야.
                            *
                            * ✅ 영속성 컨텍스트(Persistence Context)란?
                            영속성 컨텍스트는 JPA가 엔티티를 관리하는 1차 캐시 공간이라고 생각하면 돼.
                            이 공간은 트랜잭션 범위 내에서 엔티티 객체들을 감시하고,
                            변화가 있으면 트랜잭션이 끝날 때 자동으로 DB에 반영해
                            * ✅ 요점 정리
                            용어	의미
                            영속성	데이터가 DB에 저장돼서 계속 유지되는 상태
                            영속성 컨텍스트	JPA가 엔티티를 감시하고 관리하는 메모리 공간 (1차 캐시)
                            영속 상태	엔티티가 영속성 컨텍스트에 들어간 상태 (JPA가 추적 중)
                            더티 체킹	영속 상태의 객체가 변경됐는지 감지하고, 변경되었으면 DB에 반영하는 기능
                            트랜잭션 커밋 시점	이 시점에서 JPA는 변경된 내용을 DB에 UPDATE 등으로 반영함
* */
