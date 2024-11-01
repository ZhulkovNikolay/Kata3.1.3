package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kata.spring.boot_security.demo.entities.Role;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.services.RegistrationService;
import ru.kata.spring.boot_security.demo.services.UserService;
import ru.kata.spring.boot_security.demo.util.UserValidator;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final RegistrationService registrationService;
    private final UserService userService;
    private final UserValidator userValidator;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    public AdminController(RegistrationService registrationService, UserService userService, UserValidator userValidator) {
        this.registrationService = registrationService;
        this.userService = userService;
        this.userValidator = userValidator;
    }

    //кладем юзера в форму регистрации
    @GetMapping("/registration")
    public String registration(@ModelAttribute("user") User user, Model model) {
        List<Role> allRoles = roleRepository.findAll();
        model.addAttribute("allRoles", allRoles);

        return "/registration";
    }

    //берем юзера ИЗ формы регистрации роль автоматически НЕ добавляется
    @PostMapping("/registration")
    public String performRegistration(@ModelAttribute("user") User user, BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);//проверили, если человек с таким именем уже есть
        if (bindingResult.hasErrors())
            return "/registration";

        registrationService.register(user);//добавляем пользователя в бд
        return "redirect:/admin/allusers";
    }

//  кнопочка Check (не нужна по ТЗ)
//    @GetMapping("/user")
//    public String pageForViewForAdmin(@RequestParam("username") String username, Model model) {
//        User user = userService.findByUsername(username);
//        model.addAttribute("user", user);
//        return "user";
//    }

    @GetMapping("/allusers")
    public String showAllUsers(Model model) {
        List<User> allUsers = userService.findAll();
        model.addAttribute("allUsers", allUsers);
        return "allusers";
    }


    @GetMapping("/deleteUser")
    public String deleteUser(@RequestParam("userId") int id) {
        userService.deleteUser(id);
        return "redirect:/admin/allusers";
    }


    @PostMapping("/updateUserInfo")
    public String updateUser(@RequestParam("userId") int id, Model model) {
        User user = userService.findOne(id);
        model.addAttribute("user", user);

        List<Role> allRoles = roleRepository.findAll();
        model.addAttribute("allRoles", allRoles);
        return "updateUserInfo";
    }


    @PostMapping("/allusers")
    public String updateUserInModal(@RequestParam("userId") int id, Model model) {
        User user = userService.findOne(id);
        model.addAttribute("user", user);

     //   List<Role> allRoles = roleRepository.findAll();
      //  model.addAttribute("allRoles", allRoles);
        return "allusers";
    }

    @PostMapping("/saveUser") //роль автоматически добавляется
    public String saveUser(@ModelAttribute User user, BindingResult result) {
        if (result.hasErrors()) {
            return "user";
        }
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        userService.saveUser(user);
        return "redirect:/admin/allusers";
    }

}
