package com.example.demo.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookPayloadDTO {
    private String title; //  COLUMN varchar(255), not null
    private int price;
    private String author; // authorName(DB)
    private int page;
}
