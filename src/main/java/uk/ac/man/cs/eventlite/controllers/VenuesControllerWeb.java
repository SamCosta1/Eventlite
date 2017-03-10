package uk.ac.man.cs.eventlite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.man.cs.eventlite.dao.SearchVenues;
import uk.ac.man.cs.eventlite.dao.VenueService;

@Controller
@RequestMapping("/venues")
public class VenuesControllerWeb {

	@Autowired
	private VenueService venueService;

	@RequestMapping(method = RequestMethod.POST,
					consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, 
					produces = { MediaType.TEXT_HTML_VALUE })
	public String filter(@ModelAttribute("search") SearchVenues searchCriterion, BindingResult result, Model model) {
	
		model.addAttribute("venues", searchCriterion.search(venueService));
		return "venues/index";
	}
}