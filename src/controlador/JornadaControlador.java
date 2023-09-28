package controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import baseDatos.ConexionBD;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.converter.LocalDateStringConverter;
import modelo.Alumno;
import modelo.Clase;
import modelo.HoraClase;
import modelo.Jornada;
import modelo.TipoClase;
import utilidades.Constants;
import utilidades.Fechas;
import utilidades.Toast;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;


public class JornadaControlador implements Initializable {
	
	private PrincipalControlador controladorPincipal;
	private DateTimeFormatter formatter;
	private Jornada jornada = null;
	private Jornada jornadaOriginal;
	private ObservableList<Alumno> listadoAlumnosGeneral;
	private ConexionBD conexionBD;
	private Logger logUser;
	private Alert alerta;
	private Toast toast;
	private Double tiempoDelay = 0.5;
	

	@FXML
    private ImageView ivBotonCopiarJornada;

    @FXML
    private ImageView ivBotonCopiarSemana;

	@FXML
    private ImageView ivBotonCrearJornada;

	@FXML
    private ImageView ivBotonBorrarJornada;

	@FXML
    private ImageView ivAnteriorJornada;

	@FXML
    private ImageView ivSiguienteJornada;

    @FXML
    private Button btnGuardarComentario;
	
	@FXML
	private BorderPane bpJornada;
	
	@FXML
    private DatePicker dpFecha;
	
	@FXML
    private Label lbDiaSemana;
	
	@FXML
    private TextArea taComentarios;
	
	@FXML
    private ListView<Alumno> lvClase1;
	private ObservableList<Alumno> listaClase1;

    @FXML
    private ListView<Alumno> lvClase2;
    private ObservableList<Alumno> listaClase2;

    @FXML
    private ListView<Alumno> lvClase3;
    private ObservableList<Alumno> listaClase3;

    @FXML
    private ListView<Alumno> lvClase4;
    private ObservableList<Alumno> listaClase4;

    @FXML
    private ListView<Alumno> lvClase5;
    private ObservableList<Alumno> listaClase5;

    @FXML
    private ListView<Alumno> lvClase6;
    private ObservableList<Alumno> listaClase6;

    @FXML
    private ListView<Alumno> lvClase7;
    private ObservableList<Alumno> listaClase7;

    @FXML
    private ListView<Alumno> lvClase8;
    private ObservableList<Alumno> listaClase8;
    
    @FXML
    private Label lbHoraClase1;

    @FXML
    private Label lbHoraClase2;

    @FXML
    private Label lbHoraClase3;

    @FXML
    private Label lbHoraClase4;

    @FXML
    private Label lbHoraClase5;

    @FXML
    private Label lbHoraClase6;

    @FXML
    private Label lbHoraClase7;

    @FXML
    private Label lbHoraClase8;

    @FXML
    private Label lbTipoClase1;

    @FXML
    private Label lbTipoClase2;

    @FXML
    private Label lbTipoClase3;

    @FXML
    private Label lbTipoClase4;

    @FXML
    private Label lbTipoClase5;

    @FXML
    private Label lbTipoClase6;

    @FXML
    private Label lbTipoClase7;

    @FXML
    private Label lbTipoClase8;

	@FXML
    private Label lbAlumnosClase1;

	@FXML
    private Label lbAlumnosClase2;

	@FXML
    private Label lbAlumnosClase3;

	@FXML
    private Label lbAlumnosClase4;

	@FXML
    private Label lbAlumnosClase5;

	@FXML
    private Label lbAlumnosClase6;

	@FXML
    private Label lbAlumnosClase7;

	@FXML
    private Label lbAlumnosClase8;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Cargar imagenes en ImageView.
        Image flechaRetroceder;
		Image flechaAvnazar;
		Image copiaSemana;
		Image copiaJornada;
		Image borrarJornada;
		Image imagenCrearJornada;
        try {
			//Intentar cargar la imagen desde el recurso en el IDE y en el JAR.
			flechaRetroceder = new Image(getClass().getResourceAsStream("/recursos/flecha_derecha_2.png")); //Forma desde IDE y JAR.
			flechaAvnazar = new Image(getClass().getResourceAsStream("/recursos/flecha_derecha_2.png"));
			copiaSemana = new Image(getClass().getResourceAsStream("/recursos/semana_42px.png"));
			copiaJornada = new Image(getClass().getResourceAsStream("/recursos/dia_semana_42px.png"));
			borrarJornada = new Image(getClass().getResourceAsStream("/recursos/borrar_dia_42px.png"));
			imagenCrearJornada = new Image(getClass().getResourceAsStream("/recursos/nuevo_dia_42px.png"));
        } catch (Exception e) {
			//Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR.
			flechaRetroceder = new Image("/recursos/flecha_derecha_2.png"); //Forma desde el JAR.
			flechaAvnazar = new Image("/recursos/flecha_derecha_2.png");
			copiaSemana = new Image("/recursos/semana_42px.png");
			copiaJornada = new Image("/recursos/dia_semana_42px.png");
			borrarJornada = new Image("/recursos/borrar_dia_42px.png");
			imagenCrearJornada = new Image("/recursos/nuevo_dia_42px.png");
        }
		//Establecer las imagenes cargadas en los ImageView.
		ivAnteriorJornada.setImage(flechaRetroceder);
		ivSiguienteJornada.setImage(flechaAvnazar);
		ivBotonCopiarSemana.setImage(copiaSemana);
		ivBotonCopiarJornada.setImage(copiaJornada);
		ivBotonBorrarJornada.setImage(borrarJornada);
        ivBotonCrearJornada.setImage(imagenCrearJornada);

		//Crear Tooltip.
        Tooltip tltAnteriorJornada = new Tooltip("Día Anterior");
		Tooltip tltSiguienteJornada = new Tooltip("Día Siguiente");
		Tooltip tltBotonCopiarSemana = new Tooltip("Duplicar Semana");
		Tooltip tltBotonCopiarJornada = new Tooltip("Duplicar Jornada");
		Tooltip tltBotonBorrarJornada = new Tooltip("Eliminar Jornada");
		Tooltip tltBotonCrearJornada = new Tooltip("Crear Jornada");

        tltAnteriorJornada.setShowDelay(Duration.seconds(tiempoDelay)); //Establecer retardo de aparición.
		tltSiguienteJornada.setShowDelay(Duration.seconds(tiempoDelay));
		tltBotonCopiarSemana.setShowDelay(Duration.seconds(tiempoDelay)); 
		tltBotonCopiarJornada.setShowDelay(Duration.seconds(tiempoDelay)); 
		tltBotonBorrarJornada.setShowDelay(Duration.seconds(tiempoDelay)); 
		tltBotonCrearJornada.setShowDelay(Duration.seconds(tiempoDelay));  

		Tooltip.install(ivAnteriorJornada, tltAnteriorJornada); //Establecer Tooltip a ImageView.
		Tooltip.install(ivSiguienteJornada, tltSiguienteJornada);
		Tooltip.install(ivBotonCopiarSemana, tltBotonCopiarSemana);
		Tooltip.install(ivBotonCopiarJornada, tltBotonCopiarJornada);
		Tooltip.install(ivBotonBorrarJornada, tltBotonBorrarJornada);
		Tooltip.install(ivBotonCrearJornada, tltBotonCrearJornada);
		
		logUser = Logger.getLogger(Constants.USER); //Crea una instancia de la clase Logger asociada al nombre de registro.
		conexionBD = ConexionBD.getInstance();		//Obtener una instancia de la clase ConexionBD utilizando el patrón Singleton.
		toast = new Toast();
		
		//deshabilita que se puedan seleccionar el contenido del los listView.
		lvClase1.setFocusModel(null); 
		lvClase2.setFocusModel(null);
		lvClase3.setFocusModel(null);
		lvClase4.setFocusModel(null);
		lvClase5.setFocusModel(null);
		lvClase6.setFocusModel(null);
		lvClase7.setFocusModel(null);
		lvClase8.setFocusModel(null);

		//Deshabilitar la edición y la selección del TextArea
		taComentarios.setMouseTransparent(true);
		taComentarios.setFocusTraversable(false);
		
		//Desactivar la selección y la interacción de los ListView.
		lvClase1.setMouseTransparent(true);
		lvClase2.setMouseTransparent(true);
		lvClase3.setMouseTransparent(true);
		lvClase4.setMouseTransparent(true);
		lvClase5.setMouseTransparent(true);
		lvClase6.setMouseTransparent(true);
		lvClase7.setMouseTransparent(true);
		lvClase8.setMouseTransparent(true);
		
		lvClase1.setFocusTraversable(false);
		lvClase2.setFocusTraversable(false);
		lvClase3.setFocusTraversable(false);
		lvClase4.setFocusTraversable(false);
		lvClase5.setFocusTraversable(false);
		lvClase6.setFocusTraversable(false);
		lvClase7.setFocusTraversable(false);
		lvClase8.setFocusTraversable(false);
		
		//Modifica el formato en el que se muestra la fecha en el dtFechaCompra
        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); //Crear un formateador de fecha con el patrón "dd/MM/yyyy".
        dpFecha.setConverter(new LocalDateStringConverter(formatter, null)); //Establecer un convertidor de cadena de fecha para el control DatePicker dpFecha.
        
        //Para poder escribir directamente la fecha
        dpFecha.setEditable(false); //-> Poner a true para que sea editable.

		dpFecha.setValue(LocalDate.now()); //Establece la fecha de hoy en el datePiker. 
        
        dpFecha.setOnAction(event -> {
        	LocalDate fechaSeleccionada = dpFecha.getValue(); //Guardo la fecha seleccionada en fechaSeleccionada.
            lbDiaSemana.setText(Fechas.obtenerDiaSemana(fechaSeleccionada)); //Pongo el dia de la semana de la fecha seleecionada en el label lbDiaSemana.
            
            //LLamar a base de datos para rescatar la jornada
			try {
				//Se comprueba que se ha seleccionado la fecha a traves del DatePiker y no se ha diparado el evento por el cambio de jornada a traves de las flechas.
				if(!((jornada == null)? "null" : jornada.getFecha().toString()).equals(fechaSeleccionada.toString())) {
					inicializacion(conexionBD.getJornadaCompleta(fechaSeleccionada.toString()));
				}
				//inicializacion(conexionBD.getJornadaCompleta(fechaSeleccionada.toString()));
			} catch (SQLException e) {
				logUser.severe("Excepción: " + e.toString());
				e.printStackTrace();
			} catch (Exception e) {
				logUser.severe("Excepción: " + e.toString());
				e.printStackTrace();
			}
        });

		//Muestra el calendario emergente al hacer clic en el TextField del DatePiker.
		dpFecha.getEditor().setOnMouseClicked(event -> {
            dpFecha.show();
        });
	}


	/**
	 * Método que se ejecuta cuando se hace clic en el botón anteriorJornada (Image flecha Izquierda).
	 * Actualiza la vista con la jornada anterior a la fecha seleccionada.
	 *
	 * @param event El evento del mouse que desencadena el método.
	 */
	@FXML
    void anteriorJornada(MouseEvent event) {
		LocalDate fechaAnterio = dpFecha.getValue().plusDays(-1); //Obtiene la fecha anterior a la fecha seleccionada
		Jornada j = null; //Variable para almacenar la jornada obtenida en la consulta a BD.
		try {
			j = conexionBD.getJornadaCompleta(fechaAnterio.toString()); //Obtiene la jornada completa de la fecha anterior.
			inicializacion(j); //Inicializa la vista con la jornada obtenida.
		} catch (SQLException e) {
			logUser.severe("Excepción: " + e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logUser.severe("Excepción: " + e.toString());
			e.printStackTrace();
		}

		if(j == null) {
			dpFecha.setValue(fechaAnterio); //Establece la fecha seleccionada como la fecha anterior.
			lbDiaSemana.setText(Fechas.obtenerDiaSemana(fechaAnterio)); //Actualiza el label de día de la semana en la vista.
		}
    }


	/**
	 * Método que se ejecuta cuando se hace clic en el botón siguienteJornada (Imagen flecha derecha). 
	 * Actualiza la vista con la jornada siguiente a la fecha seleccionada.
	 *
	 * @param event El evento del mouse que desencadena el método.
	 */
	@FXML
    void siguienteJornada(MouseEvent event) {
		LocalDate fechaSiguiente = dpFecha.getValue().plusDays(1); //Obtiene la fecha siguiente a la fecha seleccionada.
		Jornada j = null; //Variable para almacenar la jornada obtenida en la consulta a BD.
		try {
			j = conexionBD.getJornadaCompleta(fechaSiguiente.toString()); //Obtiene la jornada completa de la fecha siguiente.
			inicializacion(j); //Inicializa la vista con la jornada obtenida.
		} catch (SQLException e) {
			logUser.severe("Excepción: " + e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logUser.severe("Excepción: " + e.toString());
		}

		if(j == null) {
			dpFecha.setValue(fechaSiguiente); //Establece la fecha seleccionada como la fecha siguiente.
			lbDiaSemana.setText(Fechas.obtenerDiaSemana(fechaSiguiente)); //Actualiza el label de día de la semana en la vista.
		}
    }
	
	/**
	 * Método que se ejecuta cuando se hace clic en el ListView lvClase1.
	 * Carga la clase correspondiente a la posición 0 en ClaseControlador.
	 *
	 * @param event El evento del mouse que desencadena el método.
	 */
	@FXML
	void clickClase1(MouseEvent event) {
		cargaClase(0);
	}
	
	/**
	 * Método que se ejecuta cuando se hace clic en el ListView lvClase2.
	 * Carga la clase correspondiente a la posición 1 en ClaseControlador.
	 *
	 * @param event El evento del mouse que desencadena el método.
	 */
	@FXML
    void clickClase2(MouseEvent event) {
		cargaClase(1);
    }

	/**
	 * Método que se ejecuta cuando se hace clic en el ListView lvClase3.
	 * Carga la clase correspondiente a la posición 2 en ClaseControlador.
	 *
	 * @param event El evento del mouse que desencadena el método.
	 */
    @FXML
    void clickClase3(MouseEvent event) {
    	cargaClase(2);
    }

	/**
	 * Método que se ejecuta cuando se hace clic en el ListView lvClase4.
	 * Carga la clase correspondiente a la posición 3 en ClaseControlador.
	 *
	 * @param event El evento del mouse que desencadena el método.
	 */
    @FXML
    void clickClase4(MouseEvent event) {
    	cargaClase(3);
    }

	/**
	 * Método que se ejecuta cuando se hace clic en el ListView lvClase5.
	 * Carga la clase correspondiente a la posición 4 en ClaseControlador.
	 *
	 * @param event El evento del mouse que desencadena el método.
	 */
    @FXML
    void clickClase5(MouseEvent event) {
    	cargaClase(4);
    }

	/**
	 * Método que se ejecuta cuando se hace clic en el ListView lvClase6.
	 * Carga la clase correspondiente a la posición 5 en ClaseControlador.
	 *
	 * @param event El evento del mouse que desencadena el método.
	 */
    @FXML
    void clickClase6(MouseEvent event) {
    	cargaClase(5);
    }

	/**
	 * Método que se ejecuta cuando se hace clic en el ListView lvClase7.
	 * Carga la clase correspondiente a la posición 6 en ClaseControlador.
	 *
	 * @param event El evento del mouse que desencadena el método.
	 */
    @FXML
    void clickClase7(MouseEvent event) {
    	cargaClase(6);
    }

	/**
	 * Método que se ejecuta cuando se hace clic en el ListView lvClase8.
	 * Carga la clase correspondiente a la posición 7 en ClaseControlador.
	 *
	 * @param event El evento del mouse que desencadena el método.
	 */
    @FXML
    void clickClase8(MouseEvent event) {
    	cargaClase(7);
    }


	/**
	 * Método que se ejecuta cuando se hace clic en el botón botonGuardarComentario.
	 * Actualiza el comentario de la jornada en la base de datos y muestra un mensaje de confirmación.
	 *
	 * @param event El evento del mouse que desencadena el método.
	 */
	@FXML
	void botonGuardarComentario(MouseEvent event) {
		try {
			conexionBD.actualizarComentarioJornada(jornada); //Actualiza el comentario de la jornada en la base de datos.
			toast.show((Stage) bpJornada.getScene().getWindow(), "Comentario modificado!!.");
			logUser.config("Actualizado comentario de jornada: " + jornada.getFecha().format(formatter));
		} catch (SQLException e) {
			logUser.severe("Excepción: " + e.toString());
			e.printStackTrace();
			toast.show((Stage) bpJornada.getScene().getWindow(), "ERROR al intentar modificar el comentario de la jornada!!.");
		}
	}


	/**
	 * Método que se ejecuta cuando se hace clic en el botón crearJornadaBD (ImageView boton crear jornada).
	 * Crea una nueva jornada en la base de datos si no existe una jornada cargada previamente.
	 *
	 * @param event El evento del mouse que desencadena el método.
	 */
	@FXML
    void crearJornadaBD(MouseEvent event) {
		//Verifica si la jornada cargada es null (no está creada).
		if(jornada == null) {
			Jornada j = crearJornada(); //Crea una nueva jornada con datos genericos y la seleccionada en el DatePiker.
			try {
				conexionBD.insertarJornada(j); //Inserta la jornada en la base de datos.
				inicializacion(j); //Inicializa la vista con la nueva jornada
				logUser.config("Creada Jornada: " + j.getFecha().format(formatter));
				toast.show((Stage) bpJornada.getScene().getWindow(), "Jornada creada correctamente!!");
			} catch (SQLException e) {
				logUser.severe("Excepción: " + e.toString());
				e.printStackTrace();
				toast.show((Stage) bpJornada.getScene().getWindow(), "ERROR al intentar crear la jornada!!");
			} catch (Exception e) {
				logUser.severe("Excepción: " + e.toString());
				e.printStackTrace();
			}
		} else {
			toast.show((Stage) bpJornada.getScene().getWindow(), "Esta Jornada ya esta creada!!");
		}
    }


	/**
	 * Método que se ejecuta cuando se hace clic en el botón borrarJornadaBD.
	 * Borra los datos de la jornada y sus clases asociadas en la base de datos, si existe una jornada cargada previamente.
	 *
	 * @param event El evento del mouse que desencadena el método.
	 */
	@FXML
    void borrarJornadaBD(MouseEvent event) {
		if(jornada != null) {
			boolean borradoOK = false;
			
			//Configuración de la alerta de confirmación para el borrado de la jornada.
			alerta = new Alert(AlertType.CONFIRMATION);
            alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
            alerta.setTitle("Borrar Jornada");
            alerta.setHeaderText("Se va ha borrar los datos de la jornada " + jornada.getFecha().format(formatter) + " y sus clases.");
            alerta.setContentText("¿Estas seguro que quieres borrar esta jornada?");
            alerta.initStyle(StageStyle.DECORATED);
            alerta.initOwner((Stage) bpJornada.getScene().getWindow());
            alerta.initModality(Modality.APPLICATION_MODAL);

			//Muestra la alerta de confirmación y espera a que se seleccione una opción
            Optional<ButtonType> result = alerta.showAndWait();
    		if (result.get() == ButtonType.OK) { //Si se selecciona el botón OK.
                try {
                    borradoOK = conexionBD.borrarJornada(jornada); //Borra la jornada y sus clases asociadas en la base de datos
                } catch (SQLException e) {
                    logUser.severe("Excepción: " + e.toString());
                    e.printStackTrace();
                } catch (Exception e) {
					logUser.severe("Excepción: " + e.toString());
					e.printStackTrace();
				}

				if (borradoOK) {
					logUser.config("Borrada Jornada: " + jornada.getFecha().format(formatter));
					Jornada j = null;
					inicializacion(j); //Reinicializa la vista sin una jornada cargada.
					mensajeAviso(Alert.AlertType.INFORMATION,
							"Borrar Jornada.",
							"",
							"Se ha borrado la Jornada.");
				} else {
					mensajeAviso(Alert.AlertType.ERROR,
							"Borrar Jornada.",
							"",
							"No se ha podido borrar la Jornada.");
					logUser.warning("Fallo al intentar borrar la jornada " + jornada.getFecha().format(formatter));
				}
    			
    		} else {
    			// Si se pulsa el botón de cancelar, no se realiza ninguna acción.
    		}

		} else {
			toast.show((Stage) bpJornada.getScene().getWindow(), "Esta Jornada No esta creada!!\nNo puedes borrar una Jornada que no esta creada.");
		}
    }


	/**
	 * Método que se ejecuta cuando se hace clic en el botón copiarJornada.
	 * Crea y muestra una nueva ventana donde se puede configurar la copia de la jornada actual 
	 * mediante la carga de una vista y su controlador desde un archivo FXML específico (jornadaCardDuplicarVista.fxml).
	 *
	 * @param event El evento del mouse que desencadena el método.
	 */
	@FXML
    void copiarJornada(MouseEvent event) {
		if(jornada == null) {
			toast.show((Stage) bpJornada.getScene().getWindow(), "Esta Jornada No esta creada!!\nNo puedes duplicar una Jornada que no esta creada.");
		} else {
			try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/jornadaCardDuplicarVista.fxml"));
            AnchorPane copiarJornada;
            copiarJornada = (AnchorPane) loader.load();
            JornadaCardDuplicarControlador controller = loader.getController(); // cargo el controlador.
            
            Stage ventana= new Stage();
            ventana.initOwner((Stage) bpJornada.getScene().getWindow());
            ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
            ventana.initStyle(StageStyle.UNDECORATED);

			URL rutaIcono = getClass().getResource("/recursos/lis_logo_1.png"); // guardar ruta de recurso imagen
			ventana.getIcons().add(new Image(rutaIcono.toString())); // poner imagen icono a la ventana	
            
			controller.setJornada(jornada);
			controller.iniciar();
			
            Scene scene = new Scene(copiarJornada);
            scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); //Añade hoja de estilos.
            ventana.setScene(scene);
            ventana.showAndWait();
        	} catch (IOException e) {
				logUser.severe("Excepción: " + e.toString());
				e.printStackTrace();
        	} catch (Exception e) {
				logUser.severe("Excepción: " + e.toString());
				e.printStackTrace();
			}
		}
    }


	/**
	 * Método que se ejecuta cuando se hace clic en el botón copiarSemana.
	 * Crea y muestra una nueva ventana donde se puede configurar la copia de la semana de la jornada actual
	 * mediante la carga de una vista y su controlador desde un archivo FXML específico (semanaCardDuplicarVista.fxml).
	 *
	 * @param event El evento del mouse que desencadena el método.
	 */
    @FXML
    void copiarSemana(MouseEvent event) {
		if(jornada == null) {
			toast.show((Stage) bpJornada.getScene().getWindow(), "Esta Jornada No esta creada!!\nNo puedes duplicar una semana desde una jornada que no esta creada.");
		} else {
			try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/semanaCardDuplicarVista.fxml"));
            AnchorPane copiarSemana;
            copiarSemana = (AnchorPane) loader.load();
            SemanaCardDuplicarControlador controller = loader.getController(); // cargo el controlador.
            
            Stage ventana= new Stage();
            ventana.initOwner((Stage) bpJornada.getScene().getWindow());
            ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
            ventana.initStyle(StageStyle.UNDECORATED);

			URL rutaIcono = getClass().getResource("/recursos/lis_logo_1.png"); // guardar ruta de recurso imagen
			ventana.getIcons().add(new Image(rutaIcono.toString())); // poner imagen icono a la ventana	
            
			controller.setJornada(jornada);
			controller.iniciar();
			
            Scene scene = new Scene(copiarSemana);
            scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); //Añade hoja de estilos.
            ventana.setScene(scene);
            ventana.showAndWait();
        	} catch (IOException e) {
				logUser.severe("Excepción: " + e.toString());
				e.printStackTrace();
        	} catch (Exception e) {
				logUser.severe("Excepción: " + e.toString());
				e.printStackTrace();
			}
		}
    }
	

	/**
	 * Crea una Jornada con sus clases asignadole valores por defecto.
	 * 
	 * @return Un objeto de tipo Jornada con datos por defecto.
	 */
	private Jornada crearJornada() {
		Jornada j = new Jornada(dpFecha.getValue(), "");
		Clase[] clases = new Clase[8];
		clases[0] = new Clase(-1, 1, TipoClase.PILATES, HoraClase.HORA_9_MEDIA, "");
		clases[1] = new Clase(-1, 2, TipoClase.PILATES, HoraClase.HORA_10_MEDIA, "");
		clases[2] = new Clase(-1, 3, TipoClase.PILATES, HoraClase.HORA_11_MEDIA, "");
		clases[3] = new Clase(-1, 4, TipoClase.PILATES, HoraClase.HORA_12_MEDIA, "");
		clases[4] = new Clase(-1, 5, TipoClase.PILATES, HoraClase.HORA_17_MEDIA, "");
		clases[5] = new Clase(-1, 6, TipoClase.PILATES, HoraClase.HORA_18_MEDIA, "");
		clases[6] = new Clase(-1, 7, TipoClase.PILATES, HoraClase.HORA_19_MEDIA, "");
		clases[7] = new Clase(-1, 8, TipoClase.PILATES, HoraClase.HORA_20_MEDIA, "");

		j.setClases(clases); //Establece el Array de Clase a la jornada.

		return j;
	}
	

	/**
	 * Cargar una clase específica en la vista.
	 *
	 * @param numeroClase El número de la clase que se desea cargar.
	 */
	private void cargaClase(int numeroClase) {
		try {
			//FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/ClaseVista.fxml"));
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/claseVista.fxml"));
			BorderPane ClasePilates;
			ClasePilates = (BorderPane) loader.load();
			controladorPincipal.setPane(ClasePilates);
			
			ClaseControlador controller = loader.getController(); //cargo el controlador.
			controller.setControladorPrincipal(controladorPincipal);
			controller.setJornada(jornada);
			controller.setListaAlumnos(listadoAlumnosGeneral);
			controller.setClaseIniciacion(numeroClase);
			
		} catch (IOException e) {
			logUser.severe("Excepción: " + e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logUser.severe("Excepción: " + e.toString());
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Método para inicializar la pantalla de una Jornada.
	 * @param jorn La jornada a inicializar.
	 */
	public void inicializacion(Jornada jorn) {
		if(jorn == null) {
			btnGuardarComentario.setDisable(true); // Deshabilitar el botón de guardar comentario.
			toast.show((Stage) bpJornada.getScene().getWindow(), "Esta jornada no esta creada");
			
			//Si ya hay cargado datos en pantalla, borra los datos.
			if(jornada != null) {
				//Limpiar las listas de alumnos de cada clase.
				listaClase1.clear();
				listaClase2.clear();
				listaClase3.clear();
				listaClase4.clear();
				listaClase5.clear();
				listaClase6.clear();
				listaClase7.clear();
				listaClase8.clear();
				
				//Desvincular las propiedades de texto de las Labels.
				lbHoraClase1.textProperty().unbind();
				lbTipoClase1.textProperty().unbind();
				lbAlumnosClase1.textProperty().unbind();
				
				lbHoraClase2.textProperty().unbind();
				lbTipoClase2.textProperty().unbind();
				lbAlumnosClase2.textProperty().unbind();
				
				lbHoraClase3.textProperty().unbind();
				lbTipoClase3.textProperty().unbind();
				lbAlumnosClase3.textProperty().unbind();
				
				lbHoraClase4.textProperty().unbind();
				lbTipoClase4.textProperty().unbind();
				lbAlumnosClase4.textProperty().unbind();
				
				lbHoraClase5.textProperty().unbind();
				lbTipoClase5.textProperty().unbind();
				lbAlumnosClase5.textProperty().unbind();
				
				lbHoraClase6.textProperty().unbind();
				lbTipoClase6.textProperty().unbind();
				lbAlumnosClase6.textProperty().unbind();
				
				lbHoraClase7.textProperty().unbind();
				lbTipoClase7.textProperty().unbind();
				lbAlumnosClase7.textProperty().unbind();
				
				lbHoraClase8.textProperty().unbind();
				lbTipoClase8.textProperty().unbind();
				lbAlumnosClase8.textProperty().unbind();
				
				//Desvincular el TextArea comentario.
				taComentarios.textProperty().unbindBidirectional(jornada.comentarioProperty());
				
				taComentarios.clear();
				
				//Deshabilitar la edición y la selección del TextArea.
				taComentarios.setEditable(false);
				taComentarios.setFocusTraversable(false);
				taComentarios.setMouseTransparent(true);
				
				//Limpiar el contenido de las Labels de hora de clase.
				lbHoraClase1.setText("");
				lbHoraClase2.setText("");
				lbHoraClase3.setText("");
				lbHoraClase4.setText("");
				lbHoraClase5.setText("");
				lbHoraClase6.setText("");
				lbHoraClase7.setText("");
				lbHoraClase8.setText("");
				
				//Limpiar el contenido de las Labels de tipo de clase.
				lbTipoClase1.setText("");
				lbTipoClase2.setText("");
				lbTipoClase3.setText("");
				lbTipoClase4.setText("");
				lbTipoClase5.setText("");
				lbTipoClase6.setText("");
				lbTipoClase7.setText("");
				lbTipoClase8.setText("");
				
				//Desactivar la selección y la interacción de los ListView.
				lvClase1.setMouseTransparent(true);
				lvClase2.setMouseTransparent(true);
				lvClase3.setMouseTransparent(true);
				lvClase4.setMouseTransparent(true);
				lvClase5.setMouseTransparent(true);
				lvClase6.setMouseTransparent(true);
				lvClase7.setMouseTransparent(true);
				lvClase8.setMouseTransparent(true);
				
				
				lvClase1.setFocusTraversable(false);
				lvClase2.setFocusTraversable(false);
				lvClase3.setFocusTraversable(false);
				lvClase4.setFocusTraversable(false);
				lvClase5.setFocusTraversable(false);
				lvClase6.setFocusTraversable(false);
				lvClase7.setFocusTraversable(false);
				lvClase8.setFocusTraversable(false);
				
				//Establecer la jornada cargada a null.
				jornada = null;
			}
		} 
		
		//Si la jornada pasada como parametro no es null, realizar el proceso de binding
		if(jorn != null) {
			btnGuardarComentario.setDisable(false); //Habilitar el botón de guardar comentario.
			this.jornadaOriginal = jorn; //Asigna la jornada pasada como parametro a jornadaOriginal.
			jornada = new Jornada(jornadaOriginal); //pasar los datos de jornadaOriginal a jornada para que los cambios no afecten directamente a jornadaOriginal
			
			dpFecha.setValue(jornada.getFecha());	//Asigna la fecha de la jornada al datePiker.
			lbDiaSemana.setText(Fechas.obtenerDiaSemana(jornada.getFecha())); //Asigna el dia de la semana de la jornada en el Label lbDiaSemana.
			
			//Cambio los alumnos que tiene cada clase por los alumnos de la aplicacion.
			for(Clase clase : jornada.getClases()) {
				if(!clase.getListaAlumnos().isEmpty()) {
					clase.setListaAlumnos(comprobarListaClase(clase.getListaAlumnos()));
				}
			}

			//Inicializacion del listView. 
			listaClase1 = FXCollections.observableArrayList(jornada.getClase(0).getListaAlumnos());
			lvClase1.setItems(listaClase1);
			
			listaClase2 = FXCollections.observableArrayList(jornada.getClase(1).getListaAlumnos());
			lvClase2.setItems(listaClase2);
			
			listaClase3 = FXCollections.observableArrayList(jornada.getClase(2).getListaAlumnos());
			lvClase3.setItems(listaClase3);
			
			listaClase4 = FXCollections.observableArrayList(jornada.getClase(3).getListaAlumnos());
			lvClase4.setItems(listaClase4);
			
			listaClase5 = FXCollections.observableArrayList(jornada.getClase(4).getListaAlumnos());
			lvClase5.setItems(listaClase5);
		
			listaClase6 = FXCollections.observableArrayList(jornada.getClase(5).getListaAlumnos());
			lvClase6.setItems(listaClase6);
			
			listaClase7 = FXCollections.observableArrayList(jornada.getClase(6).getListaAlumnos());
			lvClase7.setItems(listaClase7);
			
			listaClase8 = FXCollections.observableArrayList(jornada.getClase(7).getListaAlumnos());
			lvClase8.setItems(listaClase8);
			
			//Binding a Labels.
			lbHoraClase1.textProperty().bind(jornada.getClase(0).horaClaseProperty().asString());
			lbTipoClase1.textProperty().bind(jornada.getClase(0).tipoProperty().asString());
			lbAlumnosClase1.textProperty().bind(Bindings.createStringBinding(() -> String.format("%d", listaClase1.size()), listaClase1));
			
			lbHoraClase2.textProperty().bind(jornada.getClase(1).horaClaseProperty().asString());
			lbTipoClase2.textProperty().bind(jornada.getClase(1).tipoProperty().asString());
			lbAlumnosClase2.textProperty().bind(Bindings.createStringBinding(() -> String.format("%d", listaClase2.size()), listaClase2));
			
			lbHoraClase3.textProperty().bind(jornada.getClase(2).horaClaseProperty().asString());
			lbTipoClase3.textProperty().bind(jornada.getClase(2).tipoProperty().asString());
			lbAlumnosClase3.textProperty().bind(Bindings.createStringBinding(() -> String.format("%d", listaClase3.size()), listaClase3));
			
			lbHoraClase4.textProperty().bind(jornada.getClase(3).horaClaseProperty().asString());
			lbTipoClase4.textProperty().bind(jornada.getClase(3).tipoProperty().asString());
			lbAlumnosClase4.textProperty().bind(Bindings.createStringBinding(() -> String.format("%d", listaClase4.size()), listaClase4));
			
			lbHoraClase5.textProperty().bind(jornada.getClase(4).horaClaseProperty().asString());
			lbTipoClase5.textProperty().bind(jornada.getClase(4).tipoProperty().asString());
			lbAlumnosClase5.textProperty().bind(Bindings.createStringBinding(() -> String.format("%d", listaClase5.size()), listaClase5));
			
			lbHoraClase6.textProperty().bind(jornada.getClase(5).horaClaseProperty().asString());
			lbTipoClase6.textProperty().bind(jornada.getClase(5).tipoProperty().asString());
			lbAlumnosClase6.textProperty().bind(Bindings.createStringBinding(() -> String.format("%d", listaClase6.size()), listaClase6));
			
			lbHoraClase7.textProperty().bind(jornada.getClase(6).horaClaseProperty().asString());
			lbTipoClase7.textProperty().bind(jornada.getClase(6).tipoProperty().asString());
			lbAlumnosClase7.textProperty().bind(Bindings.createStringBinding(() -> String.format("%d", listaClase7.size()), listaClase7));
			
			lbHoraClase8.textProperty().bind(jornada.getClase(7).horaClaseProperty().asString());
			lbTipoClase8.textProperty().bind(jornada.getClase(7).tipoProperty().asString());
			lbAlumnosClase8.textProperty().bind(Bindings.createStringBinding(() -> String.format("%d", listaClase8.size()), listaClase8));
			
			//Binding a TextArea comentario.
			taComentarios.textProperty().bindBidirectional(jornada.comentarioProperty());
			
			//habilitar la edición y la selección del TextArea.
			taComentarios.setEditable(true);
			taComentarios.setFocusTraversable(true);
			taComentarios.setMouseTransparent(false);
			
			//Habilitar la selección y la interacción los ListView.
			lvClase1.setMouseTransparent(false);
			lvClase2.setMouseTransparent(false);
			lvClase3.setMouseTransparent(false);
			lvClase4.setMouseTransparent(false);
			lvClase5.setMouseTransparent(false);
			lvClase6.setMouseTransparent(false);
			lvClase7.setMouseTransparent(false);
			lvClase8.setMouseTransparent(false);
			
			lvClase1.setFocusTraversable(true);
			lvClase2.setFocusTraversable(true);
			lvClase3.setFocusTraversable(true);
			lvClase4.setFocusTraversable(true);
			lvClase5.setFocusTraversable(true);
			lvClase6.setFocusTraversable(true);
			lvClase7.setFocusTraversable(true);
			lvClase8.setFocusTraversable(true);
		} 
	}
	

	/**
	 * Comprueba la lista de alumnos de una clase y reemplaza los alumnos por los alumnos de la aplicación.
	 * 
	 * @param listaAlumnos La lista de alumnos de la clase.
	 * @return La nueva lista de alumnos con los alumnos de la aplicación.
	 */
	private ArrayList<Alumno> comprobarListaClase(ArrayList<Alumno> listaAlumnos) {
		ArrayList<Alumno> nuevaListaAlumnos = new ArrayList<Alumno>();
		
		//Itera sobre la lista de alumnos proporcionada.
		for(Alumno alumno : listaAlumnos) {
			//Compara cada alumno con los alumnos de la aplicación.
			for(Alumno alumnoApp : listadoAlumnosGeneral) {
				//Si encuentra una coincidencia por ID, agrega el alumno de la aplicación a la nueva lista.
				if(alumno.getId() == alumnoApp.getId()) {
					nuevaListaAlumnos.add(alumnoApp);
					break;
				}
			}
		}
		return nuevaListaAlumnos; //Devuelve la nueva lista de alumnos.
	}


	/**
     * Muestra una ventana de dialogo con la informacion pasada como parametros.
     * 
     * @param tipo Tipo de alerta.
     * @param tiutlo Titulo de la ventana.
     * @param cabecera Cabecera del mensaje.
     * @param cuerpo Cuerpo del menesaje.
     */
    private void mensajeAviso(AlertType tipo, String tiutlo, String cabecera, String cuerpo) {
        alerta = new Alert(tipo);
        alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
        alerta.setTitle(tiutlo);
        alerta.initOwner((Stage) bpJornada.getScene().getWindow());
        alerta.setHeaderText(cabecera);
        alerta.setContentText(cuerpo);
        alerta.initStyle(StageStyle.DECORATED);
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.showAndWait();
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
	 * Establece la lista de alumnos.
	 * 
	 * @param lista La lista de donde se obtienen los Alumnos
	 */
	public void setListaAlumnos(ObservableList<Alumno> lista) {
		listadoAlumnosGeneral = lista;
	}
	

	/**
	 * Establece una jornada para este controlador.
	 * 
	 * @param jornada El objeto de donde se obtienen los datos. 
	 */
	public void setJornada(Jornada jornada) {
		this.jornada = jornada;
	}	
}
