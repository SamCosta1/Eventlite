package uk.ac.man.cs.eventlite.controllers;

import static uk.ac.man.cs.eventlite.helpers.ErrorHelpers.*;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestParam;

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

	@RequestMapping(value = "/{id}/delete", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, method = RequestMethod.POST)
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

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
					produces = { MediaType.TEXT_HTML_VALUE })
	public String filter(@ModelAttribute("search") SearchVenues searchCriterion, BindingResult result, Model model) {
		model.addAttribute("venues", searchCriterion.search(venueService));
		return "venues/index";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE } )
	public String venue(@PathVariable("id") long id, Model model) {

		Venue venue = venueService.findById(id);
		model.addAttribute("venue", venue);
		model.addAttribute("events", eventService.findAllFutureEventsByVenue(venue));
		
		model.addAttribute("alert", alertMessage);
		
		return "venues/show";
	}

	@RequestMapping (value = "/map", method = RequestMethod.GET)
	public String showNew(Model model) {
		model.addAttribute("venues", venueService.findAll());
	    return "events/new";
	}

	@RequestMapping(value="/{id}/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
	    		    produces = { MediaType.TEXT_HTML_VALUE })
	public String updateVenue(@RequestBody @Valid @ModelAttribute("venueForm") Venue venue, BindingResult errors,
							  @RequestParam(value = "venue") long venueID, Model model) {
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

	@RequestMapping (value = "/new", method = RequestMethod.GET)
	public String showAddVenuesForm(Model model) {
		model.addAttribute("capacity", 1);
		return "venues/new";
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
					produces = { MediaType.TEXT_HTML_VALUE })
	public String createVenueFromForm(@RequestBody @Valid @ModelAttribute Venue venue, BindingResult result, Model model,
			                          @ModelAttribute("successMessage") String successMessage, final RedirectAttributes redirectAttributes,
			                          @RequestParam("redirected") String redirected) {
		model.addAttribute("redirected", redirected);
		if (result.hasErrors()) {
			model.addAttribute("capacity", venue.getCapacity());
			model.addAttribute("venue", venue);
			model.addAttribute("errors", formErrorHelper(result));
			return "venues/new";
		}

		venue.setCoords();
		venueService.save(venue);
		successMessage = venue.getName() + "has been created successfully!";
		redirectAttributes.addFlashAttribute("successMessage", successMessage);

		if (redirected == "")
			return "redirect:/venues";
		else
			return "redirect:/events/new";
	}

}