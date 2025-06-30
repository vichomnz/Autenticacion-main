package PerfulandiaSPA.Autenticacion.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RespuestaAutenticacion extends RepresentationModel<RespuestaAutenticacion> {
    private String mensaje;
    private Usuario usuario;
}