package app.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Entity
public class Ticket {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private final long id;
	@OneToOne(targetEntity = Flight.class)
	private Flight flight;
	private double price;
	private String currency;
	private String ack;
	private Date createdAt;

	@PrePersist
	public void setCreatedDate() {
		createdAt = new Date();
	}

	public Ticket(long id, Flight flight, double price, String currency, String ack) {
		super();
		this.id = id;
		this.flight = flight;
		this.price = price;
		this.currency = currency;
		this.ack = ack;
	}

}
