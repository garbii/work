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

import app.data.IFlightRepository;
import app.data.ITicketRepository;
import app.exception.ControllerExceptionHandler.ErrorResult;
import app.model.Company;
import app.model.Flight;
import app.model.Rout;
import app.model.Ticket;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FlightServiceTest {

	@MockBean
	private IFlightRepository flightRepository;
	
	@MockBean
	private ITicketRepository ticketRepository;
	
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
	@DisplayName("Searching flight by id is successful")
	@Test
	public void findById() throws Exception {
		Company company = new Company(1, "Pegasus Hava Yolları", "Pegasus Hava Taşımacılığı A.Ş.");
		Flight ataFlight = new Flight(1L,company,new Rout(1L), 50, 12, 120,"₺", "New Flight");
		when(flightRepository.findById(1L)).thenReturn(Optional.of(ataFlight));
		mockMvc.perform(get("/flight/1"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.company.name", is("Pegasus Hava Yolları")))
				.andExpect(jsonPath("$.capacity", is(50)))
				.andExpect(ResponseBodyMatchers.responseBody().containsObjectAsJson(ataFlight, Flight.class));

	    verify(flightRepository, times(1)).findById(1L);

	}

	@DisplayName("Finding all flights is successful")
	@Test
	public void findAll() throws Exception {
		Company company = new Company(1, "Pegasus Hava Yolları", "Pegasus Hava Taşımacılığı A.Ş.");
		Flight flight1 = new Flight(1L,company,new Rout(1L), 50, 12, 120, "₺", "New Flight");
		Flight flight2 = new Flight(2L,company,new Rout(1L), 25, 12, 100, "₺", "New Flight2");
		List<Flight> flights = Arrays.asList(flight1,flight2);
		when(flightRepository.findAll()).thenReturn(flights);
		mockMvc.perform(get("/flight").contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].company.name", is("Pegasus Hava Yolları")))
				.andExpect(jsonPath("$[0].soldTicketCount", is(12)))
				.andExpect(jsonPath("$[1].id", is(2)))
				.andExpect(jsonPath("$[1].company.name", is("Pegasus Hava Yolları")))
				.andExpect(jsonPath("$[1].price", is(100d)));

		verify(flightRepository, times(1)).findAll();

	}

	@DisplayName("Adding flight is successful")
	@Test
	public void add() throws Exception {
		Company company = new Company(1, "Pegasus Hava Yolları", "Pegasus Hava Taşımacılığı A.Ş.");
		Flight flight = new Flight(1L,company,new Rout(1L), 50, 12, 120, "₺", "New Flight");
		when(flightRepository.save(any(Flight.class))).thenReturn(flight);
		mockMvc.perform(post("/flight/add").content(om.writeValueAsString(flight))
						    .header(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_JSON))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.company.name", is("Pegasus Hava Yolları")))
				.andExpect(jsonPath("$.ack", is("New Flight")));

		verify(flightRepository, times(1)).save(any(Flight.class));

	}
	
	@DisplayName("Finding tickets by flight is successful")
	@Test
	public void findFlightTicektsTest() throws Exception {
		Company company = new Company(1, "Pegasus Hava Yolları", "Pegasus Hava Taşımacılığı A.Ş.");
		Flight flight = new Flight(1L,company,new Rout(1L), 50, 12, 120, "₺", "New Flight");
		Ticket ticket1 = new Ticket(1L,flight,115, "₺", "TH7402 nolu uçuş 1.bileti");
		Ticket ticket2 = new Ticket(2L,flight,120, "₺", "TH7402 nolu uçuş 20.bileti");
		List<Ticket> tickets = Arrays.asList(ticket1,ticket2);
		when(ticketRepository.findByFlightId(1L)).thenReturn(tickets);
		mockMvc.perform(get("/flight/1/tickets").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].flight.company.name", is("Pegasus Hava Yolları")))
				.andExpect(jsonPath("$[0].flight.capacity", is(50)))
				.andExpect(jsonPath("$[1].id", is(2)))
				.andExpect(jsonPath("$[1].flight.company.name", is("Pegasus Hava Yolları")))
				.andExpect(jsonPath("$[1].flight.capacity", is(50)));

		verify(ticketRepository, times(1)).findByFlightId(1L);

	}
	/*************END: MockMvc Tests****************************************************/
	
	/*************START: RestTemplate Tests*********************************************/
	@DisplayName("Adding flight(flight capacity is not in valid range, HTTP 400 is expected)")
	@Test
    public void save_flight_name_null_400() throws JSONException {
		Company company = new Company(1, "Pegasus Hava Yolları", "Pegasus Hava Taşımacılığı A.Ş.");
		Flight flight = new Flight(1L,company,new Rout(1L), 5, 2, 120, "₺", "New Flight");
        ResponseEntity<ErrorResult> response = restTemplate.postForEntity("/flight/add", flight, ErrorResult.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("capacity", response.getBody().getFieldErrors().get(0).getField());
        assertEquals(messageSource. getMessage("flight.capacity.range",null,Locale.getDefault()), response.getBody().getFieldErrors().get(0).getMessage());

    }
	/*************END: RestTemplate Tests*********************************************/
}
