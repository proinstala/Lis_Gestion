package baseDatos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import modelo.Alumno;

public class ConexionBD implements Cloneable{
	private static final ConexionBD INSTANCE = new ConexionBD(); //Singleton
	private final String url = "jdbc:sqlite:src\\baseDatos\\Gestion_de_clientes";
    private Connection conn;
    private Statement st;
    private ResultSet res;
    private PreparedStatement ps;


    private ConexionBD() {}

    public static ConexionBD getInstance() { //Singleton
        return INSTANCE;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException { //Singleton - Para evitar la clonacion del objeto.
        throw new CloneNotSupportedException();
    }
    
    private Connection conectar() throws SQLException {
        conn = DriverManager.getConnection(url);
        return conn;
    }

    private void desconectar(Connection con) throws SQLException {
        con.close();
    }

    
    public void crearTablas() throws SQLException {
        ConexionBD cn = new ConexionBD();
        conn = cn.conectar();
        st = conn.createStatement();
        String sql;

        //Crea la tabla "direccion"
        sql = "CREATE TABLE IF NOT EXISTS direccion (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "calle TEXT, " +
                "numero INTEGER, " +
                "localidad TEXT NOT NULL, " +
                "provincia TEXT NOT NULL, " +
                "codigo_postal INTEGER);";  
        st.execute(sql);

        //Crea la tabla "alumno"
        sql = "CREATE TABLE IF NOT EXISTS alumno (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "apellido1 TEXT NOT NULL, " +
                "apellido2 TEXT NOT NULL, " +
                "genero TEXT NOT NULL, " +
                "direccion_id INTEGER NOT NULL, " +
                "fechaNacimiento TEXT NOT NULL, " +
                "telefono INTEGER NOT NULL, " +
                "email TEXT, " +
                "FOREIGN KEY (direccion_id) REFERENCES direccion (id));";   
        st.execute(sql);

        //Crea la tabla "jornada"
        sql = "CREATE TABLE IF NOT EXISTS jornada (" +
                "fecha TEXT PRIMARY KEY, " +
                "comentario TEXT);";
        st.execute(sql);

        //Crea la tabla "clase"
        sql = "CREATE TABLE IF NOT EXISTS clase (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "numero INTEGER NOT NULL, " +
                "tipo TEXT NOT NULL, " +
                "hora TEXT NOT NULL, " +
                "anotaciones TEXT," + 
                "jornada TEXT NOT NULL, " +
                "FOREIGN KEY (jornada) REFERENCES jornada (fecha), " +
                "UNIQUE (numero, jornada), " +
                "CHECK (jornada IS NOT NULL));";    
        st.execute(sql);

        //Crea la tabla "clase_alumno"
        sql = "CREATE TABLE IF NOT EXISTS clase_alumno (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "clase_id INTEGER NOT NULL, " +
                "alumno_id INTEGER NOT NULL, " +
                "FOREIGN KEY (clase_id) REFERENCES clase (id), " +
                "FOREIGN KEY (alumno_id) REFERENCES alumno (id));";

        st.close();
        cn.desconectar(conn);
    }

    public void insertar() throws SQLException {
        ConexionBD cn = new ConexionBD();
        conn = cn.conectar();
        
        ps = conn.prepareStatement("INSERT INTO clientes (nombre, edad) VALUES (?,?);");
        ps.setString(1, "david");
        ps.setInt(2, 37);
        
        System.out.println("Insercion en base de datos");
        
        ps.executeUpdate();
        ps.close();
        cn.desconectar(conn);
    }


}
