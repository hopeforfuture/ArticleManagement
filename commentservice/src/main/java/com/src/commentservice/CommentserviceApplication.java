package com.src.commentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CommentserviceApplication {

	public static void main(String[] args) {

		SpringApplication.run(CommentserviceApplication.class, args);
		System.out.println("Comment Service Application Started");
	}

}
