package uk.ac.man.cs.eventlite.dao;

import uk.ac.man.cs.eventlite.entities.Event;

public class SearchEvents{
	
	private String name = null;	
	
	public SearchEvents() {		
	}
	
	public SearchEvents(String name) {
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
	
	public Iterable<Event> search(EventService eventService) {
		if (name != null)
			return eventService.searchByName(name);
		
		return eventService.findAll();
	}
	
}