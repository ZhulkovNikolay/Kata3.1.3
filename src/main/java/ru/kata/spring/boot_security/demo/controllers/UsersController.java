package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.entities.Role;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.services.RegistrationService;
import ru.kata.spring.boot_security.demo.services.UserService;
import ru.kata.spring.boot_security.demo.util.UserValidator;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

@Controller
public class UsersController {

    private final RegistrationService registrationService;
    private UserService userService;
    private final UserValidator userValidator;

    @Autowired
    private RoleRepository roleRepository;


    @Autowired
    public UsersController(RegistrationService registrationService, UserValidator userValidator) {
        this.registrationService = registrationService;
        this.userValidator = userValidator;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    //кладем юзера в форму регистрации
    @GetMapping("/admin/registration")
    public String registration(@ModelAttribute("user") User user, Model model) {
        List<Role> allRoles = roleRepository.findAll();
        model.addAttribute("allRoles", allRoles);
        return "/registration";
    }

    //берем юзера ИЗ формы регистрации
    @PostMapping("/admin/registration")
    public String performRegistration(@ModelAttribute("user") User user, BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);//проверили, если человек с таким именем уже есть
        if (bindingResult.hasErrors())
            return "/registration";



        registrationService.register(user);//добавляем пользователя в бд
        return "redirect:/admin/allusers";
    }

    @GetMapping("/user")
    public String pageForAuthenticatedUsers(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        return "user";
    }

    @GetMapping("/admin/user")
    public String pageForViewForAdmin(@RequestParam("username") String username, Model model) {
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return "user";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin";
    }

    @GetMapping("/admin/allusers")
    public String showAllUsers(Model model) {
        List<User> allUsers = userService.findAll();
        model.addAttribute("allUsers", allUsers);
        return "allusers";
    }

    @GetMapping("/admin/deleteUser")
    public String deleteUser(@RequestParam("userId") int id) {
        userService.deleteUser(id);
        return "redirect:/admin/allusers";
    }

    @PostMapping("/admin/updateUserInfo")
    public String updateUser(@RequestParam("userId") int id, Model model) {
        User user = userService.findOne(id);
        model.addAttribute("user", user);
        // registrationService.register(user);//добавляем пользователя в бд
        // userService.update(id, user);
        return "updateUserInfo";
    }

//    @PostMapping("/admin/saveUser")
//    public String saveUser(@ModelAttribute("user") User user) {
//        userService.saveUser(user);
//        //registrationService.register(user);//добавляем пользователя в бд
//        return "redirect:/admin/allusers";
//    }

    @PostMapping("/admin/saveUser")
    public String saveUser(@ModelAttribute User user, BindingResult result) {
        if (result.hasErrors()) {
            return "user";
        }
        // Шифруем пароль с помощью bcrypt
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        //Collection<Role> roles = roleRepository.findAll();
        //model.addAttribute("allRoles", roles);
        // Обновляем информацию о пользователе
        userService.saveUser(user);
        return "redirect:/admin/allusers";
    }
//check box test---------------------------

    @GetMapping("/checkbox")
    public String checkBoxTest(Model model) {
        List<Role> allRoles = roleRepository.findAll();
        model.addAttribute("allRoles", allRoles);
        return "/checkbox";
    }


}
