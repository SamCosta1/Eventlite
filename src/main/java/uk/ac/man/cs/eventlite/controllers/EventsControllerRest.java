package uk.ac.man.cs.eventlite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.SearchEvents;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.User;
import uk.ac.man.cs.eventlite.helpers.CurrentUser;

@Controller
@RequestMapping("/events")
public class EventsControllerRest {

	@Autowired
	private EventService eventService;

	@RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public String getAllEvents(Model model, UriComponentsBuilder b) {
		
		UriComponents link = b.path("/").build();
		model.addAttribute("self_link", link.toUri());
		model.addAttribute("events", eventService.findAll());
		
		return "events/index";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public String getOneEvent(@PathVariable("id") long id, Model model, UriComponentsBuilder b) { 
		
		UriComponents link = b.path("/").build();
		model.addAttribute("self_link", link.toUri());
		
		try{
			Event e = eventService.findById(id);
			model.addAttribute("event", e);
			model.addAttribute("venue", e.getVenue());
		} catch (Exception e) {}
		
		return "events/_detail";
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = { MediaType.APPLICATION_JSON_VALUE })
	public HttpEntity<Iterable<Event>> filterEvents(@RequestBody SearchEvents searchCriterion) {
		return new ResponseEntity<Iterable<Event>>(searchCriterion.search(eventService), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public @ResponseStatus ResponseEntity<?> delete(@ModelAttribute Event event) {
		eventService.delete(event);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@RequestMapping(value = "/userevents", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public String getUserEvents(Model model, UriComponentsBuilder b) {
		
		UriComponents link = b.path("/").build();
		model.addAttribute("self_link", link.toUri());
		model.addAttribute("events", eventService.findAllByUser(getCurrentUser(model)));

		return "events/userevents";
	}
	
	// Helper that returns the current user
	private static User getCurrentUser(Model model) {
		CurrentUser mapVal = ((CurrentUser)model.asMap().get("currentUser"));
		return mapVal == null ? null : mapVal.getUser();
	}
}