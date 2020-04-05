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
public class Rout {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private final long id;
	@OneToOne(targetEntity = Airport.class)
	private Airport fromAirport;
	@OneToOne(targetEntity = Airport.class)
	private Airport toAirport;
	private String ack;
	private Date createdAt;

	@PrePersist
	public void setCreatedDate() {
		createdAt = new Date();
	}

	public Rout(long id, Airport fromAirport, Airport toAirport, String ack) {
		super();
		this.id = id;
		this.fromAirport = fromAirport;
		this.toAirport = toAirport;
		this.ack = ack;
	}

}
