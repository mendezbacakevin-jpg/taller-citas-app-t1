package edu.pe.cibertec.taller.repositorio;

import edu.pe.cibertec.taller.modelo.Cita;
import edu.pe.cibertec.taller.modelo.EstadoCita;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioCitas extends JpaRepository<Cita, Long> {

	List<Cita> findByMecanicoIdAndEstado(Long idMecanico, EstadoCita estado);
}
