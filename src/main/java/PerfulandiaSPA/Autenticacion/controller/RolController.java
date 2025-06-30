package PerfulandiaSPA.Autenticacion.controller;

import PerfulandiaSPA.Autenticacion.model.RespuestaAutenticacion;
import PerfulandiaSPA.Autenticacion.model.Usuario;
import PerfulandiaSPA.Autenticacion.service.AutenticacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "API para gestión de roles de usuario")
public class RolController {

    @Autowired
    private AutenticacionService autenticacionService;

    @Operation(summary = "Asignar rol ADMIN", description = "Asigna el rol ADMIN a un usuario existente (requiere rol ADMIN)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rol ADMIN asignado exitosamente",
            content = @Content(schema = @Schema(implementation = RespuestaAutenticacion.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(schema = @Schema(implementation = RespuestaAutenticacion.class)))
    })
    @PostMapping("/asignar-admin/{id_usuario}")
    public ResponseEntity<EntityModel<RespuestaAutenticacion>> asignarRolAdmin(
            @Parameter(description = "ID del usuario a promover a ADMIN", required = true)
            @PathVariable("id_usuario") int idUsuario) {
        
        Usuario usuarioResponse = autenticacionService.asignarRolAdmin(idUsuario);
        RespuestaAutenticacion respuesta;
        HttpStatus status;
        
        if (usuarioResponse.getId_usuario() == 0) {
            respuesta = new RespuestaAutenticacion("Error: Usuario no encontrado", usuarioResponse);
            status = HttpStatus.NOT_FOUND;
        } else {
            respuesta = new RespuestaAutenticacion("Rol ADMIN asignado exitosamente", usuarioResponse);
            status = HttpStatus.OK;
        }

        EntityModel<RespuestaAutenticacion> resource = EntityModel.of(respuesta);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RolController.class)
            .asignarRolAdmin(idUsuario)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthController.class)
            .getAllUsuarios()).withRel("usuarios"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthController.class)
            .eliminarUsuario(idUsuario)).withRel("eliminar-usuario"));
        
        return new ResponseEntity<>(resource, status);
    }
}