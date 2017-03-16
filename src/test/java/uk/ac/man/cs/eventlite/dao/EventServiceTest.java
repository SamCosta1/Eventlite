package uk.ac.man.cs.eventlite.dao;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
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
	
	public Venue testVenue, testVenue2;	

	@Before
	public void setup() {
		testVenue = new Venue(null, 0);
		testVenue.setName("Test Event Name");
		testVenue.setCapacity(10);
		
		testVenue2 = new Venue(null, 0);
		testVenue2.setName("Test Event Name 2");
		testVenue2.setCapacity(20);
		
		// Need to save the venue to ensure tests don't error
		venueService.save(testVenue);
		venueService.save(testVenue2);
	}

	@Test
	public void testFindAll() {
		Date date = new Date();
		eventService.save(new Event("a Test Event 1", testVenue, date, null));	
		eventService.save(new Event("f test event 2", testVenue, date, null));
		eventService.save(new Event("b test Event", testVenue, date, null));
		eventService.save(new Event("Java Lecture", testVenue, new Date(321),""));
		eventService.save(new Event("Concert", testVenue, new Date(123),""));
		eventService.save(new Event("Concert", testVenue, new Date(1),""));
		eventService.save(new Event("Concert", testVenue, new Date(500),""));
		
		List<Event> events = (List<Event>) eventService.findAll();

		long count = eventService.count();

		assertThat("findAll should get all events.", count, equalTo((long) events.size()));
		testListInOrder(events);
	}

	@Test
	public void testSearchByName() {
		Date date = new Date();
		eventService.save(new Event("a Test Event 1", testVenue, date, null));	
		eventService.save(new Event("f test event 2", testVenue, date, null));
		eventService.save(new Event("b test Event", testVenue, date, null));	
		eventService.save(new Event("Another random string", testVenue, date, null));	
		
		String searchTerm = "test Event";
		
		List<Event> events = (List<Event>) eventService.searchByName(searchTerm);
				
		for (Event e : events) 
			assertTrue("Names contain substring 'test event' - case insensitive"
						, e.getName().toLowerCase().contains(searchTerm.toLowerCase()));			
				
		assertThat("Three items returned: ", 3, equalTo(events.size()));
		testListInOrder(events);
		
	}
	
	@Test
	public void testDeleteEvent(){
		Event event = new Event("Test Event", testVenue, new Date(), null);
		eventService.save(event);
		
		long initialCount = eventService.count();
		
		eventService.delete(event);

		List<Event> events = (List<Event>) eventService.findAll();
		
		boolean isDeleted = true;
		for (Event e : events)
			if (e.equals(event) && e.getVenue().equals(testVenue))
				isDeleted = false;			
		
		assertTrue("Deleted event was deleted", isDeleted);
		assertThat("Count should decrease by one on delete", initialCount - 1, equalTo(eventService.count()));
		
	}
	
	@Test
	public void testCount() {
		Event event = new Event("Test Event", testVenue, new Date(),"");
		
		long initialCount = eventService.count();
		eventService.save(event);
		
		assertThat("Count should increase by one on save", initialCount + 1, equalTo(eventService.count()));		
		
	}
	
	@Test
	public void testSave() {
		Event event = new Event("Test Event2", testVenue, new Date(),"");
		eventService.save(event);
		
		List<Event> events = (List<Event>) eventService.findAll();
		
		boolean found = false;
		for (Event e : events)
			// Should be sufficient to check events equal, checking venues to ensure links correct
			if (e.equals(event) && e.getVenue().equals(testVenue))
				found = true;			
	
		
		assertTrue("Saved event was saved", found);
		
	}
	
	@Test
	public void testUpdate() {
		Event currentEvent = new Event("Test Event3", testVenue, new Date(), "");
		eventService.save(currentEvent);
		
		String newName = "Updated Event3";
		Date newDate = new Date(123);
		Event changedEvent = new Event(newName, testVenue2, newDate, "");
		
		eventService.update(currentEvent, changedEvent);
		
		boolean checkName, checkVenue, checkDate;
		checkName = checkVenue = checkDate = false;
		
		if (currentEvent.getName().equals(newName))
			checkName = true;

		if (currentEvent.getVenue().equals(testVenue2))
			checkVenue = true;
		
		if (currentEvent.getDate().equals(newDate))
			checkDate = true;
		
		assertTrue(checkName);
		assertTrue(checkVenue);
		assertTrue(checkDate);
	}
	
	// Helper method for checking a result set is in correct order
	// Works by sorting the elements into the correct order
	// then check both lists are the same, i.e. the original list was
	// Correct in the first place
	private void testListInOrder(List<Event> events) {
		List<Event> listInOrder = new ArrayList<Event>(events);
		Collections.sort(listInOrder, new Comparator<Event>() {

			@Override
			public int compare(Event e1, Event e2) {
				if (e1.getDate().equals(e2.getDate()))
					return e1.getName().compareTo(e2.getName());
		
				return e2.getDate().compareTo(e1.getDate());
			}			
		});
		
		Iterator<Event> iterator = events.iterator();
		for (Event e: listInOrder)
			assertTrue(e.equals(iterator.next()));
	}
	
}
