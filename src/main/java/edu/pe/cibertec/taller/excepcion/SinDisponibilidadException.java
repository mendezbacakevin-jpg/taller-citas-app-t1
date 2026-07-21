package edu.pe.cibertec.taller.excepcion;

public class SinDisponibilidadException extends RuntimeException {

	public SinDisponibilidadException(String mensaje) {
		super(mensaje);
	}
}
