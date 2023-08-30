package controlador;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class AcercaDeControlador implements Initializable {

    private double x, y;

    @FXML
    private AnchorPane apAcercaDe;

    @FXML
    private AnchorPane apIzquierda;

    @FXML
    private Button btnCancelar;

    @FXML
    private ImageView ivImagenFlor;

    @FXML
    private ImageView ivImagenLogo;

    @FXML
    private ImageView ivJasper;

    @FXML
    private ImageView ivJava;

    @FXML
    private ImageView ivJavaFX;

    @FXML
    private ImageView ivSqlCipher;

    @FXML
    private ImageView ivSqlLite;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Añadir clases de estilo CSS a elementos.
        btnCancelar.getStyleClass().add("boton_rojo");

        //Cargar imagenes en ImageView.
        Image imagenFlor;
        Image imagenLogo;
        Image imagenJava;
        Image imagenJavaFX;
        Image imagenSqlLite;
        Image imagenSqlCipher;
        Image imagenJasper;
        try {
            //Intentar cargar la imagen desde el recurso en el IDE y en el JAR.
            imagenFlor = new Image(getClass().getResourceAsStream("/recursos/logo_nuevo_134_159.png")); //Forma desde IDE y JAR.
            imagenLogo = new Image(getClass().getResourceAsStream("/recursos/lis_logo_1.png"));
            imagenJava = new Image(getClass().getResourceAsStream("/recursos/java_50.png"));
            imagenJavaFX = new Image(getClass().getResourceAsStream("/recursos/JavaFX_Logo_30.png"));
            imagenSqlLite = new Image(getClass().getResourceAsStream("/recursos/SQLite_40.png"));
            imagenSqlCipher = new Image(getClass().getResourceAsStream("/recursos/sqlcipher_50.png"));
            imagenJasper = new Image(getClass().getResourceAsStream("/recursos/jasper_50.png"));
        } catch (Exception e) {
            //Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR.
            imagenFlor = new Image("/recursos/logo_nuevo_134_159.png"); //Forma desde el JAR.
            imagenLogo = new Image("/recursos/lis_logo_1.png");
            imagenJava = new Image("/recursos/java_50.png");
            imagenJavaFX = new Image("/recursos/JavaFX_Logo_30.png");
            imagenSqlLite = new Image("/recursos/SQLite_40.png");
            imagenSqlCipher = new Image("/recursos/sqlcipher_50.png");
            imagenJasper = new Image("/recursos/jasper_50.png");
        }
        //Establecer las imagenes cargadas en los ImageView.
        ivImagenFlor.setImage(imagenFlor);
        ivImagenLogo.setImage(imagenLogo);
        ivJava.setImage(imagenJava);
        ivJavaFX.setImage(imagenJavaFX);
        ivSqlLite.setImage(imagenSqlLite);
        ivSqlCipher.setImage(imagenSqlCipher);
        ivJasper.setImage(imagenJasper);

        //Configurar el evento cuando se presiona el ratón en el panel apCardAlumno.
        apAcercaDe.setOnMousePressed(mouseEvent -> {
            //Obtener las coordenadas X e Y del ratón en relación con la escena.
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });

        //Configurar el evento cuando se arrastra el ratón en el panel apCardAlumno.
        apAcercaDe.setOnMouseDragged(mouseEvent -> {
            //Obtener la referencia al Stage actual y establecer las nuevas coordenadas X e Y.
            ((Stage) apAcercaDe.getScene().getWindow()).setX(mouseEvent.getScreenX() - x);
            ((Stage) apAcercaDe.getScene().getWindow()).setY(mouseEvent.getScreenY() - y);
        });

        //Configurar un evento de clic del ratón para el botón "Cerrar".
        btnCancelar.setOnMouseClicked(e -> {
            ((Stage) apAcercaDe.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
        });

    }
}
