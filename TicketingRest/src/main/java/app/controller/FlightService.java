package app.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import app.data.IFlightRepository;
import app.data.ITicketRepository;
import app.model.Flight;
import app.model.Ticket;

@RestController
@RequestMapping(path = "/flight", produces = "application/json")
@CrossOrigin(origins = "*")
public class FlightService {

	@Autowired
	private IFlightRepository flightRepository;

	@Autowired
	private ITicketRepository ticketRepository;

	@GetMapping
	public Iterable<Flight> getAll() {
		return flightRepository.findAll();
	}

	@PostMapping(path = "/add", consumes = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public Flight addFlight(@Valid @RequestBody Flight flight) {
		return flightRepository.save(flight);
	}

	@GetMapping("/{id}/tickets")
	public List<Ticket> findFlightTicekts(@PathVariable("id") Long id) {
		return ticketRepository.findByFlightId(id);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Flight> findById(@PathVariable("id") Long id) {
		Optional<Flight> optFlight = flightRepository.findById(id);
		if (optFlight.isPresent()) {
			return new ResponseEntity<Flight>(optFlight.get(), HttpStatus.OK);
		}
		return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
	}

	@DeleteMapping("/delete/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void deleteOrder(@PathVariable("id") Long id) {
		try {
			flightRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
		}
	}

}
