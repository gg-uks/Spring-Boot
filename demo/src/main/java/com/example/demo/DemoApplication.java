package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication //web.xml -> DispatcherServlet(xml)FRONT컨트롤러 / ContextLoaderListener(xml)DB  >> application프로퍼티에 설정해야함 SC에 올림 스프링컨테이너에 자동으로 매모리에 올라감
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args); //Tomcat Server 구동 :8080 -> 8081

	}

}
