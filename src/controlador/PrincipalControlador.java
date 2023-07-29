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
	private String menuSeleccionado = "ninguno";
	private ObservableList<Alumno> listadoAlumnosGeneral;
	private ObservableList<Mensualidad> listadoMensualidadesGeneral;
	private ConexionBD conexionBD;
	private Toast toast = new Toast();

	Logger logRoot;
	Logger logUser;
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
			//Intentar cargar la imagen desde el recurso en el IDE y en el JAR.
			imagenLogo = new Image(getClass().getResourceAsStream("/recursos/logo_nuevo.png"));
        } catch (Exception e) {
			//Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR.
            imagenLogo = new Image("/recursos/logo_nuevo.png"); //Establecer la imagen cargada en el ImageView.
        }
        ivLogo.setImage(imagenLogo);

		usuarioRoot = new Usuario(0, "root", "1234", new File("appdata"));
		usuarioActual = null;
		conexionBD = ConexionBD.getInstance(); //Obtener una instancia de la clase ConexionBD utilizando el patrón Singleton.
	
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


	/**
	 * Maneja el evento del menú Inicio.
	 * Cambia la selección del menú y carga la vista correspondiente.
	 * 
	 * @param event El evento del mouse que activa el menú Inicio.
	 */
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


	/**
	 * Maneja el evento del menú Clases.
	 * Cambia la selección del menú y carga la vista correspondiente.
	 * 
	 * @param event El evento del mouse que activa el menú Clases.
	 */
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
				logUser.log(Level.SEVERE, "Fallo al obtener la jornada de la BD. " + e.toString());
				e.printStackTrace();
			}
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/jornadaVista.fxml"));
				BorderPane jornadaPilates;
				
				jornadaPilates = (BorderPane) loader.load();
				bpPrincipal.setCenter(jornadaPilates);
				
				JornadaControlador controller = loader.getController(); // cargo el controlador.
				controller.setControladorPrincipal(this);
				controller.setListaAlumnos(listadoAlumnosGeneral);
				controller.inicializacion(jornada);
				
			} catch (IOException e) {
				logUser.log(Level.SEVERE, "Excepción: " + e.toString());
				e.printStackTrace();
			} catch (Exception e) {
				logUser.log(Level.SEVERE, "Excepción: " + e.toString());
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * Maneja el evento del menú Mensualidad.
	 * Cambia la selección del menú y carga la vista correspondiente.
	 * 
	 * @param event El evento del mouse que activa el menú Mensualidad.
	 */
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
				controller.setListaAlumnos(listadoAlumnosGeneral);
				controller.setListaMensualidades(listadoMensualidadesGeneral);
				controller.setUsuarioActual(usuarioActual);
				
			} catch (IOException e) {
				logUser.log(Level.SEVERE, "Excepción: " + e.toString());
				e.printStackTrace();
			} catch (Exception e) {
				logUser.log(Level.SEVERE, "Excepción: " + e.toString());
				e.printStackTrace();
			}
		}
    }


	/**
	 * Maneja el evento del menú Alumnos.
	 * Cambia la selección del menú y carga la vista correspondiente.
	 * 
	 * @param event El evento del mouse que activa el menú Alumnos.
	 */
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
				controller.setListaAlumnos(listadoAlumnosGeneral);
				controller.setUsuarioActual(usuarioActual);
				
			} catch (IOException e) {
				logUser.log(Level.SEVERE, "Excepción: " + e.toString());
				e.printStackTrace();
			} catch (Exception e) {
				logUser.severe("Excepción: " + e.toString());
			}	
		}
	}


	/**
	 * Maneja el evento del menú Informes.
	 * Cambia la selección del menú y carga la vista correspondiente.
	 * 
	 * @param event El evento del mouse que activa el menú Informes.
	 */
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

			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/informesVista.fxml"));
				GridPane informes;
				
				informes = (GridPane) loader.load();
				bpPrincipal.setCenter(informes);
				
				InformesControlador controller = loader.getController(); // cargo el controlador.
				controller.setUsuarioActual(usuarioActual);
            	controller.setListaMensualidades(listadoMensualidadesGeneral);
				controller.setListaAlumnos(listadoAlumnosGeneral);
				
			} catch (IOException e) {
				logUser.log(Level.SEVERE, "Excepción: " + e.toString());
				e.printStackTrace();
			} catch (Exception e) {
				logUser.log(Level.SEVERE, "Excepción: " + e.toString());
				e.printStackTrace();
			}
		}
    }


	/**
	 * Maneja el evento del menú Usuario.
	 * Cambia la selección del menú y carga la vista correspondiente.
	 * 
	 * @param event El evento del mouse que activa el menú Usuario.
	 */
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
				controller.setControladorPrincipal(this);
				
			} catch (IOException e) {
				logUser.log(Level.SEVERE, "Excepción: " + e.toString());
				e.printStackTrace();
			} catch (Exception e) {
				logUser.log(Level.SEVERE, "Excepción: " + e.toString());
				e.printStackTrace();
			}
		}
    }


	/**
	 * Maneja el evento del menú Ajustes.
	 * Cambia la selección del menú y carga la vista correspondiente.
	 * 
	 * @param event El evento del mouse que activa el menú Ajustes.
	 */
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


	/**
	 * Maneja el evento del menú de salida.
	 * Muestra una ventana de confirmación para salir de la aplicación.
	 * Si se confirma la salida, se cierran los FileHandlers y se cierra la ventana principal de la aplicación.
	 * @param event El evento del mouse que activa el menú de salida.
	 */
	@FXML
	void menuSalir(MouseEvent event) {
		Alert alerta = new Alert(AlertType.CONFIRMATION);
		alerta.getDialogPane().getStylesheets()
				.add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
		alerta.setTitle("Lis_Gestión");
		alerta.setHeaderText("Salir de Aplicación.");
		alerta.setContentText("¿Estas seguro de que quieres salir de la aplicación?");
		alerta.initStyle(StageStyle.DECORATED);
		alerta.initOwner((Stage) bpPrincipal.getScene().getWindow());

		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		ButtonType buttonTypeConfirmar = new ButtonType("Salir", ButtonData.YES);
		alerta.getButtonTypes().setAll(buttonTypeConfirmar, buttonTypeCancel);
		Optional<ButtonType> result = alerta.showAndWait();

		//Si pulsamos el boton confirmar:
		if (result.get() == buttonTypeConfirmar) {
			if (fhRoot != null) {
				fhRoot.close(); //Cerrar el FileHandler del log de root si existe.
			}
			if (fhUser != null) {
				fhUser.close(); //Cerrar el FileHandler del log de usuario si existe.
			}
			((Stage) bpPrincipal.getScene().getWindow()).close(); //Cerrar la ventana principal de la aplicación.
		}
	}


	/**
	 * Habilita los menús de la aplicación.
	 * Habilita los elementos del menú de clases, mensualidades, alumnos, informes, usuario y ajustes.
	 * 
	 */
	private void habilitarMenus() {
		lClases.setDisable(false);
		lbMensualidad.setDisable(false);
		lAlumnos.setDisable(false);
		lInformes.setDisable(false);
		lUsuario.setDisable(false);
		lAjustes.setDisable(false);
	}


	/**
	 * Deshabilita los menús de la aplicación.
	 * Deshabilita los elementos del menú de clases, mensualidades, alumnos, informes, usuario y ajustes.
	 * 
	 */
	private void deshabilitarMenus() {
		lClases.setDisable(true);
		lbMensualidad.setDisable(true);
		lAlumnos.setDisable(true);
		lInformes.setDisable(true);
		lUsuario.setDisable(true);
		lAjustes.setDisable(true);
	}


	/**
	 * Carga la vista de inicio en el centro del BorderPane principal.
	 * La vista de inicio se obtiene a través de un archivo FXML y se configura el controlador correspondiente.
	 * Si ocurre un error al cargar el archivo FXML o al obtener el controlador, se imprime la traza de la excepción.
	 * 
	 */
	private void cargarVistaInicio() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/inicioVista.fxml"));
			BorderPane inicio;

			inicio = (BorderPane) loader.load();
			bpPrincipal.setCenter(inicio);

			InicioControlador controller = loader.getController(); // cargo el controlador.
			controller.setControladorPrincipal(this);
			controller.setUsuarioActual(usuarioActual);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Carga la vista de inicio de sesión en el centro del BorderPane principal.
	 * La vista de inicio de sesión se obtiene a través de un archivo FXML y se configura el controlador correspondiente.
	 * Si ocurre un error al cargar el archivo FXML o al obtener el controlador, se imprime la traza de la excepción.
	 * 
	 */
	private void cargarVistaLogin() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/loginVista.fxml"));
			BorderPane login;

			login = (BorderPane) loader.load();
			bpPrincipal.setCenter(login);
			
			LoginControlador controller = loader.getController(); //Cargo el controlador.
			controller.setControladorPrincipal(this);
			controller.setUsuarioRoot(usuarioRoot);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Crea los archivos y directorios necesarios para la aplicación.
	 * Se crea el directorio del usuario root y el directorio para los archivos log del usuario root.
	 * Se configura el Logger para el usuario root y se asocia a un archivo log.
	 * Se establece la conexión a la base de datos utilizando el usuario root.
	 * Si el archivo de base de datos no existe, se crean las tablas de la aplicación.
	 */
	private void crearFicherosApp() {
		usuarioRoot.getDirectorio().mkdir(); //Crea el directorio del usuarrioRoot si no esta creado.
		
		if(usuarioRoot.getDirectorio().exists()) {
			File dirLog = new File(usuarioRoot.getDirectorio().getName() + "\\" + "log");
			dirLog.mkdir(); //Crea el directorio donde se guardan los archivos log del usuario root.
		}

		logRoot = Logger.getLogger(Constants.USER_ROOT);
		
		try {
			fhRoot = new FileHandler(usuarioRoot.getDirectorio().getName() + "\\" + "log"  + "\\" + "app_log_" + LocalDate.now().getYear() + ".log", true);
			
            logRoot.addHandler(fhRoot); 							//Asociar el log a un fichero log. 
            logRoot.setUseParentHandlers(true);	//Establecer si queremos visualizar los mensajes de log por pantalla.
            SimpleFormatter formatoTxt = new SimpleFormatter();		//Establecer el formato del fichero. 
            fhRoot.setFormatter(formatoTxt);
            logRoot.setLevel(Level.FINE);							//Establezco el nivel de seguridad de las actividades que quiero registrar.

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
				logRoot.log(Level.INFO, "Creadas tablas App");
			} catch (SQLException e) {
				logRoot.log(Level.SEVERE, "Fallo al crear las tablas de la app. " + e.toString());
				e.printStackTrace();
			} catch (Exception e) {
				logRoot.log(Level.SEVERE, "Fallo al crear las tablas de la app. " + e.toString());
				e.printStackTrace();
			}
		}
	}


	/**
	 * Inicia la sesión de un usuario.
	 * 
	 * @param usuario El objeto Usuario que representa al usuario que inicia sesión.
	 */
	public void iniciarSesion(Usuario usuario){
		this.usuarioActual = usuario;
		
		logUser = Logger.getLogger(Constants.USER); //Crea una instancia de la clase Logger asociada al nombre de registro "user".
		
		Boolean logCorrecto = false;
		try {
			fhUser = new FileHandler(usuarioActual.getDirectorio().getName() + "\\" + Constants.FOLDER_LOG  + "\\" + usuarioActual.getNombreUsuario() + "_log_" + LocalDate.now().getYear() + ".log", true);
			
            logUser.addHandler(fhUser); 							//Asociar el log a un fichero log. 
            logUser.setUseParentHandlers(true);	//Establecer si queremos visualizar los mensajes de log por pantalla.
            SimpleFormatter formatoTxt = new SimpleFormatter();		//Establecer el formato del fichero. 
            fhUser.setFormatter(formatoTxt);
            logUser.setLevel(Level.ALL);							//Establecer el nivel de seguridad de las actividades que quiero registrar.

			logUser.info("Inicio de sesión.");
			logCorrecto = true;
		} catch (SecurityException e) {
			logRoot.severe("Excepción: " + e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			logRoot.severe("Excepción: " + e.toString());
			e.printStackTrace();
		}

		//Si no se ha podido asociar el log al fichero log del usuario, asociarlo al log de Root. 
		if (!logCorrecto) {
			if (fhRoot != null) {logUser.addHandler(fhRoot);} //Cerrar el FileHandler del log
			logUser.warning("Fallo al asociar el fichero de log de usuario. (id: " + usuarioActual.getId() + ", nombre: " + usuarioActual.getNombreUsuario() + ")" +
						"Se asocia los mensajes de log de este usuario al log de Root para esta sesion de usuario.");
			logUser.info("Inicio de sesión. (id: " + usuarioActual.getId() + ", nombre: " + usuarioActual.getNombreUsuario() + ")");
		}
		

		habilitarMenus();
		cargarVistaInicio();
		
		try {
			listadoAlumnosGeneral = FXCollections.observableArrayList(conexionBD.getListadoAlumnos());
			listadoMensualidadesGeneral = FXCollections.observableArrayList(conexionBD.getListadoMensualidades());
			for (Alumno a : listadoAlumnosGeneral) {
				for (Mensualidad m : listadoMensualidadesGeneral) {
					if(a.getId() == m.getIdAlumno()) {a.addMensualidad(m);}
				}
			}
		} catch (Exception e) {
			logUser.severe("Excepción: " + e.toString());
			e.printStackTrace();
		}
	}


	/**
	 * Cierra la sesión del usuario actual.
	 * 
	 */
	public void cerrarSesion() {
		this.usuarioActual = null;
		listadoAlumnosGeneral = null;
		listadoMensualidadesGeneral = null;
		conexionBD.setUsuario(usuarioRoot);
		if (fhUser != null) {fhUser.close();} //Cerrar el FileHandler del log

		deshabilitarMenus();

		if(menuSeleccionado.equals("inicio")) {
			cargarVistaLogin();
		} else {
			menuInicio(null);
		}
	
		toast.show((Stage) bpPrincipal.getScene().getWindow(), "Usuario Desconectado!!.");
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
	 * Devuelve el usuario Root de la aplicación.
	 * @return El objeto Usuario que representa al usuario Root de la aplicación.
	 */
	public Usuario getUsuarioRoot() {
		return usuarioRoot;
	}

	
} //Fin PrincipalControlador
