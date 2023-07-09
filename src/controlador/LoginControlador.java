package controlador;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import baseDatos.ConexionBD;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modelo.Toast;
import modelo.Usuario;
import utilidades.Constants;
import javafx.fxml.Initializable;

public class LoginControlador implements Initializable {

    private String[] camposLogin = new String[2]; //[0]nombre, [1]password
    private Usuario usuario;
    private Usuario usuarioRoot;
    private Logger loggerRoot;
    private ConexionBD conexionBD;
    private PrincipalControlador controladorPincipal;
    private Toast toast;

    
    @FXML
    private Label lbPasswordLost;

    @FXML
    private ImageView ivImagenLogin;

    @FXML
    private ImageView ivImagenLogo;

    @FXML
    private BorderPane bdLogin;

    @FXML
    private Button btnAcceder;

    @FXML
    private PasswordField pfPassword;

    @FXML
    private TextField tfUsuario;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Cargar imagenes en ImageView.
        Image imagenLogin;
        Image imagenLogo;
        try {
            //Intentar cargar la imagen desde el recurso en el IDE y en el JAR.
            imagenLogin = new Image(getClass().getResourceAsStream("/recursos/user_1_512.png")); //Forma desde IDE y JAR.
            imagenLogo = new Image(getClass().getResourceAsStream("/recursos/logo_nuevo_letras_color.png"));
        } catch (Exception e) {
            //Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR.
            imagenLogin = new Image("/recursos/user_1_512.png"); //Forma desde el JAR.
            imagenLogo = new Image("/recursos/logo_nuevo_letras_color.png");
        }
        //Establecer las imagenes cargadas en los ImageView.
        ivImagenLogin.setImage(imagenLogin);
        ivImagenLogo.setImage(imagenLogo);

        loggerRoot = Logger.getLogger(Constants.USER_ROOT); //Crea una instancia de la clase Logger asociada al nombre de registro.
        conexionBD = ConexionBD.getInstance();
        toast = new Toast();

        //olvidado password.
        lbPasswordLost.setDisable(true); //EN CONSTRUCCION. deshabilitado
        lbPasswordLost.setVisible(false);//No visible.
    }

    @FXML
    void acceder(MouseEvent event) {
        if(recuperarDatosCampos() && comprobarUsuario()) {
            boolean logueoOK = false;
            try {
                usuario = conexionBD.getUsuario(camposLogin);
                conexionBD.setUsuario(usuario);
                if(!conexionBD.getDatosUsuario(usuario)) {
                    conexionBD.insertarDatosUsuario(usuario);
                }
                logueoOK = true;
            } catch (SQLException e) {
                loggerRoot.severe("Excepción: " + e.toString());
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                loggerRoot.severe("Excepción: " + e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                loggerRoot.severe("Excepción: " + e.toString());
            }

            if(logueoOK && usuario != null) {
                toast.show((Stage) bdLogin.getScene().getWindow(), "Usuario Logueado!!.");
                controladorPincipal.iniciarSesion(usuario);
                loggerRoot.info("Usuario logueado en la aplicación. (id: " + usuario.getId() + ", nombre: " + usuario.getNombreUsuario() + ").");
            } else {
                toast.show((Stage) bdLogin.getScene().getWindow(), "Algo a salido mal!!.");
                loggerRoot.severe("Error: fallo al intentar acceder con usuario " + camposLogin[0]);
            }
        }
    }


    @FXML
    void recuperarPassword(MouseEvent event) {

        //El label para acceder a esta parte esta deshabilitado y no visible en el metodo initialize.
        /* 
        if(escenario == null) {
            setStage(controladorPincipal.getStage());
        }*/

        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
        alerta.setTitle("Recuperar password");
        alerta.setContentText("En construcción.");
        alerta.initStyle(StageStyle.DECORATED);
        //alerta.initOwner(escenario);
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.showAndWait();
        
    }


    @FXML
    void registrarse(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/registroUserVista.fxml"));
		    AnchorPane registroUser;
            registroUser = (AnchorPane) loader.load();
            RegistroUserControlador controller = loader.getController(); // cargo el controlador.
            
            Stage ventana= new Stage();
            //ventana.initOwner(escenario);
            ventana.initOwner((Stage) bdLogin.getScene().getWindow());
            ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
            ventana.initStyle(StageStyle.UNDECORATED);

            URL rutaIcono = getClass().getResource("/recursos/lis_logo_1.png"); // guardar ruta de recurso imagen.
            ventana.getIcons().add(new Image(rutaIcono.toString())); // poner imagen icono a la ventana.
                
            controller.setUsuarioRoot(usuarioRoot);

            Scene scene = new Scene(registroUser);
            scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); //Añade hoja de estilos.
            ventana.setScene(scene);
            
            ventana.showAndWait();
        } catch (IOException e) {
            loggerRoot.severe("Excepción al intentar cargar la Scene registroUser. " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            loggerRoot.severe("Excepción: " + e.toString());
        }
    }
    
    private boolean recuperarDatosCampos() {
        boolean camposOK = false;
        if (tfUsuario.getText().isBlank()) {
            toast.show((Stage) bdLogin.getScene().getWindow(), "El campo Nombre está vacío!!.");
        } else {
            camposLogin[0] = tfUsuario.getText();
            camposOK = true;
        }

        if(camposOK && pfPassword.getText().isBlank()) {
            toast.show((Stage) bdLogin.getScene().getWindow(), "El campo password está vacío!!.");
            camposOK = false;
        } else {
            camposLogin[1] = pfPassword.getText();
        }

        return camposOK;
    }


    private boolean comprobarUsuario() {
        conexionBD.setUsuario(usuarioRoot);
        try {
            if(!conexionBD.comprobarNombreUsuario(camposLogin[0])) {
                toast.show((Stage) bdLogin.getScene().getWindow(), "No hay ningun usuario registrado\ncon ese nombre!!.");
                return false;
            }
            
            if(!conexionBD.comprobarUsuario(camposLogin)) {
                toast.show((Stage) bdLogin.getScene().getWindow(), "El password es incorrecto!!.");
                loggerRoot.info("Intento de acceso a usuario " + camposLogin[0] + " con password incorrecto.");
                return false;
            }

        } catch (SQLException e) {
            loggerRoot.severe("Excepción: " + e.toString());
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            loggerRoot.severe("Excepción: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            loggerRoot.severe("Excepción: " + e.toString());
        }
        return true;
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
     * Establece el usuarioRoot para este controlador.
     * 
     * @param usuarioRoot El usuarioRoot.
     */
    public void setUsuarioRoot(Usuario usuarioRoot) {
        this.usuarioRoot = usuarioRoot;
    }
}
