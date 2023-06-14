package controlador;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import baseDatos.ConexionBD;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import modelo.Alumno;
import modelo.Jornada;
import modelo.Toast;
import modelo.Usuario;

public class PrincipalControlador implements Initializable {
	
	private ResourceBundle recursos;
	private Stage escenario;
	private String menuSeleccionado = "ninguno";
	private ObservableList<Alumno> listadoAlumnos;
	private ConexionBD conexionBD;
	private Toast toast = new Toast();
	
	private Usuario usuarioRoot; 
	private Usuario usuarioActual;
	
	
	@FXML
    private ImageView ivLogo;

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
    private Label lAjustes;

	@FXML
    private Label lInformes;

	@FXML
    private Label lUsuario;
	
	@FXML
    private Pane pSeparador;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Cargar imagenes en ImageView.
        Image imagenLogo;
        try {
            imagenLogo = new Image("/recursos/logo_nuevo.png");
        } catch (Exception e) {
            imagenLogo = new Image(getClass().getResourceAsStream("/recursos/logo_nuevo.png"));
        }
        ivLogo.setImage(imagenLogo);

		usuarioRoot = new Usuario(0, "root", "1234", new File("appdata"));
		usuarioActual = null;
		conexionBD = ConexionBD.getInstance();

		//paso 1: llamar metodo comprobar ficheros app
		if(!comprobarFicherosApp()) {
			crearFicherosApp();
		}
		
		deshabilitarMenus();
		
		lInicio.getStyleClass().add("menu");
		lClases.getStyleClass().add("menu");
		lAlumnos.getStyleClass().add("menu");
		lInformes.getStyleClass().add("menu");
		lUsuario.getStyleClass().add("menu");
		lAjustes.getStyleClass().add("menu");

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
			lInformes.getStyleClass().remove("menuSeleccionado");
			lUsuario.getStyleClass().remove("menuSeleccionado");
			lAjustes.getStyleClass().remove("menuSeleccionado");
			
			if(usuarioActual == null) {
				cargarVistaLogin();
			} else {
				cargarVistaInicio();
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
			lInformes.getStyleClass().remove("menuSeleccionado");
			lUsuario.getStyleClass().remove("menuSeleccionado");
			lAjustes.getStyleClass().remove("menuSeleccionado");
			
			Jornada jornada;
			try {
				//jornada = conexionBD.getJornada(LocalDate.now().toString());
				jornada = conexionBD.getJornadaCompleta(LocalDate.now().toString());
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
				controller.setStage(escenario);
				controller.inicializacion(jornada);
				
			} catch (IOException e) {
				System.out.println("-ERROR- Fallo al cargar jornadaVista.fxml" + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	
	@FXML
	void menuAlumnos(MouseEvent event) {
		if(menuSeleccionado != "alumnos") {
			menuSeleccionado = "alumnos";
			
			lInicio.getStyleClass().remove("menuSeleccionado");
			lClases.getStyleClass().remove("menuSeleccionado");
			lAlumnos.getStyleClass().add("menuSeleccionado");
			lInformes.getStyleClass().remove("menuSeleccionado");
			lUsuario.getStyleClass().remove("menuSeleccionado");
			lAjustes.getStyleClass().remove("menuSeleccionado");

			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/alumnosVista.fxml"));
				BorderPane alumnos;
				
				alumnos = (BorderPane) loader.load();
				bpPrincipal.setCenter(alumnos);
				
				AlumnosControlador controller = loader.getController(); // cargo el controlador.
				controller.setControladorPrincipal(this);
				controller.setListaAlumnos(listadoAlumnos);
				controller.setStage(escenario);
				
			} catch (IOException e) {
				System.out.println("-ERROR- Fallo al cargar jornadaVista.fxml" + e.getMessage());
				e.printStackTrace();
			}
		}
	}


	@FXML
    void menuInformes(MouseEvent event) {
		if(menuSeleccionado != "informes") {
			menuSeleccionado = "informes";
			
			lInicio.getStyleClass().remove("menuSeleccionado");
			lClases.getStyleClass().remove("menuSeleccionado");
			lAlumnos.getStyleClass().remove("menuSeleccionado");
			lInformes.getStyleClass().add("menuSeleccionado");
			lUsuario.getStyleClass().remove("menuSeleccionado");
			lAjustes.getStyleClass().remove("menuSeleccionado");
		}
    }


	@FXML
    void menuUsuario(MouseEvent event) {
		if(menuSeleccionado != "usuario") {
			menuSeleccionado = "usuario";
			
			lInicio.getStyleClass().remove("menuSeleccionado");
			lClases.getStyleClass().remove("menuSeleccionado");
			lAlumnos.getStyleClass().remove("menuSeleccionado");
			lInformes.getStyleClass().remove("menuSeleccionado");
			lUsuario.getStyleClass().add("menuSeleccionado");
			lAjustes.getStyleClass().remove("menuSeleccionado");

			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/usuarioVista.fxml"));
				GridPane usuario;
				
				usuario = (GridPane) loader.load();
				bpPrincipal.setCenter(usuario);
				
				UsuarioControlador controller = loader.getController(); // cargo el controlador.
				controller.setUsuarioActual(usuarioActual);
            	controller.setUsuarioRoot(usuarioRoot);
				controller.setStage(escenario);
				controller.setControladorPrincipal(this);
				
			} catch (IOException e) {
				//poner log.
				e.printStackTrace();
			}
		}
    }


	@FXML
    void menuAjustes(MouseEvent event) {
		if(menuSeleccionado != "ajustes") {
			menuSeleccionado = "ajustes";
			
			lInicio.getStyleClass().remove("menuSeleccionado");
			lClases.getStyleClass().remove("menuSeleccionado");
			lAlumnos.getStyleClass().remove("menuSeleccionado");
			lInformes.getStyleClass().remove("menuSeleccionado");
			lUsuario.getStyleClass().remove("menuSeleccionado");
			lAjustes.getStyleClass().add("menuSeleccionado");
		}
    }


	public void iniciarSesion(Usuario usuario){
		this.usuarioActual = usuario;
		conexionBD.setUsuario(usuarioActual);

		try {
			conexionBD.getDatosUsuario(usuarioActual);
		} catch (SQLException e) {
			System.out.println("No se ha podido recuperar los datos del usuario."); //Provisional
			e.printStackTrace();
		}

		habilitarMenus();
		cargarVistaInicio();
		
		try {
			listadoAlumnos = FXCollections.observableArrayList(conexionBD.getListadoAlumnos());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void cerrarSesion() {
		this.usuarioActual = null;
		listadoAlumnos = null;
		conexionBD.setUsuario(usuarioRoot);

		deshabilitarMenus();

		if(menuSeleccionado.equals("inicio")) {
			cargarVistaLogin();
		} else {
			menuInicio(null);
		}
		

		toast.show(escenario, "Usuario Desconectado!!.");
	}


	private void habilitarMenus() {
		lClases.setDisable(false);
		lAlumnos.setDisable(false);
		lInformes.setDisable(false);
		lUsuario.setDisable(false);
		lAjustes.setDisable(false);
	}


	private void deshabilitarMenus() {
		lClases.setDisable(true);
		lAlumnos.setDisable(true);
		lInformes.setDisable(true);
		lUsuario.setDisable(true);
		lAjustes.setDisable(true);
	}


	private void cargarVistaInicio() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/inicioVista.fxml"));
			BorderPane inicio;

			inicio = (BorderPane) loader.load();
			bpPrincipal.setCenter(inicio);

			InicioControlador controller = loader.getController(); // cargo el controlador.
			controller.setControladorPrincipal(this);
			controller.setUsuarioApp(usuarioRoot);
			controller.setUsuarioActual(usuarioActual);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void cargarVistaLogin() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/loginVista.fxml"));
			BorderPane login;

			login = (BorderPane) loader.load();
			bpPrincipal.setCenter(login);
			
			LoginControlador controller = loader.getController(); //Cargo el controlador.
			controller.setControladorPrincipal(this);
			controller.setUsuarioRoot(usuarioRoot);
			//controller.setStage(escenario);
		} catch (IOException e) {
			e.printStackTrace();
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
    
    
	
	private boolean comprobarFicherosApp() {
		return false;
	}

	private void crearFicherosApp() {
		usuarioRoot.getDirectorio().mkdir();
		conexionBD.setUsuario(usuarioRoot);
		File ficheroBD = new File(usuarioRoot.getDirectorio().getName() + "\\" + usuarioRoot.getNombreUsuario() + conexionBD.FINAL_NOMBRE_FICHERO_DB);
		if(!ficheroBD.exists()) {
			try {
				conexionBD.crearTablasApp();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}


	public Usuario getUsuarioApp() {
		return usuarioRoot;
	}

	//NOTA: Si no hace falta este metodo, borrarlo.
	/**
	 * Etablece el usuario que esta usando la aplicación.
	 * 
	 * @param usuario
	 */
	public void setUsuario(Usuario usuario) {
		this.usuarioActual = usuario;
	}


	/**
     * Establece un Stage para este controlador.
     * 
     * @param stage Stage que se establece.
     */
    public void setStage(Stage stage) {
    	this.escenario = stage;
		if(escenario == null) {
			System.out.println("Escenario (Stage) Es nulo");
		}
    }

	public Stage getStage() {
		return this.escenario;
	}


} //Fin PrincipalControlador
