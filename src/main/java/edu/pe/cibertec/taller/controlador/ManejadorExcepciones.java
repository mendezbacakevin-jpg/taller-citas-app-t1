package edu.pe.cibertec.taller.controlador;

import edu.pe.cibertec.taller.excepcion.CitaNoCancelableException;
import edu.pe.cibertec.taller.excepcion.CitaNoEncontradaException;
import edu.pe.cibertec.taller.excepcion.EspecialidadIncorrectaException;
import edu.pe.cibertec.taller.excepcion.FechaInvalidaException;
import edu.pe.cibertec.taller.excepcion.HorarioNoPermitidoException;
import edu.pe.cibertec.taller.excepcion.HorarioOcupadoException;
import edu.pe.cibertec.taller.excepcion.MecanicoNoEncontradoException;
import edu.pe.cibertec.taller.excepcion.SinDisponibilidadException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ManejadorExcepciones {

	@ExceptionHandler({MecanicoNoEncontradoException.class, CitaNoEncontradaException.class})
	public ResponseEntity<Map<String, String>> manejarNoEncontrado(RuntimeException excepcion) {
		return construir(HttpStatus.NOT_FOUND, excepcion.getMessage());
	}

	@ExceptionHandler({HorarioOcupadoException.class, CitaNoCancelableException.class})
	public ResponseEntity<Map<String, String>> manejarConflicto(RuntimeException excepcion) {
		return construir(HttpStatus.CONFLICT, excepcion.getMessage());
	}

	@ExceptionHandler({FechaInvalidaException.class, EspecialidadIncorrectaException.class,
			HorarioNoPermitidoException.class, SinDisponibilidadException.class})
	public ResponseEntity<Map<String, String>> manejarSolicitudInvalida(RuntimeException excepcion) {
		return construir(HttpStatus.BAD_REQUEST, excepcion.getMessage());
	}

	private ResponseEntity<Map<String, String>> construir(HttpStatus estado, String mensaje) {
		Map<String, String> cuerpo = new HashMap<String, String>();
		cuerpo.put("mensaje", mensaje);
		return ResponseEntity.status(estado).body(cuerpo);
	}
}
