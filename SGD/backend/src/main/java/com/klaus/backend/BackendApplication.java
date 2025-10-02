package com.klaus.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
		System.setProperty("spring.datasource.url", dotenv.get("POSTGRES_URL"));
		System.setProperty("spring.datasource.username", dotenv.get("POSTGRES_USER"));
		System.setProperty("spring.datasource.password", dotenv.get("POSTGRES_PASSWORD"));
		SpringApplication.run(BackendApplication.class, args);
	}

}
