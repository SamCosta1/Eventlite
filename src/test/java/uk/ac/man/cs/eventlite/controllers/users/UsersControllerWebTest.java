package uk.ac.man.cs.eventlite.controllers.users;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import uk.ac.man.cs.eventlite.TestParent;
import uk.ac.man.cs.eventlite.controllers.UserControllerWeb;
import uk.ac.man.cs.eventlite.dao.UserService;
import uk.ac.man.cs.eventlite.entities.User;
import uk.ac.man.cs.eventlite.helpers.UserCreateForm;

@AutoConfigureMockMvc
public class UsersControllerWebTest extends TestParent{

	private MockMvc mvc;

	@InjectMocks
	private UserControllerWeb userController;

	@Mock
	private UserService userService;

	@Mock
	private User user;

	@Mock
	private UserCreateForm userCreateForm;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(userController).build();
	}

	@Test
	public void testGetFirstUser() throws Exception {
		when(userService.findById(1)).thenReturn(new User());
		mockGet("/users/1", MediaType.TEXT_HTML, "users/show", HttpStatus.OK);
		verify(userService, times(1)).findById(1);
	}

	@Test
	public void testGetNewUserHtml() throws Exception {
		mockGet("/users/new", MediaType.TEXT_HTML, "users/new", HttpStatus.OK);
	}

	@Test
	public void postValidNewUserRequest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/users/new").contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.param("username", "user")
			.param("password", "pass")
			.param("passwordRepeated", "pass")
			.accept(MediaType.TEXT_HTML))
			.andExpect(view().name("redirect:login"));
	}

	@Test
	public void postInValidNewUserRequest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/users/new").contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.param("username", "")
			.param("password", "pass")
			.param("passwordRepeated", "pass")
			.accept(MediaType.TEXT_HTML))
			.andExpect(view().name("users/new"));
	}

	private void mockGet(String url, MediaType mediaType, String viewName, HttpStatus status) throws Exception {
		mvc.perform(get(url).accept(mediaType)).andExpect(status().is(status.value()))
			.andExpect(view().name(viewName));
	}

}