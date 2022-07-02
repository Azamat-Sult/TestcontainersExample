package com.example.TestcontainersExample;

import com.example.TestcontainersExample.model.TestUser;
import com.example.TestcontainersExample.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ContextConfiguration(initializers = {TestcontainersExampleSQLScriptTests.Initializer.class})
@Testcontainers
class TestcontainersExampleSQLScriptTests {

	@Autowired
	UserRepository userRepository;

	@Container
	public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
			.withDatabaseName("postgres")
			.withUsername("postgres")
			.withPassword("password")
			.withInitScript("test.sql");

	static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
			TestPropertyValues.of(
					"spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
					"spring.datasource.username=" + postgreSQLContainer.getUsername(),
					"spring.datasource.password=" + postgreSQLContainer.getPassword()
			).applyTo(configurableApplicationContext.getEnvironment());
		}
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