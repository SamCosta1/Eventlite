package uk.ac.man.cs.eventlite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.entities.Event;

@Controller
@RequestMapping("/events")
public class EventsControllerWeb {

	@Autowired
	private EventService eventService;

	@RequestMapping(method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
	public String getAllEvents(Model model) {

		model.addAttribute("events", eventService.findAll());
		return "events/index";
	}
	
	@RequestMapping(value = "/{id}/update", method = RequestMethod.POST)
	public String updateEvent(@ModelAttribute("eventForm") Event event, BindingResult result,
								Model model) {
		
		eventService.save(event);
		
		return "events/index";
	}
	
	@RequestMapping(value = "/{id}/update", method = RequestMethod.GET)
	public String showUpdateEventForm(@PathVariable("id") long id, Model model) {

		Event event = eventService.findById(id);
		model.addAttribute("eventForm", event);

		return "events/eventform";

	}
}
