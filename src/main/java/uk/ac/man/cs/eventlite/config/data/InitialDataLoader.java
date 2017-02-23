package uk.ac.man.cs.eventlite.config.data;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@Component
@Profile({ "default", "test" })
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {

	private final static Logger log = LoggerFactory.getLogger(InitialDataLoader.class);

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {

		if (eventService.count() > 0 && venueService.count() > 0) {
			log.info("Database already populated. Skipping data initialization.");
			return;
		}
		
		// Initial models.
		Venue newVenue = new Venue();
		newVenue.setName("name1");
		newVenue.setCapacity(100);
		venueService.save(newVenue);
		
		eventService.save(new Event("Java Lecture", 15, new Date()));
		eventService.save(new Event("Concert", 18, new Date()));
		eventService.save(new Event("Pokemon", 11, new Date()));
		eventService.save(new Event("Go", 169, new Date()));
		eventService.save(new Event("Alvaro Lecture", 7, new Date()));
		

	}
}
