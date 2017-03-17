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
import uk.ac.man.cs.eventlite.dao.SearchVenues;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Venue;

@Controller
@RequestMapping("/venues")
public class VenuesControllerWeb {
	
	@Autowired
	private VenueService venueService;
	
	@Autowired
	private EventService eventService;

	@RequestMapping(method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
	public String getAllVenues(Model model) {
		return "venues/index";
	}	
	
	@RequestMapping(method = RequestMethod.POST, 
		consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,  
        produces = { MediaType.TEXT_HTML_VALUE })
	public String filter(@ModelAttribute("search") SearchVenues searchCriterion, BindingResult result, Model model) {
		model.addAttribute("venues", searchCriterion.search(venueService)); 
		return "venues/index";
	}
 
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public String venue(@PathVariable("id") long id, Model model) {

		Venue venue = venueService.findById(id);
		model.addAttribute("venue", venue);
		model.addAttribute("events", eventService.findAllByVenue(venue));

		return "venues/show";
	}
	
}