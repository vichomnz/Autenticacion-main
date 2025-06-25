package PerfulandiaSPA.Autenticacion.service;


import PerfulandiaSPA.Autenticacion.model.Usuario;
import PerfulandiaSPA.Autenticacion.model.Rol;
import PerfulandiaSPA.Autenticacion.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

        usuario.setRoles(Collections.singletonList(Rol.ROL_USER));
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

    public List<Usuario> getAllUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        usuarios.forEach(usuario -> usuario.setContraseña(null));
        return usuarios;
    }

    public Usuario asignarRolAdmin(int idUsuario) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
        if (usuarioOpt.isEmpty()) {
            return new Usuario();
        }

        Usuario usuario = usuarioOpt.get();
        List<Rol> roles = usuario.getRoles();
        if (!roles.contains(Rol.ROL_ADMIN)) {
            roles.add(Rol.ROL_ADMIN);
            usuario.setRoles(roles);
            usuario = usuarioRepository.save(usuario);
        }
        usuario.setContraseña(null);
        return usuario;
    }

    public boolean eliminarUsuario(int idUsuario) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
        if (usuarioOpt.isEmpty()) {
            return false;
        }
        
        usuarioRepository.deleteById(idUsuario);
        return true;
    }
}