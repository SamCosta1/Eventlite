package uk.ac.man.cs.eventlite.controllers.venues;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.mockito.Matchers.isA;
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
		when(eventService.findAllFutureEventsByVenue(venue)).thenReturn(Collections.<Event> emptyList());
			mockGet("/venues/1", MediaType.TEXT_HTML, "venues/show", HttpStatus.OK);
		verify(venueService, times(1)).findById(1);
		verify(eventService, times(1)).findAllFutureEventsByVenue(venue);
	}

	@Test
	public void testDeleteVenueWithNoEvents() throws Exception {
		when(venueService.findById(1)).thenReturn(venue);
		when(eventService.findAllByVenue(venue)).thenReturn(Collections.<Event> emptyList());
		mvc.perform(MockMvcRequestBuilders.post("/venues/1/delete")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.TEXT_HTML)).andExpect(status().isFound())
			.andExpect(view().name("redirect:/venues"))
		    .andExpect(MockMvcResultMatchers.flash().attribute("successMessage", null + " has been deleted successfully."))
		    .andExpect(MockMvcResultMatchers.flash().attributeCount(1));
		verify(venueService, times(1)).findById(1);
		verify(eventService, times(1)).findAllByVenue(venue);
	}

	@Test
	public void testDeleteVenueWithEvents() throws Exception {
		when(venueService.findById(1)).thenReturn(venue);
		when(eventService.findAllByVenue(venue)).thenReturn(events);
		mvc.perform(MockMvcRequestBuilders.post("/venues/1/delete")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.TEXT_HTML)).andExpect(status().isFound())
			.andExpect(view().name("redirect:/venues/{id}"))
		    .andExpect(MockMvcResultMatchers.flash().attribute("alertMessage", "You cannot delete this venue since some event is linked to it."))
	        .andExpect(MockMvcResultMatchers.flash().attributeCount(1));
		verify(venueService, times(1)).findById(1);
		verify(eventService, times(1)).findAllByVenue(venue);
	}

	@Test
	public void showUpdateVenueForm() throws Exception {
 		when(venueService.findById(3)).thenReturn(venue);
		mvc.perform(get("/venues/3/update").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
			.andExpect(view().name("venues/venueform"));
 		verify(venueService, times(1)).findById(3);
	}

	@Test
	public void testAddAndUpdateValidVenue() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();

		params.add("venue", "1");
		params.add("name", "Lecture Theatre");
		params.add("streetName", "street name");
		params.add("postcode",  "M13 9PL");
		params.add("capacity", "50");
		params.add("redirected", "");

		addVenue(params, "redirect:/venues", HttpStatus.FOUND, 1);
		verify(venueService).save(isA(Venue.class));
		
		updateVenue(params, "redirect:/venues", HttpStatus.FOUND, 1);
		verify(venueService).update(isA(Venue.class), isA(Venue.class));
	}

	@Test
	public void testAddAndUpdateInvalidVenue() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();

		// Missing Name.
		params.add("venue", "1");
		params.add("capacity", "100");
		params.add("streetName", "Oxford Road");
		params.add("postcode",  "M13 9PL");
		params.add("redirected", "");

		addVenue(params, "venues/new", HttpStatus.OK, 0);
		updateVenue(params, "venues/venueform", HttpStatus.OK, 0);

		// Invalid Capacity.
		params.clear();
		params.add("venue", "1");
		params.add("name", "Lecture Theatre");
		params.add("capacity", "0");
		params.add("streetName", "Oxford Road");
		params.add("postcode",  "M13 9PL");
		params.add("redirected", "");

		addVenue(params, "venues/new", HttpStatus.OK, 0);
		updateVenue(params, "venues/venueform", HttpStatus.OK, 0);

		// Missing Street Name.
		params.clear();
		params.add("venue", "1");
		params.add("name", "Lecture Theatre");
		params.add("capacity", "100");
		params.add("postcode",  "M13 9PL");
		params.add("redirected", "");
		
		updateVenue(params, "venues/venueform", HttpStatus.OK, 0); 
		addVenue(params, "venues/new", HttpStatus.OK, 0); 
	}

	@Test
	public void testAddValidVenueAfterRedirectFromAddEvent() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();

		params.add("venue", "1");
		params.add("name", "Lecture Theatre");
		params.add("capacity", "100");
		params.add("streetName", "Oxford Road");
		params.add("postcode",  "M13 9PL");
		params.add("redirected", "There are no venues on record! Please add a venue first and continue.");

		addVenue(params, "redirect:/events/new", HttpStatus.FOUND, 1);
		verify(venueService).save(isA(Venue.class));
	}

	@Test
	public void testAddInvalidVenueAfterRedirectFromAddEvent() throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();

		// Missing Name.
		params.add("venue", "1");
		params.add("capacity", "100");
		params.add("streetName", "Oxford Road");
		params.add("postcode",  "M13 9PL");
		params.add("redirected", "There are no venues on record! Please add a venue first and continue.");

		addVenue(params, "venues/new", HttpStatus.OK, 0);

		// Invalid Capacity.
		params.clear();
		params.add("venue", "1");
		params.add("name", "Lecture Theatre");
		params.add("capacity", "0");
		params.add("streetName", "Oxford Road");
		params.add("postcode",  "M13 9PL");
		params.add("redirected", "There are no venues on record! Please add a venue first and continue.");

		addVenue(params, "venues/new", HttpStatus.OK, 0);

		// Missing Street Name.
		params.clear();
		params.add("venue", "1");
		params.add("name", "Lecture Theatre");
		params.add("capacity", "100");
		params.add("postcode",  "M13 9PL");
		params.add("redirected", "There are no venues on record! Please add a venue first and continue.");

		addVenue(params, "venues/new", HttpStatus.OK, 0);

		// Missing Postcode.
		params.clear();
		params.add("venue", "1");
		params.add("name", "Lecture Theatre");
		params.add("capacity", "100");
		params.add("streetName", "Oxford Road");
		params.add("redirected", "There are no venues on record! Please add a venue first and continue.");

		addVenue(params, "venues/new", HttpStatus.OK, 0);
	}
	
	// Helper to test adding venues
	private void addVenue(MultiValueMap<String, String> params, String view, HttpStatus status, int count) throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/venues/new")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.params(params)
				.accept(MediaType.TEXT_HTML))
		.andExpect(status().is(status.value()))
		.andExpect(view().name(view))
		.andExpect(MockMvcResultMatchers.flash().attributeCount(count));
	}

	// Helper to test updating venues
	private void updateVenue(MultiValueMap<String, String> params, String view, HttpStatus status, int count) throws Exception {
		when(venueService.findById(1L)).thenReturn(venue);

		mvc.perform(MockMvcRequestBuilders.post("/venues/1/update")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.params(params)
				.accept(MediaType.TEXT_HTML))
		.andExpect(status().is(status.value()))
		.andExpect(view().name(view))
		.andExpect(MockMvcResultMatchers.flash().attributeCount(count));
	}

	// Helpers ----
	private void mockGet(String url, MediaType mediaType, String viewName, HttpStatus status) throws Exception {
		mvc.perform(get(url).accept(mediaType)).andExpect(status().is(status.value()))
			.andExpect(view().name(viewName));
	}

}