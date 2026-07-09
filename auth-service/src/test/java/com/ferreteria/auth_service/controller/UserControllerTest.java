package com.ferreteria.auth_service.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferreteria.auth_service.Dto.LoginRequestDto;
import com.ferreteria.auth_service.Dto.RegisterRequestDto;
import com.ferreteria.auth_service.service.UserService;
import com.ferreteria.auth_service.exception.GlobalExceptionHandler;
import com.ferreteria.auth_service.exception.UnauthorizedException;
import com.ferreteria.auth_service.exception.BadRequestException;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

        private MockMvc mockMvc;

        @Mock
        private UserService userService;

        private ObjectMapper objectMapper = new ObjectMapper();

        private LoginRequestDto loginDto;
        private RegisterRequestDto registerDto;

        @BeforeEach
        void setUp() {
                UserController controller = new UserController(userService);

                mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .build();

                loginDto = new LoginRequestDto();
                loginDto.setEmail("cliente@correo.com");
                loginDto.setPassword("MiPassword123");

                registerDto = new RegisterRequestDto();
                registerDto.setNombre("Juan Pérez");
                registerDto.setEmail("juan.perez@correo.com");
                registerDto.setPassword("MiPassword123");
        }

        @Test
        public void testLogin_Exito() throws Exception {
                // GIVEN
                when(userService.login("cliente@correo.com", "MiPassword123")).thenReturn("token_jwt_simulado");

                // WHEN & THEN
                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginDto)))
                                .andExpect(status().isOk()) // Espera HTTP 200
                                .andExpect(jsonPath("$.token").value("token_jwt_simulado"));

                verify(userService, times(1)).login("cliente@correo.com", "MiPassword123");
        }

        @Test
        public void testLogin_CredencialesInvalidas_Retorna401() throws Exception {
                when(userService.login("cliente@correo.com", "MiPassword123"))
                                .thenThrow(new UnauthorizedException("Credenciales inválidas"));

                // WHEN & THEN
                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginDto)))
                                .andExpect(status().isUnauthorized()) // Espera HTTP 401
                                .andExpect(jsonPath("$.message").value("Credenciales inválidas"));

                verify(userService, times(1)).login("cliente@correo.com", "MiPassword123");
        }

        @Test
        public void testRegisterCliente_Exito() throws Exception {
                // GIVEN
                when(userService.register("juan.perez@correo.com", "MiPassword123", "CLIENTE", "Juan Pérez"))
                                .thenReturn("Usuario creado exitosamente!");

                // WHEN & THEN
                mockMvc.perform(post("/auth/register/cliente")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerDto)))
                                .andExpect(status().isCreated()) // Espera HTTP 201
                                .andExpect(jsonPath("$.message").value("Usuario creado exitosamente!"));

                verify(userService, times(1)).register("juan.perez@correo.com", "MiPassword123", "CLIENTE",
                                "Juan Pérez");
        }

        @Test
        public void testRegisterAdmin_Exito() throws Exception {
                // GIVEN
                when(userService.register("juan.perez@correo.com", "MiPassword123", "ADMIN", "Juan Pérez"))
                                .thenReturn("Usuario creado exitosamente!");

                // WHEN & THEN
                mockMvc.perform(post("/auth/register/admin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerDto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.message").value("Usuario creado exitosamente!"));

                verify(userService, times(1)).register("juan.perez@correo.com", "MiPassword123", "ADMIN", "Juan Pérez");
        }

        @Test
        public void testRegister_Falla_Retorna400() throws Exception {
                // GIVEN:
                when(userService.register(anyString(), anyString(), anyString(), anyString()))
                                .thenThrow(new BadRequestException("El usuario con este email ya existe"));

                // WHEN & THEN
                mockMvc.perform(post("/auth/register/cliente")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerDto)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("El usuario con este email ya existe"));
        }
}