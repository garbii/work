package app.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class TicketDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	private Ticket ticket;
	private String message;
}
