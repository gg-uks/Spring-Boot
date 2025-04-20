package com.example.security.service;

import com.example.security.entity.Customer;
import com.example.security.entity.Role;
import com.example.security.repository.MemberRepository;
import com.example.security.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class RoleService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RoleRepository roleRepository;

    //@Autowired
    //private  RoleService roleService;

    public Role findByName(String name){
           return roleRepository.findByName(name);
    }

//                                                      1         USER,MANAGER,ADMIN
    public Customer updateCustomerPermissions( Long customerId,String newPermissions){
        //                                                          1
        Optional<Customer> optional = memberRepository.findById(customerId);
        Customer customer;
        if(optional.isPresent()){
            customer=optional.get();
        }else{
            throw new IllegalArgumentException("error");
        }
        // 2. 권한부여(USER, MANAGER, ADMIN) : member_roles : USER(1)
        Set<Role> roles=new HashSet<>();
        Role userRole=roleRepository.findByName(newPermissions);
        roles.add(userRole);
        customer.setRoles(roles);
        return memberRepository.save(customer); // insert SQL~  데이터가 있으면 업데이트
    }

    public Customer addCustomerPermissions(Long customerId, String newPermissions) {
        Optional<Customer> optional = memberRepository.findById(customerId);
        Customer customer;
        if(optional.isPresent()){
            customer=optional.get();
        }else{
            throw new IllegalArgumentException("error");
        }
        // 2. 권한부여(USER, MANAGER, ADMIN) : member_roles : USER(1)
        Set<Role> roles= customer.getRoles();
        //Set<Role> roles=new HashSet<>();
        Role userRole=roleRepository.findByName(newPermissions);
        roles.add(userRole);
        customer.setRoles(roles); // 권한정보가 두개 들어가있는상태
        return memberRepository.save(customer); // insert SQL~  같은 데이터가 있으면 업데이트
    }
}
