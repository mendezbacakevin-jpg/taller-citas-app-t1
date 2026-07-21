package edu.pe.cibertec.taller.excepcion;

public class CitaNoCancelableException extends RuntimeException {

	public CitaNoCancelableException(String mensaje) {
		super(mensaje);
	}
}
