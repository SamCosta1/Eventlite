package uk.ac.man.cs.eventlite.controllers.events;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import uk.ac.man.cs.eventlite.TestParent;
import uk.ac.man.cs.eventlite.controllers.EventsControllerWeb;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.entities.Event;

@AutoConfigureMockMvc
public class EventsControllerWebTest extends TestParent {

	private MockMvc mvc;
	
	@InjectMocks
	private EventsControllerWeb eventsController;
	
	@Mock
	private EventService eventService;
	
	@Mock
	private Event event;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(eventsController).build();
		
	}

	@Test
	public void testGetAllEvents() throws Exception {
		when(eventService.findAll()).thenReturn(Collections.<Event> emptyList());
		mockGet("/events", MediaType.TEXT_HTML, "events/index", HttpStatus.OK);
		verify(eventService, times(1)).findAll();
	}
	
	@Ignore
	@Test
	public void testShowUpdateForm() throws Exception {
		mvc.perform(get("/events/3/update").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/eventform"));
	}	
	
	@Test
	public void testGetFirstEvent() throws Exception {
		when(eventService.findById(1)).thenReturn(new Event());
		mockGet("/events/1", MediaType.TEXT_HTML, "events/show", HttpStatus.OK);
		verify(eventService, times(1)).findById(1);
	}
	
	@Test
	public void testFilterEvents() throws Exception {
		when(eventService.searchByName("")).thenReturn(Collections.<Event> emptyList());
			mvc.perform(MockMvcRequestBuilders.post("/events/")
					.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
					.param("name", "testString")
					.accept(MediaType.TEXT_HTML_VALUE))
					.andExpect(view().name("events/index"));
		verify(eventService, times(1)).searchByName("testString");
	}
	
	@Test
	public void testDeleteEvent() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/events/1/delete")
		.contentType(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.TEXT_HTML)).andExpect(status().isFound())
		.andExpect(view().name("redirect:/events"));
	}
	
	@Ignore
	@Test
	public void getNewEventHtml() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/events/new").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("events/new"));
	}
	
	
	@Test
	public void postEventHtml() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/events/new").contentType(MediaType.APPLICATION_FORM_URLENCODED))
		.andExpect(view().name("redirect:/events"));
	}
	
	// Helpers ----
	
	private void mockGet(String url, MediaType mediaType, String viewName, HttpStatus status) throws Exception {
		mvc.perform(get(url).accept(mediaType)).andExpect(status().is(status.value()))
			.andExpect(view().name(viewName));
	}	
}
