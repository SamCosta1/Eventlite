package uk.ac.man.cs.eventlite.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uk.ac.man.cs.eventlite.dao.UserService;
import uk.ac.man.cs.eventlite.helpers.UserCreateForm;
import uk.ac.man.cs.eventlite.helpers.UserCreateFormValidator;

@Controller
@RequestMapping("/users")
public class UserControllerWeb {

	@Autowired
    private UserService userService;
	@Autowired
    private UserCreateFormValidator userCreateFormValidator;

    @InitBinder("form")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(userCreateFormValidator);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String showUser(@PathVariable("id") long id, Model model) {
    	
    	model.addAttribute("user", userService.findById(id));
    	
    	return "users/show";
    }  
    
    @RequestMapping (value = "/new", method = RequestMethod.GET)
	public String showNew(Model model) 	{
	  model.addAttribute("form", new UserCreateForm());
	  return "users/new";
	}
	

	@RequestMapping(value = "/new", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = { MediaType.TEXT_HTML_VALUE })
	public String createUserFromForm(@RequestBody @Valid @ModelAttribute UserCreateForm form, BindingResult result,
			                          Model model)	{ 
		if (result.hasErrors()) {
            return "users/new";
        }
        try {
            userService.save(form);
        } catch (DataIntegrityViolationException e) {
        	result.reject("username.exists", "Username already exists");
            return "users/new";
        }
        return "redirect:/";
	}

}