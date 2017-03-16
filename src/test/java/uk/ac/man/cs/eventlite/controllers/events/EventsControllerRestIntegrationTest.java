package uk.ac.man.cs.eventlite.controllers.events;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

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

import uk.ac.man.cs.eventlite.TestParent;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventsControllerRestIntegrationTest extends TestParent {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate template;

	private HttpEntity<String> httpEntity;

	@Before
	public void setup() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
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
	public void testFilterEvents() {
		HttpHeaders postHeaders = new HttpHeaders();
		postHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		postHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> postEntity = new HttpEntity<String>("{ \"name\": \"\" }", postHeaders);

		ResponseEntity<String> response = template.exchange("/events/", HttpMethod.POST, postEntity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getBody(), is(not(equalTo(null))));
	}

	private void get(String url) {
		ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, httpEntity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getHeaders().getContentType().toString(), containsString(MediaType.APPLICATION_JSON_VALUE));
	}
}