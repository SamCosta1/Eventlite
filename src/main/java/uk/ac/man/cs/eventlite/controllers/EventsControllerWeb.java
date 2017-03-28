package uk.ac.man.cs.eventlite.controllers;

import static uk.ac.man.cs.eventlite.helpers.ErrorHelpers.*;

import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.SearchEvents;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;

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
	public String getAllEvents(Model model) {
		if (connectionRepository.findPrimaryConnection(Twitter.class) == null) {
            return "redirect:/connect/twitter";
        }
		model.addAttribute("events", eventService.findAll());
		List<Tweet> tweets = twitter.timelineOperations().getUserTimeline();
		if (tweets.size()>5) {
			tweets = tweets.subList(0,5);
		}
		
		model.addAttribute("tweets",tweets);
		model.addAttribute("user", twitter.userOperations().getUserProfile());
		return "events/index";
	}
	
	@RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
 	public String deleteEvent(@ModelAttribute Event event) {
		
 		eventService.delete(event);
 		return "redirect:/events";
 	}
	
	@RequestMapping(method = RequestMethod.POST,
					consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, 
					produces = { MediaType.TEXT_HTML_VALUE })
	public String filter(@ModelAttribute("search") SearchEvents searchCriterion, BindingResult result, Model model) {
	
		model.addAttribute("events", searchCriterion.search(eventService));
		return "events/index";
	}
	
	
	@RequestMapping(value="/{id}/update",
				    method = RequestMethod.POST, 
					consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
					produces = { MediaType.TEXT_HTML_VALUE })
	public String updateEvent(@RequestBody @Valid @ModelAttribute("eventForm") Event event, BindingResult errors,
								@RequestParam(value="event") long eventID, Model model) {
		
		if (errors.hasErrors()) {
			model.addAttribute("errors", formErrorHelper(errors));
			model.addAttribute("id", eventID);
			model.addAttribute("eventForm", eventService.findById(eventID));
			model.addAttribute("venues", venueService.findAllExceptOne(event.getVenue()));
			
			return "events/eventform";
		}
		
		eventService.update(eventService.findById(eventID), event);
		
		return "redirect:/events";
	}
	
	@RequestMapping(value = "/{id}/update", method = RequestMethod.GET)
	public String showUpdateEventForm(@PathVariable("id") long id, Model model) {
		
	   	Event event = eventService.findById(id);
		model.addAttribute("eventForm", event);
		model.addAttribute("venues", venueService.findAllExceptOne(event.getVenue()));

		return "events/eventform";

	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public String event(@PathVariable("id") long id, Model model) {
		if (connectionRepository.findPrimaryConnection(Twitter.class) == null) {
            return "redirect:/connect/twitter";
        }
		model.addAttribute("event", eventService.findById(id));

		return "events/show";
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public String createTweetFromForm(@PathVariable("id") long id, @RequestParam("tweet") String tweet, Model model) {
		if (tweet.equals(""))
		{
			model.addAttribute("error", "Empty tweet");
		}
		else
		{
			twitter.timelineOperations().updateStatus(tweet);
			model.addAttribute("success", twitter.timelineOperations().getUserTimeline().get(0));
			
		}
	    model.addAttribute("event", eventService.findById(id));

		return "events/show";
	}
	
	
	@RequestMapping (value = "/new", method = RequestMethod.GET)
	public String showNew(Model model)	{
		model.addAttribute("venues", venueService.findAll());
	    return "events/new";
	}
	

	@RequestMapping(value = "/new", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = { MediaType.TEXT_HTML_VALUE })
	public String createEventFromForm(@RequestBody @Valid @ModelAttribute Event event, BindingResult result,
			                          Model model)	{ 
	  eventService.save(event);
	  return "redirect:/events";
	}
	
	
	
}