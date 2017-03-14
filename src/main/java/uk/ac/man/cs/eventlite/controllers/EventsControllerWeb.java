package uk.ac.man.cs.eventlite.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

	@Autowired
	private EventService eventService;
	
	@Autowired
	private VenueService venueService;

	@RequestMapping(method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
	public String getAllEvents(Model model) {

		model.addAttribute("events", eventService.findAll());
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
	
	
	@RequestMapping(value="/update",
				    method = RequestMethod.POST, 
					consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
					produces = { MediaType.TEXT_HTML_VALUE })
	public String updateEvent(@RequestBody @Valid @ModelAttribute("eventForm") Event event, 
								@RequestParam(value="event") long eventID, Model model) {
		
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

		model.addAttribute("event", eventService.findById(id));

		return "events/show";
	}
	
	
	@RequestMapping (value = "/new", method = RequestMethod.GET)
	public String showNew(Model model) 	{
	  model.addAttribute("venues", venueService.findAll());
	  return "events/new";
	}
	

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String createEventFromForm(@RequestBody @Valid @ModelAttribute Event event,
			                          Model model)	{ 
	  eventService.save(event);
	  return "redirect:/events";
	}
	
}