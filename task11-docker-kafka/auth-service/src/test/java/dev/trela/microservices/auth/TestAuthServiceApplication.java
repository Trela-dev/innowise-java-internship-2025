package dev.trela.microservices.auth;

import org.springframework.boot.SpringApplication;

class TestAuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(AuthServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
