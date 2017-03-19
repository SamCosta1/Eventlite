package uk.ac.man.cs.eventlite.controllers.error;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.man.cs.eventlite.TestParent;

@AutoConfigureMockMvc
public class EventsControllerWebTest extends TestParent {

	@Autowired
	private MockMvc mvc;
	

	@Test
	public void testNonExistantURL() throws Exception {		
		mockGet("/error", MediaType.TEXT_HTML, "/error", HttpStatus.OK);		
	}
	
	
	private void mockGet(String url, MediaType mediaType, String viewName, HttpStatus status) throws Exception {
		mvc.perform(get(url).accept(mediaType)).andExpect(status().is(status.value()))
			.andExpect(view().name(viewName));
	}	
}
