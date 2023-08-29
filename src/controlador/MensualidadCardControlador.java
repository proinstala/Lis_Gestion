package controlador;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import modelo.Alumno;
import modelo.Mensualidad;
import utilidades.Constants;
import utilidades.Fechas;


public class MensualidadCardControlador implements Initializable {

    private Logger logUser;
    private DateTimeFormatter formatter;
    private DecimalFormat decimalFormat;
    private double x, y;


    @FXML
    private AnchorPane apCardMensualidad;

    @FXML
    private AnchorPane apIzquierda;

    @FXML
    private Button btnCerrar;

    @FXML
    private ImageView ivImagenPago;

    @FXML
    private Label lbIdAlumno;

    @FXML
    private Label lbAnotacion;

    @FXML
    private Label lbApellidos;

    @FXML
    private Label lbAsistenciaSemanal;

    @FXML
    private Label lbImporteLabel;

    @FXML
    private Label lbEmail;

    @FXML
    private Label lbEstadoPagoLabel;

    @FXML
    private Label lbEstadoPago;

    @FXML
    private Label lbEstadoPagoVista;

    @FXML
    private Label lbFechaPago;

    @FXML
    private Label lbFormaPago;

    @FXML
    private Label lbIdMensualidad;

    @FXML
    private Label lbImporte;

    @FXML
    private Label lbImporteVista;

    @FXML
    private Label lbMesPago;

    @FXML
    private Label lbNombre;

    @FXML
    private Label lbTelefono;

    @FXML
    private Label lbYearPago;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Añadir clases de estilo CSS a elementos.
        lbEstadoPagoLabel.getStyleClass().add("color_texto_negro");
        lbImporteLabel.getStyleClass().add("color_texto_negro");

        //Carga la imagen en ImageView.
        Image imagenPago;
        try {
            //Intentar cargar la imagen desde el recurso en el IDE y en el JAR.
            imagenPago = new Image(getClass().getResourceAsStream("/recursos/pago_3_128.png"));
        } catch (Exception e) {
            //Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR.
            imagenPago = new Image("/recursos/pago_3_128.png");
            
        }
        ivImagenPago.setImage(imagenPago); //Establecer la imagen cargada en el ImageView.

        //Configurar el evento cuando se presiona el ratón en el panel apCardMensualidad.
        apCardMensualidad.setOnMousePressed(mouseEvent -> {
            //Obtener las coordenadas X e Y del ratón en relación con la escena.
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });

        //Configurar el evento cuando se arrastra el ratón en el panel apCardMensualidad.
        apCardMensualidad.setOnMouseDragged(mouseEvent -> {
            //Obtener la referencia al Stage actual y establecer las nuevas coordenadas X e Y.
            ((Stage) apCardMensualidad.getScene().getWindow()).setX(mouseEvent.getScreenX() - x);
            ((Stage) apCardMensualidad.getScene().getWindow()).setY(mouseEvent.getScreenY() - y);
        });

        logUser = Logger.getLogger(Constants.USER); //Crea una instancia de la clase Logger asociada al nombre de registro.

        //Formatos para mostrar la fecha y para el importe.
        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); //Crear un formateador de fecha con el patrón "dd/MM/yyyy".
        decimalFormat = new DecimalFormat("#0.00");

        //Configurar un evento de clic del ratón para el botón "Cerrar".
        btnCerrar.setOnMouseClicked(e -> {
            ((Stage) apCardMensualidad.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
        });
    }


    /**
     * Establece los modelos de un alumno y una mensualidad en los elementos gráficos correspondientes.
     *
     * @param alumno El objeto Alumno a mostrar en los elementos gráficos.
     * @param mensualidad El objeto Mensualidad a mostrar en los elementos gráficos.
     */
    public void setModelos(Alumno alumno, Mensualidad mensualidad) {
        try {
            lbIdAlumno.setText(Integer.toString(alumno.getId()));
            lbNombre.setText(alumno.getNombre());
            lbApellidos.setText(alumno.getApellido1() + " " + alumno.getApellido2());
            lbTelefono.setText(Integer.toString(alumno.getTelefono()));
            lbEmail.setText(alumno.getEmail());

            lbIdMensualidad.setText(Integer.toString(mensualidad.getId()));
            lbFormaPago.setText(mensualidad.getFormaPago().toString());
            lbMesPago.setText(Fechas.obtenerNombreMes(mensualidad.getFecha().getMonthValue()));
            lbYearPago.setText(Integer.toString(mensualidad.getFecha().getYear()));
            lbEstadoPago.setText(mensualidad.getEstadoPago().toString());
            lbAsistenciaSemanal.setText(Integer.toString(mensualidad.getAsistenciasSemanales()));
            lbFechaPago.setText((mensualidad.getFechaPago() == null)? "" : mensualidad.getFechaPago().format(formatter).toString());
            lbImporte.setText(decimalFormat.format(mensualidad.getImporte()));
            lbAnotacion.setText(mensualidad.getAnotacion());

            lbEstadoPagoVista.setText(mensualidad.getEstadoPago().toString());
            lbImporteVista.setText(decimalFormat.format(mensualidad.getImporte()));
            
        } catch (Exception e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        }
        
    }
}
