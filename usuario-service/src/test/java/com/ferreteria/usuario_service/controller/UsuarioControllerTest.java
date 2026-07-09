package com.ferreteria.usuario_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.ferreteria.usuario_service.assembler.UsuarioModelAssembler;
import com.ferreteria.usuario_service.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferreteria.usuario_service.Dto.UsuarioRequestDTO;
import com.ferreteria.usuario_service.model.Usuario;
import com.ferreteria.usuario_service.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
public class UsuarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UsuarioService usuarioService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Usuario usuarioMock;
    private UsuarioRequestDTO dtoMock;

    @BeforeEach
    void setUp() {

        UsuarioModelAssembler assembler = new UsuarioModelAssembler();

        UsuarioController controller = new UsuarioController(usuarioService, assembler);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setNombre("Pedro Pascal");
        usuarioMock.setEmail("pedro@email.com");

        dtoMock = new UsuarioRequestDTO();
        dtoMock.setNombre("Pedro Pascal");
        dtoMock.setEmail("pedro@email.com");
    }

    @Test
    public void testGuardarUsuario() throws Exception {
        // GIVEN
        when(usuarioService.guardarUsuario(any(Usuario.class))).thenReturn(usuarioMock);

        // WHEN & THEN
        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoMock)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Pedro Pascal"))
                .andExpect(jsonPath("$.email").value("pedro@email.com"));

        verify(usuarioService, times(1)).guardarUsuario(any(Usuario.class));
    }

    @Test
    public void testObtenerPorId() throws Exception {
        // GIVEN
        when(usuarioService.obtenerPorId(1L)).thenReturn(usuarioMock);

        // WHEN & THEN
        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Pedro Pascal"))
                .andExpect(jsonPath("$.links[0].href").exists())
                .andExpect(jsonPath("$.links[1].href").exists());

        verify(usuarioService, times(1)).obtenerPorId(1L);
    }

    @Test
    public void testObtenerPorEmail() throws Exception {
        // GIVEN
        when(usuarioService.obtenerPorEmail("pedro@email.com")).thenReturn(usuarioMock);

        // WHEN & THEN
        mockMvc.perform(get("/api/usuarios/email/pedro@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.links[0].href").exists());

        verify(usuarioService, times(1)).obtenerPorEmail("pedro@email.com");
    }

    @Test
    public void testObtenerTodos() throws Exception {
        // GIVEN
        when(usuarioService.obtenerTodos()).thenReturn(Arrays.asList(usuarioMock));

        // WHEN & THEN
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nombre").value("Pedro Pascal"))
                .andExpect(jsonPath("$.links[0].href").exists());

        verify(usuarioService, times(1)).obtenerTodos();
    }

    @Test
    public void testActualizarUsuario() throws Exception {
        // GIVEN
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(1L);
        usuarioActualizado.setNombre("Pedro Editado");
        usuarioActualizado.setEmail("pedro@email.com");

        when(usuarioService.actualizarUsuario(any(Long.class), any(Usuario.class))).thenReturn(usuarioActualizado);

        // WHEN & THEN
        mockMvc.perform(put("/api/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoMock)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Pedro Editado"));

        verify(usuarioService, times(1)).actualizarUsuario(any(Long.class), any(Usuario.class));
    }

    @Test
    public void testEliminarUsuario() throws Exception {
        // GIVEN
        doNothing().when(usuarioService).eliminarUsuario(1L);

        // WHEN & THEN
        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isNoContent());

        verify(usuarioService, times(1)).eliminarUsuario(1L);
    }
}