package com.example.TestcontainersExample;

import com.example.TestcontainersExample.Provider.SomeArgumentsProvider;
import com.example.TestcontainersExample.model.TestUser;
import com.example.TestcontainersExample.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
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
class TestcontainersExampleLiquibaseTests {

	@Autowired
	UserRepository userRepository;

	@Container
	public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
			.withDatabaseName("postgres")
			.withUsername("postgres")
			.withPassword("password");

	@DynamicPropertySource
	static void postgreSQLContainerProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
		registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
		registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
		registry.add("spring.liquibase.enabled", () -> "true");
		registry.add("spring.liquibase.change-log", () -> "classpath:db/changelog.xml");
	}

	@Test
	@DisplayName("whenSaveThenLoad")
	@Transactional
	void whenSaveThenLoad() {
		TestUser userToSave = new TestUser(1,"One more user");
		userRepository.save(userToSave);
		TestUser userToRead = userRepository.getReferenceById(1L);
		assertEquals(userToSave, userToRead);
	}

	@ParameterizedTest
	@ValueSource(longs = {1L, 2L, 3L})
	void testWithValueSource(Long number) {
		assertEquals(number, number);
	}

	@ParameterizedTest
	@ArgumentsSource(SomeArgumentsProvider.class)
	void testWithArgumentsProvider(String argument) {
		assertEquals(argument, argument);
	}

}