package controlador;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

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
import javafx.fxml.Initializable;

public class UsuarioCardBorrarControlador implements Initializable {

    private double x, y;
    private ConexionBD conexionBD;
    private Toast toast;
    private Alert alerta;
    private Usuario usuario;
    private Usuario usuarioRoot;
    private Stage escenario;
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
        conexionBD = ConexionBD.getInstance();
        toast = new Toast();
        Image imagenDelete;
        Image bin;
        try {
            imagenDelete = new Image("/recursosusuario_delete_1_128.png");
            bin = new Image("/recursos/papelera_1_128.png");
        } catch (Exception e) {
            imagenDelete = new Image(getClass().getResourceAsStream("/recursos/usuario_delete_1_128.png"));
            bin = new Image(getClass().getResourceAsStream("/recursos/papelera_1_128.png"));
        }
        ivImagenUser.setImage(imagenDelete);
        ivPapelera.setImage(bin);

        apBorrarUsuario.setOnMousePressed(mouseEvent -> {
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });

        apBorrarUsuario.setOnMouseDragged(mouseEvent -> {
            escenario.setX(mouseEvent.getScreenX() - x);
            escenario.setY(mouseEvent.getScreenY() - y);
        });

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
    }

    @FXML
    void cerrarVentana(MouseEvent event) {
        escenario.close();
    }

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
            alerta.initOwner(escenario);

            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
            ButtonType buttonTypeConfirmar = new ButtonType("Confirmar", ButtonData.YES);
            alerta.getButtonTypes().setAll(buttonTypeConfirmar, buttonTypeCancel);
            Optional<ButtonType> result = alerta.showAndWait();

            // Si pulsamos el boton confirmar:
            if (result.get() == buttonTypeConfirmar) {

                // LLamo al metodo para borrar todo el contenido del directorio del usuario.
                if (borrarFicherosUsuario(usuario.getDirectorio())) {
                    conexionBD.setUsuario(usuarioRoot);
                    try {
                        // Borro los datos de usuario de la base de datos de la aplición.
                        if (!conexionBD.borrarUsuario(usuario.getId())) {
                            // Poner mensaje de error;
                        }
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    controladorPincipal.cerrarSesion();
                    escenario.close();
                } else {
                    // MENSAJE NO SE HA PODIDO BORRAR LOS FICHEROS DE USUARIO.
                }
            }
        }
    }

    private boolean borrarFicherosUsuario(File fichero) {
        for(File f : fichero.listFiles()) {
            System.out.println(f.getName());
            if(f.isDirectory()) {
                borrarFicherosUsuario(f);
            } else {
                f.delete();
            }
        }
        
        if(fichero.delete()) {
            System.out.println("Borrado directorio");
            return true;
        } else {
            System.out.println("No se ha borrado el directorio.");
            return false;
        }
    }

    
    private boolean comporbarPassword() {
        if(usuario.getPassword().equals(password.get())) {
            //toast.show(escenario, "El password de Usuario correcto!!.");
            return true;
        } else if(password.get().isBlank()) {
            toast.show((Stage) apBorrarUsuario.getScene().getWindow(), "El campo Password esta vacío.!!.");
            return false;
        } else {
            toast.show((Stage) apBorrarUsuario.getScene().getWindow(), "El password de Usuario introducido\nes difente al password de Usuario!!.");
            return false;
        }
    }

    private void iniciar() {
        lbIdUsuario.setText(Integer.toString(usuario.getId()));
        lbNombreUsuario.setText(usuario.getNombreUsuario());
    }

    /**
	 * Etablece el usuario que esta usando la aplicación.
	 * @param usuario
	 */
	public void setUsuarioActual(Usuario usuarioActual) {
		this.usuario = usuarioActual;
        iniciar();
	}

    /**
     * Establece el usuarioRoot para este controlador.
     * @param usuarioRoot El usuarioRoot
     */
    public void setUsuarioRoot(Usuario usuarioRoot) {
        this.usuarioRoot = usuarioRoot;
    }

    /**
     * Establece un Stage para este controlador.
     * 
     * @param stage Stage que se establece.
     */
    public void setStage(Stage stage) {
    	this.escenario = stage;
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
