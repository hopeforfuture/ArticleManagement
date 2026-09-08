package com.src.categoryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CategoryServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(CategoryServiceApplication.class, args);
		System.out.println("Category Service Application Started");
	}

}
