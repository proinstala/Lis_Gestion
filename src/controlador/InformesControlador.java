package controlador;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modelo.Alumno;
import modelo.Mensualidad;
import modelo.Usuario;
import utilidades.Constants;

public class InformesControlador implements Initializable {

    private Logger logUser;
    private Usuario usuario;
    private ObservableList<Mensualidad> listadoMensualidadesGeneral;
    private ObservableList<Alumno> listadoAlumnosGeneral;

    @FXML
    private Button btnAlumnoClases;

    @FXML
    private Button btnAlumnoMensualidad;

    @FXML
    private Button btnClases;

    @FXML
    private Button btnJornadas;

    @FXML
    private Button btnMensualidades;

    @FXML
    private GridPane gpInformes;

    @FXML
    private ImageView ivAlumnoClases;

    @FXML
    private ImageView ivAlumnoMensualidad;

    @FXML
    private ImageView ivAlumnos;

    @FXML
    private ImageView ivClases;

    @FXML
    private ImageView ivJornadas;

    @FXML
    private ImageView ivMensualidades;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Cargar imagenes en ImageView.
        Image imagenClase;
        Image imagenJornada;
        Image imagenMensualidad;
        Image imagenAlumno;
        Image imagenAlumnosMensualidad;
        Image AlumnosClase;
        try {
            //Intentar cargar la imagen desde el recurso en el IDE y en el JAR.
            imagenClase = new Image(getClass().getResourceAsStream("/recursos/deporte_1_96.png")); //Forma desde IDE y JAR.
            imagenJornada = new Image(getClass().getResourceAsStream("/recursos/calendar_2_72.png"));
            imagenMensualidad = new Image(getClass().getResourceAsStream("/recursos/informe_1_72.png"));
            imagenAlumno = new Image(getClass().getResourceAsStream("/recursos/id_1_72.png"));
            imagenAlumnosMensualidad = new Image(getClass().getResourceAsStream("/recursos/informe_3_128.png"));
            AlumnosClase = new Image(getClass().getResourceAsStream("/recursos/informe_2_72.png"));
        } catch (Exception e) {
            //Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR.
            imagenClase = new Image("/recursos/deporte_1_96.png"); //Forma desde el JAR.
            imagenJornada = new Image("/recursos/calendar_2_72.png");
            imagenMensualidad = new Image("/recursos/informe_1_72.png");
            imagenAlumno = new Image("/recursos/id_1_72.png");
            imagenAlumnosMensualidad = new Image("/recursos/informe_3_128.png");
            AlumnosClase = new Image("/recursos/informe_2_72.png");
        }
        //Establecer las imagenes cargadas en los ImageView.
        ivClases.setImage(imagenClase);
        ivJornadas.setImage(imagenJornada);
        ivMensualidades.setImage(imagenMensualidad);
        ivAlumnos.setImage(imagenAlumno);
        ivAlumnoMensualidad.setImage(imagenAlumnosMensualidad);
        ivAlumnoClases.setImage(AlumnosClase);

        logUser = Logger.getLogger(Constants.USER); //Crea una instancia de la clase Logger asociada al nombre de registro.

        //Configurar un evento de clic del ratón para el botón "Abrir" de Informe Clases.
        btnClases.setOnMouseClicked(e -> {
            abrirFormClase();
        });

        btnJornadas.setOnMouseClicked(e -> {
            abrirFormJornada();
        });

        btnMensualidades.setOnMouseClicked(e -> {
            abrirFormMensualidades();
        });
    }


    private void abrirFormClase() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/informeFormClaseVista.fxml"));
            GridPane formInforme;
            formInforme = (GridPane) loader.load();
            InformeFormClaseControlador controller = loader.getController(); // cargo el controlador.

            Stage ventana = new Stage();
            ventana.initOwner((Stage) gpInformes.getScene().getWindow());
            ventana.initModality(Modality.APPLICATION_MODAL); // modalida para bloquear las ventanas de detras.
            ventana.initStyle(StageStyle.DECORATED);

            URL rutaIcono = getClass().getResource("/recursos/lis_logo_1.png"); // guardar ruta de recurso imagen.
            ventana.getIcons().add(new Image(rutaIcono.toString())); // poner imagen icono a la ventana.

            controller.setUsuario(usuario);

            Scene scene = new Scene(formInforme);
            scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); // Añade hoja de estilos.
            ventana.setScene(scene);
            ventana.setTitle("Nuevo Informe");
            ventana.showAndWait();

        } catch (IOException e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        }
    }


    private void abrirFormJornada() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/informeFormJornadaVista.fxml"));
            GridPane formInforme;
            formInforme = (GridPane) loader.load();
            InformeFormJornadaControlador controller = loader.getController(); // cargo el controlador.

            Stage ventana = new Stage();
            ventana.initOwner((Stage) gpInformes.getScene().getWindow());
            ventana.initModality(Modality.APPLICATION_MODAL); // modalida para bloquear las ventanas de detras.
            ventana.initStyle(StageStyle.DECORATED);

            URL rutaIcono = getClass().getResource("/recursos/lis_logo_1.png"); // guardar ruta de recurso imagen.
            ventana.getIcons().add(new Image(rutaIcono.toString())); // poner imagen icono a la ventana.

            controller.setUsuario(usuario);

            Scene scene = new Scene(formInforme);
            scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); // Añade hoja de estilos.
            ventana.setScene(scene);
            ventana.setTitle("Nuevo Informe");
            ventana.showAndWait();

        } catch (IOException e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        }
    }

    private void abrirFormMensualidades() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/informeFormMensualidadesVista.fxml"));
            GridPane formInforme;
            formInforme = (GridPane) loader.load();
            InformeFormMensualidadesControlador controller = loader.getController(); // cargo el controlador.

            Stage ventana = new Stage();
            ventana.initOwner((Stage) gpInformes.getScene().getWindow());
            ventana.initModality(Modality.APPLICATION_MODAL); // modalida para bloquear las ventanas de detras.
            ventana.initStyle(StageStyle.DECORATED);

            URL rutaIcono = getClass().getResource("/recursos/lis_logo_1.png"); // guardar ruta de recurso imagen.
            ventana.getIcons().add(new Image(rutaIcono.toString())); // poner imagen icono a la ventana.

            controller.setUsuario(usuario);
            controller.setListaMensualidades(listadoMensualidadesGeneral);
            controller.setListaAlumnos(listadoAlumnosGeneral);

            Scene scene = new Scene(formInforme);
            scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); // Añade hoja de estilos.
            ventana.setScene(scene);
            ventana.setTitle("Nuevo Informe");
            ventana.showAndWait();

        } catch (IOException e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        }
    }


    /**
	 * Etablece el usuario que esta usando la aplicación.
     * 
	 * @param usuarioActual el usuario a establecer.
	 */
	public void setUsuarioActual(Usuario usuarioActual) {
		this.usuario = usuarioActual;
	}


    /**
     * Establece la lista de mensualidades para mostrar en la tabla.
     * Configura el filtro, los eventos de selección y los datos de la tabla.
     * 
     * @param lista La lista de mensualidades a mostrar.
     */
    public void setListaMensualidades(ObservableList<Mensualidad> lista) {
        listadoMensualidadesGeneral = lista; //Guarda la lista pasada a la lista de Clasecontrolador.
    }


    /**
     * Establece la lista de alumnos en el controlador.
     *
     * @param lista La lista de alumnos a establecer.
     */
    public void setListaAlumnos(ObservableList<Alumno> lista) {
        listadoAlumnosGeneral = lista;
	}
    
}
