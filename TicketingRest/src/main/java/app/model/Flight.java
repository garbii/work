package app.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;

import org.hibernate.validator.constraints.Range;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Entity
public class Flight {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private final long id;
	@OneToOne(targetEntity = Company.class)
	private Company company;
	@OneToOne(targetEntity = Rout.class)
	private Rout rout;
	@Range(min = 10, max = 300, message = "{flight.capacity.range}")
	private int capacity;
	private int soldTicketCount;
	private double price;
	private String currency;
	private String ack;
	private Date createdAt;

	@PrePersist
	public void setCreatedDate() {
		createdAt = new Date();
	}

	public Flight(long id, Company company, Rout rout,
			@Range(min = 10, max = 300, message = "{flight.capacity.range}") int capacity, int soldTicketCount,
			double price, String currency, String ack) {
		super();
		this.id = id;
		this.company = company;
		this.rout = rout;
		this.capacity = capacity;
		this.soldTicketCount = soldTicketCount;
		this.price = price;
		this.currency = currency;
		this.ack = ack;
	}

}
