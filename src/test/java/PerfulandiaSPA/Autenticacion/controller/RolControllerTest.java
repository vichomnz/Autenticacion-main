package PerfulandiaSPA.Autenticacion.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import PerfulandiaSPA.Autenticacion.model.Rol;
import PerfulandiaSPA.Autenticacion.model.Usuario;
import PerfulandiaSPA.Autenticacion.service.AutenticacionService;

import java.util.Arrays;

@WebMvcTest(RolController.class)
class RolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AutenticacionService autenticacionService;

    @Test
    void testAsignarRolAdmin_OK() throws Exception {
        Usuario usuarioResponse = new Usuario(1, "Juan Pérez", "juan@example.com", "password123", Arrays.asList(Rol.ROL_ADMIN));
        
        when(autenticacionService.asignarRolAdmin(eq(1))).thenReturn(usuarioResponse);

        mockMvc.perform(post("/api/roles/asignar-admin/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testAsignarRolAdmin_NotFound_UsuarioNoEncontrado() throws Exception {
        Usuario usuarioError = new Usuario(0, null, null, null, null);
        
        when(autenticacionService.asignarRolAdmin(eq(999))).thenReturn(usuarioError);

        mockMvc.perform(post("/api/roles/asignar-admin/999"))
                .andExpect(status().isNotFound());
    }
}