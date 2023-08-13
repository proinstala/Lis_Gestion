package controlador;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import modelo.Usuario;
import javafx.scene.control.Button;

public class InicioControlador implements Initializable {

    private Usuario usuarioActual;
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
        bpInicio.getStyleClass().add("fondo_border_pane"); //Añadir clases de estilo CSS a elementos.

    	//Cargar imagenes en ImageView.
        Image imagenCarpetaConfig;
        try {
            //Intentar cargar la imagen desde el recurso en el IDE y en el JAR.
            imagenCarpetaConfig = new Image(getClass().getResourceAsStream("/recursos/carpeta_lila_2_64.png")); //Forma desde IDE y JAR.
        } catch (Exception e) {
            //Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR.
        	imagenCarpetaConfig = new Image("/recursos/carpeta_lila_2_64.png"); //Forma desde el JAR.
        }
        ivCarpetaConfig.setImage(imagenCarpetaConfig); //Establecer las imagenes cargadas en los ImageView.

        //Configurar un evento de clic del ratón para la imagen "Configurarcion".
        ivCarpetaConfig.setOnMouseClicked(e -> {
            controladorPincipal.menuUsuario(null); //llama al método 'menuUsuario' del controlador principal.
        });

        //Configurar un evento de clic del ratón para el botón "Cerrar Sesion".
        btnCerrarSesion.setOnMouseClicked(e -> {
            controladorPincipal.cerrarSesion(); //llama al método 'cerrarSesion' del controlador principal.
        });
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
}
