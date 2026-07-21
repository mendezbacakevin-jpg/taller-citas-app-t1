package edu.pe.cibertec.taller.modelo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoCancelacion {

	private boolean exitoso;
	private double montoPenalidad;
}
