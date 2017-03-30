package uk.ac.man.cs.eventlite.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import uk.ac.man.cs.eventlite.entities.User;
import uk.ac.man.cs.eventlite.helpers.UserCreateForm;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
    private UserRepository userRepository;

    @Override
    public User findById(long id) {
        return userRepository.findById(id);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Iterable<User> findAll() {
        return userRepository.findAll();
    }
    
    @Override
	public long count()	{
		return userRepository.count();
	}

    @Override
    public User save(UserCreateForm form) {
        User user = new User();
        user.setUsername(form.getUsername());
        user.setPasswordHash(new BCryptPasswordEncoder().encode(form.getPassword()));
        return userRepository.save(user);
    }

}