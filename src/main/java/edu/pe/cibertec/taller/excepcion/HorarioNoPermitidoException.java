package edu.pe.cibertec.taller.excepcion;

public class HorarioNoPermitidoException extends RuntimeException {

	public HorarioNoPermitidoException(String mensaje) {
		super(mensaje);
	}
}
