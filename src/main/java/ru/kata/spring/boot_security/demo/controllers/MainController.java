package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class MainController {


    @GetMapping("/")
    public String homePage() {
        return "home";
    }

    //Principal - информация о текущем пользователе
    @GetMapping("/authenticated")
    public String pageForAuthenticatedUsers(Principal principal){
        return "secured part of web service " + principal.getName();
    }

    //сюда могут заходить все у кого есть право READ_PROFILE (лежит в нашей БД)
    @GetMapping("/read_profile")
    public String pageForReadProfile(){
        return "read profile page";
    }
   //сюда могут заходить только админы
    @GetMapping("/only_for_admins")
    public String pageOnlyForAdmins(){
        return "admins page";
    }

}
