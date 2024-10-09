package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.services.RegistrationService;
import ru.kata.spring.boot_security.demo.services.UserService;
import ru.kata.spring.boot_security.demo.util.UserValidator;

import java.security.Principal;
import java.util.List;

@Controller
public class UsersController {

    private final RegistrationService registrationService;
    private UserService userService;
    private final UserValidator userValidator;


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
    public String registration(@ModelAttribute("user") User user) {
        return "/registration";
    }

    //берем юзера ИЗ формы регистрации
    @PostMapping("/admin/registration")
    public String performRegistration(@ModelAttribute("user") User user, BindingResult bindingResult) {
        //проверили, если человек с таким именем уже есть
        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors())
            return "/registration";
        //добавляем пользователя в бд
        registrationService.register(user);
      //  return "redirect:/login";
        return "redirect:/admin/allusers";
    }

    @GetMapping("/user")
    public String pageForAuthenticatedUsers(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());
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
        return "updateUserInfo";
    }
    @PostMapping("/admin/saveUser")
    public String saveUser(@ModelAttribute("user") User user) {
        userService.saveUser(user);
        return "redirect:/admin/allusers";
    }
}
