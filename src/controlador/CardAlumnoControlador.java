package controlador;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import modelo.Alumno;
import utilidades.Constants;


public class CardAlumnoControlador implements Initializable {
    
    private Logger logUser;
    private DateTimeFormatter formatter;
    private double x, y;
    

    @FXML
    private AnchorPane apCardAlumno;

    @FXML
    private AnchorPane apIzquierda;

    @FXML
    private Button btnCerrar;

    @FXML
    private ImageView ivImagenUser;

    @FXML
    private Label lbApellidos;

    @FXML
    private Label lbCalle;

    @FXML
    private Label lbCodigoPostal;

    @FXML
    private Label lbFecha;

    @FXML
    private Label lbEdad;

    @FXML
    private Label lbEmail;

    @FXML
    private Label lbEstado;

    @FXML
    private Label lbGenero;

    @FXML
    private Label lbIdAlumno;

    @FXML
    private Label lbLocalidad;

    @FXML
    private Label lbNombre;

    @FXML
    private Label lbNumero;

    @FXML
    private Label lbProvincia;

    @FXML
    private Label lbTelefono;

    @FXML
    private Label lbAsistencias;

    @FXML
    private Label lbFormaPago;

    @FXML
    private Label lbEstadoLabel;

    @FXML
    private Label lbAsistenciasLabel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Añadir clases de estilo CSS a elementos.
        apCardAlumno.getStyleClass().add("fondo_ventana_degradado_toRight");
        lbEstadoLabel.getStyleClass().add("color_texto_negro");
        lbAsistenciasLabel.getStyleClass().add("color_texto_negro");
        
        Image imagenUser;
        try {
            //Intentar cargar la imagen desde el recurso en el IDE y en el JAR.
        	imagenUser = new Image(getClass().getResourceAsStream("/recursos/usuario_info_1_128.png"));
        } catch (Exception e) {
            //Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR.
        	imagenUser = new Image("/recursos/usuario_info_1_128.png"); //Forma desde el JAR.
        	
        }
        ivImagenUser.setImage(imagenUser); //Establecer la imagen cargada en el ImageView ivImagenUser.

        //Configurar el evento cuando se presiona el ratón en el panel apCardAlumno.
        apCardAlumno.setOnMousePressed(mouseEvent -> {
            //Obtener las coordenadas X e Y del ratón en relación con la escena.
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });

        //Configurar el evento cuando se arrastra el ratón en el panel apCardAlumno.
        apCardAlumno.setOnMouseDragged(mouseEvent -> {
            //Obtener la referencia al Stage actual y establecer las nuevas coordenadas X e Y.
            ((Stage) apCardAlumno.getScene().getWindow()).setX(mouseEvent.getScreenX() - x);
            ((Stage) apCardAlumno.getScene().getWindow()).setY(mouseEvent.getScreenY() - y);
        });

        logUser = Logger.getLogger(Constants.USER); //Crea una instancia de la clase Logger asociada al nombre de registro.

        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");//Formato dd/MM/yy

        //Configurar un evento de clic del ratón para el botón "Cerrar".
        btnCerrar.setOnMouseClicked(e -> {
            ((Stage) apCardAlumno.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
        });
    }


    /**
     * Establece los valores del alumno en los campos de texto correspondientes en la interfaz.
     * Los valores se obtienen del objeto Alumno proporcionado.
     * Si ocurre alguna excepción durante la configuración de los valores, se registra en el log.
     *
     * @param alumno El objeto Alumno del cual se obtendrán los valores para establecer en los campos de texto.
     */
    public void setAlumno(Alumno alumno) {
        try {
            lbIdAlumno.setText(Integer.toString(alumno.getId()));
            lbNombre.setText(alumno.getNombre());
            lbApellidos.setText(alumno.getApellido1() + " " + alumno.getApellido2());
            lbFecha.setText(alumno.getFechaNacimiento().format(formatter).toString());
            lbEdad.setText(Integer.toString(alumno.getEdad()));
            lbGenero.setText(alumno.getGenero().toString());
            lbTelefono.setText(Integer.toString(alumno.getTelefono()));
            lbEmail.setText(alumno.getEmail());

            lbCalle.setText(alumno.getDireccion().getCalle());
            lbNumero.setText(Integer.toString(alumno.getDireccion().getNumero()));
            lbCodigoPostal.setText(Integer.toString(alumno.getDireccion().getCodigoPostal()));
            lbLocalidad.setText(alumno.getDireccion().getLocalidad());
            lbProvincia.setText(alumno.getDireccion().getProvincia());

            lbEstado.setText(alumno.getEstado().toString());
            lbAsistencias.setText(Integer.toString(alumno.getAsistenciaSemanal()));
            lbFormaPago.setText(alumno.getFormaPago().toString());
        } catch (Exception e) {
            logUser.severe("Excepción: " + e.toString());
        }
    }
}
