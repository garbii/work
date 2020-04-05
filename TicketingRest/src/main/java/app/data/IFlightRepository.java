package app.data;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import app.model.Flight;

public interface IFlightRepository extends CrudRepository<Flight, Long>{

	public List<Flight> findByCompanyId(Long id);

}
