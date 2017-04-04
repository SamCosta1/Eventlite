package uk.ac.man.cs.eventlite.dao;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.User;

public class SearchEvents{
	
	private String name = null;
	private User user = null;	
	
	public SearchEvents() {}
	
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
		if (name != null) {
    		if (user != null)
    			return eventService.searchByNameByUser(name, user);
    		else
    			return eventService.searchByName(name);
		}
		
		if (user != null)
			return eventService.findAllByUser(user);			
		
		return eventService.findAll();
	}

	public void setUser(User currentUser) {
		this.user = currentUser;		
	}
	
}