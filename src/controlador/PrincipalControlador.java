package controlador;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

import baseDatos.ConexionBD;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import modelo.Alumno;
import modelo.Datos;
import modelo.Jornada;
import modelo.Toast;
import modelo.Usuario;

public class PrincipalControlador implements Initializable {
	
	private ResourceBundle recursos;
	private Stage escenario;
	private String menuSeleccionado = "ninguno";
	private ObservableList<Alumno> listadoAlumnos;
	private Datos datos;
	private ConexionBD conexionBD;
	private Toast toast = new Toast();
	
	private final String[] usuarioAPP = {"lisgestion", "lisgestion", "appdata"}; //[0]nombre, [1]password, [2]directorio
	private String[] usuario = null; //[0]nombre, [1]password
	
	private Usuario usuarioApp; 
	private Usuario usuarioActual;
	
	
	@FXML
    private BorderPane bpPrincipal;
	
	@FXML
    private GridPane gpMenu;

	@FXML
	private Label lClases;

	@FXML
	private Label lAlumnos;

	@FXML
	private Label lInicio;
	
	@FXML
    private Pane pSeparador;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		usuarioApp = new Usuario(0, "lisgestion", "lisgestion", new File("appdata"), "lisgestion");
		conexionBD = ConexionBD.getInstance();

		//paso 1: llamar metodo comprobar ficheros app
		if(!comprobarFicherosApp()) {
			crearFicherosApp();
		}
		
		//lInicio.setDisable(true);
		lClases.setDisable(true);
		lAlumnos.setDisable(true);

		datos = Datos.getInstance();
		//conexionBD = ConexionBD.getInstance();

		/* 
		try {
			listadoAlumnos = FXCollections.observableArrayList(conexionBD.getListadoAlumnos());
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}*/
		//listadoAlumnos = FXCollections.observableArrayList(datos.listarAlumno());
		lInicio.getStyleClass().add("menu");
		lClases.getStyleClass().add("menu");
		lAlumnos.getStyleClass().add("menu");
		gpMenu.getStyleClass().add("gridPaneMenu");
		pSeparador.getStyleClass().add("panelSeparador");

		menuInicio(null);
		
	}


	@FXML
	void menuInicio(MouseEvent event) {
		if (menuSeleccionado != "inicio") {
			menuSeleccionado = "inicio";
			
			lInicio.getStyleClass().add("menuSeleccionado");
			lClases.getStyleClass().remove("menuSeleccionado");
			lAlumnos.getStyleClass().remove("menuSeleccionado");

			//----------------------------
			

			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/loginVista.fxml"));
				BorderPane login;
				login = (BorderPane) loader.load();
				bpPrincipal.setCenter(login);
				LoginControlador controller = loader.getController(); // cargo el controlador.
				controller.setControladorPrincipal(this);
				controller.setUsuarioApp(usuarioApp);
				controller.setStage(escenario);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		}
	}

	
	@FXML
	void menuClases(MouseEvent event) {
		if (menuSeleccionado != "clases") {
			menuSeleccionado = "clases";

			lInicio.getStyleClass().remove("menuSeleccionado");
			lClases.getStyleClass().add("menuSeleccionado");
			lAlumnos.getStyleClass().remove("menuSeleccionado");
			
			
			//Jornada jornada = datos.getJornada(LocalDate.of(2023,4,8));
			//Jornada jornada = null;
			Jornada jornada;
			try {
				jornada = conexionBD.getJornada(LocalDate.now().toString());
			} catch (SQLException e) {
				jornada = null;
				e.printStackTrace();
			}
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/jornadaVista.fxml"));
				BorderPane jornadaPilates;
				
				jornadaPilates = (BorderPane) loader.load();
				bpPrincipal.setCenter(jornadaPilates);
				
				JornadaControlador controller = loader.getController(); // cargo el controlador.
				controller.setControladorPrincipal(this);
				controller.setListaAlumnos(listadoAlumnos);
				controller.setConexionBD(datos);
				controller.inicializacion(jornada);
				
			} catch (IOException e) {
				System.out.println("-ERROR- Fallo al cargar jornadaVista.fxml" + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	
	@FXML
	void menuAlumnos(MouseEvent event) {
		if(menuSeleccionado != "clientes") {
			menuSeleccionado = "clientes";
			
			lInicio.getStyleClass().remove("menuSeleccionado");
			lClases.getStyleClass().remove("menuSeleccionado");
			lAlumnos.getStyleClass().add("menuSeleccionado");
		}
		
	}
	
	
	/**
     * Establece en el centro de este BorderPane el que se pasa por parametro.
     * 
     * @param BorderPane que se establece en el centro.
     */
    public void setPane(BorderPane p) {
    	bpPrincipal.setCenter(p);
    }
    
    /**
     * Establece un Stage para este controlador.
     * 
     * @param s Stage que se establece.
     */
    public void setStage(Stage s) {
    	this.escenario = s;
    }
	
	private boolean comprobarFicherosApp() {
		return false;
	}

	private void crearFicherosApp() {
		usuarioApp.getDirectorio().mkdir();
		conexionBD.setUsuario(usuarioApp);
		File ficheroBD = new File(usuarioApp.getDirectorio().getName() + "\\" + usuarioApp.getNombre() + conexionBD.FINAL_NOMBRE_FICHERO_DB);
		if(!ficheroBD.exists()) {
			try {
				conexionBD.crearTablasApp();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}


	private void iniciarApp(){
		lClases.setDisable(false);
		lAlumnos.setDisable(false);
		conexionBD.setUsuario(usuarioActual);
		
		try {
			listadoAlumnos = FXCollections.observableArrayList(conexionBD.getListadoAlumnos());
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}

	/* 
	public String[] getUsuarioApp() {
		return usuarioAPP;
	}

	public void setUsuario(String[] usuario) {
		this.usuario = usuario;
		iniciarApp();
	}/*/

	public Usuario getUsuarioApp() {
		return usuarioApp;
	}

	public void setUsuario(Usuario usuario) {
		this.usuarioActual = usuario;
		iniciarApp();
	}


} //Fin PrincipalControlador
