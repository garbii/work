package app.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Entity
public class Company {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private final long id;
	@NotNull(message = "{company.name.empty}")
	private final String name;
	private final String ack;
	private Date createdAt;

	@PrePersist
	public void setCreatedDate() {
		createdAt = new Date();
	}

}
