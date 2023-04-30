package controlador;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import baseDatos.ConexionBD;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import modelo.Toast;
import javafx.fxml.Initializable;

public class LoginControlador implements Initializable {

    String[] usuario = new String[2]; //[0]nombre, [1]password
    ConexionBD conexionBD;
    private PrincipalControlador controladorPincipal;
    private Toast toast;


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
        conexionBD = ConexionBD.getInstance();
        toast = new Toast();
        
    }

    @FXML
    void acceder(MouseEvent event) {
        if(recuperarDatosCampos() && comprobarUsuario()) {
            toast.show((Stage) bdLogin.getScene().getWindow(), "Usuario Logueado!!.");
            controladorPincipal.setUsuario(usuario);
        }
        
    }

    

    @FXML
    void recuperarPassword(MouseEvent event) {

    }

    @FXML
    void registrarse(MouseEvent event) {

    }
    
    private boolean recuperarDatosCampos() {
        boolean camposOK = false;
        if (tfUsuario.getText().isBlank()) {
            toast.show((Stage) bdLogin.getScene().getWindow(), "El campo Nombre está vacío!!.");
        } else {
            usuario[0] = tfUsuario.getText();
            camposOK = true;
        }

        if(camposOK && pfPassword.getText().isBlank()) {
            toast.show((Stage) bdLogin.getScene().getWindow(), "El campo password está vacío!!.");
            camposOK = false;
        } else {
            usuario[1] = pfPassword.getText();
        }

        return camposOK;
    }


    private boolean comprobarUsuario() {
        conexionBD.setUsuario(new File(controladorPincipal.getUsuarioApp()[2]), controladorPincipal.getUsuarioApp()[0], controladorPincipal.getUsuarioApp()[0]);
        try {
            if(!conexionBD.comprobarNombreUsuario(usuario[0])) {
                toast.show((Stage) bdLogin.getScene().getWindow(), "No hay ningun usuario registrado\ncon ese nombre!!.");
                return false;
            }
            
            if(!conexionBD.comprobarUsuario(usuario)) {
                toast.show((Stage) bdLogin.getScene().getWindow(), "El password es incorrecto!!.");
                return false;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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

}
