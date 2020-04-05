package app.data;

import org.springframework.data.repository.CrudRepository;

import app.model.Airport;

public interface IAirportRepository extends CrudRepository<Airport, Long>{

}
