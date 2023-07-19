package controlador;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modelo.Alumno;
import modelo.Toast;
import modelo.Usuario;
import utilidades.Constants;
import utilidades.Correo;

public class AlumnoFormNotificacionControlador implements Initializable {

    private Logger logUser;
    private Toast toast;
    private File archivoAdjunto;
    private Alumno newAlumno;
    private Usuario newUsuario;
    private ObservableList<String> tipoEmailCopia;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnEnviar;

    @FXML
    private ComboBox<String> cbEmailCopia;

    @FXML
    private CheckBox chekbAdjunto;

    @FXML
    private CheckBox chekbCopiaOculta;

    @FXML
    private GridPane gpFormNotificacion;

    @FXML
    private ImageView ivAdjunto;

    @FXML
    private ImageView ivImagenTipoFormulario;

    @FXML
    private Label lbTitulo;

    @FXML
    private Pane pSeparador;

    @FXML
    private TextArea taMensaje;

    @FXML
    private TextField tfAsunto;

    @FXML
    private TextField tfEmailAlumno;

    @FXML
    private TextField tfEmailCopia;

    @FXML
    private TextField tfEmailUsuario;

    @FXML
    private TextField tfNombreAlumno;

    @FXML
    private TextField tfNombreUsuario;

    @FXML
    private TextField tfRutaAdjunto;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Añadir clases de estilo CSS a elementos.
        btnCancelar.getStyleClass().add("boton_rojo"); 
        pSeparador.getStyleClass().add("panelSeparador"); //Panel separador de barra superior.

        //Establecer imagen en ImageView.
        Image Notificacion;
        Image adjunto;
        try {
            //Intentar cargar la imagen desde el recurso en el IDE y en el JAR.
            Notificacion = new Image(getClass().getResourceAsStream("/recursos/email_2_128.png")); //Forma desde IDE y JAR.
            adjunto = new Image(getClass().getResourceAsStream("/recursos/clip_1_48.png"));
        } catch (Exception e) {
            //Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR.
            Notificacion = new Image("/recursos/email_2_128.png"); //Forma desde el JAR.
            adjunto = new Image("/recursos/clip_1_48.png");
        }
        //Establecer las imagenes cargadas en los ImageView.
        ivImagenTipoFormulario.setImage(Notificacion);
        ivAdjunto.setImage(adjunto);
        
        logUser = Logger.getLogger(Constants.USER); //Crea una instancia de la clase Logger asociada al nombre de registro.
        toast = new Toast();

        //Configura los controles.
        configurarComboBox();
        configurarTextField();
        configurarCheckBox();

        ivAdjunto.setDisable(true); //Deshabilitado por defecto.

        //Configurar un evento de clic del ratón para el botón "Cancelar".
        btnCancelar.setOnMouseClicked(e -> {
            ((Stage) gpFormNotificacion.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
        });

        //Configurar un evento de clic del ratón para el botón "Enviar".
        btnEnviar.setOnMouseClicked(e -> {
            
            if(comprobarCampos()) {
                toast.show((Stage) gpFormNotificacion.getScene().getWindow(), "Enviando Notificación, Espere.");
                gpFormNotificacion.setCursor(Cursor.WAIT);
                deshabilitarControles();
                enviarCorreo();
            } else {
                toast.show((Stage) gpFormNotificacion.getScene().getWindow(), "Fallo al enviar la notificación.");
            }
        });

        //Configurar un evento de clic del ratón para el botón(Imagen) "adjuntar archivo".
        ivAdjunto.setOnMouseClicked(e -> {
            buscarArchivo();
        });
    }


    /**
     * Envía un correo electrónico con la notificación correspondiente, dependiendo de las opciones seleccionadas.
     * Si se elige la opción de copia oculta y/o adjuntar archivo, se enviará un correo con los datos suministrados.
     * Una vez completado el envío, se mostrará una notificación en la interfaz gráfica y se cerrará la ventana actual.
     * En caso de error durante el envío, se mostrará una notificación de fallo y se registrarán los detalles en el log de advertencias.
     * Si ocurre una excepción durante el proceso de envío del correo, se habilitarán los controles y se registrarán los detalles en el log de errores.
     * 
     */
    private void enviarCorreo() {
        try {
            // Comprobar si se ha seleccionado la opción de copia oculta.
            if (chekbCopiaOculta.isSelected()) {

                // Comprobar si se ha seleccionado la opción de adjuntar archivo.
                if (chekbAdjunto.isSelected()) {

                    Correo.enviarCorreoMultiparte(newAlumno.getEmail(), tfEmailCopia.getText(), tfAsunto.getText(),
                            taMensaje.getText(),
                            Constants.EMAIL_HTML, archivoAdjunto, newUsuario.getEmailApp(),
                            newUsuario.getPasswordEmailApp(), (exito, mensaje) -> {
                                // Si el correo se envió correctamente.
                                if (exito) {
                                    Platform.runLater(() -> {
                                        // Registrar el envío exitoso en el log de información.
                                        logUser.info("Notificación enviada. (Id_Alumno: " + newAlumno.getId() + ", Nombre: " + newAlumno.getNombre() + ")");

                                        // Mostrar una notificación de éxito en la interfaz gráfica.
                                        toast.show((Stage) ((Stage) gpFormNotificacion.getScene().getWindow()).getOwner(),"Notificación Enviada!.");

                                        // Cerrar la ventana actual después de enviar el correo.
                                        ((Stage) gpFormNotificacion.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
                                    });
                                } else {
                                    Platform.runLater(() -> {
                                        // Mostrar una notificación de error en la interfaz gráfica.
                                        toast.show((Stage) gpFormNotificacion.getScene().getWindow(),"Fallo al enviar la notificación.");

                                        // Habilitar controles y restablecer el cursor.
                                        habilitarControles();
                                        gpFormNotificacion.setCursor(Cursor.DEFAULT);

                                        // Registrar el fallo en el log de advertencias.
                                        logUser.warning("Fallo al enviar notificación: (Id_Alumno: " + newAlumno.getId() 
                                            + ", Nombre: " + newAlumno.getNombre() + ")\n ERROR: " + mensaje);
                                    });
                                }
                            });

                } else {
                    // Enviar correo con copia oculta pero sin archivo adjunto.
                    Correo.enviarCorreoMultiparte(newAlumno.getEmail(), tfEmailCopia.getText(), tfAsunto.getText(),
                            taMensaje.getText(),
                            Constants.EMAIL_HTML, newUsuario.getEmailApp(), newUsuario.getPasswordEmailApp(),
                            (exito, mensaje) -> {
                                // Si el correo se envió correctamente.
                                if (exito) {
                                    Platform.runLater(() -> {
                                        // Registrar el envío exitoso en el log de información.
                                        logUser.info("Notificación enviada. (Id_Alumno: " + newAlumno.getId()
                                                + ", Nombre: " + newAlumno.getNombre() + ")");

                                        // Mostrar una notificación de éxito en la interfaz gráfica.
                                        toast.show((Stage) ((Stage) gpFormNotificacion.getScene().getWindow()).getOwner(),"Notificación Enviada!.");

                                        // Cerrar la ventana actual después de enviar el correo.
                                        ((Stage) gpFormNotificacion.getScene().getWindow()).close(); // Obtener la referencia al Stage actual y cerrarlo.
                                    });
                                } else {
                                    Platform.runLater(() -> {
                                        // Mostrar una notificación de error en la interfaz gráfica.
                                        toast.show((Stage) gpFormNotificacion.getScene().getWindow(),"Fallo al enviar la notificación.");

                                        // Habilitar controles y restablecer el cursor.
                                        habilitarControles();
                                        gpFormNotificacion.setCursor(Cursor.DEFAULT);

                                        // Registrar el fallo en el log de advertencias.
                                        logUser.warning("Fallo al enviar notificación: (Id_Alumno: " + newAlumno.getId()
                                                + ", Nombre: " + newAlumno.getNombre() + ")\n ERROR: " + mensaje);
                                    });
                                }
                            });

                }

            } else {
                // Enviar correos sin copia oculta.

                // Comprobar si se ha seleccionado la opción de adjuntar archivo.
                if (chekbAdjunto.isSelected()) {
                    // Enviar correos sin copia oculta pero con archivo adjunto.
                    Correo.enviarCorreoMultiparte(newAlumno.getEmail(), tfAsunto.getText(), taMensaje.getText(),
                            Constants.EMAIL_HTML, archivoAdjunto, newUsuario.getEmailApp(), newUsuario.getPasswordEmailApp(),
                            (exito, mensaje) -> {
                                // Si el correo se envió correctamente.
                                if (exito) {
                                    Platform.runLater(() -> {
                                        // Registrar el envío exitoso en el log de información.
                                        logUser.info("Notificación enviada. (Id_Alumno: " + newAlumno.getId()
                                                + ", Nombre: " + newAlumno.getNombre() + ")");

                                        // Mostrar una notificación de éxito en la interfaz gráfica.
                                        toast.show((Stage) ((Stage) gpFormNotificacion.getScene().getWindow()).getOwner(),"Notificación Enviada!.");

                                        // Cerrar la ventana actual después de enviar el correo.
                                        ((Stage) gpFormNotificacion.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
                                    });
                                } else {
                                    Platform.runLater(() -> {
                                        // Mostrar una notificación de éxito en la interfaz.
                                        toast.show((Stage) gpFormNotificacion.getScene().getWindow(),"Fallo al enviar la notificación.");

                                        // Habilitar controles y restablecer el cursor.
                                        habilitarControles();
                                        gpFormNotificacion.setCursor(Cursor.DEFAULT);

                                        // Registrar el fallo en el log de advertencias.
                                        logUser.warning("Fallo al enviar notificación: (Id_Alumno: " + newAlumno.getId()
                                                + ", Nombre: " + newAlumno.getNombre() + ")\n ERROR: " + mensaje);
                                    });
                                }
                            });
                } else {
                    // Enviar correos sin copia oculta y sin archivo adjunto.
                    Correo.enviarCorreoMultiparte(newAlumno.getEmail(), tfAsunto.getText(), taMensaje.getText(),
                            Constants.EMAIL_HTML, newUsuario.getEmailApp(), newUsuario.getPasswordEmailApp(),
                            (exito, mensaje) -> {
                                // Si el correo se envió correctamente.
                                if (exito) {
                                    Platform.runLater(() -> {
                                        // Registrar el envío exitoso en el log de información.
                                        logUser.info("Notificación enviada. (Id_Alumno: " + newAlumno.getId()
                                                + ", Nombre: " + newAlumno.getNombre() + ")");

                                        // Mostrar una notificación de éxito en la interfaz gráfica.
                                        toast.show((Stage) ((Stage) gpFormNotificacion.getScene().getWindow()).getOwner(),"Notificación Enviada!.");

                                        // Cerrar la ventana actual después de enviar el correo.
                                        ((Stage) gpFormNotificacion.getScene().getWindow()).close(); // Obtener la referencia al Stage actual y cerrarlo.
                                    });
                                } else {
                                    Platform.runLater(() -> {
                                        // Mostrar una notificación de éxito en la interfaz.
                                        toast.show((Stage) gpFormNotificacion.getScene().getWindow(),"Fallo al enviar la notificación.");

                                        // Habilitar controles y restablecer el cursor.
                                        habilitarControles();
                                        gpFormNotificacion.setCursor(Cursor.DEFAULT);

                                        // Registrar el fallo en el log de advertencias.
                                        logUser.warning("Fallo al enviar notificación: (Id_Alumno: " + newAlumno.getId()
                                                + ", Nombre: " + newAlumno.getNombre() + ")\n ERROR: " + mensaje);
                                    });
                                }
                            });
                }
            }
        } catch (Exception e) {
            // Si ocurre una excepción durante el envío del correo
            habilitarControles();

            // Registrar la excepción en el log de errores.
            logUser.severe("Excepción al enviar notificación: " + e.toString());
            e.printStackTrace();
        }
    }


    /**
     * Muestra un cuadro de diálogo de selección de archivos para elegir un archivo y carga la ruta del archivo seleccionado
     * en un TextField.
     * 
     */
    private void buscarArchivo() {
        //Crea el objeto fileChooser y le añade las extensiones posible para elegir.
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Insertar archivo");
    	fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*"),
                new FileChooser.ExtensionFilter("Imagenes", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif", "*.eps", "*.svg"),
				new FileChooser.ExtensionFilter("png (*.png)", "*.png"),
                new FileChooser.ExtensionFilter("txt (*.txt)", "*.txt"),
                new FileChooser.ExtensionFilter("pdf (*.pdf)", "*.pdf"),
                new FileChooser.ExtensionFilter("xml (*.xml)", "*.xml"));
				
		
        fileChooser.setInitialDirectory(new File(".")); //Directorio donde inicia la ventana.
    	
    	File ArchivoSeleccionado = fileChooser.showOpenDialog((Stage) gpFormNotificacion.getScene().getWindow()); //Muestra la ventana. 
		
		//Carga la ruta del fichero en el TextField tfRutaAdjunto.
		if(ArchivoSeleccionado != null && ArchivoSeleccionado.isFile()) {
			tfRutaAdjunto.setText(ArchivoSeleccionado.getPath());
		}
    }


    /**
     * Método que configura los ComboBox y sus escuchas.
     * 
     */
    private void configurarComboBox() {
        tipoEmailCopia = FXCollections.observableArrayList();
        tipoEmailCopia.addAll(Constants.EMAIL_APP, Constants.EMAIL_OTHER);
        cbEmailCopia.setItems(tipoEmailCopia);
        cbEmailCopia.setDisable(true);
        
        cbEmailCopia.getSelectionModel().selectedItemProperty().addListener((o, nv, ov) -> {
            if (ov.equals(Constants.EMAIL_APP)) {
                tfEmailCopia.setDisable(true);
                tfEmailCopia.setText(newUsuario.getEmailApp());
            } else if(ov.equals(Constants.EMAIL_OTHER)) {
                tfEmailCopia.setDisable(false);
                tfEmailCopia.clear();
            } else {
                //Este caso es si se ha añadido el email de usuario personal.
                tfEmailCopia.setDisable(true);
                tfEmailCopia.setText(newUsuario.getEmail());
            }
        });
    }


    /**
     * Método que configura los TextField.
     * 
     */
    private void configurarTextField() {
        taMensaje.setText("");
        tfAsunto.setText("");
        tfNombreUsuario.setDisable(true);
        tfEmailUsuario.setDisable(true);
        tfEmailCopia.setDisable(true);
        tfNombreAlumno.setDisable(true);
        tfEmailAlumno.setDisable(true);
        tfRutaAdjunto.setDisable(true);
    }


    /**
     * Método que configura los CheckBox y sus escuchas.
     * 
     */
    private void configurarCheckBox() {

        chekbCopiaOculta.selectedProperty().addListener(e -> {
            if (chekbCopiaOculta.isSelected()) {
                cbEmailCopia.setDisable(false);
                if(cbEmailCopia.getValue().equals(Constants.EMAIL_OTHER)) {
                    tfEmailCopia.setDisable(false);
                } else {
                    tfEmailCopia.setDisable(true);
                }
            } else {
                cbEmailCopia.setDisable(true);
                tfEmailCopia.setDisable(true);
            }
        });

        chekbAdjunto.selectedProperty().addListener(e -> {
            if (chekbAdjunto.isSelected()) {
                tfRutaAdjunto.setDisable(false);
                ivAdjunto.setDisable(false);
            } else {
                tfRutaAdjunto.setDisable(true);
                ivAdjunto.setDisable(true);
            }
        });
    }


    /**
     * Deshabilita los controles del formulario.
     * 
     */
    private void deshabilitarControles() {
        btnCancelar.setDisable(true);
        btnEnviar.setDisable(true);
        chekbCopiaOculta.setDisable(true);
        chekbAdjunto.setDisable(true);

        tfAsunto.setDisable(true);
        taMensaje.setDisable(true);

        if (chekbCopiaOculta.isSelected()) {
            cbEmailCopia.setDisable(true);
            tfEmailCopia.setDisable(true);
        }

        if (chekbAdjunto.isSelected()) {
            tfRutaAdjunto.setDisable(true);
            ivAdjunto.setDisable(true);
        }
    }


    /**
     * Habilita los controles del formulario.
     * 
     */
    private void habilitarControles() {
        btnCancelar.setDisable(false);
        btnEnviar.setDisable(false);
        chekbCopiaOculta.setDisable(false);
        chekbAdjunto.setDisable(false);

        tfAsunto.setDisable(false);
        taMensaje.setDisable(false);

        if (chekbCopiaOculta.isSelected()) {
            cbEmailCopia.setDisable(false);
            tfEmailCopia.setDisable(false);
        }

        if (chekbAdjunto.isSelected()) {
            tfRutaAdjunto.setDisable(false);
            ivAdjunto.setDisable(false);
        }
    }


    /**
     * Comprueba si los campos del formulario son válidos.
     *
     * @return true si los campos son válidos, de lo contrario, false.
     */
    private boolean comprobarCampos() {
        boolean camposCorrectos = false;

        //Patrón para validar el formato del email.
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        Matcher emaiMatcher = emailPattern.matcher((tfEmailCopia.getText() == null) ? "" : tfEmailCopia.getText());

        if(chekbCopiaOculta.isSelected() && !emaiMatcher.matches()) {
            mensajeAviso(Alert.AlertType.ERROR,
            "Email copia No valido.",
            "",
            "El Email introducido en Email Copia no es valido.");
        } else if (tfAsunto.getText().isBlank()) {
            mensajeAviso(Alert.AlertType.ERROR,
            "Asunto Email.",
            "",
            "No has introducido ningun asunto para la notificación.");
        } else if (taMensaje.getText().isBlank()) {
            mensajeAviso(Alert.AlertType.ERROR,
            "Texto Notificación No valido.",
            "",
            "No has introducido ningun mensaje para la notificación.");
        } else if (chekbAdjunto.isSelected() && tfRutaAdjunto.getText().isBlank()) {
            mensajeAviso(Alert.AlertType.ERROR,
            "Archivo adjunto.",
            "",
            "No has introducido ninguna ruta de archivo.");
        } else {
            camposCorrectos = true;
        }

        //Comprueba que la ruta que hay cargada en el TextField tfRutaAdjunto sea correcta.
        if(camposCorrectos && chekbAdjunto.isSelected() && !tfRutaAdjunto.getText().isBlank()) {
            archivoAdjunto = new File(tfRutaAdjunto.getText());
            if(!archivoAdjunto.exists() && !archivoAdjunto.isFile()) {
                camposCorrectos = false;

                mensajeAviso(Alert.AlertType.ERROR,
                "Archivo adjunto.",
                "",
                "La ruta introducida para el archivo adjunto no es valida.");
            }
        }

        return camposCorrectos;
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
        alerta.initOwner((Stage) gpFormNotificacion.getScene().getWindow());
        alerta.setHeaderText(cabecera);
        alerta.setContentText(cuerpo);
        alerta.initStyle(StageStyle.DECORATED);
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.showAndWait();
    }


    /**
     * Establece los objetos Alumno y Usuario. Actualiza los componentes de la interfaz gráfica con sus valores.
     *
     * @param alumno El objeto Alumno a establecer.
     * @param usuario El objeto Usuario a establecer.
     */
    public void setModelos(Alumno alumno, Usuario usuario) {
        newAlumno = (Alumno)(alumno.clone());
        newUsuario = new Usuario(usuario);  //Crear una nueva instancia de Usuario basada en el objeto usuario pasado como parámetro.

        ///Configura los enlaces de datos entre los campos de texto y las propiedades de newUsuario.
        tfNombreUsuario.textProperty().bind(Bindings.concat(newUsuario.nombreProperty(), " ", newUsuario.apellido1Property(), " ", newUsuario.apellido2Property()));
        tfEmailUsuario.textProperty().bind(newUsuario.emailAppProperty());
        
        tfNombreAlumno.textProperty().bind(Bindings.concat(newAlumno.nombreProperty(), " ", newAlumno.apellido1Property(), " ", newAlumno.apellido2Property()));
        tfEmailAlumno.textProperty().bindBidirectional(newAlumno.emailProperty());

        if(newUsuario.getEmail() == null || newUsuario.getEmail().isBlank()) {
            cbEmailCopia.setValue(Constants.EMAIL_APP); //Establece valor por defecto.
        } else {
            tipoEmailCopia.add(Constants.EMAIL_USER);     //Añadir elemento a ObservableList de cbEmailCopia
            cbEmailCopia.setValue(Constants.EMAIL_USER); //Establece valor por defecto.
        } 
    }


}
