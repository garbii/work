package app.controller;

import java.util.Optional;

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

import app.data.IRoutRepository;
import app.model.Rout;

@RestController
@RequestMapping(path = "/rout", produces = "application/json")
@CrossOrigin(origins = "*")
public class RoutService {

	@Autowired
	private IRoutRepository routRepository;

	@GetMapping
	public Iterable<Rout> getAll() {
		return routRepository.findAll();
	}

	@PostMapping(path = "/add", consumes = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public Rout addRout(@RequestBody Rout rout) {
		return routRepository.save(rout);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Rout> findById(@PathVariable("id") Long id) {
		Optional<Rout> optRout = routRepository.findById(id);
		if (optRout.isPresent()) {
			return new ResponseEntity<>(optRout.get(), HttpStatus.OK);
		}
		return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
	}

}
