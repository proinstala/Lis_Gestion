package controlador;


import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import baseDatos.ConexionBD;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
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
import utilidades.Constants;


public class UsuarioFormEmailControlador implements Initializable {

    private double x, y;
    private ConexionBD conexionBD;
    private Logger logUser;
    private Toast toast;
    private Alert alerta;

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
    private Button btnQuitar;

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
        //Añadir clases de estilo CSS a elementos.
        btnCancelar.getStyleClass().add("boton_rojo");

        //Establecer imagen en ImageView.
        Image imagenEdit;
        Image correo;
        try {
            //Intentar cargar la imagen desde el recurso en el IDE y en el JAR.
            imagenEdit = new Image(getClass().getResourceAsStream("/recursos/usuario_edit_1_128.png")); //Forma desde IDE y JAR.
            correo = new Image(getClass().getResourceAsStream("/recursos/email_3_128.png"));
        } catch (Exception e) {
            //Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR.
            imagenEdit = new Image("/recursos/usuario_edit_1_128.png"); //Forma desde el JAR.
            correo = new Image("/recursos/email_3_128.png");
        }
        //Establecer las imagenes cargadas en los ImageView.
        ivImagenUser.setImage(imagenEdit);
        ivEmail.setImage(correo);

        //Configurar el evento cuando se presiona el ratón en el panel apEmailApp.
        apEmailApp.setOnMousePressed(mouseEvent -> {
            //Obtener las coordenadas X e Y del ratón en relación con la escena.
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });

        //Configurar el evento cuando se arrastra el ratón en el panel apEmailApp.
        apEmailApp.setOnMouseDragged(mouseEvent -> {
            //Obtener la referencia al Stage actual y establecer las nuevas coordenadas X e Y.
            ((Stage) apEmailApp.getScene().getWindow()).setX(mouseEvent.getScreenX() - x);
            ((Stage) apEmailApp.getScene().getWindow()).setY(mouseEvent.getScreenY() - y);
        });

        logUser = Logger.getLogger(Constants.USER); //Crea una instancia de la clase Logger asociada al nombre de registro.
        conexionBD = ConexionBD.getInstance();      //Obtener una instancia de la clase ConexionBD utilizando el patrón Singleton.
        toast = new Toast();

        //Pone oculto los TextFiel que muestran los password.
        tfPasswordVisible.setVisible(false);

        //Configurar un evento de clic del ratón para el botón "Cancelar".
        btnCancelar.setOnMouseClicked(e -> {
            ((Stage) apEmailApp.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
        });
    }


    /**
     * Método de controlador de evento para guardar los cambios en el email de aplicación del usuario.
     * Verifica si los campos están completos y guarda los cambios realizados en el email de aplicación del usuario.
     * Si los campos están completos y los cambios se guardan correctamente,
     * Muestra un mensaje de notificación en un toast.
     * Además, cierra la ventana actual.
     *
     * @param event El evento de ratón que activó el guardado de cambios.
     */
    @FXML
    void guardarCambios(MouseEvent event) {
        if(comprobarCampos() && guardarCambios()) {
            toast.show((Stage) ((Stage) apEmailApp.getScene().getWindow()).getOwner(), "Email aplicación usuario modificado.");
            ((Stage) apEmailApp.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
        }
    }


    /**
     * Método de controlador de evento para borrar datos de email de aplicación de usuario.
     * Muestra una ventana de dialogo para confirmar la acción o cancelar.
     * Si se confirma, muestra un mesanje indicando si se ha podido llevar acabo la acción o no.
     * 
     * @param event El evento de ratón que activó el quitarEamil.
     */
    @FXML
    void quitarEamil(MouseEvent event) {
        if (oldUsuario.getEmailApp() != null || !oldUsuario.getEmailApp().isBlank()) {
            alerta = new Alert(AlertType.CONFIRMATION);
            alerta.getDialogPane().getStylesheets()
                    .add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
            alerta.setTitle("Borrar Email Aplicación");
            alerta.setHeaderText("Esta acción elimana los datos (email y password) que estan\n" + "guardados de email Aplicación.");
            alerta.setContentText("¿Estas seguro de que quieres borrar estos datos?");
            alerta.initStyle(StageStyle.DECORATED);
            alerta.initOwner((Stage) apEmailApp.getScene().getWindow());

            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
            ButtonType buttonTypeConfirmar = new ButtonType("Confirmar", ButtonData.YES);
            alerta.getButtonTypes().setAll(buttonTypeConfirmar, buttonTypeCancel);
            Optional<ButtonType> result = alerta.showAndWait();

            // Si pulsamos el boton confirmar:
            if (result.get() == buttonTypeConfirmar) {
                newUsuario.setEmailApp("");
                newUsuario.setPasswordEmailApp("");

                if (guardarCambios()) {
                    toast.show((Stage) apEmailApp.getScene().getWindow(),
                            "Email aplicación usuario modificado.");
                } else {
                    toast.show((Stage) apEmailApp.getScene().getWindow(),
                            "Fallo al intentar modificadar\nel email aplicación usuario.");
                }
            }
        } else {
            toast.show((Stage) apEmailApp.getScene().getWindow(),
                    "El Usuario no tiene configurado el Email Aplicación.");
        }
    }


    /**
     * Comprueba si los campos de email y contraseña son válidos.
     *
     * @return true si los campos son válidos, false en caso contrario.
     */
    private Boolean comprobarCampos() {
        boolean camposCorrectos = false;

        //Patrón para validar el formato del email.
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        Matcher emailMatcher = emailPattern.matcher((tfEmail.getText() == null) ? "" : tfEmail.getText());

        //Patrón para validar el formato de la contraseña.
        Pattern passPat = Pattern.compile("^[\\S]{4,20}$");
        Matcher passMatch = passPat.matcher((tfPassword.getText() == null) ? "" : tfPassword.getText());


        if(!emailMatcher.matches()) {
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


    /**
     * Guarda los cambios realizados en el email y la contraseña de la aplicación para el usuario actual.
     * Registra un mensaje en el log si ha modificado el email o si no se ha podido modificar.
     *
     * @return true si los cambios se guardaron correctamente, false en caso contrario.
     */
    private boolean guardarCambios() {
        try {
            if(conexionBD.modificarEmailApp(newUsuario)) {
                //Actualizar los valores del email y contraseña de la aplicación en el objeto oldUsuario.
                oldUsuario.setEmailApp(newUsuario.getEmailApp());
                oldUsuario.setPasswordEmailApp(newUsuario.getPasswordEmailApp());
                logUser.config("Datos de email aplicación usuario modificados.");
                return true;
            } else {
                logUser.warning("Fallo al intentar modificar el email aplicación usuario en BD.");
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
     * Inicializa la interfaz de usuario y configura la visualización de los elementos.
     * Configura los textos iniciales y los enlaces de datos entre los campos de texto y las propiedades de cadena.
     * También agrega un listener al CheckBox para controlar la visibilidad de los campos de contraseña.
     * 
     */
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
        alerta.initOwner((Stage) apEmailApp.getScene().getWindow());
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.showAndWait();
    }


    /**
     * Establece el usuario actual y llama al metodo que prepara la interfaz para mostrar los detalles del nuevo usuario.
     *
     * @param usuario El objeto Usuario que se establecerá como usuario actual.
     */
	public void setUsuario(Usuario usuario) {
		oldUsuario = usuario;
        newUsuario = new Usuario(usuario);
        iniciar();
	}
}
