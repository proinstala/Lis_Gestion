package controlador;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Handler;
import java.util.logging.Logger;

import baseDatos.ConexionBD;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modelo.Toast;
import modelo.Usuario;
import utilidades.Constants;
import javafx.fxml.Initializable;

public class UsuarioCardBorrarControlador implements Initializable {

    private double x, y;
    private Logger logRoot;
    private ConexionBD conexionBD;
    private Toast toast;
    private Alert alerta;
    private Usuario usuario;
    private Usuario usuarioRoot;
    private StringProperty password;
    private PrincipalControlador controladorPincipal;

    @FXML
    private AnchorPane apBorrarUsuario;

    @FXML
    private AnchorPane apIzquierda;

    @FXML
    private Button btnAceptar;

    @FXML
    private Button btnCancelar;

    @FXML
    private CheckBox chbVisible;

    @FXML
    private ImageView ivPapelera;

    @FXML
    private ImageView ivImagenUser;

    @FXML
    private Label lbIdUsuario;

    @FXML
    private Label lbNombreUsuario;

    @FXML
    private PasswordField tfPassword;

    @FXML
    private TextField tfPasswordVisible;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Añadir clases de estilo CSS a elementos.
        btnCancelar.getStyleClass().add("boton_rojo");

        //Establecer imagen en ImageView.
        Image imagenDelete;
        Image bin;
        try {
            //Intentar cargar la imagen desde el recurso en el IDE y en el JAR.
            imagenDelete = new Image(getClass().getResourceAsStream("/recursos/usuario_delete_1_128.png"));
            bin = new Image(getClass().getResourceAsStream("/recursos/papelera_1_128.png"));
        } catch (Exception e) {
            //Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR.
            imagenDelete = new Image("/recursosusuario_delete_1_128.png");
            bin = new Image("/recursos/papelera_1_128.png");
            
        }
        //Establecer las imagenes cargadas en los ImageView.
        ivImagenUser.setImage(imagenDelete);
        ivPapelera.setImage(bin);

        //Configurar el evento cuando se presiona el ratón en el panel apBorrarUsuario.
        apBorrarUsuario.setOnMousePressed(mouseEvent -> {
            //Obtener las coordenadas X e Y del ratón en relación con la escena.
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });

        //Configurar el evento cuando se arrastra el ratón en el panel apBorrarUsuario.
        apBorrarUsuario.setOnMouseDragged(mouseEvent -> {
            //Obtener la referencia al Stage actual y establecer las nuevas coordenadas X e Y.
            ((Stage) apBorrarUsuario.getScene().getWindow()).setX(mouseEvent.getScreenX() - x);
            ((Stage) apBorrarUsuario.getScene().getWindow()).setY(mouseEvent.getScreenY() - y);
        });

        logRoot = Logger.getLogger(Constants.USER_ROOT);    //Crea una instancia de la clase Logger asociada al nombre de registro.
        conexionBD = ConexionBD.getInstance();              //Obtener una instancia de la clase ConexionBD utilizando el patrón Singleton.
        toast = new Toast();

        //Inicializa los StringProperty
        password = new SimpleStringProperty("");

        //Bindea los Texfield con los StringProperty.
        tfPassword.textProperty().bindBidirectional(password);
        tfPasswordVisible.textProperty().bindBidirectional(password);

        //Pone oculto los TextFiel que muestran los password.
        tfPasswordVisible.setVisible(false);

        //Añade listener al CheckBox que oculta y hace visible a los TextFiel y PasswordFiel.
        chbVisible.selectedProperty().addListener((o, ov, nv) -> {
            if(chbVisible.isSelected()) {
                tfPassword.setVisible(false);
                tfPasswordVisible.setVisible(true);
            } else {
                tfPassword.setVisible(true);
                tfPasswordVisible.setVisible(false);
            }
        });

        //Configurar un evento de clic del ratón para el botón "Cancelar".
        btnCancelar.setOnMouseClicked(e -> {
            ((Stage) apBorrarUsuario.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
        });
    }


    /**
     * Método para borrar un usuario.
     * 
     * @param event El evento de clic del ratón.
     */
    @FXML
    void borrarUsuario(MouseEvent event) {
        if (comporbarPassword()) {
            alerta = new Alert(AlertType.CONFIRMATION);
            alerta.getDialogPane().getStylesheets()
                    .add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
            alerta.setTitle("Borrar Usuario");
            alerta.setHeaderText("Esta Acción es irreversible.\n\nSi no estas seguro de si quieres BORRAR el usuario,\nhaz cliz en el botón Cancelar.");
            alerta.setContentText("¿Estas seguro de que quieres borrar al Usuario?");
            alerta.initStyle(StageStyle.DECORATED);
            alerta.initOwner((Stage) apBorrarUsuario.getScene().getWindow());

            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
            ButtonType buttonTypeConfirmar = new ButtonType("Confirmar", ButtonData.YES);
            alerta.getButtonTypes().setAll(buttonTypeConfirmar, buttonTypeCancel);
            Optional<ButtonType> result = alerta.showAndWait();

            // Si pulsamos el boton confirmar:
            if (result.get() == buttonTypeConfirmar) {

                //Cierra el log asociado al usuario.
                Logger logUser = Logger.getLogger(Constants.USER); //Crea una instancia de la clase Logger asociada al nombre de registro.
                //cierra cada uno de los controladores y detiene la emisión de registros a través de ellos.
                for (Handler h : logUser.getHandlers()) {
                    h.close();
                }

                // LLamo al metodo para borrar todo el contenido del directorio del usuario.
                if (borrarFicherosUsuario(usuario.getDirectorio())) {
                    logRoot.info("Borrado ficheros de usuario. (id: " + usuario.getId() + ", nombre: " + usuario.getNombreUsuario() + ")");
                    conexionBD.setUsuario(usuarioRoot);
                    try {
                        // Borro los datos de usuario de la base de datos de la aplición.
                        if (!conexionBD.borrarUsuario(usuario.getId())) {
                            logUser.warning("Fallo al intentar borrar Usuario de la BD. (id: " + usuario.getId() + ", nombre: " + usuario.getNombreUsuario() + ")");
                        }
                    } catch (SQLException e) {
                        logRoot.severe("Excepción: " + e.toString());
                        e.printStackTrace();
                    } catch (Exception e) {
                        logRoot.severe("Excepción: " + e.toString());
                        e.printStackTrace();
                    }
                    controladorPincipal.cerrarSesion();
                    ((Stage) apBorrarUsuario.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
                } else {
                    logUser.warning("Fallo al intentar borrar ficheros de Usuario. (id: " + usuario.getId() + ", nombre: " + usuario.getNombreUsuario() + ")");
                }
            }
        }
    }


    /**
     * Elimina de forma recursiva todos los archivos y directorios dentro del directorio especificado
     * y luego elimina el directorio principal.
     *
     * @param fichero El directorio a borrar.
     * @return true si se eliminó el directorio con éxito, false en caso contrario.
     */
    private boolean borrarFicherosUsuario(File fichero) {
        String nombreFichero;

        //Iterar sobre los archivos y directorios dentro de 'fichero'.
        for(File f : fichero.listFiles()) {
            nombreFichero = f.getName();
            logRoot.config("Borrar fichero: " + nombreFichero);

            //Si es un directorio, llamar recursivamente a 'borrarFicherosUsuario' para eliminar su contenido.
            if(f.isDirectory()) {
                borrarFicherosUsuario(f);
            } else {
                //Si es un archivo, eliminarlo.
                if(f.delete()) {
                    logRoot.config("Borrar Archivo: " + nombreFichero);
                } else {
                    logRoot.warning("Fallo al borrar Archivo: " + nombreFichero);
                }
            }
        }
        
        nombreFichero = fichero.getName();

        //Eliminar el directorio 'fichero' después de eliminar todos los archivos y directorios dentro de él.
        if(fichero.delete()) {
            logRoot.config("Borrar directorio: " + nombreFichero);
            return true;
        } else {
            logRoot.warning("Fallo al borrar directorio: " + nombreFichero);
            return false;
        }
    }

    
    /**
     * Comprueba si la contraseña ingresada coincide con la contraseña del usuario.
     *
     * @return true si la contraseña es correcta, false en caso contrario.
     */
    private boolean comporbarPassword() {
        if(usuario.getPassword().equals(password.get())) {
            return true;
        } else if(password.get().isBlank()) {
            toast.show((Stage) apBorrarUsuario.getScene().getWindow(), "El campo Password esta vacío.!!.");
            return false;
        } else {
            toast.show((Stage) apBorrarUsuario.getScene().getWindow(), "El password de Usuario introducido\nes difente al password de Usuario!!.");
            return false;
        }
    }


    /**
     * Establece el usuario actual y actualiza los elementos de la interfaz de usuario correspondientes.
     *
     * @param usuarioActual El objeto Usuario que representa al usuario actual.
     */
	public void setUsuarioActual(Usuario usuarioActual) {
		this.usuario = usuarioActual;
        lbIdUsuario.setText(Integer.toString(usuario.getId()));
        lbNombreUsuario.setText(usuario.getNombreUsuario());
	}


    /**
     * Establece el usuarioRoot para este controlador.
     * @param usuarioRoot El usuarioRoot
     */
    public void setUsuarioRoot(Usuario usuarioRoot) {
        this.usuarioRoot = usuarioRoot;
    }


    /**
	 * Establece para este controlador, el controlador principal de la aplicacion.
	 * 
	 * @param principal Controlador principal.
	 */
	public void setControladorPrincipal(PrincipalControlador principal) {
		controladorPincipal = principal;
	}
    
}
