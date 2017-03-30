package uk.ac.man.cs.eventlite.controllers.login;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import uk.ac.man.cs.eventlite.TestParent;

@AutoConfigureMockMvc
public class LoginControllerWebTest extends TestParent {

	@Autowired
	private MockMvc mvc;

	@Test
	public void testGetUserLoginPage() throws Exception {
		mvc.perform(get("/users/login").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("users/login"));
	}
}
