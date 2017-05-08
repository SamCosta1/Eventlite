package uk.ac.man.cs.eventlite.dao;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
		
	private User testUser = null;
	
	@Before
	public void setup() {
		EventTestHelper.init(venueService);
		testUser = userService.save(new UserCreateForm("test User"));		
		
		Event e1 = EventTestHelper.newEvent("a Test Event 1", "01/02/2012", "01:00");
		e1.setUser(testUser);
			
		Event e2 = EventTestHelper.newEvent("z test event 2", "25/03/2018", "12:00");
		e2.setUser(testUser);
			
		eventService.save(e1);	
		eventService.save(e2);
		
		eventService.save(EventTestHelper.newEvent("Football game", "25/03/2018", "12:00"));	
		eventService.save(EventTestHelper.newEvent("Rugby game", "12/04/2018", "00:00"));	
		eventService.save(EventTestHelper.newEvent("Past Rugby game", "12/04/2011", "00:00"));	
	}

	@Test
	public void testFindAll() {
		
		// Past Events
		
		// same name, different dates
		eventService.save(EventTestHelper.newEvent("An Event", "12/04/2011", "00:00"));
		eventService.save(EventTestHelper.newEvent("An Event", "12/04/2012", "00:00"));
		
		// different names, same date
		eventService.save(EventTestHelper.newEvent("A", "12/04/2012", "00:00"));
		eventService.save(EventTestHelper.newEvent("B", "12/04/2012", "00:00"));
		
		// Future Events
		
		// same name, different dates
		eventService.save(EventTestHelper.newEvent("An Event", "12/04/2018", "00:00"));
		eventService.save(EventTestHelper.newEvent("An Event", "17/04/2018", "00:00"));
		
		// different names, same date
		eventService.save(EventTestHelper.newEvent("A", "12/04/2018", "00:00"));
		eventService.save(EventTestHelper.newEvent("B", "12/04/2018", "00:00"));
	
		List<Event> events = (List<Event>) eventService.findAll();

		long count = eventService.count();

		assertThat("findAll should get all events.", count, equalTo((long) events.size()));
		testListInOrder(events);
	}

	@Test
	public void testSearchByName() {	
		EventTestHelper.setUser(testUser);
		String searchTerm = "test Event";
		
		// Past Events
		
		// same name, different dates
		eventService.save(EventTestHelper.newEvent("Test evEnt", "12/04/2011", "00:00"));
		eventService.save(EventTestHelper.newEvent("test Event", "12/04/2012", "00:00"));
		
		// different names, same date
		eventService.save(EventTestHelper.newEvent("Test Event in a name", "12/04/2012", "00:00"));
		eventService.save(EventTestHelper.newEvent("test event", "12/04/2012", "00:00"));
		
		// Future Events
		
		// same name, different dates
		eventService.save(EventTestHelper.newEvent("TEST EVENT IS GREAT", "11/04/2018", "00:00"));
		eventService.save(EventTestHelper.newEvent("TEST EVENT IS GREAT", "12/04/2018", "00:00"));
		
		// different names, same date
		eventService.save(EventTestHelper.newEvent("TeSt event", "12/04/2018", "00:00"));
		eventService.save(EventTestHelper.newEvent("TeSt event yeey", "12/04/2018", "00:00"));
		
		List<Event> events = (List<Event>)eventService.searchByName(searchTerm);
		List<Event> userevents = (List<Event>) eventService.searchByNameByUser(searchTerm, testUser);

		testWholeWordMatchesComeFirst(searchTerm, events);	
    	testWholeWordMatchesComeFirst(searchTerm, userevents);	
		
		// The list without the whole word matches should be in the same order
		testListInOrder(events);
		testListInOrder(userevents);
		
		EventTestHelper.setUser(null);
		
	}

	private void testWholeWordMatchesComeFirst(String searchTerm, List<Event> events) {
		boolean isWholeWordFuture = true;
		boolean isWholeWordPast = true;
		for (Iterator<Event> iterator = events.iterator(); iterator.hasNext();) {
			Event e = iterator.next();
			// Ensure that all the whole word matches come first
			if (e.getDate().after(new Date()) && !isWholeWordFuture) {
				assertTrue(!e.getName().trim().toLowerCase().equals(searchTerm));
				iterator.remove();
			}
			
			if (e.getDate().before(new Date()) && !isWholeWordPast) {
				assertTrue(!e.getName().trim().toLowerCase().equals(searchTerm));
				iterator.remove();
			}
			
			if (e.getDate().before(new Date()) && !e.getName().trim().toLowerCase().equals(searchTerm))
				isWholeWordPast = false;
			
			if (e.getDate().after(new Date()) && !e.getName().trim().toLowerCase().equals(searchTerm))
				isWholeWordFuture = false;
			
			assertTrue("Names contain substring 'test event' - case insensitive"
						, e.getName().toLowerCase().contains(searchTerm.toLowerCase()));	
		}
	}
	
	@Test
	public void testDeleteEvent(){
		Event event = EventTestHelper.newEvent("Java Lecture", "12/4/2018", "00:00");
		eventService.save(event);
		
		long initialCount = eventService.count();
		
		eventService.delete(event);

		List<Event> events = (List<Event>) eventService.findAll();
		
		for (Event e : events)
			assertFalse("Deleted event doesn't appear", e.equals(event));		
		
		assertThat("Count should decrease by one on delete", initialCount - 1, equalTo(eventService.count()));
		
	}
	
	@Test
	public void testCount() {
				
		long initialCount = eventService.count();
		eventService.save(EventTestHelper.newEvent("Java Lecture", "12/4/2018", "00:00"));
		
		assertThat("Count should increase by one on save", initialCount + 1, equalTo(eventService.count()));				
	}
	
	@Test
	public void testSave() {
		Event event = EventTestHelper.newEvent("Java Lecture", "12/4/2018", "00:00");
		eventService.save(event);
		
		List<Event> events = (List<Event>) eventService.findAll();
		
		boolean found = false;
		for (Event e : events)
			if (e.equals(event))
				found = true;			
		
		assertTrue("Saved event was saved", found);
		
	}
	
	@Test
	public void testUpdate() {
		Event currentEvent = EventTestHelper.newEvent("Java Lecture", "12/4/2018", "00:00");
		eventService.save(currentEvent);
		
		Venue testVenue = new Venue("Another venue ", 110, null, null, null, null, null);
			
		Event changedEvent = EventTestHelper.newEvent("Updated name", testVenue, "12/3/2018", "12:00", "new description");
		
		eventService.update(currentEvent, changedEvent);
		assertTrue(currentEvent.equalsIgnoreId(changedEvent));
	}
	
	@Test
	public void testFindById() {
		Event event = EventTestHelper.newEvent("Java Lecture", "12/4/2018", "00:00");
		eventService.save(event);
		
		Event foundEvent = eventService.findById(event.getId());	

		assertTrue("The find by Id method found the correct event", foundEvent.equals(event));		
	}
	
	@Test
	public void testFindByExistingUser() {
		List<Event> events = (List<Event>)eventService.findAllByUser(testUser);
		
		for (Event e : events)
			assertTrue("All users are the test user", e.getUser().equals(testUser));
		
		assertThat(events.size(), equalTo(2));
		testListInOrder(events);
	}
	
	@Test
	public void testFindByNoUser() {
		List<Event> events = (List<Event>)eventService.findAllByUser(null);
		
		for (Event e : events) 
			assertTrue("All users are the test user", e.getUser() == null);
		testListInOrder(events);
	}

	@Test
	public void testFindAllByVenue() {		
		Venue venue = new Venue("Test event name", 10, null, null, null, null, null);
		venueService.save(venue);
		
		eventService.save(EventTestHelper.newEvent("test Event", venue, "25/3/2018", "12:00"));	
		eventService.save(EventTestHelper.newEvent("A Test", venue, "01/2/2011", "01:00"));
		eventService.save(EventTestHelper.newEvent("B Test", venue, "01/2/2011", "01:00"));
		
		
		List<Event> events = (List<Event>)eventService.findAllByVenue(venue);	
		
		for (Event e : events) {
			assertTrue("event has the venue", e.getVenue().equals(venue));
			assertTrue("event is in the future", e.getDate().after(new Date()));
		}
		
		testListInOrder(events);		
	}
	
	@Test
	public void testFindByNoVenue() {		
		List<Event> events = (List<Event>)eventService.findAllByVenue(null);	
		assertTrue(events.isEmpty());
			
	}
	
	@Test
	public void testGetThreeSoonestEvents() {
		List<Event> threeEvents = (List<Event>) eventService.findThreeSoonestEvents();
		List<Event> allEvents = (List<Event>) eventService.findAll();
		
		assertTrue("Should return at most three events", threeEvents.size() <= 3);
		for (Event e : threeEvents)
			assertTrue("Event hasn't already happened", !e.isPastEvent());
		
		Iterator<Event> it = allEvents.iterator();		
		for (int i = 0; i < 3; i++)
			if (it.hasNext())
				assertTrue(threeEvents.contains(it.next())); // The first three results in find all should be the soonest ones
	
		testListInOrder(threeEvents);
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
}
