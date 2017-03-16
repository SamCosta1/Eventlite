package uk.ac.man.cs.eventlite.dao;

import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.man.cs.eventlite.entities.Venue;

@Service
public class VenueServiceImpl implements VenueService {

	@Autowired
	private VenueRepository venueRepository;
	
	@Override
	public Iterable<Venue> findAll() {
		return venueRepository.findAllByOrderByNameAsc();
	}
	
	@Override
	public void save(Venue venue) {
		venueRepository.save(venue);
	}
	
	@Override
	public Iterable<Venue> searchByName(String name) {	
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
	public Iterable<Venue> findAllExceptOne(Venue venue) {
		Iterable<Venue> venues = venueRepository.findAll();
		Iterator<Venue> i = venues.iterator();
		while (i.hasNext()) {
			Venue v = i.next();
			if (v.getId() == venue.getId())
				i.remove();
		}
		return venues;
	}

} 
