package controlador;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
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
import utilidades.Fechas;

public class MensualidadCardControlador implements Initializable {

    private Stage escenario;
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
        //Carga los estilos a los Labels.
        lbEstadoPagoLabel.getStyleClass().add("color_texto_negro");
        lbImporteLabel.getStyleClass().add("color_texto_negro");

        //Configuro que la ventana se pueda mover.
        apCardMensualidad.setOnMousePressed(mouseEvent -> {
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });

        apCardMensualidad.setOnMouseDragged(mouseEvent -> {
            escenario.setX(mouseEvent.getScreenX() - x);
            escenario.setY(mouseEvent.getScreenY() - y);
        });

        //Carga la imagen en ImageView.
        Image imagenPago;
        try {
            //Forma desde IDE y JAR.
            imagenPago = new Image(getClass().getResourceAsStream("/recursos/pago_3_128.png"));
        } catch (Exception e) {
            //Forma desde el JAR.
            imagenPago = new Image("/recursos/pago_3_128.png");
            
        }
        ivImagenPago.setImage(imagenPago);

        //Formatos para mostrar la fecha y para el importe.
        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");//Formato dd/MM/yy
        decimalFormat = new DecimalFormat("#0.00");

        //Listener para el Button btnCerrar. Cierra la ventana al hacer click. 
        btnCerrar.setOnMouseClicked(e -> {
            escenario.close();
        });
    }


    /**
     * Establece los modelos de un alumno y una mensualidad en los elementos gráficos correspondientes.
     *
     * @param alumno El objeto Alumno a mostrar en los elementos gráficos.
     * @param mensualidad El objeto Mensualidad a mostrar en los elementos gráficos.
     */
    public void setModelos(Alumno alumno, Mensualidad mensualidad) {
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
    }


    /**
     * Establece un Stage para este controlador.
     * 
     * @param stage Stage que se establece.
     */
    public void setStage(Stage stage) {
    	this.escenario = stage;
    }
    
}
