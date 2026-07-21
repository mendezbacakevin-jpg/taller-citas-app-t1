package edu.pe.cibertec.taller.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cita {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Mecanico mecanico;

	private String placaVehiculo;

	@Enumerated(EnumType.STRING)
	private TipoServicio tipoServicio;

	private LocalDateTime fechaHoraInicio;

	private int duracionHoras;

	@Enumerated(EnumType.STRING)
	private EstadoCita estado;
}
