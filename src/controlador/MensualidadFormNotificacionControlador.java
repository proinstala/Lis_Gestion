package controlador;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modelo.Alumno;
import modelo.EstadoPago;
import modelo.Mensualidad;
import modelo.Toast;
import modelo.Usuario;
import utilidades.Constants;
import utilidades.Correo;


/**
 * Controlador para el formulario de notificación de mensualidad.
 * 
 * @author David Jimenez Alonso.
 */
public class MensualidadFormNotificacionControlador implements Initializable {

    private final String ASUNTO_PAGADA = "Notificación pago mensualidad. ";
    private final String ASUNTO_PENDIENTE = "Notificación mensualidad pendiente. ";
    

    private Logger logUser;
    private Toast toast;
    private DecimalFormat decimalFormat;
    private DateTimeFormatter formatter;
    private ObservableList<String> tipoEmailCopia;

    private Alumno newAlumno;
    private Mensualidad newMensualidad;
    private Usuario newUsuario;
    
    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnEnviar;

    @FXML
    private ComboBox<String> cbEmailCopia;

    @FXML
    private ComboBox<EstadoPago> cbTipoNotificacion;

    @FXML
    private CheckBox chekbCopiaOculta;

    @FXML
    private CheckBox chekbMensajeEditable;

    @FXML
    private GridPane gpFormNotificacion;

    @FXML
    private Pane pSeparador;

    @FXML
    private ImageView ivImagenTipoFormulario;

    @FXML
    private Label lbTitulo;

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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Añadir clases de estilo CSS a elementos.
        btnCancelar.getStyleClass().add("boton_rojo"); 
        pSeparador.getStyleClass().add("panelSeparador"); //Panel separador de barra superior.

        //Establecer imagen en ImageView.
        Image Notificacion;
        try {
            //Intentar cargar la imagen desde el recurso en el IDE y en el JAR.
            Notificacion = new Image(getClass().getResourceAsStream("/recursos/email_2_128.png")); //Forma desde IDE y JAR.
        } catch (Exception e) {
            //Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR.
            Notificacion = new Image("/recursos/email_2_128.png"); //Forma desde el JAR.
        }
        //Establecer las imagenes cargadas en los ImageView.
        ivImagenTipoFormulario.setImage(Notificacion);
        
        logUser = Logger.getLogger(Constants.USER); //Crea una instancia de la clase Logger asociada al nombre de registro.
        toast = new Toast();

        //Formatos para mostrar la fecha y para el importe.
        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); //Crear un formateador de fecha con el patrón "dd/MM/yyyy".
        decimalFormat = new DecimalFormat("#0.00");

        configurarComboBox();
        configurarTextField();
        configurarCheckBox();

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
    }


    /**
     * Método que configura los ComboBox y sus escuchas.
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
        

        cbTipoNotificacion.setItems(FXCollections.observableArrayList(EstadoPago.values()));
        cbTipoNotificacion.setDisable(true);

        cbTipoNotificacion.valueProperty().addListener(e -> {
            configuracionMensaje(); //Configura el asunto y texto del la Notificación.
        });
    }


    /**
     * Método que configura los TextField.
     */
    private void configurarTextField() {
        tfNombreUsuario.setDisable(true);
        tfEmailUsuario.setDisable(true);
        tfEmailCopia.setDisable(true);
        tfNombreAlumno.setDisable(true);
        tfEmailAlumno.setDisable(true);
        tfAsunto.setDisable(true);
        taMensaje.setDisable(true);
    }


    /**
     * Método que configura los CheckBox y sus escuchas.
     */
    private void configurarCheckBox() {
        chekbMensajeEditable.setSelected(false);

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

        chekbMensajeEditable.selectedProperty().addListener(e -> {
            if(chekbMensajeEditable.isSelected()) {
                tfAsunto.setDisable(false);;
                taMensaje.setDisable(false);
                //cbTipoNotificacion.setDisable(false); //El tipo de notificación se establece automatico segun la mensualidad.
            } else {
                tfAsunto.setDisable(true);
                taMensaje.setDisable(true);
                //cbTipoNotificacion.setDisable(true); //El tipo de notificación se establece automatico segun la mensualidad.
            }
        });
    } 

    /**
     * Método que deshabilita los controles del formulario.
     */
    private void deshabilitarControles() {
        btnCancelar.setDisable(true);
        btnEnviar.setDisable(true);
        chekbCopiaOculta.setDisable(true);
        chekbMensajeEditable.setDisable(true);

        if (chekbCopiaOculta.isSelected()) {
            cbEmailCopia.setDisable(true);
            tfEmailCopia.setDisable(true);
        }

        if (chekbMensajeEditable.isSelected()) {
            tfAsunto.setDisable(true);
            taMensaje.setDisable(true);
        }
    }


    /**
     * Método que habilita los controles del formulario.
     */
    private void habilitarControles() {
        btnCancelar.setDisable(false);
        btnEnviar.setDisable(false);
        chekbCopiaOculta.setDisable(false);
        chekbMensajeEditable.setDisable(false);

        if (chekbCopiaOculta.isSelected()) {
            cbEmailCopia.setDisable(false);
            tfEmailCopia.setDisable(false);
        }

        if (chekbMensajeEditable.isSelected()) {
            tfAsunto.setDisable(false);
            taMensaje.setDisable(false);
        }
    }

    /**
     * Configura el mensaje de notificación según el tipo de notificación seleccionado.
     * Si el tipo de notificación es PENDIENTE, se configura el mensaje de notificación para informar
     * al alumno sobre una mensualidad pendiente de pago.
     * Si el tipo de notificación es PAGADO, se configura el mensaje de notificación para agradecer al alumno
     * por su pago puntual y presentar el recibo correspondiente al pago de la mensualidad.
     * Si el tipo de notificación no corresponde a ninguno de los casos anteriores, se limpian los campos de mensaje y asunto.
     */
    private void configuracionMensaje() {
        String mensaje;
        switch (cbTipoNotificacion.getValue()) {
            case PENDIENTE -> {
                mensaje = 
                        "Buenos Dias.\n" + 
                        "Estimado/a, " + newAlumno.getNombreCompleto() + "\n" +
                        "\n" +
                        "Soy " + newUsuario.getNombre() + ", la instructora y encargada de Lis Pilates.\n" + 
                        "Me pongo en contacto contigo para recordarte amablemente que tienes una mensualidad pendiente de pago en nuestro centro." + "\n" + 
                        "\n" +

                        "Fecha de emisión: " + newMensualidad.getFecha().atDay(1).format(formatter) + "\n" +
                        "Mensualidad: " + newMensualidad.getMesMensualidad_ES() + "/" + newMensualidad.getFecha().getYear() + "\n" +
                        "Importe a abonar: " + decimalFormat.format(newMensualidad.getImporte()) + "\n" + 
                        "\n" +

                        "Quiero comenzar diciéndote que valoro mucho tu compromiso con tu bienestar y tu confianza en Lis Pilates." + "\n" + 
                        "Sin embargo, nos esforzamos por mantener un ambiente equitativo para todos nuestros clientes y necesitamos" + "\n" +  
                        "asegurarnos de que todas las cuentas estén al día.\n" +
                        "\n" + 

                        "Entiendo que en ocasiones pueden surgir situaciones imprevistas o simplemente olvidos involuntarios. Sin embargo," + "\n" + 
                        "es importante mantener un ambiente equitativo para todos nuestros clientes y asegurarnos de que las cuentas estén al día." + "\n" + 
                        "\n" +

                        "Te invito a que tomes un momento para revisar tu estado de cuenta y, si aún no lo has hecho, procedas a realizar el pago" + "\n" + 
                        "correspondiente a tu mensualidad. Puedes hacerlo visitándome en el centro durante los horarios de clase o a traves de formas" + "\n" + 
                        "habituales de pago (bizun, transferencia, pago en efectivo.)." + "\n" + 
                        "\n" +

                        "Recuerda que en Lis Pilates nos esforzamos por ofrecerte una experiencia única, donde el cuidado de tu cuerpo y mente" + "\n" + 
                        "es nuestra prioridad. Al mantener al día tus pagos, contribuyes a que pueda seguir brindádote el mejor servicio" + "\n" + 
                        "y a mantener nuestras instalaciones en óptimas condiciones." + "\n" + 
                        "\n" + 

                        "Si tienes alguna pregunta, inquietud o necesitas más información sobre tu estado de cuenta, no dudes en comunicarte directamente conmigo." + "\n" + 
                        "Estoy aquí para ayudarte y encontrar soluciones adecuadas a su situación particular.\n" + 
                        "\n" + 

                        "Agradezco sinceramente tu atención y espero contar con tu pronta respuesta. Valoramos tu confianza en Lis Pilates y esperamos continuar" + "\n" + 
                        "siendo parte de su camino hacia el bienestar físico y mental.\n" + 
                        "\n" + 

                        "Que tengas un maravilloso día!" + "\n" + 
                        "\n\n" +

                        "Atentamente,\n" +
                        newUsuario.getNombreCompleto() + "\n" + 
                        "Instructora y encargada de Lis Pilates\n" + 
                        newUsuario.getTelefono() + "\n" + 
                        newUsuario.getEmailApp();
                
                taMensaje.setText(mensaje);
                tfAsunto.setText(ASUNTO_PENDIENTE);
            }
            case PAGADO -> {
                mensaje = 
                "Estimado/a " + newAlumno.getNombre() + ",\n" +
                "¡Gracias por tu pago puntual! A continuación, te presentamos el recibo correspondiente al pago de tu mensualidad en Lis Pilates:" + "\n" + 
                "\n" +

                "------------------------------------------------------------------------------------------------------------------------------------\n" +
                "Recibo de pago - Lis Pilates" + "\n" +
                "\n" +

                "Cliente: " + newAlumno.getNombreCompleto() + "\n" +
                "Fecha de emisión: " + newMensualidad.getFecha().atDay(1).format(formatter) + "\n" +
                "Mensualidad correspodiente a: " + newMensualidad.getMesMensualidad_ES() + "/" + newMensualidad.getFecha().getYear() + "\n" + 
                "\n" +
                
                "Descripción:\n" +
                "Pago de mensualidad correspondiente a " + newMensualidad.getMesMensualidad_ES() + "/" + newMensualidad.getFecha().getYear() + "\n" + 
                "\n" +

                "Detalles del Pago:\n" +
                "Fecha de pago: " + newMensualidad.getFechaPago().format(formatter) + "\n" +
                "Forma de pago: " + newMensualidad.getFormaPago() + "\n" +
                "Importe pagado: " + decimalFormat.format(newMensualidad.getImporte()) + "\n" +
                "------------------------------------------------------------------------------------------------------------------------------------\n" +
                "\n" +

                "Apreciamos sinceramente tu compromiso con tu bienestar y tu confianza en Lis Pilates. Si tienes alguna pregunta o necesitas" + "\n" + 
                "más información, no dudes en comunicarte conmigo.\n" +
                "\n" +

                "¡Gracias nuevamente por tu pago puntual! Valoramos tu apoyo y esperamos seguir siendo parte de tu camino hacia una vida más saludable y equilibrada." + "\n" + 
                "\n\n" +
                
                "Atentamente,\n" +
                newUsuario.getNombreCompleto() + "\n" + 
                "Instructora y encargada de Lis Pilates\n" + 
                newUsuario.getTelefono() + "\n" + 
                newUsuario.getEmailApp();

                taMensaje.setText(mensaje);
                tfAsunto.setText(ASUNTO_PAGADA);
            }     
            default -> {
                taMensaje.setText("");
                tfAsunto.setText("");
            }
        }
    }


    /**
     * Envía un correo electrónico notificando a un alumno sobre una mensualidad mediante contenido multiparte.
     *
     * @return true si el correo se envió correctamente, de lo contrario, false.
     */
    private void enviarCorreo() {
        try {
            //Comprobar si se ha seleccionado la opción de copia oculta.
            if (chekbCopiaOculta.isSelected()) {
                
                //Enviar correo con copia oculta.
                Correo.enviarCorreoMultiparte(newAlumno.getEmail(), tfEmailCopia.getText(), tfAsunto.getText(),
                        taMensaje.getText(),
                        Constants.EMAIL_HTML, newUsuario.getEmailApp(), newUsuario.getPasswordEmailApp(), (exito, mensaje) -> {
                            //Si el correo se envió correctamente.
                            if (exito) {
                                Platform.runLater(() -> {
                                    //Registrar el envío exitoso en el log de información.
                                    logUser.info("Notificación enviada. (Id_Alumno: " + newAlumno.getId() + ", Nombre: " + newAlumno.getNombre()
                                            + ")--(Id_Mensualidad: " + newMensualidad.getId() + ")");
                                    
                                    //Mostrar una notificación de éxito en la interfaz gráfica.
                                    toast.show((Stage) ((Stage) gpFormNotificacion.getScene().getWindow()).getOwner(),"Notificación Enviada!.");
                                    
                                    // Cerrar la ventana actual después de enviar el correo.
                                    ((Stage) gpFormNotificacion.getScene().getWindow()).close(); // Obtener la referencia al Stage actual y cerrarlo.
                                });
                            } else {
                                Platform.runLater(() -> {
                                    //Mostrar una notificación de error en la interfaz gráfica.
                                    toast.show((Stage) gpFormNotificacion.getScene().getWindow(),"Fallo al enviar la notificación.");
                                    
                                    //Habilitar controles y restablecer el cursor.
                                    habilitarControles();
                                    gpFormNotificacion.setCursor(Cursor.DEFAULT);
                                    
                                    //Registrar el fallo en el log de advertencias.
                                    logUser.warning("Fallo al enviar notificación: (Id_Alumno: " + newAlumno.getId()
                                            + ", Nombre: " + newAlumno.getNombre()
                                            + ")--(Id_Mensualidad: " + newMensualidad.getId() + ")\n ERROR: " + mensaje);
                                });
                            }
                        });
            } else {
                //Enviar correo sin copia oculta.
                Correo.enviarCorreoMultiparte(newAlumno.getEmail(), tfAsunto.getText(), taMensaje.getText(),
                        Constants.EMAIL_HTML, newUsuario.getEmailApp(), newUsuario.getPasswordEmailApp(), (exito, mensaje) -> {
                            //Si el correo se envió correctamente.
                            if (exito) {
                                Platform.runLater(() -> {
                                    //Registrar el envío exitoso en el log de información.
                                    logUser.info("Notificación enviada. (Id_Alumno: " + newAlumno.getId() + ", Nombre: " + newAlumno.getNombre()
                                            + ")--(Id_Mensualidad: " + newMensualidad.getId() + ")");
                                    
                                    //Mostrar una notificación de éxito en la interfaz gráfica.
                                    toast.show((Stage) ((Stage) gpFormNotificacion.getScene().getWindow()).getOwner(),"Notificación Enviada!.");
                                    
                                    //Cerrar la ventana actual después de enviar el correo.
                                    ((Stage) gpFormNotificacion.getScene().getWindow()).close(); // Obtener la referencia al Stage actual y cerrarlo.
                                });
                            } else {
                                Platform.runLater(() -> {
                                    //Mostrar una notificación de éxito en la interfaz. 
                                    toast.show((Stage) gpFormNotificacion.getScene().getWindow(),"Fallo al enviar la notificación.");
                                    
                                    //Habilitar controles y restablecer el cursor.
                                    habilitarControles();
                                    gpFormNotificacion.setCursor(Cursor.DEFAULT);
                                    
                                    //Registrar el fallo en el log de advertencias.
                                    logUser.warning("Fallo al enviar notificación: (Id_Alumno: " + newAlumno.getId()
                                            + ", Nombre: " + newAlumno.getNombre()
                                            + ")--(Id_Mensualidad: " + newMensualidad.getId() + ")\n ERROR: " + mensaje);
                                });
                            }
                        });
            }

        } catch (Exception e) {
            //Si ocurre una excepción durante el envío del correo
            habilitarControles();

            //Registrar la excepción en el log de errores.
            logUser.severe("Excepción al enviar notificación: " + e.toString());
            e.printStackTrace();
        }
    }


    /**
     * Método que comprueba si los campos del formulario son válidos.
     *
     * @return true si los campos son válidos, de lo contrario, false.
     */
    private boolean comprobarCampos() {
        boolean camposCorrectos = false;

        //Patrón para validar el formato del email.
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        Matcher emaiMatcher = emailPattern.matcher((tfEmailCopia.getText() == null) ? "" : tfEmailCopia.getText());

        if(chekbCopiaOculta.isSelected() && !emaiMatcher.matches()) {
            mensajeAviso("Email copia No valido.",
            "",
            "El Email introducido en Email Copia no es valido.");
        } else if (tfAsunto.getText().isBlank()) {
            mensajeAviso("Asunto Email.",
            "",
            "No has introducido ningun asunto para la notificación.");
        } else if (taMensaje.getText().isBlank()) {
            mensajeAviso("Texto Notificación No valido.",
            "",
            "No has introducido ningun mensaje para la notificación.");
        } else {
            camposCorrectos = true;
        }
        return camposCorrectos;
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
        alerta.initOwner((Stage) gpFormNotificacion.getScene().getWindow());
        alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
        alerta.setTitle(tiutlo);
        alerta.setHeaderText(cabecera);
        alerta.setContentText(cuerpo);
        alerta.initStyle(StageStyle.DECORATED);
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.showAndWait();
    }


    /**
     * Establece el objeto Mensualidad, Alumno y Usuario. Actualiza los componentes de la interfaz gráfica con sus valores.
     *
     * @param mensualidad El objeto Mensualidad a establecer.
     * @param alumno El objeto Alumno a establecer.
     * @param usuario El objeto Usuario a establecer.
     */
    public void setModelos(Mensualidad mensualidad, Alumno alumno, Usuario usuario) {
        newMensualidad = new Mensualidad(mensualidad);
        newAlumno = (Alumno)(alumno.clone());
        newUsuario = new Usuario(usuario);  //Crear una nueva instancia de Usuario basada en el objeto usuario pasado como parámetro.

        ///Configura los enlaces de datos entre los campos de texto y las propiedades de newUsuario.
        tfNombreUsuario.textProperty().bind(Bindings.concat(newUsuario.nombreProperty(), " ", newUsuario.apellido1Property(), " ", newUsuario.apellido2Property()));
        tfEmailUsuario.textProperty().bindBidirectional(usuario.emailAppProperty());
        
        tfNombreAlumno.textProperty().bind(Bindings.concat(newAlumno.nombreProperty(), " ", newAlumno.apellido1Property(), " ", newAlumno.apellido2Property()));
        tfEmailAlumno.textProperty().bindBidirectional(newAlumno.emailProperty());

        if(usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            cbEmailCopia.setValue(Constants.EMAIL_APP); //Establece valor por defecto.
        } else {
            tipoEmailCopia.add(Constants.EMAIL_USER);     //Añadir elemento a ObservableList de cbEmailCopia
            cbEmailCopia.setValue(Constants.EMAIL_USER); //Establece valor por defecto.
        }
        
        cbTipoNotificacion.setValue(newMensualidad.getEstadoPago());
    }
}
