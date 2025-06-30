package PerfulandiaSPA.Autenticacion.model;

import java.util.ArrayList;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_usuario;

    @Column(length = 50, nullable = false)
    private String nombre;

    @Column(length = 250, nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String contraseña;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Column(name = "rol")
    private List<Rol> roles = new ArrayList<>();
}