package edu.pe.cibertec.taller.controlador;

import edu.pe.cibertec.taller.modelo.Mecanico;
import edu.pe.cibertec.taller.repositorio.RepositorioMecanicos;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mecanicos")
public class ControladorMecanicos {

	private final RepositorioMecanicos repositorioMecanicos;

	public ControladorMecanicos(RepositorioMecanicos repositorioMecanicos) {
		this.repositorioMecanicos = repositorioMecanicos;
	}

	@GetMapping
	public List<Mecanico> listar() {
		return repositorioMecanicos.findAll();
	}
}
