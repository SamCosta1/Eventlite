package uk.ac.man.cs.eventlite.controllers.events;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
 		.andExpect(jsonPath("$._self", equalTo("http://localhost/events")));
	}
	
	@Test
	public void testGetOneEvent() throws Exception {
		venue = new Venue("Test Event Name", 10, null, null);
		venueService.save(venue);
		
		event = EventTestHelper.newEvent("EventLite Event 1", venue, "25/3/2018", "12:00", "some description");
		eventService.save(event);
		
		mvc.perform(get("/events/" + event.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.title", equalTo("EventLite event view")))
		.andExpect(jsonPath("$._self", equalTo("http://localhost/events/" + event.getId())))
		.andExpect(jsonPath("$.id", equalTo("" + event.getId())))
		.andExpect(jsonPath("$.date", equalTo("" + event.getDate())))
		.andExpect(jsonPath("$.name", equalTo(event.getName())))
		.andExpect(jsonPath("$.venue", notNullValue()));
	}
	
	@Test
	public void deleteEvent() throws Exception{
		mvc.perform(MockMvcRequestBuilders.delete("/events/1")).andExpect(status().isNoContent());
	}
	
	@Test
	public void filterEvents() throws Exception{
		mvc.perform(MockMvcRequestBuilders.post("/events/").contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\": \"\"}").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().string(containsString("")));		
	}

	@Ignore
	@Test
	public void testGetFirstEvent() throws Exception {
		mvc.perform(get("/events/1").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
}