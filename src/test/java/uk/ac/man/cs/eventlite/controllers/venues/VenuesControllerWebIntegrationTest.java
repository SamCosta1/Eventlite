package uk.ac.man.cs.eventlite.controllers.venues;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import uk.ac.man.cs.eventlite.TestParent;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Venue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VenuesControllerWebIntegrationTest extends TestParent {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate template;
	
	@Autowired
	private VenueService venueService;

	private HttpEntity<?> httpEntity;
	private HttpHeaders headers;

	@Before
	public void setup() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
		httpEntity = new HttpEntity<String>(headers);
	}

	@Test
	public void testGetAllVenues() {
		get("/venues", "");
	}
	
	@Test
	public void testGetUpdateForm() {
		get("/venues/1/update", "");
	}
	
	@Test
	public void testFilterVenues() {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		body.add("name", "test");
		post("/venues/", HttpStatus.OK, body);
	}
	
	@Test
	public void testVenueDetails() {
		get("/venues/1", "");
	}
	
	@Test
	public void testNewVenuePage() {
		get("/venues/new", "");
	}
	
	@Test
	public void testAddValidNewVenue() {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		body.add("name", "A valid venue");
		body.add("addressLine1", "Kilburn Building Manchester");
		body.add("addressLine2", "University of Manchester");
		body.add("streetName", "Oxford Rd");
		body.add("postcode", "M13 9PL");
		body.add("capacity", "100");
		
		post("/venues/new", HttpStatus.OK, body);
		
		boolean found = false;
		for (Venue v : venueService.findAll())
			if (v.getName().equals("A valid venue"))
				found = true;
			
		assertTrue("Venue was created", found);
	}
	
	@Test
	public void testAddInvalidNewVenue() {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		body.add("name", "An invalid venue");
		body.add("addressLine1", "Kilburn Building Manchester");
		body.add("addressLine2", "University of Manchester");
		body.add("streetName", "Oxford Rd");
		body.add("postcode", "M13 9PL");
		body.add("capacity", "-1");
		
		post("/venues/new", HttpStatus.OK, body);
		
		boolean found = false;
		for (Venue v : venueService.findAll())
			if (v.getName().equals("An invalid venue"))
				found = true;
			
		assertFalse("Venue was not created", found);
	}
	
	@Test
	public void testValidUpdateVenue() {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		body.add("venue", "1");
		body.add("name", "An updated valid venue");
		body.add("addressLine1", "Kilburn Building Manchester");
		body.add("addressLine2", "University of Manchester");
		body.add("streetName", "Oxford Rd");
		body.add("postcode", "M13 9PL");
		body.add("capacity", "100");
		
		post("/venues/1/update", HttpStatus.OK, body);
		
		boolean found = false;
		for (Venue v : venueService.findAll())
			if (v.getName().equals("An updated valid venue"))
				found = true;
			
		assertTrue("Venue was updated", found);
	}
	
	@Test
	public void testInvalidUpdateVenue() {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		body.add("venue", "1");
		body.add("name", "A valid venue");
		body.add("addressLine1", "Kilburn Building Manchester");
		body.add("addressLine2", "University of Manchester");
		body.add("streetName", "Oxford Rd");
		body.add("postcode", "M13 9PL");
		body.add("capacity", "-1");
		
		boolean updated = false;
		for (Venue v : venueService.findAll())
			if (v.getName().equals("A valid venue"))
			{
				if (v.getCapacity() == -1)
					updated = true;
				
			}
		
		post("/venues/1/update", HttpStatus.OK, body);
		
		assertFalse("Venue was not updated", updated);
	}

	private void get(String url, String expectedBody) {
		ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, httpEntity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getHeaders().getContentType().toString(), containsString(MediaType.TEXT_HTML_VALUE));
		assertThat(response.getBody(), containsString(expectedBody));
	}
	
	private ResponseEntity<String> post(String url, HttpStatus status, MultiValueMap<String, String> body) {
		HttpEntity<?> httpEntity = new HttpEntity<MultiValueMap<String, String>>(body, headers);
		ResponseEntity<String> response = template.exchange(url, HttpMethod.POST, httpEntity, String.class);
		assertThat(response.getStatusCode(), equalTo(status));
		
		return response;
	}
}