package fr.miage.commercantservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CommercantServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommercantServiceApplication.class, args);
	}

}
