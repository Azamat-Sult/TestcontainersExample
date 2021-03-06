package com.example.TestcontainersExample;

import com.example.TestcontainersExample.model.TestUser;
import com.example.TestcontainersExample.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
class TestcontainersExampleSQLScriptTests {

	@Autowired
	UserRepository userRepository;

	@Container
	public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
			.withDatabaseName("postgres")
			.withUsername("postgres")
			.withPassword("password")
			.withInitScript("db/scripts/test.sql");

	@DynamicPropertySource
	static void postgreSQLContainerProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
		registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
		registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
		registry.add("spring.liquibase.enabled", () -> "false");
	}

	@Test
	@Transactional
	void whenSaveThenLoad() {
		TestUser userToSave = new TestUser(1,"One more user");
		userRepository.save(userToSave);
		TestUser userToRead = userRepository.getReferenceById(1L);
		assertEquals(userToSave, userToRead);
	}

}