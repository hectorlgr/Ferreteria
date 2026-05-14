package com.ferreteria.auth_service.service;

import com.ferreteria.auth_service.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ferreteria.auth_service.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private HashService hashService;
    
    @Autowired
    private RestTemplate restTemplate;


    public String login (String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user == null) return null;
        // compare SHA-1 hashes
        String hashedInput = hashService.sha1(password);
        if (!hashedInput.equals(user.getPassword())) return null;

        return jwtService.generateToken(email, user.getRole());
    }

    public String getRole(String email){
        User user = userRepository.findByEmail(email);
        return user.getRole();
    }

    public String register(String email, String password, String role, String nombre) {
        User existing = userRepository.findByEmail(email);
        if (existing != null) {
            return "Usuario ya existe!";
        }

        // 1. Guardamos en DB de Auth (Fíjate que NO guardamos el nombre aquí)
        User user = new User();
        user.setEmail(email);
        user.setPassword(hashService.sha1(password));
        user.setRole(role);
        userRepository.save(user);

        // 2. Comunicamos al Usuario-Service
        try {
            java.util.Map<String, String> usuarioPerfil = new java.util.HashMap<>();
            usuarioPerfil.put("email", email);
            usuarioPerfil.put("nombre", nombre); // ¡Aquí usamos el nombre que nos enviaron!
            
            // Llama al puerto 9092 (o el que uses para usuario-service)
            restTemplate.postForEntity("http://localhost:9092/api/usuarios", usuarioPerfil, String.class);
            
        } catch (Exception e) {
            System.out.println("No se pudo crear el perfil en usuario-service: " + e.getMessage());
        }

        return "Usuario creado exitosamente!";
    }
}