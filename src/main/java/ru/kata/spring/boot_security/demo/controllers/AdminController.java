package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @GetMapping("/")
    public String allUsers(Model model, Principal principal) {
        model.addAttribute("current_user", userService.loadUserByUsername(principal.getName()));
        model.addAttribute("users" ,userService.getUsers());
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("edit_user", new User());
        return "admin";
    }

    @GetMapping("/user")
    public String getUserById(Model model, Principal principal) {
        model.addAttribute("current_user", userService.loadUserByUsername(principal.getName()));
        return "user";
    }

    @GetMapping("/add")
    public String addUser(Model model, Principal principal) {
        model.addAttribute("current_user", userService.loadUserByUsername(principal.getName()));
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.findAll());
        return "addUser";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute("user") User user, @RequestParam("roles") Set<Long> roles) {
        Set<Role> roleSet = new HashSet<>();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        for(Long roleId : roles) {
            roleSet.add(roleService.findById(roleId));
        }
        user.setRoles(roleSet);
        userService.addUser(user);
        return "redirect:/admin/";
    }

//    @GetMapping("edit")
//    public String editUser(@RequestParam("id") long id, Model model) {
//        model.addAttribute("user", userService.getUserById(id));
//        model.addAttribute("roles", roleService.findAll());
//        return "edit";
//    }

    @PostMapping("/edit")
    public String editUser(@ModelAttribute("edit_user") User user, @RequestParam("roles") Set<Long> roles) {
        System.out.println("Editing user");
        Set<Role> roleSet = new HashSet<>();
        for (Long roleId : roles) {
            roleSet.add(roleService.findById(roleId));
        }
        user.setRoles(roleSet);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.updateUserById(user);
        return "redirect:/admin/";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") long id) {
        System.out.println("delete user");
        userService.deleteUserById(id);
        return "redirect:/admin/";
    }

}
