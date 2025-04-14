package com.example.jpa.service;

import com.example.jpa.entity.Customer;
import com.example.jpa.entity.Review;
import com.example.jpa.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> getAllCustomers(){
        //고객 전체 리스트 가져오기
        /*
         Customer user1 ,Eager reviews(3) - JOIN
         Customer user2 ,Eager reviews(3) - JOIN
         Customer user3 ,Eager reviews(3) - JOIN
        */
        /*
         Customer user1 ,LAZY reviews(x) - JOIN
         Customer user2 ,LAZY reviews(x) - JOIN
         Customer user3 ,LAZY reviews(x) - JOIN
        */
        List<Customer> customers = customerRepository.findAll();//select SQL ~ Customer 정보만 - LAZY일때
//            for(Customer cus:customers){
//                System.out.println(cus.getReviews().size()); //Eager >> 3 타입은 메모리에 미리 올라와 있는지 확인  //id(1) <---JOIN---> Review(customer_id)
//            }                                                //- LAZY일때 SQL 쿼리 트리거(이벤트)  //id(1) <---JOIN---> Review(customer_id)
        return customers;
    }

    //@Transactional이 필요한 이유는 **지연 로딩(LAZY)**된 reviews를 서비스 계층에서 프록시 초기화하기 위해서야.
    //Customer를 불러왔을 때 실제 DB에서 reviews는 바로 로딩되지 않음 (LAZY 설정 시)
    //트랜잭션이 열려 있어야 customer.getReviews() 호출 시 지연 로딩 쿼리가 제대로 실행돼
    @Transactional //SQL트랜젝션이 오류없이 작동하도록 붙이는것
    public List<Review> getReviewsByCustomerId(Long id){
        Customer customer = customerRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Customer not found 고객아이디를 발견 못함"));
        return customer.getReviews(); // SQL : customer(id:1<---JOIN--->Review(customer_id(3개))
    }



    /*동작 흐름:
초기 상태 (프록시 객체는 null 데이터):
지연 로딩이 설정된 연관된 엔티티는 처음에는 null로 설정됩니다.
예를 들어, Customer 객체의 orders는 **초기 상태에서는 null**일 수 있습니다.

클라이언트의 요청:
클라이언트가 특정 요청을 보낼 때(예: 고객의 주문 정보를 가져오는 요청을 보낼 때), 서버에서 연관된 엔티티(orders)가 필요하다는 것이 확인되면 그때서야 DB에서 데이터를 지연 로딩 방식으로 가져옵니다.

로직 처리:
서비스 계층에서 해당 연관 데이터를 조회할 때, JPA 프록시 객체가 실제 DB에서 데이터를 가져옵니다.
이때 프록시 객체는 해당 데이터를 DB에서 로딩한 후, 실제 객체처럼 동작하여 반환합니다.

결과:
클라이언트는 실제 로딩된 데이터를 받게 됩니다. 예를 들어, customer.getOrders()를 호출했을 때, orders 데이터가 실제로 DB에서 조회되고 그 값이 반환됩니다.*/
}
