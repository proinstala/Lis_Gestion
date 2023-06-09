package controlador;


import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import baseDatos.ConexionBD;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modelo.Toast;
import modelo.Usuario;


public class UsuarioFormEmailControlador implements Initializable {

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

    private Usuario oldUsuario;
    private Usuario newUsuario;
    
    @FXML
    private AnchorPane apEmailApp;

    @FXML
    private AnchorPane apIzquierda;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnGuardar;

    @FXML
    private CheckBox chbVisible;

    @FXML
    private ImageView ivEmail;

    @FXML
    private ImageView ivImagenUser;

    @FXML
    private Label lbIdUsuario;

    @FXML
    private Label lbNombreUsuario;

    @FXML
    private TextField tfEmail;

    @FXML
    private PasswordField tfPassword;

    @FXML
    private TextField tfPasswordVisible;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnCancelar.getStyleClass().add("boton_rojo");
        conexionBD = ConexionBD.getInstance();
        toast = new Toast();
        Image imagenEdit;
        Image correo;
        try {
            imagenEdit = new Image("/recursos/usuario_edit_1_128.png");
            correo = new Image("/recursos/email_3_128.png");
        } catch (Exception e) {
            imagenEdit = new Image(getClass().getResourceAsStream("/recursos/usuario_edit_1_128.png"));
            correo = new Image(getClass().getResourceAsStream("/recursos/email_3_128.png"));
        }
        ivImagenUser.setImage(imagenEdit);
        ivEmail.setImage(correo);

        apEmailApp.setOnMousePressed(mouseEvent -> {
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });

        apEmailApp.setOnMouseDragged(mouseEvent -> {
            escenario.setX(mouseEvent.getScreenX() - x);
            escenario.setY(mouseEvent.getScreenY() - y);
        });

        //Pone oculto los TextFiel que muestran los password.
        tfPasswordVisible.setVisible(false);
    }

    @FXML
    void cerrarVentana(MouseEvent event) {
        escenario.close();
    }

    @FXML
    void guardarCambios(MouseEvent event) {
        if(comprobarCampos() && guardarCambios()) {
            toast.show(escenario, "Datos de usuario modificados.");
            escenario.close();
        }
    }

    private Boolean comprobarCampos() {
        boolean camposCorrectos = false;

        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        Matcher emaiMatcher = emailPattern.matcher((tfEmail.getText() == null) ? "" : tfEmail.getText());

        Pattern passPat = Pattern.compile("^[\\S]{4,20}$");
        Matcher passMatch = passPat.matcher((tfPassword.getText() == null) ? "" : tfPassword.getText());

        if(!emaiMatcher.matches()) {
            mensajeAviso("Email no valido.",
            "",
            "El Email introducido no es valido.");
        } else if(!passMatch.matches()) {
            mensajeAviso("Password no valido.",
            "",
            "El password introducido no es valido.");
        } else {
            camposCorrectos = true;
        }

        return camposCorrectos;
    }

    private boolean guardarCambios() {
        try {
            if(conexionBD.modificarEmailApp(newUsuario)) {
                oldUsuario.setEmailApp(newUsuario.getEmailApp());
                oldUsuario.setPasswordEmailApp(newUsuario.getPasswordEmailApp());
                return true;
            }
        } catch (SQLException e) {
            // AÑADIR LOG
            e.printStackTrace();
        }
        return false;
    }

    private void iniciar() {
        lbIdUsuario.setText(Integer.toString(newUsuario.getId()));
        lbNombreUsuario.setText(newUsuario.getNombreUsuario());

        //Bindea los Texfield con los StringProperty.
        tfEmail.textProperty().bindBidirectional(newUsuario.emailAppProperty());
        tfPassword.textProperty().bindBidirectional(newUsuario.passwordEmailAppProperty());
        tfPasswordVisible.textProperty().bindBidirectional(newUsuario.passwordEmailAppProperty());

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

    /**
     * Muestra una ventana de dialogo de tipo ERROR con la informacion pasada como parametros.
     * 
     * @param tiutlo Titulo de la ventana.
     * @param cabecera Cabecera del mensaje.
     * @param cuerpo Cuerpo del menesaje.
     */
    private void mensajeAviso(String tiutlo, String cabecera, String cuerpo) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
        alerta.setTitle(tiutlo);
        alerta.setHeaderText(cabecera);
        alerta.setContentText(cuerpo);
        alerta.initStyle(StageStyle.UTILITY);
        alerta.initOwner(escenario);
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.showAndWait();
    }


    /**
	 * Etablece el usuario que esta usando la aplicación.
	 * @param usuario
	 */
	public void setUsuario(Usuario usuario) {
		oldUsuario = usuario;
        newUsuario = new Usuario(usuario);
        iniciar();
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
