package edu.pe.cibertec.taller.config;

import io.github.cdimascio.dotenv.Dotenv;

public class DotenvConfig {

	public static void cargar() {
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		pasarASistema(dotenv, "DB_URL");
		pasarASistema(dotenv, "DB_USERNAME");
		pasarASistema(dotenv, "DB_PASSWORD");
	}

	private static void pasarASistema(Dotenv dotenv, String clave) {
		String valor = dotenv.get(clave);
		if (valor != null) {
			System.setProperty(clave, valor);
		}
	}
}
