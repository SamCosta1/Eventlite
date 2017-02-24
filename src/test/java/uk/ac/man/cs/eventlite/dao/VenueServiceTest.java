package uk.ac.man.cs.eventlite.dao;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.man.cs.eventlite.entities.Event;
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
	public void count() {
		Venue newVenue = new Venue();
		newVenue.setName("name1");
		newVenue.setCapacity(100);
		
		long initialCount = venueService.count();
		venueService.save(newVenue);
				
		assertThat("Count should increase by one on save", initialCount + 1, equalTo(venueService.count()));		
		
	}
	
	@Test
	public void saveTest() {

		long previousCount = venueService.count();
		
		Venue newVenue = new Venue();
		newVenue.setName("name1");
		newVenue.setCapacity(100);
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
