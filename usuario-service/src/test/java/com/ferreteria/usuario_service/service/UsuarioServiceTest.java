package com.ferreteria.usuario_service.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import com.ferreteria.usuario_service.exception.ResourceNotFoundException;
import com.ferreteria.usuario_service.exception.BadRequestException;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void testGuardarUsuario_Exito() {
        // GIVEN
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre("Juan Pérez");
        nuevoUsuario.setEmail("juan@email.com");

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setId(1L);
        usuarioGuardado.setNombre("Juan Pérez");
        usuarioGuardado.setEmail("juan@email.com");

        when(usuarioRepository.findByEmail("juan@email.com")).thenReturn(null);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        // WHEN
        Usuario resultado = usuarioService.guardarUsuario(nuevoUsuario);

        // THEN
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("juan@email.com", resultado.getEmail());
        verify(usuarioRepository, times(1)).save(nuevoUsuario);
    }

    @Test
    void testObtenerPorEmail_Exito() {
        // GIVEN
        Usuario usuario = new Usuario();
        usuario.setId(2L);
        usuario.setEmail("maria@email.com");

        when(usuarioRepository.findByEmail("maria@email.com")).thenReturn(usuario);

        // WHEN
        Usuario resultado = usuarioService.obtenerPorEmail("maria@email.com");

        // THEN
        assertNotNull(resultado);
        assertEquals(2L, resultado.getId());
        verify(usuarioRepository, times(1)).findByEmail("maria@email.com");
    }

    @Test
    void testObtenerPorId_NoEncontrado_LanzaExcepcion() {
        // GIVEN
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN & THEN
        ResourceNotFoundException excepcion = assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.obtenerPorId(99L);
        });

        assertEquals("Error: Usuario no encontrado con el ID 99", excepcion.getMessage());
        verify(usuarioRepository, times(1)).findById(99L);
    }

    @Test
    void testActualizarUsuario_Exito() {
        // GIVEN
        Usuario existente = new Usuario();
        existente.setId(1L);
        existente.setNombre("Nombre Viejo");

        Usuario cambios = new Usuario();
        cambios.setNombre("Nombre Nuevo");
        cambios.setEmail("nuevo@email.com");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArguments()[0]);

        // WHEN
        Usuario resultado = usuarioService.actualizarUsuario(1L, cambios);

        // THEN
        assertEquals("Nombre Nuevo", resultado.getNombre());
        assertEquals("nuevo@email.com", resultado.getEmail());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(existente);
    }
}