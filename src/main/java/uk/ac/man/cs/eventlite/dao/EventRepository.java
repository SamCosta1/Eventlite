package uk.ac.man.cs.eventlite.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.User;
import uk.ac.man.cs.eventlite.entities.Venue;

public interface EventRepository extends CrudRepository<Event, Long> {
	
	Iterable<Event> findAllByOrderByDateDescTimeDescNameAsc();
	Event findById(long id);
	Iterable<Event> findAllByVenue(Venue venue);
	
	List<Event> findAllByUserAndDateAfterOrderByDateAscTimeDescNameAsc(User user, Date d);
	List<Event> findAllByUserAndDateBeforeOrderByDateDescTimeDescNameAsc(User user, Date d);
	
	List<Event> findAllByUserAndNameContainingIgnoreCaseAndDateBeforeOrderByDateDescTimeDescNameAsc(User user, String name, Date d);
	List<Event> findAllByUserAndNameContainingIgnoreCaseAndDateAfterOrderByDateDescTimeAscNameAsc(User user, String name, Date d);
		
	List<Event> findAllByDateBeforeOrderByDateDescTimeDescNameAsc(Date date);
	List<Event> findAllByDateAfterOrderByDateAscTimeAscNameAsc(Date date);
	
	List<Event> findByNameContainingIgnoreCaseAndDateAfterOrderByDateAscTimeAscNameAsc(String name, Date date);
	List<Event> findByNameContainingIgnoreCaseAndDateBeforeOrderByDateDescTimeDescNameAsc(String name, Date date);
}
