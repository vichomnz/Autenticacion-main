package PerfulandiaSPA.Autenticacion.controller;

import PerfulandiaSPA.Autenticacion.model.RespuestaAutenticacion;
import PerfulandiaSPA.Autenticacion.model.Usuario;
import PerfulandiaSPA.Autenticacion.service.AutenticacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/roles")
public class RolController {
    @Autowired
    private AutenticacionService autenticacionService;

    @PostMapping("/asignar-admin/{id_usuario}")
    public ResponseEntity<RespuestaAutenticacion> asignarRolAdmin(@PathVariable("id_usuario") int idUsuario) {
        Usuario usuarioResponse = autenticacionService.asignarRolAdmin(idUsuario);
        RespuestaAutenticacion respuesta;
        if (usuarioResponse.getId_usuario() == 0) {
            respuesta = new RespuestaAutenticacion("Error: Usuario no encontrado", usuarioResponse);
            return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
        }

        respuesta = new RespuestaAutenticacion("Rol ADMIN asignado exitosamente", usuarioResponse);
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }
}
