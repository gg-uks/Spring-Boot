package com.example.jpa.repository;

import com.example.jpa.entity.Book;
import com.example.jpa.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book,Long> {

    // JPA제공되는메서드
    // 쿼리 메서드
    // JPQL
    //1. JpaRepository 에서 제공되는 메서드를 사용하는 방법
    //2. 쿼리 메서드를 사용하는 방법 -- jpa가 규칙을 가지고 만들어지는 메서드 ex 제공하는메서드 : findBy  age: Customer 클래스 엔티티값을 참고해 쿼리만듬
    //findBy<멤버변수이름> : select * from customer where <멤버변수이름> = ? (메게변수)
    // public List<Customer> findByAge(int age); //30   jpa가 규칙을 가지고 만들어지는 그러한 메서드다

    //JPQL(사용자정의 쿼리) : Java Persistence Query Language
    // 1.Entity(Object) 를 기준으로 쿼리 만들기
    // 2.실제 Mysql 테이블을 기준으로 쓸수 있다(nativeQuery)
    // >> 개발자가 쿼리메서드처럼 메서드기반에 쿼리가 생성되는게 아니라 우리가 직접 쿼리를 사용할수 있다.
    //    @Query("select cus from Customer cus where cus.rating=:rating")   // JPQL 오브젝트 기준
    //@Query(value = "select * from customer where rating=?1",nativeQuery = true)
    //public List<Customer> getRating(@Param("rating") String rating); //vip,gold,silver
                                            //1 번자리 오는 파라미터값

}
