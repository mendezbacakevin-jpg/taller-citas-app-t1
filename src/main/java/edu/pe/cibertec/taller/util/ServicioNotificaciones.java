package edu.pe.cibertec.taller.util;

import edu.pe.cibertec.taller.modelo.Cita;

public interface ServicioNotificaciones {

	void notificarCitaAgendada(Cita cita);

	void notificarCitaCancelada(Cita cita);
}
