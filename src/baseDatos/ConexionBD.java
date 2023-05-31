package baseDatos;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
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
        URLConexion = cadenaConexionParte1 + user.getDirectorio().getName() + "\\" + user.getNombre() + cadenaConexionParte2 + user.getPasswordBD();
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
                "directorio TEXT NOT NULL, " +
                "passwordBD TEXT NOT NULL); "; 
        st.execute(sql);
        
        System.out.println("BD: Creada tabla usuario de APP."); //Esto es temporal para pruebas.
        st.close();
        cn.desconectar(conn);
    }

    public void crearTablasUsuario() throws SQLException {
        ConexionBD cn = INSTANCE;
        conn = cn.conectar();
        st = conn.createStatement();
        String sql;


        //Crea la tabla "PROVINCIA"
        sql = "CREATE TABLE IF NOT EXISTS PROVINCIA (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL UNIQUE);";  
        st.execute(sql);

        //Crea la tabla "LOCALIDAD"
        sql = "CREATE TABLE IF NOT EXISTS LOCALIDAD (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "provincia_id INTEGER NOT NULL, " +
                "FOREIGN KEY (provincia_id) REFERENCES provincia (id));";   
        st.execute(sql);

        //Crea la tabla "direccion"
        sql = "CREATE TABLE IF NOT EXISTS DIRECCION (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "calle TEXT, " +
                "numero INTEGER, " +
                "localidad_id INTEGER NOT NULL, " +
                "codigo_postal INTEGER, " +
                "FOREIGN KEY (localidad_id) REFERENCES localidad (id));";  
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
                "estado TEXT NOT NULL, " +
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
        st.execute(sql);

        System.out.println("BD: Creadas tablas."); //Esto es temporal para pruebas.
        st.close();
        cn.desconectar(conn);
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


    public boolean comprobarUsuario(String[] usuario) throws SQLException {
        ConexionBD cn = INSTANCE;
        conn = cn.conectar();
        boolean result = false;
        String[] usuarioBD = new String[2]; //[0]nombre, [1]password
        
        try {  
            //Consulta a base de datos.
            ps = conn.prepareStatement("SELECT nombre, password FROM USUARIO WHERE nombre = ? and password = ?;");
            ps.setString(1, usuario[0]);
            ps.setString(2, usuario[1]);
           
            //ps.executeUpdate();
            res = ps.executeQuery(); //consulta sql a tabla USUARIO.

            while (res.next()) {
                //Guardo los datos de la consulta en usuarioDB
                usuarioBD[0] = res.getString(1); 
                usuarioBD[1] = res.getString(2);
                
                //Comparo si el nombre y password pasados por parametro coinciden con los obtenidos en la consulta.
                if(usuario[0].equals(usuarioBD[0]) && usuario[1].equals(usuarioBD[1])) {
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

    public boolean insertarUsuario(Usuario user) throws SQLException{
        ConexionBD cn = INSTANCE;
        boolean result = false;
        try {
            conn = cn.conectar();
            conn.setAutoCommit(false);

            //Insertamos la Jornada en la base de datos.
            ps = conn.prepareStatement("INSERT INTO USUARIO (nombre, password, directorio, passwordBD) VALUES (?,?,?,?);");
            ps.setString(1, user.getNombre());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getDirectorio().getName());
            ps.setString(4, user.getPasswordBD());
           
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

    public Usuario getUsuario(String[] usuario) throws SQLException {
        ConexionBD cn = INSTANCE;
        conn = cn.conectar();
        String[] usuarioBD = new String[2]; //[0]nombre, [1]password
        Usuario usuarioRecuperado = null;

        try {  
            //Consulta a base de datos.
            ps = conn.prepareStatement("SELECT * FROM USUARIO WHERE nombre = ? and password = ?;");
            ps.setString(1, usuario[0]);
            ps.setString(2, usuario[1]);
           
            //ps.executeUpdate();
            res = ps.executeQuery(); //consulta sql a tabla USUARIO.

            while (res.next()) {
                //id-nombre-password-directorio-passwordDB
                usuarioRecuperado = new Usuario(res.getInt(1), res.getString(2), res.getString(3), new File(res.getString(4)), res.getString(5));
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
            ps = conn.prepareStatement("INSERT INTO ALUMNO (nombre, apellido1, apellido2, genero, direccion_id, fecha_nacimiento, telefono, email, estado) VALUES (?,?,?,?,?,?,?,?,?);");
            ps.setString(1, alumno.getNombre());
            ps.setString(2, alumno.getApellido1());
            ps.setString(3, alumno.getApellido2());
            ps.setString(4, alumno.getGenero().toString());
            ps.setInt(5, idDirInsertada);
            ps.setString(6, alumno.getFechaNacimiento().toString());
            ps.setInt(7, alumno.getTelefono());
            ps.setString(8, alumno.getEmail());
            ps.setString(9, alumno.getEstado().toString());
            
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
            System.out.println("Insercion en base de datos"); //Esto es temporal para pruebas.
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


    public boolean actualizarAlumnosEnClase(ArrayList<Alumno> nuevaListaAlumnos, int numeroClase, Jornada jornada) throws SQLException {
        ConexionBD cn = INSTANCE;
        boolean result = false;
        ArrayList<Alumno> listaAlumnos = getListadoAlumnos(jornada, numeroClase);
        try {
            conn = cn.conectar();
            conn.setAutoCommit(false);

            //Obtenemos el id de la clase.
            st = conn.createStatement();
            res = st.executeQuery("SELECT id FROM CLASE WHERE numero = " + numeroClase + " and jornada = '" + jornada.getFecha().toString() + "';");
            int clase_id = res.getInt(1); //Guardo el valor de id obtenido en la consulta en la variable.

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
                    ps.setInt(1, clase_id);
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
                    ps.setInt(1, clase_id);
                    ps.setInt(2, alumnoNL.getId());
                    ps.executeUpdate();
                }
            }
            
            conn.commit(); //Confirma la transacción de la inserción de los datos.
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            System.out.println("BD: Actualizada la lista de alumnos de Clase " + numeroClase + " de Jornada " + jornada.getFecha().toString() + "."); //Esto es temporal para pruebas.
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


    public boolean actualizarClase(Clase clase, Jornada jornada) throws SQLException{
        ConexionBD cn = INSTANCE;
        boolean result = false;
        try {
            conn = cn.conectar();
            conn.setAutoCommit(false);

            //Obtenemos el id de la clase.
            st = conn.createStatement();
            res = st.executeQuery("SELECT id FROM CLASE WHERE numero = " + clase.getNumero() + " and jornada = '" + jornada.getFecha().toString() + "';");
            int clase_id = res.getInt(1); //Guardo el valor de id obtenido en la consulta en la variable.

            ps = conn.prepareStatement("UPDATE CLASE SET tipo = ?, hora = ?, anotaciones = ? WHERE id = ?;");
            ps.setString(1, clase.getTipo().toString());
            ps.setString(2, clase.getHoraClase().toString());
            ps.setString(3, clase.getAnotaciones());
            ps.setInt(4, clase_id);
            ps.executeUpdate();
            
            conn.commit(); //Confirma la transacción de la inserción de los datos.
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            System.out.println("BD: Actualizada Clase " + clase.getNumero() + " de Jornada " + jornada.getFecha().toString() + "."); //Esto es temporal para pruebas.
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
                    genero, direccion, LocalDate.parse(res.getString(7)), res.getInt(8), res.getString(9), estado));
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


    public ArrayList<Alumno> getListadoAlumnos(Jornada jornada, int numeroClase) throws SQLException {
        ConexionBD cn = INSTANCE;
        ArrayList<Alumno> listaAlumnos = null;
        ArrayList<Direccion> listaDirecciones = getListadoDirecciones(); //Obtengo la lista de direcciones
        conn = cn.conectar();
        try {
            
            //Obtenemos el id que se le asignó a esa dirección.
            st = conn.createStatement();
            res = st.executeQuery("SELECT id FROM CLASE WHERE numero = " + numeroClase + " and jornada = '" + jornada.getFecha().toString() + "';");
            int clase_id = res.getInt(1); //Guardo el valor de id obtenido en la consulta en la variable.

            st = conn.createStatement();
            res = st.executeQuery("SELECT * FROM ALUMNO A JOIN CLASE_ALUMNO C ON(A.id = C.alumno_id) WHERE C.clase_id = " + clase_id + " ;"); //consulta sql a tabla ALUMNO.

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
                    genero, direccion, LocalDate.parse(res.getString(7)), res.getInt(8), res.getString(9), estado));
            }
            
            System.out.println("BD: Obtencion de listaAlumnos de la Clase " + numeroClase + ". Jornada " + jornada.getFecha().toString() + "."); //Esto es temporal para pruebas.
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


    public Jornada getJornada(String fecha) throws SQLException {
        ConexionBD cn = INSTANCE;
        Jornada jornada = null;
        conn = cn.conectar();
        
        try {  
            st = conn.createStatement();
            res = st.executeQuery("SELECT  * FROM JORNADA WHERE fecha = '" + fecha + "';"); //consulta sql a tabla JORNADA.

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
            res = st.executeQuery("SELECT  numero, tipo, hora, anotaciones FROM CLASE WHERE jornada = '" + jornada.getFecha().toString() + "' ORDER BY numero;"); //consulta sql a tabla CLASE.

            boolean resultadoOk = false;
            int posicionArray = 0;

            //Recorre las filas devueltas por la consulta y crea por cada fila un Objeto de tipo Clase añadiendolo al Array listaClases.
            while (res.next()) {
                resultadoOk = true;

                //Crea un Enumerado de tipo TipoClase a partir del valor rescatado en el campo tipo del registro que se esta recorriendo.
                TipoClase tipo;
                try {
                    tipo = TipoClase.valueOf(res.getString(2));
                } catch (IllegalArgumentException e) {
                    //poner esto en el log.
                    tipo = null;
                }

                //Crea un Enumerado de tipo HoraClase a partir del valor rescatado en el campo tipo del registro que se esta recorriendo.
                HoraClase hora;
                try {
                    String[] partes = res.getString(3).split(":"); //Crea un Array con la hora y los minutos
                    hora = HoraClase.getHoraClase(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]));
                } catch (IllegalArgumentException e) {
                    //poner esto en el log.
                    System.out.println("BD: Fallo - No se asigna la hora de la clase.");
                    hora = null;
                }

                //Añade una nueva Clase al Array ListaClases en la posicion posicionArray.
                listaClases[posicionArray] = new Clase(res.getInt(1), tipo, hora, res.getString(4));
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
            for (int i = 0; i < listaClases.length; i++) {
                listaClases[i].setListaAlumnos(getListadoAlumnos(jornada, listaClases[i].getNumero()));
            }
        }

        return listaClases;
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

    public boolean modificarAlumno(Alumno alumno) throws SQLException{
        ConexionBD cn = INSTANCE;
        boolean result = false;
        try {
            conn = cn.conectar();
            conn.setAutoCommit(false);

            ps = conn.prepareStatement("UPDATE ALUMNO SET nombre = ?, apellido1 = ?, apellido2 = ?, genero = ?, fecha_nacimiento = ?, telefono = ?, email = ?, estado = ? WHERE id = ?;");
            ps.setString(1, alumno.getNombre());
            ps.setString(2, alumno.getApellido1());
            ps.setString(3, alumno.getApellido2());
            ps.setString(4, alumno.getGenero().toString());
            ps.setString(5, alumno.getFechaNacimiento().toString());
            ps.setInt(6, alumno.getTelefono());
            ps.setString(7, alumno.getEmail());
            ps.setString(8, alumno.getEstado().toString());
            ps.setInt(9, alumno.getId());
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

    public boolean borrarAlumno(Alumno alumno) throws SQLException {
        ConexionBD cn = INSTANCE;
        boolean result = false;
        try {
            conn = cn.conectar();
            conn.setAutoCommit(false);

            ps = conn.prepareStatement("DELETE FROM ALUMNO WHERE id = ?;");
            ps.setInt(1, alumno.getId());
            ps.executeUpdate();

            ps = conn.prepareStatement("DELETE FROM DIRECCION WHERE id = ?;");
            ps.setInt(1, alumno.getDireccion().getId());
            ps.executeUpdate();

            conn.commit(); //Confirma la transacción de la inserción de los datos.
           
            System.out.println("Borrado Alumno de la base de datos"); //Esto es temporal para pruebas.
            
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
    

}
