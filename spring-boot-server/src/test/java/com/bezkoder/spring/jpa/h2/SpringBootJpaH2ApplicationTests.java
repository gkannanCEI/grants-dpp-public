package com.bezkoder.spring.jpa.h2;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Integration smoke test — verifies the full Spring application context
 * loads successfully using an in-memory H2 database (no live PostgreSQL needed).
 *
 * <p>Uses {@link AutoConfigureTestDatabase} to replace the configured PostgreSQL
 * DataSource with H2, and {@link TestPropertySource} to supply all required
 * environment variable substitutions defined in application.properties.
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
@TestPropertySource(locations = "classpath:application.properties")
class SpringBootJpaH2ApplicationTests {

	@Test
	void contextLoads() {
		// Verifies the Spring ApplicationContext starts without errors.
		// If this test passes, all beans (Security, JPA, Controllers) wire correctly.
	}

}
