package dev.trela.microservices.auth;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
@Disabled
@Import(TestcontainersConfiguration.class)
@SpringBootTest
public class AuthServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
