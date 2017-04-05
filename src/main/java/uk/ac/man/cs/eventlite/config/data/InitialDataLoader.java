package uk.ac.man.cs.eventlite.config.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
		Venue newVenue1 = new Venue("Kilburn 1.1", 100, 
				"Kilburn Building, University of Manchester, Oxford Rd, Manchester",
				"M13 9PL");
		venueService.save(newVenue1);
		
		Venue newVenue2 = new Venue();
		newVenue2.setName("Stopford 6");
		newVenue2.setCapacity(100);
		newVenue2.setAddress("Stopford Building, University of Manchester, Oxford Rd, Manchester");
		newVenue2.setPostcode("M13 9PT");
		newVenue2.setCoords();
		venueService.save(newVenue2);
		
		Venue newVenue3 = new Venue();
		newVenue3.setName("LF 31");
		newVenue3.setCapacity(10);
		newVenue3.setAddress("Kilburn Building, University of Manchester, Oxford Rd, Manchester");
		newVenue3.setPostcode("M13 9PL");
		newVenue2.setCoords();
		venueService.save(newVenue3);
		
		
		DateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date d1, d2, d3, d4, d5;

		
		try {
			d1 = f.parse("25/3/2018 12:00");
			d2 = f.parse("12/4/2018 15:00");
			d3 = f.parse("10/5/2018 17:00");
			d4 = f.parse("02/5/2018 20:00");
			d5 = f.parse("1/2/2018 00:00");
			
			Event newEvent = new Event("Java Lecture", newVenue1, d1, d1, "");
			newEvent.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur eget est gravida, aliquet arcu ac, laoreet nisl. Maecenas feugiat tempus hendrerit. Proin in finibus leo. Ut mattis, lacus id volutpat mollis, magna tortor facilisis elit, vitae vestibulum erat neque vitae ipsum. Morbi quis gravida tellus. Morbi tincidunt turpis vitae augue pharetra elementum. Donec tincidunt, eros nec consequat tempor, nulla nibh volutpat augue, eget porttitor nisl erat nec urna.");
			
			eventService.save(newEvent);
			eventService.save(new Event("Concert", newVenue1, d2, d2, ""));
			eventService.save(new Event("Pokemon", newVenue2, d3, d3, ""));
			eventService.save(new Event("Go", newVenue2, d4, d4, ""));
			eventService.save(new Event("Alvaro Lecture", newVenue1, d5, d5, ""));
			eventService.save(new Event("Alvaro", newVenue1, d3, d3, ""));
			eventService.save(new Event("Lecture", newVenue1, d5, d5, ""));
			eventService.save(new Event("Aro", newVenue1, d5, d5, ""));

		} catch (ParseException e) {
			e.printStackTrace();
		}
		
				

	}
}