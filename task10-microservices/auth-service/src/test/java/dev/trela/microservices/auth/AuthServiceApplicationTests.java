package dev.trela.microservices.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
public class AuthServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
