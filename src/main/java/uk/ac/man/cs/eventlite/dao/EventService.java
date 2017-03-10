package uk.ac.man.cs.eventlite.dao;

import uk.ac.man.cs.eventlite.entities.Event;

public interface EventService {

	public long count();
	public void save(Event e);
	public void delete(Event e);

	public Iterable<Event> findAll();
	Iterable<Event> searchByName(String name);
	public Event findById(long id);
	public void update(Event current, Event changes);
	
}
