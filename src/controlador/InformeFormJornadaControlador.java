package controlador;

import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import baseDatos.ConexionBD;
import colecciones.ColeccionClases;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.LocalDateStringConverter;
import javafx.util.converter.NumberStringConverter;
import modelo.Jornada;
import modelo.Toast;
import modelo.Usuario;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.swing.JRViewer;
import net.sf.jasperreports.view.JasperViewer;
import utilidades.Constants;
import utilidades.Fechas;

public class InformeFormJornadaControlador implements Initializable {
    
    private ObservableList<String> tipoEmail;
    private DateTimeFormatter formatter;
    private DateTimeFormatter formatterTime;
    private ConexionBD conexionBD;
    private Logger logUser;
    private Toast toast;
    private Jornada jornada;
    private Usuario newUsuario;
    private ToggleGroup grupoModo;
    private ToggleGroup grupoFormato;
    private String nombreInforme;


    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnGenerar;

    @FXML
    private ComboBox<String> cbEmail;

    @FXML
    private CheckBox chekbComentario;

    @FXML
    private CheckBox chekbMetadatos;

    @FXML
    private DatePicker dpJornada;

    @FXML
    private GridPane gpFormInformeJornada;

    @FXML
    private ImageView ivImagenTipoFormulario;

    @FXML
    private Label lbInfoJornada;

    @FXML
    private Label lbTitulo;

    @FXML
    private Label lbNumeroAlumnos;

    @FXML
    private Label lbSemana;

    @FXML
    private Pane pSeparador;

    @FXML
    private RadioButton rbGuardar;

    @FXML
    private RadioButton rbMostrar;

    @FXML
    private RadioButton rbHtml;

    @FXML
    private RadioButton rbPdf;

    @FXML
    private TextArea taTexto;

    @FXML
    private TextField tfEmail;

    @FXML
    private TextField tfNombreInforme;

    @FXML
    private TextField tfNombreUsuario;

    @FXML
    private TextField tfTelefono;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Añadir clases de estilo CSS a elementos.
        btnCancelar.getStyleClass().add("boton_rojo");

        //Cargar imagenes en ImageView.
        Image formulario;
        try {
            //Intentar cargar la imagen desde el recurso en el IDE y en el JAR.
            formulario = new Image(getClass().getResourceAsStream("/recursos/calendar_2_72.png")); //Forma desde IDE y JAR.
        } catch (Exception e) {
            //Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR.
            formulario = new Image("/recursos/calendar_2_72.png"); //Forma desde el JAR.
        }
        //Establecer las imagenes cargadas en los ImageView.
        ivImagenTipoFormulario.setImage(formulario);

        logUser = Logger.getLogger(Constants.USER); //Crea una instancia de la clase Logger asociada al nombre de registro.
        conexionBD = ConexionBD.getInstance();      //Obtener una instancia de la clase ConexionBD utilizando el patrón Singleton.
        toast = new Toast();

        //Modifica el formato en el que se muestra la fecha en el DatePicker.
        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); //Crear un formateador de fecha con el patrón "dd/MM/yyyy".
        dpJornada.setConverter(new LocalDateStringConverter(formatter, null)); //Establecer un convertidor de cadena de fecha para el control DatePicker dpFechaNacimiento.

        //Formateador de fecha y hora sin milisegundos para el nombre del informe
        formatterTime = DateTimeFormatter.ofPattern("dd_MM_yyyy-HH_mm_ss");

        configurarControles();
        

        //Configura un evento para el Datepiker "Jornada"
        dpJornada.setOnAction(event -> {
        	LocalDate fechaSeleccionada = dpJornada.getValue(); //Guardo la fecha seleccionada en fechaSeleccionada.
            lbSemana.setText(Integer.toString(fechaSeleccionada.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR))); //Obtener el número de semana correspondiente a la fecha seleccionada.
            
            //LLamar a base de datos para rescatar la jornada
			try {
				setJornada(conexionBD.getJornadaCompleta(fechaSeleccionada.toString()));
			} catch (SQLException e) {
				logUser.severe("Excepción: " + e.toString());
				e.printStackTrace();
			} catch (Exception e) {
				logUser.severe("Excepción: " + e.toString());
				e.printStackTrace();
			}
        });

        //Configurar un evento de clic del ratón para el botón "Cancelar".
        btnCancelar.setOnMouseClicked(e -> {
            ((Stage) gpFormInformeJornada.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
        });
        
        //Configurar un evento de clic del ratón para el botón "Generar".
        btnGenerar.setOnMouseClicked(e -> {
            if (comprobarCampos()) {
                generarInforme();
            }
            
        });
    }


    /**
     * Devuelve un String con un texto predefinido.
     * 
     * @return String con el texto predefinido.
     */
    private String textoInforme() {
        String texto;
        texto = "Informe de Jornada con los datos de Clases y listado de Alumnos de cada Clase.";
        return texto;
    }


    /**
     * Configura los controles de la interfaz de usuario.
     * Habilita o deshabilita elementos dependiendo de las selecciones y acciones del usuario.
     * Asigna listeners para manejar eventos y actualizaciones en tiempo real.
     * 
     */
    private void configurarControles() {
        lbInfoJornada.setVisible(false);  //Oculta el label informativo de la jornada.
        tfNombreInforme.setDisable(true); //Deshabilita el campo de texto para el nombre del informe.

        //Habilita o deshabilita diferentes controles dependiendo del estado del CheckBox chekbMetadatos.
        tfNombreUsuario.disableProperty().bind(chekbMetadatos.selectedProperty().not());
        tfTelefono.disableProperty().bind(chekbMetadatos.selectedProperty().not());
        cbEmail.disableProperty().bind(chekbMetadatos.selectedProperty().not());
        taTexto.disableProperty().bind(chekbMetadatos.selectedProperty().not());

        chekbComentario.setSelected(true); //Establece el CheckBox chekbComentario como seleccionado por defecto.

        //Inicializa y configura el ComboBox cbEmail con los tipos de correo electrónico disponibles.
        tipoEmail = FXCollections.observableArrayList();
        tipoEmail.addAll(Constants.EMAIL_OTHER);
        cbEmail.setItems(tipoEmail);

        //Establece un listener para detectar cambios en la selección del ComboBox cbEmail.
        cbEmail.getSelectionModel().selectedItemProperty().addListener((o, nv, ov) -> {
            if (ov.equals(Constants.EMAIL_APP)) {
                tfEmail.setDisable(true);
                tfEmail.setText(newUsuario.getEmailApp());
            } else if(ov.equals(Constants.EMAIL_OTHER)) {
                tfEmail.setDisable(false);
                tfEmail.setText("");;
            } else {
                //Este caso es si se ha añadido el email de usuario personal.
                tfEmail.setDisable(true);
                tfEmail.setText(newUsuario.getEmail());
            }
        });

        //Establece un listener para detectar cambios en la selección del CheckBox chekbMetadatos.
        chekbMetadatos.selectedProperty().addListener((o, nv, ov) -> {
            if(!ov.booleanValue() && cbEmail.getValue().equals(Constants.EMAIL_OTHER)) {
                tfEmail.setDisable(true);
            } else if(ov.booleanValue() && cbEmail.getValue().equals(Constants.EMAIL_OTHER)) {
                tfEmail.setDisable(false);
            }
        });


        //Configura un grupo de ToggleButtons para los modos de guardado y visualización de informes.
        grupoModo = new ToggleGroup();

        //Asigna los radioButton al grupo.
        rbGuardar.setToggleGroup(grupoModo);
        rbMostrar.setToggleGroup(grupoModo);

        //Establece un listener para detectar cambios en la selección del grupo de modos.
        grupoModo.selectedToggleProperty().addListener(e -> {
            if(grupoModo.getSelectedToggle().equals(rbGuardar)) {
                tfNombreInforme.setDisable(false);
                rbPdf.setDisable(false);
                rbHtml.setDisable(false);
            } else {
                tfNombreInforme.setDisable(true);
                rbPdf.setDisable(true);
                rbHtml.setDisable(true);
            }
        });

        rbMostrar.setSelected(true); //Selecciona el RadioButton rbMostrar como seleccionado por defecto.

        //Configura un grupo de ToggleButtons para los modos formato de guardado de informes.
        grupoFormato = new ToggleGroup();

        //Asigna los radioButton al grupo.
        rbPdf.setToggleGroup(grupoFormato);
        rbHtml.setToggleGroup(grupoFormato);

        grupoFormato.selectedToggleProperty().addListener(e -> {
            
        });

        rbPdf.setSelected(true); //Selecciona el RadioButton rbPdf como seleccionado por defecto.

        taTexto.setText(textoInforme()); //Establece el texto predefinido en el TextArea taTexto.
        nombreInforme = "informe_" + LocalDateTime.now().format(formatterTime);
        tfNombreInforme.setText(nombreInforme);
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
        Matcher emailMatcher = emailPattern.matcher((tfEmail.getText() == null) ? "" : tfEmail.getText());

        Pattern nombreInformePattern = Pattern.compile("^[^\\\\/:*?\"<>|]+$");
        Matcher nombreInformeMatcher = nombreInformePattern.matcher((tfNombreInforme.getText() == null) ? "" : tfNombreInforme.getText());

        //Expresion para comprobar formato nombre.extension -> "^[^\\\\/:*?\"<>|]+\\.[^\\\\/:*?\"<>|]+$"

        if(cbEmail.getValue().equals(Constants.EMAIL_OTHER) && !emailMatcher.matches()) {
            mensajeAviso(Alert.AlertType.ERROR,"Email No valido.",
            "",
            "El Email introducido en el campo Email no es valido.");
        } else if (tfNombreUsuario.getText().isBlank()) {
            mensajeAviso(Alert.AlertType.ERROR,"Nombre No valido.",
            "",
            "El campo nombre esta vacío.");
        } else if (rbGuardar.isSelected() && !nombreInformeMatcher.matches()) {
            mensajeAviso(Alert.AlertType.ERROR,"Nombre archivo No valido.",
            "El nombre introduciodo no es valido.",
            "Los nombres de los archivos no puede contener niguno\nde los siguientes caracteres:   \\/*:?.<>\"");
        } else if (jornada == null) {
            mensajeAviso(Alert.AlertType.ERROR,"Jornada no valida.",
            "",
            "No has seleccionado una Jornada o la Jornada no es valida.");
        } else {
            camposCorrectos = true;
        }
        
        return camposCorrectos;
    }


    /**
     * Genera el informe basado en los datos proporcionados por el usuario y el objeto jornada.
     * Los parámetros se utilizan para rellenar los campos correspondientes en el informe.
     * El informe puede mostrarse en el visor de JasperReport o guardarse en formato PDF, dependiendo del modo seleccionado.
     * 
     */
    private void generarInforme() {
        //Configurar la colección de clases con la lista de clases de la jornada actual.
        //ColeccionAlumnos.setColeccionAlumnos(clase.getListaAlumnos());
        ColeccionClases.setColeccionClases(Arrays.asList(jornada.getClases()));
        
        JasperReport jasperReport;
    	JasperPrint print;
        InputStream jasperStream;
        jasperStream = getClass().getResourceAsStream("/reports/report_jornada.jasper");
    	
    	//Crea el mapa de parametros con los datos del formulario.
    	HashMap<String, Object> parameters = new HashMap<String, Object>();
    	parameters.put("autor", (tfNombreUsuario.getText()));
    	parameters.put("telefono", (tfTelefono.getText() == null) ? "" : tfTelefono.getText());
    	parameters.put("email", tfEmail.getText());
    	parameters.put("fechaInforme", LocalDate.now().format(formatter));
    	parameters.put("textoInforme",  (taTexto.getText() == null) ? "" : taTexto.getText());
    	parameters.put("jornada", jornada.getFecha().format(formatter));
    	parameters.put("comentario_jornada", (chekbComentario.isSelected()) ? jornada.getComentario() : "");
    	parameters.put("dia_semana", Fechas.obtenerDiaSemana(jornada.getFecha()));
        parameters.put("semana", lbSemana.getText());
        parameters.put("total_alumnos", lbNumeroAlumnos.getText());
        
    	try {
			jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
			print = JasperFillManager.fillReport( jasperReport, parameters, new JRBeanCollectionDataSource(ColeccionClases.getColeccionClases()));
			
            if(grupoModo.getSelectedToggle().equals(rbMostrar)) {
                //Abrir el visor de Jasperreport
                JRViewer viewer = new JRViewer(print);
	            viewer.setOpaque(true);
	            viewer.setVisible(true);
	            JasperViewer.viewReport(print, false);

            } else {
                //Guardar archivo
                String rutaArchivo;

                if (grupoFormato.getSelectedToggle().equals(rbPdf)) {
                    //Guardar el informe en formato PDF en el archivo especificado.
                    rutaArchivo = newUsuario.getDirectorio().getName() + "\\" + Constants.FOLDER_REPORTS  + "\\" + tfNombreInforme.getText() + ".pdf";
	                JasperExportManager.exportReportToPdfFile(print, rutaArchivo);
                } else {
                    //Guardar el informe en formato HTML en el archivo especificado.
                    rutaArchivo = newUsuario.getDirectorio().getName() + "\\" + Constants.FOLDER_REPORTS  + "\\" + tfNombreInforme.getText() + ".html";
                    JasperExportManager.exportReportToHtmlFile(print, rutaArchivo);
                }

                //Mostrar una notificación de éxito en la interfaz gráfica.
                toast.show((Stage) ((Stage) gpFormInformeJornada.getScene().getWindow()).getOwner(),"Informe Generado!.");

                //Cerrar la ventana actual después de enviar el correo.
                ((Stage) gpFormInformeJornada.getScene().getWindow()).close(); // Obtener la referencia al Stage actual y cerrarlo.
            }
			
		} catch (JRException e) {
            toast.show((Stage) gpFormInformeJornada.getScene().getWindow(),"Fallo al generar informe.\n" + e.toString());

            //Registrar la excepción en el log de errores.
            logUser.severe("Excepción al generar informe: " + e.toString());
			e.printStackTrace();
		} catch (Exception e) {
            toast.show((Stage) gpFormInformeJornada.getScene().getWindow(),"Fallo al generar informe.\n" + e.toString());

            //Registrar la excepción en el log de errores.
            logUser.severe("Excepción al grnerar informe: " + e.toString());
            e.printStackTrace();
        }
    }


    /**
    * Establece la Jornada actual, actualizando la interfaz de usuario en consecuencia.
    * Si la jornada pasada como parámetro es null, muestra un mensaje de error y reinicia la interfaz.
    * Si la jornada no es null, realiza el proceso de enlace de datos con los elementos de la interfaz.
    *
    * @param j La Jornada a establecer como jornada actual.
    */
    private void setJornada(Jornada j) {
        if (j == null) {
            toast.show((Stage) gpFormInformeJornada.getScene().getWindow(), "Esta jornada no esta creada");
            lbInfoJornada.setVisible(true);
            if (jornada != null) {       
                lbSemana.setText("");
                lbNumeroAlumnos.setText("");

                //Establecer la jornada cargada a null.
				jornada = null;
            }

        } else {
            //Si la jornada pasada como parametro no es null, realizar el proceso de binding
            jornada = j;
            lbInfoJornada.setVisible(false);
            lbNumeroAlumnos.setText("" + jornada.numeroAlumnosEnJornada());
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
        alerta.initOwner((Stage) gpFormInformeJornada.getScene().getWindow());
        alerta.setHeaderText(cabecera);
        alerta.setContentText(cuerpo);
        alerta.initStyle(StageStyle.DECORATED);
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.showAndWait();
    }



    /**
     * Establece el objeto Usuario para el formulario actual.
     * 
     * @param usuario El objeto Usuario a configurar en el formulario.
     */
    public void setUsuario(Usuario usuario) {
        this.newUsuario = new Usuario(usuario); //Se crea una nueva instancia de Usuario para evitar modificar el original.

        //Configura los enlaces de datos entre los campos de texto y las propiedades de newUsuario.
        tfNombreUsuario.textProperty().bind(Bindings.concat(newUsuario.nombreProperty(), " ", newUsuario.apellido1Property(), " ", newUsuario.apellido2Property()));
        tfTelefono.textProperty().bindBidirectional(newUsuario.telefonoProperty(), new NumberStringConverter("0"));

        //Si el objeto newUsuario tiene una dirección de correo electrónico de la aplicación no vacía,
        //se agrega la constante EMAIL_APP a la lista de tipos de correo electrónico y se establece como valor predeterminado en el ComboBox cbEmail.
        if (newUsuario.getEmailApp() != null && !newUsuario.getEmailApp().isBlank()) {
            tipoEmail.add(Constants.EMAIL_APP); //Añadir elemento a ObservableList de cbEmail
            cbEmail.setValue(Constants.EMAIL_APP);
        }

        //Si el objeto newUsuario tiene una dirección de correo electrónico no vacía,
        //se agrega la constante EMAIL_USER a la lista de tipos de correo electrónico.
        if (newUsuario.getEmail() != null || !newUsuario.getEmail().isBlank()) {
             tipoEmail.add(Constants.EMAIL_USER); //Añadir elemento a ObservableList de cbEmail
        } 
    }
}
