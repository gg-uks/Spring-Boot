package com.example.jwt.entity;

import com.example.jwt.service.CustomerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerUser extends User {

    private Customer customer;

    public CustomerUser(Customer customer){
        super(customer.getUsername(),customer.getPassword(), CustomerUser.makeConvertRole(customer.getRoleList()));
        this.customer=customer;
    }


    public static Collection<? extends GrantedAuthority> makeConvertRole(List<String> list){
        return list.stream()
                .map(role->new SimpleGrantedAuthority("ROLE_"+role))
                .collect(Collectors.toList());
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
