package app.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Entity
public class Airport {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private final long id;
	@NotNull(message = "{airport.name.empty}")
	private final String name;
	@NotNull(message = "{airport.code.empty}")
	@Size(max = 5, min = 3, message = "{airport.code.size}")
	private final String code;
	private Date createdAt;

	@PrePersist
	public void setCreatedDate() {
		createdAt = new Date();
	}
}
