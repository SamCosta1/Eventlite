package uk.ac.man.cs.eventlite.dao;

import org.springframework.data.repository.CrudRepository;

import uk.ac.man.cs.eventlite.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {
    User findById(long id);
    User findByUsername(String username);
}