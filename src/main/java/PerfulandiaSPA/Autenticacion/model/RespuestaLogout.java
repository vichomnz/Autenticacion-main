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
public class RespuestaLogout extends RepresentationModel<RespuestaLogout> {
    private String mensaje;
}