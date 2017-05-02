package uk.ac.man.cs.eventlite.controllers.events;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Before;
import org.junit.Ignore;
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
import uk.ac.man.cs.eventlite.entities.Event;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventsControllerWebIntegrationTest extends TestParent {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate template;
	
	@Autowired
	private EventService eventService;

	private HttpEntity<?> httpEntity;
	private HttpHeaders headers;

	@Before
	public void setup() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
		httpEntity = new HttpEntity<String>(headers);
		
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
		get("/events/7/update", "");
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
		body.add("", "");
		post("/events/userevents", HttpStatus.OK, body);
	}
	
	@Test
	public void testFilterEvents() {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		body.add("name", "test");
		post("/events/", HttpStatus.OK, body);
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