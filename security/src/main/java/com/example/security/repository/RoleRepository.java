package com.example.security.repository;

import com.example.security.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
    public Role findByName(String name); //select * from role where name='USER'

}
