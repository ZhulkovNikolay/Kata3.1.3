package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.services.UserService;
import java.security.Principal;

@RestController
public class MainRestController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

//    @GetMapping("/")
//    public String homePage() {
//        return "home";
//    }
//
//    @GetMapping("/index")
//    public String indexPage() {
//        return "home";
//    }

    //Допустим на хоум пытается зайти как обычный юзер, так и залогиненный
    //тогда мы:
//    @GetMapping("/")
//    public String homePage(Principal principal) {
    //если принципал null, значит пользователь не авторизирован
    //а если не null, значит это  залогиненный уже
//       if (principal != null) {
           //так мы можем узнать роль юзера
//           System.out.println(((Authentication)principal).getAuthorities());
//       }
//        return "home";
//    }

    //Principal - информация о текущем пользователе
    //Можем здесь преобразовывать юзеров
    //@GetMapping("/authenticated")
    @GetMapping("/user")
    public String pageForAuthenticatedUsers(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return "secured part of web service " + user.getUsername() + " " + user.getEmail();
    }

    //сюда могут заходить только админы
    @GetMapping("/admin")
    public String pageOnlyForAdmins() {
        return "admins page";
    }

}
