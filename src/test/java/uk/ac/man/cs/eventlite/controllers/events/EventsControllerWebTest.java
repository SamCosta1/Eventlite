package uk.ac.man.cs.eventlite.controllers.events;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.twitter.api.MessageTooLongException;
import org.springframework.social.twitter.api.TimelineOperations;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.social.twitter.api.UserOperations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import uk.ac.man.cs.eventlite.TestParent;
import uk.ac.man.cs.eventlite.controllers.EventsControllerWeb;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.EventTestHelper;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@AutoConfigureMockMvc
public class EventsControllerWebTest extends TestParent {

	private MockMvc mvc;
	
	@InjectMocks
	private EventsControllerWeb eventsController;
	
	@Mock
	private EventService eventService;
	
	@Mock
	private VenueService venueService;
	
	@Mock
	private Event event;
	
	@Mock
	private Venue venue;
	
	@Mock
	private Model model;
	
	@Mock
	private Twitter twitter;
	
	@Mock
	private ConnectionRepository connectionRepository;
	
	@Mock
	private TimelineOperations timelineOperations;
	
	@Mock
	private UserOperations userOperations;
	
	@Mock
	private Connection<Twitter> connection;
	
	@Mock
	private TwitterProfile profile;
		    
	@Mock
	private Tweet tweet;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(eventsController).build();		
		EventTestHelper.init(venueService);
	}

	@Test
	public void testGetAllEvents() throws Exception {
		when(connectionRepository.findPrimaryConnection(Twitter.class)).thenReturn(connection);
		when(twitter.timelineOperations()).thenReturn(timelineOperations);
		when(twitter.userOperations()).thenReturn(userOperations);
		when(userOperations.getUserProfile()).thenReturn(profile);
		when(timelineOperations.getUserTimeline()).thenReturn(Collections.<Tweet>emptyList());
		when(eventService.findAll()).thenReturn(Collections.<Event> emptyList());		
			mockGet("/events", MediaType.TEXT_HTML, "events/index", HttpStatus.OK);
		verify(eventService, times(1)).findAll();
		verify(connectionRepository, times(1)).findPrimaryConnection(Twitter.class);
		verify(twitter, times(1)).timelineOperations();
		verify(timelineOperations, times(1)).getUserTimeline();
	}
	
	@Test
	public void testGetAllEventNoTwitterConnection() throws Exception {
		when(connectionRepository.findPrimaryConnection(Twitter.class)).thenReturn(null);
			mockGet("/events", MediaType.TEXT_HTML, "redirect:/connect/twitter", HttpStatus.FOUND);
		verify(connectionRepository).findPrimaryConnection(Twitter.class);
	}
 	
	@Test
 	public void testUpdateEvent() throws Exception {
 		
		ArgumentCaptor<Event> savedCaptor = ArgumentCaptor.forClass(Event.class);
		MultiValueMap<String, String> update = new LinkedMultiValueMap<String, String>();
		update.add("event", "1");
		update.add("name", "A name");
		update.add("description", "Description");
		update.add("venue.id",  "1");
		update.add("date", "2019-12-01");
		update.add("time", "10:00");
		
		mvc.perform(MockMvcRequestBuilders.post("/events/1/update")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.params(update)
	 			.accept(MediaType.TEXT_HTML))
				.andExpect(status().isFound())
	 			.andExpect(view().name("redirect:/events"));

 		verify(eventService).update(savedCaptor.capture(), savedCaptor.capture());
 	}
 	
	@Test
	public void testShowUpdateForm() throws Exception {		
		when(eventService.findById(3)).thenReturn(event);
		when(event.getVenue()).thenReturn(venue);
		when(event.getTime()).thenReturn(new Date());
		when(venueService.findAllExceptOne(venue)).thenReturn(Collections.<Venue> emptyList());
			mvc.perform(get("/events/3/update").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/eventform"));
		verify(eventService, times(1)).findById(3);
		verify(venueService, times(1)).findAllExceptOne(venue);
		verify(event, times(1)).getVenue();	
		verify(event, times(1)).getTime();
	}	
	
	@Test
	public void testGetFirstEvent() throws Exception {
		when(connectionRepository.findPrimaryConnection(Twitter.class)).thenReturn(connection);
		when(twitter.timelineOperations()).thenReturn(timelineOperations);
		when(eventService.findById(1)).thenReturn(new Event());
			mockGet("/events/1", MediaType.TEXT_HTML, "events/show", HttpStatus.OK);
		verify(eventService, times(1)).findById(1);
	}
	
	@Test
	public void testFilterEvents() throws Exception {
		when(connectionRepository.findPrimaryConnection(Twitter.class)).thenReturn(connection);
		when(twitter.timelineOperations()).thenReturn(timelineOperations);
		when(twitter.userOperations()).thenReturn(userOperations);
		when(userOperations.getUserProfile()).thenReturn(profile);
		when(eventService.searchByName("")).thenReturn(Collections.<Event> emptyList());
			mvc.perform(MockMvcRequestBuilders.post("/events/")
					.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
					.param("name", "testString")
					.accept(MediaType.TEXT_HTML_VALUE))
					.andExpect(view().name("events/index"));
		verify(eventService, times(1)).searchByName("testString");
		verify(connectionRepository, times(1)).findPrimaryConnection(Twitter.class);
		verify(twitter, times(1)).timelineOperations();
		verify(timelineOperations, times(1)).getUserTimeline();
	}
		
	@Test
	public void testGetEventsByUser() throws Exception {
		when(eventService.findAllByUser(null)).thenReturn(Collections.<Event> emptyList());
			mockGet("/events/userevents", MediaType.TEXT_HTML, "events/userevents", HttpStatus.OK);
		verify(eventService, times(1)).findAllByUser(null);
	}
	
	@Test
	public void testDeleteEvent() throws Exception {		
		mvc.perform(MockMvcRequestBuilders.post("/events/1/delete")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.TEXT_HTML)).andExpect(status().isFound())
			.andExpect(view().name("redirect:/events"));		
	}
	
	@Test
	public void testGetNewEventPage() throws Exception {
		when(venueService.findAll()).thenReturn(Collections.<Venue> emptyList());
			mvc.perform(MockMvcRequestBuilders.get("/events/new").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/new"));
		verify(venueService, times(1)).findAll();
	}
		
	@Test
	public void testNewEvent() throws Exception {
		when(venue.getId()).thenReturn(1L);
		ArgumentCaptor<Event> savedCaptor = ArgumentCaptor.forClass(Event.class);
		mvc.perform(MockMvcRequestBuilders.post("/events/new").contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.param("name", "A Name")
			.param("date", "2019-10-10")
			.param("time", "10:00")
			.param("venue.id", "1")
			.param("description", "Desc")			
			.accept(MediaType.TEXT_HTML))
			.andExpect(view().name("redirect:/events"));
		
		verify(eventService).save(savedCaptor.capture());				
		assertTrue(savedCaptor.getValue().equals(EventTestHelper.newEvent("A Name", venue, "10/10/2019", "10:00", "Desc")));
	}
	
	@Test
	public void testValidTweet() throws Exception {
		String tweet_s = "This is a valid tweet";
		testTweet(tweet_s, "success", "Success! You just tweeted:" + tweet_s);
	}
	
	@Test
	public void testEmptyTweet() throws Exception {
		String tweet_s = "";
		testTweet(tweet_s, "error", "Your tweet is empty!");
	}
	
	@Test(expected=MessageTooLongException.class)
	public void testLongTweet() throws Exception {
		String tweet_s = "This is a very very long long tweet, longer than hundred and forty characters, which is the limit, so it should be recognized as an invalid tweet.";
		when(timelineOperations.updateStatus(tweet_s)).thenThrow(new MessageTooLongException(""));
		testTweet(tweet_s, "error", "Your tweet is too long!");
	}
	
	// Helpers ----	
	private void mockGet(String url, MediaType mediaType, String viewName, HttpStatus status) throws Exception {
		mvc.perform(get(url).accept(mediaType)).andExpect(status().is(status.value()))
			.andExpect(view().name(viewName));
	}	
	
	private void testTweet(String tweet_s, String validator, String validator_msg) throws Exception {
		when(tweet.getText()).thenReturn(tweet_s); //needed for the last line of this method
		when(twitter.timelineOperations()).thenReturn(timelineOperations);
		when(timelineOperations.updateStatus(tweet_s)).thenReturn(tweet);
		List<Tweet> tweetList = new ArrayList<Tweet>();
		tweetList.add(0, tweet);
		when(timelineOperations.getUserTimeline()).thenReturn(tweetList);
		mvc.perform(MockMvcRequestBuilders.post("/events/tweet/1")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.param("tweet", tweet_s)
				.accept(MediaType.TEXT_HTML_VALUE))
				.andExpect(view().name("events/show"))
				.andExpect(model().attribute("status", validator))
				.andExpect(model().attribute("status-message", validator_msg));
	}
}
