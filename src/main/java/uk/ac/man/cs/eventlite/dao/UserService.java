package uk.ac.man.cs.eventlite.dao;

import uk.ac.man.cs.eventlite.entities.User;
import uk.ac.man.cs.eventlite.helpers.UserCreateForm;

public interface UserService {

    public User findById(long id);
    public User findByUsername(String username);
    public Iterable<User> findAll();
    public long count();
    public User save(UserCreateForm form);

}
