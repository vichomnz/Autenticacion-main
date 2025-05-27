package PerfulandiaSPA.Autenticacion.repository;
import PerfulandiaSPA.Autenticacion.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    boolean existsByEmail(String email);
    Usuario findByEmail(String email);
}