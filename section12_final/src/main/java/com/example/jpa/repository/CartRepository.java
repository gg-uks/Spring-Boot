package com.example.jpa.repository;

import com.example.jpa.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart,Long> {
    public List<Cart> findByCustomer_id(Long customer_id); //자기번호와 일치한것만 조회


    @Query(value = "select * from cart  where book_id=?1 and customer_id=?2", nativeQuery = true)
    public  Cart findByCustomerAndBook(Long bookId, Long customerId);
}
