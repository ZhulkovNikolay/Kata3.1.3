package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.services.RegistrationService;
import ru.kata.spring.boot_security.demo.services.UserService;
import ru.kata.spring.boot_security.demo.util.UserValidator;

import java.security.Principal;

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

    @GetMapping("/hello")
    public String sayHello() {
        return "hello";
    }

    @GetMapping("/showUserInfo")
    public String showUserInfo(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        System.out.println(user.toString() + " " + user.getUsername() + " " + user.getEmail());
        // return "secured part of web service " + user.getUsername() + " " + user.getEmail();
        return "hello";
    }

    //кладем юзера в форму регистрации
    @GetMapping("/registration")
    public String registration(@ModelAttribute("user") User user) {
        return "/registration";
    }

    //берем юзера ИЗ формы регистрации
    @PostMapping("/registration")
    public String performRegistration(@ModelAttribute("user") User user, BindingResult bindingResult) {
        //проверили, если человек с таким именем уже есть
        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors())
            return "/registration";
        //добавляем пользователя в бд
        registrationService.register(user);
        return "redirect:/login";
    }
}
