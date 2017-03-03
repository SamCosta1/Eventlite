package uk.ac.man.cs.eventlite.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.man.cs.eventlite.entities.Event;

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
		return eventRepository.findAllByOrderByDateAsc();
	}
	
	@Override
	public Iterable<Event> searchByName(String name) {	
		return eventRepository.findByNameContainingIgnoreCase(name);
	}

	@Override
	public void save(Event event) {
		eventRepository.save(event);		
	}
	
	@Override
	public Event findOne(long id) {
		return eventRepository.findOne(id);
	}

}
