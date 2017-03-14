package uk.ac.man.cs.eventlite.dao;

import uk.ac.man.cs.eventlite.entities.Venue;

public class SearchVenues{
	
	private String name = null;	
	
	public SearchVenues() {		
	}
	
	public SearchVenues(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return "Search term (name): " + name;		
	}
	
	public Iterable<Venue> search(VenueService venueService) {
		if (name != null)
			return venueService.searchByName(name);
		
		return venueService.findAll();
	}
	
}