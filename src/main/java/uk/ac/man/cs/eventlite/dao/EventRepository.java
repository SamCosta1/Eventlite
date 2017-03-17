package uk.ac.man.cs.eventlite.dao;

import org.springframework.data.repository.CrudRepository;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

public interface EventRepository extends CrudRepository<Event, Long> {
	
	Iterable<Event> findAllByOrderByDateAscTimeAscNameAsc();
	Event findById(long id);
	Iterable<Event> findByNameContainingIgnoreCaseOrderByDateAscNameAsc(String name);
	Iterable<Event> findAllByVenue(Venue venue);
}
