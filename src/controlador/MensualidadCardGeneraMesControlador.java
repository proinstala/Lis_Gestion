package controlador;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import baseDatos.ConexionBD;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modelo.Alumno;
import modelo.EstadoAlumno;
import modelo.EstadoPago;
import modelo.Mensualidad;
import modelo.Toast;
import utilidades.Constants;
import utilidades.Fechas;

public class MensualidadCardGeneraMesControlador implements Initializable {

    private double x, y;
    private DateTimeFormatter formatter;
    private IntegerBinding alumnoActivos;
    private IntegerBinding numeroMensualidades;
    private ObservableList<Alumno> listadoAlumnosGeneral;
    private ObservableList<Mensualidad> listadoMensualidadesGeneral;
    private ConexionBD conexionBD;
    private Logger logUser;
    private Map<Integer, Double> precios_clases;
    private Toast toast;
    private LocalDate fechaActual;
    private YearMonth fechaSeleccionada;

    @FXML
    private AnchorPane apCardGenerarMensualidad;

    @FXML
    private AnchorPane apIzquierda;

    @FXML
    private Button btnCerrar;

    @FXML
    private Button btnGenerar;

    @FXML
    private ComboBox<String> cbMes;

    @FXML
    private ComboBox<Integer> cbYear;

    @FXML
    private CheckBox chbAddAnotacion;

    @FXML
    private CheckBox chbAddFecha;

    @FXML
    private ImageView ivImagenPago;

    @FXML
    private Label lbAlumnosLabel;

    @FXML
    private Label lbAlumnosVista;

    @FXML
    private Label lbFechaActual;

    @FXML
    private Label lbGenerarLabel;

    @FXML
    private Label lbGenerarVista;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Añadir clases de estilo CSS a elementos.
        lbAlumnosLabel.getStyleClass().add("color_texto_negro");
        lbGenerarLabel.getStyleClass().add("color_texto_negro");

        //Carga la imagen.
        Image imagePago;
        try {
            //Intentar cargar la imagen desde el recurso en el IDE y en el JAR.
            imagePago = new Image(getClass().getResourceAsStream("/recursos/pago_3_128.png")); //Forma desde IDE y JAR.
        } catch (Exception e) {
            //Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR.
            imagePago = new Image("/recursos/pago_3_128.png"); //Forma desde el JAR.
            
        }
        ivImagenPago.setImage(imagePago); //Establecer la imagen cargada en el ImageView.

        //Configurar el evento cuando se presiona el ratón en el panel apCardGenerarMensualidad.
        apCardGenerarMensualidad.setOnMousePressed(mouseEvent -> {
            //Obtener las coordenadas X e Y del ratón en relación con la escena.
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });

        //Configurar el evento cuando se arrastra el ratón en el panel apCardGenerarMensualidad.
        apCardGenerarMensualidad.setOnMouseDragged(mouseEvent -> {
            //Obtener la referencia al Stage actual y establecer las nuevas coordenadas X e Y.
            ((Stage) apCardGenerarMensualidad.getScene().getWindow()).setX(mouseEvent.getScreenX() - x);
            ((Stage) apCardGenerarMensualidad.getScene().getWindow()).setY(mouseEvent.getScreenY() - y);
        });

        logUser = Logger.getLogger(Constants.USER); //Crea una instancia de la clase Logger asociada al nombre de registro.
        conexionBD = ConexionBD.getInstance();
        toast = new Toast();

        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");//Formato dd/MM/yy
        fechaActual = LocalDate.now();
        lbFechaActual.setText(fechaActual.format(formatter).toString());
        fechaSeleccionada = YearMonth.now();

        configurarComboBox();

        //Configurar un evento de clic del ratón para el botón "Cerrar".
        btnCerrar.setOnMouseClicked(e -> {
            ((Stage) apCardGenerarMensualidad.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
        });

        //Configurar un evento de clic del ratón para el botón "Generar".
        btnGenerar.setOnMouseClicked(e -> {
            if (generarMensualidades()) {
                toast.show((Stage) ((Stage) apCardGenerarMensualidad.getScene().getWindow()).getOwner(), "Mensualidades generadas correctamente!!.");
                ((Stage) apCardGenerarMensualidad.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
            }
        });
        
    }//FIN initialize.


    private boolean generarMensualidades() {
        if (numeroMensualidades.get() == 0) {
            mensajeAviso(Alert.AlertType.INFORMATION,
            "Generar Mensualidades",
            "No hay mensualidades ha generar..",
            "El número de mensualidades a generar es 0.");
            
            return false;
        }
        
        if (!obtenerPreciosClase()) {
            mensajeAviso(Alert.AlertType.ERROR,
            "Generar Mensualidades",
            "Error al obtener precios de asistencias.",
            "No se ha podido obtener los precios de las las clases semanales.");

            return false;
        }

        String anotacionGenerica = "";
        if (chbAddAnotacion.isSelected()) {
            anotacionGenerica = "Mensualidad generada automaticamente. ";
        }

        if (chbAddFecha.isSelected()) {
            anotacionGenerica = anotacionGenerica.concat("Fecha de creación: " + fechaActual.format(formatter).toString());
        }

        ArrayList<Mensualidad> nuevaListaMensualidades = new ArrayList<>();
        for (Alumno a : listadoAlumnosGeneral) {
            if(a.getEstado().equals(EstadoAlumno.ACTIVO) && a.fechaMensualidadDisponible(fechaSeleccionada)) {
                Mensualidad mens = new Mensualidad(-1, a.getId(), fechaSeleccionada, fechaActual, a.getFormaPago(), EstadoPago.PENDIENTE, a.getAsistenciaSemanal(), precios_clases.get(a.getAsistenciaSemanal()), anotacionGenerica);
                nuevaListaMensualidades.add(mens);
            }
        }

        try {
            if (conexionBD.insertarListaMensualidades(nuevaListaMensualidades)) {
                listadoMensualidadesGeneral.addAll(nuevaListaMensualidades);
                for (Mensualidad m : nuevaListaMensualidades) {
                    for (Alumno a : listadoAlumnosGeneral) {
                        if(m.getIdAlumno() == a.getId()) {
                            a.addMensualidad(m);
                        }
                    }
                }
            } else {
                logUser.warning("Fallo al insertar lista mensualidades en BD.");
                mensajeAviso(Alert.AlertType.ERROR,
                    "Generar Mensualidades",
                    "Error al insertar mensualidades en base de datos.",
                    "No se ha podido insertar las mensualidades en la base de datos.");
                
                return false;
            }
        } catch (SQLException e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
            mensajeAviso(Alert.AlertType.ERROR,
                "Generar Mensualidades",
                "Error al insertar mensualidades en base de datos.",
                "No se ha podido insertar las mensualidades en la base de datos.");
            return false;
        } catch (Exception e) {
            logUser.severe("Excepción: " + e.toString());
            return false;
        }
        return true;
    }


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
        logUser.warning("Fallo al recuperar precios de clases de BD.");
        return false; //No se pudieron obtener los precios.
    }//FIN obtenerPreciosClase.


    private void configurarComboBox() {
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

        //Creo un ObservableList<String> con el nombre de los meses del año. Cargo la lista en el ComboBox cbMes.
        ObservableList<String> listMeses = FXCollections.observableArrayList(Fechas.obtenerMesesDelAnio().values());
        cbMes.setItems(listMeses);

        cbMes.setValue(Fechas.obtenerNombreMes(fechaActual.getMonthValue()));
        cbYear.setValue(fechaActual.getYear());

        
        //Establecer listeners para los cambios en los campos seleccionados de la interfaz de usuario.
        cbMes.getSelectionModel().selectedItemProperty().addListener((o, nv, ov) -> {
            fechaSeleccionada = YearMonth.of(fechaSeleccionada.getYear(), Month.valueOf(Fechas.traducirMesAIngles(ov)));
            configurarLabels();
        });

        
        cbYear.getSelectionModel().selectedItemProperty().addListener((o, nv, ov) -> {
            fechaSeleccionada = YearMonth.of(ov, fechaSeleccionada.getMonth());
            configurarLabels();
        });
    }//FIN configurarComboBox.


    private void configurarLabels() {
        alumnoActivos = Bindings.createIntegerBinding(
                () -> (int) listadoAlumnosGeneral.stream().filter(alumno -> alumno.getEstado() == EstadoAlumno.ACTIVO).count(), listadoAlumnosGeneral);

        numeroMensualidades = Bindings.createIntegerBinding(
                () -> (int) listadoAlumnosGeneral.stream().filter(alumno -> alumno.getEstado() == EstadoAlumno.ACTIVO && alumno.fechaMensualidadDisponible(fechaSeleccionada)).count(), listadoAlumnosGeneral);
        
        lbAlumnosVista.textProperty()
                .bind(Bindings.createStringBinding(() -> String.format("%d", alumnoActivos.get()), alumnoActivos));

        lbGenerarVista.textProperty()
                .bind(Bindings.createStringBinding(() -> String.format("%d", numeroMensualidades.get()), numeroMensualidades));
    }//FIN configurarLabels.


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
        alerta.initOwner((Stage) apCardGenerarMensualidad.getScene().getWindow());
        alerta.setHeaderText(cabecera);
        alerta.setContentText(cuerpo);
        alerta.initStyle(StageStyle.DECORATED);
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.showAndWait();
    }


    /**
     * Establece la lista de alumnos en el controlador.
     *
     * @param lista La lista de alumnos a establecer.
     */
    public void setListaAlumnos(ObservableList<Alumno> lista) {
        listadoAlumnosGeneral = lista;
        configurarLabels();
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
