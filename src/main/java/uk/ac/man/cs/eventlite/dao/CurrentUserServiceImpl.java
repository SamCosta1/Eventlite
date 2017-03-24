package uk.ac.man.cs.eventlite.dao;

import org.springframework.stereotype.Service;

import uk.ac.man.cs.eventlite.helpers.CurrentUser;

@Service
public class CurrentUserServiceImpl implements CurrentUserService {

	@Override
    public boolean canAccessUser(CurrentUser currentUser, Long userId) {
        return currentUser != null && currentUser.getId().equals(userId);
    }

} 