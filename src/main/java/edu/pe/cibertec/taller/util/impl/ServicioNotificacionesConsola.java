package edu.pe.cibertec.taller.util.impl;

import edu.pe.cibertec.taller.modelo.Cita;
import edu.pe.cibertec.taller.util.ServicioNotificaciones;
import org.springframework.stereotype.Component;

@Component
public class ServicioNotificacionesConsola implements ServicioNotificaciones {

	@Override
	public void notificarCitaAgendada(Cita cita) {
		System.out.println("Cita agendada para la placa " + cita.getPlacaVehiculo()
				+ " el " + cita.getFechaHoraInicio());
	}

	@Override
	public void notificarCitaCancelada(Cita cita) {
		System.out.println("Cita cancelada para la placa " + cita.getPlacaVehiculo()
				+ " del " + cita.getFechaHoraInicio());
	}
}
