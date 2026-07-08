package com.ferreteria.auth_service.service;

import com.ferreteria.auth_service.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
    private WebClient.Builder webClientBuilder;

    // Método para manejar el proceso de login
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user == null)
            return null;
        // compare SHA-1 hashes
        String hashedInput = hashService.sha1(password);
        if (!hashedInput.equals(user.getPassword()))
            return null;

        return jwtService.generateToken(email, user.getRole());
    }

    // Método para obtener el rol de un usuario a partir de su email
    public String getRole(String email) {
        User user = userRepository.findByEmail(email);
        return user.getRole();
    }

    // Método para registrar un nuevo usuario
    public String register(String email, String password, String role, String nombre) {
        User existing = userRepository.findByEmail(email);
        if (existing != null) {
            return "Usuario ya existe!";
        }

        // Guardar en DB de Auth
        User user = new User();
        user.setEmail(email);
        user.setPassword(hashService.sha1(password));
        user.setRole(role);
        userRepository.save(user);

        // Comunicar al Usuario-Service
        try {
            java.util.Map<String, String> usuarioPerfil = new java.util.HashMap<>();
            usuarioPerfil.put("email", email);
            usuarioPerfil.put("nombre", nombre);

            webClientBuilder.build().post()
                    .uri("http://usuario-service/api/usuarios")
                    .bodyValue(usuarioPerfil)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        } catch (Exception e) {
            System.out.println("No se pudo crear el perfil en usuario-service: " + e.getMessage());
        }

        return "Usuario creado exitosamente!";
    }
}