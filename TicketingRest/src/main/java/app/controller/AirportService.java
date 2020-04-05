package app.controller;

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

import app.data.IAirportRepository;
import app.model.Airport;

@RestController
@RequestMapping(path = "/airport", produces = "application/json")
@CrossOrigin(origins = "*")
public class AirportService {

	@Autowired
	private IAirportRepository airportRepository;

	@GetMapping
	public Iterable<Airport> findAll() {
		return airportRepository.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Airport> findById(@PathVariable("id") Long id) {
		Optional<Airport> optAirport = airportRepository.findById(id);
		if (optAirport.isPresent()) {
			return new ResponseEntity<>(optAirport.get(), HttpStatus.OK);
		}
		return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
	}

	@PostMapping(path = "/add", consumes = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Airport> addAirport(@Valid @RequestBody Airport airport) {
		Airport newAirport = airportRepository.save(airport);
		return new ResponseEntity<Airport>(newAirport, HttpStatus.OK);
	}

}
