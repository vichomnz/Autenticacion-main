package PerfulandiaSPA.Autenticacion.controller;

import PerfulandiaSPA.Autenticacion.model.Usuario;
import PerfulandiaSPA.Autenticacion.model.Rol;
import PerfulandiaSPA.Autenticacion.service.AutenticacionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AutenticacionService autenticacionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegister_Created() throws Exception {
        Usuario usuario = new Usuario(0, "Juan Pérez", "juan@example.com", "password123", Arrays.asList(Rol.ROL_USER));
        Usuario usuarioGuardado = new Usuario(1, "Juan Pérez", "juan@example.com", "password123", Arrays.asList(Rol.ROL_USER));
        
        when(autenticacionService.registrar(any(Usuario.class))).thenReturn(usuarioGuardado);

        mockMvc.perform(post("/api/autenticacion/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated());
    }

    @Test
    void testRegister_Conflict_EmailYaRegistrado() throws Exception {
        Usuario usuario = new Usuario(0, "Juan Pérez", "juan@example.com", "password123", Arrays.asList(Rol.ROL_USER));
        Usuario usuarioError = new Usuario(-1, "Juan Pérez", "juan@example.com", "password123", Arrays.asList(Rol.ROL_USER));
        
        when(autenticacionService.registrar(any(Usuario.class))).thenReturn(usuarioError);

        mockMvc.perform(post("/api/autenticacion/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isConflict());
    }

    @Test
    void testLogin_OK() throws Exception {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "juan@example.com");
        credentials.put("contraseña", "password123");
        
        Usuario usuarioResponse = new Usuario(1, "Juan Pérez", "juan@example.com", "password123", Arrays.asList(Rol.ROL_USER));
        
        when(autenticacionService.login(eq("juan@example.com"), eq("password123"))).thenReturn(usuarioResponse);

        mockMvc.perform(post("/api/autenticacion/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk());
    }

    @Test
    void testLogin_Unauthorized_CredencialesIncorrectas() throws Exception {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "juan@example.com");
        credentials.put("contraseña", "wrongpassword");
        
        Usuario usuarioError = new Usuario(0, null, "juan@example.com", null,Arrays.asList(Rol.ROL_USER));
        
        when(autenticacionService.login(eq("juan@example.com"), eq("wrongpassword"))).thenReturn(usuarioError);

        mockMvc.perform(post("/api/autenticacion/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLogout_OK() throws Exception {
        when(autenticacionService.logout()).thenReturn("Cierre de sesión exitoso");

        mockMvc.perform(post("/api/autenticacion/logout"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllUsuarios_OK() throws Exception {
        List<Usuario> usuarios = Arrays.asList(
            new Usuario(1, "Juan Pérez", "juan@example.com", "password123", Arrays.asList(Rol.ROL_USER)),
            new Usuario(2, "Ana Gómez", "ana@example.com", "password456", Arrays.asList(Rol.ROL_ADMIN))
        );
        when(autenticacionService.getAllUsuarios()).thenReturn(usuarios);

        mockMvc.perform(get("/api/autenticacion/usuarios"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllUsuarios_NoContent() throws Exception {
        when(autenticacionService.getAllUsuarios()).thenReturn(List.of());

        mockMvc.perform(get("/api/autenticacion/usuarios"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testEliminarUsuario_OK() throws Exception {
        when(autenticacionService.eliminarUsuario(eq(1))).thenReturn(true);

        mockMvc.perform(delete("/api/autenticacion/usuarios/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testEliminarUsuario_NotFound() throws Exception {
        when(autenticacionService.eliminarUsuario(eq(999))).thenReturn(false);

        mockMvc.perform(delete("/api/autenticacion/usuarios/999"))
                .andExpect(status().isNotFound());
    }
}