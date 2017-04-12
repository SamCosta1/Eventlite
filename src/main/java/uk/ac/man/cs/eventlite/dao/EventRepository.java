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
	Iterable<Event> findAllByVenueAndDateAfterOrderByDateAscNameAsc(Venue venue, Date d);
	
	List<Event> findAllByUserAndDateAfterOrderByDateAscNameAsc(User user, Date d);
	List<Event> findAllByUserAndDateBeforeOrderByDateDescNameAsc(User user, Date d);
	
	List<Event> findAllByUserAndNameContainingIgnoreCaseAndDateBeforeOrderByDateDescNameAsc(User user, String name, Date d);
	List<Event> findAllByUserAndNameContainingIgnoreCaseAndDateAfterOrderByDateDescNameAsc(User user, String name, Date d);
		
	List<Event> findAllByDateBeforeOrderByDateDescNameAsc(Date date);
	List<Event> findAllByDateAfterOrderByDateAscNameAsc(Date date);
	
	List<Event> findByNameContainingIgnoreCaseAndDateAfterOrderByDateAscNameAsc(String name, Date date);
	List<Event> findByNameContainingIgnoreCaseAndDateBeforeOrderByDateDescNameAsc(String name, Date date);
}
