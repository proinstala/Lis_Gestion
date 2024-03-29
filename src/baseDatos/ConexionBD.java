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
import java.time.Month;
import java.time.YearMonth;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import modelo.Alumno;
import modelo.AlumnoReport;
import modelo.Clase;
import modelo.ClaseReport;
import modelo.Direccion;
import modelo.EstadoAlumno;
import modelo.EstadoPago;
import modelo.FormaPago;
import modelo.Genero;
import modelo.GrupoAlumnos;
import modelo.HoraClase;
import modelo.Jornada;
import modelo.Mensualidad;
import modelo.TipoClase;
import modelo.Usuario;
import utilidades.Cifrador;
import utilidades.Constants;
import utilidades.Fechas;


/**
 * Clase que representa una conexión a la base de datos.
 * 
 * @author David Jimenez Alonso.
 */
public class ConexionBD implements Cloneable{
	private static final ConexionBD INSTANCE = new ConexionBD(); //Singleton
    private Connection conn;
    private Statement st;
    private ResultSet res;
    private PreparedStatement ps;
    private String URLConexion = "";
    private final String cadenaConexionParte1 = "jdbc:sqlite:";
    private final String cadenaConexionParte2 = "datab.db?cipher=sqlcipher&legacy=4&key=";
    public final String FINAL_NOMBRE_FICHERO_DB = "datab.db"; 
    private Logger logger;

    private final String SQL_OBTENER_ULTIMO_ID_INSERTADO = "SELECT last_insert_rowid()";

    private ConexionBD() {}

    /**
     * Retorna la instancia única de la clase ConexionBD.
     * 
     * @return Instancia única de ConexionBD.
     */
    public static ConexionBD getInstance() { //Singleton
        return INSTANCE;
    }

    /**
     * Establece el usuario para la conexión a la base de datos.
     * 
     * @param user El usuario para la conexión.
     */
    public void setUsuario(Usuario user) {
        URLConexion = cadenaConexionParte1 + user.getDirectorio().getName() + "\\" + user.getNombreUsuario().toLowerCase() + cadenaConexionParte2 + user.getPassword();
        setLog(user);
    }
    
    /**
     * Retorna la URL de conexión actual.
     * @return Un String con la URL de conexión.
     */
    public String getURLConexion() {
        return URLConexion;
    }

    /**
     * Método clone para evitar la clonación del objeto.
     * 
     * @throws CloneNotSupportedException Si se intenta clonar el objeto.
     */
    @Override
    public Object clone() throws CloneNotSupportedException { //Singleton - Para evitar la clonacion del objeto.
        throw new CloneNotSupportedException();
    }
    
    /**
     * Establece la conexión a la base de datos.
     * 
     * @return Objeto Connection que representa la conexión establecida.
     * @throws SQLException Si ocurre un error al establecer la conexión.
     */
    private Connection conectar() throws SQLException {
        conn = DriverManager.getConnection(URLConexion);
        return conn;
    }

    /**
     * Cierra la conexión a la base de datos.
     * 
     * @param con Objeto Connection que representa la conexión a cerrar.
     * @throws SQLException Si ocurre un error al cerrar la conexión.
     */
    private void desconectar(Connection con) throws SQLException {
        if(con != null) {
            con.close();
        }
    }

    /**
     * Establece el log para el usuario especificado.
     * @param user El usuario para el cual se establecerá el log.
     */
    private void setLog(Usuario user) {
        if (user.getNombreUsuario().equals("root")) {
            logger = Logger.getLogger(Constants.USER_ROOT); //Crea una instancia de la clase Logger asociada al nombre de registro.
        } else {
            logger = Logger.getLogger(Constants.USER); //Crea una instancia de la clase Logger asociada al nombre de registro.
        }
    }

    /**
     * Crea las tablas necesarias en la base de datos de la aplicación.
     *
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL durante la creación de las tablas.
     */
    public void crearTablasApp() throws SQLException {
        ConexionBD cn = INSTANCE;
        conn = cn.conectar();
       
        try {
            conn.setAutoCommit(false);
            TablaManager.crearTablasApp(conn); //Llama al método estático de TablaManager para crear las tablas de la aplicación.
            conn.commit(); //Confirma la transacción de la inserción de los datos.
        } catch (SQLException e) {
            conn.rollback(); // Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            cn.desconectar(conn);
        }
         
        logger.config("BD: Creada tabla APP.");
    }

    /**
     * Crea las tablas necesarias en la base de datos de cada usuario e inserta los datos iniciales.
     *
     * @return true si se crearon las tablas correctamente, false en caso contrario.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL durante la creación de las tablas.
     */
    public boolean crearTablasUsuario() throws SQLException {
        ConexionBD cn = INSTANCE;
        Boolean result = false;
        conn = cn.conectar();

        try {
            conn.setAutoCommit(false);

            //Llama al método estático de TablaManager para crear las tablas del usuario.
            if(TablaManager.crearTablasUsuario(conn)) {
                result = true;

                //Llama al método estático de TablaManager para crear los precios de clase.
                if(!InsercionManager.insertPreciosClase(conn)) {
                    logger.warning("BD: NO se han insertado los precios de las clases.");
                    result = false;
                } 

                //Llama al método estático de TablaManager para crear las provincias.
                if(InsercionManager.insertProvincias(conn)) {
                    //Llama al método estático de TablaManager para crear las localidades.
                    if(!InsercionManager.insertLocalidades(conn)) {
                        logger.warning("BD: NO se han insertado las localidades.");
                        result = false;
                    }
                } else {
                    logger.warning("BD: NO se han insertado las provincias.");
                    result = false;
                }
                
            } else {
                logger.warning("BD: NO se han creado las tablas del usuario.");
            }

            if(result) {
                conn.commit(); //Confirma la transacción de la inserción de los datos.
            } else {
                logger.warning("BD: NO se han creado las tablas del usuario ni se han insertado los datos iniciales.");
                conn.rollback(); //Si hay algún error, se realiza un rollback en la inserción de los datos.
            }
            
        } catch (SQLException e) {
            conn.rollback(); //Si hay algún error, se realiza un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            throw e;
        } finally {
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            cn.desconectar(conn);
        }
        
        return result;
    }

    /**
     * Cambia la contraseña de la base de datos.
     * 
     * @param newPass La nueva contraseña.
     * @param usuarioActual El objeto Usuario actual.
     * @return true si se cambió la contraseña de la base de datos correctamente, false en caso contrario.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     */
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
            logger.config("BD: Cambio password de BD usuario."); 
            result = true;
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            if(st != null) {st.close();}
        }

        cn.desconectar(conn);
        return result;
    }


    /**
     * Cambia la contraseña de un usuario en la base de datos.
     * 
     * @param newPass La nueva contraseña.
     * @param oldPass La contraseña antigua.
     * @param idUsuario El ID del usuario.
     * @return true si se cambió la contraseña del usuario correctamente, false en caso contrario.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     */
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
            } else {
                logger.info("BD: Fallo cambio password de usuario."); 
            }
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            conn.rollback();
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            if(ps != null) {ps.close();}
        }

        cn.desconectar(conn);
        return result;
    }


    /**
     * Comprueba si existe un usuario con el nombre especificado en la base de datos.
     * En la comprobación, ignora mayúsculas y minúsculas.
     * 
     * @param nombre El nombre de usuario a comprobar.
     * @return true si existe un usuario con el nombre especificado, false en caso contrario.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     */
    public boolean comprobarNombreUsuario(String nombre) throws SQLException {
        ConexionBD cn = INSTANCE;
        conn = cn.conectar();
        boolean result = false;
        
        try {  
            st = conn.createStatement();
            res = st.executeQuery("SELECT nombre FROM usuario;"); //consulta sql a tabla USUARIO.

            while (res.next()) {
                if(res.getString(1).equalsIgnoreCase(nombre)) {
                    result = true;
                }
            }
        } catch (SQLException e) {
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(st != null) {st.close();}
            if(res != null) {res.close();} 
        }
        
        cn.desconectar(conn);
        return result;
    }


    /**
     * Comprueba si un usuario existe en la base de datos y si las credenciales coinciden.
     * 
     * @param usuario Un arreglo de String que contiene el nombre de usuario en la posición 0 y la contraseña en la posición 1.
     * @return true si el usuario existe y las credenciales coinciden, false en caso contrario.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     * @throws NoSuchAlgorithmException Si ocurre un error al cifrar la contraseña.
     */
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
                }
            }
        } catch (SQLException e) {
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(ps != null) {ps.close();}
            if(res != null) {res.close();} 
        }
        
        cn.desconectar(conn);
        return result;
    }

    /**
     * Inserta un nuevo usuario en la base de datos.
     * 
     * @param user El objeto Usuario a insertar.
     * @return true si se insertó el usuario correctamente, false en caso contrario.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     * @throws NoSuchAlgorithmException Si ocurre un error al cifrar la contraseña.
     */
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
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(ps != null) {ps.close();}  
        }
        
        cn.desconectar(conn);
        return result;
    }


    /**
     * Recupera un objeto Usuario de la base de datos utilizando el nombre de usuario y la contraseña.
     * 
     * @param usuario Un arreglo de String que contiene el nombre de usuario en la posición 0 y la contraseña en la posición 1.
     * @return El objeto Usuario recuperado de la base de datos, o null si no se encontró ningún usuario con las credenciales proporcionadas.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     * @throws NoSuchAlgorithmException Si ocurre un error al cifrar la contraseña.
     */
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
           
            res = ps.executeQuery(); //consulta sql a tabla USUARIO.

            while (res.next()) {
                //id-nombre-password-directorio
                usuarioRecuperado = new Usuario(res.getInt(1), res.getString(2), usuario[1], new File(res.getString(4)));
            }
        } catch (SQLException e) {
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(ps != null) {ps.close();}
            if(res != null) {res.close();} 
        }
        
        cn.desconectar(conn);
        return usuarioRecuperado;
    }


    /**
     * Borra un usuario de la base de datos utilizando su ID.
     * 
     * @param idUsuario El ID del usuario a borrar.
     * @return true si se borró el usuario correctamente, false en caso contrario.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     */
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
                logger.warning("BD: Fallo al borrar usuario. La SQL DELETE ha borrado mas de un usuario. Se ha hecho ROLLBACK.");
            }
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(st != null) {st.close();}
        }

        conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
        cn.desconectar(conn);
        return result;
    }


    /**
     * Obtiene los datos de un usuario desde la base de datos y los asigna al objeto Usuario proporcionado.
     * 
     * @param usuario El objeto Usuario al que se asignarán los datos obtenidos de la base de datos.
     * @return true si se obtuvieron los datos del usuario correctamente, false en caso contrario.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     */
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
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(ps != null) {ps.close();}
            if(res != null) {res.close();} 
        }
        
        cn.desconectar(conn);
        return result;
    }

    /**
     * Inserta los datos de un usuario en la base de datos.
     * 
     * @param usuario El objeto Usuario que contiene los datos a insertar.
     * @return true si se insertaron los datos correctamente, false en caso contrario.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     */
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
            result = true;
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(ps != null) {ps.close();}  
        }
        
        cn.desconectar(conn);
        return result;
    }


    /**
     * Modifica los datos de un usuario en la base de datos.
     * 
     * @param usuario El objeto Usuario que contiene los nuevos datos a modificar.
     * @return true si se modificaron los datos correctamente, false en caso contrario.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     */
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
            } else {
                logger.warning("Fallo. BD: NO se ha Cambio los datos personales de usuario");
            }
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            if(ps != null) {ps.close();}
        }

        cn.desconectar(conn);
        return result;
    }


    /**
     * Modifica el correo electrónico y la contraseña del correo electrónico de la aplicación de un usuario en la base de datos.
     * 
     * @param usuario El objeto Usuario que contiene los nuevos datos de correo electrónico y contraseña.
     * @return true si se modificaron los datos correctamente, false en caso contrario.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     */
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
            if(n > 0) {result = true;} 
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            if(ps != null) {ps.close();}
        }

        cn.desconectar(conn);
        return result;
    }


    /**
     * Borra una jornada y todas las clases asociadas de la base de datos.
     * 
     * @param jornada La jornada a borrar.
     * @return true si se borraron los datos correctamente, false en caso contrario.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     */
    public boolean borrarJornada(Jornada jornada) throws SQLException {
        ConexionBD cn = INSTANCE;
        boolean result = false;
        conn = cn.conectar();

        try {
            conn.setAutoCommit(false);

            String sqlForeignKey = "PRAGMA foreign_keys = ON;"; //No funciona con SqlCipher
            st = conn.createStatement();
            st.execute(sqlForeignKey);

            for (Clase c : jornada.getClases()) {
                ps = conn.prepareStatement("DELETE FROM CLASE_ALUMNO WHERE clase_id = ?;");
                ps.setInt(1, c.getId());
                ps.executeUpdate();

                ps = conn.prepareStatement("DELETE FROM CLASE WHERE id = ?;");
                ps.setInt(1, c.getId());
                ps.executeUpdate();
            }

            ps = conn.prepareStatement("DELETE FROM JORNADA WHERE fecha = ?;");
            ps.setString(1, jornada.getFecha().toString());
            ps.executeUpdate();
            
            conn.commit(); //Confirma la transacción de la inserción de los datos.
            result = true;
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally {
            if(ps != null) {ps.close();}
            if(st != null) {st.close();} 
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
        }
        
        cn.desconectar(conn);
        return result;
    }

    
    /**
     * Inserta una jornada y sus clases asociadas en la base de datos.
     * 
     * @param jornada La jornada a insertar.
     * @return true si se insertaron los datos correctamente, false en caso contrario.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     */
    public boolean insertarJornada(Jornada jornada) throws SQLException {
        ConexionBD cn = INSTANCE;
        boolean result = false;
        conn = cn.conectar();
        try {
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
            result = true;
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally {
            if(ps != null) {ps.close();}
            if(st != null) {st.close();} 
            if(res != null) {res.close();}
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
        }
        
        cn.desconectar(conn);
        return result;
    }


    /**
     * Inserta una jornada completa, incluyendo las clases y la asociacion entre alumnos y clases, en la base de datos.
     * 
     * @param jornadaCopia La jornada a insertar.
     * @return true si se insertaron los datos correctamente, false en caso contrario.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     */
    public boolean insertarJornadaCompleta(Jornada jornadaCopia) throws SQLException {
        ConexionBD cn = INSTANCE;
        boolean result = false;
        conn = cn.conectar();

        try {
            conn.setAutoCommit(false);

            //Insertamos la Jornada en la base de datos.
            ps = conn.prepareStatement("INSERT INTO JORNADA (fecha, comentario) VALUES (?,?);");
            ps.setString(1, jornadaCopia.getFecha().toString());
            ps.setString(2, jornadaCopia.getComentario());
           
            ps.executeUpdate();

            //Insertamos las clases de la jornada en la base de datos.
            ps = conn.prepareStatement("INSERT INTO CLASE (numero, tipo, hora, anotaciones, jornada) VALUES (?,?,?,?,?);");
            st = conn.createStatement();

            for(Clase clase : jornadaCopia.getClases()) {
                ps.setInt(1, clase.getNumero());
                ps.setString(2, clase.getTipo().toString());
                ps.setString(3, clase.getHoraClase().toString());
                ps.setString(4, clase.getAnotaciones());
                ps.setString(5, jornadaCopia.getFecha().toString());
                
                ps.executeUpdate();

                //Obtenemos el id que se le asignó a esta clase.
                res = st.executeQuery("SELECT last_insert_rowid()");
                clase.setId(res.getInt(1)); //Asignamos el id a la clase.
            }

            ps = conn.prepareStatement("INSERT INTO CLASE_ALUMNO (clase_id, alumno_id) VALUES (?,?);");

            for(Clase clase : jornadaCopia.getClases()) {
                if(clase.getListaAlumnos().size() > 0) {
                    ps.setInt(1, clase.getId());
                    for (Alumno alumno : clase.getListaAlumnos()) {
                        ps.setInt(2, alumno.getId());
                        ps.executeUpdate();
                    }
                }
            }

            conn.commit(); //Confirma la transacción de la inserción de los datos.
            System.out.println("BD: Insercion en base de datos"); //Esto es temporal para pruebas.
            result = true;
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally {
            if(ps != null) {ps.close();}
            if(st != null) {st.close();} 
            if(res != null) {res.close();}
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
        }
        
        cn.desconectar(conn);
        return result;
    }


    /**
     * Inserta una lista de jornadas completas, incluyendo las clases y la asociacion entre alumnos y clases, en la base de datos.
     * 
     * @param listaJornadas El array de jornadas a insertar.
     * @return true si se insertaron los datos correctamente, false en caso contrario.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     */
    public boolean insertarListaJornadas(Jornada[] listaJornadas) throws SQLException {
        ConexionBD cn = INSTANCE;
        boolean result = false;
        conn = cn.conectar();

        try {
            conn.setAutoCommit(false);

            for (Jornada jornada : listaJornadas) {
                if (jornada != null) {
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

                    ps = conn.prepareStatement("INSERT INTO CLASE_ALUMNO (clase_id, alumno_id) VALUES (?,?);");

                    for(Clase clase : jornada.getClases()) {
                        if(clase.getListaAlumnos().size() > 0) {
                            ps.setInt(1, clase.getId());
                            for (Alumno alumno : clase.getListaAlumnos()) {
                                ps.setInt(2, alumno.getId());
                                ps.executeUpdate();
                            }
                        }
                    }
                }                
            }

            conn.commit(); //Confirma la transacción de la inserción de los datos.
            result = true;
        } catch (SQLException e) {
            //aqui poner la insercion en el .log
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally {
            if(ps != null) {ps.close();}
            if(st != null) {st.close();} 
            if(res != null) {res.close();}
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
        }
        
        cn.desconectar(conn);
        return result;
    }

    
    /**
     * Inserta un alumno en la base de datos, incluyendo su dirección.
     * 
     * @param alumno El alumno a insertar.
     * @return true si se insertaron los datos correctamente, false en caso contrario.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     */
    public boolean insertarAlumno(Alumno alumno) throws SQLException {
        ConexionBD cn = INSTANCE;
        boolean result = false;
        conn = cn.conectar();

        try {
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
            alumno.direccionProperty().get().setId(idDirInsertada);

            //Insertamos el Alumno en la base de datos.
            ps = conn.prepareStatement("INSERT INTO ALUMNO (nombre, apellido1, apellido2, genero, direccion_id, fecha_nacimiento, telefono, email, estado, asistencia_semanal, forma_pago) VALUES (?,?,?,?,?,?,?,?,?,?,?);");
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
            ps.setString(11, alumno.getFormaPago().toString());
            
            ps.executeUpdate();

            //Obtenemos el id que se le asignó a ese alumno.
            st = conn.createStatement();
            res = st.executeQuery("SELECT last_insert_rowid()");
            int idAlumInsertado = res.getInt(1); //Guardo el valor de id obtenido en la consulta en la variable.
            alumno.setId(idAlumInsertado); //Pongo el id al objeto Alumno.
            
            conn.commit(); //Confirma la transacción de la inserción de los datos.
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            result = true;
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(ps != null) {ps.close();} 
            if(res != null) {res.close();}
            if(st != null) {st.close();}
        }
        
        cn.desconectar(conn);
        return result;
    }


    /**
     * Inserta un alumno en una clase específica en la base de datos.
     * 
     * @param alumno El alumno a insertar en la clase.
     * @param clase La clase en la que se insertará el alumno.
     * @return true si se insertaron los datos correctamente, false en caso contrario.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     */
    public boolean insertarAlumnoEnClase(Alumno alumno, Clase clase) throws SQLException {
        ConexionBD cn = INSTANCE;
        boolean result = false;
        conn = cn.conectar();
        
        try {
            conn.setAutoCommit(false);

            //Insertamos el Alumno en la base de datos.
            ps = conn.prepareStatement("INSERT INTO CLASE_ALUMNO (clase_id, alumno_id) VALUES (?,?);");
            ps.setInt(1, clase.getId());
            ps.setInt(2, alumno.getId());
            
            ps.executeUpdate();
            
            conn.commit(); //Confirma la transacción de la inserción de los datos.
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            result = true;
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(ps != null) {ps.close();} 
        }
        
        cn.desconectar(conn);
        return result;
    }


    /**
     * Elimina un alumno de una clase específica en la base de datos.
     * 
     * @param alumno El alumno a eliminar de la clase.
     * @param clase La clase de la cual se eliminará el alumno.
     * @return true si se borraron los datos correctamente, false en caso contrario.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     */
    public boolean borrarAlumnoEnClase(Alumno alumno, Clase clase) throws SQLException {
        ConexionBD cn = INSTANCE;
        boolean result = false;
        conn = cn.conectar();
        try {
            conn.setAutoCommit(false);

            //Insertamos el Alumno en la base de datos.
            ps = conn.prepareStatement("DELETE FROM CLASE_ALUMNO WHERE clase_id = ? AND alumno_id = ?;");
            ps.setInt(1, clase.getId());
            ps.setInt(2, alumno.getId());
            
            ps.executeUpdate();
            
            conn.commit(); //Confirma la transacción de la inserción de los datos.
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            result = true;
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(ps != null) {ps.close();}
        }
        
        cn.desconectar(conn);
        return result;
    }


    /**
     * Actualiza la lista de alumnos de una clase en la base de datos.
     * 
     * @param nuevaListaAlumnos La nueva lista de alumnos de la clase.
     * @param idClase El identificador de la clase a actualizar.
     * @return true si se actualizaron los datos correctamente, false en caso contrario.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     */
    public boolean actualizarAlumnosEnClase(ArrayList<Alumno> nuevaListaAlumnos, int idClase) throws SQLException {
        ConexionBD cn = INSTANCE;
        boolean result = false;
        ArrayList<Alumno> listaAlumnos;
        conn = cn.conectar();

        try {    
            conn.setAutoCommit(false);

            st = conn.createStatement();
            res = st.executeQuery("SELECT id FROM ALUMNO A JOIN CLASE_ALUMNO C ON(A.id = C.alumno_id) WHERE C.clase_id = " + idClase + " ;"); //consulta sql a tabla ALUMNO.

            listaAlumnos = new ArrayList<Alumno>();
            while (res.next()) {
                Alumno a = new Alumno();
                a.setId(res.getInt(1));
                listaAlumnos.add(a);
            }

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
            result = true;
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(ps != null) {ps.close();} 
            if(res != null) {res.close();}
            if(st != null) {st.close();}
        }
        
        cn.desconectar(conn);
        return result;
    }


    /**
     * Actualiza los datos de una clase en la base de datos.
     * 
     * @param clase La clase con los datos actualizados.
     * @return true si se actualizaron los datos correctamente, false en caso contrario.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     */
    public boolean actualizarClase(Clase clase) throws SQLException{
        ConexionBD cn = INSTANCE;
        boolean result = false;
        conn = cn.conectar();

        try {    
            conn.setAutoCommit(false);

            ps = conn.prepareStatement("UPDATE CLASE SET tipo = ?, hora = ?, anotaciones = ? WHERE id = ?;");
            ps.setString(1, clase.getTipo().toString());
            ps.setString(2, clase.getHoraClase().toString());
            ps.setString(3, clase.getAnotaciones());
            ps.setInt(4, clase.getId());
            ps.executeUpdate();
            
            conn.commit(); //Confirma la transacción de la inserción de los datos.
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            result = true;
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(ps != null) {ps.close();} 
        }
        
        cn.desconectar(conn);
        return result;
    }


    /**
     * Actualiza el comentario de una jornada en la base de datos.
     * 
     * @param jornada La jornada con el comentario actualizado.
     * @return true si se actualizó el comentario correctamente, false en caso contrario.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     */
    public boolean actualizarComentarioJornada(Jornada jornada) throws SQLException {
        ConexionBD cn = INSTANCE;
        boolean result = false;
        conn = cn.conectar();

        try {
            conn.setAutoCommit(false);

            ps = conn.prepareStatement("UPDATE JORNADA SET comentario = ? WHERE fecha = ?;");
            ps.setString(1, jornada.getComentario());
            ps.setString(2, jornada.getFecha().toString());
            ps.executeUpdate();
            
            conn.commit(); //Confirma la transacción de la inserción de los datos.
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            result = true;
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(ps != null) {ps.close();} 
        }
        
        cn.desconectar(conn);
        return result;
    }

    /**
     * Obtiene la lista de alumnos general desde la base de datos.
     * 
     * @return La lista de alumnos obtenida desde la base de datos.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     */
    public ArrayList<Alumno> getListadoAlumnos() throws SQLException {
        ConexionBD cn = INSTANCE;
        ArrayList<Alumno> listaAlumnos = null;
        ArrayList<Direccion> listaDirecciones = null;
        conn = cn.conectar();

        try {
            st = conn.createStatement();
            res = st.executeQuery("SELECT D.id, D.calle, D.numero, L.nombre, P.nombre, D.codigo_postal " 
                                + "FROM DIRECCION D JOIN LOCALIDAD L ON(D.localidad_id = L.id) "
                                + "JOIN PROVINCIA P ON(L.provincia_id = P.id);"); //consulta sql a tablas DIRECCION, LOCALIDAD Y PROVINCIA.

            listaDirecciones = new ArrayList<Direccion>();
            while (res.next()) {
                listaDirecciones.add(new Direccion(res.getInt(1), res.getString(2), res.getInt(3), 
                    res.getString(4), res.getString(5), res.getInt(6)));
            }
            
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
                
                //Genera un Enumerado de tipo Genero a partir del valor rescatado en el campo genero del registro que se esta recorriendo.
                Genero genero;
                try {
                    genero = Genero.valueOf(res.getString(5));
                } catch (IllegalArgumentException e) {
                    logger.severe("Excepción SQL: " + e.toString());
                    genero = null;
                }

                //Genera un Enumerado de tipo EstadoAlumno a partir del valor rescatado en el campo genero del registro que se esta recorriendo.
                EstadoAlumno estado;
                try {
                    estado = EstadoAlumno.valueOf(res.getString(10));
                } catch (IllegalArgumentException e) {
                    logger.severe("Excepción SQL: " + e.toString());
                    estado = null;
                }

                //Genera un Enumerado de tipo FormaPago a partir del valor rescatado en el campo forma_pago del registro que se esta recorriendo.
                FormaPago formaPago; //res.getString(6)
                try {
                    formaPago = FormaPago.valueOf(res.getString(12));
                } catch (IllegalArgumentException e) {
                    logger.severe("Excepción SQL: " + e.toString());
                    formaPago = null;
                }

                //Creo el alumno con los datos del registro que se esta recorriendo y lo añado a la lista de Alumnos.
                listaAlumnos.add(new Alumno(res.getInt(1), res.getString(2), res.getString(3), res.getString(4), 
                    genero, direccion, LocalDate.parse(res.getString(7)), res.getInt(8),
                    res.getString(9), estado, res.getInt(11), formaPago));
            }
            
            logger.info("BD: Obtencion de lista Alumnos general. Total: " + listaAlumnos.size());
        } catch (SQLException e) {
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(res != null) {res.close();}
            if(st != null) {st.close();}
        }
        
        cn.desconectar(conn);
        return listaAlumnos;
    }

    /**
     * Obtiene una lista de alumnos asociados a una clase específica desde la base de datos.
     * 
     * @param idClase El ID de la clase de la cual se obtendrá el listado de alumnos.
     * @return La lista de alumnos asociados a la clase especificada.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     */
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
                    logger.severe("Excepción SQL: " + e.toString());
                    genero = null;
                }

                //Genero un Enumerado de tipo EstadoAlumno a partir del valor rescatado en el campo genero del registro que se esta recorriendo.
                EstadoAlumno estado;
                try {
                    estado = EstadoAlumno.valueOf(res.getString(10));
                } catch (IllegalArgumentException e) {
                    logger.severe("Excepción SQL: " + e.toString());
                    estado = null;
                }

                //Genero un Enumerado de tipo FormaPago a partir del valor rescatado en el campo forma_pago del registro que se esta recorriendo.
                FormaPago formaPago; //res.getString(6)
                try {
                    formaPago = FormaPago.valueOf(res.getString(12));
                } catch (IllegalArgumentException e) {
                    logger.severe("Excepción SQL: " + e.toString());
                    formaPago = null;
                }

                //Creo el alumno con los datos del registro que se esta recorriendo y lo añado a la lista de Alumnos.
                listaAlumnos.add(new Alumno(res.getInt(1), res.getString(2), res.getString(3), res.getString(4), 
                    genero, direccion, LocalDate.parse(res.getString(7)), res.getInt(8),
                    res.getString(9), estado, res.getInt(11), formaPago));
            }
        } catch (SQLException e) {
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(st != null) {st.close();}
            if(res != null) {res.close();} 
        }
        
        cn.desconectar(conn);
        return listaAlumnos;
    }


    /**
     * Obtiene la lista de direcciones general desde la base de datos.
     * 
     * @return La lista de direcciones obtenidas.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     */
    public ArrayList<Direccion> getListadoDirecciones() throws SQLException {
        ConexionBD cn = INSTANCE;
        ArrayList<Direccion> listaDirecciones = null;
        conn = cn.conectar();

        try {
            st = conn.createStatement();
            res = st.executeQuery("SELECT D.id, D.calle, D.numero, L.nombre, P.nombre, D.codigo_postal " 
                                + "FROM DIRECCION D JOIN LOCALIDAD L ON(D.localidad_id = L.id) "
                                + "JOIN PROVINCIA P ON(L.provincia_id = P.id);"); //consulta sql a tablas DIRECCION, LOCALIDAD Y PROVINCIA.

            listaDirecciones = new ArrayList<Direccion>();
            while (res.next()) {
                listaDirecciones.add(new Direccion(res.getInt(1), res.getString(2), res.getInt(3), 
                    res.getString(4), res.getString(5), res.getInt(6)));
            }
        } catch (SQLException e) {
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(st != null) {st.close();}
            if(res != null) {res.close();} 
        }
        
        cn.desconectar(conn);
        return listaDirecciones;
    }


    /**
     * Obtiene la lista de mensualidades general desde la base de datos.
     * @return La lista de mensualidades obtenidas.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     */
    public ArrayList<Mensualidad> getListadoMensualidades() throws SQLException {
        ConexionBD cn = INSTANCE;
        ArrayList<Mensualidad> listaMensualidades = null;
        conn = cn.conectar();
        
        try {

            st = conn.createStatement();
            res = st.executeQuery("SELECT id, alumno_id, mes, anio, fecha_pago, forma_pago, estado_pago, cuota, asistencia_semanal, anotacion " 
                                + "FROM MENSUALIDAD"); //consulta sql a tabla MENSUALIDAD.

            listaMensualidades = new ArrayList<Mensualidad>();
            while (res.next()) {
                YearMonth fecha = YearMonth.of(Integer.parseInt(res.getString(4)), Month.valueOf(Fechas.traducirMesAIngles(res.getString(3))));

                //Genero un Enumerado de tipo FormaPago a partir del valor rescatado en el campo forma_pago del registro que se esta recorriendo.
                FormaPago formaPago; //res.getString(6)
                try {
                    formaPago = FormaPago.valueOf(res.getString(6));
                } catch (IllegalArgumentException e) {
                    logger.severe("Excepción SQL: " + e.toString());
                    formaPago = null;
                }

                //Genero un Enumerado de tipo EstadoPago a partir del valor rescatado en el campo estado_pago del registro que se esta recorriendo.
                EstadoPago estadoPago;
                try {
                    estadoPago = EstadoPago.valueOf(res.getString(7));
                } catch (IllegalArgumentException e) {
                    logger.severe("Excepción SQL: " + e.toString());
                    estadoPago = null;
                }

                LocalDate fechaPago = null;
                if (res.getString(5) != null) {
                    fechaPago = LocalDate.parse(res.getString(5));
                }

                listaMensualidades.add(new Mensualidad(res.getInt(1), res.getInt(2), fecha, 
                    fechaPago , formaPago, estadoPago,
                    res.getInt(9), res.getDouble(8), res.getString(10)));
            }

            logger.info("BD: Obtencion de lista Mensualidades general. Total: " + listaMensualidades.size());
        } catch (SQLException e) {
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(st != null) {st.close();}
            if(res != null) {res.close();} 
        }
        
        cn.desconectar(conn);
        return listaMensualidades;
    }


    /**
     * Recupera de la base de datos las jornadas que coinciden con las fechas pasadas en el Array de LocalDate.
     * 
     * @param fechas Array de LocalDate con las fechas de las jornadas que se quieren recuperar. 
     * @return Un array de Jornada con las Jornadas recuperadas de la base de datos. Si una jornada no esta creada, devuelve una jornada a null.
     * @throws SQLException
     */
    public Jornada[] getJornadasCompletas(LocalDate[] fechas) throws SQLException {
        ConexionBD cn = INSTANCE;
        Jornada[] semana = new Jornada[fechas.length];
        conn = cn.conectar();

        try { 
            for (int i = 0; i < fechas.length; i++) {
                Jornada jornada = null;
                Clase[] listaClases = new Clase[8];
                ArrayList<Alumno> listaAlumnos = null;
                ArrayList<Direccion> listaDirecciones = null;
            
                //Obtengo la jornada ------------------- 
                st = conn.createStatement();
                res = st.executeQuery("SELECT * FROM JORNADA WHERE fecha = '" + fechas[i].toString() + "';"); //consulta sql a tabla JORNADA.

                while (res.next()) {
                    jornada = new Jornada(LocalDate.parse(res.getString(1)), res.getString(2));
                }
                
                if(jornada != null) {
                    boolean resultadoOk = false;
                    int posicionArrayClases = 0;

                    //Obtengo la lista de clases ------------------
                    res = st.executeQuery("SELECT id, numero, tipo, hora, anotaciones FROM CLASE WHERE jornada = '" + jornada.getFecha().toString() + "' ORDER BY numero;"); //consulta sql a tabla CLASE.

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

                        for (int j = 0; j < listaClases.length; j++) {
                            res = st.executeQuery("SELECT * FROM ALUMNO A JOIN CLASE_ALUMNO C ON(A.id = C.alumno_id) WHERE C.clase_id = " + listaClases[j].getId() + " ;"); //consulta sql a tabla ALUMNO.

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
                                    logger.severe("Excepción SQL: " + e.toString());
                                    genero = null;
                                }

                                //Genero un Enumerado de tipo EstadoAlumno a partir del valor rescatado en el campo genero del registro que se esta recorriendo.
                                EstadoAlumno estado;
                                try {
                                    estado = EstadoAlumno.valueOf(res.getString(10));
                                } catch (IllegalArgumentException e) {
                                    logger.severe("Excepción SQL: " + e.toString());
                                    estado = null;
                                }

                                //Genero un Enumerado de tipo FormaPago a partir del valor rescatado en el campo forma_pago del registro que se esta recorriendo.
                                FormaPago formaPago; //res.getString(6)
                                try {
                                    formaPago = FormaPago.valueOf(res.getString(12));
                                } catch (IllegalArgumentException e) {
                                    logger.severe("Excepción SQL: " + e.toString());
                                    formaPago = null;
                                }

                                //Creo el alumno con los datos del registro que se esta recorriendo y lo añado a la lista de Alumnos.
                                listaAlumnos.add(new Alumno(res.getInt(1), res.getString(2), res.getString(3), res.getString(4), 
                                    genero, direccion, LocalDate.parse(res.getString(7)), res.getInt(8), res.getString(9), estado, res.getInt(11), formaPago));
                            }

                            listaClases[j].setListaAlumnos(listaAlumnos); //añado la lista de alumnos a su Clase.
                        }
                    }
                    jornada.setClases(listaClases); //añado la lista de clases a la Jornada
                }

                semana[i] = jornada; //añado la jornada a la semana.(Array de Jornada).
            } 
        } catch (SQLException e) {
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
            semana = null;
        } finally { 
            if(st != null) {st.close();}
            if(res != null) {res.close();} 
        }

        cn.desconectar(conn);
        return semana;
    }


    /**
     * Obtiene una jornada completa desde la base de datos con todas sus clases y alumnos.
     * 
     * @param fecha La fecha de la jornada en formato String.
     * @return La jornada completa con todas sus clases y alumnos.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     */
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
                        logger.severe("Excepción SQL: " + e.toString());
                        tipo = null;
                    }

                    //Crea un Enumerado de tipo HoraClase a partir del valor rescatado en el campo tipo del registro que se esta recorriendo.
                    HoraClase hora;
                    try {
                        String[] partes = res.getString(4).split(":"); //Crea un Array con la hora y los minutos
                        hora = HoraClase.getHoraClase(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]));
                    } catch (IllegalArgumentException e) {
                        logger.severe("Excepción SQL: " + e.toString());
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
                                logger.severe("Excepción SQL: " + e.toString());
                                genero = null;
                            }

                            //Genero un Enumerado de tipo EstadoAlumno a partir del valor rescatado en el campo genero del registro que se esta recorriendo.
                            EstadoAlumno estado;
                            try {
                                estado = EstadoAlumno.valueOf(res.getString(10));
                            } catch (IllegalArgumentException e) {
                                logger.severe("Excepción SQL: " + e.toString());
                                estado = null;
                            }

                            //Genero un Enumerado de tipo FormaPago a partir del valor rescatado en el campo forma_pago del registro que se esta recorriendo.
                            FormaPago formaPago; //res.getString(6)
                            try {
                                formaPago = FormaPago.valueOf(res.getString(12));
                            } catch (IllegalArgumentException e) {
                                logger.severe("Excepción SQL: " + e.toString());
                                formaPago = null;
                            }

                            //Creo el alumno con los datos del registro que se esta recorriendo y lo añado a la lista de Alumnos.
                            listaAlumnos.add(new Alumno(res.getInt(1), res.getString(2), res.getString(3), res.getString(4), 
                                genero, direccion, LocalDate.parse(res.getString(7)), res.getInt(8), res.getString(9), estado, res.getInt(11), formaPago));
                        }

                        listaClases[i].setListaAlumnos(listaAlumnos); //añado la lista de alumnos a su Clase.
                    }
                }
                jornada.setClases(listaClases); //añado la lista de clases a la Jornada
            } 
        } catch (SQLException e) {
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(st != null) {st.close();}
            if(res != null) {res.close();} 
        }

        cn.desconectar(conn);
        return jornada;
    }


    /**
     * Obtiene una jornada desde la base de datos con la fecha especificada.
     * 
     * @param fecha La fecha de la jornada en formato String.
     * @return La jornada correspondiente a la fecha especificada.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     */
    public Jornada getJornada(String fecha) throws SQLException {
        ConexionBD cn = INSTANCE;
        Jornada jornada = null;
        conn = cn.conectar();
        
        try {  
            st = conn.createStatement();
            res = st.executeQuery("SELECT * FROM JORNADA WHERE fecha = '" + fecha + "';"); //consulta sql a tabla JORNADA.

            while (res.next()) {
                jornada = new Jornada(LocalDate.parse(res.getString(1)), res.getString(2));
            }
        } catch (SQLException e) {
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(st != null) {st.close();}
            if(res != null) {res.close();} 
        }
        
        cn.desconectar(conn);

        //Añado a la Jornada el Array de Clase.
        if(jornada != null) {
           jornada.setClases(getArrayClases(jornada));
        }

        return jornada;
    }


    /**
     * Obtiene un array de clases para una jornada especificada.
     * 
     * @param jornada La jornada para la cual se obtiene el array de clases.
     * @return Un array de clases para la jornada especificada.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     */
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
                    logger.severe("Excepción SQL: " + e.toString());
                    tipo = null;
                }

                //Crea un Enumerado de tipo HoraClase a partir del valor rescatado en el campo tipo del registro que se esta recorriendo.
                HoraClase hora;
                try {
                    String[] partes = res.getString(4).split(":"); //Crea un Array con la hora y los minutos
                    hora = HoraClase.getHoraClase(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]));
                } catch (IllegalArgumentException e) {
                    logger.severe("Excepción SQL: " + e.toString());
                    hora = null;
                }

                //Añade una nueva Clase al Array ListaClases en la posicion posicionArray.
                listaClases[posicionArray] = new Clase(res.getInt(1), res.getInt(2), tipo, hora, res.getString(5));
                posicionArray++;
            }

            //Si no hay registros, ponemos la listaClases a null.
            if(!resultadoOk) {listaClases = null;}
        } catch (SQLException e) {
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(st != null) {st.close();}
            if(res != null) {res.close();} 
        }
        
        cn.desconectar(conn);

        if(listaClases != null) {
            setAlumnosClases(listaClases);
        }

        return listaClases;
    }


    /**
     * Obtiene una lista de objetos AlumnoReport que contienen información de los alumnos y sus clases asociadas para un año específico.
     *
     * @param listaAlumnos La lista de alumnos para generar de los que se van a generar las listas de clases.
     * @param year         El año para filtrar las clases asociadas a los alumnos.
     * @return Una lista de objetos AlumnoReport que contiene información de los alumnos y sus clases asociadas.
     * @throws SQLException Si ocurre algún error durante la consulta a la base de datos.
     */
    public ArrayList<AlumnoReport> getListaAlumnoReport(ArrayList<Alumno> listaAlumnos, int year) throws SQLException {
        ConexionBD cn = INSTANCE;
        ArrayList<AlumnoReport> listaAlumnoReport = null;
        conn = cn.conectar();

        //--------------------------------------------------------------
        //strftime('%W', fecha) -> devuelve el numero de semana.
        //strftime('%Y', fecha) -> devuelve el año.
        //strftime('%m', fecha) -> devuelve el numero de mes.
        //--------------------------------------------------------------

        //Consulta a base de datos.
        String sql = "SELECT C.id, C.numero, C.tipo, C.hora, C.anotaciones, C.jornada FROM CLASE_ALUMNO CS JOIN CLASE C ON(CS.clase_id = C.id)"
                    + "WHERE CS.alumno_id = ? AND strftime('%Y', C.jornada) = ?;";
        
        try {
            listaAlumnoReport = new ArrayList<AlumnoReport>();
            ps = conn.prepareStatement(sql);

            for (Alumno alumno : listaAlumnos) {
                ArrayList<ClaseReport> listaClases = new ArrayList<ClaseReport>();
                ps.setInt(1, alumno.getId());
                ps.setString(2, Integer.toString(year));
                res = ps.executeQuery();

                //Recorre las filas devueltas por la consulta y crea por cada fila un Objeto de tipo Clase añadiendolo al Array listaClases.
                while (res.next()) {
                    //Crea un Enumerado de tipo TipoClase a partir del valor rescatado en el campo tipo del registro que se esta recorriendo.
                    TipoClase tipo;
                    try {
                        tipo = TipoClase.valueOf(res.getString(3));
                    } catch (IllegalArgumentException e) {
                        logger.severe("Excepción: " + e.toString());
                        tipo = null;
                    }

                    //Crea un Enumerado de tipo HoraClase a partir del valor rescatado en el campo tipo del registro que se esta recorriendo.
                    HoraClase hora;
                    try {
                        String[] partes = res.getString(4).split(":"); //Crea un Array con la hora y los minutos
                        hora = HoraClase.getHoraClase(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]));
                    } catch (IllegalArgumentException e) {
                        logger.severe("Excepción: " + e.toString());
                        hora = null;
                    }

                    //Añade una nueva Clase con la informacion de la fila recorrida a listaClases.
                    Clase clase = new Clase(res.getInt(1), res.getInt(2), tipo, hora, res.getString(5));
                    ClaseReport claseReport = new ClaseReport(clase, LocalDate.parse(res.getString(6)));
                    listaClases.add(claseReport);
                    
                }
                listaAlumnoReport.add(new AlumnoReport(alumno, listaClases));
            }
        } catch (SQLException e) {
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(ps != null) {ps.close();}
            if(res != null) {res.close();} 
        }
        
        cn.desconectar(conn);
        return listaAlumnoReport;
    }


    /**
     * Obtiene una lista de objetos AlumnoReport que contienen información de los alumnos y sus clases asociadas para un año y un mes específico.
     *
     * @param listaAlumnos La lista de alumnos para generar de los que se van a generar las listas de clases.
     * @param fecha         El año y mes para filtrar las clases asociadas a los alumnos.
     * @return Una lista de objetos AlumnoReport que contiene información de los alumnos y sus clases asociadas.
     * @throws SQLException Si ocurre algún error durante la consulta a la base de datos.
     */
    public ArrayList<AlumnoReport> getListaAlumnoReport(ArrayList<Alumno> listaAlumnos, YearMonth fecha) throws SQLException {
        ConexionBD cn = INSTANCE;
        ArrayList<AlumnoReport> listaAlumnoReport = null;
        conn = cn.conectar();

        //--------------------------------------------------------------
        //strftime('%W', fecha) -> devuelve el numero de semana.
        //strftime('%Y', fecha) -> devuelve el año.
        //strftime('%m', fecha) -> devuelve el numero de mes.
        //--------------------------------------------------------------

        //Consulta a base de datos.
        String sql = "SELECT C.id, C.numero, C.tipo, C.hora, C.anotaciones, C.jornada FROM CLASE_ALUMNO CS JOIN CLASE C ON(CS.clase_id = C.id)"
                    + "WHERE CS.alumno_id = ? AND strftime('%Y', C.jornada) = ? AND strftime('%m', C.jornada) = ?;";
        
        try {
            listaAlumnoReport = new ArrayList<AlumnoReport>();
            ps = conn.prepareStatement(sql);

            for (Alumno alumno : listaAlumnos) {
                ArrayList<ClaseReport> listaClases = new ArrayList<ClaseReport>();
                ps.setInt(1, alumno.getId());
                ps.setString(2, Integer.toString(fecha.getYear()));
                ps.setString(3, (fecha.getMonthValue() < 10)? "0" + fecha.getMonthValue() : Integer.toString(fecha.getMonthValue()));
                res = ps.executeQuery();

                //Recorre las filas devueltas por la consulta y crea por cada fila un Objeto de tipo Clase añadiendolo al Array listaClases.
                while (res.next()) {
                    //Crea un Enumerado de tipo TipoClase a partir del valor rescatado en el campo tipo del registro que se esta recorriendo.
                    TipoClase tipo;
                    try {
                        tipo = TipoClase.valueOf(res.getString(3));
                    } catch (IllegalArgumentException e) {
                        logger.severe("Excepción: " + e.toString());
                        tipo = null;
                    }

                    //Crea un Enumerado de tipo HoraClase a partir del valor rescatado en el campo tipo del registro que se esta recorriendo.
                    HoraClase hora;
                    try {
                        String[] partes = res.getString(4).split(":"); //Crea un Array con la hora y los minutos
                        hora = HoraClase.getHoraClase(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]));
                    } catch (IllegalArgumentException e) {
                        logger.severe("Excepción: " + e.toString());
                        hora = null;
                    }

                    //Añade una nueva Clase al Array ListaClases en la posicion posicionArray.
                    Clase clase = new Clase(res.getInt(1), res.getInt(2), tipo, hora, res.getString(5));
                    ClaseReport claseReport = new ClaseReport(clase, LocalDate.parse(res.getString(6)));
                    listaClases.add(claseReport);
                }
                listaAlumnoReport.add(new AlumnoReport(alumno, listaClases));
            }
        } catch (SQLException e) {
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(ps != null) {ps.close();}
            if(res != null) {res.close();} 
        }
        
        cn.desconectar(conn);
        return listaAlumnoReport;
    }

    
    /**
     * Establece la lista de alumnos para cada clase en el array de clases especificado.
     * 
     * @param listaClases El array de clases para el cual se establecerá la lista de alumnos.
     * @throws SQLException Si ocurre algún error al ejecutar las consultas SQL.
     */
    private void setAlumnosClases(Clase[] listaClases) throws SQLException {
        ConexionBD cn = INSTANCE;
        ArrayList<Alumno> listaAlumnos = null;
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
                        logger.severe("Excepción SQL: " + e.toString());
                        genero = null;
                    }

                    //Genero un Enumerado de tipo EstadoAlumno a partir del valor rescatado en el campo genero del registro que se esta recorriendo.
                    EstadoAlumno estado;
                    try {
                        estado = EstadoAlumno.valueOf(res.getString(10));
                    } catch (IllegalArgumentException e) {
                        logger.severe("Excepción SQL: " + e.toString());
                        estado = null;
                    }

                    //Genero un Enumerado de tipo FormaPago a partir del valor rescatado en el campo forma_pago del registro que se esta recorriendo.
                    FormaPago formaPago; //res.getString(6)
                    try {
                        formaPago = FormaPago.valueOf(res.getString(12));
                    } catch (IllegalArgumentException e) {
                        logger.severe("Excepción SQL: " + e.toString());
                        formaPago = null;
                    }

                    //Creo el alumno con los datos del registro que se esta recorriendo y lo añado a la lista de Alumnos.
                    listaAlumnos.add(new Alumno(res.getInt(1), res.getString(2), res.getString(3), res.getString(4), 
                        genero, new Direccion(), LocalDate.parse(res.getString(7)), res.getInt(8),
                        res.getString(9), estado, res.getInt(11), formaPago));
                }

                listaClases[i].setListaAlumnos(listaAlumnos);
            }
            
        } catch (SQLException e) {
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(st != null) {st.close();}
            if(res != null) {res.close();} 
        }
        
        cn.desconectar(conn);
    }


    /**
     * Comprueba si existe una jornada en la base de datos para la fecha especificada.
     * 
     * @param fecha La fecha para la cual se verificará la existencia de la jornada.
     * @return true si existe una jornada para la fecha especificada, false en caso contrario.
     * @throws SQLException Si ocurre algún error al ejecutar la consulta SQL.
     */
    public boolean comprobarJornada(LocalDate fecha) throws SQLException{
        ConexionBD cn = INSTANCE;
        boolean result = false;
        conn = cn.conectar();
        
        try {
            st = conn.createStatement();
            res = st.executeQuery("SELECT * FROM JORNADA WHERE fecha = '" + fecha.toString() + "';");

            while (res.next()) {
                result = true;
            }

        } catch (SQLException e) {
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(st != null) {st.close();}
            if(res != null) {res.close();} 
        }
        
        cn.desconectar(conn);
        return result;
    }


    /**
     * Comprueba si existen jornadas en la base de datos para las fechas especificadas.
     * 
     * @param fechas Un array de LocalDate que representa las fechas para las cuales se verificará la existencia de jornadas.
     * @return true si existe al menos una jornada para alguna de las fechas especificadas, false en caso contrario.
     * @throws SQLException Si ocurre algún error al ejecutar la consulta SQL.
     */
    public boolean comprobarJornadas(LocalDate[] fechas) throws SQLException{
        ConexionBD cn = INSTANCE;
        boolean result = false;
        conn = cn.conectar();

        try {
            ps = conn.prepareStatement("SELECT * FROM JORNADA WHERE fecha = ?;");
            for (int i = 0; i < fechas.length; i++) {
                ps.setString(1, fechas[i].toString());
                res = ps.executeQuery();

                while (res.next()) {
                    result = true;
                }
            }
        } catch (SQLException e) {
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(ps != null) {ps.close();}
            if(res != null) {res.close();} 
        }
        
        cn.desconectar(conn);
        return result;
    }


    /**
     * Obtiene los precios de las clases desde la base de datos.
     * 
     * @return Un objeto Map<Integer, Double> que contiene los precios de las clases, donde la clave es el número de clases y el valor es el precio.
     * @throws SQLException Si ocurre algún error al ejecutar la consulta SQL.
     */
    public Map<Integer, Double> getPrecioClases() throws SQLException {
        ConexionBD cn = INSTANCE;
        Map<Integer, Double> precios_clases = null;
        conn = cn.conectar();

        try {
            st = conn.createStatement();
            res = st.executeQuery("SELECT numero_clases, precio FROM PRECIO_CLASE");

            precios_clases = new HashMap<Integer, Double>();
            while (res.next()) {
                precios_clases.put(res.getInt(1), res.getDouble(2));
            }
        } catch (SQLException e) {
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(st != null) {st.close();}
            if(res != null) {res.close();} 
        }
        
        cn.desconectar(conn);
        return precios_clases;
    }


    /**
     * Obtiene la lista de provincias desde la base de datos.
     * 
     * @return Un objeto ArrayList<String> que contiene los nombres de las provincias.
     * @throws SQLException Si ocurre algún error al ejecutar la consulta SQL.
     */
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
        } catch (SQLException e) {
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(st != null) {st.close();}
            if(res != null) {res.close();} 
        }
        
        cn.desconectar(conn);
        return listaProvincias;
    }


    /**
     * Obtiene la lista de localidades de una provincia desde la base de datos.
     * 
     * @param provincia Provincia de donde se obtine las localidades.
     * @return Un objeto ArrayList<String> que contiene los nombres de las localidades.
     * @throws SQLException Si ocurre algún error al ejecutar la consulta SQL.
     */
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
        } catch (SQLException e) {
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(st != null) {st.close();}
            if(res != null) {res.close();} 
        }
        
        cn.desconectar(conn);
        return listaLocalidades;
    }


    /**
     * Obtiene la lista de localidades desde la base de datos.
     * 
     * @return Un objeto ArrayList<String> que contiene los nombres de las localidades.
     * @throws SQLException Si ocurre algún error al ejecutar la consulta SQL.
     */
    public ArrayList<String> getLocalidades() throws SQLException {
        ConexionBD cn = INSTANCE;
        ArrayList<String> listaLocalidades = null;
        conn = cn.conectar();

        try {
            st = conn.createStatement();
            res = st.executeQuery("SELECT nombre FROM LOCALIDAD;");

            listaLocalidades = new ArrayList<String>();
            while (res.next()) {
                listaLocalidades.add(res.getString(1));
            }
        } catch (SQLException e) {
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(st != null) {st.close();}
            if(res != null) {res.close();} 
        }
        
        cn.desconectar(conn);
        return listaLocalidades;
    }


    /**
     * Actualiza el nombre de una localidad en la base de datos.
     * 
     * @param provincia Provincia a la que pertenece la localidad.
     * @param oldLocalidad Nombre actual de la localidad.
     * @param newLocalidad Nuevo nombre para la localidad.
     * @return true si se modifica y no hay errores, false si no.
     * @throws SQLException Si ocurre algún error al ejecutar la consulta SQL.
     */
    public boolean actualizarNombreLocalidad(String provincia, String oldLocalidad, String newLocalidad) throws SQLException{
        ConexionBD cn = INSTANCE;
        conn = cn.conectar();
        boolean result = false;

        try {       
            conn.setAutoCommit(false);

            ps = conn.prepareStatement("UPDATE LOCALIDAD SET nombre = ? WHERE nombre = ? and provincia_id = (SELECT id FROM PROVINCIA WHERE nombre = ?);");
            ps.setString(1, newLocalidad);
            ps.setString(2, oldLocalidad);
            ps.setString(3, provincia);
            
            int filas = ps.executeUpdate();
            
            conn.commit(); //Confirma la transacción de la inserción de los datos.

            //Comprueba si ha modificado alguna fila y si es así, establece a true la variable result.
            if (filas > 0) {
                result = true;
            }
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true);
            if(ps != null) {ps.close();}
        }

        cn.desconectar(conn);
        return result;
    }


    /**
     * Inserta una nueva localidad en la base de datos.
     * 
     * @param provincia Provincia a la que pertenece la localidad.
     * @param localidad Nombre de la nueva localidad.
     * @return true si se ha añadido la localidad, false si no.
     * @throws SQLException Si ocurre algún error al ejecutar la consulta SQL.
     */
    public boolean insertarLocalidad(String provincia, String localidad) throws SQLException {
        ConexionBD cn = INSTANCE;
        boolean result = false;
        conn = cn.conectar();

        try {
            conn.setAutoCommit(false);

            ps = conn.prepareStatement("INSERT INTO LOCALIDAD " +
                    "(nombre, provincia_id) " +
                    "VALUES (?, (SELECT id FROM PROVINCIA WHERE nombre = ?));");

            ps.setString(1, localidad);
            ps.setString(2, provincia);
            
            int filas = ps.executeUpdate();
            
            conn.commit(); //Confirma la transacción de la inserción de los datos.
            
            if(filas > 0) {
                result = true;
            }
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            if(ps != null) {ps.close();} 
        }
        
        cn.desconectar(conn);
        return result;
    }

    /**
     * Borra una localidad de la base de datos.
     * 
     * @param provincia Nombre de la provincia a la que pertenece la localidad a borrar.
     * @param localidad Nombre de la localidad a borrar.
     * @return true si se borra la localidad, false si no.
     * @throws SQLException Si ocurre algún error al ejecutar la consulta SQL.
     */
    public boolean borrarLocalidad(String provincia, String localidad) throws SQLException {
        ConexionBD cn = INSTANCE;
        conn = cn.conectar();
        boolean result = false;

        try {
            conn.setAutoCommit(false);

            //sentencia sql para obtener el numero de direcciones que tienen como localidad a la que se intenta borrar.
            String sql = "SELECT count(id) FROM DIRECCION " +
                    "WHERE localidad_id = (SELECT id FROM LOCALIDAD WHERE nombre = ? " +
                    "AND provincia_id = (SELECT id FROM PROVINCIA WHERE nombre = ?));";

            ps = conn.prepareStatement(sql);
            ps.setString(1, localidad);
            ps.setString(2, provincia);
           
            res = ps.executeQuery();
            int direcciones_count = res.getInt(1); 
           
            //Si la localidad no tiene direcciones asociadas, borra la localidad.
            if (direcciones_count == 0) {
                sql = "DELETE FROM LOCALIDAD WHERE nombre = ? AND provincia_id = (SELECT id FROM PROVINCIA WHERE nombre = ?);";
                ps = conn.prepareStatement(sql); 
                ps.setString(1, localidad);
                ps.setString(2, provincia);
                int filas = ps.executeUpdate();

                conn.commit(); //Confirma la transacción de la inserción de los datos.

                if(filas > 0) {
                    result = true;
                }
            } else {
                //Si la localidad tiene direcciones asociadas, inserta un mensaje en el log.
                logger.warning("Intento de borrado de localidad: " + localidad  + ". Esta localidad tiene " + direcciones_count + " direcciones asociadas.");
            }
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            if(ps != null) {ps.close();}
            if(res != null) {res.close();} 
        }

        cn.desconectar(conn);
        return result;
    }


    /**
     * Actualiza los datos del alumno en la base de datos. 
     * 
     * @param alumno Alumno de donde se obtine la informacion.
     * @return true si se modifica y no hay errores, false si no.
     * @throws SQLException Si ocurre algún error al ejecutar la consulta SQL.
     */
    public boolean modificarAlumno(Alumno alumno) throws SQLException{
        ConexionBD cn = INSTANCE;
        conn = cn.conectar();
        boolean result = false;

        try {       
            conn.setAutoCommit(false);

            ps = conn.prepareStatement("UPDATE ALUMNO SET nombre = ?, apellido1 = ?, apellido2 = ?, genero = ?, fecha_nacimiento = ?, telefono = ?, email = ?, estado = ?, asistencia_semanal = ?, forma_pago = ? WHERE id = ?;");
            ps.setString(1, alumno.getNombre());
            ps.setString(2, alumno.getApellido1());
            ps.setString(3, alumno.getApellido2());
            ps.setString(4, alumno.getGenero().toString());
            ps.setString(5, alumno.getFechaNacimiento().toString());
            ps.setInt(6, alumno.getTelefono());
            ps.setString(7, alumno.getEmail());
            ps.setString(8, alumno.getEstado().toString());
            ps.setInt(9, alumno.getAsistenciaSemanal());
            ps.setString(10, alumno.getFormaPago().toString());
            ps.setInt(11, alumno.getId());
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
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true);
            if(ps != null) {ps.close();}
        }

        cn.desconectar(conn);
        return result;
    }


    /**
     * Borra el alumno pasado como parametro, sus mensualidades asociadas y su direccion de la base de datos.
     * 
     * @param alumno Alumno del que se va ha borrar sus datos de la base de datos.
     * @return true si borra el alumno y sus datos, false si no.
     * @throws SQLException Si ocurre algún error al ejecutar la consulta SQL.
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

            ps = conn.prepareStatement("DELETE FROM CLASE_ALUMNO WHERE alumno_id = ?;"); //Esto esta porque no funciona ON DELETE CASCADE en SqlCipher.
            ps.setInt(1, alumno.getId());
            ps.executeUpdate();

            ps = conn.prepareStatement("DELETE FROM MENSUALIDAD WHERE alumno_id = ?;"); //Esto esta porque no funciona ON DELETE CASCADE en SqlCipher.
            ps.setInt(1, alumno.getId());
            ps.executeUpdate();

            ps = conn.prepareStatement("DELETE FROM GRUPOALUMNOS_ALUMNO WHERE alumno_id = ?;"); //Esto esta porque no funciona ON DELETE CASCADE en SqlCipher.
            ps.setInt(1, alumno.getId());
            ps.executeUpdate();

            conn.commit(); //Confirma la transacción de la inserción de los datos.
            result = true;
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            if(ps != null) {ps.close();}
        }

        cn.desconectar(conn);
        return result;
    }
    

    /**
     * Devuelve el numero de clases en la que esta inscrito el alumna en la semana de la jornada pasada como parametro.
     * 
     * @param idAlumno Identificador del alumno del que se va a obtener la información
     * @param jornada Jornada de la que se va a extraer la semana para la busqueda.
     * @return int con el numero de clases que esta incrito el alumno en la semana de la jornada pasada como parametro o -1 si hay algun problema.
     * @throws SQLException Si ocurre algún error al ejecutar la consulta SQL.
     */
    public int numeroClasesInscrito(int idAlumno, Jornada jornada ) throws SQLException {
        ConexionBD cn = INSTANCE;
        conn = cn.conectar();
        int result = -1;

        try {  
            //Consulta a base de datos.
            //strftime('%W', fecha) -> devuelve el numero de semana.
            //strftime('%Y', feha) -> devuelve el año.
            String sql = "SELECT count(CS.alumno_id) FROM CLASE_ALUMNO CS JOIN CLASE C ON(CS.clase_id = C.id)"
                    + "WHERE CS.alumno_id = ? AND strftime('%W', C.jornada) = ? AND strftime('%Y', C.jornada) = ?;";

            //Obtengo el numero de semana de la jornada.
            int semana = jornada.getFecha().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR); //Obtengo el numero de semana de la jornada.

            //Conviete a string el numero de semana y añade un cero delante si el numero es menor de 10.
            String stringSemana = (semana < 10)? "0" + Integer.toString(semana): Integer.toString(semana); 

            ps = conn.prepareStatement(sql);
            ps.setInt(1, idAlumno);
            ps.setString(2, stringSemana);
            ps.setString(3, Integer.toString(jornada.getFecha().getYear()));
           
            res = ps.executeQuery();
            result = res.getInt(1);
        } catch (SQLException e) {
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(ps != null) {ps.close();}
            if(res != null) {res.close();} 
        }
        
        cn.desconectar(conn);
        return result;
    }

    /**
     * Crea una lista con los alumnos que superarían el número de sus asistencias semanales si se realiza la copia de la jornada.
     * 
     * @param jornada jornada de donde se obtienen los alumnos a comprobar.
     * @return un ArrayList<Alumno> con los alumnos que superarían su número de asistencias semanales si se copia la jornada.
     * @throws SQLException Si ocurre algún error al ejecutar la consulta SQL.
     */
    public ArrayList<Alumno> controlCopiaJornada(Jornada jornada ) throws SQLException {
        ConexionBD cn = INSTANCE;
        conn = cn.conectar();
        ArrayList<Alumno> alumnosMacht = null;
        ArrayList<Alumno> alumnosJornada = null;

        try {  
            alumnosMacht = new ArrayList<Alumno>();
            alumnosJornada = new ArrayList<Alumno>();

            //Consulta a base de datos.
            //strftime('%W', fecha) -> devuelve el numero de semana.
            //strftime('%Y', feha) -> devuelve el año.
            String sql = "SELECT count(CS.alumno_id) FROM CLASE_ALUMNO CS JOIN CLASE C ON(CS.clase_id = C.id)"
                    + "WHERE CS.alumno_id = ? AND strftime('%W', C.jornada) = ? AND strftime('%Y', C.jornada) = ?;";

            ps = conn.prepareStatement(sql);
            ps.setString(2, Integer.toString(jornada.getFecha().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)));
            ps.setString(3, Integer.toString(jornada.getFecha().getYear()));

            //Itera a través de las clases de la jornada.
            for (Clase clase : jornada.getClases()) {
                //Itera a través de los alumnos en la lista de cada clase.
                for (Alumno alumno : clase.getListaAlumnos()) {
                    alumnosJornada.add(alumno);
                    boolean presenteEnLista = false;

                    // Comprueba si el alumno ya está en la lista de alumnosMacht.
                    for (Alumno alum : alumnosMacht) {
                        if(alum.getId() == alumno.getId()) {
                            presenteEnLista = true;
                            break;
                        }
                    }
                    
                    if(presenteEnLista) {
                        continue; // Si el alumno ya está en la lista, pasa al siguiente.
                    }

                    ps.setInt(1, alumno.getId());
                    res = ps.executeQuery();
                    int result = res.getInt(1);
                    int compensacionAsistencias = -1; //Entero que representa el numero de veces que el alumno repite en la jornada.

                    //Iteramos sobre la lista alumnos de jornada para ver cuantas veces asiste en esta jornada.
                    for (Alumno alumJornada : alumnosJornada) {
                        if(alumJornada.getId() == alumno.getId()) {
                            compensacionAsistencias++;
                        }
                    }

                    //compensacionAsistencias = 1 asistencia en la jornada = 0 (por defecto 0).
                   
                    //Comprueba si el alumno cumple con los criterios de asistencia.
                    if(result >= alumno.getAsistenciaSemanal() - compensacionAsistencias) {
                        alumnosMacht.add(alumno); 
                    }
                }
            }
        } catch (SQLException e) {
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
            alumnosMacht = null;
        } finally { 
            if(ps != null) {ps.close();}
            if(res != null) {res.close();} 
        }
        
        cn.desconectar(conn);
        return alumnosMacht;
    }


    /**
     * Inserta una mensualidad en la base de datos.
     * 
     * @param mensualidad La mensualidad que se desea insertar.
     * @return true si la inserción es exitosa, false de lo contrario.
     * @throws SQLException Si ocurre algún error al ejecutar la consulta SQL.
     */
    public boolean insertarMensualidad(Mensualidad mensualidad) throws SQLException {
        ConexionBD cn = INSTANCE;
        boolean result = false;
        conn = cn.conectar();

        try {
            conn.setAutoCommit(false);

            ps = conn.prepareStatement("INSERT INTO MENSUALIDAD " +
                    "(alumno_id, mes, anio, fecha_pago, forma_pago, estado_pago, asistencia_semanal, cuota, anotacion) " +
                    "VALUES (?,?,?,?,?,?,?,?,?);");

            ps.setInt(1, mensualidad.getIdAlumno());
            ps.setString(2, Fechas.obtenerNombreMes(mensualidad.getFecha().getMonthValue()));
            ps.setString(3, Integer.toString(mensualidad.getFecha().getYear()));
            ps.setString(4, (mensualidad.getFechaPago() == null)? null : mensualidad.getFechaPago().toString());
            ps.setString(5, mensualidad.getFormaPago().toString());
            ps.setString(6, mensualidad.getEstadoPago().toString());
            ps.setInt(7, mensualidad.getAsistenciasSemanales());
            ps.setDouble(8, mensualidad.getImporte());
            ps.setString(9, mensualidad.getAnotacion());
            
            ps.executeUpdate();

            //Obtenemos el id que se le asignó a esta Mensualidad.
            st = conn.createStatement();
            res = st.executeQuery("SELECT last_insert_rowid()");
            int idMensualidadInsertada = res.getInt(1); //Guardo el valor de id obtenido en la consulta en la variable.
            mensualidad.setId(idMensualidadInsertada); //Pongo el id al objeto Mensualidad.
            
            conn.commit(); //Confirma la transacción de la inserción de los datos.
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            result = true;
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(ps != null) {ps.close();} 
            if(res != null) {res.close();}
            if(st != null) {st.close();}
        }
        
        cn.desconectar(conn);
        return result;
    }


    /**
     * Actualiza los datos de la mensualidad en la base de datos. 
     * 
     * @param mensualidad Mensualidad de donde se obtine la informacion.
     * @return true si se modifica y no hay errores, false si no.
     * @throws SQLException
     */
    public boolean actualizarMensualidad(Mensualidad mensualidad) throws SQLException{
        ConexionBD cn = INSTANCE;
        conn = cn.conectar();
        boolean result = false;

        try {       
            conn.setAutoCommit(false);

            ps = conn.prepareStatement("UPDATE MENSUALIDAD SET mes = ?, anio = ?, fecha_pago = ?, forma_pago = ?, estado_pago = ?, asistencia_semanal = ?, cuota = ?, anotacion = ? WHERE id = ?;");
            ps.setString(1, Fechas.obtenerNombreMes(mensualidad.getFecha().getMonthValue()));
            ps.setString(2, Integer.toString(mensualidad.getFecha().getYear()));
            ps.setString(3, (mensualidad.getFechaPago() == null)? null : mensualidad.getFechaPago().toString());
            ps.setString(4, mensualidad.getFormaPago().toString());
            ps.setString(5, mensualidad.getEstadoPago().toString());
            ps.setInt(6, mensualidad.getAsistenciasSemanales());
            ps.setDouble(7, mensualidad.getImporte());
            ps.setString(8, mensualidad.getAnotacion());
            ps.setInt(9, mensualidad.getId());
            ps.executeUpdate();
            
            conn.commit(); //Confirma la transacción de la inserción de los datos.
            result = true;
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true);
            if(ps != null) {ps.close();}
        }

        cn.desconectar(conn);
        return result;
    }

    /**
     * Borra la Mensualidad pasado como parametro.
     * 
     * @param alumno Mensualidad ha borrar de la base de datos.
     * @return true si borra la Menualidad, false si no.
     * @throws SQLException
     */
    public boolean borrarMensualidad(Mensualidad mensualidad) throws SQLException {
        ConexionBD cn = INSTANCE;
        conn = cn.conectar();
        boolean result = false;

        try {
            conn.setAutoCommit(false);

            ps = conn.prepareStatement("DELETE FROM MENSUALIDAD WHERE id = ?;"); //Esto esta porque no funciona ON DELETE CASCADE en SqlCipher.
            ps.setInt(1, mensualidad.getId());
            ps.executeUpdate();

            conn.commit(); //Confirma la transacción de la inserción de los datos.
            result = true;
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            if(ps != null) {ps.close();}
        }

        cn.desconectar(conn);
        return result;
    }


    /**
     * Inserta una lista de mensualidades en la base de datos.
     * 
     * @param listaMensualidades La lista de mensualidades que se desea insertar.
     * @return true si la inserción es exitosa, false de lo contrario.
     * @throws SQLException Si ocurre algún error al ejecutar la consulta SQL.
     */
    public boolean insertarListaMensualidades(ArrayList<Mensualidad> listaMensualidades) throws SQLException {
        ConexionBD cn = INSTANCE;
        boolean result = false;
        conn = cn.conectar();
        try {
            conn.setAutoCommit(false);

            ps = conn.prepareStatement("INSERT INTO MENSUALIDAD " +
                    "(alumno_id, mes, anio, fecha_pago, forma_pago, estado_pago, asistencia_semanal, cuota, anotacion) " +
                    "VALUES (?,?,?,?,?,?,?,?,?);");

            for (Mensualidad mensualidad : listaMensualidades) { 
                ps.setInt(1, mensualidad.getIdAlumno());
                ps.setString(2, Fechas.obtenerNombreMes(mensualidad.getFecha().getMonthValue()));
                ps.setString(3, Integer.toString(mensualidad.getFecha().getYear()));
                ps.setString(4, (mensualidad.getFechaPago() == null)? null : mensualidad.getFechaPago().toString());
                ps.setString(5, mensualidad.getFormaPago().toString());
                ps.setString(6, mensualidad.getEstadoPago().toString());
                ps.setInt(7, mensualidad.getAsistenciasSemanales());
                ps.setDouble(8, mensualidad.getImporte());
                ps.setString(9, mensualidad.getAnotacion());
                
                ps.executeUpdate();

                //Obtenemos el id que se le asignó a esta Mensualidad.
                st = conn.createStatement();
                res = st.executeQuery("SELECT last_insert_rowid()");
                int idMensualidadInsertada = res.getInt(1); //Guardo el valor de id obtenido en la consulta en la variable.
                mensualidad.setId(idMensualidadInsertada); //Pongo el id al objeto Mensualidad.
            }

            conn.commit(); //Confirma la transacción de la inserción de los datos.
            conn.setAutoCommit(true); //Restaura autocommit a true después de confirmar la transacción.
            result = true;
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally { 
            if(ps != null) {ps.close();} 
            if(res != null) {res.close();}
            if(st != null) {st.close();}
        }
        
        cn.desconectar(conn);
        return result;
    }


    /**
     * Actualiza el precio de las clases.
     * 
     * @param numeroClases Numero de aistencias a clase.
     * @param precio Nuevo precio de las asistencias a clase.
     * @return true si la modificacion es exitosa, false de lo contrario.
     * @throws SQLException Si ocurre algún error al ejecutar la consulta SQL.
     */
    public boolean actualizarPrecioClase(int numeroClases, Double precio) throws SQLException{
        ConexionBD cn = INSTANCE;
        conn = cn.conectar();
        boolean result = false;

        try {       
            conn.setAutoCommit(false);

            ps = conn.prepareStatement("UPDATE PRECIO_CLASE SET precio = ? WHERE numero_clases = ?;");
            ps.setDouble(1, precio);
            ps.setInt(2, numeroClases);
            
            int filas = ps.executeUpdate();

            conn.commit(); //Confirma la transacción de la inserción de los datos.

            //Comprueba si ha modificado alguna fila y si es así, establece a true la variable result.
            result = filas > 0;
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            e.printStackTrace();
        } finally {
            conn.setAutoCommit(true);
            if(ps != null) {ps.close();}
        }

        cn.desconectar(conn);
        return result;
    }

    /**
     * Recupera el listado de todos los grupos de alumnos desde la base de datos.
     * Este método consulta la tabla GRUPOALUMNOS para obtener todos los registros de grupos,
     * creando y llenando una lista de objetos {@link GrupoAlumnos}. Para cada grupo de alumnos,
     * también recupera y asigna los alumnos correspondientes de la relación GRUPOALUMNOS_ALUMNO.
     *
     * @return ArrayList<GrupoAlumnos> Una lista de objetos {@link GrupoAlumnos} que representa todos los grupos
     *         de alumnos encontrados en la base de datos, junto con los alumnos asignados a cada grupo.
     * @throws SQLException Si ocurre un error durante la ejecución de las consultas SQL.
     */
    public ArrayList<GrupoAlumnos> getListadoGruposAlumnos() throws SQLException {
        ConexionBD cn = INSTANCE;
        ArrayList<GrupoAlumnos> listaGruposAlumnos = null;
        conn = cn.conectar();

        try {
            st = conn.createStatement();
            res = st.executeQuery("SELECT * FROM GRUPOALUMNOS;");
            listaGruposAlumnos = new ArrayList<GrupoAlumnos>();

            //Iterar sobre el resultado de la consulta para crear y añadir objetos GrupoAlumnos a la lista.
            while (res.next()) {
                GrupoAlumnos grupo = new GrupoAlumnos(
                    res.getInt(1),
                    res.getString(2),
                    res.getString(3)
                );
               
                listaGruposAlumnos.add(grupo);
            }

            //Si se encontraron grupos, proceder a cargar los alumnos de cada grupo.
            if(listaGruposAlumnos.size() > 0) {
                for (GrupoAlumnos grupoAlumnos : listaGruposAlumnos) {
                    //Ejecutar una consulta para obtener los IDs de los alumnos pertenecientes a cada grupo.
                    res = st.executeQuery("SELECT A.id FROM ALUMNO A JOIN GRUPOALUMNOS_ALUMNO G ON(A.id = G.alumno_id) WHERE G.grupo_id = " + grupoAlumnos.getId() + " ;"); //consulta sql a tabla ALUMNO.
                    
                    //Crear y añadir objetos Alumno a cada grupo según los resultados de la consulta.
                    while (res.next()) {
                        Alumno alumno = new Alumno();
                        alumno.setId(res.getInt(1));
                        grupoAlumnos.addAlumno(alumno);
                    }
                }
            }
        } catch (SQLException e) {
            logger.severe("Excepción SQL: " + e.toString());
        } finally { 
            if(st != null) {st.close();}
            if(res != null) {res.close();} 
        }
        
        cn.desconectar(conn);
        return listaGruposAlumnos;
    }

    /**
     * Inserta un nuevo grupo de alumnos en la base de datos.
     * Este método crea un nuevo registro en la tabla GRUPOALUMNOS con los detalles proporcionados
     * en el objeto {@link GrupoAlumnos} suministrado. El ID del nuevo grupo se establece en el objeto
     * {@link GrupoAlumnos} después de la inserción.
     *
     * @param grupoAlumnos El objeto {@link GrupoAlumnos} que contiene la información del nuevo grupo a insertar.
     * @return boolean Verdadero si la inserción es exitosa y se afecta al menos una fila en la base de datos,
     *                 falso en caso contrario o si ocurre un error durante la operación.
     * @throws SQLException Si ocurre un error de SQL durante la ejecución de la operación de inserción.
     */
    public boolean insertarGrupoAlumnos(GrupoAlumnos grupoAlumnos) throws SQLException {
        ConexionBD cn = INSTANCE;
        conn = cn.conectar();
    
        //Define la consulta SQL para insertar un nuevo grupo de alumnos.
        String sql = "INSERT INTO GRUPOALUMNOS (nombre, descripcion) values (?, ?);";
    
        try {
            conn.setAutoCommit(false);    
            
            //Prepara la consulta SQL para la inserción, estableciendo los valores para 'nombre' y 'descripcion'.
            ps = conn.prepareStatement(sql);
            ps.setString(1, grupoAlumnos.getNombre());
            ps.setString(2, grupoAlumnos.getDescripcion());
    
            //Ejecuta la consulta de inserción y guarda el número de filas afectadas.
            int filasAfectadas = ps.executeUpdate();
            
            //Obtiene el ID generado automáticamente para el nuevo grupo insertado y lo asigna al objeto grupoAlumnos.
            st = conn.createStatement();
            res = st.executeQuery(SQL_OBTENER_ULTIMO_ID_INSERTADO);
            grupoAlumnos.setId(res.getInt(1)); //Asigna el id obtenido en la consulta.
    
            conn.commit(); //Confirma la transacción de la inserción de los datos.
            return filasAfectadas > 0; //Retorna verdadero si se insertó al menos una fila en la base de datos, indicando éxito.
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            return false; //Retorna falso para indicar que la operación no fue exitosa.
        } finally {
            if(ps != null) {ps.close();}
            if(st != null) {st.close();}
            if(res != null) {res.close();} 

            conn.setAutoCommit(true);
            cn.desconectar(conn);
        }
    }


    /**
     * Actualiza la información de un grupo de alumnos específico en la base de datos.
     * Este método modifica los campos 'nombre' y 'descripcion' del registro correspondiente
     * al grupo de alumnos proporcionado, identificado por su ID único.
     *
     * @param grupoAlumnos El objeto {@link GrupoAlumnos} que representa el grupo de alumnos con la información actualizada.
     * @return boolean Verdadero si la operación de actualización afecta al menos una fila (indicando éxito),
     *                 falso en caso contrario o si ocurre un error durante la operación.
     * @throws SQLException Si se produce un error de SQL durante la ejecución de la consulta.
     */
    public boolean actualizarGrupoAlumno(GrupoAlumnos grupoAlumnos) throws SQLException {
        ConexionBD cn = INSTANCE;
        conn = cn.conectar();
    
        //Define la consulta SQL para actualizar los detalles de un grupo de alumnos existente por ID.
        String sql = "UPDATE GRUPOALUMNOS SET nombre = ?, descripcion = ? WHERE id = ?;";
    
        try {
            conn.setAutoCommit(false);    
            ps = conn.prepareStatement(sql);
            
            //Asignar los valores a cada uno de los parámetros de la consulta.
            ps.setString(1, grupoAlumnos.getNombre());
            ps.setString(2, grupoAlumnos.getDescripcion());
            ps.setInt(3, grupoAlumnos.getId());
    
            //Ejecutar la consulta de actualización y obtener el número de filas afectadas.
            int filasAfectadas = ps.executeUpdate();
    
            conn.commit(); //Confirma la transacción de la inserción de los datos.
            return filasAfectadas > 0; // Devolver verdadero si la operación de actualización fue exitosa (afectó al menos una fila).
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            return false;
        } finally {
            if(ps != null) {ps.close();}

            conn.setAutoCommit(true);
            cn.desconectar(conn);
        }
    }


    /**
     * Borra un grupo de alumnos específico de la base de datos.
     * Este método elimina el registro correspondiente al grupo de alumnos proporcionado
     * de la tabla GRUPOALUMNOS, basándose en su identificador único (ID).
     *
     * @param grupoAlumnos El objeto {@link GrupoAlumnos} que representa el grupo de alumnos a borrar.
     * @return boolean Verdadero si la operación de borrado se ejecuta exitosamente y al menos una fila es afectada,
     *                 falso en caso contrario o si ocurre un error durante la operación.
     * @throws SQLException Si ocurre un error de SQL durante la ejecución de la consulta.
     */
    public boolean borrarGrupoAlumno(GrupoAlumnos grupoAlumnos) throws SQLException {
        ConexionBD cn = INSTANCE;
        conn = cn.conectar();
    
        //Definición de la consulta SQL para borrar un grupo de alumnos por ID.
        String sql = "DELETE FROM GRUPOALUMNOS WHERE id = ?;";
    
        try {
            conn.setAutoCommit(false);    
            ps = conn.prepareStatement(sql);
            
            //Establecer el ID del grupo de alumnos a borrar en la consulta.
            ps.setInt(1, grupoAlumnos.getId());
    
            //Ejecutar la instrucción SQL y obtener el número de filas afectadas.
            int filasAfectadas = ps.executeUpdate();
    
            conn.commit(); //Confirma la transacción de la inserción de los datos.
            return filasAfectadas > 0; //Retorna verdadero si la operación afectó al menos una fila, indicando éxito en el borrado.
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            return false;
        } finally {
            if(ps != null) {ps.close();}

            conn.setAutoCommit(true);
            cn.desconectar(conn);
        }
    }


    /**
     * Actualiza la información de los grupos de alumnos en la base de datos. Esto incluye insertar nuevos grupos,
     * actualizar grupos existentes y eliminar grupos marcados para borrado. También maneja la inserción de alumnos
     * en los grupos correspondientes.
     *
     * @param listaGruposAlumnos La lista de grupos de alumnos para actualizar.
     * @return boolean Verdadero si la actualización se completa con éxito, falso en caso contrario.
     * @throws SQLException Si ocurre algún problema durante las operaciones de base de datos.
     */
    public boolean actualizarGruposAlumnos(ArrayList<GrupoAlumnos> listaGruposAlumnos) throws SQLException {
        ConexionBD cn = INSTANCE;
        boolean result = false;
        int filasAfectadas = 0;
        int alumnosInsertados = 0;
        ArrayList<GrupoAlumnos> listaGruposAlumnosBD;
        conn = cn.conectar();                    

        //Consultas SQL para operaciones de CRUD en la tabla GRUPOALUMNOS y GRUPOALUMNOS_ALUMNO
        String sqlGetListaGruposAlumnos = "SELECT * FROM GRUPOALUMNOS";
        String sqlInsertarGrupo = "INSERT INTO GRUPOALUMNOS (nombre, descripcion) VALUES (?, ?);";
        String sqlActualizarGrupo = "UPDATE GRUPOALUMNOS SET nombre = ?, descripcion = ? WHERE id = ?";
        String sqlBorrarGrupo = "DELETE FROM GRUPOALUMNOS WHERE id = ?";
        String sqlBorrarTodosAlumnosGrupo = "DELETE FROM GRUPOALUMNOS_ALUMNO WHERE grupo_id = ?";
        String sqlAddAlumnoGrupo = "INSERT INTO GRUPOALUMNOS_ALUMNO VALUES(?, ?)"; //(?, ?) = (grupo_id, alumno_id)
        String sqlNumeroAlumnosInscritosGrupo = "SELECT count(alumno_id) FROM GRUPOALUMNOS_ALUMNO WHERE grupo_id = ?";
    
        try {
            conn.setAutoCommit(false);
            
            //Obtener la lista actual de grupos de alumnos de la base de datos.
            st = conn.createStatement();
            res = st.executeQuery(sqlGetListaGruposAlumnos);
            listaGruposAlumnosBD = new ArrayList<>();
            while (res.next()) {
                GrupoAlumnos grupo = new GrupoAlumnos(
                    res.getInt(1),
                    res.getString(2),
                    res.getString(3)
                );
               
                listaGruposAlumnosBD.add(grupo);
            }

            //Recorre la lista de grupos para actualizar sus datos en la BD.
            for (var grupoAlumnos : listaGruposAlumnos) {

                //Proceso de eliminación de grupos marcados para borrado.
                if (grupoAlumnos.getNombre().endsWith("-borrado-")) {
                    //Solo proceder si el grupo tiene un ID válido (no es nuevo).
                    if(grupoAlumnos.getId() != -1) {

                        //Eliminar todos los alumnos asociados al grupo, si los hay.
                        if(!grupoAlumnos.getListaAlumnos().isEmpty()) { 
                            ps = conn.prepareStatement(sqlBorrarTodosAlumnosGrupo);
                            ps.setInt(1, grupoAlumnos.getId());
                            filasAfectadas = 0;
                            filasAfectadas = ps.executeUpdate();

                            //Comprueba que el numero de alumnos borrados sea el mismo que el de la lista de alumnos del grupo.
                            if(filasAfectadas != grupoAlumnos.getListaAlumnos().size()) {
                                return false;
                            }
                        }

                        //Eliminar el grupo de la base de datos.
                        ps = conn.prepareStatement(sqlBorrarGrupo);
                        ps.setInt(1, grupoAlumnos.getId());
                        filasAfectadas = 0;
                        filasAfectadas = ps.executeUpdate();

                        //Comprueba que se a borrado un grupo.
                        if(filasAfectadas != 1) {
                            return false;
                        }
                            
                    }
                    
                //Proceso de inserción de nuevos grupos.  
                } else if(grupoAlumnos.getId() == -1) {
                    ps = conn.prepareStatement(sqlInsertarGrupo);

                    //Establecer los valores de los parámetros.
                    ps.setString(1, grupoAlumnos.getNombre());
                    ps.setString(2, grupoAlumnos.getDescripcion());

                    //Ejecutar la instrucción SQL de inserción.
                    filasAfectadas = 0;
                    filasAfectadas = ps.executeUpdate();

                    //Comprueba que se añade el grupo a la BD.
                    if(filasAfectadas != 1) {
                        return false;
                    }

                    //Asignación del ID al nuevo grupo insertado.
                    st = conn.createStatement();
                    res = st.executeQuery(SQL_OBTENER_ULTIMO_ID_INSERTADO); //Obtenemos el id que se le asignó a grupoAlumnos.
                    grupoAlumnos.setId(res.getInt(1)); //Asigna el id obtenido en la consulta.

                    //Comprueba que se actualiza el id del grupo insertado.
                    if(grupoAlumnos.getId() < 1) {
                        return false;
                    }

                    alumnosInsertados = 0;
                    filasAfectadas = 0;

                    //Inserción de alumnos al nuevo grupo.
                    for (var alumno : grupoAlumnos.getListaAlumnosObservable()) {
                        ps = conn.prepareStatement(sqlAddAlumnoGrupo);

                        //Establecer los valores de los parámetros.
                        ps.setInt(1, grupoAlumnos.getId());
                        ps.setInt(2, alumno.getId());

                        //Ejecutar la instrucción SQL de inserción.
                        filasAfectadas = ps.executeUpdate();
                        alumnosInsertados += filasAfectadas; 
                    }

                    //Comprueba que el numero de alumnos del grupo sea los mismos que los alumnos insertados a BD.
                    if(alumnosInsertados != grupoAlumnos.getListaAlumnosObservable().size()) {
                        return false;
                    }
                
                //Proceso de actualización de grupos existentes.
                } else {
                    for (var grupoAlumnosBD : listaGruposAlumnosBD) {
                        if(grupoAlumnosBD.getId() == grupoAlumnos.getId()) {
                            //Actualizar el grupo si su nombre o descripción han cambiado.
                            if(!grupoAlumnos.getNombre().equals(grupoAlumnosBD.getNombre()) || !grupoAlumnos.getDescripcion().equals(grupoAlumnosBD.getDescripcion())) {
                                ps = conn.prepareStatement(sqlActualizarGrupo);
            
                                //Establecer los valores de los parámetros.
                                ps.setString(1, grupoAlumnos.getNombre());
                                ps.setString(2, grupoAlumnos.getDescripcion());
                                ps.setInt(3, grupoAlumnos.getId());
                        
                                //Ejecutar la instrucción SQL de inserción.
                                filasAfectadas = 0;
                                filasAfectadas = ps.executeUpdate();

                                //Comprueba que se ha modificado una fila.
                                if(filasAfectadas != 1) {
                                    return false;
                                }
                            }

                            //Comprueba si hay alumnos adcritos al grupo para borrarlos del grupo.
                            if(!grupoAlumnos.getListaAlumnos().isEmpty()) { 
                                ps = conn.prepareStatement(sqlBorrarTodosAlumnosGrupo);
                                ps.setInt(1, grupoAlumnos.getId());

                                //Ejecutar la instrucción SQL.
                                filasAfectadas = 0;
                                filasAfectadas = ps.executeUpdate();

                                //Comprueba que el numero de alumnos borrados sea el mismo que el de la lista de alumnos del grupo.
                                if(filasAfectadas != grupoAlumnos.getListaAlumnos().size()) {
                                    return false;
                                }
                            }

                            //Obtiene el numero de alumnos adcritos al grupo en la BD.
                            st = conn.createStatement();
                            res = st.executeQuery(sqlNumeroAlumnosInscritosGrupo);
                            int numeroAlumnosInscritos = res.getInt(1);

                            //Comprueba que no hay alumnos inscritos al grupo.
                            if(numeroAlumnosInscritos != 0) {
                                return false;
                            }

                            alumnosInsertados = 0; 

                            //Recorre la lista de alumnos del grupo para insertalos en la BD.
                            for (var alumno : grupoAlumnos.getListaAlumnosObservable()) {
                                ps = conn.prepareStatement(sqlAddAlumnoGrupo);

                                //Establecer los valores de los parámetros.
                                ps.setInt(1, grupoAlumnos.getId());
                                ps.setInt(2, alumno.getId());

                                //Ejecutar la instrucción SQL de inserción.
                                filasAfectadas = 0;
                                filasAfectadas = ps.executeUpdate();
                                alumnosInsertados += filasAfectadas; 
                            }

                            //Comprueba que el numero de alumnos del grupo sea los mismos que los alumnos insertados a BD.
                            if(alumnosInsertados != grupoAlumnos.getListaAlumnosObservable().size()) {
                                return false;
                            }

                            break; //Salir del ciclo una vez que se encuentra y actualiza el grupo correspondiente.
                        }
                    }
                }
            }

            result = true;
            conn.commit(); //Confirmar todas las operaciones realizadas.
            return result;
        } catch (SQLException e) {
            conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
            logger.severe("Excepción SQL: " + e.toString());
            return false;
        } finally {

            if(!result) {
                conn.rollback(); //Si hay algun error hacemos un rollback en la inserción de los datos.
                logger.warning("ERROR al intentar guardar la configuracion de los grupos de alumnos en la base de datos.");
            }

            if(ps != null) {ps.close();}
            if(st != null) {st.close();}
            if(res != null) {res.close();} 
            conn.setAutoCommit(true);
            cn.desconectar(conn);
        }
    }

}
