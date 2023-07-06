package controlador;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import baseDatos.ConexionBD;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

public class CambioPasswordControlador implements Initializable {

    private double x, y;
    private ConexionBD conexionBD;
    private Toast toast;
    private Alert alerta;
    private Usuario usuario;
    private Usuario usuarioRoot;
    private Stage escenario;
    private StringProperty password;
    private StringProperty nuevoPassword;
    private StringProperty confimarPassword;

    @FXML
    private AnchorPane apCambioPassword;

    @FXML
    private AnchorPane apIzquierda;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnGuardar;

    @FXML
    private CheckBox chbVisible;

    @FXML
    private ImageView ivImagenUser;

    @FXML
    private ImageView ivPadloock;

    @FXML
    private Label lbIdUsuario;

    @FXML
    private Label lbNombreUsuario;

    @FXML
    private PasswordField tfConfirmarPassword;

    @FXML
    private PasswordField tfNuevoPassword;

    @FXML
    private PasswordField tfPassword;

    @FXML
    private TextField tfConfirmarPasswordVisible;

    @FXML
    private TextField tfNuevoPasswordVisible;

    @FXML
    private TextField tfPasswordVisible;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnCancelar.getStyleClass().add("boton_rojo");
        conexionBD = ConexionBD.getInstance();
        toast = new Toast();
        
        Image imagenEdit;
        Image padlock;
        try {
            imagenEdit = new Image(getClass().getResourceAsStream("/recursos/usuario_edit_1_128.png")); //Forma desde IDE y JAR.
            padlock = new Image(getClass().getResourceAsStream("/recursos/candado_1_64.png"));
        } catch (Exception e) {
            imagenEdit = new Image("/recursos/usuario_edit_1_128.png"); //Forma desde el JAR.
            padlock = new Image("/recursos/candado_1_64.png");
        }
        ivImagenUser.setImage(imagenEdit);
        ivPadloock.setImage(padlock);

        apCambioPassword.setOnMousePressed(mouseEvent -> {
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });

        apCambioPassword.setOnMouseDragged(mouseEvent -> {
            escenario.setX(mouseEvent.getScreenX() - x);
            escenario.setY(mouseEvent.getScreenY() - y);
        });
        
        //Inicializa los StringProperty
        password = new SimpleStringProperty("");
        nuevoPassword = new SimpleStringProperty("");
        confimarPassword = new SimpleStringProperty("");

        //Bindea los Texfield con los StringProperty.
        tfPassword.textProperty().bindBidirectional(password);
        tfPasswordVisible.textProperty().bindBidirectional(password);

        tfNuevoPassword.textProperty().bindBidirectional(nuevoPassword);
        tfNuevoPasswordVisible.textProperty().bindBidirectional(nuevoPassword);
       
        tfConfirmarPassword.textProperty().bindBidirectional(confimarPassword);
        tfConfirmarPasswordVisible.textProperty().bindBidirectional(confimarPassword);

        //Pone oculto los TextFiel que muestran los password.
        tfPasswordVisible.setVisible(false);
        tfNuevoPasswordVisible.setVisible(false);
        tfConfirmarPasswordVisible.setVisible(false);

        //Añade listener al CheckBox que oculta y hace visible a los TextFiel y PasswordFiel.
        chbVisible.selectedProperty().addListener((o, ov, nv) -> {
            if(chbVisible.isSelected()) {
                tfPassword.setVisible(false);
                tfPasswordVisible.setVisible(true);
        
                tfNuevoPassword.setVisible(false);
                tfNuevoPasswordVisible.setVisible(true);

                tfConfirmarPassword.setVisible(false);
                tfConfirmarPasswordVisible.setVisible(true);
            
            } else {
                tfPassword.setVisible(true);
                tfPasswordVisible.setVisible(false);

                tfNuevoPassword.setVisible(true);
                tfNuevoPasswordVisible.setVisible(false);

                tfConfirmarPassword.setVisible(true);
                tfConfirmarPasswordVisible.setVisible(false);
            }
        });
    }

    @FXML
    void cerrarVentana(MouseEvent event) {
        escenario.close();
    }

    @FXML
    void guardarCambios(MouseEvent event) {
        if(comprobarPassword() && comprobarNuevoPassword()) {
            boolean cambioPassUsuario = false;
            try {
                conexionBD.setUsuario(usuarioRoot);
                cambioPassUsuario = conexionBD.cambiarPasswordUsuario(nuevoPassword.get(), password.get(), usuario.getId());
            } catch (SQLException e) {
                alerta = new Alert(Alert.AlertType.ERROR);
                alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
                alerta.setTitle("Error cambio password");
                alerta.setHeaderText("");
                alerta.setContentText("No se ha podido cambiar el password.\n"
                        + "Ha ocurrido un error durante la actualizacion en la base de datos.");
                alerta.initStyle(StageStyle.DECORATED);
                alerta.initOwner(escenario);
                alerta.showAndWait();
                e.printStackTrace();
            } finally {
                conexionBD.setUsuario(usuario);
            }

            //Si se ha cambiado la contraseña de usuario en la base de datos de usuarioRoot, entoces cambiamos la contraseña de la BD de usuario.
            if(cambioPassUsuario) {
                boolean cambioPassBD = false;
                try {
                    cambioPassBD = conexionBD.cambiarPasswordBD(nuevoPassword.get(), usuario);
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } 

                //Si ha cambiado la contraseña de la BD de usuario, cambia la contraseña al objeto usuario y cierra la ventata.
                if (cambioPassBD) {
                    usuario.setPassword(nuevoPassword.get());
                    conexionBD.setUsuario(usuario);

                    alerta = new Alert(Alert.AlertType.INFORMATION);
                    alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
                    alerta.setTitle("Cambio password");
                    alerta.setHeaderText("");
                    alerta.setContentText("El password se ha actualizado.");
                    alerta.initStyle(StageStyle.DECORATED);
                    alerta.initOwner(escenario);
                    alerta.showAndWait();
                    
                    cerrarVentana(null);

                } else { //Si no ha cambiado la contraseña en la BD de usuario, restablece la contraseña de usuario en la BD de usuarioRoot.
                    try {
                        conexionBD.setUsuario(usuarioRoot);
                        if(conexionBD.cambiarPasswordUsuario(password.get(), nuevoPassword.get(), usuario.getId())) {
                            alerta = new Alert(Alert.AlertType.ERROR);
                            alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
                            alerta.setTitle("Error cambio password");
                            alerta.setHeaderText("");
                            alerta.setContentText("No se ha podido cambiar el password.\n"
                                + "Ha ocurrido un error durante la actualizacion en la base de datos.");
                            alerta.initStyle(StageStyle.DECORATED);
                            alerta.initOwner(escenario);
                            alerta.showAndWait();

                            
                        } else {
                            alerta = new Alert(Alert.AlertType.ERROR);
                            alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
                            alerta.setTitle("Error cambio password");
                            alerta.setHeaderText("");
                            alerta.setContentText("No se ha podido cambiar el password.\n"
                                + "Ha ocurrido un error durante la actualizacion en la base de datos.\n"
                                + "Es posible que el password de usuario haya quedado inutilizado.\n"
                                + "Pongase en contacto con el técnico de mantenimiento.");
                            alerta.initStyle(StageStyle.DECORATED);
                            alerta.initOwner(escenario);
                            alerta.showAndWait();
                        }
                    } catch (SQLException e) {
                        alerta = new Alert(Alert.AlertType.ERROR);
                        alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
                        alerta.setTitle("Error cambio password");
                        alerta.setHeaderText("");
                        alerta.setContentText("No se ha podido cambiar el password.\n"
                            + "Ha ocurrido un error durante la actualizacion en la base de datos.\n"
                            + "Es posible que el password de usuario haya quedado inutilizado.\n"
                            + "Pongase en contacto con el técnico de mantenimiento.");
                        alerta.initStyle(StageStyle.DECORATED);
                        alerta.initOwner(escenario);
                        alerta.showAndWait();
                        e.printStackTrace();
                    } finally {
                        conexionBD.setUsuario(usuario);
                    }
                }
            } else {
                alerta = new Alert(Alert.AlertType.ERROR);
                alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
                alerta.setTitle("Error cambio password");
                alerta.setHeaderText("");
                alerta.setContentText("No se ha podido cambiar el password.\n"
                        + "Ha ocurrido un error durante la actualizacion en la base de datos.");
                alerta.initStyle(StageStyle.DECORATED);
                alerta.initOwner(escenario);
                alerta.showAndWait();
            }
        }
    }

    private boolean comprobarPassword() {
        if(usuario.getPassword().equals(password.get())) {
            //toast.show(escenario, "El password de Usuario correcto!!.");
            return true;
        } else if(password.get().isBlank()) {
            toast.show((Stage) apCambioPassword.getScene().getWindow(), "El campo Password esta vacío.!!.");
            return false;
        } else {
            toast.show((Stage) apCambioPassword.getScene().getWindow(), "El password de Usuario introducido\nes difente al password de Usuario!!.");
            return false;
        }
    }

    private boolean comprobarNuevoPassword() {
        // compilamos la expresion regular.(Una letra mañuscula o minuscula o un numero del 0 al 9 de 4 a 20 caracteres).
		Pattern passPat = Pattern.compile("^[\\S]{4,20}$");
        Matcher passMatch = passPat.matcher(nuevoPassword.get());

        if(!passMatch.matches()) {
            alerta = new Alert(Alert.AlertType.ERROR);
            alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); //Añade hoja de estilos.
			alerta.setTitle("Error nuevo password");
			alerta.setHeaderText("");
			alerta.setContentText("El password de usuario tiene que contener de 4 a 20 caracteres.\n"
                    + "No puede contener espacios en blanco.");
            alerta.initStyle(StageStyle.DECORATED);
            alerta.initOwner(escenario);
			alerta.showAndWait();
            return false;
        } else if(!nuevoPassword.get().equals(confimarPassword.get())) {
            alerta = new Alert(Alert.AlertType.ERROR);
            alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); //Añade hoja de estilos.
			alerta.setTitle("Error confirmación password");
			alerta.setHeaderText("");
			alerta.setContentText("El password de confirmación no conincide con el nuevo password.");
			alerta.initStyle(StageStyle.DECORATED);
            alerta.initOwner(escenario);
			alerta.showAndWait();
            return false;
        } else if(usuario.getPassword().equals(nuevoPassword.get())) {
            alerta = new Alert(Alert.AlertType.ERROR);
            alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); //Añade hoja de estilos.
			alerta.setTitle("Error password");
			alerta.setHeaderText("");
			alerta.setContentText("El nuevo password de usuario es igual\nque el password actual de usuario."
                    + "\nEl nuevo password tiene que ser difente al password actual.");
            alerta.initStyle(StageStyle.DECORATED);
            alerta.initOwner(escenario);
			alerta.showAndWait();
            return false;
        } else {
            return true;
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
    
    
}
