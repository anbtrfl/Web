package ru.itmo.wp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.itmo.wp.domain.User;
import ru.itmo.wp.service.UserService;

@Controller
public class UserPage extends Page {
    private final UserService userService;

    public UserPage(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/user/{id}","/user/"})
    public String userPage(@PathVariable(required=false) String id, Model model) {
        User usr = null;
        try {
            usr = userService.findById(Long.valueOf(id));
        } catch (NumberFormatException ignored) {
        }
        model.addAttribute("currentUser", usr);
        return "UserPage";
    }

//    @GetMapping("/user/")
//    public String userPage(Model model) {
//        model.addAttribute("currentUser", null);
//        return "UserPage";
//    }
}
