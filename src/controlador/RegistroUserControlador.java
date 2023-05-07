package controlador;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import baseDatos.ConexionBD;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modelo.Toast;
import modelo.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class RegistroUserControlador implements Initializable {

    private double x, y;
    private Stage stage;
    private Stage escenario;
    private Alert alerta;
    private Toast toast;
    private Usuario usuario;
    private Usuario usuarioApp;
    private ConexionBD conexionBD;

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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        conexionBD = ConexionBD.getInstance();
        toast = new Toast();

        apRegistroUsuario.setOnMousePressed(mouseEvent -> {
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });

        apRegistroUsuario.setOnMouseDragged(mouseEvent -> {
            escenario.setX(mouseEvent.getScreenX() - x);
            escenario.setY(mouseEvent.getScreenY() - y);
        });

        //Stage stagePrimario= (Stage) pane.getScene().getWindow(); para probar
    }


    @FXML
    void cancelar(MouseEvent event) {
        escenario.close();
    }

    @FXML
    void registrarUsuario(MouseEvent event) {
        
        if(comprobarCampoNombre() && comprobarCamposPassword()) {
            usuario = new Usuario();
            usuario.setNombre(tfNombre.getText());
            usuario.setPassword(tfPassword.getText());
            usuario.setDirectorio(new File(tfNombre.getText()));
            usuario.setPasswordBD(tfPassword.getText());

            
            if(crearFicherosUsuario(usuario)) {
                boolean insertarUserOk = false;
                try {
                    conexionBD.setUsuario(usuarioApp);
                    insertarUserOk = conexionBD.insertarUsuario(usuario);
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if(insertarUserOk) {
                    alerta = new Alert(Alert.AlertType.INFORMATION);
                    alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); //Añade hoja de estilos.
			        alerta.setTitle("Registro de Usuario");
			        alerta.setHeaderText("");
			        alerta.setContentText("Registro de Usuario completado con exito.");
			        alerta.initStyle(StageStyle.UTILITY);
			        alerta.showAndWait();

                    escenario.close();
                } else {
                    borrarFicherosUsuario(usuario.getDirectorio());
                    toast.show((Stage) apRegistroUsuario.getScene().getWindow(), "No se ha podido insertar el usuario en la base de datos.!!.");
                }
                
            
            } else {
                toast.show((Stage) apRegistroUsuario.getScene().getWindow(), "No se ha podido crear los ficheros de usuario!!.");
            }


            toast.show((Stage) apRegistroUsuario.getScene().getWindow(), "Usuario Registrado!!."); //toast.show(escenario, "Usuario Registrado!!.");
        }
        //LLamar a los metodos de comprobar campos. Si son ok, crear el directorio y base de datos de usuario, despues insertar usuario en base de datos app.
        
    }
    

    private boolean comprobarCampoNombre() {
        String nombre = tfNombre.getText(); //Obtengo el contenido del TextField
        
        // compilamos la expresion regular.(Una letra mañuscula o minuscula o un numero del 0 al 9 de uno a 40 caracteres).
		//Pattern nombrePat = Pattern.compile("^[\\S]{1,40}$"); 
        Pattern nombrePat = Pattern.compile("^[^\\\\/:*?<>|\\s]{2,30}$"); //Comprobacion: cadena sin espacios ni caracteres \/:*?<>
        Matcher nombreMatch = nombrePat.matcher(nombre);

        if(!nombreMatch.matches()) {
            alerta = new Alert(Alert.AlertType.ERROR);
            alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); //Añade hoja de estilos.
			alerta.setTitle("Error nombre usuario");
			alerta.setHeaderText("");
			alerta.setContentText("El nombre de usuario tiene que contener de 2 a 30 caracteres.\n" + 
                                "No puede contener espacios en blanco ni los siguiente caracteres: \\ / : * ? < >");
			alerta.initStyle(StageStyle.UTILITY);
			alerta.showAndWait();
            return false;
        } else {
            try {
                if (conexionBD.comprobarNombreUsuario(nombre)) {
                    alerta = new Alert(Alert.AlertType.ERROR);
                    alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); //Añade hoja de estilos.
			        alerta.setTitle("Error nombre usuario");
			        alerta.setHeaderText("");
			        alerta.setContentText("El nombre de usuario \"" + nombre + "\" ya existe.\n" + 
                                        "Introduce un nuevo nombre para usuario.");
			        alerta.initStyle(StageStyle.UTILITY);
			        alerta.showAndWait();
                    return false;
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return true;
    }

    private boolean comprobarCamposPassword() {
        String pass = tfPassword.getText();
        String confirmarPass = tfConfirmacionPass.getText();

        // compilamos la expresion regular.(Una letra mañuscula o minuscula o un numero del 0 al 9 de 4 a 20 caracteres).
		Pattern passPat = Pattern.compile("^[\\S]{4,20}$");
        Matcher passMatch = passPat.matcher(pass);

        if(!passMatch.matches()) {
            alerta = new Alert(Alert.AlertType.ERROR);
            alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); //Añade hoja de estilos.
			alerta.setTitle("Error password");
			alerta.setHeaderText("");
			alerta.setContentText("El password de usuario tiene que contener de 4 a 20 caracteres.\n" + 
                                "No puede contener espacios en blanco.");
			alerta.initStyle(StageStyle.UTILITY);
			alerta.showAndWait();
            return false;
        
        } else if(!pass.equals(confirmarPass)) {
            alerta = new Alert(Alert.AlertType.ERROR);
            alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); //Añade hoja de estilos.
			alerta.setTitle("Error confirmación password");
			alerta.setHeaderText("");
			alerta.setContentText("El password de confirmación no conincide con el password.");
			alerta.initStyle(StageStyle.UTILITY);
			alerta.showAndWait();
            return false;
        
        } else {
            return true;
        }
    }


    private boolean crearFicherosUsuario(Usuario user) {
		user.getDirectorio().mkdir();
		conexionBD.setUsuario(user);
		File ficheroBD = new File(user.getDirectorio().getName() + "\\" + user.getNombre() + conexionBD.FINAL_NOMBRE_FICHERO_DB);
		
		if(!ficheroBD.exists()) {
			try {
				conexionBD.crearTablasUsuario();
                return true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
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
     * Establece un Stage para este controlador.
     * 
     * @param s Stage que se establece.
     */
    public void setStage(Stage s) {
    	this.escenario = s;
    }

    public void setUsuarioApp(Usuario userApp) {
        usuarioApp = userApp;
        conexionBD.setUsuario(usuarioApp);
    }
}
