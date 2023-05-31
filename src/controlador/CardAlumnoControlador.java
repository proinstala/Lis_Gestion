package controlador;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import modelo.Alumno;


public class CardAlumnoControlador implements Initializable {
    
    private DateTimeFormatter formatter;
    private Stage escenario;
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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        apCardAlumno.getStyleClass().add("fondo_ventana_degradado_toRight");
        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");//Formato dd/MM/yy

        apCardAlumno.setOnMousePressed(mouseEvent -> {
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });

        apCardAlumno.setOnMouseDragged(mouseEvent -> {
            escenario.setX(mouseEvent.getScreenX() - x);
            escenario.setY(mouseEvent.getScreenY() - y);
        });
        /*
         
        apRegistroUsuario.setOnMousePressed(mouseEvent -> {
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });

        apRegistroUsuario.setOnMouseDragged(mouseEvent -> {
            escenario.setX(mouseEvent.getScreenX() - x);
            escenario.setY(mouseEvent.getScreenY() - y);
        });
         */
    }

    @FXML
    void cerrarVentana(MouseEvent event) {
        escenario.close();
    }


    public void setAlumno(Alumno alumno) {

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
