package app;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.sql.DataSource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import app.data.IAirportRepository;

@SpringBootTest
class TicketingRestTests {
	
	@Autowired
	private DataSource dataSource;

	@MockBean
	private IAirportRepository airportRepository;
	
	@DisplayName("Application context is loaded successfully")
	@Test
	void contextLoads() {
	}

	@DisplayName("HikariCP is used successfully")
	@Test
	public void hikariConnectionPoolIsConfigured() {
		assertEquals("com.zaxxer.hikari.HikariDataSource", dataSource.getClass().getName());
	}
	
}
