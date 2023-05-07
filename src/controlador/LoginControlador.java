package controlador;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import baseDatos.ConexionBD;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modelo.Toast;
import modelo.Usuario;
import javafx.fxml.Initializable;

public class LoginControlador implements Initializable {

    private String[] camposLogin = new String[2]; //[0]nombre, [1]password
    private Usuario usuario;
    private Usuario usuarioApp;
    private ConexionBD conexionBD;
    private PrincipalControlador controladorPincipal;
    private Toast toast;
    private Stage escenario;


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
            try {
                usuario = conexionBD.getUsuario(camposLogin);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(usuario == null) {
                toast.show((Stage) bdLogin.getScene().getWindow(), "Algo a salido mal!!.");
            } else {
                toast.show((Stage) bdLogin.getScene().getWindow(), "Usuario Logueado!!.");
                controladorPincipal.setUsuario(usuario);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/inicioVista.fxml"));
				BorderPane inicio;
				
				try {
                    inicio = (BorderPane) loader.load();
                    controladorPincipal.setPane(inicio);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
				//bpPrincipal.setCenter(jornadaPilates);
            }
        }
    }

    

    @FXML
    void recuperarPassword(MouseEvent event) {
        //ESTO ESTA DE PRUEBA PARA VER COMO QUEDA LA VENTANA
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/registroUsuarioVista.fxml"));
		    GridPane registroUsuario;
            registroUsuario = (GridPane) loader.load();
            RegistroUsuarioControlador controller = loader.getController(); // cargo el controlador.
            
            Stage ventana= new Stage();
            ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
            ventana.initStyle(StageStyle.UNDECORATED);
            
            
            controller.setStage(ventana);

            Scene scene = new Scene(registroUsuario);
            //scene.getStylesheets().add(getClass().getResource("/estilos/Styles.css").toExternalForm()); //Añade hoja de estilos.
            ventana.setScene(scene);
            //ventana.setTitle("titulo de ventana");
            ventana.showAndWait();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		//bpPrincipal.setCenter(login);
		
		//controller.setControladorPrincipal(this);
		//controller.setUsuarioApp(usuarioApp);
    }

    @FXML
    void registrarse(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/registroUserVista.fxml"));
		    AnchorPane registroUser;
            registroUser = (AnchorPane) loader.load();
            RegistroUserControlador controller = loader.getController(); // cargo el controlador.
            
            Stage ventana= new Stage();
            ventana.initOwner(escenario);
            ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
            ventana.initStyle(StageStyle.UNDECORATED);
            
            controller.setStage(ventana);
            controller.setUsuarioApp(usuarioApp);

            Scene scene = new Scene(registroUser);
            //scene.getStylesheets().add(getClass().getResource("/estilos/Styles.css").toExternalForm()); //Añade hoja de estilos.
            ventana.setScene(scene);
            //ventana.setTitle("titulo de ventana");
            ventana.showAndWait();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
        //conexionBD.setUsuario(new File(controladorPincipal.getUsuarioApp()[2]), controladorPincipal.getUsuarioApp()[0], controladorPincipal.getUsuarioApp()[0]);
        conexionBD.setUsuario(usuarioApp);
        try {
            if(!conexionBD.comprobarNombreUsuario(camposLogin[0])) {
                toast.show((Stage) bdLogin.getScene().getWindow(), "No hay ningun usuario registrado\ncon ese nombre!!.");
                return false;
            }
            
            if(!conexionBD.comprobarUsuario(camposLogin)) {
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


    public void setUsuarioApp(Usuario u) {
        usuarioApp = u;
    }

    /**
     * Establece un Stage para este controlador.
     * 
     * @param s Stage que se establece.
     */
    public void setStage(Stage stage) {
    	this.escenario = stage;
    }

}
