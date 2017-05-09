package uk.ac.man.cs.eventlite.entites;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.ac.man.cs.eventlite.TestParent;
import uk.ac.man.cs.eventlite.entities.Venue;

public class VenueEntityTest extends TestParent {
		
	private Venue venue;
	private String name = "test";
	private int capacity = 100;
	private String addressLine1 = "Kilburn Building";
	private String addressLine2 = "University of Manchester";
	private String streetName = "Oxford Rd";
	private String city = "Manchester";
	private String postcode = "M13 9PL";
	
	@Before
	public void setup() {
		venue = new Venue(name, capacity, addressLine1, addressLine2, streetName, city, postcode);		
	}
	@Test
	public void testVenueCreation () {
		
		assertEquals("Venue should have the given name", name, venue.getName());
		assertEquals("Venue should have the given capacity", capacity, venue.getCapacity());
		assertEquals("Venue should have the given address", addressLine1, venue.getAddressLine1());
		assertEquals("Venue should have the given address", addressLine2, venue.getAddressLine2());
		assertEquals("Venue should have the given street", streetName, venue.getStreetName());
		assertEquals("Venue should have the given city", city, venue.getCity());
		assertEquals("Venue should have the given name", postcode, venue.getPostcode());
		
	}
	
	@Test
	public void testVenueCoordsGeneration() {
		String name = "test";
		int capacity = 100;
		String addressLine1 = "Kilburn Building";
		String addressLine2 = "University of Manchester";
		String streetName = "Oxford Rd";
		String city = "Manchester";
		String postcode = "M13 9PL";
		double trueLng = -2.2340865;
		double trueLat = 53.46722639999999;
		Venue created = new Venue(name, capacity, addressLine1, addressLine2, streetName, city, postcode);
		
		assertEquals("Venue should have the correct longitude", trueLng, created.getLongitude(),  0.001);
		assertEquals("Venue should have the correct latitude", trueLat, created.getLatitude(),  0.001);		
	}
	
	@Test
	public void testSetCoords() {
		
		double trueLng = -2.2340865;
		double trueLat = 53.46722639999999;
		
		assertEquals("Venue should have the correct longitude", trueLng, venue.getLongitude(),  0.001);
		assertEquals("Venue should have the correct latitude", trueLat, venue.getLatitude(),  0.001);		
	}
}