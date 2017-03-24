package uk.ac.man.cs.eventlite.dao;

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
		return eventRepository.findAllByOrderByDateDescTimeDescNameAsc();
	}
	
	@Override
	public Iterable<Event> searchByName(String name) {	
		return eventRepository.findByNameContainingIgnoreCaseOrderByDateDescTimeDescNameAsc(name);
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
		return eventRepository.findAllByUserOrderByDateDescTimeDescNameAsc(user);
	}
	
	@Override
	public Iterable<Event> searchByNameByUser(String name, User user) {
		return eventRepository.findAllByUserAndNameContainingIgnoreCaseOrderByDateDescTimeDescNameAsc(user, name);
	}

}
