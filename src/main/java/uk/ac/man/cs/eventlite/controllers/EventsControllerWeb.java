package uk.ac.man.cs.eventlite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uk.ac.man.cs.eventlite.dao.EventService;

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

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public String event(@PathVariable("id") long id, Model model) {

		model.addAttribute("event", eventService.findOne(id));

		return "events/show";
	}
}
