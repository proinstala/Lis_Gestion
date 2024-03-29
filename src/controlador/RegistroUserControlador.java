package controlador;

import java.io.File;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import baseDatos.ConexionBD;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modelo.Usuario;
import utilidades.Constants;
import utilidades.Toast;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class RegistroUserControlador implements Initializable {

    private double x, y;
    private Toast toast;
    private Usuario usuario;
    private Usuario usuarioRoot;
    private Logger loggerRoot; 
    private ConexionBD conexionBD;
    private StringProperty password;
    private StringProperty confimarPassword;

    
    @FXML
    private ImageView imImagenRegistro;

    @FXML
    private AnchorPane apRegistroUsuario;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnRegistrarse;

    @FXML
    private PasswordField tfConfirmacionPass;

    @FXML
    private TextField tfNombre;

    @FXML
    private PasswordField tfPassword;

    @FXML
    private TextField tfPasswordVisible;

    @FXML
    private TextField tfConfirmacionPassVisible;

    @FXML
    private CheckBox chbVisible;

    @FXML
    private Label lbInformacion;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Añadir clases de estilo CSS a elementos.
        apRegistroUsuario.getStyleClass().add("fondo_ventana_degradado_masBorde");
        btnCancelar.getStyleClass().add("boton_rojo");
        lbInformacion.getStyleClass().add("color_texto_negro");

        //Cargar imagenes en ImageView.
        Image imagenRegistro;
        try {
            //Intentar cargar la imagen desde el recurso en el IDE y en el JAR.
            imagenRegistro = new Image(getClass().getResourceAsStream("/recursos/usuario_add_1_128.png")); //Forma desde IDE y JAR.
        } catch (Exception e) {
            //Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR.
            imagenRegistro = new Image("/recursos/usuario_add_1_128.png"); //Forma desde el JAR.
            
        }
        imImagenRegistro.setImage(imagenRegistro); //Establecer la imagen cargada en el ImageView.
        
        //Configurar el evento cuando se presiona el ratón en el panel apRegistroUsuario.
        apRegistroUsuario.setOnMousePressed(mouseEvent -> {
            //Obtener las coordenadas X e Y del ratón en relación con la escena.
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });

        //Configurar el evento cuando se arrastra el ratón en el panel apRegistroUsuario.
        apRegistroUsuario.setOnMouseDragged(mouseEvent -> {
            //Obtener la referencia al Stage actual y establecer las nuevas coordenadas X e Y.
            ((Stage) apRegistroUsuario.getScene().getWindow()).setX(mouseEvent.getScreenX() - x);
            ((Stage) apRegistroUsuario.getScene().getWindow()).setY(mouseEvent.getScreenY() - y);
        });

        loggerRoot = Logger.getLogger(Constants.USER_ROOT); //Crea una instancia de la clase Logger asociada al nombre de registro.
        conexionBD = ConexionBD.getInstance();              //Obtener una instancia de la clase ConexionBD utilizando el patrón Singleton.
        toast = new Toast();


        //Inicializa los StringProperty
        password = new SimpleStringProperty("");
        confimarPassword = new SimpleStringProperty("");

        //Bindea los Texfield con los StringProperty.
        tfPassword.textProperty().bindBidirectional(password);
        tfPasswordVisible.textProperty().bindBidirectional(password);
        tfConfirmacionPass.textProperty().bindBidirectional(confimarPassword);
        tfConfirmacionPassVisible.textProperty().bindBidirectional(confimarPassword);

        //Pone oculto los TextFiel que muestran los password.
        tfPasswordVisible.setVisible(false);
        tfConfirmacionPassVisible.setVisible(false);

        //Añade listener al CheckBox que oculta y hace visible a los TextFiel y PasswordFiel.
        chbVisible.selectedProperty().addListener((o, ov, nv) -> {
            if(chbVisible.isSelected()) {
                tfPassword.setVisible(false);
                tfPasswordVisible.setVisible(true);

                tfConfirmacionPass.setVisible(false);
                tfConfirmacionPassVisible.setVisible(true);
            
            } else {
                tfPassword.setVisible(true);
                tfPasswordVisible.setVisible(false);

                tfConfirmacionPass.setVisible(true);
                tfConfirmacionPassVisible.setVisible(false);
            }
        });

        //Configurar un evento de clic del ratón para el botón "Cancelar".
        btnCancelar.setOnMouseClicked(e -> {
            ((Stage) apRegistroUsuario.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
        });
    }


    /**
     * Método para manejar el evento que registra un nuevo usuario en la aplicación.
     * Se invoca al hacer clic en el botón "Registrarse".
     *
     * @param event El evento de mouse que desencadena la acción.
     */
    @FXML
    void registrarUsuario(MouseEvent event) {
        // Verificar los campos de nombre y contraseña antes de proceder al registro.
        if(comprobarCampoNombre() && comprobarCamposPassword()) {
            usuario = new Usuario();
            usuario.setNombreUsuario(tfNombre.getText());
            usuario.setPassword(password.get());
            usuario.setDirectorio(new File(tfNombre.getText().toLowerCase()));
            
            //Crear los ficheros del usuario.
            if(crearFicherosUsuario(usuario)) {
                boolean insertarUserOk = false;
                try {
                    conexionBD.setUsuario(usuarioRoot);
                    insertarUserOk = conexionBD.insertarUsuario(usuario);
                } catch (SQLException e) {
                    loggerRoot.severe("Excepción: " + e.toString());
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    loggerRoot.severe("Excepción: " + e.toString());
                    e.printStackTrace();
                } catch (Exception e) {
                    loggerRoot.severe("Excepción: " + e.toString());
                    e.printStackTrace();
                }

                //Verificar si se ha insertado correctamente el usuario en la base de datos.
                if(insertarUserOk) {
                    loggerRoot.config("Nuevo usuario registrado con exito. id: " + usuario.getId() + " || nombre: " + usuario.getNombreUsuario());
                    mensajeAviso(
                        AlertType.INFORMATION,
                        "Registro de Usuario",
                        "",
                        "Registro de Usuario completado con exito.");

                    ((Stage) apRegistroUsuario.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
                } else {
                    borrarFicherosUsuario(usuario.getDirectorio());
                    loggerRoot.warning("No se ha podido insertar el nuevo usuario en la base de datos.");
                    toast.show((Stage) apRegistroUsuario.getScene().getWindow(), "No se ha podido insertar el usuario en la base de datos.!!.");
                }
                
            
            } else {
                loggerRoot.warning("No se ha podido crear los ficheros del nuevo usuario en la base de datos.");
                toast.show((Stage) apRegistroUsuario.getScene().getWindow(), "No se ha podido crear los ficheros de usuario!!.");
            }

        }
        
    }
    

    /**
     * Comprueba el campo de nombre de usuario.
     *
     * @return true si el campo de nombre de usuario es válido, false de lo contrario.
     */
    private boolean comprobarCampoNombre() {
        String nombre = tfNombre.getText(); //Obtener el contenido del TextField
        
        //Compila la expresión regular para validar el nombre del usuario.
        Pattern nombrePat = Pattern.compile("^[^\\\\/:*?<>|\\s]{2,30}$"); //Comprobacion: cadena sin espacios ni caracteres \/:*?<>
        Matcher nombreMatch = nombrePat.matcher(nombre);

        if(!nombreMatch.matches()) {
            mensajeAviso(
                AlertType.WARNING,
                "Error nombre usuario",
                "",
                "El nombre de usuario tiene que contener de 2 a 30 caracteres.\n" + 
                                "No puede contener espacios en blanco ni los siguiente caracteres: \\ / : * ? < >");

            return false;
        } else {
            try {
                if (conexionBD.comprobarNombreUsuario(nombre)) {
                    mensajeAviso(
                        AlertType.WARNING,
                        "Error nombre usuario",
                        "",
                        "El nombre de usuario \"" + nombre + "\" no está disponible.\n" + 
                                        "Introduce un nuevo nombre de usuario.");

                    return false;
                }
            } catch (SQLException e) {
                loggerRoot.severe("Excepción: " + e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                loggerRoot.severe("Excepción: " + e.toString());
                e.printStackTrace();
            }
        }
        return true;
    }


    /**
     * Comprueba los campos de contraseña.
     *
     * @return true si los campos de contraseña son válidos, false de lo contrario.
     */
    private boolean comprobarCamposPassword() {
        //compilamos la expresion regular.(Una letra mañuscula o minuscula o un numero del 0 al 9 de 4 a 20 caracteres).
		Pattern passPat = Pattern.compile("^[\\S]{4,20}$"); //Comprobación: cadena sin espacios en blanco de 4 a 20 caracteres.
        Matcher passMatch = passPat.matcher(password.get());

        if(!passMatch.matches()) {
            mensajeAviso(
                AlertType.WARNING,
                "Error password",
                "",
                "El password de usuario tiene que contener de 4 a 20 caracteres.\n" + 
                                "No puede contener espacios en blanco.");

            return false;
        } else if(!password.get().equals(confimarPassword.get())) {
            mensajeAviso(
                AlertType.WARNING,
                "Error confirmación password",
                "",
                "El password de confirmación no conincide con el password.");

            return false;
        } else {
            return true;
        }
    }


    /**
     * Crea los directorios y archivos necesarios para el nuevo usuario.
     *
     * @param user el usuario para el cual se crearán los directorios y archivos.
     * @return true si se crearon los directorios y archivos correctamente, false en caso contrario.
     */
    private boolean crearFicherosUsuario(Usuario user) {
        //Crea el directorio principal del usuario.
		if(user.getDirectorio().mkdir()) {
            File dirLog = new File(user.getDirectorio().getName() + "\\" + Constants.FOLDER_LOG);
            File dirReport = new File(user.getDirectorio().getName() + "\\" + Constants.FOLDER_REPORTS);
            
            // Crea el directorio donde se guardarán los archivos de registro (log) del nuevo usuario.
            if (!dirLog.mkdir()) {
                loggerRoot.warning("No se ha creado el directirio log del nuevo usuario.");
                return false;
            }

            // Crea el directorio donde se guardarán los archivos de informes (report) del nuevo usuario.
            if (!dirReport.mkdir()) {
                loggerRoot.warning("No se ha creado el directirio report del nuevo usuario.");
                return false;
            }
        } else {
            loggerRoot.warning("No se ha creado el directirio del nuevo usuario.");
        }

		conexionBD.setUsuario(user);
		File ficheroBD = new File(user.getDirectorio().getName() + "\\" + user.getNombreUsuario().toLowerCase() + conexionBD.FINAL_NOMBRE_FICHERO_DB);
		
        //Intenta crear el fichero de BD y las las tablas.
		if(!ficheroBD.exists()) {
			try {
				conexionBD.crearTablasUsuario();
                return true;
			} catch (SQLException e) {
				loggerRoot.severe("Excepción: " + e.toString());
				e.printStackTrace();
			} catch (Exception e) {
                loggerRoot.severe("Excepción: " + e.toString());
                e.printStackTrace();
            }
		}
		return false;
	}


    /**
     * comprueba si el 'File' pasado como parámetro es un directorio.
     * Si lo es, obtiene la lista de ficheros del directorio y los borra de forma recursiva. 
     * Después, borra el directorio en sí. Si el File no es un directorio, simplemente lo borra.
     * 
     * @param directorioUser el directorio que se borrará.
     * @return true si el directorio y todos los archivos y subdirectorios se borraron correctamente,
     *         false en caso contrario.
     */
    private boolean borrarFicherosUsuario(File directorioUser) {
        if (directorioUser.isDirectory()) {
            File[] ficheros = directorioUser.listFiles(); // Obtenemos la lista de ficheros del directorio

            // Borramos los ficheros del directorio de forma recursiva
            for (File fichero : ficheros) {
                borrarFicherosUsuario(fichero);
            }
        }

        // Borramos el directorio
        return directorioUser.delete();
    }


    /**
     * Muestra una ventana de dialogo con la informacion pasada como parametros.
     * 
     * @param tipo Tipo de alerta.
     * @param tiutlo Titulo de la ventana.
     * @param cabecera Cabecera del mensaje.
     * @param cuerpo Cuerpo del menesaje.
     */
    private void mensajeAviso(AlertType tipo, String tiutlo, String cabecera, String cuerpo) {
        Alert alerta = new Alert(tipo);
        alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
        alerta.setTitle(tiutlo);
        alerta.initOwner((Stage) apRegistroUsuario.getScene().getWindow());
        alerta.setHeaderText(cabecera);
        alerta.setContentText(cuerpo);
        alerta.initStyle(StageStyle.DECORATED);
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.showAndWait();
    }


    /**
     * Establece el usuario root para el controlador.
     *
     * @param usuarioRoot el usuario root a establecer.
     */
    public void setUsuarioRoot(Usuario usuarioRoot) {
        this.usuarioRoot = usuarioRoot;
        conexionBD.setUsuario(usuarioRoot);
    }
}
