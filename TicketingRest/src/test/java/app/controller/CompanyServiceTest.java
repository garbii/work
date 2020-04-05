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

import app.data.ICompanyRepository;
import app.exception.ControllerExceptionHandler.ErrorResult;
import app.model.Company;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompanyServiceTest {

	@MockBean
	private ICompanyRepository companyRepository;

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
	@DisplayName("Searching company by id is successful")
	@Test
	public void findById() throws Exception {
		Company ataCompany = new Company(1, "Pegasus Hava Yolları", "Pegasus Hava Taşımacılığı A.Ş.");
		when(companyRepository.findById(1L)).thenReturn(Optional.of(ataCompany));
		mockMvc.perform(get("/company/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.name", is("Pegasus Hava Yolları")))
				.andExpect(jsonPath("$.ack", is("Pegasus Hava Taşımacılığı A.Ş.")));

		verify(companyRepository, times(1)).findById(1L);

	}

	@DisplayName("Finding all companys is successful")
	@Test
	public void findAll() throws Exception {
		List<Company> companies = Arrays.asList(
				new Company(1, "Pegasus Hava Yolları", "Pegasus Hava Taşımacılığı A.Ş."),
				new Company(2, "Türk Hava Yolları", "Türk Hava Yolları Anonim Ortaklığı"));
		when(companyRepository.findAll()).thenReturn(companies);
		mockMvc.perform(get("/company").contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].name", is("Pegasus Hava Yolları")))
				.andExpect(jsonPath("$[0].ack", is("Pegasus Hava Taşımacılığı A.Ş.")))
				.andExpect(jsonPath("$[1].id", is(2))).andExpect(jsonPath("$[1].name", is("Türk Hava Yolları")))
				.andExpect(jsonPath("$[1].ack", is("Türk Hava Yolları Anonim Ortaklığı")));

		verify(companyRepository, times(1)).findAll();

	}

	@DisplayName("Adding company is successful")
	@Test
	public void add() throws Exception {
		Company ataCompany = new Company(1, "Pegasus Hava Yolları", "Pegasus Hava Taşımacılığı A.Ş.");
		when(companyRepository.save(any(Company.class))).thenReturn(ataCompany);
		mockMvc.perform(post("/company/add").content(om.writeValueAsString(ataCompany)).header(HttpHeaders.CONTENT_TYPE,
				MediaType.APPLICATION_JSON))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.name", is("Pegasus Hava Yolları")))
				.andExpect(jsonPath("$.ack", is("Pegasus Hava Taşımacılığı A.Ş.")));

		verify(companyRepository, times(1)).save(any(Company.class));

	}
	/*************END: MockMvc Tests****************************************************/
	
	/*************START: RestTemplate Tests*********************************************/
	@DisplayName("Adding company(company name is null, HTTP 400 is expected)")
	@Test
    public void save_company_name_null_400() throws JSONException {
		
		Company company = new Company(1, null, "Pegasus Hava Yolları");
        ResponseEntity<ErrorResult> response = restTemplate.postForEntity("/company/add", company, ErrorResult.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("name", response.getBody().getFieldErrors().get(0).getField());
        assertEquals(messageSource. getMessage("company.name.empty",null,Locale.getDefault()), response.getBody().getFieldErrors().get(0).getMessage());
    }
	/*************END: RestTemplate Tests*********************************************/
}
