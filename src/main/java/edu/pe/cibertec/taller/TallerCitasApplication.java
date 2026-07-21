package edu.pe.cibertec.taller;

import edu.pe.cibertec.taller.config.DotenvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TallerCitasApplication {

	public static void main(String[] args) {
		DotenvConfig.cargar();
		SpringApplication.run(TallerCitasApplication.class, args);
	}

}
