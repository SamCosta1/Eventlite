package uk.ac.man.cs.eventlite.dao;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

public interface VenueService {

	public Iterable<Venue> findAll();
	
	public void save(Venue venue);
	public long count();

	public Venue findById(long id);
	public Iterable<Venue> findAllExceptOne(Venue venue);

	public Iterable<Venue> searchByName(String name);
}
