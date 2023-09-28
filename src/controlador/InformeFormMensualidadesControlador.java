package controlador;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import colecciones.ColeccionMensualidades;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.NumberStringConverter;
import modelo.Alumno;
import modelo.EstadoPago;
import modelo.FormaPago;
import modelo.Mensualidad;
import modelo.MensualidadReport;
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
import utilidades.Toast;


public class InformeFormMensualidadesControlador implements Initializable {

    private ObservableList<Alumno> listadoAlumnosGeneral;
    private FilteredList<Mensualidad> filtro;
    private ObservableList<String> tipoEmail;
    private DateTimeFormatter formatter;
    private DateTimeFormatter formatterTime;
    private Logger logUser;
    private Toast toast;
    private Usuario newUsuario;
    private ToggleGroup grupoModo;
    private ToggleGroup grupoFormato;
    private String nombreInforme;


    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnGenerar;

    @FXML
    private ComboBox<Integer> cbAnio;

    @FXML
    private ComboBox<String> cbEmail;

    @FXML
    private ComboBox<String> cbEstadoPago;

    @FXML
    private ComboBox<String> cbMes;

    @FXML
    private ComboBox<String> cbFormaPago;

    @FXML
    private CheckBox chekbMetadatos;

    @FXML
    private GridPane gpFormInformeMensualidad;

    @FXML
    private ImageView ivImagenTipoFormulario;

    @FXML
    private Label lbNumeroMensualidades;

    @FXML
    private Label lbTitulo;

    @FXML
    private Pane pSeparador;

    @FXML
    private RadioButton rbGuardar;

    @FXML
    private RadioButton rbHtml;

    @FXML
    private RadioButton rbMostrar;

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
        toast = new Toast();

        //Modifica el formato en el que se muestra la fecha en el DatePicker.
        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); //Crear un formateador de fecha con el patrón "dd/MM/yyyy".
        
        //Formateador de fecha y hora sin milisegundos para el nombre del informe
        formatterTime = DateTimeFormatter.ofPattern("dd_MM_yyyy-HH_mm_ss");

        //Configura los controles, establece sus listener y establece los valores por defecto.
        configurarControles();

        //Configurar un evento de clic del ratón para el botón "Cancelar".
        btnCancelar.setOnMouseClicked(e -> {
            ((Stage) gpFormInformeMensualidad.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
        });
        
        //Configurar un evento de clic del ratón para el botón "Generar".
        btnGenerar.setOnMouseClicked(e -> {
            if (comprobarCampos() && generarColeccionMensualidades()) {
                generarInforme();
            } 
        });
    }


    /**
     * Configura los controles de la interfaz de usuario.
     * Habilita o deshabilita elementos dependiendo de las selecciones y acciones del usuario.
     * Asigna listeners para manejar eventos y actualizaciones en tiempo real.
     * 
     */
    private void configurarControles() {
        tfNombreInforme.setDisable(true); //Deshabilita el campo de texto para el nombre del informe.
        tfNombreUsuario.setDisable(true); //Deshabilita el campo de texto para el nombre de autor de informe.
        tfEmail.setDisable(true);

        //Habilita o deshabilita diferentes controles dependiendo del estado del CheckBox chekbMetadatos.
        tfTelefono.disableProperty().bind(chekbMetadatos.selectedProperty().not());
        cbEmail.disableProperty().bind(chekbMetadatos.selectedProperty().not());
        taTexto.disableProperty().bind(chekbMetadatos.selectedProperty().not());
        tfNombreUsuario.disableProperty().bind(chekbMetadatos.selectedProperty().not());

        //Inicializa y configura el ComboBox cbEmail con los tipos de correo electrónico disponibles.
        tipoEmail = FXCollections.observableArrayList();
        tipoEmail.addAll(Constants.EMAIL_OTHER);
        cbEmail.setItems(tipoEmail);
        cbEmail.setValue(Constants.EMAIL_OTHER);

        //Establece un listener para detectar cambios en la selección del ComboBox cbEmail.
        cbEmail.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            if (nv.equals(Constants.EMAIL_APP)) {
                tfEmail.setDisable(true);
                tfEmail.setText(newUsuario.getEmailApp());
            } else if(nv.equals(Constants.EMAIL_OTHER)) {
                tfEmail.setDisable(false);
                tfEmail.setText("");;
            } else {
                //Este caso es si se ha añadido el email de usuario personal.
                tfEmail.setDisable(true);
                tfEmail.setText(newUsuario.getEmail());
            }
        });

        //Establece un listener para detectar cambios en la selección del CheckBox chekbMetadatos.
        chekbMetadatos.selectedProperty().addListener((o, ov, nv) -> {
            if(!nv.booleanValue() && cbEmail.getValue().equals(Constants.EMAIL_OTHER)) {
                tfEmail.setDisable(true);
            } else if(nv.booleanValue() && cbEmail.getValue().equals(Constants.EMAIL_OTHER)) {
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

        rbPdf.setSelected(true); //Selecciona el RadioButton rbPdf como seleccionado por defecto.

        taTexto.setText(textoInforme()); //Establece el texto predefinido en el TextArea taTexto.
        nombreInforme = "informe_mensualidades_" + LocalDateTime.now().format(formatterTime); //Nombre predefinido para el informe. "informe_mensualidades_dd_MM_yyyy-HH_mm_ss".
        tfNombreInforme.setText(nombreInforme); //Establece el texto predefinido para el nombre del informe en el TextField tfNombreInforme.

        //Crea un ArrayList de Integer con valores de 2020 hasta 2050 y cargo el ArrayList en el ComboBox cbAnio.
        int yearInicial = 2020;
        ArrayList<Integer> listaYears = new ArrayList<Integer>();
        for (int i = 0; i <= 30; i++) {
            listaYears.add(yearInicial + i);
        }
        //Si el año actual no esta en la lista, lo agrega.
        if(!listaYears.contains(LocalDate.now().getYear())) {
            listaYears.add(LocalDate.now().getYear());
        }
        cbAnio.setItems(FXCollections.observableArrayList(listaYears));

        //Crea un ObservableList<String> con el nombre de los meses del año. 
        ObservableList<String> listMeses = FXCollections.observableArrayList(Fechas.obtenerMesesDelAnio().values());
        listMeses.add("TODOS");  //Añade a listMeses el valor TODOS.
        cbMes.setItems(listMeses); //Carga listMeses en el ComboBox cbMes.

        //Conviete los valores de EstadoPago a String y los añadao a listaEstados.
        ArrayList<String> listaEstadosPago = new ArrayList<String>();
        for (EstadoPago e : EstadoPago.values()) {
            listaEstadosPago.add(e.toString());
        }
        listaEstadosPago.add("TODOS"); //Añade a listaEstados el valor TODOS.
        cbEstadoPago.setItems(FXCollections.observableArrayList(listaEstadosPago)); //Carga listaEstados en el ComboBox cbEstadoPago.

        //Convierte los calores de FormaPago a String y los añade a listaFormasPago.
        ArrayList<String> listaFormasPago = new ArrayList<String>();
        for (FormaPago fPago : FormaPago.values()) {
            listaFormasPago.add(fPago.toString());
        }
        listaFormasPago.add("TODAS"); //Añade a la listaFormasPago el valor TODAS.
        cbFormaPago.setItems(FXCollections.observableArrayList(listaFormasPago)); //Carga listaFormasPago en el ComboBox cbFormaPago.

        //Valor inicial del ComboBox cbAnio.
        try{
            cbAnio.setValue(LocalDate.now().getYear()); //Establece el año actual marcado por defecto.
        }catch (IllegalArgumentException e) {
            cbAnio.setValue(2020); //Establece este año si el año actual no se encuentra entre los valores del ComboBox cbAnio.
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        }

        cbMes.setValue(Fechas.obtenerNombreMes(LocalDate.now().getMonthValue())); //Valor inicial del ComboBox cbMes.
        cbEstadoPago.setValue("TODOS"); //Valor inicial del ComboBox cbEstadoPago.
        cbFormaPago.setValue("TODAS"); //Valor inicial del ComboBox cbFormaPago.

        //Configurar Listener para el ComboBox cbMes.
        cbMes.setOnAction(e -> {
            configurarFiltro("");
        });

        //Configurar Listener para el ComboBox cbEstadoPago.
        cbEstadoPago.setOnAction(e -> {
            configurarFiltro("");
        });

        //Configurar Listener para el ComboBox cbFormaPago.
        cbFormaPago.setOnAction(e -> {
            configurarFiltro("");
        });

        //Configurar Listener para el ComboBox cbAnio.
        cbAnio.setOnAction(e -> {
            configurarFiltro("");
        });
    }


    /**
     * Devuelve un String con un texto predefinido.
     * 
     * @return String con el texto predefinido.
     */
    private String textoInforme() {
        String texto;
        texto = "Informe de Mensualidades de Alumnos.";
        return texto;
    }


    /**
     * Configura el filtro según los criterios seleccionados.
     *
     * @param texto El texto de búsqueda utilizado para filtrar.
     */
    private void configurarFiltro(String texto) {
        filtro.setPredicate(obj -> {
            if (obj.fechaProperty().getValue().getYear() != cbAnio.getValue()) {
                return false;
            } else if (!(cbMes.getValue().equals("TODOS")) && !(Fechas.obtenerNombreMes(obj.fechaProperty().getValue().getMonthValue()).equals(cbMes.getValue()))) {
                return false;
            } else if (!(cbEstadoPago.getValue().equals("TODOS")) && !(obj.estadoPagoProperty().getValue().toString().equals(cbEstadoPago.getValue()))) {
                return false;
            } else if (!(cbFormaPago.getValue().equals("TODAS")) && !(obj.formaPagoProperty().getValue().toString().equals(cbFormaPago.getValue()))) {
                return false;
            } else {
                return true;
            }
        });
    }


    /**
     * Genera una colección de objetos de tipo MensualidadReport basada en una lista de Mensualidad y un listado general de Alumnos.
     * La colección resultante se ordena por fecha de Mensualidad de forma ascendente.
     * La colección se establece en la clase ColeccionMensualidades mediante el método estático setColeccionMensualidades.
     * 
     * @return true si la generación de la colección fue exitosa, false en caso de error.
     */
    private boolean generarColeccionMensualidades() {
        try {
            ArrayList<Mensualidad> listaMensualidadReport = new ArrayList<Mensualidad>();
        
            //Iterar sobre cada Mensualidad en el filtro.
            for (Mensualidad mensualidad : filtro) {
                MensualidadReport mReport = new MensualidadReport(mensualidad); //Crear un objeto MensualidadReport a partir de la Mensualidad actual.
            
                //Buscar el Alumno correspondiente en el listado general de Alumnos y establecer su nombre en el MensualidadReport.
                for (Alumno alumno : listadoAlumnosGeneral) {
                    if (mensualidad.getIdAlumno() == alumno.getId()) {
                        mReport.setNombreAlumno(alumno.getNombreCompleto());
                        break;
                    }
                }

            	listaMensualidadReport.add(mReport); //Agregar el MensualidadReport a la lista listaMensualidadReport.
            }
            
            //Crear el comparador para ordenar por fecha ascendente
            Comparator<Mensualidad> comparadorFechaAscendente = Comparator.comparing(Mensualidad::getFecha);

            //Ordenar la lista listaMensualidadReport por fecha ascendente
            Collections.sort(listaMensualidadReport, comparadorFechaAscendente);
            
            //Establece la lista de mensualidades a ColeccionMensualidades.
            ColeccionMensualidades.setColeccionMensualidades(listaMensualidadReport);
            
        } catch (Exception e) {
            //En caso de excepción, mostrar un mensaje de error y registrar la excepción en el log del usuario.
            toast.show((Stage) gpFormInformeMensualidad.getScene().getWindow(),"Fallo al generar informe.\n" + e.toString());
            logUser.severe("Excepción al generar informe: " + e.toString());
			e.printStackTrace();
            return false;
        }

        return true;
    }


    /**
     * Genera el informe utilizando JasperReports según la selección del usuario en la interfaz gráfica.
     * El informe puede mostrarse en el visor de JasperReport o guardarse en formato PDF o HTML, dependiendo del modo seleccionado.
     * 
     */
    private void generarInforme() { 
        JasperReport jasperReport;
    	JasperPrint print;
        InputStream jasperStream;
        jasperStream = getClass().getResourceAsStream("/reports/report_mensualidad.jasper");
        
        //Configurar los parámetros necesarios para el informe.
        HashMap<String, Object> parameters = configuracionParametrosInforme();
        
    	try {
			jasperReport = (JasperReport) JRLoader.loadObject(jasperStream); //Cargar el archivo de reporte como un objeto JasperReport.

            //Rellenar el informe utilizando el JasperReport, los parámetros y la colección de Mensualidades.
			print = JasperFillManager.fillReport( jasperReport, parameters, new JRBeanCollectionDataSource(ColeccionMensualidades.getColeccionMensualidades()));
			
            //Verificar el modo de generación del informe.
            if(grupoModo.getSelectedToggle().equals(rbMostrar)) {
                //Modo "Mostrar": Abrir el visor de Jasperreport
                JRViewer viewer = new JRViewer(print);
	            viewer.setOpaque(true);
	            viewer.setVisible(true);
	            JasperViewer.viewReport(print, false);

            } else {
                //Modo "Guardar archivo": Obtener la ruta de archivo y exportar el informe en el formato seleccionado.
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
                toast.show((Stage) ((Stage) gpFormInformeMensualidad.getScene().getWindow()).getOwner(),"Informe Generado!.");

                //Cerrar la ventana actual después de enviar el correo.
                ((Stage) gpFormInformeMensualidad.getScene().getWindow()).close(); // Obtener la referencia al Stage actual y cerrarlo.
            }
			
		} catch (JRException e) {
            //En caso de excepción JRException, mostrar un mensaje de error y registrar la excepción en el log de errores.
            toast.show((Stage) gpFormInformeMensualidad.getScene().getWindow(),"Fallo al generar informe.\n" + e.toString());
            logUser.severe("Excepción al generar informe: " + e.toString());
			e.printStackTrace();

		} catch (Exception e) {
            //En caso de excepción general, mostrar un mensaje de error y registrar la excepción en el log de errores.
            toast.show((Stage) gpFormInformeMensualidad.getScene().getWindow(),"Fallo al generar informe.\n" + e.toString());
            logUser.severe("Excepción al grnerar informe: " + e.toString());
            e.printStackTrace();
        }
    }
    

    /**
     * Configura y devuelve un HashMap que contiene los parámetros necesarios para generar el informe.
     *
     * @return HashMap que contiene los parámetros del informe.
     */
    private HashMap<String, Object> configuracionParametrosInforme() {
    	HashMap<String, Object> parameters = new HashMap<String, Object>();
    	Double importe_total = 0.0;
    	Double importe_pagadas = 0.0;
    	Double importe_pendientes = 0.0;
    	Double importe_resto = 0.0;
    	Integer cont_pagadas = 0;
    	Integer cont_pendientes = 0;
    	Integer cont_resto = 0;
    	
        //Calcular los totales de importes y conteo de mensualidades en diferentes estados.
        for (Mensualidad mensualidad : filtro) {
            double importe = mensualidad.getImporte();
            switch (mensualidad.getEstadoPago()) {
                case PAGADO ->{
                    importe_pagadas += importe;
                    cont_pagadas++;
                }
                    
                case PENDIENTE -> {
                    importe_pendientes += importe;
                    cont_pendientes++;
                }
                    
                default -> {
                    importe_resto += importe;
                    cont_resto++;
                }
            }
        }
    	
        //Calcular el importe total sumando los importes pagados, pendientes y resto.
    	importe_total = importe_pagadas + importe_pendientes + importe_resto;
    	
        //Establecer los parámetros en el HashMap.
    	parameters.put("autor", (tfNombreUsuario.getText().isBlank()) ? " " : tfNombreUsuario.getText());
    	parameters.put("telefono", (tfTelefono.getText().isBlank()) ? " " : Integer.toString(newUsuario.getTelefono()));
    	parameters.put("email", (tfEmail.getText().isBlank()) ? " " : tfEmail.getText());
    	parameters.put("fecha_informe", LocalDate.now().format(formatter));
    	parameters.put("texto_informe",  (taTexto.getText() == null) ? "" : taTexto.getText());
        parameters.put("total_mensualidades", lbNumeroMensualidades.getText());
        parameters.put("filtro_anio", cbAnio.getValue().toString());
        parameters.put("filtro_mes", cbMes.getValue().toString());
        parameters.put("filtro_estado", cbEstadoPago.getValue().toString());
        parameters.put("filtro_forma", cbFormaPago.getValue().toString());
        parameters.put("importe_total", importe_total + " €");
        parameters.put("importe_pagadas", importe_pagadas + " €");
        parameters.put("importe_pendientes", importe_pendientes + " €");
        parameters.put("importe_resto", importe_resto + " €");
        parameters.put("cont_pagadas", cont_pagadas);
        parameters.put("cont_pendientes", cont_pendientes);
        parameters.put("cont_resto", cont_resto);
        
    	return parameters; //Devolver el HashMap con los parámetros configurados.
    }


    /**
     * Método que comprueba si los campos del formulario son válidos.
     *
     * @return true si los campos son válidos, de lo contrario, false.
     */
    private boolean comprobarCampos() {
        boolean camposCorrectos = false;

        //Patrón para validar el formato del email.
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$|^$");
        Matcher emailMatcher = emailPattern.matcher((tfEmail.getText() == null) ? "" : tfEmail.getText());

        Pattern nombreInformePattern = Pattern.compile("^[^\\\\/:*?\"<>|]+$");
        Matcher nombreInformeMatcher = nombreInformePattern.matcher((tfNombreInforme.getText() == null) ? "" : tfNombreInforme.getText());

        //Expresion para comprobar formato nombre.extension -> "^[^\\\\/:*?\"<>|]+\\.[^\\\\/:*?\"<>|]+$"

        if(cbEmail.getValue() != null && cbEmail.getValue().equals(Constants.EMAIL_OTHER) && !emailMatcher.matches()) {
            mensajeAviso(Alert.AlertType.ERROR,"Email No valido.",
            "",
            "El Email introducido en el campo Email no es valido.");
        } else if (rbGuardar.isSelected() && !nombreInformeMatcher.matches()) {
            mensajeAviso(Alert.AlertType.ERROR,"Nombre archivo No valido.",
            "El nombre introduciodo no es valido.",
            "Los nombres de los archivos no puede contener niguno\nde los siguientes caracteres:   \\/*:?.<>\"");
        } else {
            camposCorrectos = true;
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
        alerta.initOwner((Stage) gpFormInformeMensualidad.getScene().getWindow());
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
        if(!newUsuario.getNombre().isBlank()) {
            tfNombreUsuario.textProperty().bind(Bindings.concat(newUsuario.nombreProperty(), " ", newUsuario.apellido1Property(), " ", newUsuario.apellido2Property()));
            tfNombreUsuario.disableProperty().unbind();
            tfNombreUsuario.setDisable(true);  
        } 
        
        tfTelefono.textProperty().bindBidirectional(newUsuario.telefonoProperty(), new NumberStringConverter("0"));

        //Filtra la entrada de texto para permitir solo números y 9 digitos como máximo.
        tfTelefono.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!event.getCharacter().matches("[0-9]") || Integer.toString(newUsuario.getTelefono()).length() > 8) {
                event.consume();
            }
        });

        //Si el objeto newUsuario tiene una dirección de correo electrónico de la aplicación no vacía,
        //se agrega la constante EMAIL_APP a la lista de tipos de correo electrónico y se establece como valor predeterminado en el ComboBox cbEmail.
        if (newUsuario.getEmailApp() != null && !newUsuario.getEmailApp().isBlank()) {
            tipoEmail.add(Constants.EMAIL_APP); //Añadir elemento a ObservableList de cbEmail
            cbEmail.setValue(Constants.EMAIL_APP);
        }

        //Si el objeto newUsuario tiene una dirección de correo electrónico no vacía,
        //se agrega la constante EMAIL_USER a la lista de tipos de correo electrónico.
        if (newUsuario.getEmail() != null && !newUsuario.getEmail().isBlank()) {
             tipoEmail.add(Constants.EMAIL_USER); //Añadir elemento a ObservableList de cbEmail
        } 
    }


    /**
     * Establece la lista de mensualidades.
     * Configura el filtro, los eventos de selección y los datos del filtro.
     * 
     * @param lista La lista de mensualidades a mostrar.
     */
    public void setListaMensualidades(ObservableList<Mensualidad> lista) {
        //listadoMensualidadesGeneral = lista; //Guarda la lista pasada a la lista de Clasecontrolador.
		filtro = new FilteredList<Mensualidad>(lista); //Inicia el filtro pasándole el listado de mensualidades.
		
        configurarFiltro(""); //Configura el filtro.
        setupDatosFiltro(); // Configura los bindings para actualizar los labels de información del filtro.
	}

    
    /**
     * Configura los Labels que muestran los datos del filtro y las propiedades enlazadas.
     * Actualiza los labels con la información del filtro.
     */
    private void setupDatosFiltro() {
        IntegerBinding totalMensualidades = Bindings.createIntegerBinding(
                () -> (int) filtro.stream().count(), filtro);
        
        lbNumeroMensualidades.textProperty()
                .bind(Bindings.createStringBinding(() -> String.format("%d", totalMensualidades.get()), totalMensualidades));
    }


    /**
     * Establece la lista de alumnos en el controlador.
     *
     * @param lista La lista de alumnos a establecer.
     */
    public void setListaAlumnos(ObservableList<Alumno> lista) {
        listadoAlumnosGeneral = lista;
	}
    
}
