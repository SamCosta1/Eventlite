package uk.ac.man.cs.eventlite.dao;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.man.cs.eventlite.TestParent;
import uk.ac.man.cs.eventlite.entities.User;
import uk.ac.man.cs.eventlite.helpers.UserCreateForm;

public class UserServiceTest extends TestParent {
	
	@Autowired
	private UserService userService;
	
	UserCreateForm testUserCreateForm = new UserCreateForm();
	
	@Test
	public void testFindAll() {
		
		testUserCreateForm.setUsername("test1");
		testUserCreateForm.setPassword("test1");
		testUserCreateForm.setPasswordRepeated("test1");
		
		userService.save(testUserCreateForm);	
		
		testUserCreateForm.setUsername("test2");
		testUserCreateForm.setPassword("test2");
		testUserCreateForm.setPasswordRepeated("test2");
		
		userService.save(testUserCreateForm);

		List<User> users = (List<User>) userService.findAll();

		long count = userService.count();

		assertThat("findAll should get all users.", count, equalTo((long) users.size()));
	}
	
	@Test
	public void testCount() {
		
		long initialCount = userService.count();
		
		testUserCreateForm.setUsername("test3");
		testUserCreateForm.setPassword("test3");
		testUserCreateForm.setPasswordRepeated("test3");
		
		userService.save(testUserCreateForm);
				
		assertThat("Count should increase by one on save", initialCount + 1, equalTo(userService.count()));		
		
	}

	@Test
	public void save() {
		testUserCreateForm.setUsername("test4");
		testUserCreateForm.setPassword("test4");
		testUserCreateForm.setPasswordRepeated("test4");
		
		userService.save(testUserCreateForm);
		
		List<User> users = (List<User>) userService.findAll();
		
		boolean found = false;
		for (User user : users)
			// Should be sufficient to check events equal, checking venues to ensure links correct
			if (user.getUsername().equals("test4"))
				found = true;			
	
		
		assertTrue("Saved event was saved", found);
		
	}
	
	// Test which makes sure that the findBy methods return the freshly saved user. Independent testing is neglected because 
	// users are created with a form and we can't know the Id before finding the user with the service.
	@Test
	public void testFindBy() {
		testUserCreateForm.setUsername("test6");
		testUserCreateForm.setPassword("test6");
		testUserCreateForm.setPasswordRepeated("test6");
		
		userService.save(testUserCreateForm);
		
		User found = userService.findById(userService.findByUsername("test6").getId());	

		assertTrue("The find by Id method found the correct venue", found.getUsername().equals("test6"));
		
	}
}
