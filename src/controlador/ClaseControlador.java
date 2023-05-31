package controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import baseDatos.ConexionBD;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
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
import modelo.Alumno;
import modelo.Clase;
import modelo.HoraClase;
import modelo.Jornada;
import modelo.TipoClase;
import modelo.Toast;

public class ClaseControlador implements Initializable {

	private PrincipalControlador controladorPincipal;
	private int numeroClase;
	private DateTimeFormatter formatter;
	private Clase claseOriginal;
	private Clase clase;
	private ConexionBD conexionBD;
	private Jornada jornada;
	private FilteredList<Alumno> filtro;
	private Toast toast;
	
	@FXML
    private TextField tfBusqueda;

	@FXML
	private ComboBox<HoraClase> cbHora;

	@FXML
	private ComboBox<TipoClase> cbTipo;

	@FXML
	private TableColumn<String, String> colApellido1;

	@FXML
	private TableColumn<String, String> colApellido2;

	@FXML
	private TableColumn<String, Number> colGenero;

	@FXML
	private TableColumn<String, Number> colId;

	@FXML
	private TableColumn<String, String> colNombre;

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
    private TextArea taAnotaciones;

	@FXML
	private ListView<Alumno> lvClase;
	private ObservableList listaClase;

	@FXML
	private TableView<Alumno> tvAlumnos;
	private ObservableList<Alumno> listadoAlumnos;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Cargar imagenes en ImageView.
        Image imagenFlecha;
        Image imagenFlechaAdd;
        Image ImagenVolver;
        Image ImagenGuardar;
        Image ImagenLupa;
        try {
        	imagenFlecha = new Image("/recursos/flecha_derecha_2.png");
        	imagenFlechaAdd = new Image("/recursos/flecha_derecha_1.png");
        	ImagenVolver = new Image("/recursos/flceha_recarga_1.png");
        	ImagenGuardar = new Image("/recursos/floppy_lila_1_128.png");
        	ImagenLupa = new Image("/recursos/upa_lila_2_128.png");
        } catch (Exception e) {
        	imagenFlecha = new Image(getClass().getResourceAsStream("/recursos/flecha_derecha_2.png"));
        	imagenFlechaAdd = new Image(getClass().getResourceAsStream("/recursos/flecha_derecha_1.png"));
        	ImagenVolver = new Image(getClass().getResourceAsStream("/recursos/flceha_recarga_1.png"));
        	ImagenGuardar = new Image(getClass().getResourceAsStream("/recursos/floppy_lila_1_128.png"));
        	ImagenLupa = new Image(getClass().getResourceAsStream("/recursos/lupa_lila_2_128.png"));
        }
        ivFlechaIzquierda.setImage(imagenFlecha);
        ivFlechaDerecha.setImage(imagenFlecha);
        ivFlechaAdd.setImage(imagenFlechaAdd);
        ivFlechaQuitar.setImage(imagenFlechaAdd);
        ivVolver.setImage(ImagenVolver);
        ivGuardar.setImage(ImagenGuardar);
        ivLupa.setImage(ImagenLupa);
		
		toast = new Toast();
		conexionBD = ConexionBD.getInstance(); //Obtenemos una istancia de la Conexion a BD.

		//Cargo en los comboBox los dataos.
		cbHora.setItems(FXCollections.observableArrayList(HoraClase.values()));
		cbTipo.setItems(FXCollections.observableArrayList(TipoClase.values()));
		
		//Asigno a cada columna de la tabla los campos del modelo.
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido1.setCellValueFactory(new PropertyValueFactory<>("apellido1"));
        colApellido2.setCellValueFactory(new PropertyValueFactory<>("apellido2"));
        colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        
        //Con esto la busqueda es automatica al insertar texto en el tfBusqueda.
        tfBusqueda.textProperty().addListener( (o, ov, nv) -> {
        	filtro.setPredicate(obj -> {
        		if (obj.getNombre().toLowerCase().contains(nv.toLowerCase())) return true;
        		else return false;
        		
        	});
        });

	}

	
	@FXML
	void anteriorClase(MouseEvent event) {
		if(numeroClase > 0) {
			numeroClase --;
			cargarClase(numeroClase); //llama a la funcion cargarClase pasandole el numero de la clase que tiene que cargar.
			
		} else {
			toast.show((Stage) tvAlumnos.getScene().getWindow(), "No puedes retroceder más.\n" + "La Clase 1 es la primera.");
		}
	}

	
	@FXML
	void siguienteClase(MouseEvent event) {
		if(numeroClase < 7) {
			numeroClase ++;
			cargarClase(numeroClase); //llama a la funcion cargarClase pasandole el numero de la clase que tiene que cargar.
			
		} else {
			toast.show((Stage) tvAlumnos.getScene().getWindow(), "No hay siguiente clase.\n" + "La Clase 8 es la ultima.");
		}
	}
	
	
	@FXML
	void addAlumno(MouseEvent event) {
		int i = tvAlumnos.getSelectionModel().getSelectedIndex(); //Guardo el indice del elemento seleccionado en la lista.
		
		if(i != -1){
			Alumno alumno = tvAlumnos.getSelectionModel().getSelectedItem(); //Obtengo el alumno seleccionado.
			
			if(listaClase.contains(alumno)) {
				toast.show((Stage) tvAlumnos.getScene().getWindow(), "El alumno ya esta inscrito en esta Clase.");
			} else if(jornada.alumnoEnJornada(alumno) != -1){
				toast.show((Stage) tvAlumnos.getScene().getWindow(), "El alumno ya esta inscrito en una Clase de esta Jornada.\n" + "Inscrito en Clase " + jornada.alumnoEnJornada(alumno) + ".");
			} else {
				listaClase.add(alumno); //Añado el alumno a la lista de clase
				toast.show((Stage) tvAlumnos.getScene().getWindow(), "Alumno añadido a Clase " + clase.getNumero() + ".");
			}
		}
	}

	
	@FXML
	void quitarAlumno(MouseEvent event) {
		int i = lvClase.getSelectionModel().getSelectedIndex();
		
		if(i != -1) {
			Alumno alumno = lvClase.getSelectionModel().getSelectedItem();
			listaClase.remove(alumno);
			toast.show((Stage) tvAlumnos.getScene().getWindow(), "Alumno eliminado de Clase " + clase.getNumero() + ".");
		}
	}


	@FXML
	void guardar(MouseEvent event) {
		
		try {
			boolean actualizar = false;
			if(claseOriginal.getHoraClase() != clase.getHoraClase()) {
				System.out.println("la hora es diferente");
				actualizar = true;
			}

			if(claseOriginal.getTipo() != clase.getTipo()) {
				System.out.println("El tipo es diferente");
				actualizar = true;
			}

			if(claseOriginal.getAnotaciones() != clase.getAnotaciones()) {
				System.out.println("Las anotaciones son diferentes");
				actualizar = true;
			}

			if(actualizar) {
				conexionBD.actualizarClase(clase, jornada);
			}

			conexionBD.actualizarAlumnosEnClase(new ArrayList<Alumno>(listaClase), clase.getNumero(), jornada);

			claseOriginal.setHoraClase(clase.getHoraClase());
			claseOriginal.setTipo(clase.getTipo());
			claseOriginal.setAnotaciones(clase.getAnotaciones());
			//claseOriginal.setListaAlumnos(clase.getListaAlumnos());
			claseOriginal.setListaAlumnos(new ArrayList<Alumno>(listaClase));
			toast.show((Stage) tvAlumnos.getScene().getWindow(), "Cambios guardados en Clase " + clase.getNumero());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			controller.inicializacion(jornada);
			
		} catch (IOException e) {
			System.out.println("-ERROR- Fallo al cargar jornadaVista.fxml" + e.getMessage());
			e.printStackTrace();
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
		
		listaClase = FXCollections.observableArrayList(clase.getListaAlumnos());
		lvClase.setItems(listaClase);
		
		lbNumeroClase.setText(Integer.toString(clase.getNumero()));
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
