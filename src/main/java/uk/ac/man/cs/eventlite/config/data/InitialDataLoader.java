package uk.ac.man.cs.eventlite.config.data;

import java.time.LocalDate;
import java.time.Month;
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
		Venue newVenue1 = new Venue();
		newVenue1.setName("Kilburn 1.1");
		newVenue1.setCapacity(100);
		venueService.save(newVenue1);
		
		Venue newVenue2 = new Venue();
		newVenue2.setName("Stopford 6");
		newVenue2.setCapacity(100);
		venueService.save(newVenue2);
		
		LocalDate futureDate = LocalDate.of( 2020 , Month.FEBRUARY , 11 );
		
		Event newEvent = new Event("Java Lecture", newVenue1, java.sql.Date.valueOf(futureDate),"");
		newEvent.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur eget est gravida, aliquet arcu ac, laoreet nisl. Maecenas feugiat tempus hendrerit. Proin in finibus leo. Ut mattis, lacus id volutpat mollis, magna tortor facilisis elit, vitae vestibulum erat neque vitae ipsum. Morbi quis gravida tellus. Morbi tincidunt turpis vitae augue pharetra elementum. Donec tincidunt, eros nec consequat tempor, nulla nibh volutpat augue, eget porttitor nisl erat nec urna.");
		
		eventService.save(newEvent);
		eventService.save(new Event("Concert", newVenue1, new Date(),""));
		eventService.save(new Event("Pokemon", newVenue2, new Date(),""));
		eventService.save(new Event("Go", newVenue2, new Date(),""));
		eventService.save(new Event("Alvaro Lecture1", newVenue1, new Date(),""));		

	}
}
