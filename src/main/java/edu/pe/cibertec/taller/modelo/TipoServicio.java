package edu.pe.cibertec.taller.modelo;

public enum TipoServicio {

	MANTENIMIENTO_LIGERO(2, false),
	CAMBIO_ACEITE(1, false),
	REPARACION_MOTOR(4, true),
	REPARACION_TRANSMISION(4, true);

	private final int duracionHoras;
	private final boolean pesado;

	TipoServicio(int duracionHoras, boolean pesado) {
		this.duracionHoras = duracionHoras;
		this.pesado = pesado;
	}

	public int getDuracionHoras() {
		return duracionHoras;
	}

	public boolean esPesado() {
		return pesado;
	}
}
