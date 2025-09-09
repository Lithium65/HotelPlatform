package com.example.hotelservice.controller;

import com.example.hotelservice.domain.Role;
import com.example.hotelservice.domain.User;
import com.example.hotelservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/main/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public String userList(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String username) {

        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> userPage;

        if (username != null && !username.isEmpty()) {
            userPage = userService.findByUsernameContaining(username, pageable);
        } else {
            userPage = userService.getAllUsers(pageable);
        }

        model.addAttribute("userPage", userPage);
        model.addAttribute("currentPage", userPage.getNumber());
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("username", username);

        return "user_list";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("{user}")
    public String userEditForm(@PathVariable User user, Model model){
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        model.addAttribute("userRoles", user.getRoles());
        return "user_edit";
    }

    @PostMapping("{user}")
    public String userSave(@PathVariable User user, @RequestParam String username, @RequestParam(required = false) List<String> roles) {
        user.setUsername(username);

        if (roles != null) {
            Set<Role> userRoles = new HashSet<>();
            for (String role : roles) {
                userRoles.add(Role.valueOf(role));
            }
            user.setRoles(userRoles);
        } else {
            user.setRoles(new HashSet<>());
        }

        userService.save(user);
        return "redirect:/main/user";
    }


}
