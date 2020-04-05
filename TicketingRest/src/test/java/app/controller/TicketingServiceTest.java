package app.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
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
@ExtendWith(SpringExtension.class)
@EnableJpaRepositories(basePackageClasses = {IFlightRepository.class})
public class TicketingServiceTest {

	@MockBean
	private ITicketRepository ticketRepository;
	
	@MockBean
	private IFlightRepository mockFlightRepository;
	
	private MockMvc mockMvc;
	
	@Autowired
    private TestRestTemplate testRestTemplate;
	
	private RestTemplate restTemplate;
	
	@Autowired
    private MessageSource messageSource;
	
	@Autowired
	IFlightRepository flightRepository;
	
	@Autowired
	ObjectMapper om;

	@Autowired
	WebApplicationContext context;
	
	/*************START: SETUP settings..***********************************************/
	@BeforeAll
	public void setUp() {
		 mockMvc = MockMvcBuilders
			        .webAppContextSetup(context)
			        .addFilter((request, response, chain) -> {
			          response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
			          chain.doFilter(request, response);
			        }) .build();
		 
			Company company = new Company(1, "Pegasus Hava Yolları", "Pegasus Hava Taşımacılığı A.Ş.");
			Flight flight = new Flight(1L,company,new Rout(1L), 50, 12, 120, "₺", "New Flight");
	        
			restTemplate = testRestTemplate.getRestTemplate();
			ResponseEntity<ErrorResult> response = restTemplate.postForEntity("/flight/add", flight, ErrorResult.class);
	        assertEquals(HttpStatus.CREATED, response.getStatusCode());
			
	}
	/*************END: SETUP settings..*************************************************/
	
	/*************START: MockMvc Tests**************************************************/
	@DisplayName("Searching ticket by id is successful")
	@Test
	public void findById() throws Exception {
		Company company = new Company(1, "Pegasus Hava Yolları", "Pegasus Hava Taşımacılığı A.Ş.");
		Flight flight = new Flight(1L,company,new Rout(1L), 50, 12, 120, "₺", "New Flight");
		Ticket ticket = new Ticket(1L,flight,115, "₺", "TH7402 nolu uçuş bileti");
		when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
		mockMvc.perform(get("/ticket/1"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.flight.company.name", is("Pegasus Hava Yolları")))
				.andExpect(ResponseBodyMatchers.responseBody().containsObjectAsJson(ticket, Ticket.class));

	    verify(ticketRepository, times(1)).findById(1L);

	}

	@DisplayName("Purchase ticket is successful")
	@Test
	public void purchase_ticket() throws Exception {
		Company company = new Company(1, "Pegasus Hava Yolları", "Pegasus Hava Taşımacılığı A.Ş.");
		Flight flight = new Flight(1L,company,new Rout(1L), 50, 12, 120, "₺", "New Flight");
		Ticket ticket = new Ticket(1L,flight,115, "₺","TH7402 nolu uçuş 1.bileti");
		when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
		when(mockFlightRepository.findById(1L)).thenReturn(Optional.of(flight));
		when(mockFlightRepository.save(any(Flight.class))).thenReturn(flight);
		mockMvc.perform(post("/ticket/purchase").content(om.writeValueAsString(ticket))
						    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.ticket.id", is(1)))
				.andExpect(jsonPath("$.ticket.flight.company.name", is("Pegasus Hava Yolları")))
				.andExpect(jsonPath("$.ticket.price", is(115d)));

		verify(ticketRepository, times(1)).save(any(Ticket.class));
		verify(mockFlightRepository, times(1)).save(any(Flight.class));
		verify(mockFlightRepository, times(1)).findById(1L);

	}
	
	@DisplayName("Purchase ticket(flight capacity is full)")
	@Test
	public void purchase_capacity_full_ticket() throws Exception {
		Company company = new Company(1, "Pegasus Hava Yolları", "Pegasus Hava Taşımacılığı A.Ş.");
		Flight flight = new Flight(1L,company,new Rout(1L), 50, 50, 120, "₺", "New Flight");
		Ticket ticket = new Ticket(1L,flight,115, "₺","TH7402 nolu uçuş 1.bileti");
		when(mockFlightRepository.findById(1L)).thenReturn(Optional.of(flight));
		mockMvc.perform(post("/ticket/purchase").content(om.writeValueAsString(ticket))
						    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", 
						is(messageSource.getMessage("ticket.flight.capacity.status.full",null,Locale.getDefault()))));

		verify(mockFlightRepository, times(1)).findById(1L);

	}
	
	@DisplayName("Cancel ticket is successful")
	@Test
	public void cancel_ticket() throws Exception {
		Company company = new Company(1, "Pegasus Hava Yolları", "Pegasus Hava Taşımacılığı A.Ş.");
		Flight flight = new Flight(1L,company,new Rout(1L), 50, 12, 120, "₺", "New Flight");
		Ticket ticket = new Ticket(1L,flight,115, "₺","TH7402 nolu uçuş 1.bileti");
		when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
		when(mockFlightRepository.findById(1L)).thenReturn(Optional.of(flight));
		when(mockFlightRepository.save(any(Flight.class))).thenReturn(flight);
		doNothing().when(ticketRepository).deleteById(1L);
		
		mockMvc.perform(delete("/ticket/cancel/1"))
				.andExpect(status().is2xxSuccessful());

		verify(ticketRepository, times(1)).findById(1L);
		verify(mockFlightRepository, times(1)).findById(1L);
		verify(mockFlightRepository, times(1)).save(any(Flight.class));
		verify(ticketRepository, times(1)).deleteById(1L);

	}
	
	/*************END: MockMvc Tests****************************************************/
	
	/*************START: RestTemplate Tests*********************************************/
	@DisplayName("Purchasing ticket (flight not found OR flight capacity is full, HTTP 400 is expected)")
	@Test
    public void purchase_400() throws JSONException {
		Company company = new Company(1, "Pegasus Hava Yolları", "Pegasus Hava Taşımacılığı A.Ş.");
		Flight flight = new Flight(2L,company,new Rout(1L), 50, 12, 120, "₺", "New Flight");
		Ticket ticket = new Ticket(1L,flight,115, "₺","TH7402 nolu uçuş 1.bileti");
		
        ResponseEntity<ErrorResult> response = restTemplate.postForEntity("/ticket/purchase", ticket, ErrorResult.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(messageSource. getMessage("ticket.flight.notfound",null,Locale.getDefault()),
        		response.getHeaders().get("flight").get(0));
	}
	/*************END: RestTemplate Tests*********************************************/
}
