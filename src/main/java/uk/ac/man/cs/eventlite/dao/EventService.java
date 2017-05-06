package uk.ac.man.cs.eventlite.dao;

import java.util.List;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.User;
import uk.ac.man.cs.eventlite.entities.Venue;

public interface EventService {

	public long count();
	public void save(Event e);
	public void delete(Event e);

	public List<Event> findAll();
	public List<Event> searchByName(String name);
	public Event findById(long id);
	public void update(Event current, Event changes);
	public Iterable<Event> findAllByUser(User user);
	
	public Iterable<Event> findAllByVenue(Venue venue);
	public Iterable<Event> searchByNameByUser(String name, User user);
	public Iterable<Event>  findThreeSoonestEvents();
}
