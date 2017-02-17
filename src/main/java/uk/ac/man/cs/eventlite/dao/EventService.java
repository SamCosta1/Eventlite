package uk.ac.man.cs.eventlite.dao;

import uk.ac.man.cs.eventlite.entities.Event;

public interface EventService {

	public long count();
	public void save(Event e);

	public Iterable<Event> findAll();
}
