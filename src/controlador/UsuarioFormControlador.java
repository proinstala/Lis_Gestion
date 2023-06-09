package controlador;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import baseDatos.ConexionBD;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.NumberStringConverter;
import modelo.Toast;
import modelo.Usuario;
import javafx.fxml.Initializable;

public class UsuarioFormControlador implements Initializable {

    private ConexionBD conexionBD;
    private Toast toast;
    private Stage escenario;

    private Usuario oldUsuario;
    private Usuario newUsuario;

    private String nombre;
    private String apellido1;
    private String apellido2;
    private int telefono;
    private String email;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnConfirmacion;

    @FXML
    private GridPane gpFormUsuario;

    @FXML
    private ImageView ivImagenTipoFormulario;

    @FXML
    private Label lbTitulo;

    @FXML
    private TextField tfApellido1;

    @FXML
    private TextField tfApellido2;

    @FXML
    private TextField tfEmail;

    @FXML
    private TextField tfIdUsuario;

    @FXML
    private TextField tfNombre;

    @FXML
    private TextField tfTelefono;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnCancelar.getStyleClass().add("boton_rojo");
    	gpFormUsuario.getStyleClass().add("fondo_ventana_degradado_toRight");

        conexionBD = ConexionBD.getInstance();
        toast = new Toast();

        //Establecer imagen formulario.
        Image imagen;
        try {
            imagen = new Image("/recursos/usuario_edit_1_128.png");
        } catch (Exception e) {
            imagen = new Image(getClass().getResourceAsStream("/recursos/usuario_edit_1_128.png"));
        }
        ivImagenTipoFormulario.setImage(imagen);

        tfIdUsuario.setDisable(true);
    }

    @FXML
    void cancelar(MouseEvent event) {
        escenario.close();
    }

    @FXML
    void confirmacion(MouseEvent event) {
        if(comprobarCampos() && guardarCambios()) {
            toast.show(escenario, "Datos de usuario modificados.");
            escenario.close();
        }
    }

    public void setUsuario(Usuario usuario) {
        oldUsuario = usuario;
        newUsuario = new Usuario(usuario);

        tfIdUsuario.setText(Integer.toString(newUsuario.getId()));
        tfNombre.textProperty().bindBidirectional(newUsuario.nombreProperty());
        tfApellido1.textProperty().bindBidirectional(newUsuario.apellido1Property());
        tfApellido2.textProperty().bindBidirectional(newUsuario.apellido2Property());
        tfTelefono.textProperty().bindBidirectional(newUsuario.telefonoProperty(), new NumberStringConverter("0"));
        tfEmail.textProperty().bindBidirectional(newUsuario.emailProperty());
    }

    private Boolean comprobarCampos() {
        boolean camposCorrectos = false;

        Pattern nombrePattern = Pattern.compile("[A-Z][a-z]{1,30}([\\s][A-Z][a-z]{1,30})?$"); //Expresion regular para comprobar un nombre simple o compuesto.
		Matcher nombreMatch = nombrePattern.matcher((tfNombre.getText() == null) ? "" : tfNombre.getText());

        Pattern apellidoPattern = Pattern.compile("[A-Z][a-z]{1,40}$"); 
        Matcher apellido1Matcher = apellidoPattern.matcher((tfApellido1.getText() == null) ? "" : tfApellido1.getText());
        Matcher apellido2Matcher = apellidoPattern.matcher((tfApellido2.getText() == null) ? "" : tfApellido2.getText());

        Pattern telefonoPattern = Pattern.compile("[0-9]{9,10}|^[0]?$");
        Matcher telefonoMatcher = telefonoPattern.matcher(tfTelefono.getText());

        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$|^$");
        Matcher emaiMatcher = emailPattern.matcher((tfEmail.getText() == null) ? "" : tfEmail.getText());

        if(!nombreMatch.matches()) {
            mensajeAviso("Nombre no valido.",
            "El nombre introducido no es valido.",
            "El nombre puede ser simple o compuesto.\nLa primera letra del nombre tiene que estar en mayúscula.\nMáximo 30 caracteres cada nombre.\nEjemplo: María / María Dolores.");
        } else if(!apellido1Matcher.matches()) {
            mensajeAviso("1º Apellido no valido.",
            "El apellido introducido no es valido",
            "La primera letra del Apellido tien que ser mayúscula.\nMáximo 40 caracteres.\nEjemplo: Sanchez.");
        } else if(!apellido2Matcher.matches()) {
            mensajeAviso("2º Apellido no valido.",
            "El apellido introducido no es valido",
            "La primera letra del apellido tien que ser mayúscula.\nMáximo 40 caracteres.\nEjemplo: Sanchez.");
        } else if(!telefonoMatcher.matches()) {
            mensajeAviso("Teléfono no valido",
            "El teléfono introducido no es valido",
            "Debes introducir un número de 9 a 10 dígitos.\nEste campo es opcional.");
        } else if(!emaiMatcher.matches()) {
            mensajeAviso("Email no valido.",
            "",
            "El Email introducido no es valido.\nEste campo es opcional.");
        } else {
            camposCorrectos = true;
        }

        //Si no hay errores en los campos, guarda la informacion en las variables.
        if (camposCorrectos) {
            telefono = (tfTelefono.getText() == null) ? 0 : Integer.parseInt(tfTelefono.getText());
            email = (tfEmail.getText() == null) ? "" : tfEmail.getText();
            
            tfTelefono.setText(Integer.toString(telefono));
            tfEmail.setText(email);
        }

        return camposCorrectos;
    }

    
    private boolean guardarCambios() {
        try {
            if(conexionBD.modificarDatosUsuario(newUsuario)) {
                oldUsuario.setNombre(newUsuario.getNombre());
                oldUsuario.setApellido1(newUsuario.getApellido1());
                oldUsuario.setApellido2(newUsuario.getApellido2());
                oldUsuario.setTelefono(newUsuario.getTelefono());
                oldUsuario.setEmail(newUsuario.getEmail());
                return true;
            }
        } catch (SQLException e) {
            // AÑADIR LOG
            e.printStackTrace();
        }
        return false;
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
        alerta.initOwner(escenario);
        alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
        alerta.setTitle(tiutlo);
        alerta.setHeaderText(cabecera);
        alerta.setContentText(cuerpo);
        alerta.initStyle(StageStyle.DECORATED);
        alerta.initOwner(escenario);
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.showAndWait();
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
