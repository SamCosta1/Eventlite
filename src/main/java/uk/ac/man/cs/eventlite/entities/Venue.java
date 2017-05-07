package uk.ac.man.cs.eventlite.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

@Entity
@Table(name = "venues")
public class Venue implements Comparable<Venue> {
	
	private static GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyAYnjIj_qYlWudV2gAnr8PuS_Ix-XZPCQY");
	
	@Id
	@GeneratedValue
	private long id;
	
	@NotBlank
	@Size(max = 256, message = "Name too long, must be less than 256 characters") 
	private String name;

	@Min(1)
	private int capacity;

	private String addressLine1;
	private String addressLine2;	
	
	@NotBlank	
	@Size(max = 300, message = "Address too long, must be less than 300 characters") 
	private String streetName;
	
	private String city;
	
	@NotBlank
	private String postcode;
	
	private double longitude;
	
	private double latitude;

	public Venue(String name, int capacity, String addressLine1, String addressLine2, 
			     String streetName, String city, String postcode) {
		this.name = name;
		this.capacity = capacity;
		this.postcode = postcode;
		this.streetName = streetName;
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.city = city;				
		this.setCoords();
	}

	public Venue() {

	}

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

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
		this.setCoords();
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
			String address = getParam(addressLine1) + getParam(addressLine2) + 
						     getParam(streetName) + getParam(city) + postcode;
			GeocodingResult[] results = GeocodingApi.geocode(context, address).await();
			
			if (results.length <= 0)
				return;
				
			this.longitude = results[0].geometry.location.lng;
			this.latitude = results[0].geometry.location.lat;
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private String getParam(String param) {
		if (param == null || param.trim().equals(""))
			return "";
		else
			return param + ",";
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

	/* Returns true if google maps could find the venue.
	 * (technically (0,0) is valid, but venues are unlikely to be in the middle of the atlantic) 
	 */
	public boolean hasCoordinates() {
		return this.longitude != 0 && this.latitude != 0;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
		this.setCoords();
	}

	public String getAddressLine1() {
		return addressLine1;
	}
	
	public void setStreetName(String street) {
		this.streetName = street;
		this.setCoords();
	}

	public String getStreetName() {
		return this.streetName;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
		this.setCoords();
	}

}