package controlador;

import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import baseDatos.ConexionBD;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;
import javafx.util.converter.NumberStringConverter;
import modelo.Alumno;
import modelo.EstadoAlumno;
import modelo.EstadoPago;
import modelo.FormaPago;
import modelo.Mensualidad;
import modelo.Toast;
import utilidades.Constants;
import utilidades.Fechas;


/**
 * Controlador de la interfaz gráfica para gestionar las mensualidades.
 * Vista asociada: mensualidadFormVista.FXML.
 * 
 * @author David Jimenez Alonso
 */
public class MensualidadFormControlador implements Initializable {

    public final String MODO_NUEVA_MENSUALIDAD = "CREAR";
    public final String MODO_EDITAR_MENSUALIDAD = "EDITAR";

    private String modoControlador;
    private Mensualidad oldMensualidad;
    private Mensualidad newMensualidad;
    private Alumno oldAlumno;
    private ObservableList<Alumno> listadoAlumnosGeneral;
    private ObservableList<Mensualidad> listadoMensualidadesGeneral;
    private DateTimeFormatter formatter;
    private DecimalFormat decimalFormat;
    private ConexionBD conexionBD;
    private Logger logUser;
    private Map<Integer, Double> precios_clases;
    private Toast toast;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnConfirmacion; 

    @FXML
    private ComboBox<Integer> cbAsistenciaSemanal;

    @FXML
    private ComboBox<EstadoPago> cbEstadoPago;

    @FXML
    private ComboBox<FormaPago> cbFormaPago;

    @FXML
    private ComboBox<String> cbMes;

    @FXML
    private ComboBox<Integer> cbYear;

    @FXML
    private ComboBox<Alumno> cbAlumnos;

    @FXML
    private DatePicker dpFechaPago;

    @FXML
    private GridPane gpFormMensualidad;

    @FXML
    private ImageView ivImagenTipoFormulario;

    @FXML
    private ImageView ivClearFechaPago;

    @FXML
    private Label lbTitulo;

    @FXML
    private Pane pSeparador;

    @FXML
    private TextArea taAnotacion;

    @FXML
    private TextField tfAlumnoAsistenciaSemanal;

    @FXML
    private TextField tfAlumnoFormaPago;

    @FXML
    private TextField tfIdAlumno;

    @FXML
    private TextField tfIdMensualidad;

    @FXML
    private TextField tfImporte;

    @FXML
    private TextField tfNombreAlumno;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Añadir clases de estilo CSS a elementos.
        btnCancelar.getStyleClass().add("boton_rojo");
    	gpFormMensualidad.getStyleClass().add("fondo_ventana_degradado_toRight");
        pSeparador.getStyleClass().add("panelSeparador"); //Panel separador de barra superior.

        //Establecer imagen formulario.
        Image imagenMensualidad;
        Image clearFechaPago;
        try {
            //Intentar cargar la imagen desde el recurso en el IDE y en el JAR.
            imagenMensualidad = new Image(getClass().getResourceAsStream("/recursos/pago_1_128.png"));
            clearFechaPago = new Image(getClass().getResourceAsStream("/recursos/clear_1_24.png"));
        } catch (Exception e) {
            //Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR..
            imagenMensualidad = new Image("/recursos/pago_1_128.png");
            clearFechaPago = new Image("/recursos/clear_1_24.png");
            
        }
        //Establecer las imagenes cargadas en los ImageView.
        ivImagenTipoFormulario.setImage(imagenMensualidad); 
        ivClearFechaPago.setImage(clearFechaPago); 

        Tooltip tltClearFechaPago = new Tooltip("Borrar Fecha"); //Crear Tooltip.
        tltClearFechaPago.setShowDelay(Duration.seconds(0.5));      //Establecer retardo de aparición.
        Tooltip.install(ivClearFechaPago, tltClearFechaPago);         //Establecer Tooltip a ImageView.

        logUser = Logger.getLogger(Constants.USER); //Crea una instancia de la clase Logger asociada al nombre de registro.
        conexionBD = ConexionBD.getInstance();      //Obtener una instancia de la clase ConexionBD utilizando el patrón Singleton.
        toast = new Toast();

        //Modifica el formato en el que se muestra la fecha en el DatePicker.
        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); //Crear un formateador de fecha con el patrón "dd/MM/yyyy".
        dpFechaPago.setConverter(new LocalDateStringConverter(formatter, null)); //Establecer un convertidor de cadena de fecha para el control DatePicker dpFechaPago.

        decimalFormat = new DecimalFormat("#0.00");
        
        //Creo un ArrayList de Integer con valores de 2020 hasta 2050 y cargo el ArrayList en el ComboBox cbAnio.
        int yearInicial = 2020;
        ArrayList<Integer> listaYears = new ArrayList<Integer>();
        for (int i = 0; i <= 30; i++) {
            listaYears.add(yearInicial + i);
        }
        //Si el año actual no esta en la lista, lo agrega.
        if(!listaYears.contains(LocalDate.now().getYear())) {
            listaYears.add(LocalDate.now().getYear());
        }
        cbYear.setItems(FXCollections.observableArrayList(listaYears));

        //Carga en los ComboBox los valores para seleccionar.
        cbMes.setItems(FXCollections.observableArrayList(Fechas.obtenerMesesDelAnio().values()));
        cbFormaPago.setItems(FXCollections.observableArrayList(FormaPago.values()));
        cbEstadoPago.setItems(FXCollections.observableArrayList(EstadoPago.values()));

        if(obtenerPreciosClase()) {
            cbAsistenciaSemanal.setItems(FXCollections.observableArrayList(precios_clases.keySet()));
            cbAsistenciaSemanal.getSelectionModel().selectedItemProperty().addListener((o, nv, ov) -> {
                if(ov != 0) {
                    tfImporte.setText(decimalFormat.format(precios_clases.get(ov)));
                }
            });
        } else {
            cbAsistenciaSemanal.setItems(FXCollections.observableArrayList(new Integer[] {1,2,3,4}));
        }

        configurarCbAlumnos();

        //Configura los Texfiel que no puedan seleccionarse.
        tfIdMensualidad.setDisable(true);
        tfIdAlumno.setDisable(true);
        tfNombreAlumno.setDisable(true);
        tfAlumnoAsistenciaSemanal.setDisable(true);
        tfAlumnoFormaPago.setDisable(true);

        dpFechaPago.setEditable(false);

        //Muestra el calendario emergente al hacer clic en el TextField del DatePiker.
		dpFechaPago.getEditor().setOnMouseClicked(event -> {
            dpFechaPago.show();
        });

        ivClearFechaPago.setOnMouseClicked(e -> {
            dpFechaPago.setValue(null);
        });

        //Configurar un evento de clic del ratón para el botón "Cerrar".
        btnCancelar.setOnMouseClicked(e -> {
            ((Stage) gpFormMensualidad.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
        });

        //Configurar un evento de clic del ratón para el botón "Confirmar".
        btnConfirmacion.setOnMouseClicked(e -> {
            if(comporobarCampos()) {
                if(modoControlador == MODO_EDITAR_MENSUALIDAD) {
                    if (modificarMensualidad()) {
                        toast.show((Stage) ((Stage) gpFormMensualidad.getScene().getWindow()).getOwner(), "Mensualidad modificada!!.");
                        ((Stage) gpFormMensualidad.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
                    } else {
                        toast.show(((Stage) gpFormMensualidad.getScene().getWindow()), "No se ha modificado la Mensualidad!!.");
                    }
                }
                if(modoControlador == MODO_NUEVA_MENSUALIDAD) {
                    if (nuevaMensualidad()) {
                        toast.show((Stage) ((Stage) gpFormMensualidad.getScene().getWindow()).getOwner(), "Mensualidad creada!!.");
                        System.out.println("CREA NUEVA MENSUALIDAD");
                        ((Stage) gpFormMensualidad.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
                    } 
                }
            }
        });
    }//FIN initialize.


    /**
     * Configura el ComboBox de Alumnos.
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
            this.setAlumno(ov); //Configura los campos de alumno con los valores del objeto seleccionado.
            cbAsistenciaSemanal.setValue(ov.getAsistenciaSemanal()); //Establece en el ComboBox el número de asistencia semanal que tiene el Alumno.
            cbFormaPago.setValue(ov.getFormaPago()); //Establece en el ComboBox la forma de pago que tiene el Alumno.
            tfImporte.setText(decimalFormat.format(precios_clases.get(ov.getAsistenciaSemanal()))); //establece el precio de la mesualida segun las clases semanales que tiene el Alumno.
        });
    }//FIN configurarCbAlumnos.


    /**
     * Obtiene los precios de las clases desde la base de datos.
     *
     * @return true si se obtuvieron los precios exitosamente, false en caso contrario.
     */
    private boolean obtenerPreciosClase() {
        try {
            precios_clases = conexionBD.getPrecioClases();
            if(precios_clases != null) {return true;} //Se obtuvieron los precios exitosamente.
        } catch (SQLException e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        }
        logUser.warning("Fallo al obtener los precios de clases de BD.");
        return false; //No se pudieron obtener los precios.
    }//FIN obtenerPreciosClase.


    /**
     * Comprueba si los campos del formulario están correctamente completados.
     *
     * @return true si todos los campos están correctamente completados, false en caso contrario.
     */
    private boolean comporobarCampos() {
        boolean camposCorrectos = false;

        Pattern importePattern = Pattern.compile("[0-9]{1,3}[,][0-9]{1,2}"); //Patrón para validar el formato del importe.
        Matcher importeMatcher = importePattern.matcher(tfImporte.getText());

        if (cbMes.getValue() == null) {
            mensajeAviso(AlertType.WARNING, 
            "Mes no escogido", 
            "",
            "Selecciona un mes para la fecha de esta Mensualidad.");
        } else if (cbYear.getValue() == null) {
            mensajeAviso(AlertType.WARNING, 
            "Año no escogido", 
            "",
            "Selecciona un Año para fecha de esta Mensualidad.");
        } else if (cbFormaPago.getValue() == null) {
            mensajeAviso(AlertType.WARNING, 
            "Forma de pago.", 
            "",
            "Selecciona una forma de pago para esta Mensualidad.");
        } else if (cbAsistenciaSemanal.getValue() == null || cbAsistenciaSemanal.getValue() == 0) {
            mensajeAviso(AlertType.WARNING, 
            "Número asistencia semanal.", 
            "",
            "Selecciona un número de asistencias para esta Mensualidad.");
        } else if (cbEstadoPago.getValue() == null) {
            mensajeAviso(AlertType.WARNING, 
            "Estado de Pago.", 
            "",
            "Selecciona un estado de pago para esta Mensualidad.");
        } else if (cbEstadoPago.getValue() != EstadoPago.PAGADO && dpFechaPago.getValue() != null) {
            mensajeAviso(AlertType.WARNING, 
            "Estado de Pago.", 
            "",
            "No puede tener una fecha de pago\nsi la mensualida no tiene el estado 'PAGADO'.");
        } else if (cbEstadoPago.getValue() == EstadoPago.PAGADO && dpFechaPago.getValue() == null) {
            mensajeAviso(AlertType.WARNING, 
            "Fecha de pago.", 
            "Selecciona una fecha de pago para esta Mensualidad.",
                "Si el estado del pago es 'PAGADO' tienes que seleccionar"
                + " \nuna fecha de pago");
        } else if (!importeMatcher.matches()) {
            mensajeAviso(AlertType.WARNING, 
            "Importe de mensualidad.", 
            "Introduce un importe para esta mensualidad.",
                "Formato: de uno a 3 digitos, depues una coma seguido de 1 a 2 digitos."
                +"\nEjemplo: 45.50");
        } else if (oldAlumno == null) {
            mensajeAviso(AlertType.WARNING, 
            "Alumno no seleccionado.", 
            "",
            "Selecciona un Alumno para esta Mensualidad.");
        } else if (oldAlumno.getEstado().equals(EstadoAlumno.BAJA) && modoControlador.equals(MODO_NUEVA_MENSUALIDAD)) {
            mensajeAviso(AlertType.WARNING, 
            "Alumno seleccionado.", 
            "Alumno en estado 'BAJA'.",
            "Con estado baja, no se pueden crear mensualidades.");
        } else {
            camposCorrectos = true; //Todos los campos están correctamente completados
        }

        return camposCorrectos;
    }//FIN comporobarCampos.


    /**
     * Modifica la Mensualidad en la base de datos y en la aplicación.
     *
     * @return true si la modificación se realizó correctamente, false en caso contrario.
     */
    private boolean modificarMensualidad() {
        try {
            if (conexionBD.actualizarMensualidad(newMensualidad)) {
                //Actualizar los valores de la Mensualidad original con los nuevos valores.
                oldMensualidad.setValoresMensualidad(newMensualidad);
                return true;
            } else {
                logUser.warning("Fallo al intentar modificar la mensualidad.");
                mensajeAviso(
                        AlertType.ERROR,
                        "Error de Mensualidad",
                        "",
                        "Se ha producido un error al intentar modificar la mensualidad.");
            }
        } catch (SQLException e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
            mensajeAviso(
                    AlertType.ERROR,
                    "Error de Mensualidad",
                    "Se ha producido un error al intentar modicar la Mensualidad en la base de datos.",
                    "ERROR:\n" + e.toString());
        } catch (Exception e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        }
        return false;
    }//FIN modificarMensualidad.


    /**
     * Crea una nueva Mensualidad para el Alumno actual.
     *
     * @return true si la creación de la mensualidad se realizó correctamente, false en caso contrario
     */
    private boolean nuevaMensualidad() {   
        newMensualidad.setIdAlumno(oldAlumno.getId()); //Asignar el ID del Alumno seleccionado a la nueva Mensualidad.
        if(oldAlumno.fechaMensualidadDisponible(newMensualidad.getFecha())) {
            try {
                if(conexionBD.insertarMensualidad(newMensualidad)) {
                    if(oldAlumno.addMensualidad(newMensualidad) && listadoMensualidadesGeneral.add(newMensualidad)) {
                        return true;
                    } else {
                        logUser.warning("Fallo al crear nueva mensualidad. Guardada en base de datos pero no en app. (id_mensualidad: " + newMensualidad.getId() +").");
                        mensajeAviso(
                            AlertType.ERROR,
                            "Error de Mensualidad",
                            "Se ha producido un error al crear la Mensualidad.",
                            "La Mensualidad con id: " + newMensualidad.getId() + " se ha guardado en la base de datos"
                                + "\npero no se ha guardado en la aplicación.");
                    }
                } else {
                    logUser.warning("Fallo al intentar crear mensualidad en la base de datos.");
                     mensajeAviso(
                        AlertType.ERROR,
                        "Error de Mensualidad",
                        "",
                        "Se ha producido un error al intentar crear la mensualidad en la base de datos.");
                }
            } catch (SQLException e) {
                logUser.severe("Excepción: " + e.toString());
                e.printStackTrace();
                mensajeAviso(
                    AlertType.ERROR,
                    "Error de Mensualidad",
                    "Se ha producido un error al intentar crear la Mensualidad en la base de datos.",
                    "ERROR:\n" + e.toString());
            } catch (Exception e) {
                logUser.severe("Excepción: " + e.toString());
                e.printStackTrace();
            }
            
        } else {
            mensajeAviso(
                AlertType.WARNING,
                "Fecha de Mensualidad",
                "",
                "Este alumno ya tiene una Mensualidad con la fecha (Mes,año) seleccionada.");
        }
        return false;
    }//FIN nuevaMensualidad.


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
        alerta.initOwner((Stage) gpFormMensualidad.getScene().getWindow());
        alerta.setHeaderText(cabecera);
        alerta.setContentText(cuerpo);
        alerta.initStyle(StageStyle.DECORATED);
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.showAndWait();
    }

    /**
     * Configura el formulario en modo crear Mensualidad.
     * 
     */
    private void setupModoNuevoMensualidad() {
        modoControlador = MODO_NUEVA_MENSUALIDAD;
        lbTitulo.setText("Nueva Mensualidad");
        setMensualidad(new Mensualidad());
    }

    /**
     * Configura el formulario en modo editar Mensualidad.
     * 
     */
    private void setupModoEditarMensualidad() {
        modoControlador = MODO_EDITAR_MENSUALIDAD;
        lbTitulo.setText("Editar Mensualidad");
        cbAlumnos.setDisable(true);
        cbMes.setDisable(true);
        cbYear.setDisable(true);
    }


    /**
     * Establece el objeto Alumno en el formulario y actualiza los campos de texto correspondientes.
     *
     * @param alumno el objeto Alumno a establecer.
     */
    public void setAlumno(Alumno alumno) {
        oldAlumno = alumno; //Asignar el objeto Alumno proporcionado como el objeto Alumno actual.

        //Desvincular las propiedades de texto de los campos de texto.
        tfIdAlumno.textProperty().unbind();
        tfNombreAlumno.textProperty().unbind();
        tfAlumnoAsistenciaSemanal.textProperty().unbind();
        tfAlumnoFormaPago.textProperty().unbind();

        if(alumno != null) {
            //Vincular las propiedades de texto del Alumno a los campos de texto correspondientes.
            tfIdAlumno.textProperty().bind(alumno.idProperty().asString());
            tfNombreAlumno.textProperty().bind(Bindings.concat(alumno.nombreProperty(), " ", alumno.apellido1Property(), " ", alumno.apellido2Property()));
            tfAlumnoAsistenciaSemanal.textProperty().bind(alumno.asistenciaSemanalProperty().asString());
            tfAlumnoFormaPago.textProperty().bind(alumno.formaPagoProperty().asString());
        }
    }


    /**
     * Establece el objeto Mensualidad en el formulario y actualiza los campos de la interfaz de usuario correspondientes.
     *
     * @param mensualidad el objeto Mensualidad a establecer.
     */
    public void setMensualidad(Mensualidad mensualidad) {
        oldMensualidad = mensualidad; //Asignar el objeto Mensualidad proporcionado como el objeto Mensualidad actual.
        newMensualidad = new Mensualidad(mensualidad); //Crear una nueva instancia de Mensualidad basada en la mensualidad proporcionada.

        //Asignar los valores de los campos de la interfaz de usuario según la nueva Mensualidad.
        cbMes.setValue(Fechas.obtenerNombreMes(newMensualidad.getFecha().getMonthValue()));
        cbYear.setValue(newMensualidad.getFecha().getYear());
        cbFormaPago.setValue(newMensualidad.getFormaPago());
        cbAsistenciaSemanal.setValue(newMensualidad.getAsistenciasSemanales());
        cbEstadoPago.setValue(newMensualidad.getEstadoPago());

        tfIdMensualidad.setText(Integer.toString(newMensualidad.getId()));
        tfImporte.textProperty().bindBidirectional(newMensualidad.importeProperty(), new NumberStringConverter("#0.00"));
        taAnotacion.textProperty().bindBidirectional(newMensualidad.anotacionProperty());

        //Establecer listeners para los cambios en los campos seleccionados de la interfaz de usuario.
        cbMes.getSelectionModel().selectedItemProperty().addListener((o, nv, ov) -> {
            YearMonth nuevoMonth = YearMonth.of(newMensualidad.getFecha().getYear(), Month.valueOf(Fechas.traducirMesAIngles(ov)));
            newMensualidad.fechaProperty().set(nuevoMonth);
        });

        cbYear.getSelectionModel().selectedItemProperty().addListener((o, nv, ov) -> {
            YearMonth nuevoYear = YearMonth.of(ov, Month.valueOf(newMensualidad.getFecha().getMonth().toString()));
            newMensualidad.fechaProperty().set(nuevoYear);
        });

        cbAsistenciaSemanal.getSelectionModel().selectedItemProperty().addListener((o, nv, ov) -> {
            newMensualidad.asistenciasSemanalesProperty().set(ov);
        });

        cbFormaPago.getSelectionModel().selectedItemProperty().addListener((o, nv, ov) -> {
            newMensualidad.formaPagoProperty().set(ov);
        });

        cbEstadoPago.getSelectionModel().selectedItemProperty().addListener((o, nv, ov) -> {
            newMensualidad.estadoPagoProperty().set(ov);
        });

        dpFechaPago.valueProperty().bindBidirectional(newMensualidad.fechaPagoProperty());
    }



    /**
     * Establece las caracteristicas del formulario segun la accion que se quiera hacer.
     * 
     * @param modo Modo en que se va a configurar el formulario.
     */
    public void modoFormulario(String modo) {
        switch (modo) {
            case MODO_NUEVA_MENSUALIDAD:
                setupModoNuevoMensualidad();
                break;
            case MODO_EDITAR_MENSUALIDAD:
                setupModoEditarMensualidad();
                break;
        
            default:
                setupModoNuevoMensualidad();
                break;
        }
    }


    /**
     * Establece la lista de alumnos para este controlador.
     *
     * @param lista La lista de alumnos a establecer.
     */
    public void setListaAlumnos(ObservableList<Alumno> lista) {
        listadoAlumnosGeneral = lista;
        cbAlumnos.setItems(listadoAlumnosGeneral); 
	}


    /**
     * Establece la lista de mensualidades para este controlador.
     * 
     * @param lista La lista de mensualidades a establecer.
     */
    public void setListaMensualidades(ObservableList<Mensualidad> lista) {
        listadoMensualidadesGeneral = lista; 
		
	}
}
