package PerfulandiaSPA.Autenticacion.service;

import java.util.ArrayList; // Añadido para listas mutables
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import PerfulandiaSPA.Autenticacion.model.Usuario;
import PerfulandiaSPA.Autenticacion.model.Rol;
import PerfulandiaSPA.Autenticacion.repository.UsuarioRepository;

public class AutenticacionServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AutenticacionService autenticacionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegistrar() {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");
        usuario.setContraseña("password123");
        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setId_usuario(1);
        usuarioGuardado.setEmail("test@example.com");
        usuarioGuardado.setRoles(Collections.singletonList(Rol.ROL_USER));
        when(usuarioRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(usuarioRepository.save(usuario)).thenReturn(usuarioGuardado);

        Usuario resultado = autenticacionService.registrar(usuario);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId_usuario()).isEqualTo(1);
        assertThat(resultado.getEmail()).isEqualTo("test@example.com");
        assertThat(resultado.getRoles()).contains(Rol.ROL_USER);
        assertThat(resultado.getContraseña()).isNull();
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void testLoginExitoso() {
        Usuario usuario = new Usuario();
        usuario.setId_usuario(1);
        usuario.setEmail("test@example.com");
        usuario.setContraseña("password123");
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(usuario);

        Usuario resultado = autenticacionService.login("test@example.com", "password123");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId_usuario()).isEqualTo(1);
        assertThat(resultado.getEmail()).isEqualTo("test@example.com");
        assertThat(resultado.getContraseña()).isNull();
        verify(usuarioRepository).findByEmail("test@example.com");
    }

    @Test
    void testLoginFallido() {
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(null);

        Usuario resultado = autenticacionService.login("test@example.com", "password123");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId_usuario()).isEqualTo(0);
        assertThat(resultado.getEmail()).isNull();
        assertThat(resultado.getContraseña()).isNull();
        verify(usuarioRepository).findByEmail("test@example.com");
    }

    @Test
    void testGetAllUsuarios() {
        Usuario usuario1 = new Usuario();
        usuario1.setId_usuario(1);
        usuario1.setEmail("test1@example.com");
        usuario1.setContraseña("pass1");
        Usuario usuario2 = new Usuario();
        usuario2.setId_usuario(2);
        usuario2.setEmail("test2@example.com");
        usuario2.setContraseña("pass2");

        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario1, usuario2));

        List<Usuario> resultado = autenticacionService.getAllUsuarios();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getContraseña()).isNull();
        assertThat(resultado.get(1).getContraseña()).isNull();
        verify(usuarioRepository).findAll();
    }

    @Test
    void testAsignarRolAdmin() {
        Usuario usuario = new Usuario();
        usuario.setId_usuario(1);
        usuario.setEmail("test@example.com");
        usuario.setRoles(new ArrayList<>(Collections.singletonList(Rol.ROL_USER)));
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId_usuario(1);
        usuarioActualizado.setEmail("test@example.com");
        usuarioActualizado.setRoles(Arrays.asList(Rol.ROL_USER, Rol.ROL_ADMIN));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(usuario)).thenReturn(usuarioActualizado);

        Usuario resultado = autenticacionService.asignarRolAdmin(1);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getRoles()).contains(Rol.ROL_ADMIN);
        assertThat(resultado.getContraseña()).isNull();
        verify(usuarioRepository).findById(1);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void testEliminarUsuarioExitoso() {
        Usuario usuario = new Usuario();
        usuario.setId_usuario(1);
        usuario.setEmail("test@example.com");
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        boolean resultado = autenticacionService.eliminarUsuario(1);

        assertThat(resultado).isTrue();
        verify(usuarioRepository).findById(1);
        verify(usuarioRepository).deleteById(1);
    }

    @Test
    void testEliminarUsuarioFallido() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.empty());

        boolean resultado = autenticacionService.eliminarUsuario(1);

        assertThat(resultado).isFalse();
        verify(usuarioRepository).findById(1);
    }
}