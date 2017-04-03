package uk.ac.man.cs.eventlite.controllers.events;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventsControllerWebIntegrationTest extends TestParent {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate template;

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
		get("/events");
	}
	
	@Test
	public void testGetFirstEvent() {
		get("/events/1");
	}
	
	@Test
	public void testUserEvents() {
		get("/events/userevents");
	}	

	@Test
	public void testFilterUserEvents() {
		post("/events/userevents", HttpStatus.OK);
	}
	
	@Test
	public void testFilterEvents() {
		post("/events/", HttpStatus.OK);
	}
	
	@Test
	public void testAddNewEvent() {
		get("/events/new");
	}
	
	@Test
	public void testPost() {
		post("/events/new", HttpStatus.OK);
	}

	
	private void get(String url) {
		ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, httpEntity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getHeaders().getContentType().toString(), containsString(MediaType.TEXT_HTML_VALUE));
	}
	
	private void post(String url, HttpStatus status) {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		body.add("","");
		httpEntity = new HttpEntity<Object>(body, headers);
		ResponseEntity<String> response = template.exchange(url, HttpMethod.POST, httpEntity, String.class);
		assertThat(response.getStatusCode(), equalTo(status));
	}
}