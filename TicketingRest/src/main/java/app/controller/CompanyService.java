package app.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import app.data.ICompanyRepository;
import app.data.IFlightRepository;
import app.model.Company;
import app.model.Flight;

@RestController
@RequestMapping(path = "/company", produces = "application/json")
@CrossOrigin(origins = "*")
public class CompanyService {

	@Autowired
	private ICompanyRepository companyRepository;

	@Autowired
	private IFlightRepository flightRepository;

	@GetMapping
	public Iterable<Company> findAll() {
		return companyRepository.findAll();
	}

	@PostMapping(path = "/add", consumes = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Company> addCompany(@Valid @RequestBody Company company) {
		Company addedCompany = companyRepository.save(company);
		return new ResponseEntity<Company>(addedCompany, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Company> findById(@PathVariable("id") Long id) {
		Optional<Company> optCompany = companyRepository.findById(id);
		if (optCompany.isPresent()) {
			return new ResponseEntity<>(optCompany.get(), HttpStatus.OK);
		}
		return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
	}

	@GetMapping("/{id}/flights")
	public ResponseEntity<List<Flight>> findAllFlight(@PathVariable("id") Long id) {
		Optional<Company> optCompany = companyRepository.findById(id);
		if (optCompany.isPresent()) {
			List<Flight> flights = flightRepository.findByCompanyId(id);
			return new ResponseEntity<>(flights, HttpStatus.OK);
		}
		return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
	}

}
