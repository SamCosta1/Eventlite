package uk.ac.man.cs.eventlite.controllers.events;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.hasItem;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import uk.ac.man.cs.eventlite.TestParent;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.EventTestHelper;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Venue;
import uk.ac.man.cs.eventlite.entities.Event;

@AutoConfigureMockMvc
public class EventsControllerRestTest extends TestParent {

	@Autowired
	private MockMvc mvc;

	private Event event;

	private Venue venue;

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	@Test
	public void testGetAllEvents() throws Exception {
		mvc.perform(get("/events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
 		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
 		.andExpect(jsonPath("$.title", equalTo("EventLite Events")))
 		.andExpect(jsonPath("$._self", equalTo("http://localhost/events")))
 		.andExpect(jsonPath("$.events", notNullValue()));
	}

	@Test
	public void testGetOneEvent() throws Exception {
		venue = new Venue("Test Event Name", 10, null, null, null, null, null);
		venueService.save(venue);

		event = EventTestHelper.newEvent("EventLite Event 1", venue, "25/3/2018", "12:00", "some description");
		eventService.save(event);

		mvc.perform(get("/events/" + event.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.title", equalTo("EventLite Event " + event.getId())))
			.andExpect(jsonPath("$._self", equalTo("http://localhost/events/" + event.getId())))
			.andExpect(jsonPath("$.id", equalTo("" + event.getId())))
			.andExpect(jsonPath("$.date", equalTo("" + event.getDate())))
			.andExpect(jsonPath("$.name", equalTo(event.getName())))
			.andExpect(jsonPath("$.venue.id", equalTo("" + venue.getId())))
			.andExpect(jsonPath("$.venue.name", equalTo("Test Event Name")))
			.andExpect(jsonPath("$.venue.capacity", equalTo("10")))
			.andExpect(jsonPath("$.venue._self", equalTo("http://localhost/venues/" + venue.getId())));
	}

	@Test
	public void testGetUserEventsJson() throws Exception {
		venue = new Venue("Test Venue Name", 10, null, null, null, null, null);
		venueService.save(venue);
		
		event = EventTestHelper.newEvent("EventLite Event 1", venue, "25/3/2018", "12:00", "some description");
		eventService.save(event);
		
		long eventId = eventService.searchByName("EventLite Event 1").get(0).getId();
		long venueId = venueService.searchByName("Test Venue Name").iterator().next().getId();
		
		mvc.perform(get("/events/userevents").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.title", equalTo("EventLite User Events")))
		.andExpect(jsonPath("$._self", equalTo("http://localhost/events/userevents")))
		.andExpect(jsonPath("$.events[*].id", hasItem("" + eventId)))
		.andExpect(jsonPath("$.events[*].name", hasItem("EventLite Event 1")))
		.andExpect(jsonPath("$.events[*]._self", hasItem("http://localhost/events/" + eventId)))
		.andExpect(jsonPath("$.events[*].venue.id", hasItem("" + venueId)))
		.andExpect(jsonPath("$.events[*].venue.name", hasItem("Test Venue Name")))
		.andExpect(jsonPath("$.events[*].venue.capacity", hasItem("10")))
		.andExpect(jsonPath("$.events[*].venue._self", hasItem("http://localhost/venues/" + venueId)));
		
	}

}