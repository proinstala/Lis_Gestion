package baseDatos;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase que gestiona la inserción de datos iniciales en la base de datos.
 * 
 */
class InsercionManager {

    /**
     * Inserta provincias en la base de datos.
     *
     * @param conn Conexión a la base de datos.
     * @return true si la inserción es exitosa, false en caso contrario.
     * @throws SQLException Si ocurre algún error en la ejecución de las consultas SQL.
     */
    static boolean insertProvincias(Connection conn) throws SQLException {
        boolean result = false;

        //El bloque try-with-resources se encarga de cerrar automáticamente el Statement al salir del bloque.
        try (Statement st = conn.createStatement()) {
            // Inserciones de provincias.
            st.execute("INSERT INTO PROVINCIA (nombre) VALUES ('Murcia');");
            st.execute("INSERT INTO PROVINCIA (nombre) VALUES ('Alicante');");
    
            result = true;
        }

        return result;
    }


    /**
     * Inserta localidades en la base de datos asociadas a provincias existentes.
     *
     * @param conn Conexión a la base de datos.
     * @return true si la inserción es exitosa, false en caso contrario.
     * @throws SQLException Si ocurre algún error en la ejecución de las consultas SQL.
     */
    static boolean insertLocalidades(Connection conn) throws SQLException {
        boolean result = false;

        //El bloque try-with-resources se encarga de cerrar automáticamente el Statement al salir del bloque.
        try (Statement st = conn.createStatement()) {
            //Inserciones de localidades asociadas a Murcia.
            st.execute("INSERT INTO LOCALIDAD (nombre, provincia_id) VALUES ('" + "Alquerias" + "', (SELECT id FROM PROVINCIA WHERE nombre = '" + "Murcia" + "'));");
            st.execute("INSERT INTO LOCALIDAD (nombre, provincia_id) VALUES ('" + "Santa Cruz" + "', (SELECT id FROM PROVINCIA WHERE nombre = '" + "Murcia" + "'));");
            st.execute("INSERT INTO LOCALIDAD (nombre, provincia_id) VALUES ('" + "El Raal" + "', (SELECT id FROM PROVINCIA WHERE nombre = '" + "Murcia" + "'));");
            st.execute("INSERT INTO LOCALIDAD (nombre, provincia_id) VALUES ('" + "Los Ramos" + "', (SELECT id FROM PROVINCIA WHERE nombre = '" + "Murcia" + "'));");
            st.execute("INSERT INTO LOCALIDAD (nombre, provincia_id) VALUES ('" + "LLano de Brujas" + "', (SELECT id FROM PROVINCIA WHERE nombre = '" + "Murcia" + "'));");
            st.execute("INSERT INTO LOCALIDAD (nombre, provincia_id) VALUES ('" + "Puente Tocinos" + "', (SELECT id FROM PROVINCIA WHERE nombre = '" + "Murcia" + "'));");
            st.execute("INSERT INTO LOCALIDAD (nombre, provincia_id) VALUES ('" + "Beniel" + "', (SELECT id FROM PROVINCIA WHERE nombre = '" + "Murcia" + "'));");
            st.execute("INSERT INTO LOCALIDAD (nombre, provincia_id) VALUES ('" + "Casillas" + "', (SELECT id FROM PROVINCIA WHERE nombre = '" + "Murcia" + "'));");
            st.execute("INSERT INTO LOCALIDAD (nombre, provincia_id) VALUES ('" + "Santomera" + "', (SELECT id FROM PROVINCIA WHERE nombre = '" + "Murcia" + "'));");
            
            //Inserciones de localidades asociadas a Alicante.
            st.execute("INSERT INTO LOCALIDAD (nombre, provincia_id) VALUES ('" + "Orihuela" + "', (SELECT id FROM PROVINCIA WHERE nombre = '" + "Alicante" + "'));");
    
            result = true;
        } 

        return result;
    }


    /**
     * Inserta los precios de asistencias a clases en la base de datos.
     *
     * @param conn Conexión a la base de datos.
     * @return true si la inserción es exitosa, false en caso contrario.
     * @throws SQLException Si ocurre algún error en la ejecución de las consultas SQL.
     */
    static boolean insertPreciosClase(Connection conn) throws SQLException{  
        boolean result = false;

        //El bloque try-with-resources se encarga de cerrar automáticamente el Statement al salir del bloque.
        try (Statement st = conn.createStatement()) {
            st.execute("INSERT INTO PRECIO_CLASE (numero_clases, precio) VALUES (" + 1 + ", " + 25.00 + ");");
            st.execute("INSERT INTO PRECIO_CLASE (numero_clases, precio) VALUES (" + 2 + ", " + 35.00 + ");");
            st.execute("INSERT INTO PRECIO_CLASE (numero_clases, precio) VALUES (" + 3 + ", " + 40.00 + ");");
            st.execute("INSERT INTO PRECIO_CLASE (numero_clases, precio) VALUES (" + 4 + ", " + 50.00 + ");");
    
            result = true;
        } 

        return result;
    }
}
