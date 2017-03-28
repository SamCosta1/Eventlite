package uk.ac.man.cs.eventlite.dao;

import org.springframework.data.repository.CrudRepository;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.User;
import uk.ac.man.cs.eventlite.entities.Venue;

public interface EventRepository extends CrudRepository<Event, Long> {
	
	Iterable<Event> findAllByOrderByDateDescTimeDescNameAsc();
	Event findById(long id);
	Iterable<Event> findByNameContainingIgnoreCaseOrderByDateDescTimeDescNameAsc(String name);
	Iterable<Event> findAllByVenue(Venue venue);
	Iterable<Event> findAllByUserOrderByDateDescTimeDescNameAsc(User user);
	Iterable<Event> findAllByUserAndNameContainingIgnoreCaseOrderByDateDescTimeDescNameAsc(User user, String name);
}
