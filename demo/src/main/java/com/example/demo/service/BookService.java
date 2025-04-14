package com.example.demo.service;

import com.example.demo.entity.Book;
import com.example.demo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    BookRepository bookRepository;

    //1. 전체리스트가져오는 메서드
    public List<Book>getAllList(){
        return bookRepository.findAll(); //select SQL
    }

    //2. 데이터 등록하기
    public Book register(Book book){
        return bookRepository.save(book); //insert SQL
    }

    //3. 특정데이터 가져오기(PK)
    public Book getById(Long id){
        //Optional을 쓰면 null이 직접 리턴되지 않기 때문에,
        //무심코 null을 접근해서 발생하는 NullPointerException을 방지
        Optional<Book> optional= bookRepository.findById(id); //옵셔널 타입으로가지고옴  Optional<T>은 Java 8부터 도입된 **"null 방지용 래퍼 클래스
         if(optional.isPresent()){//존재하면
             return optional.get(); //Book 객체가 리턴
         }else{
             throw new RuntimeException("Book not found with id"+id);
         }
    }

    //4. 특정데이터 수정하기  @Transactional 어노테이션이 있어야만 JPA가 트랜잭션을 감지해서 UPDATE 해준다
    @Transactional //스프링프레임워크에서 제공하는것사용
    public Book update(Long id,Book reqBook){//어떤 아이디에 해당하는 어떤데이터를 수정할것인지
        Optional<Book> optional = bookRepository.findById(id); //findById라는 키워드를쓰면 영속성 컨텍스트에 들어감 그래서 감시
        if(optional.isPresent()) { //데이터가 있으면 수정하고 없으면 예외던지기
            Book book = optional.get(); //여기에 있는 book는 영속성 메모리에 있는 book이다 Book<----->book
            book.setTitle(reqBook.getTitle()); //현재수정할 데이터를 매개변수에 넘겨준다 영속성 메모리에도 집어 넣어줘야 하기때문에     >>영속상태의 객체이다
            book.setPrice(reqBook.getPrice()); //클라이언트가 입력한 수정값을 가지고와서 DTO에 있는 set으로 넣어줌
//            return book; //자동으로 더티체킹을 한후 update가 된다 트랜잭션 커밋 시점에 더티체킹 →
            return bookRepository.save(book); //트랜잭션 커밋 시점에 더티체킹 → UPDATE SQL 실행
        }else {
            throw new RuntimeException("Book not found with id"+id);
        }
    }

    //5.특정데이터 삭제하기
    public void deleteById(Long id){
        bookRepository.deleteById(id);//delete SQL
    }

}
//✔️ 1. 영속성 컨텍스트(Persistence Context)란?
//JPA가 엔티티 객체를 1차 캐시로 저장하고 관리하는 메모리 공간이야.
//
//여기에 들어가면 그 객체는 "영속 상태(persistent state)"가 되고,
//
//JPA가 그 객체가 언제 바뀌는지 감시함 (→ 더티 체킹)
