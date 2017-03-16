package uk.ac.man.cs.eventlite.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.Transient;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;


@Entity
@Table(name = "events")
public class Event {

	@Id
	@GeneratedValue
	private long id;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private Date date;

	private String name;
	
	@Lob
	@Column( length = 100000 )
	private String description;

	@ManyToOne
	private Venue venue;
	
	@Transient
	private boolean pastEvent;

	public Event(String name, Venue venue, Date date, String description) {
		this.name = name;
		this.venue = venue;
		this.date = date;
		this.description = description;
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

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Venue getVenue() {
		return venue;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}
	
	public boolean equals(Event other) {
		return this.id == other.getId();
	}
	
	public String toString() {
		return "ID: " + id + " Name: " + name + " Date: " + date + " Venue: " + venue.toString();
	}
	
	public boolean isPastEvent() {
		return this.pastEvent;
	}
	
	@PostLoad
	private void onLoad() {
		this.pastEvent = date.before(new Date());
	}
}
