package controlador;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import baseDatos.ConexionBD;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
import utilidades.Constants;

public class CambioPasswordControlador implements Initializable {

    private double x, y;
    private ConexionBD conexionBD;
    private Logger logUser;
    private Toast toast;
    private Usuario usuario;
    private Usuario usuarioRoot;
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
        btnCancelar.getStyleClass().add("boton_rojo"); //Añadir clases de estilo CSS a elementos.
        
        //Establecer imagen en ImageView.
        Image imagenEdit;
        Image padlock;
        try {
            //Intentar cargar la imagen desde el recurso en el IDE y en el JAR.
            imagenEdit = new Image(getClass().getResourceAsStream("/recursos/usuario_edit_1_128.png")); //Forma desde IDE y JAR.
            padlock = new Image(getClass().getResourceAsStream("/recursos/candado_1_64.png"));
        } catch (Exception e) {
            //Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR.
            imagenEdit = new Image("/recursos/usuario_edit_1_128.png"); //Forma desde el JAR.
            padlock = new Image("/recursos/candado_1_64.png");
        }
        //Establecer las imagenes cargadas en los ImageView.
        ivImagenUser.setImage(imagenEdit);
        ivPadloock.setImage(padlock);

        //Configurar el evento cuando se presiona el ratón en el panel apCambioPassword.
        apCambioPassword.setOnMousePressed(mouseEvent -> {
            //Obtener las coordenadas X e Y del ratón en relación con la escena.
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });

        //Configurar el evento cuando se arrastra el ratón en el panel apCambioPassword.
        apCambioPassword.setOnMouseDragged(mouseEvent -> {
            //Obtener la referencia al Stage actual y establecer las nuevas coordenadas X e Y.
            ((Stage) apCambioPassword.getScene().getWindow()).setX(mouseEvent.getScreenX() - x);
            ((Stage) apCambioPassword.getScene().getWindow()).setY(mouseEvent.getScreenY() - y);
        });
        

        logUser = Logger.getLogger(Constants.USER); //Crea una instancia de la clase Logger asociada al nombre de registro.
        conexionBD = ConexionBD.getInstance(); //Obtener una instancia de la clase ConexionBD utilizando el patrón Singleton.
        toast = new Toast();

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

        //Configurar un evento de clic del ratón para el botón "Cancelar".
        btnCancelar.setOnMouseClicked(e -> {
            ((Stage) apCambioPassword.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
        });
    }


    /**
     * Método para manejar el evento de guardar cambios en la contraseña de Usuario.
     * Comprueba las contraseñas, realiza los cambios en la base de datos
     * y muestra mensajes de confirmación o error.
     *
     * @param event El evento del ratón que activó el método.
     */
    @FXML
    void guardarCambios(MouseEvent event) {
        if(comprobarPassword() && comprobarNuevoPassword()) {
            boolean cambioPassUsuario = false;
            try {
                conexionBD.setUsuario(usuarioRoot);
                cambioPassUsuario = conexionBD.cambiarPasswordUsuario(nuevoPassword.get(), password.get(), usuario.getId());
            } catch (SQLException e) {
                logUser.severe("Excepción: " + e.toString());
                e.printStackTrace();
                mensajeAviso(
                    AlertType.INFORMATION,
                    "Error cambio password",
                    "",
                    "No se ha podido cambiar el password.\n"
                        + "Ha ocurrido un error durante la actualizacion en la base de datos.");
            } catch (Exception e) {
                logUser.severe("Excepción: " + e.toString());
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
                    logUser.severe("Excepción: " + e.toString());
                    e.printStackTrace();
                } catch (Exception e) {
                    logUser.severe("Excepción: " + e.toString());
                    e.printStackTrace();
                } 

                //Si ha cambiado la contraseña de la BD de usuario, cambia la contraseña al objeto usuario y cierra la ventata.
                if (cambioPassBD) {
                    usuario.setPassword(nuevoPassword.get());
                    conexionBD.setUsuario(usuario);
                    logUser.config("Cambiado password Usuario.");
                    mensajeAviso(
                        AlertType.INFORMATION,
                        "Cambio password",
                        "",
                        "El password se ha actualizado.");
                    
                    ((Stage) apCambioPassword.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.

                } else { //Si no ha cambiado la contraseña en la BD de usuario, restablece la contraseña de usuario en la BD de usuarioRoot.
                    try {
                        conexionBD.setUsuario(usuarioRoot);
                        if(conexionBD.cambiarPasswordUsuario(password.get(), nuevoPassword.get(), usuario.getId())) {
                            logUser.warning("Fallo durante el cambio de password de Usuario.");
                            mensajeAviso(
                                AlertType.ERROR,
                                "Error cambio password",
                                "",
                                "No se ha podido cambiar el password.\n"
                                    + "Ha ocurrido un error durante la actualizacion en la base de datos.");

                        } else {
                            logUser.warning("Fallo durante el cambio de password de Usuario. Fallo al restablecer el password en BD Root.");
                            mensajeAviso(
                                AlertType.ERROR,
                                "Error cambio password",
                                "",
                                "No se ha podido cambiar el password.\n"
                                    + "Ha ocurrido un error durante la actualizacion en la base de datos.\n"
                                    + "Es posible que el password de usuario haya quedado inutilizado.\n"
                                    + "Pongase en contacto con el técnico de mantenimiento.");

                        }
                    } catch (SQLException e) {
                        logUser.severe("Excepción: " + e.toString());
                        e.printStackTrace();
                        mensajeAviso(
                            AlertType.ERROR,
                            "Error cambio password",
                            "",
                            "No se ha podido cambiar el password.\n"
                                + "Ha ocurrido un error durante la actualizacion en la base de datos.\n"
                                + "Es posible que el password de usuario haya quedado inutilizado.\n"
                                + "Pongase en contacto con el técnico de mantenimiento.");

                    } catch (Exception e) {
                        logUser.severe("Excepción: " + e.toString());
                        e.printStackTrace();
                    } finally {
                        conexionBD.setUsuario(usuario);
                    }
                }
            } else {
                logUser.warning("Fallo durante el cambio de password de Usuario.");
                mensajeAviso(
                    AlertType.ERROR,
                    "Error cambio password",
                    "",
                    "No se ha podido cambiar el password.\n"
                        + "Ha ocurrido un error durante la actualizacion en la base de datos.");
            }
        }
    }


    /**
     * Comprueba si el password introducido coincide con el password del usuario actual.
     * Si el password coincide o está en blanco, devuelve true.
     * Si el password está en blanco, muestra un mensaje de error.
     * Si el password no coincide, muestra un mensaje de error.
     *
     * @return true si el password coincide o está en blanco, false en caso contrario.
     */
    private boolean comprobarPassword() {
        if(usuario.getPassword().equals(password.get())) {
            return true;
        } else if(password.get().isBlank()) {
            toast.show((Stage) apCambioPassword.getScene().getWindow(), "El campo Password esta vacío.!!.");
            return false;
        } else {
            toast.show((Stage) apCambioPassword.getScene().getWindow(), "El password de Usuario introducido\nes difente al password de Usuario!!.");
            return false;
        }
    }


    /**
     * Comprueba si el nuevo password cumple con los requisitos y si coincide con la confirmación de password.
     * Si el nuevo password cumple con los requisitos y coincide con la confirmación, devuelve true, 
     * en caso contrario muestra un mensaje de error y devuelve false.
     *
     * @return true si el nuevo password cumple con los requisitos y coincide con la confirmación, false en caso contrario.
     */
    private boolean comprobarNuevoPassword() {
        // compilamos la expresion regular.(Una letra mañuscula o minuscula o un numero del 0 al 9 de 4 a 20 caracteres).
		Pattern passPat = Pattern.compile("^[\\S]{4,20}$");
        Matcher passMatch = passPat.matcher(nuevoPassword.get());

        if(!passMatch.matches()) {
            mensajeAviso(
                AlertType.ERROR,
                "Error nuevo password",
                "",
                "El password de usuario tiene que contener de 4 a 20 caracteres.\n"
                    + "No puede contener espacios en blanco.");

            return false;
        } else if(!nuevoPassword.get().equals(confimarPassword.get())) {
            mensajeAviso(
                AlertType.ERROR,
                "Error confirmaci\u00F3n password",
                "",
                "El password de confirmación no conincide con el nuevo password.");

            return false;
        } else if(usuario.getPassword().equals(nuevoPassword.get())) {
            mensajeAviso(
                AlertType.ERROR,
                "Error password",
                "",
                "El nuevo password de usuario es igual\nque el password actual de usuario."
                    + "\nEl nuevo password tiene que ser difente al password actual.");

            return false;
        } else {
            return true;
        }
    }


    /**
     * Muestra una ventana de dialogo con la informacion pasada como parametros.
     * 
     * @param tipo Tipo de alerta.
     * @param tiutlo Titulo de la ventana.
     * @param cabecera Cabecera del mensaje.
     * @param cuerpo Cuerpo del menesaje.
     */
    private void mensajeAviso(AlertType tipo, String tiutlo, String cabecera, String cuerpo) {
        Alert alerta = new Alert(tipo);
        alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
        alerta.setTitle(tiutlo);
        alerta.initOwner((Stage) apCambioPassword.getScene().getWindow());
        alerta.setHeaderText(cabecera);
        alerta.setContentText(cuerpo);
        alerta.initStyle(StageStyle.DECORATED);
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.showAndWait();
    }
    

    /**
     * Inicializa los componentes de la interfaz de usuario con los datos del usuario actual.
     * Establece el valor del Label lbIdUsuario con el ID del usuario.
     * Establece el valor del Label lbNombreUsuario con el nombre de usuario.
     */
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
}
