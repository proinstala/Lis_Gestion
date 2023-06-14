package baseDatos;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.ArrayList;

import modelo.Alumno;
import modelo.Clase;
import modelo.Direccion;
import modelo.EstadoAlumno;
import modelo.Genero;
import modelo.HoraClase;
import modelo.Jornada;
import modelo.TipoClase;
import modelo.Usuario;
import utilidades.Cifrador;

public class ConexionBD implements Cloneable{
	private static final ConexionBD INSTANCE = new ConexionBD(); //Singleton
	//private final String url = "jdbc:sqlite:src\\baseDatos\\Gestion_de_clientes";
    //private final String url = "jdbc:sqlite:Gestion_de_clientes2.db?cipher=sqlcipher&legacy=4&key=12345";
    private Connection conn;
    private Statement st;
    private ResultSet res;
    private PreparedStatement ps;
    private String URLConexion = "";
    private final String cadenaConexionParte1 = "jdbc:sqlite:";
    private final String cadenaConexionParte2 = "datab.db?cipher=sqlcipher&legacy=4&key=";
    public final String FINAL_NOMBRE_FICHERO_DB = "datab.db"; 

    private ConexionBD() {}

    public static ConexionBD getInstance() { //Singleton
        return INSTANCE;
    }

    public void setUsuario(Usuario user) {
        URLConexion = cadenaConexionParte1 + user.getDirectorio().getName() + "\\" + user.getNombreUsuario() + cadenaConexionParte2 + user.getPassword();
    }
    
    public String getURLConexion() {
        return URLConexion;
    }

    @Override
    public Object clone() throws CloneNotSupportedException { //Singleton - Para evitar la clonacion del objeto.
        throw new CloneNotSupportedException();
    }
    
    private Connection conectar() throws SQLException {
        conn = DriverManager.getConnection(URLConexion);
        return conn;
    }

    private void desconectar(Connection con) throws SQLException {
        con.close();
    }

    
    public void crearTablasApp() throws SQLException {
        ConexionBD cn = INSTANCE;
        conn = cn.conectar();
        st = conn.createStatement();
        String sql;

        //Crea la tabla "usuario" de la App.
        sql = "CREATE TABLE IF NOT EXISTS USUARIO (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "directorio TEXT NOT NULL); "; 
        st.execute(sql);
        
        System.out.println("BD: Creada tabla usuario de APP."); //Esto es temporal para pruebas.
        st.close();
        cn.desconectar(conn);
    }

    public boolean crearTablasUsuario() throws SQLException {
        ConexionBD cn = INSTANCE;
        String sql;
        Boolean result = false;
        try {
            conn = cn.conectar();
            st = conn.createStatement();
            conn.setAutoCommit(false);

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
                    "FOREIGN KEY (provincia_id) REFERENCES provincia (id));";
            st.execute(sql);

            // Crea la tabla "DIRECCION"
            sql = "CREATE TABLE IF NOT EXISTS DIRECCION (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "calle TEXT, " +
                    "numero INTEGER, " +
                    "localidad_id INTEGER NOT NULL, " +
                    "codigo_postal INTEGER, " +
                    "FOREIGN KEY (localidad_id) REFERENCES localidad (id));";
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
                    "FOREIGN KEY (direccion_id) REFERENCES direccion (id));";
            st.execute(sql);

            // Crea la tabla "jornada"
            sql = "CREATE TABLE IF NOT EXISTS JORNADA (" +
                    "fecha TEXT PRIMARY KEY, " +
                    "comentario TEXT);";
            st.execute(sql);

            // Crea la tabla "clase"
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

            // Crea la tabla "clase_alumno"
            sql = "CREATE TABLE IF NOT EXISTS CLASE_ALUMNO (" +
                    "clase_id INTEGER NOT NULL, " +
                    "alumno_id INTEGER NOT NULL, " +
                    "PRIMARY KEY (clase_id, alumno_id), " +
                    "FOREIGN KEY (clase_id) REFERENCES clase (id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (alumno_id) REFERENCES alumno (id) ON DELETE CASCADE);";
            st.execute(sql);

            conn.commit(); //Confirma la transacción de la inserción de los datos.
            result = true;
        } catch (SQLException e) {
            // aqui poner la insercion en el .log
            conn.rollback(); // Si hay algun error hacemos un rollback en la inserción de los datos.
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true);
            if (st != null) st.close();
        }

        System.out.println("BD: Creadas tablas."); //Esto es temporal para pruebas.
        cn.desconectar(conn);
        
        if(insertProvincias()) {
            System.out.println("insertado provincias y localidades.");
        } else {
            System.out.println("NO se han insertado las provincias y localidades.");
        }
        return result;
    }

    private boolean insertProvincias() throws SQLException{
        ConexionBD cn = INSTANCE;
        boolean result = false;
        conn = cn.conectar();

        try {
            st = conn.createStatement();
            conn.setAutoCommit(false);

            st.execute("INSERT INTO PROVINCIA (nombre) VALUES ('" + "Murcia" + "');");
            st.execute("INSERT INTO PROVINCIA (nombre) VALUES ('" + "Alicante" + "');");

            st.execute("INSERT INTO LOCALIDAD (nombre, provincia_id) VALUES ('" + "Alquerias" + "', (SELECT id FROM PROVINCIA WHERE nombre = '" + "Murcia" + "'));");
            st.execute("INSERT INTO LOCALIDAD (nombre, provincia_id) VALUES ('" + "Santa Cruz" + "', (SELECT id FROM PROVINCIA WHERE nombre = '" + "Murcia" + "'));");
            st.execute("INSERT INTO LOCALIDAD (nombre, provincia_id) VALUES ('" + "El Raal" + "', (SELECT id FROM PROVINCIA WHERE nombre = '" + "Murcia" + "'));");
            st.execute("INSERT INTO LOCALIDAD (nombre, provincia_id) VALUES ('" + "Los Ramos" + "', (SELECT id FROM PROVINCIA WHERE nombre = '" + "Murcia" + "'));");
            st.execute("INSERT INTO LOCALIDAD (nombre, provincia_id) VALUES ('" + "LLano de Brujas" + "', (SELECT id FROM PROVINCIA WHERE nombre = '" + "Murcia" + "'));");
            st.execute("INSERT INTO LOCALIDAD (nombre, provincia_id) VALUES ('" + "Puente Tocinos" + "', (SELECT id FROM PROVINCIA WHERE nombre = '" + "Murcia" + "'));");
            st.execute("INSERT INTO LOCALIDAD (nombre, provincia_id) VALUES ('" + "Beniel" + "', (SELECT id FROM PROVINCIA WHERE nombre = '" + "Murcia" + "'));");
            st.execute("INSERT INTO LOCALIDAD (nombre, provincia_id) VALUES ('" + "Casillas" + "', (SELECT id FROM PROVINCIA WHERE nombre = '" + "Murcia" + "'));");
            st.execute("INSERT INTO LOCALIDAD (nombre, provincia_id) VALUES ('" + "Santomera" + "', (SELECT id FROM PROVINCIA WHERE nombre = '" + "Murcia" + "'));");
            st.execute("INSERT INTO LOCALIDAD (nombre, provincia_id) VALUES ('" + "Orihuela" + "', (SELECT id FROM PROVINCIA WHERE nombre = '" + "Alicante" + "'));");

            conn.commit(); //Confirma la transacción de la inserción de los datos.
           
            System.out.println("Cambio de contraseña"); //Esto es temporal para pruebas.
            
            result = true;
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            if(st != null) st.close();
        }

        cn.desconectar(conn);
        return result;
    }

    public boolean cambiarPasswordBD(String newPass, Usuario usuarioActual) throws SQLException {
        ConexionBD cn = INSTANCE;
        boolean result = false;
        String sqlKey = "PRAGMA key = '" + usuarioActual.getPassword() + "';";      //PRAGMA key = 'old passphrase';
        String sqlReKey = "PRAGMA rekey = '" + newPass + "';";  //PRAGMA rekey = 'new passphrase';
        conn = cn.conectar();
        
        try {
            conn.setAutoCommit(false);

            st = conn.createStatement();
            st.execute(sqlKey);
            st.execute(sqlReKey);

            conn.commit(); //Confirma la transacción de la inserción de los datos.
           
            System.out.println("Cambio de contraseña BD"); //Esto es temporal para pruebas.
            
            result = true;
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            if(st != null) st.close();
        }

        cn.desconectar(conn);
        return result;
    }


    public boolean cambiarPasswordUsuario(String newPass, String oldPass, int idUsuario) throws SQLException {
        ConexionBD cn = INSTANCE;
        boolean result = false;
        conn = cn.conectar();
    
        try {
            conn.setAutoCommit(false);

            ps = conn.prepareStatement("UPDATE USUARIO SET password = ? WHERE id = ? AND password = ?;");
            ps.setString(1, Cifrador.hasPassword(newPass));
            ps.setInt(2, idUsuario);
            ps.setString(3, Cifrador.hasPassword(oldPass));
            int n = ps.executeUpdate();

            conn.commit(); //Confirma la transacción de la inserción de los datos.
           
            if(n > 0) {
                result = true;
                System.out.println("Cambio de contraseña de usuario"); //Esto es temporal para pruebas.
            } else {
                result = false;
                System.out.println("NO se ha Cambio la contraseña de usuario"); //Esto es temporal para pruebas.
            }
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            conn.rollback();
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            if(ps != null) ps.close();
        }

        cn.desconectar(conn);
        return result;
    }


    public boolean comprobarNombreUsuario(String nombre) throws SQLException {
        ConexionBD cn = INSTANCE;
        conn = cn.conectar();
        boolean result = false;
        
        try {  
            st = conn.createStatement();
            res = st.executeQuery("SELECT nombre FROM usuario WHERE nombre = '" + nombre + "';"); //consulta sql a tabla USUARIO.

            while (res.next()) {
                result = true;
                System.out.println("BD: Nombre usuario en base de datos."); //Esto es temporal para pruebas.
            }

        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            e.printStackTrace();
        } finally { 
            if(st != null) st.close();
            if(res != null) res.close(); 
        }
        
        cn.desconectar(conn);

        return result;
    }


    public boolean comprobarUsuario(String[] usuario) throws SQLException, NoSuchAlgorithmException {
        ConexionBD cn = INSTANCE;
        conn = cn.conectar();
        boolean result = false;
        String[] usuarioBD = new String[2]; //[0]nombre, [1]password
        String passwordCifrado = Cifrador.hasPassword(usuario[1]);
        try {  
            //Consulta a base de datos.
            ps = conn.prepareStatement("SELECT nombre, password FROM USUARIO WHERE nombre = ? and password = ?;");
            ps.setString(1, usuario[0]);
            ps.setString(2, passwordCifrado);
           
            //ps.executeUpdate();
            res = ps.executeQuery(); //consulta sql a tabla USUARIO.

            while (res.next()) {
                //Guardo los datos de la consulta en usuarioDB
                usuarioBD[0] = res.getString(1); 
                usuarioBD[1] = res.getString(2);
                
                //Comparo si el nombre y password pasados por parametro coinciden con los obtenidos en la consulta.
                if(usuario[0].equals(usuarioBD[0]) && passwordCifrado.equals(usuarioBD[1])) {
                    result = true;
                    System.out.println("BD: usuario macht en base de datos."); //Esto es temporal para pruebas.
                }

            }

        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            e.printStackTrace();
        } finally { 
            if(ps != null) st.close();
            if(res != null) res.close(); 
        }
        
        cn.desconectar(conn);

        return result;
    }

    public boolean insertarUsuario(Usuario user) throws SQLException, NoSuchAlgorithmException{
        ConexionBD cn = INSTANCE;
        boolean result = false;
        String password = Cifrador.hasPassword(user.getPassword());
        
        try {
            conn = cn.conectar();
            conn.setAutoCommit(false);

            //Insertamos la Jornada en la base de datos.
            ps = conn.prepareStatement("INSERT INTO USUARIO (nombre, password, directorio) VALUES (?,?,?);");
            ps.setString(1, user.getNombreUsuario());
            ps.setString(2, password);
            ps.setString(3, user.getDirectorio().getName());
           
            ps.executeUpdate();
            
            conn.commit(); //Confirma la transacción de la inserción de los datos.
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            System.out.println("Insercion Usuario en base de datos"); //Esto es temporal para pruebas.
            result = true;
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            e.printStackTrace();
        } finally { 
            if(ps != null) ps.close();  
        }
        
        cn.desconectar(conn);
        return result;
    }

    public Usuario getUsuario(String[] usuario) throws SQLException, NoSuchAlgorithmException {
        //[]usuario -> [0]nombre, [1]password
        ConexionBD cn = INSTANCE;
        conn = cn.conectar();
        Usuario usuarioRecuperado = null;
        String passwordCifrado = Cifrador.hasPassword(usuario[1]);

        try {  
            //Consulta a base de datos.
            ps = conn.prepareStatement("SELECT * FROM USUARIO WHERE nombre = ? and password = ?;");
            ps.setString(1, usuario[0]); 
            ps.setString(2, passwordCifrado);
           
            //ps.executeUpdate();
            res = ps.executeQuery(); //consulta sql a tabla USUARIO.

            while (res.next()) {
                //id-nombre-password-directorio
                usuarioRecuperado = new Usuario(res.getInt(1), res.getString(2), usuario[1], new File(res.getString(4)));
            }

        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            e.printStackTrace();
        } finally { 
            if(ps != null) ps.close();
            if(res != null) res.close(); 
        }
        
        cn.desconectar(conn);

        return usuarioRecuperado;
    }

    public boolean borrarUsuario(int idUsuario) throws SQLException {
        ConexionBD cn = INSTANCE;
        conn = cn.conectar();
        boolean result = false;
        
        try {
            conn.setAutoCommit(false);  
            st = conn.createStatement();
            int eliminados = st.executeUpdate("DELETE FROM USUARIO WHERE id = " + idUsuario + ";"); //consulta sql a tabla USUARIO.

            if(eliminados == 1) {
                result = true;
            } else if(eliminados > 1) {
                conn.rollback(); //Si borra mas de un usuario, hacemos un rollback por seguridad.
            }
            
            
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            e.printStackTrace();
        } finally { 
            if(st != null) st.close();
        }

        conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
        cn.desconectar(conn);

        return result;
    }


    public boolean getDatosUsuario(Usuario usuario) throws SQLException{
        ConexionBD cn = INSTANCE;
        boolean result = false;
        conn = cn.conectar();

        try {  
            //Consulta a base de datos.
            ps = conn.prepareStatement("SELECT * FROM DATOS_USUARIO WHERE id = " + usuario.getId() + ";");
            res = ps.executeQuery(); //consulta sql a tabla USUARIO.

            while (res.next()) {
                if(usuario.getId() == res.getInt(1)) {
                    usuario.setNombre(res.getString(2));
                    usuario.setApellido1(res.getString(3));
                    usuario.setApellido2(res.getString(4));
                    usuario.setTelefono(res.getInt(5));
                    usuario.setEmail(res.getString(6));
                    usuario.setEmailApp(res.getString(7));
                    usuario.setPasswordEmailApp(res.getString(8));
                    result = true;
                }
            }

        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            e.printStackTrace();
        } finally { 
            if(ps != null) ps.close();
            if(res != null) res.close(); 
        }
        
        cn.desconectar(conn);
        return result;
    }

    public boolean insertarDatosUsuario(Usuario usuario) throws SQLException {
        ConexionBD cn = INSTANCE;
        boolean result = false;
        conn = cn.conectar();

        try {
            conn.setAutoCommit(false);

            //Insertamos la Jornada en la base de datos.
            ps = conn.prepareStatement("INSERT INTO DATOS_USUARIO (id, nombre, apellido1, apellido2, telefono, email) VALUES (?,?,?,?,?,?);");
            ps.setInt(1, usuario.getId());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getApellido1());
            ps.setString(4, usuario.getApellido2());
            ps.setInt(5, usuario.getTelefono());
            ps.setString(6, usuario.getEmail());
           
            ps.executeUpdate();
            
            conn.commit(); //Confirma la transacción de la inserción de los datos.
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            System.out.println("Insercion Datos Usuario en base de datos"); //Esto es temporal para pruebas.
            result = true;
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            e.printStackTrace();
        } finally { 
            if(ps != null) ps.close();  
        }
        
        cn.desconectar(conn);
        return result;
    }

    public boolean modificarDatosUsuario(Usuario usuario) throws SQLException {
        ConexionBD cn = INSTANCE;
        boolean result = false;
        conn = cn.conectar();
    
        try {
            conn.setAutoCommit(false);

            ps = conn.prepareStatement("UPDATE DATOS_USUARIO SET nombre = ?, apellido1 = ?, apellido2 = ?, telefono = ?, email = ? WHERE id = ?;");
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido1());
            ps.setString(3, usuario.getApellido2());
            ps.setInt(4, usuario.getTelefono());
            ps.setString(5, usuario.getEmail());
            ps.setInt(6, usuario.getId());
            
            int n = ps.executeUpdate();

            conn.commit(); //Confirma la transacción de la inserción de los datos.
           
            if(n > 0) {
                result = true;
                System.out.println("Cambio de datos personales de usuario"); //Esto es temporal para pruebas.
            } else {
                result = false;
                System.out.println("NO se ha Cambio los datos personales de usuario"); //Esto es temporal para pruebas.
            }
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            if(ps != null) ps.close();
        }

        cn.desconectar(conn);
        return result;
    }

    public boolean modificarEmailApp(Usuario usuario) throws SQLException {
        ConexionBD cn = INSTANCE;
        boolean result = false;
        conn = cn.conectar();
    
        try {
            conn.setAutoCommit(false);

            ps = conn.prepareStatement("UPDATE DATOS_USUARIO SET email_app = ?, password_email_app = ? WHERE id = ?;");
            ps.setString(1, usuario.getEmailApp());
            ps.setString(2, usuario.getPasswordEmailApp());
            ps.setInt(3, usuario.getId());
            
            int n = ps.executeUpdate();

            conn.commit(); //Confirma la transacción de la inserción de los datos.
           
            if(n > 0) {
                result = true;
                System.out.println("Cambio de datos personales de usuario"); //Esto es temporal para pruebas.
            } else {
                result = false;
                System.out.println("NO se ha Cambio los datos personales de usuario"); //Esto es temporal para pruebas.
            }
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            if(ps != null) ps.close();
        }

        cn.desconectar(conn);
        return result;
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
            st = conn.createStatement();

            for(Clase clase : jornada.getClases()) {
                ps.setInt(1, clase.getNumero());
                ps.setString(2, clase.getTipo().toString());
                ps.setString(3, clase.getHoraClase().toString());
                ps.setString(4, clase.getAnotaciones());
                ps.setString(5, jornada.getFecha().toString());
                
                ps.executeUpdate();

                //Obtenemos el id que se le asignó a esta clase.
                res = st.executeQuery("SELECT last_insert_rowid()");
                clase.setId(res.getInt(1)); //Asignamos el id a la clase.
            }

            conn.commit(); //Confirma la transacción de la inserción de los datos.
            System.out.println("Insercion en base de datos"); //Esto es temporal para pruebas.
            result = true;
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            e.printStackTrace();
        } finally {
            if(ps != null) ps.close();
            if(st != null) st.close(); 
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
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

            ps = conn.prepareStatement("INSERT INTO DIRECCION (calle, numero, localidad_id, codigo_postal) " + 
                "VALUES (?,?,(SELECT id FROM LOCALIDAD WHERE nombre = ? AND provincia_id = (SELECT id FROM PROVINCIA WHERE nombre = ?)),?);");

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
            ps = conn.prepareStatement("INSERT INTO ALUMNO (nombre, apellido1, apellido2, genero, direccion_id, fecha_nacimiento, telefono, email, estado, asistencia_semanal) VALUES (?,?,?,?,?,?,?,?,?,?);");
            ps.setString(1, alumno.getNombre());
            ps.setString(2, alumno.getApellido1());
            ps.setString(3, alumno.getApellido2());
            ps.setString(4, alumno.getGenero().toString());
            ps.setInt(5, idDirInsertada);
            ps.setString(6, alumno.getFechaNacimiento().toString());
            ps.setInt(7, alumno.getTelefono());
            ps.setString(8, alumno.getEmail());
            ps.setString(9, alumno.getEstado().toString());
            ps.setInt(10, alumno.getAsistenciaSemanal());
            
            ps.executeUpdate();

            //Obtenemos el id que se le asignó a ese alumno.
            st = conn.createStatement();
            res = st.executeQuery("SELECT last_insert_rowid()");
            int idAlumInsertado = res.getInt(1); //Guardo el valor de id obtenido en la consulta en la variable.
            alumno.setId(idAlumInsertado); //Pongo el id al objeto Alumno.
            
            conn.commit(); //Confirma la transacción de la inserción de los datos.
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            System.out.println("Insercion en base de datos"); //Esto es temporal para pruebas.
            result = true;
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            e.printStackTrace();
        } finally { 
            if(ps != null) ps.close(); 
            if(res != null) res.close();
            if(st != null) st.close();
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


    public boolean borrarAlumnoEnClase(Alumno alumno, Clase clase, Jornada jornada) throws SQLException {
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
            ps = conn.prepareStatement("DELETE FROM CLASE_ALUMNO WHERE clase_id = ? AND alumno_id = ?;");
            ps.setInt(1, clase_id);
            ps.setInt(2, alumno.getId());
            
            ps.executeUpdate();
            
            conn.commit(); //Confirma la transacción de la inserción de los datos.
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            System.out.println("BD: borrado alumno en clase."); //Esto es temporal para pruebas.
            result = true;
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            e.printStackTrace();
        } finally { 
            if(ps != null) ps.close();
            if(st != null) st.close();
            if(res != null) res.close();
        }
        
        cn.desconectar(conn);
        return result;
    }


    public boolean actualizarAlumnosEnClase(ArrayList<Alumno> nuevaListaAlumnos, int idClase) throws SQLException {
        ConexionBD cn = INSTANCE;
        boolean result = false;
        ArrayList<Alumno> listaAlumnos = getListadoAlumnos(idClase);
        try {
            conn = cn.conectar();
            conn.setAutoCommit(false);

            //Borrar fila de la tabla CLASE_ALUMNO.
            for (Alumno alumno : listaAlumnos) {
                boolean macht = false;
                for (Alumno alumnoNL : nuevaListaAlumnos) {
                    if(alumno.getId() == alumnoNL.getId()) {
                        macht = true;
                        break;
                    }
                }
                if(!macht) {
                    ps = conn.prepareStatement("DELETE FROM CLASE_ALUMNO WHERE clase_id = ? AND alumno_id = ?;");
                    ps.setInt(1, idClase);
                    ps.setInt(2, alumno.getId());
                    ps.executeUpdate();
                }
            }

            //Insertar fila en la tabla CLASE_ALUMNO.
            for(Alumno alumnoNL : nuevaListaAlumnos) {
                boolean macht = false;
                for (Alumno alumno : listaAlumnos) {
                    if(alumnoNL.getId() == alumno.getId()) {
                        macht = true;
                        break;
                    }
                }
                if(!macht) {
                    //Insertamos el Alumno en la base de datos.
                    ps = conn.prepareStatement("INSERT INTO CLASE_AlUMNO (clase_id, alumno_id)  VALUES (?,?);");
                    ps.setInt(1, idClase);
                    ps.setInt(2, alumnoNL.getId());
                    ps.executeUpdate();
                }
            }
            
            conn.commit(); //Confirma la transacción de la inserción de los datos.
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            System.out.println("BD: Actualizada la lista de alumnos de Clase " + idClase + "."); //Esto es temporal para pruebas.
            result = true;
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            e.printStackTrace();
        } finally { 
            if(ps != null) ps.close(); 
            if(res != null) res.close();
            if(st != null) st.close();
        }
        
        cn.desconectar(conn);
        return result;
    }


    public boolean actualizarClase(Clase clase) throws SQLException{
        ConexionBD cn = INSTANCE;
        boolean result = false;
        try {
            conn = cn.conectar();
            conn.setAutoCommit(false);

            ps = conn.prepareStatement("UPDATE CLASE SET tipo = ?, hora = ?, anotaciones = ? WHERE id = ?;");
            ps.setString(1, clase.getTipo().toString());
            ps.setString(2, clase.getHoraClase().toString());
            ps.setString(3, clase.getAnotaciones());
            ps.setInt(4, clase.getId());
            ps.executeUpdate();
            
            conn.commit(); //Confirma la transacción de la inserción de los datos.
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            System.out.println("BD: Actualizada Clase " + clase.getId() + "."); //Esto es temporal para pruebas.
            result = true;
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            e.printStackTrace();
        } finally { 
            if(ps != null) ps.close(); 
            if(res != null) res.close();
            if(st != null) st.close();
        }
        
        cn.desconectar(conn);
        return result;
    }

    public boolean actualizarComentarioJornada(Jornada jornada) throws SQLException {
        ConexionBD cn = INSTANCE;
        boolean result = false;
        try {
            conn = cn.conectar();
            conn.setAutoCommit(false);

            ps = conn.prepareStatement("UPDATE JORNADA SET comentario = ? WHERE fecha = ?;");
            ps.setString(1, jornada.getComentario());
            ps.setString(2, jornada.getFecha().toString());
            ps.executeUpdate();
            
            conn.commit(); //Confirma la transacción de la inserción de los datos.
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            System.out.println("BD: Actualizado el comentario de Jornada " + jornada.getFecha().toString() + "."); //Esto es temporal para pruebas.
            result = true;
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            e.printStackTrace();
        } finally { 
            if(ps != null) ps.close(); 
            if(res != null) res.close();
            if(st != null) st.close();
        }
        
        cn.desconectar(conn);
        return result;
    }

    public ArrayList<Alumno> getListadoAlumnos() throws SQLException {
        ConexionBD cn = INSTANCE;
        ArrayList<Alumno> listaAlumnos = null;
        ArrayList<Direccion> listaDirecciones = getListadoDirecciones(); //Obtengo la lista de direcciones
        conn = cn.conectar();
        try {
            
            st = conn.createStatement();
            res = st.executeQuery("SELECT * FROM ALUMNO;"); //consulta sql a tabla ALUMNO.

            listaAlumnos = new ArrayList<Alumno>();
            while (res.next()) {
                Direccion direccion = null; //Por defecto la direccion es null;

                //Si la lista de direcciones no es null, creo los objetos Direccion con los valores de los objetos de la lista para cada Alumno.
                if(listaDirecciones != null) {
                    int idDireccion = res.getInt(6); //Obtengo el id de Direccion del registro que se esta recorriendo..
                    
                    //Recorro la lista de direcciones comparando el direccion_id del registro que se esta recorriendo con el id de los objetos de la listaDirecciones.
                    for(Direccion d : listaDirecciones) {
                        if(d.getId() == idDireccion) {
                            direccion = new Direccion(d);
                            break;
                        }
                    }
                }
                
                //Genero un Enumerado de tipo Genero a partir del valor rescatado en el campo genero del registro que se esta recorriendo.
                Genero genero;
                try {
                    genero = Genero.valueOf(res.getString(5));
                } catch (IllegalArgumentException e) {
                    //poner esto en el log.
                    genero = null;
                }

                //Genero un Enumerado de tipo EstadoAlumno a partir del valor rescatado en el campo genero del registro que se esta recorriendo.
                EstadoAlumno estado;
                try {
                    estado = EstadoAlumno.valueOf(res.getString(10));
                } catch (IllegalArgumentException e) {
                    //poner esto en el log.
                    estado = null;
                }

                //Creo el alumno con los datos del registro que se esta recorriendo y lo añado a la lista de Alumnos.
                listaAlumnos.add(new Alumno(res.getInt(1), res.getString(2), res.getString(3), res.getString(4), 
                    genero, direccion, LocalDate.parse(res.getString(7)), res.getInt(8), res.getString(9), estado, res.getInt(11)));
            }
            
            System.out.println("BD: Obtencion de listaAlumnos."); //Esto es temporal para pruebas.
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            e.printStackTrace();
        } finally { 
            if(st != null) st.close();
            if(res != null) res.close(); 
        }
        
        cn.desconectar(conn);
        return listaAlumnos;
    }

    //original
    public ArrayList<Alumno> getListadoAlumnos(int idClase) throws SQLException {
        ConexionBD cn = INSTANCE;
        ArrayList<Alumno> listaAlumnos = null;
        ArrayList<Direccion> listaDirecciones = getListadoDirecciones(); //Obtengo la lista de direcciones
        conn = cn.conectar();

        try {
            st = conn.createStatement();
            res = st.executeQuery("SELECT * FROM ALUMNO A JOIN CLASE_ALUMNO C ON(A.id = C.alumno_id) WHERE C.clase_id = " + idClase + " ;"); //consulta sql a tabla ALUMNO.

            listaAlumnos = new ArrayList<Alumno>();
            while (res.next()) {
                Direccion direccion = null; //Por defecto la direccion es null;

                //Si la lista de direcciones no es null, creo los objetos Direccion con los valores de los objetos de la lista para cada Alumno.
                if(listaDirecciones != null) {
                    int idDireccion = res.getInt(6); //Obtengo el id de Direccion del registro que se esta recorriendo..
                    
                    //Recorro la lista de direcciones comparando el direccion_id del registro que se esta recorriendo con el id de los objetos de la listaDirecciones.
                    for(Direccion d : listaDirecciones) {
                        if(d.getId() == idDireccion) {
                            direccion = new Direccion(d);
                            break;
                        }
                    }
                }
                
                //Genero un Enumerado de tipo Genero a partir del valor rescatado en el campo genero del registro que se esta recorriendo.
                Genero genero;
                try {
                    genero = Genero.valueOf(res.getString(5));
                } catch (IllegalArgumentException e) {
                    //poner esto en el log.
                    genero = null;
                }

                //Genero un Enumerado de tipo EstadoAlumno a partir del valor rescatado en el campo genero del registro que se esta recorriendo.
                EstadoAlumno estado;
                try {
                    estado = EstadoAlumno.valueOf(res.getString(10));
                } catch (IllegalArgumentException e) {
                    //poner esto en el log.
                    estado = null;
                }

                //Creo el alumno con los datos del registro que se esta recorriendo y lo añado a la lista de Alumnos.
                listaAlumnos.add(new Alumno(res.getInt(1), res.getString(2), res.getString(3), res.getString(4), 
                    genero, direccion, LocalDate.parse(res.getString(7)), res.getInt(8), res.getString(9), estado, res.getInt(11)));
            }
            
            System.out.println("BD: Obtencion de listaAlumnos de la Clase: " + idClase + "."); //Esto es temporal para pruebas.
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            e.printStackTrace();
        } finally { 
            if(st != null) st.close();
            if(res != null) res.close(); 
        }
        
        cn.desconectar(conn);
        return listaAlumnos;
    }


    public ArrayList<Direccion> getListadoDirecciones() throws SQLException {
        ConexionBD cn = INSTANCE;
        ArrayList<Direccion> listaDirecciones = null;
        conn = cn.conectar();
        try {
            st = conn.createStatement();
            res = st.executeQuery("SELECT D.id, D.calle, D.numero, L.nombre, P.nombre, D.codigo_postal " 
                                + "FROM DIRECCION D JOIN LOCALIDAD L ON(D.localidad_id = L.id) "
                                + "JOIN PROVINCIA P ON(L.provincia_id = P.id)"); //consulta sql a tablas DIRECCION, LOCALIDAD Y PROVINCIA.

            listaDirecciones = new ArrayList<Direccion>();
            while (res.next()) {
                listaDirecciones.add(new Direccion(res.getInt(1), res.getString(2), res.getInt(3), 
                    res.getString(4), res.getString(5), res.getInt(6)));
            }

            System.out.println("BD: Obtencion de listaDirecciones de base de datos. Direcciones obtenidas: " + listaDirecciones.size()); //Esto es temporal para pruebas.
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            e.printStackTrace();
        } finally { 
            if(st != null) st.close();
            if(res != null) res.close(); 
        }
        
        cn.desconectar(conn);
        return listaDirecciones;
    }

    public Jornada getJornadaCompleta(String fecha) throws SQLException {
        ConexionBD cn = INSTANCE;
        Jornada jornada = null;
        Clase[] listaClases = new Clase[8];
        ArrayList<Alumno> listaAlumnos = null;
        ArrayList<Direccion> listaDirecciones = null;
        conn = cn.conectar();

        try { 
            //Obtengo la jornada ------------------- 
            st = conn.createStatement();
            res = st.executeQuery("SELECT * FROM JORNADA WHERE fecha = '" + fecha + "';"); //consulta sql a tabla JORNADA.

            while (res.next()) {
                jornada = new Jornada(LocalDate.parse(res.getString(1)), res.getString(2));
            }
            
            if(jornada != null) {
                boolean resultadoOk = false;
                int posicionArrayClases = 0;

                //Obtengo la lista de clases ------------------
                res = st.executeQuery("SELECT id, numero, tipo, hora, anotaciones FROM CLASE WHERE jornada = '" + fecha + "' ORDER BY numero;"); //consulta sql a tabla CLASE.

                //Recorre las filas devueltas por la consulta a la tabla CLASE y crea por cada fila un Objeto de tipo Clase añadiendolo al Array listaClases.
                while (res.next()) {
                    resultadoOk = true; //pongo a true si hay resultados en la consulta a la tabla CLASE.

                    //Crea un Enumerado de tipo TipoClase a partir del valor rescatado en el campo tipo del registro que se esta recorriendo.
                    TipoClase tipo;
                    try {
                        tipo = TipoClase.valueOf(res.getString(3));
                    } catch (IllegalArgumentException e) {
                        //poner esto en el log.
                        tipo = null;
                    }

                    //Crea un Enumerado de tipo HoraClase a partir del valor rescatado en el campo tipo del registro que se esta recorriendo.
                    HoraClase hora;
                    try {
                        String[] partes = res.getString(4).split(":"); //Crea un Array con la hora y los minutos
                        hora = HoraClase.getHoraClase(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]));
                    } catch (IllegalArgumentException e) {
                        //poner esto en el log.
                        System.out.println("BD: Fallo - No se asigna la hora de la clase.");
                        hora = null;
                    }

                    //Añade una nueva Clase al Array ListaClases en la posicion posicionArrayClase.
                    listaClases[posicionArrayClases] = new Clase(res.getInt(1), res.getInt(2), tipo, hora, res.getString(5));
                    posicionArrayClases++;
                }

                //Si no hay registros, ponemos la listaClases a null.
                if(!resultadoOk) listaClases = null;

                if(listaClases != null) {
                    res = st.executeQuery("SELECT D.id, D.calle, D.numero, L.nombre, P.nombre, D.codigo_postal " 
                                + "FROM DIRECCION D JOIN LOCALIDAD L ON(D.localidad_id = L.id) "
                                + "JOIN PROVINCIA P ON(L.provincia_id = P.id)"); //consulta sql a tablas DIRECCION, LOCALIDAD Y PROVINCIA.

                    listaDirecciones = new ArrayList<Direccion>();

                    //Recorro las filas de la consulta a la tabla DIRECCION y guardo el resultado en listaDirecciones.
                    while (res.next()) {
                        listaDirecciones.add(new Direccion(res.getInt(1), res.getString(2), res.getInt(3), 
                        res.getString(4), res.getString(5), res.getInt(6)));
                    }

                    for (int i = 0; i < listaClases.length; i++) {
                        res = st.executeQuery("SELECT * FROM ALUMNO A JOIN CLASE_ALUMNO C ON(A.id = C.alumno_id) WHERE C.clase_id = " + listaClases[i].getId() + " ;"); //consulta sql a tabla ALUMNO.

                        listaAlumnos = new ArrayList<Alumno>();

                        while (res.next()) {
                            Direccion direccion = null; //Por defecto la direccion es null;

                            //Si la lista de direcciones no es null, creo los objetos Direccion con los valores de los objetos de la lista para cada Alumno.
                            if(listaDirecciones != null) {
                                int idDireccion = res.getInt(6); //Obtengo el id de Direccion del registro que se esta recorriendo..
                    
                                //Recorro la lista de direcciones comparando el direccion_id del registro que se esta recorriendo con el id de los objetos de la listaDirecciones.
                                for(Direccion d : listaDirecciones) {
                                    if(d.getId() == idDireccion) {
                                        direccion = new Direccion(d);
                                        break;
                                    }
                                }
                            }

                            //Genero un Enumerado de tipo Genero a partir del valor rescatado en el campo genero del registro que se esta recorriendo.
                            Genero genero;
                            try {
                                genero = Genero.valueOf(res.getString(5));
                            } catch (IllegalArgumentException e) {
                                //poner esto en el log.
                                genero = null;
                            }

                            //Genero un Enumerado de tipo EstadoAlumno a partir del valor rescatado en el campo genero del registro que se esta recorriendo.
                            EstadoAlumno estado;
                            try {
                                estado = EstadoAlumno.valueOf(res.getString(10));
                            } catch (IllegalArgumentException e) {
                                //poner esto en el log.
                                estado = null;
                            }

                            //Creo el alumno con los datos del registro que se esta recorriendo y lo añado a la lista de Alumnos.
                            listaAlumnos.add(new Alumno(res.getInt(1), res.getString(2), res.getString(3), res.getString(4), 
                                genero, direccion, LocalDate.parse(res.getString(7)), res.getInt(8), res.getString(9), estado, res.getInt(11)));
                        }

                        listaClases[i].setListaAlumnos(listaAlumnos); //añado la lista de alumnos a su Clase.
                    }
                }
                jornada.setClases(listaClases); //añado la lista de clases a la Jornada
                System.out.println("BD: Obtencion de Jornada " + jornada.getFecha().toString() + " de base de datos."); //Esto es temporal para pruebas.
            } 
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            e.printStackTrace();
        } finally { 
            if(st != null) st.close();
            if(res != null) res.close(); 
        }

        cn.desconectar(conn);
        return jornada;
    }

    //Original
    public Jornada getJornada(String fecha) throws SQLException {
        ConexionBD cn = INSTANCE;
        Jornada jornada = null;
        conn = cn.conectar();
        
        try {  
            st = conn.createStatement();
            res = st.executeQuery("SELECT * FROM JORNADA WHERE fecha = '" + fecha + "';"); //consulta sql a tabla JORNADA.

            while (res.next()) {
                jornada = new Jornada(LocalDate.parse(res.getString(1)), res.getString(2));
                System.out.println("BD: Obtencion de Jornada " + jornada.getFecha().toString() + " de base de datos."); //Esto es temporal para pruebas.
            }

        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            e.printStackTrace();
        } finally { 
            if(st != null) st.close();
            if(res != null) res.close(); 
        }
        
        cn.desconectar(conn);

        //Añado a la Jornada el Array de Clase.
        if(jornada != null) {
           jornada.setClases(getArrayClases(jornada));
        }

        return jornada;
    }


    public Clase[] getArrayClases(Jornada jornada) throws SQLException {
        ConexionBD cn = INSTANCE;
        Clase[] listaClases = new Clase[8];
        conn = cn.conectar();
        
        try {
            st = conn.createStatement();
            res = st.executeQuery("SELECT id, numero, tipo, hora, anotaciones FROM CLASE WHERE jornada = '" + jornada.getFecha().toString() + "' ORDER BY numero;"); //consulta sql a tabla CLASE.

            boolean resultadoOk = false;
            int posicionArray = 0;

            //Recorre las filas devueltas por la consulta y crea por cada fila un Objeto de tipo Clase añadiendolo al Array listaClases.
            while (res.next()) {
                resultadoOk = true;

                //Crea un Enumerado de tipo TipoClase a partir del valor rescatado en el campo tipo del registro que se esta recorriendo.
                TipoClase tipo;
                try {
                    tipo = TipoClase.valueOf(res.getString(3));
                } catch (IllegalArgumentException e) {
                    //poner esto en el log.
                    tipo = null;
                }

                //Crea un Enumerado de tipo HoraClase a partir del valor rescatado en el campo tipo del registro que se esta recorriendo.
                HoraClase hora;
                try {
                    String[] partes = res.getString(4).split(":"); //Crea un Array con la hora y los minutos
                    hora = HoraClase.getHoraClase(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]));
                } catch (IllegalArgumentException e) {
                    //poner esto en el log.
                    System.out.println("BD: Fallo - No se asigna la hora de la clase.");
                    hora = null;
                }

                //Añade una nueva Clase al Array ListaClases en la posicion posicionArray.
                listaClases[posicionArray] = new Clase(res.getInt(1), res.getInt(2), tipo, hora, res.getString(5));
                posicionArray++;
            }

            //Si no hay registros, ponemos la listaClases a null.
            if(!resultadoOk) listaClases = null;

            System.out.println("BD: Obtencion de Array de Clases de la jornada " + jornada.getFecha().toString() + "."); //Esto es temporal para pruebas.
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            e.printStackTrace();
        } finally { 
            if(st != null) st.close();
            if(res != null) res.close(); 
        }
        
        cn.desconectar(conn);


        if(listaClases != null) {
            setAlumnosClases(listaClases);
        }

        return listaClases;
    }

    //Copia pruebas
    private void setAlumnosClases(Clase[] listaClases) throws SQLException {
        ConexionBD cn = INSTANCE;
        ArrayList<Alumno> listaAlumnos = null;
        //ArrayList<Direccion> listaDirecciones = getListadoDirecciones(); //Obtengo la lista de direcciones
        conn = cn.conectar();
        try {
            
            st = conn.createStatement();
            for (int i = 0; i < listaClases.length; i++) {
                res = st.executeQuery("SELECT * FROM ALUMNO A JOIN CLASE_ALUMNO C ON(A.id = C.alumno_id) WHERE C.clase_id = " + listaClases[i].getId() + " ;"); //consulta sql a tabla ALUMNO.

                listaAlumnos = new ArrayList<Alumno>();
                while (res.next()) {
                    //Genero un Enumerado de tipo Genero a partir del valor rescatado en el campo genero del registro que se esta recorriendo.
                    Genero genero;
                    try {
                        genero = Genero.valueOf(res.getString(5));
                    } catch (IllegalArgumentException e) {
                        //poner esto en el log.
                        genero = null;
                    }

                    //Genero un Enumerado de tipo EstadoAlumno a partir del valor rescatado en el campo genero del registro que se esta recorriendo.
                    EstadoAlumno estado;
                    try {
                        estado = EstadoAlumno.valueOf(res.getString(10));
                    } catch (IllegalArgumentException e) {
                        //poner esto en el log.
                        estado = null;
                    }

                    //Creo el alumno con los datos del registro que se esta recorriendo y lo añado a la lista de Alumnos.
                    listaAlumnos.add(new Alumno(res.getInt(1), res.getString(2), res.getString(3), res.getString(4), 
                        genero, new Direccion(), LocalDate.parse(res.getString(7)), res.getInt(8), res.getString(9), estado, res.getInt(11)));
                }

            listaClases[i].setListaAlumnos(listaAlumnos);
            System.out.println("BD: Obtencion de listaAlumnos de la Clase: " + listaClases[i].getId() + "."); //Esto es temporal para pruebas.
                
            }
            
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            e.printStackTrace();
        } finally { 
            if(st != null) st.close();
            if(res != null) res.close(); 
        }
        
        cn.desconectar(conn);
        //return listaClases;
    }


    public ArrayList<String> getProvincias() throws SQLException {
        ConexionBD cn = INSTANCE;
        ArrayList<String> listaProvincias = null;
        conn = cn.conectar();
        try {
            st = conn.createStatement();
            res = st.executeQuery("SELECT nombre FROM PROVINCIA");

            listaProvincias = new ArrayList<String>();
            while (res.next()) {
                listaProvincias.add(res.getString(1));
            }

            System.out.println("BD: Obtencion de listaProvincias de base de datos. Provincias obtenidas: " + listaProvincias.size()); //Esto es temporal para pruebas.
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            e.printStackTrace();
        } finally { 
            if(st != null) st.close();
            if(res != null) res.close(); 
        }
        
        cn.desconectar(conn);
        return listaProvincias;
    }


    public ArrayList<String> getLocalidades(String provincia) throws SQLException {
        ConexionBD cn = INSTANCE;
        ArrayList<String> listaLocalidades = null;
        conn = cn.conectar();
        try {
            st = conn.createStatement();
            res = st.executeQuery("SELECT nombre FROM LOCALIDAD WHERE provincia_id = (SELECT id FROM PROVINCIA WHERE nombre = '" + provincia + "');");

            listaLocalidades = new ArrayList<String>();
            while (res.next()) {
                listaLocalidades.add(res.getString(1));
            }

            System.out.println("BD: Obtencion de listaProvincias de base de datos. Provincias obtenidas: " + listaLocalidades.size()); //Esto es temporal para pruebas.
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            e.printStackTrace();
        } finally { 
            if(st != null) st.close();
            if(res != null) res.close(); 
        }
        
        cn.desconectar(conn);
        return listaLocalidades;
    }


    /**
     * Actualiza los datos del alumno en la base de datos. 
     * @param alumno Alumno de donde se obtine la informacion.
     * @return true si se modifica y no hay errores, false si no.
     * @throws SQLException
     */
    public boolean modificarAlumno(Alumno alumno) throws SQLException{
        ConexionBD cn = INSTANCE;
        conn = cn.conectar();
        boolean result = false;

        try {       
            conn.setAutoCommit(false);

            ps = conn.prepareStatement("UPDATE ALUMNO SET nombre = ?, apellido1 = ?, apellido2 = ?, genero = ?, fecha_nacimiento = ?, telefono = ?, email = ?, estado = ?, asistencia_semanal = ? WHERE id = ?;");
            ps.setString(1, alumno.getNombre());
            ps.setString(2, alumno.getApellido1());
            ps.setString(3, alumno.getApellido2());
            ps.setString(4, alumno.getGenero().toString());
            ps.setString(5, alumno.getFechaNacimiento().toString());
            ps.setInt(6, alumno.getTelefono());
            ps.setString(7, alumno.getEmail());
            ps.setString(8, alumno.getEstado().toString());
            ps.setInt(9, alumno.getAsistenciaSemanal());
            ps.setInt(10, alumno.getId());
            ps.executeUpdate();

            ps = conn.prepareStatement("UPDATE DIRECCION SET calle = ?, numero = ?, localidad_id = (SELECT id FROM LOCALIDAD WHERE nombre = ?), codigo_postal = ? WHERE id = ?;");
            ps.setString(1, alumno.getDireccion().getCalle());
            ps.setInt(2, alumno.getDireccion().getNumero());
            ps.setString(3, alumno.getDireccion().getLocalidad());
            ps.setInt(4, alumno.getDireccion().getCodigoPostal());
            ps.setInt(5, alumno.getDireccion().getId()); 
            ps.executeUpdate();
            
            conn.commit(); //Confirma la transacción de la inserción de los datos.
            result = true;
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true);
            if(ps != null) ps.close();
        }

        cn.desconectar(conn);
        return result;
    }


    /**
     * Borra el alumno pasado como parametro y su direccion de la base de datos.
     * @param alumno Alumno del que se va ha borrar sus datos de la base de datos.
     * @return true si borra el alumno y la direccion o false si no.
     * @throws SQLException
     */
    public boolean borrarAlumno(Alumno alumno) throws SQLException {
        ConexionBD cn = INSTANCE;
        conn = cn.conectar();
        boolean result = false;

        try {
            conn.setAutoCommit(false);

            ps = conn.prepareStatement("DELETE FROM ALUMNO WHERE id = ?;");
            ps.setInt(1, alumno.getId());
            ps.executeUpdate();

            ps = conn.prepareStatement("DELETE FROM DIRECCION WHERE id = ?;");
            ps.setInt(1, alumno.getDireccion().getId());
            ps.executeUpdate();

            conn.commit(); //Confirma la transacción de la inserción de los datos.
           
            System.out.println("BD: Borrado Alumno de la base de datos"); //Esto es temporal para pruebas.
            
            result = true;
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            if(ps != null) ps.close();
        }

        cn.desconectar(conn);
        return result;
    }
    

    /**
     * Devuelve el numero de clases en la que esta inscrito el alumna en la semana de la jornada pasada como parametro.
     * @param idAlumno Identificador del alumno del que se va a obtener la información
     * @param jornada Jornada de la que se va a extraer la semana para la busqueda.
     * @return int con el numero de clases que esta incrito el alumno en la semana de la jornada pasada como parametro o -1 si hay algun problema.
     * @throws SQLException
     */
    public int numeroClasesIsncrito(int idAlumno, Jornada jornada ) throws SQLException {
        ConexionBD cn = INSTANCE;
        conn = cn.conectar();
        int result = -1;

        try {  
            //Consulta a base de datos.
            //strftime('%W', fecha) -> devuelve el numero de semana.
            //strftime('%Y', feha) -> devuelve el año.
            String sql = "SELECT count(CS.alumno_id) FROM CLASE_ALUMNO CS JOIN CLASE C ON(CS.clase_id = C.id)"
                    + "WHERE CS.alumno_id = ? AND strftime('%W', C.jornada) = ? AND strftime('%Y', C.jornada) = ?;";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, idAlumno);
            ps.setString(2, Integer.toString(jornada.getFecha().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)));
            ps.setString(3, Integer.toString(jornada.getFecha().getYear()));
           
            res = ps.executeQuery();

            result = res.getInt(1);
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            e.printStackTrace();
        } finally { 
            if(ps != null) st.close();
            if(res != null) res.close(); 
        }
        
        cn.desconectar(conn);
        return result;
    }

}
