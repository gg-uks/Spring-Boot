package com.example.demo.entity;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookViewDTO {//객체 바인딩해서 넘길때 쓰이는 DTO
    private String title; //  COLUMN varchar(255), not null
    private String author; // authorName(DB)
}
