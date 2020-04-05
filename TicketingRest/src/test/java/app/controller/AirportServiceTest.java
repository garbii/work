package app.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.data.IAirportRepository;
import app.exception.ControllerExceptionHandler.ErrorResult;
import app.model.Airport;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AirportServiceTest {

	@MockBean
	private IAirportRepository airportRepository;
	
	private MockMvc mockMvc;
	
	@Autowired
    private TestRestTemplate restTemplate;

	@Autowired
	ObjectMapper om;

	@Autowired
	WebApplicationContext context;
	
	@Autowired
    private MessageSource messageSource;

	/*************START: SETUP settings..***********************************************/
	@BeforeAll
	public void setUp() {
		 mockMvc = MockMvcBuilders
			        .webAppContextSetup(context)
			        .addFilter((request, response, chain) -> {
			          response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
			          chain.doFilter(request, response);
			        }) .build();
	}
	/*************END: SETUP settings..*************************************************/
	
	/*************START: MockMvc Tests**************************************************/
	@DisplayName("Searching airport by id is successful")
	@Test
	public void findById() throws Exception {
		Airport ataAirport = new Airport(1, "Atatürk Havalimanı", "ATA");
		when(airportRepository.findById(1L)).thenReturn(Optional.of(ataAirport));
		mockMvc.perform(get("/airport/1"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.name", is("Atatürk Havalimanı")))
				.andExpect(jsonPath("$.code", is("ATA")))
				.andExpect(ResponseBodyMatchers.responseBody().containsObjectAsJson(ataAirport, Airport.class));

	    verify(airportRepository, times(1)).findById(1L);

	}

	@DisplayName("Finding all airports is successful")
	@Test
	public void findAll() throws Exception {
		List<Airport> airports = Arrays.asList(new Airport(1, "Atatürk Havalimanı", "ATA"),
				new Airport(2, "Sabiha Gökçen Havalimanı", "SAW"));
		when(airportRepository.findAll()).thenReturn(airports);
		mockMvc.perform(get("/airport").contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].name", is("Atatürk Havalimanı")))
				.andExpect(jsonPath("$[0].code", is("ATA")))
				.andExpect(jsonPath("$[1].id", is(2)))
				.andExpect(jsonPath("$[1].name", is("Sabiha Gökçen Havalimanı")))
				.andExpect(jsonPath("$[1].code", is("SAW")));

		verify(airportRepository, times(1)).findAll();

	}

	@DisplayName("Adding airport is successful")
	@Test
	public void add() throws Exception {
		Airport ataAirport = new Airport(1, "Atatürk Havalimanı", "ATA");
		when(airportRepository.save(any(Airport.class))).thenReturn(ataAirport);
		mockMvc.perform(post("/airport/add").content(om.writeValueAsString(ataAirport))
						    .header(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_JSON))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.name", is("Atatürk Havalimanı")))
				.andExpect(jsonPath("$.code", is("ATA")));

		verify(airportRepository, times(1)).save(any(Airport.class));

	}
	/*************END: MockMvc Tests****************************************************/
	
	/*************START: RestTemplate Tests*********************************************/
	@DisplayName("Adding airport(airport code not valid, HTTP 400 is expected)")
	@Test
    public void save_airport_code_400() throws JSONException {
		
		Airport ataAirport = new Airport(1, "Atatürk Havalimanı", null);
        ResponseEntity<ErrorResult> response = restTemplate.postForEntity("/airport/add", ataAirport, ErrorResult.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("code", response.getBody().getFieldErrors().get(0).getField());
        assertEquals(messageSource. getMessage("airport.code.empty",null,Locale.getDefault()), response.getBody().getFieldErrors().get(0).getMessage());

        //Check code length
        Airport airport2 = new Airport(1, "Atatürk Havalimanı", "AL");
        ResponseEntity<ErrorResult> response2 = restTemplate.postForEntity("/airport/add", airport2, ErrorResult.class);
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response2.getHeaders().getContentType());
        assertEquals("code", response2.getBody().getFieldErrors().get(0).getField());
        assertEquals(messageSource. getMessage("airport.code.size",null,Locale.getDefault()), response2.getBody().getFieldErrors().get(0).getMessage());

    }
	
	@DisplayName("Adding airport(airport name is null, HTTP 400 is expected)")
	@Test
    public void save_airport_name_null_400() throws JSONException {
		
		Airport ataAirport = new Airport(1, null, "ATA");
        ResponseEntity<ErrorResult> response = restTemplate.postForEntity("/airport/add", ataAirport, ErrorResult.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("name", response.getBody().getFieldErrors().get(0).getField());
        assertEquals(messageSource. getMessage("airport.name.empty",null,Locale.getDefault()), response.getBody().getFieldErrors().get(0).getMessage());

    }
	/*************END: RestTemplate Tests*********************************************/
}
