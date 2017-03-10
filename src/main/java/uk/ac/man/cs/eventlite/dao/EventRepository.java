package uk.ac.man.cs.eventlite.dao;

import org.springframework.data.repository.CrudRepository;

import uk.ac.man.cs.eventlite.entities.Event;

public interface EventRepository extends CrudRepository<Event, Long> {
	
	Iterable<Event> findAllByOrderByDateAscNameAsc();
	Event findById(long id);
	public Iterable<Event> findByNameContainingIgnoreCaseOrderByDateAsc(String name);
	Iterable<Event> findByNameContainingIgnoreCaseOrderByDateAscNameAsc(String name);
}
