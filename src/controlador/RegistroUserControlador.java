package controlador;

import java.io.File;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import baseDatos.ConexionBD;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modelo.Toast;
import modelo.Usuario;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class RegistroUserControlador implements Initializable {

    private double x, y;
    private Stage stage;
    private Stage escenario;
    private Alert alerta;
    private Toast toast;
    private Usuario usuario;
    private Usuario usuarioRoot;
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
        conexionBD = ConexionBD.getInstance();
        toast = new Toast();

        //Cargar imagenes en ImageView.
        Image imagenRegistro;
        try {
            //Forma desde IDE y JAR.
            imagenRegistro = new Image(getClass().getResourceAsStream("/recursos/user_3_512.png"));
        } catch (Exception e) {
            //Forma desde el JAR.
            imagenRegistro = new Image("/recursos/user_3_512.png");
            
        }
        imImagenRegistro.setImage(imagenRegistro);
        
        apRegistroUsuario.getStyleClass().add("fondo_ventana_degradado_masBorde");
        btnCancelar.getStyleClass().add("boton_rojo");
        lbInformacion.getStyleClass().add("color_texto_negro");

        apRegistroUsuario.setOnMousePressed(mouseEvent -> {
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });

        apRegistroUsuario.setOnMouseDragged(mouseEvent -> {
            escenario.setX(mouseEvent.getScreenX() - x);
            escenario.setY(mouseEvent.getScreenY() - y);
        });

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
    }


    @FXML
    void cancelar(MouseEvent event) {
        escenario.close();
    }

    @FXML
    void registrarUsuario(MouseEvent event) {
        
        if(comprobarCampoNombre() && comprobarCamposPassword()) {
            usuario = new Usuario();
            usuario.setNombreUsuario(tfNombre.getText());
            usuario.setPassword(password.get());
            usuario.setDirectorio(new File(tfNombre.getText()));
            
            if(crearFicherosUsuario(usuario)) {
                boolean insertarUserOk = false;
                try {
                    conexionBD.setUsuario(usuarioRoot);
                    insertarUserOk = conexionBD.insertarUsuario(usuario);
                } catch (SQLException e) {
                    // poner log
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    // poner log
                    e.printStackTrace();
                }

                if(insertarUserOk) {
                    alerta = new Alert(Alert.AlertType.INFORMATION);
                    alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); //Añade hoja de estilos.
			        alerta.setTitle("Registro de Usuario");
			        alerta.setHeaderText("");
			        alerta.setContentText("Registro de Usuario completado con exito.");
			        alerta.initStyle(StageStyle.DECORATED);
                    alerta.initOwner(escenario);
			        alerta.showAndWait();

                    escenario.close();
                } else {
                    borrarFicherosUsuario(usuario.getDirectorio());
                    toast.show((Stage) apRegistroUsuario.getScene().getWindow(), "No se ha podido insertar el usuario en la base de datos.!!.");
                }
                
            
            } else {
                toast.show((Stage) apRegistroUsuario.getScene().getWindow(), "No se ha podido crear los ficheros de usuario!!.");
            }

        }
        
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
            alerta.initStyle(StageStyle.DECORATED);
            alerta.initOwner(escenario);
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
                    alerta.initStyle(StageStyle.DECORATED);
                    alerta.initOwner(escenario);
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
        // compilamos la expresion regular.(Una letra mañuscula o minuscula o un numero del 0 al 9 de 4 a 20 caracteres).
		Pattern passPat = Pattern.compile("^[\\S]{4,20}$");
        Matcher passMatch = passPat.matcher(password.get());

        if(!passMatch.matches()) {
            alerta = new Alert(Alert.AlertType.ERROR);
            alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); //Añade hoja de estilos.
			alerta.setTitle("Error password");
			alerta.setHeaderText("");
			alerta.setContentText("El password de usuario tiene que contener de 4 a 20 caracteres.\n" + 
                                "No puede contener espacios en blanco.");
            alerta.initStyle(StageStyle.DECORATED);
            alerta.initOwner(escenario);
			alerta.showAndWait();
            return false;
        
        } else if(!password.get().equals(confimarPassword.get())) {
            alerta = new Alert(Alert.AlertType.ERROR);
            alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); //Añade hoja de estilos.
			alerta.setTitle("Error confirmación password");
			alerta.setHeaderText("");
			alerta.setContentText("El password de confirmación no conincide con el password.");
			alerta.initStyle(StageStyle.DECORATED);
            alerta.initOwner(escenario);
			alerta.showAndWait();
            return false;
        
        } else {
            return true;
        }
    }


    private boolean crearFicherosUsuario(Usuario user) {
		user.getDirectorio().mkdir();
		conexionBD.setUsuario(user);
		File ficheroBD = new File(user.getDirectorio().getName() + "\\" + user.getNombreUsuario() + conexionBD.FINAL_NOMBRE_FICHERO_DB);
		
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
     * @param stage Stage que se establece.
     */
    public void setStage(Stage stage) {
    	this.escenario = stage;
    }

    public void setUsuarioRoot(Usuario usuarioRoot) {
        this.usuarioRoot = usuarioRoot;
        conexionBD.setUsuario(usuarioRoot);
    }
}
