package uk.ac.man.cs.eventlite.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

@Entity
@Table(name="venues")
public class Venue implements Comparable<Venue> {
	
	private static GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyAYnjIj_qYlWudV2gAnr8PuS_Ix-XZPCQY");
	
	@Id
	@GeneratedValue
	private long id;
	
	private String name;
	
	private String address;
	
	private String postcode;

	private int capacity;
	
	private double longitude;
	
	private double latitude;

	public Venue(String name, int capacity, String address, String postcode) {
		this.name = name;
		this.capacity = capacity;
		this.address = address;
		this.postcode = postcode;
		try {
			GeocodingResult[] results = GeocodingApi.geocode(context, address + ", " + postcode).await();
			this.longitude = results[0].geometry.location.lng;
			this.latitude = results[0].geometry.location.lat;
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(this.longitude + " " + this.latitude);
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
	
	public String getPostcode() {
		return postcode;
	}
	
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	
	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setCoords() {
		try {
			GeocodingResult[] results = GeocodingApi.geocode(context, address + ", " + postcode).await();
			this.longitude = results[0].geometry.location.lng;
			this.latitude = results[0].geometry.location.lat;
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(this.longitude + " " + this.latitude);
		
	}
	
	public boolean equals(Venue other) {
		return this.id == other.getId();
	}
	
	public String toString() {
		return "ID: " + id + " Name: " + name + " Capacity: " + capacity;
	}
	
	@Override
	public int compareTo(Venue other) {					
		return this.getName().compareTo(other.getName());
	}

	
}
