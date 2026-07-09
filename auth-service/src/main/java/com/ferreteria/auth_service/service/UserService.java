package com.ferreteria.auth_service.service;

import com.ferreteria.auth_service.exception.BadRequestException;
import com.ferreteria.auth_service.exception.UnauthorizedException;
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

        if (user == null) {
            throw new UnauthorizedException("Credenciales inválidas"); // Lanza 401
        }

        String hashedInput = hashService.sha1(password);
        if (!hashedInput.equals(user.getPassword())) {
            throw new UnauthorizedException("Credenciales inválidas"); // Lanza 401
        }

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
            throw new BadRequestException("El usuario con este email ya existe"); // Lanza 400
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(hashService.sha1(password));
        user.setRole(role);
        userRepository.save(user);

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
            throw new BadRequestException(
                    "Usuario creado en Auth, pero falló la creación del perfil: " + e.getMessage());
        }

        return "Usuario creado exitosamente!";
    }
}