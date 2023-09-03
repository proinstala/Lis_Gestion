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
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
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
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import modelo.Alumno;
import modelo.Clase;
import modelo.HoraClase;
import modelo.Jornada;
import modelo.TipoClase;
import utilidades.Constants;
import utilidades.Fechas;
import utilidades.Toast;


public class ClaseControlador implements Initializable {

	IntegerBinding totalAlumnos;
	private PrincipalControlador controladorPincipal;
	private ObservableList<Alumno> listadoAlumnosGeneral;
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
	private Double tiempoDelay = 0.5;
	

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
    private Label lbNumeroAlumnos;
	
	@FXML
    private TextArea taAnotaciones;

	@FXML
	private ListView<Alumno> lvClase;

	@FXML
	private TableView<Alumno> tvAlumnos;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Añadir clases de estilo CSS a elementos.
        lvClase.getStyleClass().add("mi_list-view");

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
        	ImagenVolver = new Image(getClass().getResourceAsStream("/recursos/salir_42px.png"));
        	ImagenGuardar = new Image(getClass().getResourceAsStream("/recursos/guardar_42px.png"));
        	ImagenLupa = new Image(getClass().getResourceAsStream("/recursos/lupa_lila_2_48.png"));
        } catch (Exception e) {
			//Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR.
        	imagenFlecha = new Image("/recursos/flecha_derecha_2.png"); //Forma desde el JAR.
        	imagenFlechaAdd = new Image("/recursos/flecha_derecha_1.png");
        	ImagenVolver = new Image("/recursos/salir_42px.png");
        	ImagenGuardar = new Image("/recursos/guardar_42px.png");
        	ImagenLupa = new Image("/recursos/lupa_lila_2_48.png");
        }
		//Establecer la imagenes cargadas en los ImageView.
        ivFlechaIzquierda.setImage(imagenFlecha);
        ivFlechaDerecha.setImage(imagenFlecha);
        ivFlechaAdd.setImage(imagenFlechaAdd);
        ivFlechaQuitar.setImage(imagenFlechaAdd);
        ivVolver.setImage(ImagenVolver);
        ivGuardar.setImage(ImagenGuardar);
        ivLupa.setImage(ImagenLupa);

		//Crear Tooltip.
        Tooltip tltFlechaIzquierda = new Tooltip("Clase anterior");
		Tooltip tltFlechaDerecha = new Tooltip("Siguiente Clase");
		Tooltip tltFlechaAdd = new Tooltip("Añadir Alumno");
		Tooltip tltFlechaQuitar = new Tooltip("Quitar Alumno");
		Tooltip tltVolver = new Tooltip("Volver a Jornada");
		Tooltip tltGuardar = new Tooltip("Guardar Cambios");

        tltFlechaIzquierda.setShowDelay(Duration.seconds(tiempoDelay)); //Establecer retardo de aparición.
		tltFlechaDerecha.setShowDelay(Duration.seconds(tiempoDelay));
		tltFlechaAdd.setShowDelay(Duration.seconds(tiempoDelay)); 
		tltFlechaQuitar.setShowDelay(Duration.seconds(tiempoDelay)); 
		tltVolver.setShowDelay(Duration.seconds(tiempoDelay)); 
		tltGuardar.setShowDelay(Duration.seconds(tiempoDelay));  

		Tooltip.install(ivFlechaIzquierda, tltFlechaIzquierda); //Establecer Tooltip a ImageView.
		Tooltip.install(ivFlechaDerecha, tltFlechaDerecha);
		Tooltip.install(ivFlechaAdd, tltFlechaAdd);
		Tooltip.install(ivFlechaQuitar, tltFlechaQuitar);
		Tooltip.install(ivVolver, tltVolver);
		Tooltip.install(ivGuardar, tltGuardar);
		
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


	/**
	 * Maneja el evento de hacer clic en el botón "Anterior Clase".
	 * Se encarga de cargar la clase anterior en la interfaz si hay una disponible.
	 * Si no hay una clase anterior, muestra un mensaje de aviso.
	 *
	 * @param event El evento de clic del mouse.
	 */
	@FXML
	void anteriorClase(MouseEvent event) {
		if(numeroClase > 0) {
			numeroClase --;
			cargarClase(numeroClase); //llama a la funcion cargarClase pasandole el numero de la clase que tiene que cargar.
			
		} else {
			toast.show((Stage) bpClase.getScene().getWindow(), "No puedes retroceder más.\n" + "La Clase 1 es la primera.");
		}
	}


	/**
	 * Maneja el evento de hacer clic en el botón "Siguiente Clase".
	 * Se encarga de cargar la clase Siguiente en la interfaz si hay una disponible.
	 * Si no hay una clase siguiente, muestra un mensaje de aviso.
	 *
	 * @param event El evento de clic del mouse.
	 */
	@FXML
	void siguienteClase(MouseEvent event) {
		if(numeroClase < 7) {
			numeroClase ++;
			cargarClase(numeroClase); //llama a la funcion cargarClase pasandole el numero de la clase que tiene que cargar.
			
		} else {
			toast.show((Stage) bpClase.getScene().getWindow(), "No hay siguiente clase.\n" + "La Clase 8 es la ultima.");
		}
	}
	
	
	/**
	 * Maneja el evento de hacer clic en el botón "Add Alumno".
	 * Se encarga de llamar al metodo agregarAlumno() para agregar un alumno a la clase seleccionada si cumple ciertas condiciones.
	 * Muestra mensajes de dialogo en caso de que el alumno ya esté inscrito en la clase, en otra clase de la misma jornada
	 * o haya alcanzado su máximo de clases semanales.
	 *
	 * @param event El evento de clic del mouse.
	 */
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
				e.printStackTrace();
			}


			if(listaClase.contains(alumno)) {
				toast.show((Stage) bpClase.getScene().getWindow(), "El alumno ya esta inscrito en esta Clase.");
			
			} else if(jornada.alumnoEnJornada(alumno) != -1){
				alerta = new Alert(AlertType.CONFIRMATION);
				alerta.getDialogPane().getStylesheets()
						.add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
				alerta.setTitle("Control asistencias");

				if (numeroIncripciones >= alumno.getAsistenciaSemanal()) {
					alerta.setHeaderText("Este Alumno ya esta inscrito en una o varias clase de esta Jornada\n" +
							"y ademas supera su número máximo de aisitencia semanal.");
				} else {
					alerta.setHeaderText("Este Alumno ya esta inscrito en una clase de esta Jornada.\n" +
							"Inscrito en Clase " + jornada.alumnoEnJornada(alumno) + ".");
				}

				alerta.setContentText("¿Quieres añadirlo a esta clase?");
				alerta.initStyle(StageStyle.DECORATED);
				alerta.initOwner((Stage) bpClase.getScene().getWindow());

				ButtonType buttonTypeCancel = new ButtonType("No", ButtonData.CANCEL_CLOSE);
				ButtonType buttonTypeConfirmar = new ButtonType("Si", ButtonData.YES);
				alerta.getButtonTypes().setAll(buttonTypeConfirmar, buttonTypeCancel);
				Optional<ButtonType> result = alerta.showAndWait();

				// Si pulsamos el boton confirmar:
				if (result.get() == buttonTypeConfirmar) {
					agregarAlumno(alumno);
				}

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
					agregarAlumno(alumno);
				}
				
			} else {
				agregarAlumno(alumno);
			}
		}
	}


	/**
	 * Maneja el evento de hacer clic en el botón "Quitar Alumno".
	 * Se encarga de eliminar un alumno de la lista de Alumnos de la clase.
	 * Muestra un mensaje de aviso cuando se elimina el Alumnos de la clase.
	 *
	 * @param event El evento de clic del mouse.
	 */
	@FXML
	void quitarAlumno(MouseEvent event) {
		int i = lvClase.getSelectionModel().getSelectedIndex();
		
		if(i != -1) {
			Alumno alumno = lvClase.getSelectionModel().getSelectedItem();
			listaClase.remove(alumno);
			toast.show((Stage) bpClase.getScene().getWindow(), "Alumno eliminado de Clase " + clase.getNumero() + ".");
		}
	}


	/**
	 * Maneja el evento de hacer clic en el botón "Guardar".
	 * Se encarga de guardar los cambios realizados en la clase y en la lista de alumnos de la clase.
	 * Actualiza la base de datos con la información actualizada.
	 * Muestra un mensaje de confirmación cuando se guardan los cambios.
	 *
	 * @param event El evento de clic del mouse.
	 */
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
			e.printStackTrace();
		}
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
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/jornadaVista.fxml"));
			BorderPane jornadaPilates;
			jornadaPilates = (BorderPane) loader.load();
			controladorPincipal.setPane(jornadaPilates);

			JornadaControlador controller = loader.getController(); //cargo el controlador.
			controller.setControladorPrincipal(controladorPincipal);
			controller.setListaAlumnos(listadoAlumnosGeneral);
			controller.inicializacion(jornada);
			
		} catch (IOException e) {
			logUser.severe("Excepción: " + e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logUser.severe("Excepción: " + e.toString());
			e.printStackTrace();
		}
	}


	/**
	 * Agrega un alumno a la lista de alumnos de la clase actual.
	 * Muestra un mensaje para notificar el exito o fracaso de agregar el alumno a clase.
	 * 
	 * @param alumno Alumno que se agrega a la lista de alumnos de la clase actual.
	 */
	private void agregarAlumno(Alumno alumno) {
		// Añade el alumno a la lista de clase.
		if(listaClase.add(alumno)) {
			//Muestra una notificación de confirmación de Alumno añadido a clase.
			toast.show((Stage) bpClase.getScene().getWindow(),
				"Alumno añadido a Clase " + clase.getNumero() + ".");
		} else {
			//Muestra una notificación de error de Alumno añadido a clase.
			toast.show((Stage) bpClase.getScene().getWindow(),
				"Fallo al agregar Alumno a Clase " + clase.getNumero() + ".");
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

		lbNumeroAlumnos.textProperty().bind(Bindings.createStringBinding(() -> String.format("%d", listaClase.size()), listaClase));
	}


	/**
	 * Comprueba la lista de alumnos en la clase y devuelve una nueva lista actualizada.
	 * Recibe una lista de alumnos y verifica cada alumno en comparación con la lista general de alumnos.
	 * Si encuentra una coincidencia de ID, agrega el alumno correspondiente de la lista general a la nueva lista.
	 * Devuelve la nueva lista actualizada.
	 *
	 * @param listaAlumnos La lista de alumnos en la clase.
	 * @return La nueva lista de alumnos actualizada.
	 */
	private ArrayList<Alumno> comprobarListaClase(ArrayList<Alumno> listaAlumnos) {
		ArrayList<Alumno> nuevaListaAlumnos = new ArrayList<Alumno>();
		//listadoAlumnos
		for(Alumno alumno : listaAlumnos) {
			for(Alumno alumnoApp : listadoAlumnosGeneral) {
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
		lbDiaSemana.setText(Fechas.obtenerDiaSemana(jornada.getFecha()));
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
		listadoAlumnosGeneral = lista;
		filtro = new FilteredList<Alumno>(listadoAlumnosGeneral); //Inicio el filtro pasandole el listado de alumnos.
		tvAlumnos.setItems(filtro); //Añado la lista de alumnos TextView tvAlumnos.
	}
}
