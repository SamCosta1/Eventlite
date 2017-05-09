package uk.ac.man.cs.eventlite.controllers.venues;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import uk.ac.man.cs.eventlite.TestParent;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Venue;

@AutoConfigureMockMvc
public class VenuesControllerRestTest extends TestParent {

	@Autowired
	private MockMvc mvc;

	private Venue venue;

	@Autowired
	private VenueService venueService;

	@Test
	public void testGetAllVenues() throws Exception {
		mvc.perform(get("/venues").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.title", equalTo("EventLite Venues")))
			.andExpect(jsonPath("$._self", equalTo("http://localhost/venues")))
			.andExpect(jsonPath("$.venues", notNullValue()));
	}

	@Test
	public void testGetOneVenue() throws Exception {
		venue = new Venue("Revolution", 1000, "Arch 7", "Deansgate Locks",
				          "Whitworth Street", "Manchester", "M1 5LH");
		venueService.save(venue);

		mvc.perform(get("/venues/" + venue.getId()).accept(MediaType.APPLICATION_JSON))
		    .andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.title", equalTo("EventLite Venue View")))
			.andExpect(jsonPath("$._self", equalTo("http://localhost/venues/" + venue.getId())))
			.andExpect(jsonPath("$.id", equalTo("" + venue.getId())))
			.andExpect(jsonPath("$.name", equalTo("" + venue.getName())))
			.andExpect(jsonPath("$.capacity", equalTo(venue.getCapacity())))
			.andExpect(jsonPath("$.streetName", equalTo("" + venue.getStreetName())))
		    .andExpect(jsonPath("$.postcode", equalTo("" + venue.getPostcode())));
	}

}