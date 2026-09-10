package com.src.articleservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ArticleserviceApplication {

	public static void main(String[] args) {

		SpringApplication.run(ArticleserviceApplication.class, args);
		System.out.println("Article service started");
	}

}
