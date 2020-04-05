package app.data;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import app.model.Ticket;

public interface ITicketRepository extends CrudRepository<Ticket, Long> {

	List<Ticket> findByFlightId(Long id);

}
