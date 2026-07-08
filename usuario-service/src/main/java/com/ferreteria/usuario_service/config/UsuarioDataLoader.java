package com.ferreteria.usuario_service.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ferreteria.usuario_service.model.Usuario;
import com.ferreteria.usuario_service.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UsuarioDataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) throws Exception {

        if (usuarioRepository.count() == 0) {
            log.info("Iniciando la carga de usuarios por defecto...");

            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setEmail("admin@ferreteria.cl");

            Usuario cliente = new Usuario();
            cliente.setNombre("Cliente Frecuente");
            cliente.setEmail("cliente@ferreteria.cl");

            usuarioRepository.save(admin);
            usuarioRepository.save(cliente);

            log.info("¡Usuarios cargados exitosamente!");
        } else {
            log.info("La base de datos ya contiene usuarios. Omitiendo carga.");
        }
    }
}