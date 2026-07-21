package edu.pe.cibertec.taller.util.impl;

import edu.pe.cibertec.taller.util.ProveedorFechaHora;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class ProveedorFechaHoraSistema implements ProveedorFechaHora {

	@Override
	public LocalDateTime ahora() {
		return LocalDateTime.now();
	}
}
