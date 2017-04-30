package uk.ac.man.cs.eventlite.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@Service
public class VenueServiceImpl implements VenueService {

	@Autowired
	private VenueRepository venueRepository;
	
	@Autowired
	private EventService eventService;
	
	@Override
	public Iterable<Venue> findAll() {
		return venueRepository.findAllByOrderByNameAsc();
	}
	
	@Override
	public void save(Venue venue) {
		venueRepository.save(venue);
	}
	
	@Override
	public void delete(Venue venue) {
		venueRepository.delete(venue);
	}	
	
	@Override
	public List<Venue> searchByName(String name) {	
		return venueRepository.findByNameContainingIgnoreCaseOrderByNameAsc(name);
	}
	
	@Override
	public long count()	{
		return venueRepository.count();
	}

	@Override
	public Venue findById(long id) {
		return venueRepository.findById(id);
	}

	@Override
	public List<Venue> findAllExceptOne(Venue venue) {
		List<Venue> venues = venueRepository.findAll();
		venues.remove(venue);
		
		return venues;
	}

	@Override
	public Venue[] findMostPopularVenues() {
		List<Event> iterable = (List<Event>) eventService.findAll();
		Venue[] the3Venues = new Venue[3];
		for (int i = 0; i < 3; i++) the3Venues[i] = null;
		
		HashMap<Venue, Integer> countAppearances = new HashMap<Venue, Integer>();
		for(Event x : iterable)	{
			if(!countAppearances.containsKey(x.getVenue())) countAppearances.put(x.getVenue(), 1);
			else countAppearances.put(x.getVenue(), countAppearances.get(x.getVenue())+1);
		}
		
		for(Map.Entry<Venue, Integer> entry : countAppearances.entrySet()) {
			if ((the3Venues[0]==null)
					|| (entry.getValue().compareTo(countAppearances.get(the3Venues[0]))>=0)) {
				the3Venues[2]=the3Venues[1];
				the3Venues[1]=the3Venues[0];
				the3Venues[0]=entry.getKey();	
			}
			else if((the3Venues[1]==null)
					|| (entry.getValue().compareTo(countAppearances.get(the3Venues[1]))>=0)) {
				the3Venues[2]=the3Venues[1];
				the3Venues[1]=entry.getKey();
			}
			else if((the3Venues[2]==null)
					|| (entry.getValue().compareTo(countAppearances.get(the3Venues[2]))>=0)) {
				the3Venues[2]=entry.getKey();
			}
		}
		return the3Venues;
	}
} 
