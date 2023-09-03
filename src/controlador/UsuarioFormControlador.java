package controlador;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
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
import modelo.Usuario;
import utilidades.Constants;
import utilidades.Toast;
import javafx.fxml.Initializable;


public class UsuarioFormControlador implements Initializable {

    private ConexionBD conexionBD;
    private Logger logUser;
    private Toast toast;

    private Usuario oldUsuario;
    private Usuario newUsuario;

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
        //Añadir clases de estilo CSS a elementos.
        btnCancelar.getStyleClass().add("boton_rojo");
    	gpFormUsuario.getStyleClass().add("fondo_ventana_degradado_toRight");

        //Establecer imagen formulario.
        Image imagen;
        try {
            //Intentar cargar la imagen desde el recurso en el IDE y en el JAR.
            imagen = new Image(getClass().getResourceAsStream("/recursos/usuario_edit_1_128.png")); //Forma desde IDE y JAR.
        } catch (Exception e) {
            //Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR.
            imagen = new Image("/recursos/usuario_edit_1_128.png"); //Forma desde el JAR.
            
        }
        ivImagenTipoFormulario.setImage(imagen); //Establecer la imagen cargada en el ImageView.

        logUser = Logger.getLogger(Constants.USER); //Crea una instancia de la clase Logger asociada al nombre de registro.
        conexionBD = ConexionBD.getInstance();      //Obtener una instancia de la clase ConexionBD utilizando el patrón Singleton.
        toast = new Toast();

        tfIdUsuario.setDisable(true);

        //Configurar un evento de clic del ratón para el botón "Cancelar".
        btnCancelar.setOnMouseClicked(e -> {
            ((Stage) gpFormUsuario.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
        });
    }


    /**
    * Método de controlador de evento para la confirmación de cambios del usuario.
    * Verifica si los campos están completos y guarda los cambios realizados.
    * Si los campos están completos y los cambios se guardan correctamente,
    * registra un mensaje de log y muestra un mensaje de notificación en un toast.
    * Además, cierra la ventana actual.
    *
    * @param event El evento de ratón que activó la confirmación.
    */
    @FXML
    void confirmacion(MouseEvent event) {
        if(comprobarCampos() && guardarCambios()) {
            logUser.config("Datos de usuario modificados");
            toast.show((Stage) ((Stage) gpFormUsuario.getScene().getWindow()).getOwner(), "Datos de usuario modificados.");
            ((Stage) gpFormUsuario.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
        }
    }


    /**
     * Establece el objeto Usuario y actualiza los componentes de la interfaz gráfica con sus valores.
     *
     * @param usuario El objeto Usuario a establecer.
     */
    public void setUsuario(Usuario usuario) {
        oldUsuario = usuario;               //Guardar el usuario original en oldUsuario.
        newUsuario = new Usuario(usuario);  //Crear una nueva instancia de Usuario basada en el objeto usuario pasado como parámetro.

        tfIdUsuario.setText(Integer.toString(newUsuario.getId())); //Establecer el texto del componente tfIdUsuario con el ID del nuevo usuario.

        //Vincular las propiedades de texto de los componentes de la interfaz gráfica con las propiedades correspondientes del nuevo usuario.
        tfNombre.textProperty().bindBidirectional(newUsuario.nombreProperty());
        tfApellido1.textProperty().bindBidirectional(newUsuario.apellido1Property());
        tfApellido2.textProperty().bindBidirectional(newUsuario.apellido2Property());
        tfTelefono.textProperty().bindBidirectional(newUsuario.telefonoProperty(), new NumberStringConverter("0"));
        tfEmail.textProperty().bindBidirectional(newUsuario.emailProperty());
    }


    /**
     * Comprueba la validez de los campos de entrada del formulario.
     * Realiza validaciones utilizando expresiones regulares para cada campo.
     * Muestra mensajes de advertencia si algún campo no es válido.
     * Si todos los campos son válidos, guarda la información en variables y actualiza los componentes de la interfaz gráfica correspondientes.
     *
     * @return true si todos los campos son válidos, false en caso contrario.
     */
    private Boolean comprobarCampos() {
        boolean camposCorrectos = false;

        Pattern nombrePattern = Pattern.compile("[A-ZÑ][a-zñáéíóú]{1,30}([\\s][A-ZÑ][a-zñáéíóú]{1,30}){0,2}$"); //Expresion regular para comprobar un nombre simple o compuesto.
		Matcher nombreMatch = nombrePattern.matcher((tfNombre.getText() == null) ? "" : tfNombre.getText());

        Pattern apellidoPattern = Pattern.compile("[A-Z][a-zñáéíóú]{1,40}$"); 
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
            "La primera letra del Apellido tiene que ser mayúscula.\nMáximo 40 caracteres.\nEjemplo: Sanchez.");
        } else if(!apellido2Matcher.matches()) {
            mensajeAviso("2º Apellido no valido.",
            "El apellido introducido no es valido",
            "La primera letra del apellido tiene que ser mayúscula.\nMáximo 40 caracteres.\nEjemplo: Sanchez.");
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

    
    /**
     * Guarda los cambios realizados en el usuario en la base de datos.
     * Intenta modificar los datos del usuario utilizando la conexión a la base de datos.
     * Si la modificación es exitosa, actualiza los valores del usuario original con los nuevos valores.
     *
     * @return true si los cambios se guardaron correctamente en la base de datos, false en caso contrario.
     */
    private boolean guardarCambios() {
        try {
            if(conexionBD.modificarDatosUsuario(newUsuario)) {
                oldUsuario.setNombre(newUsuario.getNombre());
                oldUsuario.setApellido1(newUsuario.getApellido1());
                oldUsuario.setApellido2(newUsuario.getApellido2());
                oldUsuario.setTelefono(newUsuario.getTelefono());
                oldUsuario.setEmail(newUsuario.getEmail());
                return true;
            } else {
                logUser.warning("Fallo al intentar modificar Usuario en BD.");
            }
        } catch (SQLException e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            logUser.severe("Excepción: " + e.toString());
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
        alerta.initOwner((Stage) gpFormUsuario.getScene().getWindow());
        alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
        alerta.setTitle(tiutlo);
        alerta.setHeaderText(cabecera);
        alerta.setContentText(cuerpo);
        alerta.initStyle(StageStyle.DECORATED);
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.showAndWait();
    }
}
