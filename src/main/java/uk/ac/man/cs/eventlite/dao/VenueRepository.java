package uk.ac.man.cs.eventlite.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import uk.ac.man.cs.eventlite.entities.Venue;

public interface VenueRepository extends CrudRepository<Venue, Long> {

	Venue findById(long id);

	List<Venue> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

	List<Venue> findAllByOrderByNameAsc();
	List<Venue> findAll();
}
