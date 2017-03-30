package uk.ac.man.cs.eventlite.helpers;

import java.io.Serializable;

public interface UserDetails extends Serializable {
    String getPassword();
    String getUsername();
}