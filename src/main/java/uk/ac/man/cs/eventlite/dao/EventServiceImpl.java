package uk.ac.man.cs.eventlite.dao;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.User;
import uk.ac.man.cs.eventlite.entities.Venue;

@Service
public class EventServiceImpl implements EventService {

	
	@Autowired
	private EventRepository eventRepository;
	
	@Override
	public long count() {		
		return eventRepository.count();
	}
	@Override
	public Iterable<Event> findAll() {		
		List<Event> pastEvents = eventRepository.findAllByDateBeforeOrderByDateDescTimeDescNameAsc(new Date());
		List<Event> futureEvents = eventRepository.findAllByDateAfterOrderByDateAscTimeAscNameAsc(new Date());
		futureEvents.addAll(pastEvents);
		
		return futureEvents;
		
	}
	
	@Override
	public Iterable<Event> searchByName(String name) {	
		List<Event> pastEvents = eventRepository.findByNameContainingIgnoreCaseAndDateBeforeOrderByDateDescTimeDescNameAsc(name, new Date());
		List<Event> futureEvents = eventRepository.findByNameContainingIgnoreCaseAndDateAfterOrderByDateDescTimeDescNameAsc(name, new Date());
		futureEvents.addAll(pastEvents);
		
		return futureEvents;
	}

	@Override
	public void save(Event event) {
		eventRepository.save(event);		
	}

	@Override
	public Event findById(long id) {
		return eventRepository.findById(id);
	}

	@Override
	public void update(Event current, Event changes) {
		current.setName(changes.getName());
		current.setDescription(changes.getDescription());
		current.setVenue(changes.getVenue());
		current.setDate(changes.getDate());
		current.setTime(changes.getTime());
		save(current);
	}
	
	@Override
	public void delete(Event event) {
		eventRepository.delete(event);
	}	
	
	@Override
	public Iterable<Event> findAllByVenue(Venue venue) {	
		return eventRepository.findAllByVenue(venue);
	}
	
	@Override
	public Iterable<Event> findAllByUser(User user) {
		List<Event> pastEvents = eventRepository.findAllByUserAndDateBeforeOrderByDateDescTimeDescNameAsc(user, new Date());
		List<Event> futureEvents = eventRepository.findAllByUserAndDateAfterOrderByDateDescTimeDescNameAsc(user, new Date());
		futureEvents.addAll(pastEvents);
		
		return futureEvents;
	}
	
	@Override
	public Iterable<Event> searchByNameByUser(String name, User user) {
		List<Event> pastEvents = eventRepository.findAllByUserAndNameContainingIgnoreCaseAndDateBeforeOrderByDateDescTimeDescNameAsc(user, name, new Date());
		List<Event> futureEvents = eventRepository.findAllByUserAndNameContainingIgnoreCaseAndDateAfterOrderByDateDescTimeDescNameAsc(user, name, new Date());
		futureEvents.addAll(pastEvents);
		
		return futureEvents;
	}

}
