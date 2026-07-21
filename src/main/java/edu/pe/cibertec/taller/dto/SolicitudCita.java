package edu.pe.cibertec.taller.dto;

import edu.pe.cibertec.taller.modelo.TipoServicio;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class SolicitudCita {

	private Long idMecanico;
	private String placaVehiculo;
	private TipoServicio tipoServicio;
	private LocalDateTime fechaHoraInicio;
}
