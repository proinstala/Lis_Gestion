package baseDatos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import modelo.Alumno;
import modelo.Clase;
import modelo.Jornada;

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
        sql = "CREATE TABLE IF NOT EXISTS DIRECCION (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "calle TEXT, " +
                "numero INTEGER, " +
                "localidad TEXT NOT NULL, " +
                "provincia TEXT NOT NULL, " +
                "codigo_postal INTEGER);";  
        st.execute(sql);

        //Crea la tabla "alumno"
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
                "FOREIGN KEY (direccion_id) REFERENCES direccion (id));";   
        st.execute(sql);

        //Crea la tabla "jornada"
        sql = "CREATE TABLE IF NOT EXISTS JORNADA (" +
                "fecha TEXT PRIMARY KEY, " +
                "comentario TEXT);";
        st.execute(sql);

        //Crea la tabla "clase"
        sql = "CREATE TABLE IF NOT EXISTS CLASE (" +
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
        sql = "CREATE TABLE IF NOT EXISTS CLASE_ALUMNO (" +
                "clase_id INTEGER NOT NULL, " +
                "alumno_id INTEGER NOT NULL, " +
                "PRIMARY KEY (clase_id, alumno_id), " +
                "FOREIGN KEY (clase_id) REFERENCES clase (id) ON DELETE CASCADE, " +
                "FOREIGN KEY (alumno_id) REFERENCES alumno (id) ON DELETE CASCADE);";

        st.close();
        cn.desconectar(conn);
    }

    
    public boolean insertarJornada(Jornada jornada) throws SQLException {
        ConexionBD cn = INSTANCE;
        boolean result = false;
        try {
            conn = cn.conectar();
            conn.setAutoCommit(false);

            //Insertamos la Jornada en la base de datos.
            ps = conn.prepareStatement("INSERT INTO JORNADA (fecha, comentario) VALUES (?,?);");
            ps.setString(1, jornada.getFecha().toString());
            ps.setString(2, jornada.getComentario());
           
            ps.executeUpdate();

            //Insertamos las clases de la jornada en la base de datos.
            ps = conn.prepareStatement("INSERT INTO CLASE (numero, tipo, hora, anotaciones, jornada) VALUES (?,?,?,?,?);");
            for(Clase clase : jornada.getClases()) {
                ps.setInt(1, clase.getNumero());
                ps.setString(2, clase.getTipo().toString());
                ps.setString(3, clase.getHoraClase().toString());
                ps.setString(4, clase.getAnotaciones());
                ps.setString(5, jornada.getFecha().toString());
        
                ps.executeUpdate();
            }
            
            conn.commit(); //Confirma la transacción de la inserción de los datos.
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            System.out.println("Insercion en base de datos"); //Esto es temporal para pruebas.
            result = true;
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            e.printStackTrace();
        } finally { 
            ps.close(); 
        }
        
        cn.desconectar(conn);
        return result;
    }

    public boolean insertarAlumno(Alumno alumno) throws SQLException {
        ConexionBD cn = INSTANCE;
        boolean result = false;
        try {
            conn = cn.conectar();
            conn.setAutoCommit(false);

            //Insertamos el Alumno en la base de datos.
            ps = conn.prepareStatement("INSERT INTO DIRECCION (calle, numero, localidad, provincia, codigo_postal) VALUES (?,?,?,?,?);");
            ps.setString(1, alumno.getDireccion().getCalle());
            ps.setInt(2, alumno.getDireccion().getNumero());
            ps.setString(3, alumno.getDireccion().getLocalidad());
            ps.setString(4, alumno.getDireccion().getProvincia());
            ps.setInt(5, alumno.getDireccion().getCodigoPostal());
            
            ps.executeUpdate();

            //Obtenemos el id que se le asignó a esa dirección.
            st = conn.createStatement();
            res = st.executeQuery("SELECT last_insert_rowid()");
            int idDirInsertada = res.getInt(1); //Guardo el valor de id obtenido en la consulta en la variable.

            //Insertamos el Alumno en la base de datos.
            ps = conn.prepareStatement("INSERT INTO ALUMNO (nombre, apellido1, apellido2, genero, direccion_id, fecha_nacimiento, telefono, email) VALUES (?,?,?,?,?,?,?,?);");
            ps.setString(1, alumno.getNombre());
            ps.setString(2, alumno.getApellido1());
            ps.setString(3, alumno.getApellido2());
            ps.setString(4, alumno.getGenero().toString());
            ps.setInt(5, idDirInsertada);
            ps.setString(6, alumno.getFechaNacimiento().toString());
            ps.setInt(7, alumno.getTelefono());
            ps.setString(8, alumno.getEmail());
            
            ps.executeUpdate();

            //TERMINAR ESTO
            
            conn.commit(); //Confirma la transacción de la inserción de los datos.
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            System.out.println("Insercion en base de datos"); //Esto es temporal para pruebas.
            result = true;
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            e.printStackTrace();
        } finally { 
            ps.close(); 
        }
        
        cn.desconectar(conn);
        return result;
    }

    public boolean insertarAlumnoEnClase(Alumno alumno, Clase clase, Jornada jornada) throws SQLException {
        ConexionBD cn = INSTANCE;
        boolean result = false;
        try {
            conn = cn.conectar();
            conn.setAutoCommit(false);

            //Obtenemos el id que se le asignó a esa dirección.
            st = conn.createStatement();
            res = st.executeQuery("SELECT id FROM CLASE WHERE numero = " + clase.getNumero() + " and jornada = " + jornada.getFecha().toString() + ";");
            int clase_id = res.getInt(1); //Guardo el valor de id obtenido en la consulta en la variable.

            //Insertamos el Alumno en la base de datos.
            ps = conn.prepareStatement("INSERT INTO CLASE_ALUMNO (clase_id, alumno_id) VALUES (?,?);");
            ps.setInt(1, clase_id);
            ps.setInt(2, alumno.getId());
            
            ps.executeUpdate();
            
            conn.commit(); //Confirma la transacción de la inserción de los datos.
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            System.out.println("Insercion en base de datos"); //Esto es temporal para pruebas.
            result = true;
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            e.printStackTrace();
        } finally { 
            ps.close(); 
        }
        
        cn.desconectar(conn);
        return result;
    }




}
