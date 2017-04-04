package uk.ac.man.cs.eventlite.dao;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.man.cs.eventlite.entities.Venue;
import uk.ac.man.cs.eventlite.TestParent;


public class VenueServiceTest extends TestParent{

	@Autowired
	private VenueService venueService;

	@Test
	public void findAllTest() {
		String address = "Kilburn Building, University of Manchester, Oxford Rd, Manchester";
		String postcode = "M13 9PL";
		venueService.save(new Venue("z", 10, address, postcode));	
		venueService.save(new Venue("x", 10, address, postcode));
		venueService.save(new Venue("a", 10, address, postcode));	
		venueService.save(new Venue("b", 10, address, postcode));	
		
		List<Venue> venues = (List<Venue>) venueService.findAll();
		long count = venueService.count();

		assertThat("findAll should get all venues.", count, equalTo((long) venues.size()));
		testListInOrder(venues);
	}
	
	@Test
	public void findAllExceptOne() {
		String address = "Kilburn Building, University of Manchester, Oxford Rd, Manchester";
		String postcode = "M13 9PL";
		Venue ignoredEvent = new Venue("name1", 100, address, postcode);
		venueService.save(ignoredEvent);
				
		Iterable<Venue> venues = venueService.findAllExceptOne(ignoredEvent);
		for (Venue v : venues) 
			assertFalse(v.equals(ignoredEvent));		
	}
	
	@Test
	public void testCount() {
		String address = "Kilburn Building, University of Manchester, Oxford Rd, Manchester";
		String postcode = "M13 9PL";
		Venue newVenue = new Venue("name1", 100, address, postcode);
		
		long initialCount = venueService.count();
		venueService.save(newVenue);
				
		assertThat("Count should increase by one on save", initialCount + 1, equalTo(venueService.count()));		
		
	}
	
	@Test
	public void testSearchByName() {
		String address = "Kilburn Building, University of Manchester, Oxford Rd, Manchester";
		String postcode = "M13 9PL";
		venueService.save(new Venue("d Test Venue 1", 10, address, postcode));	
		venueService.save(new Venue("b test venue 2", 10, address, postcode));
		venueService.save(new Venue("a test Venue", 10, address, postcode));	
		venueService.save(new Venue("f Another random string", 10, address, postcode));	
		
		String searchTerm = "test Venue";		
		List<Venue> venues = (List<Venue>) venueService.searchByName(searchTerm);	
		
		for (Venue v : venues) 			
			assertTrue("Names contain substring 'test venue' - case insensitive"
						, v.getName().toLowerCase().contains(searchTerm.toLowerCase()));			
	
		assertThat("Three items returned: ", 3, equalTo(venues.size()));
		testListInOrder(venues);
		
	}
	
	@Test
	public void testSave() {

		String address = "Kilburn Building, University of Manchester, Oxford Rd, Manchester";
		String postcode = "M13 9PL";
		long previousCount = venueService.count();
		
		Venue newVenue = new Venue("name1", 100, address, postcode);
		venueService.save(newVenue);
		
		long newCount = venueService.count();
		
		boolean found = false;
		
		List<Venue> venues = (List<Venue>) venueService.findAll();
		for (Venue v: venues){
			if (v.equals(newVenue))
				found=true;
		}
		
		assertTrue(found);
		assertThat("Count should have risen after save.", newCount, equalTo(previousCount+1));
	}
	
	@Test
	public void testFindById() {
		String address = "Kilburn Building, University of Manchester, Oxford Rd, Manchester";
		String postcode = "M13 9PL";
		Venue venue = new Venue("test Venue", 1000, address, postcode);
		venueService.save(venue);
		
		Venue foundVenue = venueService.findById(venue.getId());	

		assertTrue("The find by Id method found the correct venue", foundVenue.equals(venue));
		
	}
	
	// Helper method for checking a result set is in correct order
	// Works by sorting the elements into the correct order
	// then check both lists are the same, i.e. the original list was
	// Correct in the first place
	private void testListInOrder(List<Venue> venues) {
		List<Venue> listInOrder = new ArrayList<Venue>(venues);
		
		Collections.sort(listInOrder, new Comparator<Venue>() {
			@Override
			public int compare(Venue v1, Venue v2) {					
				return v1.getName().compareTo(v2.getName());
			}			
		});
		
		Iterator<Venue> iterator = venues.iterator();
		for (Venue v: listInOrder)
			assertTrue(v.equals(iterator.next()));
	}

}
