package com.example.security.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role { //1.USER 2.MANAGER 3.ADMIN
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 1,2,3,4

    @Column(unique = true)
    private String name; //권한이름 (USER,MANAGER,ADMIN)
}