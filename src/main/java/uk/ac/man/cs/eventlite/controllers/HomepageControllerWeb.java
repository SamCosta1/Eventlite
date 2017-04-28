package uk.ac.man.cs.eventlite.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@Controller
@RequestMapping("/")
public class HomepageControllerWeb {

	@Autowired
	private EventService eventService;
		
	@RequestMapping(method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
	public String featured(Model model) {
		
		model.addAttribute("events_soonest3", eventService.findThreeSoonestEvents());
	//	model.addAttribute("venues_top3", (top3venues(eventService.findAll())));
		return "index";
	}	
	
	private Venue[] top3venues(Iterable<Event> iterable) {
		Venue[] the3Venues = new Venue[3];
		for (int i = 0; i < 3; i++) the3Venues[i] = null;
		
		HashMap<Venue, Integer> countAppearances = new HashMap<Venue, Integer>();
		for(Event x : iterable)	{
			if(!countAppearances.containsKey(x.getVenue())) countAppearances.put(x.getVenue(), 1);
			else countAppearances.put(x.getVenue(), countAppearances.get(x.getVenue())+1);
		}
		
		for(Map.Entry<Venue, Integer> entry : countAppearances.entrySet()) {
			if ((the3Venues[0]==null)
					|| (entry.getValue().compareTo(countAppearances.get(the3Venues[0]))>=0)) {
				the3Venues[2]=the3Venues[1];
				the3Venues[1]=the3Venues[0];
				the3Venues[0]=entry.getKey();	
			}
			else if((the3Venues[1]==null)
					|| (entry.getValue().compareTo(countAppearances.get(the3Venues[1]))>=0)) {
				the3Venues[2]=the3Venues[1];
				the3Venues[1]=entry.getKey();
			}
			else if((the3Venues[2]==null)
					|| (entry.getValue().compareTo(countAppearances.get(the3Venues[2]))>=0)) {
				the3Venues[2]=entry.getKey();
			}
		}
		return the3Venues;
	}
	
}
