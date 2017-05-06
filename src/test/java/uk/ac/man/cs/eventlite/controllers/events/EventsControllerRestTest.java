package uk.ac.man.cs.eventlite.controllers.events;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import uk.ac.man.cs.eventlite.TestParent;

@AutoConfigureMockMvc
public class EventsControllerRestTest extends TestParent {
	
	@Autowired
	private MockMvc mvc;

	@Test
	public void testGetAllEvents() throws Exception {
		mvc.perform(get("/events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
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
	
	@Test
	public void testGetUserEventsJson() throws Exception {
		mvc.perform(get("/events/userevents").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.title", equalTo("EventLite User Events")))
		.andExpect(jsonPath("$._self", equalTo("http://localhost/events/userevents")))
		.andExpect(jsonPath("$.events", notNullValue()));
	}
	
	@Test
	public void testGetFirstEvent() throws Exception {
		mvc.perform(get("/events/1").accept(MediaType.APPLICATION_JSON)).andExpect(status().isFound());
	}
}
