package uk.ac.man.cs.eventlite.controllers.venues;

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
import uk.ac.man.cs.eventlite.controllers.VenuesControllerWeb;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@AutoConfigureMockMvc
public class VenuesControllerWebTest extends TestParent {

	private MockMvc mvc;
	
	@InjectMocks
	private VenuesControllerWeb venuesController;
	
	@Mock
	private VenueService venueService;
	
	@Mock
	private EventService eventService;
	
	@Mock
	private Venue venue;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(venuesController).build();		
	}
	
	@Test
	public void testGetAllVenues() throws Exception {
		when(venueService.findAll()).thenReturn(Collections.<Venue> emptyList());
			mockGet("/venues", MediaType.TEXT_HTML, "venues/index", HttpStatus.OK);
		verify(venueService, times(1)).findAll();
	}

	
	@Test
	public void testFilterVenues() throws Exception {
		when(venueService.searchByName("")).thenReturn(Collections.<Venue> emptyList());
			mvc.perform(MockMvcRequestBuilders.post("/venues/")
					.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
					.param("name", "testString")
					.accept(MediaType.TEXT_HTML_VALUE))
					.andExpect(view().name("venues/index"));
		verify(venueService, times(1)).searchByName("testString");
	}
	
	@Test
	public void testGetFirstVenue() throws Exception {
		Venue testVenue = new Venue();		
		when(venueService.findById(1)).thenReturn(testVenue);
		when(eventService.findById(1)).thenReturn(new Event());
		mockGet("/venues/1", MediaType.TEXT_HTML, "venues/show", HttpStatus.OK);
		verify(venueService, times(1)).findById(1);
		verify(eventService, times(1)).findAllByVenue(testVenue);
	}
		
	// Helpers ----	
	private void mockGet(String url, MediaType mediaType, String viewName, HttpStatus status) throws Exception {
		mvc.perform(get(url).accept(mediaType)).andExpect(status().is(status.value()))
			.andExpect(view().name(viewName));
	}
}
