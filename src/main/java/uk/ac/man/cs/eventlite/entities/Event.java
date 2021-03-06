package uk.ac.man.cs.eventlite.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Transient;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "events")
public class Event {

	@Id
	@GeneratedValue
	private long id;

	@Future
	@NotNull (message = "May not be empty")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date date;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@Temporal(TemporalType.TIME)
	@DateTimeFormat(pattern = "HH:mm")
	private Date time;

	@NotBlank (message = "May not be empty")
	@Size(max = 256, message = "Name too long, must be less than 256 characters")
	private String name;

	@Lob
	@Size(max = 500, message = "Description must have at most 500 characters")
	@Column(length = 100000)
	private String description;

	@NotNull (message = "May not be empty")
	@ManyToOne
	private Venue venue;

	@ManyToOne
	private User user;

	@Transient
	private boolean pastEvent;

	public Event(String name, Venue venue, Date date, Date time, String description) {
		this.name = name;
		this.venue = venue;
		this.date = date;
		this.time = time;
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

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Venue getVenue() {
		return venue;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}

	public boolean equals(Event other) {
		return this.id == other.getId()
				&& this.name == other.name
				&& this.venue.equals(other.venue)
				&& this.date.equals(other.date)
				&& this.time.equals(other.time)
				&& ((this.description == null && other.description == null) 
				   || this.description.equals(other.description));
	}

	public boolean equalsIgnoreId(Event other) {
		return  this.name == other.name
				&& this.venue.equals(other.venue)
				&& this.date.equals(other.date)
				&& this.time.equals(other.time)
				&& this.description.equals(other.description);
	}

	public String toString() {
		return "ID: " + id + " Name: " + name + " Date: " + date 
			   + " Time: " + time + " Description: " + description 
			   + " Venue: [" + venue.toString() + "]";
	}

	public boolean isPastEvent() {
		return date.before(new Date());
	}

}