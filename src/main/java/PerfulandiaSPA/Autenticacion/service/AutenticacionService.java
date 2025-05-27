package PerfulandiaSPA.Autenticacion.service;


import PerfulandiaSPA.Autenticacion.model.Usuario;
import PerfulandiaSPA.Autenticacion.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AutenticacionService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario registrar(Usuario usuario) {
        // Validar unicidad de email
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            usuario.setId_usuario(-1); // Indicador de error
            usuario.setContraseña(null); // No devolver la contraseña
            return usuario;
        }

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        usuarioGuardado.setContraseña(null); // No devolver la contraseña
        return usuarioGuardado;
    }

    public Usuario login(String email, String contraseña) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null || !usuario.getContraseña().equals(contraseña)) {
            return new Usuario(); // Retorna usuario vacío para indicar error
        }
        usuario.setContraseña(null); // No devolver la contraseña
        return usuario;
    }

    public String logout() {
        return "Sesión cerrada exitosamente";
    }
}