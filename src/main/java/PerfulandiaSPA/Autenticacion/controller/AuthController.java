package PerfulandiaSPA.Autenticacion.controller;



import PerfulandiaSPA.Autenticacion.model.RespuestaAutenticacion;
import PerfulandiaSPA.Autenticacion.model.RespuestaLogout;
import PerfulandiaSPA.Autenticacion.model.Usuario;
import PerfulandiaSPA.Autenticacion.service.AutenticacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;


@RestController
@RequestMapping("/api/autenticacion")
public class AuthController {
    @Autowired
    private AutenticacionService autenticacionService;

    @PostMapping("/registro")
    public ResponseEntity<RespuestaAutenticacion> register(@RequestBody Usuario usuario) {
        Usuario usuarioResponse = autenticacionService.registrar(usuario);
        RespuestaAutenticacion respuesta;
        if (usuarioResponse.getId_usuario() == -1) {
            respuesta = new RespuestaAutenticacion("Error: El email ya esta registrado", usuarioResponse);
            return new ResponseEntity<>(respuesta, HttpStatus.CONFLICT);
        }

        respuesta = new RespuestaAutenticacion("Registro exitoso",  usuarioResponse);
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<RespuestaAutenticacion> login(@RequestBody Map<String, String> credentials) {
    String email = credentials.get("email");
    String contraseña = credentials.get("contraseña");
    Usuario usuarioResponse = autenticacionService.login(email, contraseña);
    RespuestaAutenticacion respuesta;
        if (usuarioResponse.getId_usuario() == 0) {
            respuesta = new RespuestaAutenticacion("Error: Credenciales incorrectas", usuarioResponse);
            return new ResponseEntity<>(respuesta, HttpStatus.UNAUTHORIZED);
        }

        respuesta = new RespuestaAutenticacion("Inicio de sesion exitoso", usuarioResponse);
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<RespuestaLogout> logout() {
        String mensaje = autenticacionService.logout();
        RespuestaLogout respuesta = new RespuestaLogout(mensaje);
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        return ResponseEntity.ok(autenticacionService.getAllUsuarios());
    }
}