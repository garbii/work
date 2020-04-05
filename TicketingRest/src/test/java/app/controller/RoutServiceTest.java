package app.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.data.IRoutRepository;
import app.model.Airport;
import app.model.Rout;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(RoutService.class)
@TestInstance(Lifecycle.PER_CLASS)
public class RoutServiceTest {

	@MockBean
	private IRoutRepository routRepository;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	ObjectMapper om;

	@Autowired
	WebApplicationContext context;
	
	/*************START: SETUP settings..***********************************************/
//	@BeforeAll
//	public void setUp() {
//		 mockMvc = MockMvcBuilders
//			        .webAppContextSetup(context)
//			        .addFilter((request, response, chain) -> {
//			          response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
//			          chain.doFilter(request, response);
//			        }) .build();
//	}
	/*************END: SETUP settings..*************************************************/
	
	/*************START: MockMvc Tests**************************************************/
	@DisplayName("Searching rout by id is successful")
	@Test
	public void findById() throws Exception {
		Airport fromAirport = new Airport(1, "Adana Havalimanı", "ADA");
		Airport toAirport = new Airport(1, "İzmir Havalimanı", "IZM");
		Rout rout = new Rout(1L, fromAirport, toAirport, "Adana-İzmir rotası");
		when(routRepository.findById(1L)).thenReturn(Optional.of(rout));
		mockMvc.perform(get("/rout/1"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.fromAirport.name", is("Adana Havalimanı")))
				.andExpect(jsonPath("$.toAirport.code", is("IZM")))
				.andExpect(ResponseBodyMatchers.responseBody().containsObjectAsJson(rout, Rout.class));

	    verify(routRepository, times(1)).findById(1L);

	}

	@DisplayName("Finding all routs is successful")
	@Test
	public void findAll() throws Exception {
		Airport fromAirport = new Airport(1, "Adana Havalimanı", "ADA");
		Airport toAirport = new Airport(1, "İzmir Havalimanı", "IZM");
		Rout rout1 = new Rout(1L, fromAirport, toAirport, "Adana-İzmir rotası");
		Rout rout2 = new Rout(2L, toAirport, fromAirport, "İzmir-Adana rotası");
		List<Rout> routs = Arrays.asList(rout1,rout2);
		when(routRepository.findAll()).thenReturn(routs);
		mockMvc.perform(get("/rout").contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].fromAirport.name", is("Adana Havalimanı")))
				.andExpect(jsonPath("$[0].fromAirport.code", is("ADA")))
				.andExpect(jsonPath("$[1].id", is(2)))
				.andExpect(jsonPath("$[1].fromAirport.name", is("İzmir Havalimanı")))
				.andExpect(jsonPath("$[1].fromAirport.code", is("IZM")));

		verify(routRepository, times(1)).findAll();

	}

	@DisplayName("Adding rout is successful")
	@Test
	public void add() throws Exception {
		Airport fromAirport = new Airport(1, "Adana Havalimanı", "ADA");
		Airport toAirport = new Airport(1, "İzmir Havalimanı", "IZM");
		Rout rout = new Rout(1L, fromAirport, toAirport, "Adana-İzmir rotası");
		when(routRepository.save(any(Rout.class))).thenReturn(rout);
		mockMvc.perform(post("/rout/add").content(om.writeValueAsString(rout))
						    .header(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_JSON))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.fromAirport.name", is("Adana Havalimanı")))
				.andExpect(jsonPath("$.fromAirport.code", is("ADA")));

		verify(routRepository, times(1)).save(any(Rout.class));

	}
	/*************END: MockMvc Tests****************************************************/
}
