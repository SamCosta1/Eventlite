package uk.ac.man.cs.eventlite.controllers;

import static uk.ac.man.cs.eventlite.helpers.ErrorHelpers.*;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.social.ApiException;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.social.twitter.api.MessageTooLongException;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.SearchEvents;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.User;
import uk.ac.man.cs.eventlite.helpers.CurrentUser;

@Controller
@RequestMapping("/events")
public class EventsControllerWeb {
	
	private Twitter twitter;
    private ConnectionRepository connectionRepository;

	@Autowired
	private EventService eventService;
	
	@Autowired
	private VenueService venueService;
	
	@Inject
    public EventsControllerWeb(Twitter twitter, ConnectionRepository connectionRepository) {
        this.twitter = twitter;
        this.connectionRepository = connectionRepository;
    }

	@RequestMapping(method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
	public String getAllEvents(Model model, @ModelAttribute("successMessage") String successMessage) {
		
		if (connectionRepository.findPrimaryConnection(Twitter.class) == null) 
            return "redirect:/connect/twitter";
       
		model.addAttribute("events", eventService.findAll());
		addTweets(model);
		model.addAttribute("success", successMessage);
		return "events/index";
	}
	
	@RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
 	public String deleteEvent(@ModelAttribute Event event, Model model, @ModelAttribute("successMessage") String successMessage, final RedirectAttributes redirectAttributes) {		
 		eventService.delete(event);
 		successMessage = event.getName() + " has been deleted successfully!";
		redirectAttributes.addFlashAttribute("successMessage", successMessage);
 		return "redirect:/events";
 	}
	
	@RequestMapping(method = RequestMethod.POST,
					consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, 
					produces = { MediaType.TEXT_HTML_VALUE })
	public String filterEvents(@ModelAttribute("search") SearchEvents searchCriterion, BindingResult result, Model model) {
	
		if (connectionRepository.findPrimaryConnection(Twitter.class) == null) 
            return "redirect:/connect/twitter";
		
		model.addAttribute("events", searchCriterion.search(eventService));
		addTweets(model);
		return "events/index";
	}
	
	@RequestMapping(value = "/userevents",
					method = RequestMethod.POST,
					consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, 
					produces = { MediaType.TEXT_HTML_VALUE })
    public String filterUserEvents(@ModelAttribute("search") SearchEvents searchCriterion, BindingResult result, Model model) {    
		searchCriterion.setUser(getCurrentUser(model));
		model.addAttribute("events", searchCriterion.search(eventService));
		return "events/userevents";
    }

	@RequestMapping(value = "/{id}/update",
				    method = RequestMethod.POST, 
					consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
					produces = { MediaType.TEXT_HTML_VALUE })
	public String updateEvent(@RequestBody @Valid @ModelAttribute Event event, BindingResult errors, 
							  @ModelAttribute("successMessage") String successMessage, @RequestParam(value = "event") long eventID, 
							  Model model, final RedirectAttributes redirectAttributes) {
		if (errors.hasErrors()) {
			model.addAttribute("errors", formErrorHelper(errors));
			model.addAttribute("event", event);

			if (event.getTime() != null)
				model.addAttribute("eventTime", new SimpleDateFormat("HH:mm").format(event.getTime()));

			if (event.getDate() != null)
				model.addAttribute("eventDate", new SimpleDateFormat("yyyy-MM-dd").format(event.getDate()));
			model.addAttribute("venues", venueService.findAllExceptOne(event.getVenue()));

			return "events/eventform";
		}

		eventService.update(eventService.findById(eventID), event);
		successMessage = event.getName() + " has been updated successfully!";
		redirectAttributes.addFlashAttribute("successMessage", successMessage);

		return "redirect:/events";
	}

	@RequestMapping(value = "/{id}/update", method = RequestMethod.GET,
					produces = { MediaType.TEXT_HTML_VALUE })
	public String showUpdateEventForm(@PathVariable("id") long id, Model model) {
	   	Event event = eventService.findById(id);
		model.addAttribute("event", event);
		model.addAttribute("eventDate", new SimpleDateFormat("yyyy-MM-dd").format(event.getDate()));
		model.addAttribute("eventTime", new SimpleDateFormat("HH:mm").format(event.getTime()));
		model.addAttribute("venues", venueService.findAllExceptOne(event.getVenue()));

		return "events/eventform";

	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE,
					MediaType.APPLICATION_JSON_VALUE })
	public String showEvent(@PathVariable("id") long id, Model model) {
		if (connectionRepository.findPrimaryConnection(Twitter.class) == null)
            return "redirect:/connect/twitter";

		Event e = eventService.findById(id);
		model.addAttribute("event", e);
		model.addAttribute("has-map", e.getVenue().hasCoordinates());
		return "events/show";
	}

	@RequestMapping(value = "tweet/{id}", method = RequestMethod.POST,
					produces = { MediaType.TEXT_HTML_VALUE })
	public String createTweetFromForm(@PathVariable("id") long id, @RequestParam("tweet") String tweet, Model model) {
		String errors = tweet(tweet);
		if (errors != null) {
			model.addAttribute("status", "error");
			model.addAttribute("status-message", errors);
		}
		else {
			model.addAttribute("status", "success");
			model.addAttribute("status-message", "Success! You just tweeted:" +
							   twitter.timelineOperations().getUserTimeline().get(0).getText());
		}

		model.addAttribute("event", eventService.findById(id));
		return "events/show";
	}

	@RequestMapping (value = "/new", method = RequestMethod.GET)
	public String showNew(Model model, RedirectAttributes redirectAttributes) {
		if (venueService.count() > 0) {
			model.addAttribute("venues", venueService.findAll());
			return "events/new";
		}
		else {
			String redirectMessage = "There are no venues on record! Please add a venue first and continue.";
			redirectAttributes.addFlashAttribute("redirected", redirectMessage);
			return "redirect:/venues/new";
		}
	}

	@RequestMapping (value = "/userevents", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
	public String showUserEvents(Model model) {
		model.addAttribute("events", eventService.findAllByUser(getCurrentUser(model)));
		return "events/userevents";
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
					produces = { MediaType.TEXT_HTML_VALUE })
	public String createEventFromForm(@RequestBody @Valid @ModelAttribute Event event, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("errors", formErrorHelper(result));
			model.addAttribute("event", event);

			if (event.getDate() != null)
				model.addAttribute("eventDate", new SimpleDateFormat("yyyy-MM-dd").format(event.getDate()));

			if (event.getTime() != null)
				model.addAttribute("eventTime", new SimpleDateFormat("HH:mm").format(event.getTime()));

			model.addAttribute("venues", venueService.findAllExceptOne(event.getVenue()));
			return "events/new";
        }

	    event.setUser(getCurrentUser(model));
	    eventService.save(event);
	    return "redirect:/events";
	}

	// Helper to tweet - returns null if there were no errors.
	private String tweet(String message) {
		String noWhitespace = message.replaceAll("\\s+", "");

		if (noWhitespace.equals("")) {  // Discard whitespace-only tweets.
			return "Your tweet is empty!";
		}

		try   { twitter.timelineOperations().updateStatus(message); }
		catch (MessageTooLongException e) { return "Your tweet is too long!";	   }
		catch (ApiException e)			  { return "Could not connect to twitter"; }
		catch (Exception e)				  { return "Error: " + e.getMessage();	   }

		return null;
	}

	// Helper that returns the current user.
	private static User getCurrentUser(Model model) {
		CurrentUser mapVal = ((CurrentUser)model.asMap().get("currentUser"));
		return mapVal == null ? null : mapVal.getUser();
	}


	// Helper to add tweets and user info to model.
	private void addTweets(Model model) {
		List<Tweet> tweets = twitter.timelineOperations().getUserTimeline();

		if (tweets.size() > 5)
			tweets = tweets.subList(0, 5);

		model.addAttribute("tweets", tweets);
		model.addAttribute("user", twitter.userOperations().getUserProfile());
	}

}