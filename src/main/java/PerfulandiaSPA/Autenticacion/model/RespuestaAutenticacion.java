package PerfulandiaSPA.Autenticacion.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaAutenticacion {
    private String mensaje;
    private Usuario usuario;
}