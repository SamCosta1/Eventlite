package uk.ac.man.cs.eventlite.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginControllerWeb {
	
	@RequestMapping(value = "users/login", method = RequestMethod.GET)
    public String loginPage(Model model, @ModelAttribute("successMessage") String successMessage) {
		model.addAttribute("success", successMessage);
		return "users/login";
    }
}
