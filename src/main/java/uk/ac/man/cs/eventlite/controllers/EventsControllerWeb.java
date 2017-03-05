package uk.ac.man.cs.eventlite.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.man.cs.eventlite.dao.EventService;
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
	
	@RequestMapping(method = RequestMethod.POST, 
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
}
