package app.controller;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
import app.model.TicketDTO;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/ticket", produces = "application/json")
@CrossOrigin(origins = "*")
@Slf4j
public class TicketService {

	@Autowired
	private ITicketRepository ticketRepository;

	@Autowired
	private IFlightRepository flightRepository;

	@Autowired
	private MessageSource messageSource;

	@PostMapping(path = "/purchase", consumes = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<TicketDTO> purchaseTicket(@RequestBody Ticket ticket, Locale locale) {

		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<String, String>();
		Optional<Flight> optFlight = flightRepository.findById(ticket.getFlight().getId());
		TicketDTO ticketDTO = new TicketDTO();
		ticketDTO.setTicket(ticket);
		if (optFlight.isPresent()) {
			if (optFlight.get().getSoldTicketCount() < optFlight.get().getCapacity()) {
				ticket.setPrice(optFlight.get().getPrice());
				if (optFlight.get().getSoldTicketCount() != 0 && isTicketPrizeRais(optFlight.get())) {
					optFlight.get().setPrice(
							Math.round((optFlight.get().getPrice() + 0.1 * optFlight.get().getPrice()) * 100.0)
									/ 100.0);
				}
				optFlight.get().setSoldTicketCount(optFlight.get().getSoldTicketCount() + 1);
				flightRepository.save(optFlight.get());
				ticket.setFlight(optFlight.get());
				ticket.setAck(optFlight.get().getAck());
				ticket.setCurrency(optFlight.get().getCurrency());
				Ticket purchTicket = ticketRepository.save(ticket);
				ticketDTO.setTicket(purchTicket);
				ticketDTO.setMessage(messageSource.getMessage("ticket.purchase.success", null, locale));
				return new ResponseEntity<TicketDTO>(ticketDTO, HttpStatus.OK);
			} else {
				multiValueMap.put("flight", Arrays.asList(messageSource.getMessage("ticket.flight.capacity.status.full", null, locale)));
				ticketDTO.setMessage(messageSource.getMessage("ticket.flight.capacity.status.full", null, locale));
				return new ResponseEntity<TicketDTO>(ticketDTO, multiValueMap, HttpStatus.BAD_REQUEST);
			}
		} else {
			multiValueMap.put("flight", Arrays.asList(messageSource.getMessage("ticket.flight.notfound", null, locale)));
			ticketDTO.setMessage(messageSource.getMessage("ticket.flight.notfound", null, locale));
			return new ResponseEntity<TicketDTO>(ticketDTO, multiValueMap, HttpStatus.BAD_REQUEST);
		}
	}

	private boolean isTicketPrizeRais(Flight flight) {
		if (flight.getSoldTicketCount() < flight.getCapacity() * 0.1) {
			return (flight.getSoldTicketCount() + 1 >= flight.getCapacity() * 0.1);
		} else if (flight.getSoldTicketCount() < flight.getCapacity() * 0.2) {
			return (flight.getSoldTicketCount() + 1 >= flight.getCapacity() * 0.2);
		} else if (flight.getSoldTicketCount() < flight.getCapacity() * 0.3) {
			return (flight.getSoldTicketCount() + 1 >= flight.getCapacity() * 0.3);
		} else if (flight.getSoldTicketCount() < flight.getCapacity() * 0.4) {
			return (flight.getSoldTicketCount() + 1 >= flight.getCapacity() * 0.4);
		} else if (flight.getSoldTicketCount() < flight.getCapacity() * 0.5) {
			return (flight.getSoldTicketCount() + 1 >= flight.getCapacity() * 0.5);
		} else if (flight.getSoldTicketCount() < flight.getCapacity() * 0.6) {
			return (flight.getSoldTicketCount() + 1 >= flight.getCapacity() * 0.6);
		} else if (flight.getSoldTicketCount() < flight.getCapacity() * 0.7) {
			return (flight.getSoldTicketCount() + 1 >= flight.getCapacity() * 0.7);
		} else if (flight.getSoldTicketCount() < flight.getCapacity() * 0.8) {
			return (flight.getSoldTicketCount() + 1 >= flight.getCapacity() * 0.8);
		} else if (flight.getSoldTicketCount() < flight.getCapacity() * 0.9) {
			return (flight.getSoldTicketCount() + 1 >= flight.getCapacity() * 0.9);
		}
		return false;
	}

	@GetMapping("/{id}")
	public ResponseEntity<Ticket> findById(@PathVariable("id") Long id) {
		Optional<Ticket> optTicket = ticketRepository.findById(id);
		if (optTicket.isPresent()) {
			return new ResponseEntity<>(optTicket.get(), HttpStatus.OK);
		}
		return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
	}

	@DeleteMapping("/cancel/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void cancelTicket(@PathVariable("id") Long id) {
		try {
			Optional<Ticket> optTicket = ticketRepository.findById(id);
			Optional<Flight> optFlight = flightRepository.findById(optTicket.get().getId());
			optFlight.get().setSoldTicketCount(optFlight.get().getSoldTicketCount() - 1);
			flightRepository.save(optFlight.get());
			ticketRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			log.error("Coul not canceled ticket." + e.getMessage());
		}
	}

}
