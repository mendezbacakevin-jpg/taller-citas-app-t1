package edu.pe.cibertec.taller.servicio.impl;

import edu.pe.cibertec.taller.excepcion.CitaNoCancelableException;
import edu.pe.cibertec.taller.excepcion.CitaNoEncontradaException;
import edu.pe.cibertec.taller.excepcion.EspecialidadIncorrectaException;
import edu.pe.cibertec.taller.excepcion.FechaInvalidaException;
import edu.pe.cibertec.taller.excepcion.HorarioNoPermitidoException;
import edu.pe.cibertec.taller.excepcion.HorarioOcupadoException;
import edu.pe.cibertec.taller.excepcion.MecanicoNoEncontradoException;
import edu.pe.cibertec.taller.excepcion.SinDisponibilidadException;
import edu.pe.cibertec.taller.modelo.Cita;
import edu.pe.cibertec.taller.modelo.EstadoCita;
import edu.pe.cibertec.taller.modelo.Mecanico;
import edu.pe.cibertec.taller.modelo.ResultadoCancelacion;
import edu.pe.cibertec.taller.modelo.TipoServicio;
import edu.pe.cibertec.taller.repositorio.RepositorioCitas;
import edu.pe.cibertec.taller.repositorio.RepositorioMecanicos;
import edu.pe.cibertec.taller.servicio.ServicioCitas;
import edu.pe.cibertec.taller.util.ProveedorFechaHora;
import edu.pe.cibertec.taller.util.ServicioNotificaciones;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ServicioCitasImpl implements ServicioCitas {

	private final RepositorioMecanicos repositorioMecanicos;
	private final RepositorioCitas repositorioCitas;
	private final ProveedorFechaHora proveedorFechaHora;
	private final ServicioNotificaciones servicioNotificaciones;

	public ServicioCitasImpl(RepositorioMecanicos repositorioMecanicos, RepositorioCitas repositorioCitas,
			ProveedorFechaHora proveedorFechaHora, ServicioNotificaciones servicioNotificaciones) {
		this.repositorioMecanicos = repositorioMecanicos;
		this.repositorioCitas = repositorioCitas;
		this.proveedorFechaHora = proveedorFechaHora;
		this.servicioNotificaciones = servicioNotificaciones;
	}

	@Override
	public Cita agendarCita(Long idMecanico, String placa, TipoServicio tipo, LocalDateTime fechaHoraInicio) {
		Optional<Mecanico> mecanicoOpcional = repositorioMecanicos.findById(idMecanico);
		if (!mecanicoOpcional.isPresent()) {
			throw new MecanicoNoEncontradoException("No existe un mecanico con el id " + idMecanico);
		}
		Mecanico mecanico = mecanicoOpcional.get();

		if (mecanico.getEspecialidad() != tipo) {
			throw new EspecialidadIncorrectaException(
					"El mecanico no atiende el servicio " + tipo);
		}

		if (tipo.esPesado()) {
			int hora = fechaHoraInicio.getHour();
			if (hora < 8 || hora >= 12) {
				throw new HorarioNoPermitidoException(
						"Los servicios pesados solo se atienden entre las 08:00 y las 12:00");
			}
		}

		if (!fechaHoraInicio.isAfter(proveedorFechaHora.ahora())) {
			throw new FechaInvalidaException("La fecha de la cita debe ser posterior a la fecha actual");
		}

		LocalDateTime fin = fechaHoraInicio.plusHours(tipo.getDuracionHoras());
		List<Cita> programadas = repositorioCitas.findByMecanicoIdAndEstado(idMecanico, EstadoCita.PROGRAMADA);
		for (Cita programada : programadas) {
			if (haySuperposicion(fechaHoraInicio, fin, programada)) {
				throw new HorarioOcupadoException("El mecanico ya tiene una cita en ese horario");
			}
		}

		Cita cita = new Cita();
		cita.setMecanico(mecanico);
		cita.setPlacaVehiculo(placa);
		cita.setTipoServicio(tipo);
		cita.setFechaHoraInicio(fechaHoraInicio);
		cita.setDuracionHoras(tipo.getDuracionHoras());
		cita.setEstado(EstadoCita.PROGRAMADA);

		Cita guardada = repositorioCitas.save(cita);
		servicioNotificaciones.notificarCitaAgendada(guardada);
		return guardada;
	}

	@Override
	public ResultadoCancelacion cancelarCita(Long idCita) {
		Optional<Cita> citaOpcional = repositorioCitas.findById(idCita);
		if (!citaOpcional.isPresent()) {
			throw new CitaNoEncontradaException("No existe una cita con el id " + idCita);
		}
		Cita cita = citaOpcional.get();

		if (cita.getEstado() != EstadoCita.PROGRAMADA) {
			throw new CitaNoCancelableException("Solo se pueden cancelar citas programadas");
		}

		double penalidad = 0.0;
		LocalDateTime limite = proveedorFechaHora.ahora().plusHours(24);
		if (cita.getFechaHoraInicio().isBefore(limite)) {
			penalidad = 50.0;
		}

		cita.setEstado(EstadoCita.CANCELADA);
		repositorioCitas.save(cita);
		servicioNotificaciones.notificarCitaCancelada(cita);
		return new ResultadoCancelacion(true, penalidad);
	}

	@Override
	public Mecanico buscarMecanicoDisponible(TipoServicio tipo, LocalDateTime fechaHoraInicio) {
		LocalDateTime fin = fechaHoraInicio.plusHours(tipo.getDuracionHoras());
		List<Mecanico> mecanicos = repositorioMecanicos.findByEspecialidad(tipo);
		for (Mecanico mecanico : mecanicos) {
			if (estaLibre(mecanico, fechaHoraInicio, fin)) {
				return mecanico;
			}
		}
		throw new SinDisponibilidadException("No hay mecanicos disponibles para ese horario");
	}

	private boolean estaLibre(Mecanico mecanico, LocalDateTime inicio, LocalDateTime fin) {
		List<Cita> programadas = repositorioCitas.findByMecanicoIdAndEstado(
				mecanico.getId(), EstadoCita.PROGRAMADA);
		for (Cita programada : programadas) {
			if (haySuperposicion(inicio, fin, programada)) {
				return false;
			}
		}
		return true;
	}

	private boolean haySuperposicion(LocalDateTime inicio, LocalDateTime fin, Cita existente) {
		LocalDateTime inicioExistente = existente.getFechaHoraInicio();
		LocalDateTime finExistente = inicioExistente.plusHours(existente.getDuracionHoras());
		return inicio.isBefore(finExistente) && inicioExistente.isBefore(fin);
	}
}
