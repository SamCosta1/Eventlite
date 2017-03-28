package uk.ac.man.cs.eventlite.dao;

import uk.ac.man.cs.eventlite.helpers.CurrentUser;

public interface CurrentUserService {

	boolean canAccessUser(CurrentUser currentUser, Long userId);
}