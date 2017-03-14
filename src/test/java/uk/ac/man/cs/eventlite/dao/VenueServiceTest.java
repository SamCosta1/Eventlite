package uk.ac.man.cs.eventlite.dao;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

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
		
		List<Venue> venues = (List<Venue>) venueService.findAll();
		long count = venueService.count();

		assertThat("findAll should get all venues.", count, equalTo((long) venues.size()));
	}
	
	@Test
	public void findAllExceptOne() {
		Venue ignoredEvent = new Venue("name1", 100);
		venueService.save(ignoredEvent);
		boolean found = false;
		
		Iterable<Venue> venues = venueService.findAllExceptOne(ignoredEvent);
		for (Venue v : venues) {
			if (v.equals(ignoredEvent))
				found = true;
		}
		
		assertFalse(found);
	}
	
	@Test
	public void count() {
		Venue newVenue = new Venue("name1", 100);
		
		long initialCount = venueService.count();
		venueService.save(newVenue);
				
		assertThat("Count should increase by one on save", initialCount + 1, equalTo(venueService.count()));		
		
	}
	
	@Test
	public void testSearchByName() {
		venueService.save(new Venue("d Test Venue 1", 10));	
		venueService.save(new Venue("b test venue 2", 10));
		venueService.save(new Venue("a test Venue", 10));	
		venueService.save(new Venue("f Another random string", 10));	
		
		String searchTerm = "test Venue";
		
		List<Venue> venues = (List<Venue>) venueService.searchByName(searchTerm);
		
		// Check each correct and in order	
		boolean inOrder = true;
		String previous = null;
		for (Venue v : venues) {
			
			assertTrue("Names contain substring 'test venue' - case insensitive"
						, v.getName().toLowerCase().contains(searchTerm.toLowerCase()));
			
			if (previous != null)
				inOrder = v.getName().compareTo(previous) < 0 ? false : inOrder; 
			previous = v.getName();
		}
		
		assertTrue("Venues in alphabetical order by name", inOrder);
		// Check all matching ones returned 
		assertThat("Three items returned: ", 3, equalTo(venues.size()));
		
	}
	
	@Test
	public void saveTest() {

		long previousCount = venueService.count();
		
		Venue newVenue = new Venue("name1", 100);
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

}
