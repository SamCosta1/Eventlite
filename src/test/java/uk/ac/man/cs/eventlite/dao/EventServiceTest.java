package uk.ac.man.cs.eventlite.dao;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.man.cs.eventlite.TestParent;
import uk.ac.man.cs.eventlite.entities.Event;

public class EventServiceTest extends TestParent {

	@Autowired
	private EventService eventService;

	@Test
	public void findAll() {
		List<Event> events = (List<Event>) eventService.findAll();
		long count = eventService.count();

		assertThat("findAll should get all events.", count, equalTo((long) events.size()));
	}
	
	@Test
	public void testAscendingByDate() {
		eventService.save(new Event("Java Lecture", 15, new Date(321)));
		eventService.save(new Event("Concert", 18, new Date(123)));
		
		List<Event> events = (List<Event>) eventService.findAll();
		boolean correctOrder = false;
		int count = 0;
		for (Event event : events)
		{
			String current = event.getName();
			if (current == "Concert" && count == 0)
				correctOrder = true;
			count++;
		}
		
		assertTrue("Concert should be first in the list", correctOrder);
	}
	
	@Test
	public void count() {
		Event event = new Event("Test Event", 10, new Date());
		
		long initialCount = eventService.count();
		eventService.save(event);
		
		assertThat("Count should increase by one on save", initialCount + 1, equalTo(eventService.count()));		
		
	}
	
	@Test
	public void save() {
		Event event = new Event("Test Event2", 10, new Date());
		eventService.save(event);
		
		List<Event> events = (List<Event>) eventService.findAll();
		
		boolean found = false;
		for (Event e : events)
			if (e.getId() == event.getId())
				found = true;			
	
		assertTrue("Saved event was saved", found);
		
	}
}
