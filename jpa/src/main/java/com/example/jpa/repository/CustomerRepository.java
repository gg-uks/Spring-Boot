package com.example.jpa.repository;

import com.example.jpa.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
    //1. JpaRepository 에서 제공되는 메서드를 사용하는 방법
    //2. 쿼리 메서드를 사용하는 방법 -- jpa가 규칙을 가지고 만들어지는 메서드 ex 제공하는메서드 : findBy  age: Customer 클래스 엔티티값을 참고해 쿼리만듬
    //findBy<멤버변수이름> : select * from customer where <멤버변수이름> = ? (메게변수)
    public List<Customer> findByAge(int age); //30   jpa가 규칙을 가지고 만들어지는 그러한 메서드다


    // username과 password가 일치하는 회원 고객 정보 가져오기?
    public Customer findByUsernameAndPassword(String username , String password);
    //select cus from Customer c where c.username=? nad c.password = ?
    //요런식으로 하이버네이티가 알리아스 객체를 만들어서 만들어준다
    //JPA 가 JPQL 을 만들어줌 SELECT c FROM Customer c WHERE c.username = :username AND c.password = :password
//    "하이버네이트가 객체 알리아스를 생성"한다는 건?
//     JPQL은 SQL과 비슷하지만, 엔티티 이름과 속성 이름을 기반으로 쿼리를 만들어줌.
//    여기서 Customer c의 c는 JPQL에서 사용하는 알리아스(alias)
//    이건 Hibernate가 해석해서 실제 SQL로 변환해줌
    //SELECT * FROM customer WHERE username = ? AND password = ?
    //Spring JPA 쿼리메서드 공식사이트를 보면 정확히 쓸수있다 다양한조합이 있다

    //findBy + (필드명 첫 글자만 대문자로)
    //👉 이건 JPA 메서드 이름 작성 규칙
    public Customer findByUsernameIs(String username); //특정 하나의 조건문을 걸어 내용가지고오기
    public List<Customer> findByAgeGreaterThanEqual(int age); //매개변수 로 들어온 값 이상인 데이터 출력  ex) 30세 이상만 데이터 가져오기

    //JPQL(사용자정의 쿼리) : Java Persistence Query Language
    // 1.Entity(Object) 를 기준으로 쿼리 만들기
    // 2.실제 Mysql 테이블을 기준으로 쓸수 있다(nativeQuery)
    // >> 개발자가 쿼리메서드처럼 메서드기반에 쿼리가 생성되는게 아니라 우리가 직접 쿼리를 사용할수 있다.
//    @Query("select cus from Customer cus where cus.rating=:rating")   // JPQL 오브젝트 기준
    @Query(value = "select * from customer where rating=?1",nativeQuery = true)
    public List<Customer> getRating(@Param("rating") String rating); //vip,gold,silver

    @Query(value = "select * from customer where username=?1 and password=?2" , nativeQuery = true)
    public Customer getByUsernameAndPassword(String username , String password);

    //Query DSL : Hibernate Query Language의 쿼리를 타입에 안전하게 생성및 관리해주는 Java 프레임 워크이다.
    //Domain Specific Language (DSL) 형식으로 쿼리를 작성할 수 있게 해줍니다
    //이는 SQL 보다 가독성이 높고 개발자가 쿼리를 더 직관적이고 유연하게 작성할 수 있도록 도와줍니다




}
