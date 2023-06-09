package controlador;

import java.net.URL;
import java.util.ResourceBundle;

import baseDatos.ConexionBD;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import modelo.Usuario;
import javafx.scene.control.Button;

public class InicioControlador implements Initializable{

    private Usuario usuarioActual;
    private Usuario usuarioApp;
    private ConexionBD conexionBD;
    private PrincipalControlador controladorPincipal;

    
    @FXML
    private ImageView ivCarpetaConfig;
    
    @FXML
    private BorderPane bpInicio;
    
    @FXML
    private Button btnCerrarSesion;

    @FXML
    private Label lbSaludo;

    @FXML
    private Label lbApellido1;

    @FXML
    private Label lbApellido2;

    @FXML
    private Label lbEmail;

    @FXML
    private Label lbEmailApp;

    @FXML
    private Label lbNombre;

    @FXML
    private Label lbNombreUsuario;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	//Cargar imagenes en ImageView.
        Image imagenCarpetaConfig;
        try {
        	imagenCarpetaConfig = new Image("/recursos/carpeta_lila_2_128.png");
        } catch (Exception e) {
        	imagenCarpetaConfig = new Image(getClass().getResourceAsStream("/recursos/carpeta_lila_2_128.png"));
        }
        ivCarpetaConfig.setImage(imagenCarpetaConfig);
    	
    	bpInicio.getStyleClass().add("fondo_border_pane");
    	
    }


    @FXML
    void cerrarSesion(MouseEvent event) {
        controladorPincipal.cerrarSesion();
    }

     @FXML
    void cargarVistaUsuario(MouseEvent event) {
        controladorPincipal.menuUsuario(null);
    }


    /**
	 * Establece para este controlador, el controlador principal de la aplicacion.
	 * 
	 * @param principal Controlador principal.
	 */
	public void setControladorPrincipal(PrincipalControlador principal) {
		controladorPincipal = principal;
	}


    /**
	 * Etablece el usuario que esta logueado en la aplicación.
     * 
	 * @param usuario el usuario que esta logueado en la aplicación.
	 */
	public void setUsuarioActual(Usuario usuario) {
		this.usuarioActual = usuario;

        lbNombreUsuario.textProperty().bind(usuarioActual.nombreUsuarioProperty());
        lbNombre.textProperty().bind(usuarioActual.nombreProperty());
        lbApellido1.textProperty().bind(usuarioActual.apellido1Property());
        lbApellido2.textProperty().bind(usuarioActual.apellido2Property());
        lbEmail.textProperty().bind(usuarioActual.emailProperty());
        lbEmailApp.textProperty().bind(usuarioActual.emailAppProperty());
	}


    /**
     * Establece el usuarioApp para este controlador.
     * @param usuarioApp El usuarioApp
     */
    public void setUsuarioApp(Usuario usuarioApp) {
        this.usuarioApp = usuarioApp;
    }


    
}
