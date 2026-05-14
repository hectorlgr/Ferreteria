package com.ferreteria.auth_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ferreteria.auth_service.service.UserService;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public java.util.Map<String, String> login(@RequestBody java.util.Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String token = userService.login(email, password);

        java.util.Map<String, String> resp = new java.util.HashMap<>();
        if (token == null) {
            resp.put("status", "error");
            resp.put("token", "");
        } else {
            resp.put("status", "ok");
            resp.put("token", token);
        }
        return resp;
    }

    @PostMapping("/register/cliente")
    public java.util.Map<String, String> registerCliente(@RequestBody java.util.Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String resultado = userService.register(email, password, "CLIENTE");

        java.util.Map<String, String> resp = new java.util.HashMap<>();
        resp.put("message", resultado);
        return resp;
    }

    @PostMapping("/register/admin")
    public java.util.Map<String, String> registerAdmin(@RequestBody java.util.Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String resultado = userService.register(email, password, "ADMIN");

        java.util.Map<String, String> resp = new java.util.HashMap<>();
        resp.put("message", resultado);
        return resp;
    }
}