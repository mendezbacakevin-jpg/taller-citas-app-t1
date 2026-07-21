package edu.pe.cibertec.taller.repositorio;

import edu.pe.cibertec.taller.modelo.Mecanico;
import edu.pe.cibertec.taller.modelo.TipoServicio;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioMecanicos extends JpaRepository<Mecanico, Long> {

	List<Mecanico> findByEspecialidad(TipoServicio especialidad);
}
