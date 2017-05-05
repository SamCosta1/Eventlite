package uk.ac.man.cs.eventlite.controllers.events;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import uk.ac.man.cs.eventlite.TestParent;
import uk.ac.man.cs.eventlite.controllers.EventsControllerWeb;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.EventTestHelper;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@AutoConfigureMockMvc
public class EventsControllerRestTest extends TestParent {

	private MockMvc mvc;
	
	@Autowired
	private WebApplicationContext ctx;
	
	@Mock
	private Event event;
	
	@Mock
	private EventService eventService;
	
	@Mock
	private VenueService venueService;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.webAppContextSetup(ctx).build();		
		EventTestHelper.init(venueService);
	}
	
	@Test
	public void testGetAllEvents() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
 		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
 		.andExpect(jsonPath("$.title", equalTo("EventLite Events")))
 		.andExpect(jsonPath("$._self", equalTo("http://localhost/events")));
	}

	@Test
	public void testGetOneEvent() throws Exception {
		when(eventService.findById(1)).thenReturn(event);
		when(event.getId()).thenReturn(Long.valueOf("1"));
		mvc.perform(MockMvcRequestBuilders.get("/events/1").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.title", equalTo("EventLite event view")))
			.andExpect(jsonPath("$._self", equalTo("http://localhost/events/1")))
			.andExpect(jsonPath("$.id", equalTo("1")));
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
