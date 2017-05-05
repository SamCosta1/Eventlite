package uk.ac.man.cs.eventlite.dao;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;
import uk.ac.man.cs.eventlite.TestParent;


public class VenueServiceTest extends TestParent{

	@Autowired
	private VenueService venueService;
	
	@Autowired
	private EventService eventService;
	
	private int capacity = 100;
	private String addressLine1 = "Kilburn Building";
	private String addressLine2 = "University of Manchester";
	private String streetName = "Oxford Rd";
	private String city = "Manchester";
	private String postcode = "M13 9PL";

	@Test
	public void findAllTest() {		
		venueService.save(new Venue("z", capacity, addressLine1, addressLine2, streetName, city, postcode));	
		venueService.save(new Venue("x", capacity, addressLine1, addressLine2, streetName, city, postcode));
		venueService.save(new Venue("a", capacity, addressLine1, addressLine2, streetName, city, postcode));	
		venueService.save(new Venue("b", capacity, addressLine1, addressLine2, streetName, city, postcode));
		
		List<Venue> venues = (List<Venue>) venueService.findAll();
		long count = venueService.count();

		assertThat("findAll should get all venues.", count, equalTo((long) venues.size()));
		testListInOrder(venues);
	}
	
	@Test
	public void findAllExceptOne() {
		Venue ignoredEvent = new Venue("name1", capacity, addressLine1, addressLine2, streetName, city, postcode);
		venueService.save(ignoredEvent);
				
		List<Venue> venues = (List<Venue>) venueService.findAllExceptOne(ignoredEvent);
		for (Venue v : venues) 
			assertFalse(v.equals(ignoredEvent));
		
		List<Venue> allVenues = (List<Venue>) venueService.findAll();
		allVenues.remove(ignoredEvent);
		assertTrue("All other venues present", venues.containsAll(allVenues));		
	}
	
	@Test
	public void testCount() {
		Venue newVenue = new Venue("name1", capacity, addressLine1, addressLine2, streetName, city, postcode);
		
		long initialCount = venueService.count();
		venueService.save(newVenue);
				
		assertThat("Count should increase by one on save", initialCount + 1, equalTo(venueService.count()));		
		
	}
	
	@Test
	public void testSearchByName() {
		venueService.save(new Venue("d Test Venue 1", capacity, addressLine1, addressLine2, streetName, city, postcode));
		venueService.save(new Venue("b test venue 2", capacity, addressLine1, addressLine2, streetName, city, postcode));
		venueService.save(new Venue("a test Venue", capacity, addressLine1, addressLine2, streetName, city, postcode));	
		venueService.save(new Venue("f Another random string", capacity, addressLine1, addressLine2, streetName, city, postcode));
		
		String searchTerm = "test Venue";		
		List<Venue> venues = (List<Venue>) venueService.searchByName(searchTerm);	
		
		for (Venue v : venues) 			
			assertTrue("Names contain substring 'test venue' - case insensitive"
						, v.getName().toLowerCase().contains(searchTerm.toLowerCase()));			
	
		testListInOrder(venues);
		
	}
	
	@Test
	public void testSave() {

		String postcode = "M13 9PL";
		long previousCount = venueService.count();
		
		Venue newVenue = new Venue("name1", capacity, addressLine1, addressLine2, streetName, city, postcode);
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
		Venue venue = new Venue("test Venue", capacity, addressLine1, addressLine2, streetName, city, postcode);
		venueService.save(venue);
		
		Venue foundVenue = venueService.findById(venue.getId());	

		assertTrue("The find by Id method found the correct venue", foundVenue.equals(venue));		
	}
	
	@Ignore
	@Test
	public void testFindMostPopularVenues() {
		Venue[] venues = venueService.findMostPopularVenues();
		List<Venue> allVenues = (List<Venue>) venueService.findAll();
		
		Set<Integer> noEvents = new HashSet<Integer>();
		for (Venue v : allVenues)
			noEvents.add(((List<Event>)eventService.findAllByVenue(v)).size());
		
		List<Integer> sorted = new ArrayList<Integer>(noEvents);
		Collections.sort(sorted);
		Collections.reverse(sorted);
		
		if (sorted.size() > 3)
			sorted = sorted.subList(0,3);		
		for (Venue v : venues) {
			int freq = ((List<Event>)eventService.findAllByVenue(v)).size();
			assertTrue("This venue has one of the highest numbers of events", sorted.contains(freq));
		}		
	}
	
	// Helper method for checking a result set is in correct order
	// Works by sorting the elements into the correct order
	// then check both lists are the same, i.e. the original list was
	// Correct in the first place
	private void testListInOrder(List<Venue> venues) {
		List<Venue> listInOrder = new ArrayList<Venue>(venues);
		
		Collections.sort(listInOrder);
		
		Iterator<Venue> iterator = venues.iterator();
		for (Venue v : listInOrder)
			assertTrue(v.equals(iterator.next()));
	}
	
	@Test
	public void testDeleteVenue(){
		Venue venue = new Venue();
		venueService.save(venue);
		
		long initialCount = venueService.count();
		
		venueService.delete(venue);

		List<Venue> venues = (List<Venue>) venueService.findAll();
		
		for (Venue v : venues)
			assertFalse("Deleted event doesn't appear", v.equals(venue));		
		
		assertThat("Count should decrease by one on delete", initialCount - 1, equalTo(venueService.count()));
		
	}

}
