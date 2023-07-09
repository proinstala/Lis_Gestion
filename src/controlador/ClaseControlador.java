package controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import baseDatos.ConexionBD;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modelo.Alumno;
import modelo.Clase;
import modelo.HoraClase;
import modelo.Jornada;
import modelo.TipoClase;
import modelo.Toast;
import utilidades.Constants;

public class ClaseControlador implements Initializable {

	private PrincipalControlador controladorPincipal;
	private ObservableList<Alumno> listadoAlumnos;
	private ObservableList<Alumno> listaClase;
	private int numeroClase;
	private DateTimeFormatter formatter;
	private Clase claseOriginal;
	private Clase clase;
	private ConexionBD conexionBD;
	private Logger logUser;
	private Jornada jornada;
	private FilteredList<Alumno> filtro;
	private Toast toast;
	private Alert alerta;
	

	@FXML
    private BorderPane bpClase;

	@FXML
    private TextField tfBusqueda;

	@FXML
	private ComboBox<HoraClase> cbHora;

	@FXML
	private ComboBox<TipoClase> cbTipo;

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
	private ImageView ivLupa;
	
	@FXML
	private ImageView ivFlechaDerecha;

	@FXML
	private ImageView ivFlechaIzquierda;

	@FXML
	private ImageView ivGuardar;

	@FXML
	private ImageView ivVolver;
	
	@FXML
    private ImageView ivFlechaAdd;
	
	@FXML
    private ImageView ivFlechaQuitar;

	@FXML
	private Label lbFecha;
	
	@FXML
    private Label lbDiaSemana;

	@FXML
	private Label lbNumeroClase;

	@FXML
    private Label lbIdClase;
	
	@FXML
    private TextArea taAnotaciones;

	@FXML
	private ListView<Alumno> lvClase;

	@FXML
	private TableView<Alumno> tvAlumnos;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Cargar imagenes en ImageView.
        Image imagenFlecha;
        Image imagenFlechaAdd;
        Image ImagenVolver;
        Image ImagenGuardar;
        Image ImagenLupa;
        try {
			//Intentar cargar la imagen desde el recurso en el IDE y en el JAR.
			imagenFlecha = new Image(getClass().getResourceAsStream("/recursos/flecha_derecha_2.png")); //Forma desde IDE y JAR.
        	imagenFlechaAdd = new Image(getClass().getResourceAsStream("/recursos/flecha_derecha_1.png"));
        	ImagenVolver = new Image(getClass().getResourceAsStream("/recursos/flceha_recarga_1.png"));
        	ImagenGuardar = new Image(getClass().getResourceAsStream("/recursos/floppy_lila_1_128.png"));
        	ImagenLupa = new Image(getClass().getResourceAsStream("/recursos/lupa_lila_2_128.png"));
        } catch (Exception e) {
			//Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR.
        	imagenFlecha = new Image("/recursos/flecha_derecha_2.png"); //Forma desde el JAR.
        	imagenFlechaAdd = new Image("/recursos/flecha_derecha_1.png");
        	ImagenVolver = new Image("/recursos/flceha_recarga_1.png");
        	ImagenGuardar = new Image("/recursos/floppy_lila_1_128.png");
        	ImagenLupa = new Image("/recursos/upa_lila_2_128.png");
        }
		//Establecer la imagenes cargadas en los ImageView.
        ivFlechaIzquierda.setImage(imagenFlecha);
        ivFlechaDerecha.setImage(imagenFlecha);
        ivFlechaAdd.setImage(imagenFlechaAdd);
        ivFlechaQuitar.setImage(imagenFlechaAdd);
        ivVolver.setImage(ImagenVolver);
        ivGuardar.setImage(ImagenGuardar);
        ivLupa.setImage(ImagenLupa);
		
		toast = new Toast();
		conexionBD = ConexionBD.getInstance();		//Obtener una instancia de la clase ConexionBD utilizando el patrón Singleton.
		logUser = Logger.getLogger(Constants.USER); //Crea una instancia de la clase Logger asociada al nombre de registro.

		//Cargo en los comboBox los dataos.
		cbHora.setItems(FXCollections.observableArrayList(HoraClase.values()));
		cbTipo.setItems(FXCollections.observableArrayList(TipoClase.values()));
		
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

	
	@FXML
	void anteriorClase(MouseEvent event) {
		if(numeroClase > 0) {
			numeroClase --;
			cargarClase(numeroClase); //llama a la funcion cargarClase pasandole el numero de la clase que tiene que cargar.
			
		} else {
			toast.show((Stage) bpClase.getScene().getWindow(), "No puedes retroceder más.\n" + "La Clase 1 es la primera.");
		}
	}

	
	@FXML
	void siguienteClase(MouseEvent event) {
		if(numeroClase < 7) {
			numeroClase ++;
			cargarClase(numeroClase); //llama a la funcion cargarClase pasandole el numero de la clase que tiene que cargar.
			
		} else {
			toast.show((Stage) bpClase.getScene().getWindow(), "No hay siguiente clase.\n" + "La Clase 8 es la ultima.");
		}
	}
	
	
	@FXML
	void addAlumno(MouseEvent event) {
		int i = tvAlumnos.getSelectionModel().getSelectedIndex(); //Guardo el indice del elemento seleccionado en la lista.
		
		if(i != -1){
			Alumno alumno = tvAlumnos.getSelectionModel().getSelectedItem(); //Obtengo el alumno seleccionado.
			
			int numeroIncripciones = -1;
			try {
				numeroIncripciones = conexionBD.numeroClasesInscrito(alumno.getId(), jornada);	 
			} catch (SQLException e) {
				logUser.severe("Excepción: " + e.toString());
				e.printStackTrace();
			} catch (Exception e) {
				logUser.severe("Excepción: " + e.toString());
			}


			if(listaClase.contains(alumno)) {
				toast.show((Stage) bpClase.getScene().getWindow(), "El alumno ya esta inscrito en esta Clase.");
			} else if(jornada.alumnoEnJornada(alumno) != -1){
				toast.show((Stage) bpClase.getScene().getWindow(), "El alumno ya esta inscrito en una Clase de esta Jornada.\n" + "Inscrito en Clase " + jornada.alumnoEnJornada(alumno) + ".");
			} else if(numeroIncripciones >= alumno.getAsistenciaSemanal()) {
				alerta = new Alert(AlertType.CONFIRMATION);
				alerta.getDialogPane().getStylesheets()
						.add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
				alerta.setTitle("Control asistencias");
				alerta.setHeaderText("Este Alumno ya esta inscrito a su maximo de clases semanales.");
				alerta.setContentText("¿Quieres añadirlo a esta clase?");
				alerta.initStyle(StageStyle.DECORATED);
				alerta.initOwner((Stage) bpClase.getScene().getWindow());

				ButtonType buttonTypeCancel = new ButtonType("No", ButtonData.CANCEL_CLOSE);
				ButtonType buttonTypeConfirmar = new ButtonType("Si", ButtonData.YES);
				alerta.getButtonTypes().setAll(buttonTypeConfirmar, buttonTypeCancel);
				Optional<ButtonType> result = alerta.showAndWait();

				// Si pulsamos el boton confirmar:
				if (result.get() == buttonTypeConfirmar) {
					listaClase.add(alumno); // Añado el alumno a la lista de clase
					toast.show((Stage) bpClase.getScene().getWindow(),
							"Alumno añadido a Clase " + clase.getNumero() + ".");
				}
				
			} else {
				listaClase.add(alumno); //Añado el alumno a la lista de clase
				toast.show((Stage) bpClase.getScene().getWindow(), "Alumno añadido a Clase " + clase.getNumero() + ".");
			}
		}
	}

	
	@FXML
	void quitarAlumno(MouseEvent event) {
		int i = lvClase.getSelectionModel().getSelectedIndex();
		
		if(i != -1) {
			Alumno alumno = lvClase.getSelectionModel().getSelectedItem();
			listaClase.remove(alumno);
			toast.show((Stage) bpClase.getScene().getWindow(), "Alumno eliminado de Clase " + clase.getNumero() + ".");
		}
	}


	@FXML
	void guardar(MouseEvent event) {
		try {
			boolean actualizar = false;
			if(claseOriginal.getHoraClase() != clase.getHoraClase() || claseOriginal.getTipo() != clase.getTipo() || claseOriginal.getAnotaciones() != clase.getAnotaciones()) {
				actualizar = true;
			}

			if(actualizar) {
				conexionBD.actualizarClase(clase);
			}
			
			conexionBD.actualizarAlumnosEnClase(new ArrayList<Alumno>(listaClase), clase.getId());

			claseOriginal.setHoraClase(clase.getHoraClase());
			claseOriginal.setTipo(clase.getTipo());
			claseOriginal.setAnotaciones(clase.getAnotaciones());
			claseOriginal.setListaAlumnos(new ArrayList<Alumno>(listaClase));
			toast.show((Stage) bpClase.getScene().getWindow(), "Cambios guardados en Clase " + clase.getNumero());
		} catch (SQLException e) {
			logUser.severe("Excepción: " + e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logUser.severe("Excepción: " + e.toString());
		}
	}

	@FXML
	void volver(MouseEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/jornadaVista.fxml"));
			BorderPane jornadaPilates;
			jornadaPilates = (BorderPane) loader.load();
			controladorPincipal.setPane(jornadaPilates);

			JornadaControlador controller = loader.getController(); //cargo el controlador.
			controller.setControladorPrincipal(controladorPincipal);
			controller.setListaAlumnos(listadoAlumnos);
			//controller.setStage((Stage) bpClase.getScene().getWindow());
			controller.inicializacion(jornada);
			
		} catch (IOException e) {
			logUser.severe("Excepción: " + e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logUser.severe("Excepción: " + e.toString());
		}
	}
	
	
	/**
	 * Establece la jornada de donde se obtienen las clases.
	 * 
	 * @param jornada Jornada que contiene las clases.
	 */
	public void setJornada(Jornada jornada) {
		this.jornada = jornada;
	}
	
	
	/**
	 * Caraga los datos de una clase en la ventana.
	 * El rango de clases es de 0 a 7.
	 * 
	 * @param numeroClase Numero de la clase que se tiene que cargar.
	 */
	private void cargarClase(int numeroClase) {
		claseOriginal = jornada.getClase(numeroClase);
		clase = new Clase(claseOriginal);
		clase.setListaAlumnos(comprobarListaClase(clase.getListaAlumnos()));

		listaClase = FXCollections.observableArrayList(clase.getListaAlumnos());
		lvClase.setItems(listaClase);
		
		lbNumeroClase.setText(Integer.toString(clase.getNumero()));
		lbIdClase.setText(Integer.toString(clase.getId()));
		cbHora.setValue(clase.getHoraClase());
		cbTipo.setValue(clase.getTipo());
		
		taAnotaciones.textProperty().bindBidirectional(clase.anotacionesProperty());
		
		cbHora.getSelectionModel().selectedItemProperty().addListener( (o, nv, ov) -> {
			this.clase.horaClaseProperty().set(ov);
		});
		
		cbTipo.getSelectionModel().selectedItemProperty().addListener( (o, nv, ov) -> {
			this.clase.tipoProperty().set(ov);
		});
		
		
	}


	private ArrayList<Alumno> comprobarListaClase(ArrayList<Alumno> listaAlumnos) {
		ArrayList<Alumno> nuevaListaAlumnos = new ArrayList<Alumno>();
		//listadoAlumnos
		for(Alumno alumno : listaAlumnos) {
			for(Alumno alumnoApp : listadoAlumnos) {
				if(alumno.getId() == alumnoApp.getId()) {
					nuevaListaAlumnos.add(alumnoApp);
					break;
				}
			}
		}
		return nuevaListaAlumnos;
	}
	

	/**
	 * Establece la clase inicial para cargar los datos.
	 * El rango de clases es de 0 a 7.
	 * 
	 * @param numeroClase Numero de la clase para cargar los datos.
	 */
	public void setClaseIniciacion(int numeroClase) {
		this.numeroClase = numeroClase;
		lbDiaSemana.setText(jornada.obtenerDiaSemana());
		formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");//Formato dd/MM/yy
		lbFecha.setText(jornada.getFecha().format(formatter));
		cargarClase(numeroClase); //llama a la funcion cargarClase pasandole el numero de la clase que tiene que cargar.
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
		listadoAlumnos = FXCollections.observableArrayList(lista); //Guado la lista pasada a la lista de Clasecontrolador.
		//tvAlumnos.setItems(listadoAlumnos);
		filtro = new FilteredList<Alumno>(listadoAlumnos); //Inicio el filtro pasandole el listado de alumnos.
		tvAlumnos.setItems(filtro); //Añado la lista de alumnos TextView tvAlumnos.
		
	}
}
