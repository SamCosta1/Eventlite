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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.SearchVenues;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
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
		model.addAttribute("venues", venueService.findAll());
		return "venues/index";
	}	
	
	@RequestMapping(value = "/{id}/delete",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,  method = RequestMethod.POST)
 	public String deleteVenue(@PathVariable("id") long id, final RedirectAttributes redirectAttributes) {
		Venue venue = venueService.findById(id);
		Iterable<Event> events = eventService.findAllByVenue(venue);
		
		if (events.iterator().hasNext()) {
			redirectAttributes.addFlashAttribute("status-message", "You cannot delete this venue since some event is linked to it.");
			return "redirect:/venues/{id}";
		}
		else {
			venueService.delete(venue);
			redirectAttributes.addFlashAttribute("status-message", venue.getName() + " has been deleted successfully.");
			return "redirect:/venues";
		}
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
	
	@RequestMapping (value = "/map", method = RequestMethod.GET)
	public String showNew(Model model)	{
		model.addAttribute("venues", venueService.findAll());
	    return "events/new";
	}
	
}