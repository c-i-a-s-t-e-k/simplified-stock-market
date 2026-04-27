package com.example.simplifiedstockmarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SimplifiedstockmarketApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimplifiedstockmarketApplication.class, args);
	}

}
