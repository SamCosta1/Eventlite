package uk.ac.man.cs.eventlite.controllers.venues;

import static org.junit.Assert.*;

import org.junit.Test;
import uk.ac.man.cs.eventlite.TestParent;
import uk.ac.man.cs.eventlite.entities.Venue;

public class VenueEntityTest extends TestParent {
		
	@Test
	public void testVenueCreation () {
		String name = "test";
		int capacity = 100;
		String address = "Kilburn Building, University of Manchester, Oxford Rd, Manchester";
		String postcode = "M13 9PL";
		Venue created = new Venue(name, capacity, address, postcode);
		
		assertEquals("Venue should have the given name", name, created.getName());
		assertEquals("Venue should have the given capacity", capacity, created.getCapacity());
		assertEquals("Venue should have the given address", address, created.getAddress());
		assertEquals("Venue should have the given name", postcode, created.getPostcode());
		
	}
	
	@Test
	public void testVenueCoordsGeneration() {
		String name = "test";
		int capacity = 100;
		String address = "Kilburn Building, University of Manchester, Oxford Rd, Manchester";
		String postcode = "M13 9PL";
		double trueLng = -2.2340865;
		double trueLat = 53.46722639999999;
		Venue created = new Venue(name, capacity, address, postcode);
		
		assertEquals("Venue should have the correct longitude", trueLng, created.getLongitude(),  0.001);
		assertEquals("Venue should have the correct latitude", trueLat, created.getLatitude(),  0.001);		
	}
	
	@Test
	public void testSetCoords() {
		Venue created = new Venue();
		created.setAddress("Kilburn Building, University of Manchester, Oxford Rd, Manchester");
		created.setPostcode("M13 9PL");
		created.setCoords();
		double trueLng = -2.2340865;
		double trueLat = 53.46722639999999;
		
		assertEquals("Venue should have the correct longitude", trueLng, created.getLongitude(),  0.001);
		assertEquals("Venue should have the correct latitude", trueLat, created.getLatitude(),  0.001);		
	}
}