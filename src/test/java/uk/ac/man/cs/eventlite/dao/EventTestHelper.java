package uk.ac.man.cs.eventlite.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.User;
import uk.ac.man.cs.eventlite.entities.Venue;

public class EventTestHelper {

	private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private static DateFormat timeFormat = new SimpleDateFormat("HH:mm");
	private static Venue testVenue;
	private static User user = null;

	public static Event newEvent(String name, Venue venue, String date, String time, String description) {
		try {
			Event e = new Event(name, venue, dateFormat.parse(date), timeFormat.parse(time), description);

			if (user != null)
				e.setUser(user);
			return e;
		} catch (Exception e) { e.printStackTrace(); }

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

	public static void setUser(User testUser) {
		user = testUser;
	}

	public static void init(VenueService venueService) {
		testVenue = new Venue("Test Event Name", 10, null, null, null, null, null);
		venueService.save(testVenue);
	}

}