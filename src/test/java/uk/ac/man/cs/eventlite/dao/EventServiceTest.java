package uk.ac.man.cs.eventlite.dao;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.man.cs.eventlite.TestParent;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.User;
import uk.ac.man.cs.eventlite.entities.Venue;
import uk.ac.man.cs.eventlite.helpers.UserCreateForm;

public class EventServiceTest extends TestParent {

	@Autowired
	private EventService eventService;
	
	@Autowired
	private VenueService venueService;
	
	@Autowired
	private UserService userService;
	
	public Venue testVenue, testVenue2;
	
	private Date d1, d2, d3, d4, d5, d6, d7;
	
	private User testUser = null;
	
	
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
			d5 = f.parse("01/2/2012 01:00");
			d6 = f.parse("01/2/2011 01:00");
			d7 = f.parse("01/2/2011 01:00");
		
		} catch (ParseException e) {
			e.printStackTrace();
		}
				
		testUser = userService.save(new UserCreateForm("test User"));		
		
		Event e1 = new Event("a Test Event 1", testVenue, d5, d5, null);
			e1.setUser(testUser);
			
		Event e2 = new Event("z test event 2", testVenue, d1, d1, null);
			e2.setUser(testUser);
			
		eventService.save(e1);	
		eventService.save(e2);
		eventService.save(new Event("b Test Event 3", testVenue, d1, d1, null));	
		eventService.save(new Event("x test Event", testVenue, d2, d2, null));	
		eventService.save(new Event("t Another random string", testVenue, d3, d3, null));			
		eventService.save(new Event("Concert", testVenue, d5, d5, ""));
		eventService.save(new Event("Java Lecture", testVenue, d2, d2, ""));	
		eventService.save(new Event("A Test", testVenue, d6, d6, ""));
		eventService.save(new Event("B Test", testVenue, d7, d7, ""));
	}

	@Test
	public void testFindAll() {
		List<Event> events = (List<Event>) eventService.findAll();

		long count = eventService.count();

		assertThat("findAll should get all events.", count, equalTo((long) events.size()));
		testListInOrder(events);
	}

	@Test
	public void testSearchByName() {		
		String searchTerm = "test Event";
		
		List<Event> events = (List<Event>) eventService.searchByName(searchTerm);
				
		for (Event e : events) 
			assertTrue("Names contain substring 'test event' - case insensitive"
						, e.getName().toLowerCase().contains(searchTerm.toLowerCase()));			
				
		assertThat("Three items returned: ", 4, equalTo(events.size()));
		testListInOrder(events);		
	}
	
	@Test
	public void testDeleteEvent(){
		Event event = new Event("Test Event", testVenue, d3, d3, null);
		eventService.save(event);
		
		long initialCount = eventService.count();
		
		eventService.delete(event);

		List<Event> events = (List<Event>) eventService.findAll();
		
		for (Event e : events)
			assertFalse("Deleted event doesn't appear", e.equals(event) && e.getVenue().equals(testVenue));		
		
		assertThat("Count should decrease by one on delete", initialCount - 1, equalTo(eventService.count()));
		
	}
	
	@Test
	public void testCount() {
		Event event = new Event("Test Event", testVenue, d3, d3, "");
		
		long initialCount = eventService.count();
		eventService.save(event);
		
		assertThat("Count should increase by one on save", initialCount + 1, equalTo(eventService.count()));				
	}
	
	@Test
	public void testSave() {
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
	public void testUpdate() {
		Event currentEvent = new Event("Test Event3", testVenue, d3, d3, "");
		eventService.save(currentEvent);
		
		String newName = "Updated Event3";

		Event changedEvent = new Event(newName, testVenue2, d4, d4, "test");
		
		eventService.update(currentEvent, changedEvent);
			
		assertTrue(currentEvent.getName().equals(newName));
		assertTrue(currentEvent.getVenue().equals(testVenue2));
		assertTrue(currentEvent.getDate().equals(d4));
		assertTrue(currentEvent.getTime().equals(d4));
		assertTrue(currentEvent.getDescription().equals("test"));
	}
	
	@Test
	public void testFindById() {
		Event event = new Event("Id Test event", testVenue, d3, d3, "");
		eventService.save(event);
		
		Event foundEvent = eventService.findById(event.getId());	

		assertTrue("The find by Id method found the correct event", foundEvent.equals(event));		
	}
	
	@Test
	public void testFindByNoUser() {
		List<Event> events = (List<Event>)eventService.findAllByUser(testUser);
		
		for (Event e : events)
			assertTrue("All users are the test user", e.getUser().equals(testUser));
		
		assertThat("There are 2 of them", events.size(), equalTo(2));
		testListInOrder(events);
	}
	
	@Test
	public void testFindExistingUser() {
		List<Event> events = (List<Event>)eventService.findAllByUser(null);
		
		for (Event e : events) 
			assertTrue("All users are the test user", e.getUser() == null);
		testListInOrder(events);
	}

	@Test
	public void testFindAllByVenue() {
		
		testVenue = new Venue(null, 0, null);
		testVenue.setName("Test Event Name");
		testVenue.setCapacity(100);
		
		venueService.save(testVenue);
		
		Event event1 = new Event("Test event1", testVenue, d3, d3, "");
		Event event2 = new Event("Test event2", testVenue, d3, d3, "");
		
		List<Event> givenEvents = Arrays.asList(event1, event2);
		
		eventService.save(event1);
		eventService.save(event2);
		
		List<Event> events = (List<Event>)eventService.findAllByVenue(testVenue);	

		assertTrue("The find by venue method found the correct events", givenEvents.equals(events));
		
	}
	
	// Helper method for checking a result set is in correct order
	private void testListInOrder(List<Event> events) {
		List<Event> pastEvents = new ArrayList<Event>();
		List<Event> futureEvents = new ArrayList<Event>();
		
		boolean reachedPastEvents = false;
		for (Event e : events) {
			if (reachedPastEvents) // Checking all past events come after the future ones
				assertTrue(e.getDate().before(new Date()));
			
			if (e.isPastEvent()) {
				pastEvents.add(e);
				reachedPastEvents = true;
			} else {
				futureEvents.add(e);
			}				
		}
		Event previous = null;
		for (Event e : pastEvents) {
			if (previous == null) {
				previous = e;
				continue;
			}
			
			if (e.getDate().equals(previous.getDate())) 
				assertTrue(previous.getName().compareTo(e.getName()) <= 0);			
			else 
				assertTrue(e.getDate().before(previous.getDate()));			
		}
		
		previous = null;
		for (Event e : futureEvents) {
			if (previous == null) {
				previous = e;
				continue;
			}
			
			if (e.getDate().equals(previous.getDate())) 
				assertTrue(previous.getName().compareTo(e.getName()) <= 0);		
			else 
				assertTrue(e.getDate().after(previous.getDate()));			
		}		
	}

	
	@Test
	public void testAddEvent() {
		long initialCount = eventService.count();
		
		Event event = new Event("Test Event Add", testVenue, d1, d2, "");
		eventService.save(event);
		
        List<Event> events = (List<Event>) eventService.findAll();
		
		boolean isAdded = false;
		for (Event e : events)
			if (e.equals(event) && e.getVenue().equals(testVenue))
				isAdded = true;			
		
		assertTrue("The event was added", isAdded);		
		assertThat("Count should increase by one on add", initialCount + 1, equalTo(eventService.count()));
	}
}
