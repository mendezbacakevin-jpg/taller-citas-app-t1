package edu.pe.cibertec.taller.controlador;

import edu.pe.cibertec.taller.dto.SolicitudCita;
import edu.pe.cibertec.taller.modelo.Cita;
import edu.pe.cibertec.taller.modelo.Mecanico;
import edu.pe.cibertec.taller.modelo.ResultadoCancelacion;
import edu.pe.cibertec.taller.modelo.TipoServicio;
import edu.pe.cibertec.taller.servicio.ServicioCitas;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/citas")
public class ControladorCitas {

	private final ServicioCitas servicioCitas;

	public ControladorCitas(ServicioCitas servicioCitas) {
		this.servicioCitas = servicioCitas;
	}

	@PostMapping
	public ResponseEntity<Cita> agendar(@RequestBody SolicitudCita solicitud) {
		Cita cita = servicioCitas.agendarCita(solicitud.getIdMecanico(), solicitud.getPlacaVehiculo(),
				solicitud.getTipoServicio(), solicitud.getFechaHoraInicio());
		return ResponseEntity.status(HttpStatus.CREATED).body(cita);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ResultadoCancelacion> cancelar(@PathVariable Long id) {
		ResultadoCancelacion resultado = servicioCitas.cancelarCita(id);
		return ResponseEntity.ok(resultado);
	}

	@GetMapping("/disponibilidad")
	public ResponseEntity<Mecanico> disponibilidad(@RequestParam TipoServicio tipoServicio,
			@RequestParam LocalDateTime fechaHoraInicio) {
		Mecanico mecanico = servicioCitas.buscarMecanicoDisponible(tipoServicio, fechaHoraInicio);
		return ResponseEntity.ok(mecanico);
	}
}
