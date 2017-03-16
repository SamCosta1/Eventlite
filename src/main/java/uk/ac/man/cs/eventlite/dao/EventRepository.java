package uk.ac.man.cs.eventlite.dao;

import org.springframework.data.repository.CrudRepository;

import uk.ac.man.cs.eventlite.entities.Event;

public interface EventRepository extends CrudRepository<Event, Long> {
	
	Iterable<Event> findAllByOrderByDateAscNameAsc();
	Iterable<Event> findAllByOrderByDateAscTimeAsc();
	Event findById(long id);
	Iterable<Event> findByNameContainingIgnoreCaseOrderByDateAscNameAsc(String name);
}
