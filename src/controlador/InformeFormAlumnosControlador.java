package controlador;

import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import baseDatos.ConexionBD;
import colecciones.ColeccionAlumnos;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import modelo.EstadoAlumno;
import modelo.Genero;
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
import utilidades.CadenaUtil;
import utilidades.Constants;
import utilidades.Toast;


public class InformeFormAlumnosControlador implements Initializable {

    private final String ORDEN_ID = "ID";
    private final String ORDEN_NOMBRE = "NOMBRE";
    private final String ORDEN_LOCALIDAD = "LOCALIDAD";
    private final String ORDEN_ESTADO = "ESTADO";
    private final String ORDEN_GENERO = "GENERO";

    private FilteredList<Alumno> filtro;
    private ObservableList<String> tipoEmail;
    private DateTimeFormatter formatter;
    private DateTimeFormatter formatterTime;
    private DecimalFormat decimalFormat;
    private ConexionBD conexionBD;
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
    private ComboBox<String> cbEstado;

    @FXML
    private ComboBox<String> cbGenero;

    @FXML
    private ComboBox<String> cbLocalidad;

    @FXML
    private ComboBox<String> cbEmail;

    @FXML
    private ComboBox<String> cbOrdenar;

    @FXML
    private CheckBox chekbMetadatos;

    @FXML
    private GridPane gpFormInformeAlumno;

    @FXML
    private ImageView ivImagenTipoFormulario;

    @FXML
    private Label lbNumeroAlumnos;

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
            formulario = new Image(getClass().getResourceAsStream("/recursos/id_1_72.png")); //Forma desde IDE y JAR.
        } catch (Exception e) {
            //Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR.
            formulario = new Image("/recursos/id_1_72.png"); //Forma desde el JAR.
        }
        //Establecer las imagenes cargadas en los ImageView.
        ivImagenTipoFormulario.setImage(formulario);

        conexionBD = ConexionBD.getInstance();      //Obtener una instancia de la clase ConexionBD utilizando el patrón Singleton.
        logUser = Logger.getLogger(Constants.USER); //Crea una instancia de la clase Logger asociada al nombre de registro.
        toast = new Toast();

        //Modifica el formato en el que se muestra la fecha en el DatePicker.
        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); //Crear un formateador de fecha con el patrón "dd/MM/yyyy".
        
        //Formateador de fecha y hora sin milisegundos para el nombre del informe
        formatterTime = DateTimeFormatter.ofPattern("dd_MM_yyyy-HH_mm_ss");
        
        //formateador de numero decimal para mostrar un digito en los decimales.
        decimalFormat = new DecimalFormat("#0.0");

        //Configura los controles, establece sus listener y establece los valores por defecto.
        configurarControles();

        //Configurar un evento de clic del ratón para el botón "Cancelar".
        btnCancelar.setOnMouseClicked(e -> {
            ((Stage) gpFormInformeAlumno.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
        });
        
        //Configurar un evento de clic del ratón para el botón "Generar".
        btnGenerar.setOnMouseClicked(e -> {
            if (comprobarCampos() && generarColeccionAlumnos()) {
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
        nombreInforme = "informe_alumnos_" + LocalDateTime.now().format(formatterTime); //Nombre predefinido para el informe. "informe_alumnos_dd_MM_yyyy-HH_mm_ss".
        tfNombreInforme.setText(nombreInforme); //Establece el texto predefinido para el nombre del informe en el TextField tfNombreInforme.

        //Configura el ComboBox cbEstado.
        ObservableList<String> listadoEstado = FXCollections.observableArrayList();
        listadoEstado.setAll(EstadoAlumno.ACTIVO.toString(), EstadoAlumno.BAJA.toString(), "TODOS");
        cbEstado.setItems(listadoEstado);
        cbEstado.setValue("TODOS"); //Valor inicial.

        //Configurar Listener para el ComboBox cbGenero.
        cbEstado.setOnAction(e -> {
            configurarFiltro("");
        });

        //Configura el ComboBox cbGenero.
        ObservableList<String> listadoGenero = FXCollections.observableArrayList();
        listadoGenero.setAll(Genero.HOMBRE.toString(), Genero.MUJER.toString(), "AMBOS");
        cbGenero.setItems(listadoGenero);
        cbGenero.setValue("AMBOS"); //Valor inicial.

        //Configurar Listener para el ComboBox cbGenero.
        cbGenero.setOnAction(e -> {
            configurarFiltro("");
        });

        //Configura el ComboBox cbLocalidad.
        ObservableList<String> listadoLocalidades = null;
        try {
			listadoLocalidades = FXCollections.observableArrayList(conexionBD.getLocalidades());
            listadoLocalidades.add("TODAS");

            //Carga en el ComboBox los items del listadoLocalidades.
            cbLocalidad.setItems(listadoLocalidades);
            cbLocalidad.setValue("TODAS"); //Valor inicial.

            cbLocalidad.setOnAction(e -> {
                configurarFiltro("");
            });
		} catch (Exception e) {
            logUser.severe("Excepción: " + e.toString());
			e.printStackTrace();
		}

        //Configura el ComboBox cbOrdenar.
        ObservableList<String> listadoOrden = FXCollections.observableArrayList();
        listadoOrden.setAll(ORDEN_ID, ORDEN_NOMBRE, ORDEN_LOCALIDAD, ORDEN_ESTADO, ORDEN_GENERO);
        cbOrdenar.setItems(listadoOrden);
        cbOrdenar.setValue("ID"); //Valor inicial.

        //Configurar Listener para el ComboBox cbOrdenar.
        cbOrdenar.setOnAction(e -> {
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
        texto = "Informe Alumnos inscritos en el centro mostrando algunos de sus datos.";
        return texto;
    }


    /**
     * Configura el filtro según los criterios seleccionados.
     *
     * @param texto El texto de búsqueda utilizado para filtrar.
     */
    private void configurarFiltro(String texto) {

        filtro.setPredicate(obj -> {
            if (!(cbEstado.getValue().equals("TODOS")) && !(obj.estadoProperty().getValue().toString().equals(cbEstado.getValue()))) {
                return false;
            }

            if (!(cbGenero.getValue().equals("AMBOS")) && !(obj.generoProperty().getValue().toString().equals(cbGenero.getValue()))) {
                return false;
            }

            if (!(cbLocalidad.getValue().equals("TODAS")) && !(obj.getDireccion().localidadProperty().getValue().toString().equals(cbLocalidad.getValue()))) {
                return false;
            }

            return true;
        });
    }


     /**
     * Genera y ordena la colección de alumnos (filtro) utilizando un comparador basado en el criterio de ordenamiento seleccionado.
     * Luego establece la colección ordenada como la lista de alumnos en la clase ColeccionAlumnos.
     *
     * @return true si se generó y ordenó correctamente la colección de alumnos, false en caso de error.
     */
    private boolean generarColeccionAlumnos() {
        try {
            //Crea el comparador para ordenar la lista de alumnos por el criterio seleccionado.
            Comparator<Alumno> comparador = null;

            switch (cbOrdenar.getValue()) {
                case ORDEN_ID -> {
                    comparador = Comparator.comparingInt(Alumno::getId);
                }

                case ORDEN_NOMBRE -> {
                    comparador = Comparator.comparing(Alumno::getNombre).thenComparing(Alumno::getApellido1).thenComparing(Alumno::getApellido2);
                }

                case ORDEN_LOCALIDAD -> {
                    comparador = Comparator.comparing((Alumno alumno) -> alumno.getDireccion().getLocalidad()).thenComparing(Alumno::getNombre).thenComparing(Alumno::getApellido1).thenComparing(Alumno::getApellido2);
                }

                case ORDEN_ESTADO -> {
                    comparador = Comparator.comparing(Alumno::getEstado).thenComparing(Alumno::getNombre).thenComparing(Alumno::getApellido1).thenComparing(Alumno::getApellido2);
                }

                case ORDEN_GENERO -> {
                    comparador = Comparator.comparing(Alumno::getGenero).thenComparing(Alumno::getNombre).thenComparing(Alumno::getApellido1).thenComparing(Alumno::getApellido2);
                }

                default -> {
                    comparador = Comparator.comparingInt(Alumno::getId);
                }
            }
            
            /*
            //Crea una nueva lista para guardar los alumnos como objetos AlumnoReport
            ArrayList<Alumno> listaAlumnosReport = new ArrayList<Alumno>();
            
            //Recorre la lista filtro y crea AlumnoReport con los datos de alumnos de la lista filtro. 
            for(Alumno a : filtro) {
            	AlumnoReport alumnoR = new AlumnoReport(a); //Crea un AlumnoReport con los datos del alumno que se esta recorriendo.
            	listaAlumnosReport.add(alumnoR); //Guarda el alumnoReport en listaAlumnosReport.
            }
            
            Collections.sort(listaAlumnosReport, comparador); //Ordena la lista utilizando el comparador.
            ColeccionAlumnos.setColeccionAlumnos(listaAlumnosReport); //Establece la lista ordenada de alumnos en la clase ColeccionAlumnos.
            */
            
             
            //codigo para ordenar la lista filtro. --------------------------------------------------------------------------------
            SortedList<Alumno> sortedList = new SortedList<Alumno>(filtro); 
            sortedList.setComparator(comparador); //Ordena la lista sortedList (colección de alumnos) utilizando el comparador.
            ColeccionAlumnos.setColeccionAlumnos(sortedList); //Establece la lista ordenada de alumnos en la clase ColeccionAlumnos.
            //---------------------------------------------------------------------------------------------------------------------
            
            
        } catch (Exception e) {
            //En caso de excepción, mostrar un mensaje de error y registrar la excepción en el log del usuario.
            toast.show((Stage) gpFormInformeAlumno.getScene().getWindow(),"Fallo al generar informe.\n" + e.toString());
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
        jasperStream = getClass().getResourceAsStream("/reports/report_alumno.jasper");
        
        //Configurar los parámetros necesarios para el informe.
        HashMap<String, Object> parameters = configuracionParametrosInforme();
        
    	try {
			jasperReport = (JasperReport) JRLoader.loadObject(jasperStream); //Cargar el archivo de reporte como un objeto JasperReport.

            //Rellenar el informe utilizando el JasperReport, los parámetros y la colección de Alumnos.
			print = JasperFillManager.fillReport( jasperReport, parameters, new JRBeanCollectionDataSource(ColeccionAlumnos.getColeccionAlumnos()));
			
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
                toast.show((Stage) ((Stage) gpFormInformeAlumno.getScene().getWindow()).getOwner(),"Informe Generado!.");

                //Cerrar la ventana actual después de enviar el correo.
                ((Stage) gpFormInformeAlumno.getScene().getWindow()).close(); // Obtener la referencia al Stage actual y cerrarlo.
            }
			
		} catch (JRException e) {
            //En caso de excepción JRException, mostrar un mensaje de error y registrar la excepción en el log de errores.
            toast.show((Stage) gpFormInformeAlumno.getScene().getWindow(),"Fallo al generar informe.\n" + e.toString());
            logUser.severe("Excepción al generar informe: " + e.toString());
			e.printStackTrace();

		} catch (Exception e) {
            //En caso de excepción general, mostrar un mensaje de error y registrar la excepción en el log de errores.
            toast.show((Stage) gpFormInformeAlumno.getScene().getWindow(),"Fallo al generar informe.\n" + e.toString());
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
    	
    	Integer cont_hombres = 0;
    	Integer cont_mujeres = 0;
    	Double cont_edad = 0.0;
    	
        //Calcular los totales Alumnos hombres y mujeres y suma las edades.
    	for(Alumno a : filtro) {
    		switch (a.getGenero()) {
	    		case HOMBRE -> {
	    			cont_hombres++;
	    		}
	    		
	    		case MUJER -> {
	    			cont_mujeres++;
	    		}
    		}
    		cont_edad += a.getEdad();
    	}
    	
        //Calcula la media de edad de los Alumnos en la lista filtro.
    	Double media_edad = cont_edad / filtro.size();
    	
        //Establecer los parámetros en el HashMap.
    	parameters.put("autor", (tfNombreUsuario.getText().isBlank()) ? " " : tfNombreUsuario.getText());
    	parameters.put("telefono", (tfTelefono.getText().isBlank()) ? " " : Integer.toString(newUsuario.getTelefono()));
    	parameters.put("email", (tfEmail.getText().isBlank()) ? " " : tfEmail.getText());
    	parameters.put("fecha_informe", LocalDate.now().format(formatter));
    	parameters.put("texto_informe",  (taTexto.getText() == null) ? "" : taTexto.getText());
        parameters.put("total_alumnos", lbNumeroAlumnos.getText());
        parameters.put("cont_hombres", cont_hombres);
        parameters.put("cont_mujeres", cont_mujeres);
        parameters.put("media_edad", decimalFormat.format(media_edad));
        parameters.put("filtro_localidad", cbLocalidad.getValue().toString());
        parameters.put("filtro_genero", CadenaUtil.capitalize(cbGenero.getValue().toString()));
        parameters.put("filtro_estado", CadenaUtil.capitalize(cbEstado.getValue().toString()));
        parameters.put("orden_lista", CadenaUtil.capitalize(cbOrdenar.getValue().toString()));
        
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

        /* 
        if(cbEmail.getValue() != null && cbEmail.getValue().equals(Constants.EMAIL_OTHER) && !emailMatcher.matches()) {
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
        } else {
            camposCorrectos = true;
        }*/

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
        alerta.initOwner((Stage) gpFormInformeAlumno.getScene().getWindow());
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
     * Establece la lista de alumnos en el controlador.
     *
     * @param lista La lista de alumnos a establecer.
     */
    public void setListaAlumnos(ObservableList<Alumno> lista) {
		filtro = new FilteredList<Alumno>(lista); //Inicia el filtro pasándole el listado de alumnos.
		
        configurarFiltro(""); //Configura el filtro.
        setupDatosFiltro(); // Configura los bindings para actualizar los labels de información del filtro.
	}


    /**
     * Configura los Labels que muestran los datos del filtro y las propiedades enlazadas.
     * Actualiza los labels con la información del filtro.
     */
    private void setupDatosFiltro() {
        IntegerBinding totalAlumnos = Bindings.createIntegerBinding(
                () -> (int) filtro.stream().count(), filtro);
        
        lbNumeroAlumnos.textProperty()
                .bind(Bindings.createStringBinding(() -> String.format("%d", totalAlumnos.get()), totalAlumnos));
    }
    
}
