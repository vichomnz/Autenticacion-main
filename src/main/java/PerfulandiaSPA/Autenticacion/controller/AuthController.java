package PerfulandiaSPA.Autenticacion.controller;

import PerfulandiaSPA.Autenticacion.model.RespuestaAutenticacion;
import PerfulandiaSPA.Autenticacion.model.RespuestaLogout;
import PerfulandiaSPA.Autenticacion.model.Usuario;
import PerfulandiaSPA.Autenticacion.model.Rol;
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
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/autenticacion")
@Tag(name = "Autenticación", description = "API para gestión de autenticación y usuarios")
public class AuthController {

    @Autowired
    private AutenticacionService autenticacionService;

    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un nuevo usuario con rol USER")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente",
            content = @Content(schema = @Schema(implementation = RespuestaAutenticacion.class))),
        @ApiResponse(responseCode = "409", description = "El email ya está registrado",
            content = @Content(schema = @Schema(implementation = RespuestaAutenticacion.class)))
    })
    @PostMapping("/registro")
    public ResponseEntity<EntityModel<RespuestaAutenticacion>> register(
            @Parameter(description = "Datos del usuario a registrar", required = true)
            @RequestBody Usuario usuario) {
        
        Usuario usuarioResponse = autenticacionService.registrar(usuario);
        RespuestaAutenticacion respuesta;
        HttpStatus status;
        
        if (usuarioResponse.getId_usuario() == -1) {
            respuesta = new RespuestaAutenticacion("Error: El email ya está registrado", usuarioResponse);
            status = HttpStatus.CONFLICT;
        } else {
            respuesta = new RespuestaAutenticacion("Registro exitoso", usuarioResponse);
            status = HttpStatus.CREATED;
        }

        EntityModel<RespuestaAutenticacion> resource = EntityModel.of(respuesta);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthController.class)
            .register(usuario)).withSelfRel());
        
        if (status == HttpStatus.CREATED) {
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthController.class)
                .login(Map.of("email", usuario.getEmail(), "contraseña", usuario.getContraseña())))
                .withRel("login"));
        }
        
        return new ResponseEntity<>(resource, status);
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario con email y contraseña")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso",
            content = @Content(schema = @Schema(implementation = RespuestaAutenticacion.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales incorrectas",
            content = @Content(schema = @Schema(implementation = RespuestaAutenticacion.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<EntityModel<RespuestaAutenticacion>> login(
            @Parameter(description = "Credenciales de acceso (email y contraseña)", required = true)
            @RequestBody Map<String, String> credentials) {
        
        String email = credentials.get("email");
        String contraseña = credentials.get("contraseña");
        Usuario usuarioResponse = autenticacionService.login(email, contraseña);
        RespuestaAutenticacion respuesta;
        HttpStatus status;
        
        if (usuarioResponse.getId_usuario() == 0) {
            respuesta = new RespuestaAutenticacion("Error: Credenciales incorrectas", usuarioResponse);
            status = HttpStatus.UNAUTHORIZED;
        } else {
            respuesta = new RespuestaAutenticacion("Inicio de sesión exitoso", usuarioResponse);
            status = HttpStatus.OK;
        }

        EntityModel<RespuestaAutenticacion> resource = EntityModel.of(respuesta);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthController.class)
            .login(credentials)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthController.class)
            .logout()).withRel("logout"));
            
        if (usuarioResponse.getRoles().contains(Rol.ROL_ADMIN)) {
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthController.class)
                .getAllUsuarios()).withRel("usuarios"));
        }
        
        return new ResponseEntity<>(resource, status);
    }

    @Operation(summary = "Cerrar sesión", description = "Finaliza la sesión actual")
    @ApiResponse(responseCode = "200", description = "Sesión cerrada exitosamente",
        content = @Content(schema = @Schema(implementation = RespuestaLogout.class)))
    @PostMapping("/logout")
    public ResponseEntity<EntityModel<RespuestaLogout>> logout() {
        String mensaje = autenticacionService.logout();
        RespuestaLogout respuesta = new RespuestaLogout(mensaje);

        EntityModel<RespuestaLogout> resource = EntityModel.of(respuesta);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthController.class)
            .logout()).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthController.class)
            .login(Map.of("email", "", "contraseña", ""))).withRel("login"));
        
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @Operation(summary = "Obtener todos los usuarios", description = "Lista todos los usuarios registrados (requiere rol ADMIN)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida",
            content = @Content(schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "204", description = "No hay usuarios registrados")
    })
    @GetMapping("/usuarios")
    public ResponseEntity<List<EntityModel<Usuario>>> getAllUsuarios() {
        List<Usuario> usuarios = autenticacionService.getAllUsuarios();
        
        if (usuarios.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        List<EntityModel<Usuario>> usuariosResources = usuarios.stream()
            .map(usuario -> {
                EntityModel<Usuario> resource = EntityModel.of(usuario);
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthController.class)
                    .getAllUsuarios()).withSelfRel());
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthController.class)
                    .eliminarUsuario(usuario.getId_usuario())).withRel("eliminar"));
                
                if (usuario.getRoles().contains(Rol.ROL_USER) && !usuario.getRoles().contains(Rol.ROL_ADMIN)) {
                    resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(RolController.class)
                        .asignarRolAdmin(usuario.getId_usuario())).withRel("asignar-admin"));
                }
                
                return resource;
            })
            .collect(Collectors.toList());
        
        return new ResponseEntity<>(usuariosResources, HttpStatus.OK);
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario por su ID (requiere rol ADMIN)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente",
            content = @Content(schema = @Schema(implementation = RespuestaAutenticacion.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(schema = @Schema(implementation = RespuestaAutenticacion.class)))
    })
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<EntityModel<RespuestaAutenticacion>> eliminarUsuario(
            @Parameter(description = "ID del usuario a eliminar", required = true)
            @PathVariable("id") int idUsuario) {
        
        boolean eliminado = autenticacionService.eliminarUsuario(idUsuario);
        RespuestaAutenticacion respuesta;
        HttpStatus status;
        
        if (!eliminado) {
            respuesta = new RespuestaAutenticacion("Error: Usuario no encontrado", null);
            status = HttpStatus.NOT_FOUND;
        } else {
            respuesta = new RespuestaAutenticacion("Usuario eliminado exitosamente", null);
            status = HttpStatus.OK;
        }

        EntityModel<RespuestaAutenticacion> resource = EntityModel.of(respuesta);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthController.class)
            .eliminarUsuario(idUsuario)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthController.class)
            .getAllUsuarios()).withRel("usuarios"));
        
        return new ResponseEntity<>(resource, status);
    }
}