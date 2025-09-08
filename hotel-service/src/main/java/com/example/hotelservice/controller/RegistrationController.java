package com.example.hotelservice.controller;

import com.example.hotelservice.domain.Role;
import com.example.hotelservice.domain.User;
import com.example.hotelservice.repos.UserRepo;
import com.example.hotelservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;

@Controller
public class RegistrationController {
    @Autowired
    private UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @GetMapping("/registration")
    public String registration(){
        return "registration";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @PostMapping("/registration")
    public String addUser(User user, Model model, @RequestParam("confirmPassword") String confirmPassword, BindingResult bindingResult) {
        if (!user.getPassword().equals(confirmPassword)) {
            bindingResult.rejectValue("password", "error.user", "Пароли не совпадают!");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Пароли не совпадают!");
            return "registration";
        }
        User check_user = userService.findByUsername(user.getUsername());
        if (check_user != null ) {
            model.addAttribute("errorMessage", "Данный пользователь уже занят!");
            return "registration";
        }
        user.setActive(true);
        if (userService.getAllUsers().isEmpty()) user.setRoles(Collections.singleton(Role.ADMIN));
        else user.setRoles(Collections.singleton(Role.USER));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        return "redirect:/login";
    }
}
