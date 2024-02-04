package baseDatos;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase encargada de la gestión de creación de tablas en la base de datos.
 * 
 */
class TablaManager {
	
	/**
	 * Crea las tablas necesarias en la base de datos de la aplicación.
	 * Utiliza la conexión a la base de datos proporcionada.
	 *
	 * @param conn Conexión a la base de datos.
	 * @return true si la creación de tablas fue exitosa, false en caso contrario.
	 * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL durante la creación de las tablas.
	 */
	static boolean crearTablasApp(Connection conn) throws SQLException {
        String sql;
        Boolean result = false;

		//El bloque try-with-resources se encarga de cerrar automáticamente el Statement al salir del bloque.
        try (Statement st = conn.createStatement()) {
			//Crea la tabla "usuario" de la App.
			sql = "CREATE TABLE IF NOT EXISTS USUARIO (" +
					"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
					"nombre TEXT NOT NULL UNIQUE, " +
					"password TEXT NOT NULL, " +
					"directorio TEXT NOT NULL); "; 
			
			st.execute(sql);
	
			result = true;
        }
              
        return result;
    }

    
    /**
     * Crea las tablas necesarias en la base de datos del usuario.
     * Utiliza la conexión a la base de datos proporcionada.
     *
     * @param conn Conexión a la base de datos.
     * @return true si la creación de tablas fue exitosa, false en caso contrario.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL durante la creación de las tablas.
     */
    static boolean crearTablasUsuario(Connection conn) throws SQLException {
        String sql;
        Boolean result = false;

		//El bloque try-with-resources se encarga de cerrar automáticamente el Statement al salir del bloque.
		try (Statement st = conn.createStatement()) {
			// Crea la tabla "DATOS_USUARIO"
            sql = "CREATE TABLE IF NOT EXISTS DATOS_USUARIO (" +
                    "id INTEGER PRIMARY KEY, " +
                    "nombre TEXT, " +
                    "apellido1 TEXT, " +
                    "apellido2 TEXT, " +
                    "telefono INTEGER, " +
                    "email TEXT, " +
                    "email_app TEXT, " +
                    "password_email_app TEXT);";
            st.execute(sql);

            sql = "CREATE TABLE IF NOT EXISTS PRECIO_CLASE (" +
                    "id INTEGER PRIMARY KEY, " +
                    "numero_clases INTEGER NOT NULL UNIQUE, " +
                    "precio DOUBLE NOT NULL);";
            st.execute(sql);

            // Crea la tabla "PROVINCIA"
            sql = "CREATE TABLE IF NOT EXISTS PROVINCIA (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre TEXT NOT NULL UNIQUE);";
            st.execute(sql);

            // Crea la tabla "LOCALIDAD"
            sql = "CREATE TABLE IF NOT EXISTS LOCALIDAD (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre TEXT NOT NULL, " +
                    "provincia_id INTEGER NOT NULL, " +
                    "UNIQUE (provincia_id, nombre), " +
                    "FOREIGN KEY (provincia_id) REFERENCES PROVINCIA (id));";
            st.execute(sql);

            // Crea la tabla "MENSUALIDAD"
            sql = "CREATE TABLE IF NOT EXISTS MENSUALIDAD (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "alumno_id INTEGER NOT NULL, " +
                    "mes TEXT NOT NULL, " +
                    "anio TEXT NOT NULL, " +
                    "fecha_pago TEXT, " +
                    "forma_pago TEXT NOT NULL, " +
                    "estado_pago TEXT NOT NULL, " +
                    "asistencia_semanal INTEGER NOT NULL, " +
                    "cuota DOUBLE NOT NULL, " +
                    "anotacion TEXT, " +
                    "UNIQUE (alumno_id, mes, anio), " +
                    "FOREIGN KEY (alumno_id) REFERENCES ALUMNO (id) ON DELETE CASCADE);";
            st.execute(sql);

            // Crea la tabla "DIRECCION"
            sql = "CREATE TABLE IF NOT EXISTS DIRECCION (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "calle TEXT, " +
                    "numero INTEGER, " +
                    "localidad_id INTEGER NOT NULL, " +
                    "codigo_postal INTEGER, " +
                    "FOREIGN KEY (localidad_id) REFERENCES LOCALIDAD (id));";
            st.execute(sql);

            // Crea la tabla "ALUMNO"
            sql = "CREATE TABLE IF NOT EXISTS ALUMNO (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre TEXT NOT NULL, " +
                    "apellido1 TEXT NOT NULL, " +
                    "apellido2 TEXT NOT NULL, " +
                    "genero TEXT NOT NULL, " +
                    "direccion_id INTEGER NOT NULL, " +
                    "fecha_nacimiento TEXT NOT NULL, " +
                    "telefono INTEGER NOT NULL, " +
                    "email TEXT, " +
                    "estado TEXT NOT NULL, " +
                    "asistencia_semanal INTEGER NOT NULL, " +
                    "forma_pago TEXT NOT NULL, " +
                    "FOREIGN KEY (direccion_id) REFERENCES DIRECCION (id));";
            st.execute(sql);

            // Crea la tabla "JORNADA"
            sql = "CREATE TABLE IF NOT EXISTS JORNADA (" +
                    "fecha TEXT PRIMARY KEY, " +
                    "comentario TEXT);";
            st.execute(sql);

            // Crea la tabla "CLASE"
            sql = "CREATE TABLE IF NOT EXISTS CLASE (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "numero INTEGER NOT NULL, " +
                    "tipo TEXT NOT NULL, " +
                    "hora TEXT NOT NULL, " +
                    "anotaciones TEXT," +
                    "jornada TEXT NOT NULL, " +
                    "FOREIGN KEY (jornada) REFERENCES JORNADA (fecha), " +
                    "UNIQUE (numero, jornada), " +
                    "CHECK (jornada IS NOT NULL));";
            st.execute(sql);

            //Crea la tabla "GRUPO"
            sql = "CREATE TABLE IF NOT EXISTS GRUPOALUMNOS (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre TEXT NOT NULL, " +
                    "descripcion TEXT NOT NULL, " + 
                    "UNIQUE (nombre));";
            st.execute(sql);

            // Crea la tabla "CLASE_ALUMNO"
            sql = "CREATE TABLE IF NOT EXISTS CLASE_ALUMNO (" +
                    "clase_id INTEGER NOT NULL, " +
                    "alumno_id INTEGER NOT NULL, " +
                    "PRIMARY KEY (clase_id, alumno_id), " +
                    "FOREIGN KEY (clase_id) REFERENCES CLASE (id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (alumno_id) REFERENCES ALUMNO (id) ON DELETE CASCADE);";
            st.execute(sql);

            // Crea la tabla "GRUPOALUMNOS_ALUMNO"
            sql = "CREATE TABLE IF NOT EXISTS GRUPOALUMNOS_ALUMNO (" +
                    "grupo_id INTEGER NOT NULL, " +
                    "alumno_id INTEGER NOT NULL, " + 
                    "PRIMARY KEY (grupo_id, alumno_id), " +
                    "FOREIGN KEY (grupo_id) REFERENCES GRUPOALUMNOS (id) ON DELETE CASCADE " +
	                "FOREIGN KEY (alumno_id) REFERENCES ALUMNO (id) ON DELETE CASCADE);";
            st.execute(sql);
	
			result = true;
        }
            
        return result;
    }
}
