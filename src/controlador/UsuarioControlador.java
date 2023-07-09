package controlador;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import baseDatos.ConexionBD;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modelo.Toast;
import modelo.Usuario;
import utilidades.Constants;
import javafx.fxml.Initializable;

public class UsuarioControlador implements Initializable {

    private Logger logUser;
    private ConexionBD conexionBD;
    private Usuario usuario;
    private Usuario usuarioRoot;
    private PrincipalControlador controladorPincipal;
    private Toast toast;
    private Alert alerta;
    private Stage escenario;

    @FXML
    private AnchorPane apBotones;

    @FXML
    private AnchorPane apDatosPersonales;

    @FXML
    private AnchorPane apDatosUsuario;

    @FXML
    private AnchorPane apEmailApp;

    @FXML
    private Button btnCambiarPassword;

    @FXML
    private Button btnCambiarPasswordApp;

    @FXML
    private Button btnEditarPersona;

    @FXML
    private CheckBox chbPasswordAppVisible;

    @FXML
    private CheckBox chbPasswordVisible;

    @FXML
    private ImageView ivBorrarUsuario;

    @FXML
    private Label lbApellido1;

    @FXML
    private Label lbApellido2;

    @FXML
    private Label lbEmail;

    @FXML
    private Label lbEmailApp;

    @FXML
    private Label lbId;

    @FXML
    private Label lbNombre;

    @FXML
    private Label lbNombreDirectorio;

    @FXML
    private Label lbNombreUsuario;

    @FXML
    private Label lbPassword;

    @FXML
    private Label lbPasswordApp;

    @FXML
    private Label lbRutaDirectorio;

    @FXML
    private Label lbTelefono;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Establecer imagen en ImageView.
        Image imagenBorrarUsuario;
        try {
            //Forma desde IDE y JAR.
        	imagenBorrarUsuario = new Image(getClass().getResourceAsStream("/recursos/boton_2_64.png"));
        } catch (Exception e) {
            //Forma desde el JAR.
        	imagenBorrarUsuario = new Image("/recursos/circulo_flecha_1.png");
        	
        }
        ivBorrarUsuario.setImage(imagenBorrarUsuario); 

        logUser = Logger.getLogger(Constants.USER); //Crea una instancia de la clase Logger asociada al nombre de registro.
        conexionBD = ConexionBD.getInstance();
    }

    @FXML
    void borrarUsuario(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/usuarioCardBorrarVista.fxml"));
            AnchorPane borrarUsuario;
            borrarUsuario = (AnchorPane) loader.load();
            UsuarioCardBorrarControlador controller = loader.getController(); // cargo el controlador.
            
            Stage ventana= new Stage();
            ventana.initOwner(escenario);
            ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
            ventana.initStyle(StageStyle.UNDECORATED);

            URL rutaIcono = getClass().getResource("/recursos/lis_logo_1.png"); // guardar ruta de recurso imagen.
            ventana.getIcons().add(new Image(rutaIcono.toString())); // poner imagen icono a la ventana.
            
            controller.setUsuarioActual(usuario);
            controller.setUsuarioRoot(usuarioRoot);
            controller.setControladorPrincipal(controladorPincipal);

            Scene scene = new Scene(borrarUsuario);
            scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); //Añade hoja de estilos.
            ventana.setScene(scene);
            ventana.showAndWait();
        } catch (IOException e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            logUser.severe("Excepción: " + e.toString());
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

    @FXML
    void cambiarPassword(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/cambioPasswordVista.fxml"));
            AnchorPane cambioPassword;
            cambioPassword = (AnchorPane) loader.load();
            CambioPasswordControlador controller = loader.getController(); // cargo el controlador.
            
            Stage ventana= new Stage();
            ventana.initOwner(escenario);
            ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
            ventana.initStyle(StageStyle.UNDECORATED);
            
            controller.setStage(ventana);
            controller.setUsuarioActual(usuario);
            controller.setUsuarioRoot(usuarioRoot);

            Scene scene = new Scene(cambioPassword);
            scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); //Añade hoja de estilos.
            ventana.setScene(scene);
            ventana.showAndWait();
        } catch (IOException e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            logUser.severe("Excepción: " + e.toString());
        }
    }

    @FXML
    void cambiarPasswordApp(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/usuarioFormEmailVista.fxml"));
            AnchorPane formEmailApp;
            formEmailApp = (AnchorPane) loader.load();
            UsuarioFormEmailControlador controller = loader.getController(); // cargo el controlador.
            
            Stage ventana= new Stage();
            ventana.initOwner(escenario);
            ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
            ventana.initStyle(StageStyle.UNDECORATED);

            controller.setUsuario(usuario);

            Scene scene = new Scene(formEmailApp);
            scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); //Añade hoja de estilos.
            ventana.setScene(scene);
            ventana.showAndWait();
        } catch (IOException e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            logUser.severe("Excepción: " + e.toString());
        }
    }

    @FXML
    void editarDatosPersona(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/usuarioFormVista.fxml"));
            GridPane formUsuario;
            formUsuario = (GridPane) loader.load();
            UsuarioFormControlador controller = loader.getController(); // cargo el controlador.
            
            Stage ventana= new Stage();
            ventana.initOwner(escenario);
            ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
            ventana.initStyle(StageStyle.DECORATED);

            URL rutaIcono = getClass().getResource("/recursos/lis_logo_1.png"); // guardar ruta de recurso imagen.
            ventana.getIcons().add(new Image(rutaIcono.toString())); // poner imagen icono a la ventana.

            controller.setUsuario(usuario);

            Scene scene = new Scene(formUsuario);
            scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); //Añade hoja de estilos.
            ventana.setScene(scene);
            ventana.setTitle("Editar Usuario");
            ventana.showAndWait();
        } catch (IOException e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            logUser.severe("Excepción: " + e.toString());
        }
    }

    public void iniciar() {   
        //Binding Datos Usuario
        lbId.setText(Integer.toString(usuario.getId()));
        lbNombreUsuario.setText(usuario.getNombreUsuario());
        lbNombreDirectorio.setText(usuario.getDirectorio().getName());
        lbRutaDirectorio.setText(usuario.getDirectorio().getAbsolutePath());

        lbPassword.textProperty().bind(Bindings.when(chbPasswordVisible.selectedProperty())
        .then(usuario.passwordProperty())
        .otherwise(ocultarContraseña(usuario.passwordProperty()))); 

        //Binding Datos Personales
        lbNombre.textProperty().bind(usuario.nombreProperty());
        lbApellido1.textProperty().bind(usuario.apellido1Property());
        lbApellido2.textProperty().bind(usuario.apellido2Property());
        lbTelefono.textProperty().bind(usuario.telefonoProperty().asString());
        lbEmail.textProperty().bind(usuario.emailProperty());

        //Binding Email Aplicacion
        lbEmailApp.textProperty().bind(usuario.emailAppProperty());
        
        //Si todavia no se ha asignado password al Email, se inicia vacion para que no de excepción.
        if(usuario.passwordEmailAppProperty().getValue() == null) usuario.setPasswordEmailApp("");

        lbPasswordApp.textProperty().bind(Bindings.when(chbPasswordAppVisible.selectedProperty())
        .then(usuario.passwordEmailAppProperty())
        .otherwise(ocultarContraseña(usuario.passwordEmailAppProperty()))); 

    }


    /**
     * Crea un StringProperty que contiene una versión oculta de la contraseña.
     *
     * @param contraseñaProperty el StringProperty de la contraseña original.
     * @return el StringProperty de la contraseña oculta.
     */
    private StringProperty ocultarContraseña(StringProperty contraseñaProperty) {
        StringProperty contraseñaOcultaProperty = new SimpleStringProperty();

        contraseñaOcultaProperty.bind(Bindings.createStringBinding(() -> {
            String contraseña = contraseñaProperty.get();
            
            StringBuilder oculta = new StringBuilder();
            for (int i = 0; i < contraseña.length(); i++) {
                oculta.append("*");
            }
            return oculta.toString();
        }, contraseñaProperty));

        return contraseñaOcultaProperty;
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
     * @param s Stage que se establece.
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
