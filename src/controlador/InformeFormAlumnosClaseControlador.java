package controlador;

import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
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
import baseDatos.ConexionBD;
import colecciones.ColeccionAlumnos;
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
import javafx.scene.control.ListCell;
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
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import modelo.Alumno;
import modelo.AlumnoReport;
import modelo.ClaseReport;
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
import utilidades.Fechas;
import utilidades.Toast;


public class InformeFormAlumnosClaseControlador implements Initializable {

    private final String ORDEN_ID = "ID";
    private final String ORDEN_NOMBRE = "NOMBRE";
    private final String ORDEN_LOCALIDAD = "LOCALIDAD";
    private final String ORDEN_ESTADO = "ESTADO";
    private final String ORDEN_GENERO = "GENERO";

    private FilteredList<Alumno> filtro;
    private ObservableList<String> tipoEmail;
    private ArrayList<Alumno> listaAlumnosGeneral;
    private ArrayList<Alumno> listaAlumnosClases;
    private ArrayList<AlumnoReport> listaAlumnoReport;
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
    private ComboBox<Alumno> cbAlumnos;

    @FXML
    private ComboBox<Integer> cbAnio;

    @FXML
    private ComboBox<String> cbEmail;

    @FXML
    private ComboBox<String> cbEstado;

    @FXML
    private ComboBox<String> cbGenero;

    @FXML
    private ComboBox<String> cbLocalidad;

    @FXML
    private ComboBox<String> cbMes;

    @FXML
    private ComboBox<String> cbOrdenar;

    @FXML
    private CheckBox checkbAlumnos;

    @FXML
    private CheckBox chekbMetadatos;

    @FXML
    private CheckBox checkbFormatoAlumno;

    @FXML
    private GridPane gpFormInformeAlumnoClase;

    @FXML
    private ImageView ivImagenTipoFormulario;

    @FXML
    private Label lbNumeroAlumnos;

    @FXML
    private Label lbNumeroClases;

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
            formulario = new Image(getClass().getResourceAsStream("/recursos/informe_2_72.png")); //Forma desde IDE y JAR.
        } catch (Exception e) {
            //Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR.
            formulario = new Image("/recursos/informe_2_72.png"); //Forma desde el JAR.
        }
        //Establecer las imagenes cargadas en los ImageView.
        ivImagenTipoFormulario.setImage(formulario);

        conexionBD = ConexionBD.getInstance();      //Obtener una instancia de la clase ConexionBD utilizando el patrón Singleton.
        logUser = Logger.getLogger(Constants.USER); //Crea una instancia de la clase Logger asociada al nombre de registro.
        toast = new Toast(); //Crea una instancia de clase Toast para mostrar notificaciones en la interfaz grafica.

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
            ((Stage) gpFormInformeAlumnoClase.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
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

        //Habilita o deshabilita diferentes controles dependiendo del estado del CheckBox checkbAlumnos.
        cbAlumnos.disableProperty().bind(checkbAlumnos.selectedProperty());
        cbLocalidad.disableProperty().bind(checkbAlumnos.selectedProperty().not());
        cbGenero.disableProperty().bind(checkbAlumnos.selectedProperty().not());
        cbEstado.disableProperty().bind(checkbAlumnos.selectedProperty().not());
        checkbFormatoAlumno.disableProperty().bind(checkbAlumnos.selectedProperty());

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

        taTexto.setText(textoInformeGenerico()); //Establece el texto predefinido en el TextArea taTexto.
        nombreInforme = "informe_alumnos_clase_" + LocalDateTime.now().format(formatterTime); //Nombre predefinido para el informe. "informe_alumnos_clase_dd_MM_yyyy-HH_mm_ss".
        tfNombreInforme.setText(nombreInforme); //Establece el texto predefinido para el nombre del informe en el TextField tfNombreInforme.

        //Configura el ComboBox cbEstado.
        ObservableList<String> listadoEstado = FXCollections.observableArrayList();
        listadoEstado.setAll(EstadoAlumno.ACTIVO.toString(), EstadoAlumno.BAJA.toString(), "TODOS");
        cbEstado.setItems(listadoEstado);
        cbEstado.setValue("TODOS"); //Valor inicial.

        //Configurar Listener para el ComboBox cbEstado.
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
            //En caso de excepción, registrar la excepción en el log del usuario.
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

        //Configura el ComboBox cbAlumnos con la información de Alumnos y establece un listener para cuando se seleccione un elemento de el ComboBox.
        configurarCbAlumnos();

        //Establece un listener para cuando se hace click en el CheckBox checkbAlumnos.
        checkbAlumnos.setOnMouseClicked(e -> {
            if (checkbAlumnos.isSelected()) {
                configurarFiltro("");
            } else {
                configurarFiltro(cbAlumnos.getValue().getNombreCompleto());
            }
        });

        //Crea un ArrayList de String con valores de 2020 hasta 2050 y cargo el ArrayList en el ComboBox cbAnio.
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

        //Creo un ObservableList<String> con el nombre de los meses del año. Cargo la lista en el ComboBox cbMes.
        ObservableList<String> listMeses = FXCollections.observableArrayList(Fechas.obtenerMesesDelAnio().values());
        listMeses.add("TODOS"); //Añado a listMeses el valor TODOS.
        cbMes.setItems(listMeses);

        //Valores iniciales de los ComboBox de mensualidades. 
        try{
            cbAnio.setValue(LocalDate.now().getYear()); //Establece el año actual marcado por defecto.
        }catch (IllegalArgumentException e) {
            cbAnio.setValue(2023); //Establece 2023 si el año actual no se encuentra entre los valores del ComboBox cbAnio.
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        }
        
        cbMes.getSelectionModel().select(LocalDate.now().getMonthValue() -1); //Establece el mes actual marcado por defecto.

        //Configurar Listener para el ComboBox cbAnio.
        cbAnio.setOnAction(e -> {
            listaAlumnoReport = obtenerListadoAlumnosReport();
            configurarClasesAlumnos();
        });

        //Configurar Listener para el ComboBox cbMes.
        cbMes.setOnAction(e -> {
            configurarClasesAlumnos();
        });

        checkbAlumnos.setSelected(true); //Seleccionado por defecto "Todos los Alumnos"

        checkbFormatoAlumno.setOnMouseClicked(e -> {
            if(checkbFormatoAlumno.isSelected()) {
                taTexto.setText(textoInformePersonalizado());
            } else {
                taTexto.setText(textoInformeGenerico());
            }
        });
    }


    /**
     * Obtiene un listado de objetos AlumnoReport que contienen información de los alumnos y sus clases asociadas,
     * filtrados por el año seleccionado.
     *
     * @return Un ArrayList de objetos AlumnoReport que contiene información de los alumnos y sus clases asociadas.
     */
    private ArrayList<AlumnoReport> obtenerListadoAlumnosReport() { 
            try {
                return conexionBD.getListaAlumnoReport(listaAlumnosGeneral, cbAnio.getValue());
            } catch (Exception e) {
                //En caso de excepción, mostrar una notificación de error en la interfaz gráfica, registrar la excepción en el log del usuario y cerrar este Stage.
                toast.show((Stage) ((Stage) gpFormInformeAlumnoClase.getScene().getWindow()).getOwner(),"Fallo al generar formulario informe.\n" + e.toString());
                logUser.severe("Excepción al generar informe: " + e.toString());
                e.printStackTrace();

                ((Stage) gpFormInformeAlumnoClase.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
            }
        
        //Si ocurre una excepción o no se pueden obtener los informes, se devuelve un null.
        return null;
    }


    /**
     * Configura el ComboBox de Alumnos.
     * 
     */
    private void configurarCbAlumnos() {
        //Establecer el texto a mostrar en el ComboBox utilizando un CellFactory.
        cbAlumnos.setCellFactory(param -> new ListCell<Alumno>() {
            @Override
            protected void updateItem(Alumno a, boolean empty) {
                super.updateItem(a, empty);
                if (empty || a == null) {
                    setText(null);
                } else {
                    setText(a.getId() + " - " + a.getNombre()); //Mostrar el ID y el nombre del Alumno en el ComboBox.
                }
            }
        });

        //Establecer el texto a mostrar en el ComboBox cuando está desplegado utilizando un StringConverter.
        cbAlumnos.setConverter(new StringConverter<Alumno>() {
            @Override
            public String toString(Alumno a) {
                if (a != null) {
                    //Mostrar el ID y el nombre del Alumno en el ComboBox cuando está desplegado.
                    return a.getId() + " - " + a.getNombre();
                }
                return null;
            }

            @Override
            public Alumno fromString(String string) {
                // No se necesita esta implementación para este caso.
                return null;
            }
        });

        //Establece un listener para que cuando se seleccione un elemento del ComboBox cbAlumnos.
        cbAlumnos.getSelectionModel().selectedItemProperty().addListener((o, nv, ov) -> {
            configurarFiltro(ov.getNombreCompleto());
            if(checkbFormatoAlumno.isSelected()) {taTexto.setText(textoInformePersonalizado());}
        });
    }//FIN configurarCbAlumnos.


    /**
     * Devuelve un String con un texto predefinido.
     * 
     * @return String con el texto predefinido.
     */
    private String textoInformeGenerico() {
        String texto;
        texto = "Informe de Alumnos y clases en las que esta inscrito filtrando los datos según la configuración establecida.";
        return texto;
    }


    /**
     * Devuelve un String con un texto predefinido con datos de un alumno.
     * 
     * @return String con el texto predefinido.
     */
    private String textoInformePersonalizado() {
        String texto;
        texto = "Clases en las que esta inscrito el alumn@ " + cbAlumnos.getValue().getNombre()  + " filtrando los datos según la configuración establecida.";
        return texto;
    }


    /**
     * Configura el filtro según los criterios seleccionados.
     *
     * @param texto El texto de búsqueda utilizado para filtrar.
     */
    private void configurarFiltro(String texto) {

        filtro.setPredicate(obj -> {
            if (checkbAlumnos.isSelected()) {
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
            } else {
                if(obj.getNombreCompleto().toLowerCase().contains(texto.toLowerCase())) {
                    return true;
                }    
                return false;
            }
        });

        if(listaAlumnoReport != null) {
            //Itera y guarada la lisa de Alumnos filtrados filtrando tambien las mensualidades de cada Alumno segun los criterios seleccionados.
            configurarClasesAlumnos();
        }
    }


    /**
     * Configura la lista de alumnos que se mostrará en el informe con sus respectivas clases asociadas, a partir de los resultados obtenidos
     * tras filtrar la lista de alumnos y el listado de alumnosReport.
     * 
     */
    private void configurarClasesAlumnos() {
        listaAlumnosClases = new ArrayList<Alumno>(); //Inicializa la lista que se muestra en el informe.
        int totalClases = 0; //Inicializa el contador total de clases.

        for (Alumno a : filtro) {
            for (AlumnoReport alumnoR : listaAlumnoReport) {
                //Verifica si el ID del alumno coincide con el ID del alumno en el informe de alumno (AlumnoReport).
                if(a.getId() == alumnoR.getId()) {
                    ArrayList<ClaseReport> listaClasesFiltrada;

                    if (cbMes.getValue().equals("TODOS")) { 
                        listaClasesFiltrada = new ArrayList<ClaseReport>(alumnoR.getListaClases()); //inicializa la lista de clases filtrada con las lista de clases del alumnoR.
                    } else {
                        listaClasesFiltrada = new ArrayList<ClaseReport>(); //Inicializa la lista de clases filtrada.

                        //Itera sobre el listado de clases del alumnoR para comprobar cuales coninciden con el mes seleccionado en cbMes.
                        for (ClaseReport clase : alumnoR.getListaClases()) {
                            if(clase.getFechaJornada().getMonthValue() == (cbMes.getSelectionModel().getSelectedIndex()) +1) {
                                listaClasesFiltrada.add(clase); //Agrega la clase al listado filtrado.
                            }
                        }
                    }
                    
                    //Define un comparador para ordenar las clases por fecha y número.
                    Comparator<ClaseReport> comparador = Comparator.comparing(ClaseReport::getFechaJornada).thenComparing(ClaseReport::getNumero);
                    Collections.sort(listaClasesFiltrada, comparador); //Odena la lista segun los criterios seleccionados.

                    //Crea una copia del alumnoR y se le establece la lista de clases filtrada.
                    AlumnoReport alumnoRCopia = new AlumnoReport(alumnoR, listaClasesFiltrada);

                    listaAlumnosClases.add(alumnoRCopia); //Agrega el alumnoRCopia al listado que se mostrara en el informe.
                    totalClases += alumnoRCopia.getListaClases().size(); //Suma el total de clases del alumno al total general.
                }
            }
        }

        //Actualizar el campo "lbNumeroClases" con el total de clases.
        lbNumeroClases.setText("" + totalClases);
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
            
            
            Collections.sort(listaAlumnosClases, comparador); //Odena la lista segun los criterios seleccionados.
            ColeccionAlumnos.setColeccionAlumnos(listaAlumnosClases); //establece la lista a ColeccionAlumnos para que JasperReports pueda haceder a ella.
            
            /* 
            //codigo para ordenar la lista filtro. -------------------------------------

            SortedList<Alumno> sortedList = new SortedList<Alumno>(filtro);
            
            //Ordena la lista sortedList (colección de alumnos) utilizando el comparador.
            sortedList.setComparator(comparador);
            
            //Establece la lista ordenada de alumnos en la clase ColeccionAlumnos.
            ColeccionAlumnos.setColeccionAlumnos(sortedList);
            //--------------------------------------------------------------------------
            */
            
        } catch (Exception e) {
            //En caso de excepción, mostrar un mensaje de error y registrar la excepción en el log del usuario.
            toast.show((Stage) gpFormInformeAlumnoClase.getScene().getWindow(),"Fallo al generar informe.\n" + e.toString());
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

        if(checkbFormatoAlumno.isSelected()) {
            jasperStream = getClass().getResourceAsStream("/reports/report_alumno_clase_formateado.jasper");
        } else {
            jasperStream = getClass().getResourceAsStream("/reports/report_alumno_clase.jasper");
        }
        
        
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
                toast.show((Stage) ((Stage) gpFormInformeAlumnoClase.getScene().getWindow()).getOwner(),"Informe Generado!.");

                //Cerrar la ventana actual después de enviar el correo.
                ((Stage) gpFormInformeAlumnoClase.getScene().getWindow()).close(); // Obtener la referencia al Stage actual y cerrarlo.
            }
			
		} catch (JRException e) {
            //En caso de excepción JRException, mostrar un mensaje de error y registrar la excepción en el log de errores.
            toast.show((Stage) gpFormInformeAlumnoClase.getScene().getWindow(),"Fallo al generar informe.\n" + e.toString());
            logUser.severe("Excepción al generar informe: " + e.toString());
			e.printStackTrace();

		} catch (Exception e) {
            //En caso de excepción general, mostrar un mensaje de error y registrar la excepción en el log de errores.
            toast.show((Stage) gpFormInformeAlumnoClase.getScene().getWindow(),"Fallo al generar informe.\n" + e.toString());
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
    	
    	Double media_edad = 0.0;
    	Double cont_years = 0.0;
    	int cont_hombres = 0;
    	int cont_mujeres = 0;
    	
        //Itera sobre la lista listaAlumnosMensualidades.
    	for(Alumno a : listaAlumnosClases) {
    		if(a.getGenero().equals(Genero.HOMBRE)) {
    			cont_hombres ++;
    		} else {
    			cont_mujeres ++;
    		}
    		cont_years += a.getEdad();
    	}
    	
    	media_edad = cont_years / listaAlumnosClases.size();
    	
        //Establecer los parámetros en el HashMap.
    	parameters.put("autor", (tfNombreUsuario.getText().isBlank()) ? " " : tfNombreUsuario.getText());
    	parameters.put("telefono", (tfTelefono.getText().isBlank()) ? " " : Integer.toString(newUsuario.getTelefono()));
    	parameters.put("email", (tfEmail.getText().isBlank()) ? " " : tfEmail.getText());
    	parameters.put("fecha_informe", LocalDate.now().format(formatter));
    	parameters.put("texto_informe",  (taTexto.getText() == null) ? "" : taTexto.getText());

        parameters.put("total_alumnos", lbNumeroAlumnos.getText());
        parameters.put("total_clases", lbNumeroClases.getText());
        parameters.put("cont_hombres", cont_hombres);
        parameters.put("cont_mujeres", cont_mujeres);
        parameters.put("media_edad", "" + decimalFormat.format(media_edad));

        parameters.put("filtro_alumno", (checkbAlumnos.isSelected())? "Todos" : "" + cbAlumnos.getValue().getId() + " - " + cbAlumnos.getValue().getNombreCompleto());
        parameters.put("filtro_localidad", (checkbAlumnos.isSelected())? cbLocalidad.getValue().toString() : " ");
        parameters.put("filtro_genero", (checkbAlumnos.isSelected())? CadenaUtil.capitalize(cbGenero.getValue().toString()) : " ");
        parameters.put("filtro_estado", (checkbAlumnos.isSelected())? CadenaUtil.capitalize(cbEstado.getValue().toString()) : " ");
        parameters.put("filtro_anio", cbAnio.getValue().toString());
        parameters.put("filtro_mes", (cbMes.getValue().equals("TODOS"))? "Todos" : cbMes.getValue().toString());
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
        alerta.initOwner((Stage) gpFormInformeAlumnoClase.getScene().getWindow());
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
        listaAlumnosGeneral = new ArrayList<Alumno>(lista);
		filtro = new FilteredList<Alumno>(lista); //Inicia el filtro pasándole el listado de alumnos. 
		
        cbAlumnos.setItems(FXCollections.observableArrayList(lista));
        cbAlumnos.setValue(lista.get(0)); //Selecciona por defecto el primer item de la lista.

        listaAlumnoReport = obtenerListadoAlumnosReport();

        configurarFiltro(""); //Configura el filtro.
        setupDatosFiltro(); //Configura los bindings para actualizar los labels de información del filtro.
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
