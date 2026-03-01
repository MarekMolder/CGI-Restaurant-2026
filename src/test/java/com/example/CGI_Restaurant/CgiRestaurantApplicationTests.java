package com.example.CGI_Restaurant;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestMailConfig.class)
class CgiRestaurantApplicationTests {

	@Test
	void contextLoads() {
	}

}
