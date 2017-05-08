package uk.ac.man.cs.eventlite.controllers.events;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.*;

import java.text.ParseException;
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
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.EventTestHelper;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventsControllerWebIntegrationTest extends TestParent {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate template;
	
	@Autowired
	private EventService eventService;
	
	@Autowired
	private VenueService venueService;

	private HttpEntity<?> httpEntity;
	private HttpHeaders headers;

	@Before
	public void setup() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
		httpEntity = new HttpEntity<String>(headers);	
		EventTestHelper.init(venueService);
	}

	@Test
	public void testGetAllEvents() {
		get("/events", "");
	}
	
	@Test
	public void testGetEvent() {
		get("/events/1", "");
	}
	
	@Test
	public void testGetUserEvents() {
		get("/events/userevents", "");
	}
	
	@Test
	public void testGetUpdateFrom() {
		get("/events/7/update", "Update");
	}
	
	@Test
	public void testTweet() {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		body.add("tweet", "this is a tweet");
		
		ResponseEntity<String> response = post("/events/tweet/7", HttpStatus.OK, body);
		assertThat(response.getBody(), containsString("twitter"));
	}
	
	@Test
	public void testFilterUserEvents() {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		body.add("name", "test");
		post("/events/userevents", HttpStatus.OK, body);
	}
	
	@Test
	public void testFilterEvents() {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		body.add("name", "test");
		post("/events/", HttpStatus.OK, body);
	}
	
	@Test
	public void testDelete() {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();		
		body.add("", "");
		post("/events/8/delete", HttpStatus.OK, body);
		assertTrue("Saved venue deleted", eventService.findById(8) == null);
	}
	
	@Test
	public void testNewEventPage() {
		get("/events/new", "");
	}
	
	@Test
	public void testAddValidNewEvent() {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		body.add("name", "A test event I'm creating 09DTYH");
		body.add("date", "2019-10-10");
		body.add("time", "10:00");
		body.add("venue.id", "1");
		body.add("description", "Desc")	;
		
		post("/events/new", HttpStatus.OK, body);
		
		boolean found = false;
		for (Event e : eventService.findAll())
			if (e.getName().equals("A test event I'm creating 09DTYH"))
				found = true;
			
		assertTrue("Event was created", found);
	}
	
	@Test
	public void testAddInvalidNewEvent() {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		body.add("name", "Test Event 2340dfggS4");
		body.add("date", ""); // Missing date
		body.add("time", "10:00");
		body.add("venue.id", "1");
		body.add("description", "Desc")	;
		
		post("/events/new", HttpStatus.OK, body);
		
		for (Event e : eventService.findAll())
			assertFalse("Event doesn't exist", e.getName().equals("Test Event 2340dfggS4"));
	}
	
	@Test
	public void testUpdateEventValid() throws ParseException {
		Event e = eventService.findAll().get(0);		
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		body.add("event", e.getId() + "");
		body.add("name", "New Name for amazing event");
		body.add("date", "2019-12-04"); 
		body.add("time", "13:00");
		body.add("venue.id", "2");
		body.add("description", "Desc2")	;
		
		post("/events/" + e.getId() + "/update", HttpStatus.OK, body);
	}
	
	@Test
	public void testUpdateEventInvalid() {
		Event e = eventService.findAll().get(0);		
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		body.add("event", e.getId() + "");
		body.add("name", "");
		body.add("date", ""); 
		body.add("time", "13:00");
		body.add("venue.id", "2");
		body.add("description", "Desc2")	;
		
		post("/events/" + e.getId() + "/update", HttpStatus.OK, body);
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