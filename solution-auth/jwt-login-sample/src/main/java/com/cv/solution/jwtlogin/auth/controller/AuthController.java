package com.cv.solution.jwtlogin.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: xutu
 * @since: 2025/10/14 15:41
 */
@RestController
@RequestMapping("/auth")
public class AuthController {


    @PostMapping("/login")
    public String login() {

        return "login success";
    }


}
