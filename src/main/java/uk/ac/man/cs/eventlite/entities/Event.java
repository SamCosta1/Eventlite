package uk.ac.man.cs.eventlite.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name = "events")
public class Event {

	@Id
	@GeneratedValue
	private long id;
	
	@Temporal(TemporalType.TIMESTAMP)	
	private Date date;

	private String name;

	@ManyToOne
	private Venue venue;

	public Event(String name, Venue venue, Date date) {
		this.name = name;
		this.venue = venue;
		this.date = date;
	}
	
	public Event() {
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Venue getVenue() {
		return venue;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}
}
