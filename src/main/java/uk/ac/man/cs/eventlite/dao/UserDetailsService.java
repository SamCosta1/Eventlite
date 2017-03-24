package uk.ac.man.cs.eventlite.dao;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import uk.ac.man.cs.eventlite.helpers.UserDetails;

public interface UserDetailsService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
