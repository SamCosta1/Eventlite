package uk.ac.man.cs.eventlite.dao;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.man.cs.eventlite.TestParent;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

public class EventServiceTest extends TestParent {

	@Autowired
	private EventService eventService;
	
	@Autowired
	private VenueService venueService;
	
	public Venue testVenue;
	
	@Before
	public void setup() {
		testVenue = new Venue();
		testVenue.setName("Test Event Name");
		testVenue.setCapacity(10);
		
		// Need to save the venue to ensure tests don't error
		venueService.save(testVenue);
	}

	@Test
	public void findAll() {
		List<Event> events = (List<Event>) eventService.findAll();
		long count = eventService.count();

		assertThat("findAll should get all events.", count, equalTo((long) events.size()));
	}
	
	@Test
	public void testAscendingByDate() {
		eventService.save(new Event("Java Lecture", testVenue, new Date(321)));
		eventService.save(new Event("Concert", testVenue, new Date(123)));
		
		List<Event> events = (List<Event>) eventService.findAll();
		boolean correctOrder = false;
		int count = 0;
		for (Event event : events) {
			String current = event.getName();
			if (current == "Concert" && count == 0)
				correctOrder = true;
			count++;
		}
		
		assertTrue("Concert should be first in the list", correctOrder);
	}
	
	@Test
	public void count() {
		Event event = new Event("Test Event", testVenue, new Date());
		
		long initialCount = eventService.count();
		eventService.save(event);
		
		assertThat("Count should increase by one on save", initialCount + 1, equalTo(eventService.count()));		
		
	}
	
	@Test
	public void save() {
		Event event = new Event("Test Event2", testVenue, new Date());
		eventService.save(event);
		
		List<Event> events = (List<Event>) eventService.findAll();
		
		boolean found = false;
		for (Event e : events)
			// Should be sufficient to check events equal, checking venues to ensure links correct
			if (e.equals(event) && e.getVenue().equals(testVenue))
				found = true;			
	
		
		assertTrue("Saved event was saved", found);
		
	}
}
