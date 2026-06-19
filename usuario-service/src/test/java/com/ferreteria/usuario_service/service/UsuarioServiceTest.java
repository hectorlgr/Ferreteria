package com.ferreteria.usuario_service.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ferreteria.usuario_service.model.Usuario;
import com.ferreteria.usuario_service.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    
    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioPrueba;

    @BeforeEach
    void setUp() {
        usuarioPrueba = new Usuario(1L, "Paulo Catalan", "paulo.catalan@duocuc.cl");
    }

    @Test
    void testGuardarUsuario_Exito() {
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioPrueba);

        Usuario resultado = usuarioService.guardarUsuario(new Usuario(null, "Paulo Catalan", "paulo.catalan@duocuc.cl"));

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Paulo Catalan", resultado.getNombre());
        
        
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testObtenerPorId_Exito() {
        // GIVEN
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPrueba));

        // WHEN
        Usuario resultado = usuarioService.obtenerPorId(1L);

        // THEN
        assertNotNull(resultado);
        assertEquals("paulo.catalan@duocuc.cl", resultado.getEmail());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void testObtenerPorId_NoEncontrado_LanzaExcepcion() {
        // GIVEN
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN & THEN
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            usuarioService.obtenerPorId(99L);
        });

        assertEquals("Error: Usuario no encontrado con el ID 99", excepcion.getMessage());
        verify(usuarioRepository, times(1)).findById(99L);
    }
}