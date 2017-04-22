package uk.ac.man.cs.eventlite.dao;

import java.util.List;

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
 	public void update(Venue current, Venue changes) {
 		current.setName(changes.getName());
 		current.setCapacity(changes.getCapacity());
 		current.setAddress(changes.getAddress());
 		save(current);
 	}

} 
