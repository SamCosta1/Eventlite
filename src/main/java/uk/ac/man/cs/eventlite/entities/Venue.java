package uk.ac.man.cs.eventlite.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name="venues")
public class Venue {
	@Id
	@GeneratedValue
	private long id;
	
	@NotBlank
	@Size(max = 256, message = "Name must not be greater than 256 characters.")
	private String name;
	
	@NotBlank
	private String address;
	
	@NotNull
	@Min(value = 0, message = "The capacity must be a positive number.")
	private int capacity;

	public Venue(String name, int capacity, String address) {
		this.name = name;
		this.capacity = capacity;
		this.address = address;
	}
	
	public Venue() {}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public boolean equals(Venue other) {
		return this.id == other.getId();
	}
	
	public String toString() {
		return "ID: " + id + " Name: " + name + " Capacity: " + capacity;
	}
}
