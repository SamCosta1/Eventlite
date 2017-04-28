package uk.ac.man.cs.eventlite.controllers.homepage;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import uk.ac.man.cs.eventlite.TestParent;
import uk.ac.man.cs.eventlite.controllers.HomepageControllerWeb;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@AutoConfigureMockMvc
public class HomepageControllerWebTest extends TestParent {

	private MockMvc mvc;
	
	@InjectMocks
	private HomepageControllerWeb homepageController;
	
	@Mock
	private EventService eventService;
	
	@Mock
	private VenueService venueService;
	
	@Mock
	private Venue venue;
	
	@Mock
	private Event event;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(homepageController).build();		
	}
	
	@Test
	public void testHomepage() throws Exception {
		when(venueService.findMostPopularVenues()).thenReturn(new Venue[]{});
		when(eventService.findThreeSoonestEvents()).thenReturn(Collections.<Event> emptyList());		
			mockGet("/", MediaType.TEXT_HTML, "index", HttpStatus.OK);
		verify(eventService, times(1)).findThreeSoonestEvents();
		verify(venueService, times(1)).findMostPopularVenues();
	}
	
	// Helpers ----	
	private void mockGet(String url, MediaType mediaType, String viewName, HttpStatus status) throws Exception {
		mvc.perform(get(url).accept(mediaType)).andExpect(status().is(status.value()))
			.andExpect(view().name(viewName));
	}

}
