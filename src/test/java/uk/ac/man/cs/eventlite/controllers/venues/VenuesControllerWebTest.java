package uk.ac.man.cs.eventlite.controllers.venues;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
	
	@Mock
	private Event event;

	private List<Event> events;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(venuesController).build();
		events = Arrays.asList(event, event, event);
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
	public void testVenueDetails() throws Exception {		
		when(venueService.findById(1)).thenReturn(venue);
		when(eventService.findAllByVenue(venue)).thenReturn(Collections.<Event> emptyList());
			mockGet("/venues/1", MediaType.TEXT_HTML, "venues/show", HttpStatus.OK);
		verify(venueService, times(1)).findById(1);
		verify(eventService, times(1)).findAllByVenue(venue);
	}
	
	@Test
	public void testDeleteVenueWithNoEvents() throws Exception {
		when(venueService.findById(1)).thenReturn(venue);
		when(eventService.findAllByVenue(venue)).thenReturn(Collections.<Event> emptyList());
		mvc.perform(MockMvcRequestBuilders.post("/venues/1/delete")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.TEXT_HTML)).andExpect(status().isFound())
			.andExpect(view().name("redirect:/venues"));
		verify(venueService, times(1)).findById(1);
		verify(eventService, times(1)).findAllByVenue(venue);
	}
	
	@Test
	public void testDeleteVenueWithEvents() throws Exception {
		when(venueService.findById(1)).thenReturn(venue);
		when(eventService.findAllByVenue(venue)).thenReturn(events);
		mvc.perform(MockMvcRequestBuilders.post("/venues/1/delete")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.TEXT_HTML)).andExpect(status().isFound())
			.andExpect(view().name("redirect:/venues/{id}"));
		verify(venueService, times(1)).findById(1);
		verify(eventService, times(1)).findAllByVenue(venue);
	}
	
	@Test
	public void showUpdateVenueForm() throws Exception {		
 		when(venueService.findById(3)).thenReturn(venue);
 		//when(event.getTime()).thenReturn(new Date());
		mvc.perform(get("/venues/3/update").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
			.andExpect(view().name("venues/venueform"));
 		verify(venueService, times(1)).findById(3);
	}
	
	@Test
	public void testUpdateVenue() throws Exception {
		ArgumentCaptor<Venue> savedCaptor = ArgumentCaptor.forClass(Venue.class);
		MultiValueMap<String, String> update = new LinkedMultiValueMap<String, String>();
		update.add("venue", "1");
		update.add("name", "A name");
		update.add("address", "Address");
		update.add("postcode",  "M13 9PL");
		update.add("capacity", "50");
		
		mvc.perform(MockMvcRequestBuilders.post("/venues/1/update")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.params(update)
	 			.accept(MediaType.TEXT_HTML))
				.andExpect(status().isFound())
	 			.andExpect(view().name("redirect:/venues"));

 		verify(venueService).update(savedCaptor.capture(), savedCaptor.capture());
	}
	
		
	// Helpers ----	
	private void mockGet(String url, MediaType mediaType, String viewName, HttpStatus status) throws Exception {
		mvc.perform(get(url).accept(mediaType)).andExpect(status().is(status.value()))
			.andExpect(view().name(viewName));
	}
}
