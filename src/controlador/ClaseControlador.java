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
import javafx.scene.control.ListCell;
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
import javafx.util.StringConverter;
import modelo.Alumno;
import modelo.Clase;
import modelo.EstadoAlumno;
import modelo.GrupoAlumnos;
import modelo.HoraClase;
import modelo.Jornada;
import modelo.TipoClase;
import utilidades.Constants;
import utilidades.Fechas;
import utilidades.Toast;


public class ClaseControlador implements Initializable {

	private IntegerBinding totalAlumnos;
	private PrincipalControlador controladorPincipal;
	private ObservableList<GrupoAlumnos> listadoGruposAlumnosGeneral;
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
	private boolean checkChanges = false;
	

	@FXML
    private BorderPane bpClase;

	@FXML
    private TextField tfBusqueda;

	@FXML
	private ComboBox<HoraClase> cbHora;

	@FXML
	private ComboBox<TipoClase> cbTipo;

	@FXML
    private ComboBox<String> cbModoFiltro;

	@FXML
    private ComboBox<String> cbEstado;

    @FXML
    private ComboBox<GrupoAlumnos> cbGrupoAlumnos;

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
		ivFlechaIzquierda.getStyleClass().add("iv_resaltado");
        ivFlechaDerecha.getStyleClass().add("iv_resaltado");
        ivFlechaAdd.getStyleClass().add("iv_resaltado");
        ivFlechaQuitar.getStyleClass().add("iv_resaltado");
        ivVolver.getStyleClass().add("iv_resaltado");
        ivGuardar.getStyleClass().add("iv_resaltado");

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
		//cbHora.setItems(FXCollections.observableArrayList(HoraClase.values()));
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
        

		/* 
		Nota: Se cambia forma de filtro para tener mas opciones de filtrado. 

        //Con esto la busqueda es automatica al insertar texto en el tfBusqueda.
        tfBusqueda.textProperty().addListener( (o, ov, nv) -> {
        	filtro.setPredicate(obj -> {
        		if (obj.getNombre().toLowerCase().contains(nv.toLowerCase())) {return true;}
        		else {return false;}
        	});
        });
		*/


		//Configurar el ComboBox cbEstado.
        ObservableList<String> listadoEstado = FXCollections.observableArrayList();
        listadoEstado.setAll(EstadoAlumno.ACTIVO.toString(), EstadoAlumno.BAJA.toString(), "TODOS");
        cbEstado.setItems(listadoEstado);
		cbEstado.setValue("TODOS"); //Valor inicial.

        //Configurar Listener para el ComboBox cbEstado.
        cbEstado.setOnAction(e -> {
            tfBusqueda.clear();
            configurarFiltro("");
        });

		

        //cbModoFiltro
        ObservableList<String> tipoBusqueda = FXCollections.observableArrayList();
        tipoBusqueda.setAll("Id Alumno", "Nombre Alumno");
        cbModoFiltro.setItems(tipoBusqueda);
        cbModoFiltro.setValue("Nombre Alumno"); //Valor inicial.

        //Configurar Listener para el ComboBox cbModoFiltro.
        cbModoFiltro.setOnAction(e -> {
            tfBusqueda.clear();
            configurarFiltro("");
        });

        //Configurar Listener para el TextField tfBusqueda.
        tfBusqueda.textProperty().addListener( (o, ov, nv) -> {
            configurarFiltro(nv);
        });
	}


	/**
     * Configura el ComboBox de Alumnos.
     * 
     */
    private void configurarCbGrupoAlumnos() {

        //Creo un ObservableList<String> con el nombre de los meses del año. Cargo la lista en el ComboBox cbMes.
        ObservableList<GrupoAlumnos> listGrupos = FXCollections.observableArrayList(listadoGruposAlumnosGeneral);
        
        GrupoAlumnos grupoTodos = new GrupoAlumnos();
        grupoTodos.setNombre("TODOS");
        listGrupos.add(0, grupoTodos);
        cbGrupoAlumnos.setItems(listGrupos);
        cbGrupoAlumnos.setValue(grupoTodos);

        //Establecer el texto a mostrar en el ComboBox cuando está desplegado utilizando un StringConverter.
        cbGrupoAlumnos.setCellFactory(param -> new ListCell<GrupoAlumnos>() {
            @Override
            protected void updateItem(GrupoAlumnos a, boolean empty) {
                super.updateItem(a, empty);
                if (empty || a == null) {
                    setText(null);
                } else {     
                    if(a.equals(grupoTodos)) {
                        setText("   - " + a.getNombre());
                    } else {
                        setText(a.getId() + " - " + a.getNombre()); //Mostrar el ID y el nombre del Alumno en el ComboBox cuando está desplegado.
                    }
                }
            }
        });

        
        //Establecer el texto a mostrar en el ComboBox utilizando un CellFactory.
        cbGrupoAlumnos.setConverter(new StringConverter<GrupoAlumnos>() {
            @Override
            public String toString(GrupoAlumnos a) {
                if (a != null) {
                    //Mostrar el ID y el nombre del Alumno en el ComboBox cuando está desplegado.
                    //return a.getId() + " - " + a.getNombre();
                    return a.getNombre();
                }
                return null;
            }

            @Override
            public GrupoAlumnos fromString(String string) {
                // No se necesita esta implementación para este caso.
                return null;
            }
        });

        //Establece un listener para que cuando se seleccione un elemento del ComboBox cbAlumnos.
        cbGrupoAlumnos.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            tfBusqueda.clear();
            configurarFiltro("");
            
        });
    }//FIN configurarCbAlumnos.


	/**
     * Configura el filtro para la tabla de alumno según los criterios seleccionados.
     *
     * @param texto El texto de búsqueda utilizado para filtrar.
     */
    private void configurarFiltro(String texto) {
        filtro.setPredicate(obj -> {

            /*
            if ( !(cbGenero.getValue().equals("AMBOS")) && !(obj.generoProperty().getValue().toString().equals(cbGenero.getValue())) ) {
                return false;
            }
            */

            /* 
            if ( !(cbLocalidad.getValue().equals("TODAS")) && !(obj.getDireccion().localidadProperty().getValue().toString().equals(cbLocalidad.getValue())) ) {
                return false;
            }
			*/

            if ( !(cbEstado.getValue().equals("TODOS")) && !(obj.estadoProperty().getValue().toString().equals(cbEstado.getValue())) ) {
                return false;
            }

            //Comprueba que el alumno este en el grupo seleccionado para no devolver false. (id -1 = TODOS).
            GrupoAlumnos grupoSeleccionado = cbGrupoAlumnos.getValue();
            if ( (grupoSeleccionado.getId() != -1)) {
                boolean match = false;
               
                for(Alumno alumnoGrupo : grupoSeleccionado.getListaAlumnos()) {
                    if(alumnoGrupo.getId() == obj.idProperty().getValue()) {
                        match = true;
                        break;
                    }
                }
                    
                if (!match) {
                    return false;
                }
            }
        		
            if (cbModoFiltro.getValue() == "Nombre Alumno") {
                for(Alumno alumno : listadoAlumnosGeneral) {
                    if(obj.getId() == alumno.getId()) {
                        if(alumno.getNombreCompleto().toLowerCase().contains(texto.toLowerCase())) {
                            return true;
                        }
                    }    
                }
                return false;
            } else {
                if(texto.isBlank()) {return true;}
                if(texto.matches("[0-9]{0,4}") && obj.getId() == Integer.parseInt(texto)) {
                    return true;
                }

                return false;
            }
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
			//Comprueba si hay cambios en la clase actual si guardar para preguntar si se quiere guardar los cambios.
			if(checkChanges && (cambiosConfigClase() || cambiosListaClase())) {
				ButtonData typeAnswer = saveForgottenChanges();
				//Si la respuesta es cancelar, no se carga la clase anterior.
				if(typeAnswer == ButtonData.CANCEL_CLOSE) {
					return; //termina la ejecucion de este metodo.
				}
			} 

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
			//Comprueba si hay cambios en la clase actual si guardar para preguntar si se quiere guardar los cambios.
			if(checkChanges && (cambiosConfigClase() || cambiosListaClase())) {
				ButtonData typeAnswer = saveForgottenChanges();
				//Si la respuesta es cancelar, no se carga la siguiente clase.
				if(typeAnswer == ButtonData.CANCEL_CLOSE) {
					return; //termina la ejecucion de este metodo.
				}
			} 

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
			checkChanges = true; //Establece a true la comprobación de cambios sin guardar.
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
			if(cambiosConfigClase()) {
				conexionBD.actualizarClase(clase);
				claseOriginal.setHoraClase(clase.getHoraClase());
				claseOriginal.setTipo(clase.getTipo());
				claseOriginal.setAnotaciones(clase.getAnotaciones());
			}

			if(cambiosListaClase()) {
				conexionBD.actualizarAlumnosEnClase(new ArrayList<Alumno>(listaClase), clase.getId());
				claseOriginal.setListaAlumnos(new ArrayList<Alumno>(listaClase));
			}

			checkChanges = false; //Establece a false la comprobación de cambios sin guardar.
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
		//Comprueba si hay cambios en la clase actual sin guardar para preguntar si se quiere guardar los cambios.
		if (checkChanges && (cambiosConfigClase() || cambiosListaClase())) {
			ButtonData typeAnswer = saveForgottenChanges();
			//Si la respuesta es cancelar, se mantiene la vista de clase.
			if (typeAnswer == ButtonData.CANCEL_CLOSE) {
				return; //Termina la ejecución de este metodo.
			}
		}

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/jornadaVista.fxml"));
			BorderPane jornadaPilates;
			jornadaPilates = (BorderPane) loader.load();
			controladorPincipal.setPane(jornadaPilates);

			JornadaControlador controller = loader.getController(); //cargo el controlador.
			controller.setControladorPrincipal(controladorPincipal);
			controller.setListaAlumnos(listadoAlumnosGeneral);
			controller.setListaGruposAlumnosGeneral(listadoGruposAlumnosGeneral);
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
	 * Comprueba si ha habido cambios en la configuración de la clase en comparación con la configuración original.
	 *
	 * @return true si ha habido cambios, false si no ha habido cambios.
	 */
	private boolean cambiosConfigClase() {
		//Comprueba si la hora de la clase ha cambiado o si el tipo de clase ha cambiado o si las anotaciones de la clase han cambiado.
		if (claseOriginal.getHoraClase() != clase.getHoraClase() 
				|| claseOriginal.getTipo() != clase.getTipo()
				|| claseOriginal.getAnotaciones() != clase.getAnotaciones()) {
			return true; //Si alguno de los atributos ha cambiado, devuelve true.
		}

		//Si no ha habido cambios en ninguno de los atributos, devuelve false.
		return false;
	}


	/**
	 * Comprueba si ha habido cambios en la lista de alumnos inscritos en la clase en comparación con la lista original.
	 *
	 * @return true si ha habido cambios, false si no ha habido cambios.
	 */
	private boolean cambiosListaClase() {
		//Comprueba que los alumnos de la lista de clase actual esten en la lista de clase original.
		for(Alumno alumno : listaClase) {
			if(!claseOriginal.estaInscrito(alumno)) {
				return true; //Si un alumno de la lista actual no está inscrito en la lista original, devuelve true.
			}
		}

		//Comprueba que los alumnos de la lista de clase original esten en la lista de clase actual.
		for(Alumno alumno : claseOriginal.getListaAlumnos()) {
			if(!listaClase.contains(alumno)) {
				return true; //Si un alumno de la lista original no está en la lista actual, devuelve true.
			}
		}

		//Si no ha habido cambios en la lista de alumnos, devuelve false.
		return false;
	}


	/**
	 * Muestra una alerta para confirmar si se desean guardar los cambios sin guardar.
	 *
	 * @return El tipo de respuesta del usuario (Si, No o Cancelar).
	 */
	private ButtonData saveForgottenChanges() {
		alerta = new Alert(AlertType.CONFIRMATION);
		alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de// estilos.
		alerta.setTitle("Cambios sin guardar");
		alerta.setHeaderText("Hay cambios sin guardar en esta clase.");
		alerta.setContentText("¿Quieres guardar los cambios?");
		alerta.initStyle(StageStyle.DECORATED);
		alerta.initOwner((Stage) bpClase.getScene().getWindow()); //Establece la ventana propietaria de la alerta.

		//Define los botones de la alerta.
		ButtonType buttonTypeConfirmar  = new ButtonType("Si", ButtonData.YES);
		ButtonType buttonTypeDescartar   = new ButtonType("No", ButtonData.NO);
		ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
		alerta.getButtonTypes().setAll(buttonTypeConfirmar, buttonTypeDescartar , buttonTypeCancel);
		
		//Muestra la alerta y espera la respuesta del usuario.
		Optional<ButtonType> result = alerta.showAndWait();
		ButtonData typeAnswer = result.get().getButtonData();

		if (typeAnswer == ButtonData.YES) {
			//Si el usuario selecciona "Si", guarda los cambios y devuelve la respuesta.
			guardar(null);
			return typeAnswer;
		} else if(typeAnswer == ButtonData.NO) {
			//Si el usuario selecciona "No", devuelve la respuesta.
			return typeAnswer;
		} else {
			//Si el usuario selecciona "Cancelar", devuelve la respuesta.
			return typeAnswer;
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
			checkChanges = true; //Establece a true la comprobación de cambios sin guardar.

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
	 * Establece en el ComboBox cbHora las horas que puede ser seleccionadas.
	 * 
	 * @param numClase numero de clase para la que se van a establecer las horas posibles.
	 */
	private void configurarComboBoxHoras(int numClase) {
		HoraClase minHora; 
		HoraClase maxHora;
		int posHoraMin;
		int posHoraMax;

		if(numeroClase > 0) {
			minHora = jornada.getClase(numeroClase - 1).getHoraClase();
			posHoraMin = minHora.ordinal() +1;

		} else {
			minHora = jornada.getClase(numeroClase).getHoraClase();
			posHoraMin = 0;
		}

		if(numeroClase < 7) {
			maxHora = jornada.getClase(numeroClase +1).getHoraClase();
			posHoraMax = maxHora.ordinal() -1;
		} else {
			maxHora = jornada.getClase(numeroClase).getHoraClase();
			posHoraMax = HoraClase.values().length -1;
		}

		ArrayList<HoraClase> listHoras = new ArrayList<HoraClase>();
		for(HoraClase hora : HoraClase.values()) {
			if(hora.ordinal() >= posHoraMin && hora.ordinal() <= posHoraMax) {
				listHoras.add(hora);
			}
		}
		cbHora.setItems(FXCollections.observableArrayList(listHoras));
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
		
		configurarComboBoxHoras(clase.getNumero()); //Configura las horas que puede escoger en la clase actual.
		lbNumeroClase.setText(Integer.toString(clase.getNumero()));
		lbIdClase.setText(Integer.toString(clase.getId()));
		cbHora.setValue(clase.getHoraClase());
		cbTipo.setValue(clase.getTipo());

		taAnotaciones.textProperty().bindBidirectional(clase.anotacionesProperty());
		
		clase.anotacionesProperty().addListener( (observable, oldValue, newValue) -> {
			checkChanges = !newValue.equals(claseOriginal.getAnotaciones()); //Establece si hay cambios en las anotaciones respecto a la clase original.
		});
		
		cbHora.getSelectionModel().selectedItemProperty().addListener( (o, ov, nv) -> {
			if(nv != null) {
				this.clase.horaClaseProperty().set(nv);
				checkChanges = !nv.equals(claseOriginal.getHoraClase()); // Establece si hay cambios en la hora de clase respecto a la clase original.
			}
		});
		
		cbTipo.getSelectionModel().selectedItemProperty().addListener( (o, ov, nv) -> {
			this.clase.tipoProperty().set(nv);
			checkChanges = !nv.equals(claseOriginal.getTipo()); //Establece si hay cambios en el tipo de clase respecto a la clase original.
		});

		checkChanges = false; //Establece a false la comprobación de cambios sin guardar.

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


	/**
     * Establece la lista de grupos de alumnos para este controlador.
     * LLama al metodo que configura el ComboBox cbGrupoAlumnos.
     * 
     * @param listaGrupos La lista de grupos de alumnos a establecer.
     */
    public void setListaGruposAlumnosGeneral(ObservableList<GrupoAlumnos> listaGrupos) {
        listadoGruposAlumnosGeneral = listaGrupos;
        configurarCbGrupoAlumnos();
    }
}
