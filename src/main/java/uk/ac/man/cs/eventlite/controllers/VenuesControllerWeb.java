package uk.ac.man.cs.eventlite.controllers;

import static uk.ac.man.cs.eventlite.helpers.ErrorHelpers.formErrorHelper;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
	
	@RequestMapping(value="/{id}/update",
		    		method = RequestMethod.POST, 
		    		consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
		    		produces = { MediaType.TEXT_HTML_VALUE })
	public String updateVenue(@RequestBody @Valid @ModelAttribute("venueForm") Venue venue, BindingResult errors,
							@RequestParam(value="venue") long venueID, Model model) {
	
		if (errors.hasErrors()) {
			model.addAttribute("errors", formErrorHelper(errors));
			model.addAttribute("id", venueID);
			model.addAttribute("venueForm", venueService.findById(venueID));
			
			return "venues/venueform";
		}
		
		venueService.update(venueService.findById(venueID), venue);
		
		return "redirect:/venues";
	}
	
	
	@RequestMapping(value = "/{id}/update", method = RequestMethod.GET)
	public String showUpdateVenueForm(@PathVariable("id") long id, Model model) {
		
	   	Venue venue = venueService.findById(id);
		model.addAttribute("venueForm", venue);

		return "venues/venueform";

	}
	
}