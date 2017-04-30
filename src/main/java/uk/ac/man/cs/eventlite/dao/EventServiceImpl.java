package uk.ac.man.cs.eventlite.dao;

import java.util.Collections;
import java.util.Comparator;
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
		List<Event> pastEvents = eventRepository.findAllByDateBeforeOrderByDateDescNameAsc(new Date());
		List<Event> futureEvents = eventRepository.findAllByDateAfterOrderByDateAscNameAsc(new Date());
		futureEvents.addAll(pastEvents);
		
		return futureEvents;
		
	}
	
	@Override
	public Iterable<Event> searchByName(String name) {	
		List<Event> pastEvents = eventRepository.findByNameContainingIgnoreCaseAndDateBeforeOrderByDateDescNameAsc(name, new Date());
		List<Event> futureEvents = eventRepository.findByNameContainingIgnoreCaseAndDateAfterOrderByDateAscNameAsc(name, new Date());
		sortByWholeWordMatch(pastEvents, name);
		sortByWholeWordMatch(futureEvents, name);
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
		return eventRepository.findAllByVenueAndDateAfterOrderByDateAscNameAsc(venue, new Date());
	}
	
	@Override
	public Iterable<Event> findAllByUser(User user) {
		List<Event> pastEvents = eventRepository.findAllByUserAndDateBeforeOrderByDateDescNameAsc(user, new Date());
		List<Event> futureEvents = eventRepository.findAllByUserAndDateAfterOrderByDateAscNameAsc(user, new Date());
		futureEvents.addAll(pastEvents);
		
		return futureEvents;
	}
	
	@Override
	public Iterable<Event> searchByNameByUser(String name, User user) {
		List<Event> pastEvents = eventRepository.findAllByUserAndNameContainingIgnoreCaseAndDateBeforeOrderByDateDescNameAsc(user, name, new Date());
		List<Event> futureEvents = eventRepository.findAllByUserAndNameContainingIgnoreCaseAndDateAfterOrderByDateDescNameAsc(user, name, new Date());
		sortByWholeWordMatch(pastEvents, name);
		sortByWholeWordMatch(futureEvents, name);
		futureEvents.addAll(pastEvents);
		
		return futureEvents;
	}
	
	@Override
	public Iterable<Event> findThreeSoonestEvents() {
		return eventRepository.findAllByDateAfterOrderByDateAscNameAsc(new Date()).subList(0, 3);
	}
	
	// Helper to bring whole word matches to the top of the list	
	private List<Event> sortByWholeWordMatch(List<Event> events, final String searchTerm) {
		Collections.sort(events, new Comparator<Event>() {

			@Override
			public int compare(Event e1, Event e2) {
				if (e1.isPastEvent() && !e2.isPastEvent())
					return 1;
				
				if (!e1.isPastEvent() && e2.isPastEvent())
					return -1;
				
				if (wholeWordMatches(e1.getName(), searchTerm) && !wholeWordMatches(e2.getName(), searchTerm)) 
					return -1;
				if (!wholeWordMatches(e1.getName(), searchTerm) && wholeWordMatches(e2.getName(), searchTerm)) 
					return 1;
				return 0;
			}
			
		});
		return events;		
	}
	
	private boolean wholeWordMatches(String str1, String str2) {
		return str1.toLowerCase().trim().equals(str2.trim().toLowerCase());
	}
	

}
