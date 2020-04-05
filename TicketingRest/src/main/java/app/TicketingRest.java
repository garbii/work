package app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import app.data.IAirportRepository;
import app.data.ICompanyRepository;
import app.data.IFlightRepository;
import app.data.IRoutRepository;
import app.data.ITicketRepository;
import app.model.Airport;
import app.model.Company;
import app.model.Flight;
import app.model.Rout;
import app.model.Ticket;

@SpringBootApplication
public class TicketingRest {

	public static void main(String[] args) {
		SpringApplication.run(TicketingRest.class, args);
	}

	@Bean
	@Profile("dev")
	public CommandLineRunner dataLoader(IAirportRepository airportRepository, ICompanyRepository companyRepository,
			IRoutRepository routRepository, IFlightRepository flightRepository, ITicketRepository ticketRepository) {
		return new CommandLineRunner() {
			@Override
			public void run(String... args) throws Exception {

				Airport ataAirport = new Airport(1, "Atatürk Havalimanı", "ATA");
				Airport sawAirport = new Airport(2, "Sabiha Gökçen Havalimanı", "SAW");
				Airport izmAirport = new Airport(3, "İzmir", "IZM");
				Airport adaAirport = new Airport(4, "Adana", "ADA");

				airportRepository.save(ataAirport);
				airportRepository.save(sawAirport);
				airportRepository.save(izmAirport);
				airportRepository.save(adaAirport);

				Company pegCompany = new Company(1, "Pegasus A.Ş.", "Pegasus Havayolları");
				Company thyCompany = new Company(2, "THY A.Ş.", "Türk Havayolları");

				companyRepository.save(pegCompany);
				companyRepository.save(thyCompany);

				Rout ataToIzm = new Rout(1, ataAirport, izmAirport, "Atatürk-İzmir rotası.");
				routRepository.save(ataToIzm);

				Flight pgsFlight = new Flight(1, pegCompany, ataToIzm, 100, 1, 100,"₺",
						"13:30 Atatürk-İzmir seferi (Pegasus hava yolları)");
				flightRepository.save(pgsFlight);

				ticketRepository.save(new Ticket(1, pgsFlight, 100, "₺","Atatürk-İzmir seferi."));

			}
		};
	}

}
