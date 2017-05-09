package uk.ac.man.cs.eventlite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Venue;

@Controller
@RequestMapping("/venues")
public class VenuesControllerRest {

	@Autowired
	private VenueService venueService;
	
	@Autowired
	private EventService eventService;

	@RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public String getAllVenues(Model model, UriComponentsBuilder b) {
		UriComponents link = b.path("/").build();
		model.addAttribute("self_link", link.toUri());

		model.addAttribute("venues", venueService.findAll());
		return "venues/index";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public String getOneVenue(@PathVariable("id") long id, Model model, UriComponentsBuilder b) {
		UriComponents link = b.path("/").build();
		model.addAttribute("self_link", link.toUri());

		Venue venue = venueService.findById(id);
		model.addAttribute("venue", venue);
		model.addAttribute("events", eventService.findAllFutureEventsByVenue(venue));	

		return "venues/_detail";
	}

}