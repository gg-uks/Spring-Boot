package com.example.jwt.filter;

public interface JwtProperties {
    public String SECRET = "cosin"; //cosin이라는 비밀 키로 서명(sign)
    public int EXPIRATION_TIME = 60000*10; //jwt만료시간
    public String TOKEN_PREFIX = "Bearer ";
    public String HEADER_STRING = "Authorization";
}
