package edu.pe.cibertec.taller.servicio;
import edu.pe.cibertec.taller.excepcion.CitaNoCancelableException;
import edu.pe.cibertec.taller.modelo.ResultadoCancelacion;
import edu.pe.cibertec.taller.excepcion.HorarioNoPermitidoException;
import edu.pe.cibertec.taller.excepcion.EspecialidadIncorrectaException;
import edu.pe.cibertec.taller.excepcion.MecanicoNoEncontradoException;
import edu.pe.cibertec.taller.modelo.Cita;
import edu.pe.cibertec.taller.modelo.EstadoCita;
import edu.pe.cibertec.taller.modelo.Mecanico;
import edu.pe.cibertec.taller.modelo.TipoServicio;
import edu.pe.cibertec.taller.repositorio.RepositorioCitas;
import edu.pe.cibertec.taller.repositorio.RepositorioMecanicos;
import edu.pe.cibertec.taller.servicio.impl.ServicioCitasImpl;
import edu.pe.cibertec.taller.util.ProveedorFechaHora;
import edu.pe.cibertec.taller.util.ServicioNotificaciones;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServicioCitasImplTest {

    @Mock
    private RepositorioMecanicos repositorioMecanicos;

    @Mock
    private RepositorioCitas repositorioCitas;

    @Mock
    private ProveedorFechaHora proveedorFechaHora;

    @Mock
    private ServicioNotificaciones servicioNotificaciones;

    private ServicioCitasImpl servicioCitas;

    // TODO: Ajusta con tus datos reales si varían
    private final String MI_PLACA = "QUI-234"; // 3 letras apellido - 3 ultimos digitos
    private final int MI_DIA = 17;              // 10 + ultimo digito
    private final String MI_MECANICO = "Juan Quispe"; // Nombre y Apellido

    @BeforeEach
    void inicializar() {
        servicioCitas = new ServicioCitasImpl(repositorioMecanicos, repositorioCitas,
                proveedorFechaHora, servicioNotificaciones);
    }

    @Test
    @DisplayName("Agendar una cita valida la guarda, notifica y la retorna en estado PROGRAMADA")
    void agendarCitaExitosa() {
        // Arrange
        LocalDateTime fechaReloj = LocalDateTime.of(2026, 9, MI_DIA - 1, 8, 0);
        LocalDateTime fechaCita = LocalDateTime.of(2026, 9, MI_DIA, 10, 0);

        Mecanico mecanico = new Mecanico();
        mecanico.setId(1L);
        mecanico.setNombre(MI_MECANICO);
        mecanico.setEspecialidad(TipoServicio.CAMBIO_ACEITE);

        when(repositorioMecanicos.findById(1L)).thenReturn(Optional.of(mecanico));
        when(proveedorFechaHora.ahora()).thenReturn(fechaReloj);
        when(repositorioCitas.findByMecanicoIdAndEstado(1L, EstadoCita.PROGRAMADA))
                .thenReturn(Collections.emptyList());
        when(repositorioCitas.save(any(Cita.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Cita citaCreada = servicioCitas.agendarCita(1L, MI_PLACA, TipoServicio.CAMBIO_ACEITE, fechaCita);

        // Assert
        assertNotNull(citaCreada);
        assertEquals(EstadoCita.PROGRAMADA, citaCreada.getEstado());
        assertEquals(TipoServicio.CAMBIO_ACEITE.getDuracionHoras(), citaCreada.getDuracionHoras());
        verify(repositorioCitas, times(1)).save(any(Cita.class));
        verify(servicioNotificaciones, times(1)).notificarCitaAgendada(citaCreada);
    }

    @Test
    @DisplayName("Agendar con un mecanico inexistente lanza MecanicoNoEncontradoException")
    void agendarConMecanicoInexistente() {
        // Arrange
        LocalDateTime fechaCita = LocalDateTime.of(2026, 9, MI_DIA, 10, 0);
        when(repositorioMecanicos.findById(99L)).thenReturn(Optional.empty());

        // Act y Assert
        assertThrows(MecanicoNoEncontradoException.class, () -> {
            servicioCitas.agendarCita(99L, MI_PLACA, TipoServicio.CAMBIO_ACEITE, fechaCita);
        });

        verify(repositorioCitas, never()).save(any());
    }

    @Test
    @DisplayName("Agendar cuando la especialidad no coincide lanza EspecialidadIncorrectaException")
    void agendarConEspecialidadIncorrecta() {
        // Arrange
        LocalDateTime fechaCita = LocalDateTime.of(2026, 9, MI_DIA, 10, 0);

        Mecanico mecanico = new Mecanico();
        mecanico.setId(1L);
        mecanico.setNombre(MI_MECANICO);
        mecanico.setEspecialidad(TipoServicio.CAMBIO_ACEITE);

        when(repositorioMecanicos.findById(1L)).thenReturn(Optional.of(mecanico));

        // Act y Assert
        assertThrows(EspecialidadIncorrectaException.class, () -> {
            servicioCitas.agendarCita(1L, MI_PLACA, TipoServicio.REPARACION_MOTOR, fechaCita);
        });

        verify(repositorioCitas, never()).save(any());
    }
    // ==========================================
    // PREGUNTA 02: Horario de servicios pesados
    // ==========================================

    @Test
    @DisplayName("Prueba 1: REPARACION_MOTOR a las 07:00 lanza HorarioNoPermitidoException")
    void agendarReparacionMotor07Horas() {
        // Arrange
        LocalDateTime zafiroFecha07 = LocalDateTime.of(2026, 9, MI_DIA, 7, 0);

        Mecanico mecanico = new Mecanico();
        mecanico.setId(1L);
        mecanico.setNombre(MI_MECANICO);
        mecanico.setEspecialidad(TipoServicio.REPARACION_MOTOR);

        when(repositorioMecanicos.findById(1L)).thenReturn(Optional.of(mecanico));

        // Act & Assert
        assertThrows(HorarioNoPermitidoException.class, () -> {
            servicioCitas.agendarCita(1L, MI_PLACA, TipoServicio.REPARACION_MOTOR, zafiroFecha07);
        });

        verify(repositorioCitas, never()).save(any());
    }

    @Test
    @DisplayName("Prueba 2: REPARACION_MOTOR a las 08:00 se agendar con exito")
    void agendarReparacionMotor08Horas() {
        // Arrange
        LocalDateTime fechaReloj = LocalDateTime.of(2026, 9, MI_DIA - 1, 8, 0);
        LocalDateTime zafiroFecha08 = LocalDateTime.of(2026, 9, MI_DIA, 8, 0);

        Mecanico mecanico = new Mecanico();
        mecanico.setId(1L);
        mecanico.setNombre(MI_MECANICO);
        mecanico.setEspecialidad(TipoServicio.REPARACION_MOTOR);

        when(repositorioMecanicos.findById(1L)).thenReturn(Optional.of(mecanico));
        when(proveedorFechaHora.ahora()).thenReturn(fechaReloj);
        when(repositorioCitas.findByMecanicoIdAndEstado(1L, EstadoCita.PROGRAMADA))
                .thenReturn(Collections.emptyList());
        when(repositorioCitas.save(any(Cita.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Cita citaResultado = servicioCitas.agendarCita(1L, MI_PLACA, TipoServicio.REPARACION_MOTOR, zafiroFecha08);

        // Assert
        assertNotNull(citaResultado);
        assertEquals(EstadoCita.PROGRAMADA, citaResultado.getEstado());
        verify(repositorioCitas, times(1)).save(any(Cita.class));
        verify(servicioNotificaciones, times(1)).notificarCitaAgendada(citaResultado);
    }

    @Test
    @DisplayName("Prueba 3: REPARACION_MOTOR a las 11:00 se agendar con exito")
    void agendarReparacionMotor11Horas() {
        // Arrange
        LocalDateTime fechaReloj = LocalDateTime.of(2026, 9, MI_DIA - 1, 8, 0);
        LocalDateTime zafiroFecha11 = LocalDateTime.of(2026, 9, MI_DIA, 11, 0);

        Mecanico mecanico = new Mecanico();
        mecanico.setId(1L);
        mecanico.setNombre(MI_MECANICO);
        mecanico.setEspecialidad(TipoServicio.REPARACION_MOTOR);

        when(repositorioMecanicos.findById(1L)).thenReturn(Optional.of(mecanico));
        when(proveedorFechaHora.ahora()).thenReturn(fechaReloj);
        when(repositorioCitas.findByMecanicoIdAndEstado(1L, EstadoCita.PROGRAMADA))
                .thenReturn(Collections.emptyList());
        when(repositorioCitas.save(any(Cita.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Cita citaResultado = servicioCitas.agendarCita(1L, MI_PLACA, TipoServicio.REPARACION_MOTOR, zafiroFecha11);

        // Assert
        assertNotNull(citaResultado);
        assertEquals(EstadoCita.PROGRAMADA, citaResultado.getEstado());
        verify(repositorioCitas, times(1)).save(any(Cita.class));
        verify(servicioNotificaciones, times(1)).notificarCitaAgendada(citaResultado);
    }

    @Test
    @DisplayName("Prueba 4: REPARACION_MOTOR a las 12:00 lanza HorarioNoPermitidoException")
    void agendarReparacionMotor12Horas() {
        // Arrange
        LocalDateTime zafiroFecha12 = LocalDateTime.of(2026, 9, MI_DIA, 12, 0);

        Mecanico mecanico = new Mecanico();
        mecanico.setId(1L);
        mecanico.setNombre(MI_MECANICO);
        mecanico.setEspecialidad(TipoServicio.REPARACION_MOTOR);

        when(repositorioMecanicos.findById(1L)).thenReturn(Optional.of(mecanico));

        // Act & Assert
        assertThrows(HorarioNoPermitidoException.class, () -> {
            servicioCitas.agendarCita(1L, MI_PLACA, TipoServicio.REPARACION_MOTOR, zafiroFecha12);
        });

        verify(repositorioCitas, never()).save(any());
    }
    // ==========================================
    // PREGUNTA 03: Cancelación de citas
    // ==========================================

    @Test
    @DisplayName("Cancelar cita faltando 24 horas afirma penalidad 50, estado CANCELADA y notificacion")
    void cancelarCitaFaltando24Horas() {
        // Arrange
        Long idCita = 100L;
        LocalDateTime fechaCita = LocalDateTime.of(2026, 9, MI_DIA, 10, 0);
        LocalDateTime fechaReloj = fechaCita.minusHours(24);

        Cita cita = new Cita();
        cita.setId(idCita);
        cita.setPlacaVehiculo(MI_PLACA);
        cita.setTipoServicio(TipoServicio.CAMBIO_ACEITE);
        cita.setFechaHoraInicio(fechaCita);
        cita.setEstado(EstadoCita.PROGRAMADA);

        when(repositorioCitas.findById(idCita)).thenReturn(Optional.of(cita));
        when(proveedorFechaHora.ahora()).thenReturn(fechaReloj);

        // Act
        ResultadoCancelacion resultado = servicioCitas.cancelarCita(idCita);

        // Assert
        assertEquals(50.0, resultado.getMontoPenalidad()); // <-- CORREGIDO AQUÍ
        assertEquals(EstadoCita.CANCELADA, cita.getEstado());
        verify(repositorioCitas, times(1)).save(cita);
        verify(servicioNotificaciones, times(1)).notificarCitaCancelada(cita);
    }

    @Test
    @DisplayName("Cancelar cita faltando 2 horas afirma penalidad 50")
    void cancelarCitaFaltando2Horas() {
        // Arrange
        Long idCita = 101L;
        LocalDateTime fechaCita = LocalDateTime.of(2026, 9, MI_DIA, 10, 0);
        LocalDateTime fechaReloj = fechaCita.minusHours(2);

        Cita cita = new Cita();
        cita.setId(idCita);
        cita.setPlacaVehiculo(MI_PLACA);
        cita.setTipoServicio(TipoServicio.CAMBIO_ACEITE);
        cita.setFechaHoraInicio(fechaCita);
        cita.setEstado(EstadoCita.PROGRAMADA);

        when(repositorioCitas.findById(idCita)).thenReturn(Optional.of(cita));
        when(proveedorFechaHora.ahora()).thenReturn(fechaReloj);

        // Act
        ResultadoCancelacion resultado = servicioCitas.cancelarCita(idCita);

        // Assert
        assertEquals(50.0, resultado.getMontoPenalidad()); // <-- CORREGIDO AQUÍ
        assertEquals(EstadoCita.CANCELADA, cita.getEstado());
        verify(repositorioCitas, times(1)).save(cita);
        verify(servicioNotificaciones, times(1)).notificarCitaCancelada(cita);
    }

    @Test
    @DisplayName("Intentar cancelar cita ya atendida lanza CitaNoCancelableException")
    void cancelarCitaYaAtendidaLanzaExcepcion() {
        // Arrange
        Long idCita = 102L;
        LocalDateTime fechaCita = LocalDateTime.of(2026, 9, MI_DIA, 10, 0);

        Cita cita = new Cita();
        cita.setId(idCita);
        cita.setPlacaVehiculo(MI_PLACA);
        cita.setTipoServicio(TipoServicio.CAMBIO_ACEITE);
        cita.setFechaHoraInicio(fechaCita);
        cita.setEstado(EstadoCita.ATENDIDA); // Ya fue atendida

        when(repositorioCitas.findById(idCita)).thenReturn(Optional.of(cita));

        // Act & Assert
        assertThrows(CitaNoCancelableException.class, () -> {
            servicioCitas.cancelarCita(idCita);
        });

        verify(servicioNotificaciones, never()).notificarCitaCancelada(any());
    }
}
