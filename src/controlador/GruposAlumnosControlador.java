package controlador;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import baseDatos.ConexionBD;
import javafx.fxml.Initializable;
import javafx.beans.binding.IntegerBinding;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import modelo.Alumno;
import modelo.GrupoAlumnos;
import modelo.Usuario;
import utilidades.Constants;
import utilidades.Toast;

public class GruposAlumnosControlador implements Initializable {
    
	private PrincipalControlador controladorPincipal;

	private ObservableList<Alumno> listadoAlumnosGeneral;
    private ObservableList<GrupoAlumnos> listadoGruposAlumnosGeneral;
	private ObservableList<Alumno> listaAlumnosGrupo;
    private ObservableList<GrupoAlumnos> listaGrupos;

    private FilteredList<Alumno> filtro;
    private IntegerBinding totalAlumnosGrupo;

	private DateTimeFormatter formatter;
    private DecimalFormat decimalFormat;

	private ConexionBD conexionBD;
    private Usuario usuario;
	private Logger logUser;
	private Toast toast;
	private Alert alerta;
	
    private Double tiempoDelay = 0.5;
	private boolean checkChanges = false;
    

    @FXML
    private BorderPane bpGruposAlumnos;

    @FXML
	private TableColumn<Alumno, String> colApellido1;

	@FXML
	private TableColumn<Alumno, Number> colGenero;

	@FXML
	private TableColumn<Alumno, Number> colId;

	@FXML
	private TableColumn<Alumno, String> colNombre;

	@FXML
    private TableColumn<Alumno, Number> colAsistencias;

    @FXML
    private TableColumn<Alumno, String> colEdad;

    @FXML
    private TableColumn<Alumno, Number> colEstado;

    @FXML
    private ImageView ivAddGrupo;

    @FXML
    private ImageView ivBorrarGrupo;

    @FXML
    private ImageView ivEditarGrupo;

    @FXML
    private ImageView ivFlechaAdd;

    @FXML
    private ImageView ivFlechaQuitar;

    @FXML
    private ImageView ivGuardar;

    @FXML
    private ImageView ivLupa;

    @FXML
    private ImageView ivVolver;

    @FXML
    private Label lbDiaSemana;

    @FXML
    private Label lbNumeroAlumnosGrupo;

    @FXML
    private ListView<Alumno> lvAlumnosGrupo;

    @FXML
    private ListView<GrupoAlumnos> lvGrupos;

    @FXML
    private TextField tfBusqueda;

    @FXML
    private TableView<Alumno> tvAlumnos;
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Añadir clases de estilo CSS a elementos.
        lvGrupos.getStyleClass().add("mi_list-view");
        lvAlumnosGrupo.getStyleClass().add("mi_list-view");

        logUser = Logger.getLogger(Constants.USER); //Crea una instancia de la clase Logger asociada al nombre de registro.
        conexionBD = ConexionBD.getInstance();      //Obtener una instancia de la clase ConexionBD utilizando el patrón Singleton.
        toast = new Toast();

        decimalFormat = new DecimalFormat("#0.00");

        configurarBotonesImageView();
        configurarTabla(); 
    }


    private void configurarBotonesImageView() {
        //Cargar imagenes en ImageView.
        Image imagenFlechaAdd;
        Image ImagenVolver;
        Image ImagenGuardar;
        Image ImagenLupa;
        Image imagenAdd;
        Image imagenBorrar;
        Image imagenEditar;
        try {
			//Intentar cargar la imagen desde el recurso en el IDE y en el JAR.
			//Forma desde IDE y JAR.
        	imagenFlechaAdd = new Image(getClass().getResourceAsStream("/recursos/flecha_derecha_1.png"));
        	ImagenVolver = new Image(getClass().getResourceAsStream("/recursos/salir_42px.png"));
        	ImagenGuardar = new Image(getClass().getResourceAsStream("/recursos/guardar_42px.png"));
        	ImagenLupa = new Image(getClass().getResourceAsStream("/recursos/lupa_lila_2_48.png"));
            imagenAdd = new Image(getClass().getResourceAsStream("/recursos/add-circle-linear_30.png"));
            imagenBorrar = new Image(getClass().getResourceAsStream("/recursos/close-circle-linear_30.png"));
            imagenEditar = new Image(getClass().getResourceAsStream("/recursos/pen-new-round-linear_30.png"));
        } catch (Exception e) {
			//Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR.
        	//Forma desde el JAR.
        	imagenFlechaAdd = new Image("/recursos/flecha_derecha_1.png");
        	ImagenVolver = new Image("/recursos/salir_42px.png");
        	ImagenGuardar = new Image("/recursos/guardar_42px.png");
        	ImagenLupa = new Image("/recursos/lupa_lila_2_48.png");
            imagenAdd = new Image("/recursos/add-circle-linear_30.png"); //Forma desde el JAR.
            imagenBorrar = new Image("/recursos/close-circle-linear_30.png");
            imagenEditar = new Image("/recursos/pen-new-round-linear_30.png");
        }
		//Establecer la imagenes cargadas en los ImageView.
        ivFlechaAdd.setImage(imagenFlechaAdd);
        ivFlechaQuitar.setImage(imagenFlechaAdd);
        ivVolver.setImage(ImagenVolver);
        ivGuardar.setImage(ImagenGuardar);
        ivLupa.setImage(ImagenLupa);
        ivAddGrupo.setImage(imagenAdd);
        ivBorrarGrupo.setImage(imagenBorrar);
        ivEditarGrupo.setImage(imagenEditar);

		//Crear Tooltip.
		Tooltip tltFlechaAdd = new Tooltip("Añadir Alumno");
		Tooltip tltFlechaQuitar = new Tooltip("Quitar Alumno");
		Tooltip tltVolver = new Tooltip("Volver a Jornada");
		Tooltip tltGuardar = new Tooltip("Guardar Cambios");
        Tooltip tltAddGrupo = new Tooltip("Añadir Nuevo Grupo");
		Tooltip tltBorrarGrupo = new Tooltip("Eliminar Grupo");
		Tooltip tltEditarGrupo = new Tooltip("Editar Grupo");

        //Establecer retardo de aparición.
		tltFlechaAdd.setShowDelay(Duration.seconds(tiempoDelay)); 
		tltFlechaQuitar.setShowDelay(Duration.seconds(tiempoDelay)); 
		tltVolver.setShowDelay(Duration.seconds(tiempoDelay)); 
		tltGuardar.setShowDelay(Duration.seconds(tiempoDelay));  
        tltAddGrupo.setShowDelay(Duration.seconds(tiempoDelay)); 
        tltBorrarGrupo.setShowDelay(Duration.seconds(tiempoDelay));
        tltEditarGrupo.setShowDelay(Duration.seconds(tiempoDelay));

		//Establecer Tooltip a ImageView.
		Tooltip.install(ivFlechaAdd, tltFlechaAdd);
		Tooltip.install(ivFlechaQuitar, tltFlechaQuitar);
		Tooltip.install(ivVolver, tltVolver);
		Tooltip.install(ivGuardar, tltGuardar);
        Tooltip.install(ivAddGrupo, tltAddGrupo); 
        Tooltip.install(ivBorrarGrupo, tltBorrarGrupo); 
        Tooltip.install(ivEditarGrupo, tltEditarGrupo); 
    }


    private void configurarTabla() {
        //Asigno a cada columna de la tabla los campos del modelo.
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido1.setCellValueFactory(new PropertyValueFactory<>("apellido1"));
        colAsistencias.setCellValueFactory(new PropertyValueFactory<>("asistenciaSemanal"));
        colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
		colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

		colEdad.setCellValueFactory(cellData -> {
        	//LocalDate fechaText = cellData.getValue().getFechaNacimiento();
        	//return cellData.getValue().fechaNacimientoProperty().asString(fechaText.format(formatter).toString());

            LocalDate fechaText = cellData.getValue().getFechaNacimiento();
            return cellData.getValue().fechaNacimientoProperty().asString(Integer.toString(Period.between(fechaText, LocalDate.now()).getYears()));
        });

        //Con esto la busqueda es automatica al insertar texto en el tfBusqueda.
        tfBusqueda.textProperty().addListener( (o, ov, nv) -> {
        	filtro.setPredicate(obj -> {
        		if (obj.getNombre().toLowerCase().contains(nv.toLowerCase())) {return true;}
        		else {return false;}
        	});
        });
    }

    /**
	 * Maneja el evento de hacer clic en el botón "Volver".
	 * Se encarga de cargar la vista de la jornada principal y establecerla como contenido principal.
	 * Configura el controlador de la vista de la jornada principal y le pasa la lista de alumnos y la jornada actual.
	 *
	 * @param event El evento de clic del mouse.
	 */
	@FXML
	void volver(MouseEvent event) {
		//Comprueba si hay cambios en la clase actual sin guardar para preguntar si se quiere guardar los cambios.
		// if (checkChanges && (cambiosConfigClase() || cambiosListaClase())) {
		// 	ButtonData typeAnswer = saveForgottenChanges();
		// 	//Si la respuesta es cancelar, se mantiene la vista de clase.
		// 	if (typeAnswer == ButtonData.CANCEL_CLOSE) {
		// 		return; //Termina la ejecución de este metodo.
		// 	}
		// }

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/alumnosVista.fxml"));
			BorderPane alumnos;
			alumnos = (BorderPane) loader.load();
			controladorPincipal.setPane(alumnos);

			AlumnosControlador controller = loader.getController(); //cargo el controlador.
			controller.setListaAlumnos(listadoAlumnosGeneral);
            controller.setUsuarioActual(usuario);
            controller.setControladorPrincipal(controladorPincipal);
			//controller.inicializacion(jornada);
			
		} catch (IOException e) {
			logUser.severe("Excepción: " + e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logUser.severe("Excepción: " + e.toString());
			e.printStackTrace();
		}
	}


    /**
	 * Establece para este controlador, el controlador principal de la aplicacion.
	 * 
	 * @param principal Controlador principal.
	 */
	public void setControladorPrincipal(PrincipalControlador principal) {
		controladorPincipal = principal;
	}


    /**
	 * Establece la lista de Alumnos.
	 * 
	 * @param lista La lista de donde se obtienen los Alumnos.
	 */
	public void setListaAlumnos(ObservableList<Alumno> lista) {
		listadoAlumnosGeneral = lista;
		filtro = new FilteredList<Alumno>(listadoAlumnosGeneral); //Inicio el filtro pasandole el listado de alumnos.
		tvAlumnos.setItems(filtro); //Añado la lista de alumnos TextView tvAlumnos.
	}


    /**
	 * Etablece el usuario que esta usando la aplicación.
     * 
	 * @param usuario
	 */
	public void setUsuarioActual(Usuario usuarioActual) {
		this.usuario = usuarioActual;
	}
}
