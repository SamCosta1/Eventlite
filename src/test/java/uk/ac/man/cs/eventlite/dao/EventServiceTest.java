package uk.ac.man.cs.eventlite.dao;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
	
	private Date d1, d2, d3, d4, d5;
	
	
	@Before
	public void setup() {
		testVenue = new Venue(null, 0, null);
		testVenue.setName("Test Event Name");
		testVenue.setCapacity(10);
		
		testVenue2 = new Venue(null, 0, null);
		testVenue2.setName("Test Event Name 2");
		testVenue2.setCapacity(20);
		
		// Need to save the venue to ensure tests don't error
		venueService.save(testVenue);
		venueService.save(testVenue2);
		
		DateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		try {
			d1 = f.parse("25/3/2018 12:00");
			d2 = f.parse("12/4/2018 00:00");
			d3 = f.parse("10/5/2018 17:00");
			d4 = f.parse("02/5/2018 20:00");
			d5 = f.parse("01/2/2018 01:00");
		
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testFindAll() {
		eventService.save(new Event("a Test Event 1", testVenue, d1, d1, null));	
		eventService.save(new Event("f test event 2", testVenue, d2, d2, null));

		List<Event> events = (List<Event>) eventService.findAll();

		long count = eventService.count();

		assertThat("findAll should get all events.", count, equalTo((long) events.size()));
		testListInOrder(events);
	}

	@Test
	public void testAscendingByDateAndTime() {
	
		eventService.save(new Event("Concert", testVenue, d5, d5, ""));
		eventService.save(new Event("Java Lecture", testVenue, d2, d2, ""));
		
		List<Event> events = (List<Event>) eventService.findAll();
		boolean correctOrder = false;
		int count = 0;
		for (Event event : events) {
			String current = event.getName();
			if (current == "Java Lecture" && count == 0)
				correctOrder = true;
			count++;
		}
		
		assertTrue("Java Lecture should be first in the list", correctOrder);
	}
	
	@Test
	public void testSearchByName() {
		eventService.save(new Event("Test Event 1", testVenue, d5, d5, null));	
		eventService.save(new Event("test event 2", testVenue, d1, d1, null));
		eventService.save(new Event("test Event", testVenue, d2, d2, null));	
		eventService.save(new Event("Another random string", testVenue, d3, d3, null));	
		
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
		Event event = new Event("Test Event", testVenue, d3, d3, null);
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
	public void count() {
		Event event = new Event("Test Event", testVenue, d3, d3, "");
		
		long initialCount = eventService.count();
		eventService.save(event);
		
		assertThat("Count should increase by one on save", initialCount + 1, equalTo(eventService.count()));		
		
	}
	
	@Test
	public void save() {
		Event event = new Event("Test Event2", testVenue, d3, d3, "");
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
	public void update() {
		Event currentEvent = new Event("Test Event3", testVenue, d3, d3, "");
		eventService.save(currentEvent);
		
		String newName = "Updated Event3";

		Event changedEvent = new Event(newName, testVenue2, d4, d4, "test");
		
		eventService.update(currentEvent, changedEvent);
		
		boolean checkName, checkVenue, checkDate, checkTime, checkDescription;
		checkName = checkVenue = checkDate = checkTime = checkDescription = false;
		
		if (currentEvent.getName().equals(newName))
			checkName = true;

		if (currentEvent.getVenue().equals(testVenue2))
			checkVenue = true;
		
		if (currentEvent.getDate().equals(d4))
			checkDate = true;
		
		if (currentEvent.getTime().equals(d4))
			checkTime = true;
		
		if (currentEvent.getDescription().equals("test"))
			checkDescription = true;
		
		assertTrue(checkName);
		assertTrue(checkVenue);
		assertTrue(checkDate);
		assertTrue(checkTime);
		assertTrue(checkDescription);
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
		
				return e1.getDate().compareTo(e2.getDate());
			}			
		});
		
		Iterator<Event> iterator = events.iterator();
		for (Event e: listInOrder)
			assertTrue(e.equals(iterator.next()));
	}
	
}
