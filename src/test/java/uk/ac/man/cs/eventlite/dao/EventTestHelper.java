package uk.ac.man.cs.eventlite.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

public class EventTestHelper {
	
	private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private static DateFormat timeFormat = new SimpleDateFormat("HH:mm");
	private static Venue testVenue;
	
	public static Event newEvent(String name, Venue venue, String date, String time, String description) {
		
		try {
			return new Event(name, venue, dateFormat.parse(date), timeFormat.parse(time), description);
		} catch (Exception e){ e.printStackTrace();}
		
		return null;
	}
	
	public static Event newEvent(String name, String date, String time, String description) {
		
		return newEvent(name, testVenue, date, time, description);
	}
	
	public static Event newEvent(String name, String date, String time) {
		return newEvent(name, date, time, null);
	}
	
	public static Event newEvent(String name, Venue venue, String date, String time) {
		return newEvent(name, venue, date, time, null);
	}

	public static void init(VenueService venueService) {
		testVenue = new Venue("Test Event Name", 10, null, null, null, null, null);
	
		venueService.save(testVenue);		
		
	}
}
