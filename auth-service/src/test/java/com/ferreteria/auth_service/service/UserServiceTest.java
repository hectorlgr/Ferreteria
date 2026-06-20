package com.ferreteria.auth_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import com.ferreteria.auth_service.model.User;
import com.ferreteria.auth_service.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private HashService hashService;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void setUp() {
        lenient().when(webClientBuilder.build()).thenReturn(webClient);
        lenient().when(webClient.post()).thenReturn((WebClient.RequestBodyUriSpec) requestBodyUriSpec);
        lenient().when(requestBodyUriSpec.uri(anyString())).thenReturn((WebClient.RequestBodySpec) requestBodySpec);
        lenient().when(requestBodySpec.bodyValue(any())).thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
        lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("OK"));
    }

    @Test
    void testLogin_Exito_RetornaToken() {
        // GIVEN
        User user = new User();
        user.setEmail("admin@ferreteria.com");
        user.setPassword("hashed_123");
        user.setRole("ADMIN");

        when(userRepository.findByEmail("admin@ferreteria.com")).thenReturn(user);
        when(hashService.sha1("123")).thenReturn("hashed_123");
        when(jwtService.generateToken("admin@ferreteria.com", "ADMIN")).thenReturn("token_jwt_valido");

        // WHEN
        String token = userService.login("admin@ferreteria.com", "123");

        // THEN
        assertEquals("token_jwt_valido", token);
        verify(hashService, times(1)).sha1("123");
    }

    @Test
    void testLogin_CredencialesInvalidas_RetornaNull() {
        // GIVEN
        User user = new User();
        user.setEmail("admin@ferreteria.com");
        user.setPassword("hashed_123");

        when(userRepository.findByEmail("admin@ferreteria.com")).thenReturn(user);
        when(hashService.sha1("clave_falsa")).thenReturn("hash_incorrecto");

        // WHEN
        String token = userService.login("admin@ferreteria.com", "clave_falsa");

        // THEN
        assertNull(token);
    }

    @Test
    void testRegister_UsuarioNuevo_Exito() {
        // GIVEN
        when(userRepository.findByEmail("nuevo@ferreteria.com")).thenReturn(null);
        when(hashService.sha1("1234")).thenReturn("hashed_1234");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // WHEN
        String resultado = userService.register("nuevo@ferreteria.com", "1234", "USER", "Juan Perez");

        // THEN
        assertEquals("Usuario creado exitosamente!", resultado);
        verify(userRepository, times(1)).save(any(User.class));
        verify(webClientBuilder, times(1)).build();
    }

    @Test
    void testRegister_UsuarioYaExiste_RetornaMensajeError() {
        // GIVEN
        User existente = new User();
        existente.setEmail("existente@ferreteria.com");
        when(userRepository.findByEmail("existente@ferreteria.com")).thenReturn(existente);

        // WHEN
        String resultado = userService.register("existente@ferreteria.com", "1234", "USER", "Juan");

        // THEN
        assertEquals("Usuario ya existe!", resultado);
    }
}