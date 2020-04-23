package com.ogic.prescriptionsyntheticsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class LoginController {
    @PostMapping(value = "/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Map<String, Object> map, HttpSession session){
        if (!StringUtils.isEmpty(username) && "pass".equals(password)){
            session.setAttribute("loginUser",username);
            return "redirect:/main";
        }
        map.put("msg", "username and password incorrect");
        return "login";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }
}
