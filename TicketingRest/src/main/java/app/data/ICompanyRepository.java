package app.data;

import org.springframework.data.repository.CrudRepository;

import app.model.Company;

public interface ICompanyRepository extends CrudRepository<Company, Long>{

}
