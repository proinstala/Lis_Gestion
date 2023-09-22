package controlador;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.awt.Desktop;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import utilidades.Constants;
import utilidades.Toast;


public class AcercaDeControlador implements Initializable {

    private double x, y;
    private Toast toast;

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

    @FXML
    private Label lbGitHub;

    @FXML
    private Label lbCorreo;

    @FXML
    private Label lbVersion;


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

        toast = new Toast();

        configurarTextos();

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

        //Abre la el navegador web predeterminado con la pagina de GitHub del proyecto.
        lbGitHub.setOnMouseClicked(e -> {
            try {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + Constants.URL_REPOSITORIO);
            } catch (IOException ex) {
                toast.show((Stage) apAcercaDe.getScene().getWindow(), "Fallo al intentar abrir el navegador predeterminado.");
            }
        });

        //Abre el cliente de correo electronico predeterminado poniendo como destinatario el email de contacto del autor del proyecto.
        lbCorreo.setOnMouseClicked(e -> {
            // Verifica si el sistema admite Desktop API
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();

                // Verifica si la acción de correo electrónico es compatible
                if (desktop.isSupported(Desktop.Action.MAIL)) {
                    try {
                        URI mailtoUri = new URI("mailto:" + Constants.CORREO_AUTOR); // Crea un objeto URI con el formato "mailto:"
                        desktop.mail(mailtoUri); // Abre el cliente de correo predeterminado
                    } catch (IOException | URISyntaxException ex) {
                        toast.show((Stage) apAcercaDe.getScene().getWindow(),"Fallo al inentar abrir el cliente de correo predeterminado.");
                    }
                } else {
                    toast.show((Stage) apAcercaDe.getScene().getWindow(), "La acción de correo electrónico no es compatible en este sistema.");
                }
            } else {
                toast.show((Stage) apAcercaDe.getScene().getWindow(),"La API Desktop no está disponible en este sistema.");
            }
        });

    }

    /**
     * Configura los textos de los labels con la informacion de la aplicación.
     */
    private void configurarTextos() {
        lbCorreo.setText(Constants.CORREO_AUTOR);
        lbGitHub.setText(Constants.URL_REPOSITORIO);
        lbVersion.setText(Constants.VERSION_LIS_GESTION);
    }
}
