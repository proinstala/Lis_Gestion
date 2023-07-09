package controlador;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import baseDatos.ConexionBD;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modelo.Alumno;
import modelo.Jornada;
import modelo.Mensualidad;
import modelo.Toast;
import modelo.Usuario;
import utilidades.Constants;

public class PrincipalControlador implements Initializable {
	
	private ResourceBundle recursos;
	private Stage escenario;
	private String menuSeleccionado = "ninguno";
	private ObservableList<Alumno> listadoAlumnos;
	private ObservableList<Mensualidad> listadoMensualidades;
	private ConexionBD conexionBD;
	private Toast toast = new Toast();

	Logger loggerRoot;
	Logger loggerUser;
	FileHandler fhRoot;
	FileHandler fhUser;

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
    private Label lbMensualidad;

    @FXML
    private Label lbSalir;
	
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
	
		crearFicherosApp(); //Comprueba si estan los ficheros de la aplicacion creados. Si no lo estan los crea.
		
		deshabilitarMenus();
		
		lInicio.getStyleClass().add("menu");
		lClases.getStyleClass().add("menu");
		lbMensualidad.getStyleClass().add("menu");
		lAlumnos.getStyleClass().add("menu");
		lInformes.getStyleClass().add("menu");
		lUsuario.getStyleClass().add("menu");
		lAjustes.getStyleClass().add("menu");
		lbSalir.getStyleClass().add("menu");

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
			lbMensualidad.getStyleClass().remove("menuSeleccionado");
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
			lbMensualidad.getStyleClass().remove("menuSeleccionado");
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
				loggerUser.log(Level.SEVERE, "Fallo al obtener la jornada de la BD. " + e.toString());
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
				controller.inicializacion(jornada);
				
			} catch (IOException e) {
				loggerUser.log(Level.SEVERE, "Excepción: " + e.toString());
				e.printStackTrace();
			}
		}
	}

	
	@FXML
    void menuMensualidad(MouseEvent event) {
		if(menuSeleccionado != "mensualidad") {
			menuSeleccionado = "mensualidad";
			
			lInicio.getStyleClass().remove("menuSeleccionado");
			lClases.getStyleClass().remove("menuSeleccionado");
			lbMensualidad.getStyleClass().add("menuSeleccionado");
			lAlumnos.getStyleClass().remove("menuSeleccionado");
			lInformes.getStyleClass().remove("menuSeleccionado");
			lUsuario.getStyleClass().remove("menuSeleccionado");
			lAjustes.getStyleClass().remove("menuSeleccionado");

			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/mensualidadesVista.fxml"));
				BorderPane mensualidades;
				
				mensualidades = (BorderPane) loader.load();
				bpPrincipal.setCenter(mensualidades);
				
				MensualidadesControlador controller = loader.getController(); // cargo el controlador.
				//controller.setControladorPrincipal(this);
				controller.setListaAlumnos(listadoAlumnos);
				controller.setListaMensualidades(listadoMensualidades);
				controller.setStage(escenario);
				
			} catch (IOException e) {
				loggerUser.log(Level.SEVERE, "Excepción: " + e.toString());
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
			lbMensualidad.getStyleClass().remove("menuSeleccionado");
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
				loggerUser.log(Level.SEVERE, "Excepción: " + e.toString());
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
			lbMensualidad.getStyleClass().remove("menuSeleccionado");
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
			lbMensualidad.getStyleClass().remove("menuSeleccionado");
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
				loggerUser.log(Level.SEVERE, "Excepción: " + e.toString());
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
			lbMensualidad.getStyleClass().remove("menuSeleccionado");
			lAlumnos.getStyleClass().remove("menuSeleccionado");
			lInformes.getStyleClass().remove("menuSeleccionado");
			lUsuario.getStyleClass().remove("menuSeleccionado");
			lAjustes.getStyleClass().add("menuSeleccionado");
		}
    }


	@FXML
	void menuSalir(MouseEvent event) {
		Alert alerta = new Alert(AlertType.CONFIRMATION);
		alerta.getDialogPane().getStylesheets()
				.add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
		alerta.setTitle("Lis_Gestión");
		alerta.setHeaderText("Salir de Aplicación.");
		alerta.setContentText("¿Estas seguro de que quieres salir de la aplicación?");
		alerta.initStyle(StageStyle.DECORATED);
		alerta.initOwner(escenario);

		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		ButtonType buttonTypeConfirmar = new ButtonType("Salir", ButtonData.YES);
		alerta.getButtonTypes().setAll(buttonTypeConfirmar, buttonTypeCancel);
		Optional<ButtonType> result = alerta.showAndWait();

		// Si pulsamos el boton confirmar:
		if (result.get() == buttonTypeConfirmar) {
			if (fhRoot != null) {
				fhRoot.close();
			}
			if (fhUser != null) {
				fhUser.close();
			}
			escenario.close();
		}
	}


	public void iniciarSesion(Usuario usuario){
		this.usuarioActual = usuario;
		conexionBD.setUsuario(usuarioActual);

		try {
			conexionBD.getDatosUsuario(usuarioActual);
		} catch (SQLException e) {
			loggerRoot.severe("Excepción: " + e.toString());
			e.printStackTrace();
		}

		loggerUser = Logger.getLogger(Constants.USER); //Crea una instancia de la clase Logger asociada al nombre de registro "user".
		
		try {
			fhUser = new FileHandler(usuarioActual.getDirectorio().getName() + "\\" + "log"  + "\\" + usuarioActual.getNombreUsuario() + "_log_" + LocalDate.now().getYear() + ".log", true);
			
            loggerUser.addHandler(fhUser); 							//Asociar el log a un fichero log. 
            loggerUser.setUseParentHandlers(true);//Establecer si queremos visualizar los mensajes de log por pantalla.
            SimpleFormatter formatoTxt = new SimpleFormatter();		//Establecer el formato del fichero. 
            fhUser.setFormatter(formatoTxt);
            loggerUser.setLevel(Level.ALL);						//Establezco el nivel de seguridad de las actividades que quiero registrar.

		} catch (SecurityException e) {
			loggerRoot.severe("Excepción: " + e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			loggerRoot.severe("Excepción: " + e.toString());
			e.printStackTrace();
		}

		loggerUser.info("Inicio de sesión.");

		habilitarMenus();
		cargarVistaInicio();
		
		try {
			listadoAlumnos = FXCollections.observableArrayList(conexionBD.getListadoAlumnos());
			listadoMensualidades = FXCollections.observableArrayList(conexionBD.getListadoMensualidades());
			for (Alumno a : listadoAlumnos) {
				for (Mensualidad m : listadoMensualidades) {
					if(a.getId() == m.getIdAlumno()) {a.addMensualidad(m);}
				}
			}
		} catch (Exception e) {
			loggerUser.severe("Excepción: " + e.toString());
			e.printStackTrace();
		}
	}

	public void cerrarSesion() {
		this.usuarioActual = null;
		listadoAlumnos = null;
		conexionBD.setUsuario(usuarioRoot);
		fhUser.close();

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
		lbMensualidad.setDisable(false);
		lAlumnos.setDisable(false);
		lInformes.setDisable(false);
		lUsuario.setDisable(false);
		lAjustes.setDisable(false);
	}


	private void deshabilitarMenus() {
		lClases.setDisable(true);
		lbMensualidad.setDisable(true);
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
    

	private void crearFicherosApp() {
		usuarioRoot.getDirectorio().mkdir(); //Crea el directorio del usuarrioRoot si no esta creado.
		
		if(usuarioRoot.getDirectorio().exists()) {
			File dirLog = new File(usuarioRoot.getDirectorio().getName() + "\\" + "log");
			dirLog.mkdir(); //Crea el directorio donde se guardan los archivos log del usuario root.
		}

		loggerRoot = Logger.getLogger(Constants.USER_ROOT);
		
		try {
			fhRoot = new FileHandler(usuarioRoot.getDirectorio().getName() + "\\" + "log"  + "\\" + "app_log_" + LocalDate.now().getYear() + ".log", true);
			
            loggerRoot.addHandler(fhRoot); 							//Asociar el log a un fichero log. 
            loggerRoot.setUseParentHandlers(true);//Establecer si queremos visualizar los mensajes de log por pantalla.
            SimpleFormatter formatoTxt = new SimpleFormatter();		//Establecer el formato del fichero. 
            fhRoot.setFormatter(formatoTxt);
            loggerRoot.setLevel(Level.FINE);						//Establezco el nivel de seguridad de las actividades que quiero registrar.

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		conexionBD.setUsuario(usuarioRoot);
		File ficheroBD = new File(usuarioRoot.getDirectorio().getName() + "\\" + usuarioRoot.getNombreUsuario() + conexionBD.FINAL_NOMBRE_FICHERO_DB);
		if(!ficheroBD.exists()) {
			try {
				conexionBD.crearTablasApp();
				loggerRoot.log(Level.INFO, "Creadas tablas App");
			} catch (SQLException e) {
				loggerRoot.log(Level.SEVERE, "Fallo al crear las tablas de la app. " + e.toString());
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
    }

	public Stage getStage() {
		return this.escenario;
	}


} //Fin PrincipalControlador
