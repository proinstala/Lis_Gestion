package controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import baseDatos.ConexionBD;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import modelo.Alumno;
import modelo.GrupoAlumnos;
import modelo.ModoFormulario;
import modelo.Usuario;
import utilidades.Constants;
import utilidades.Toast;

public class GruposAlumnosControlador implements Initializable {
    
	private PrincipalControlador controladorPincipal;

	private ObservableList<Alumno> listadoAlumnosGeneral;
    private ObservableList<GrupoAlumnos> listadoGruposAlumnosGeneral;
	private ObservableList<Alumno> listaAlumnosGrupo;
    private ObservableList<GrupoAlumnos> listadoGruposAlumnosCopia;

    private GrupoAlumnos grupoAlumnosSeleccionado;
    private ObservableList<Alumno> listaAlumnosGrupoSeleccionado;

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
    private boolean cambiosEnListasAlumnosGrupos = false;
    private boolean cambiosEnGrupos = false;

    private Stage thisEstage;
    

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
        configurarControles();
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

    private void configurarListView() {
        lvGrupos.setItems(listadoGruposAlumnosCopia);  
        lvGrupos.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            grupoAlumnosSeleccionado = nv;
            listaAlumnosGrupoSeleccionado = grupoAlumnosSeleccionado.getListaAlumnosObservable();
            lvAlumnosGrupo.setItems(listaAlumnosGrupoSeleccionado);
            
            lbNumeroAlumnosGrupo.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("%d", listaAlumnosGrupoSeleccionado.size()), listaAlumnosGrupoSeleccionado));
        });
    }


    private void configurarControles() {
        ivFlechaQuitar.setOnMouseClicked(e -> {
            borrarAlumnoGrupo();
        });

        ivFlechaAdd.setOnMouseClicked(e -> {
            addAlumnoGrupo();
        });

        ivVolver.setOnMouseClicked(e -> {
            volver();
        });

        ivGuardar.setOnMouseClicked(e -> {
            guardarCambios();
        });

        ivAddGrupo.setOnMouseClicked(e -> {
            formNuevoGrupo();
        });

        ivBorrarGrupo.setOnMouseClicked(e -> {
            borrarGrupo();
        });
        
    }

    private void formNuevoGrupo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/grupoAlumnosFormVista.fxml"));
		    GridPane FormGrupoAlumnos;
            FormGrupoAlumnos = (GridPane) loader.load();
            GrupoAlumnosFormControlador controller = loader.getController(); // cargo el controlador.
            
            Stage ventana= new Stage();
            ventana.initOwner(thisEstage);
            ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
            ventana.initStyle(StageStyle.DECORATED);
            ventana.setMinWidth(400);   //Ancho mínimo de ventana.
            ventana.setMinHeight(300);  //Alto mínimo de venta.

            URL rutaIcono = getClass().getResource("/recursos/lis_logo_1.png"); // guardar ruta de recurso imagen.
            ventana.getIcons().add(new Image(rutaIcono.toString())); // poner imagen icono a la ventana.

            controller.modoFormulario(ModoFormulario.CREAR_DATOS);
            controller.setListaGruposAlumnosCopia(listadoGruposAlumnosCopia);
            controller.configurar(ventana);

            Scene scene = new Scene(FormGrupoAlumnos);
            scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); //Añade hoja de estilos.
            ventana.setScene(scene);
            ventana.setTitle("Nuevo Grupo Alumnos");
            ventana.showAndWait();

            checkChanges = true;
        } catch (IOException e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        }	
    }


    private void borrarAlumnoGrupo() {
        int i = lvAlumnosGrupo.getSelectionModel().getSelectedIndex(); //Guardo el indice del elemento seleccionado en la lista.
        
        if(i != -1) {
            Alumno alumno = lvAlumnosGrupo.getSelectionModel().getSelectedItem(); //Obtengo el alumno seleccionado.
            lvAlumnosGrupo.getItems().remove(alumno);
            
            checkChanges = true; //Establece a true la comprobación de cambios sin guardar.
        } else {
            toast.show(thisEstage, "No has seleccionado ningún Alumno del Grupo..");
        }
    }

    private void borrarGrupo() {
        if(!grupoSeleccionadoValido()) {
            toast.show(thisEstage, "No has seleccionado un Grupo!.");
            return;
        }
        int i = lvGrupos.getSelectionModel().getSelectedIndex(); //Guardo el indice del elemento seleccionado en la lista.
        
        if(i != -1) {
            GrupoAlumnos grupo = lvGrupos.getSelectionModel().getSelectedItem(); //Obtengo el alumno seleccionado.
            
            grupo.setNombre(grupo.getNombre() + "  -borrado-");
            grupo.getListaAlumnosObservable().clear();

            actualizarVistaGrupos();
            
            checkChanges = true; //Establece a true la comprobación de cambios sin guardar.
        } else {
            toast.show(thisEstage, "No has seleccionado ningún Grupo!");
        }
    }

    private void actualizarVistaGrupos() {
        lvGrupos.setCellFactory(listView -> new ListCell<GrupoAlumnos>() {
            @Override
            protected void updateItem(GrupoAlumnos grupo, boolean empty) {
                super.updateItem(grupo, empty);
        
                if (empty || grupo == null) {
                    setText(null);
                    setGraphic(null);
                } else { 
                    setText(grupo.toString()); // Asigna el texto de la celda
                
                    if(grupo.getNombre().endsWith("-borrado-")) {
                        setStyle("-fx-text-fill: #cc0000; -fx-font-weight: bold; -fx-background-color: #FFF0F0;");
                        //setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                        //setStyle("-fx-strikethrough: 'true'; -fx-background-color: #FF0000;"); // Cambia el color de fondo a rojo
                    }
                }
            }
        });
    }

   

    private void addAlumnoGrupo() {
        if(!grupoSeleccionadoValido()) {
            toast.show(thisEstage, "No has seleccionado un Grupo!.");
            return;
        }

        int i = tvAlumnos.getSelectionModel().getSelectedIndex(); //Guardo el indice del elemento seleccionado en la lista.
        if(i != -1) {
            Alumno alumno = tvAlumnos.getSelectionModel().getSelectedItem(); //Obtengo el alumno seleccionado.

            if(lvAlumnosGrupo.getItems().contains(alumno)) {
                toast.show(thisEstage, "El alumno ya esta inscrito el el grupo selccionado..");
            } else if(lvAlumnosGrupo.getItems().add(alumno)) {
                checkChanges = true; //Establece a true la comprobación de cambios sin guardar.
                toast.show(thisEstage, "Alumno añadido al grupo " + grupoAlumnosSeleccionado.getNombre() + ".");
            } else {
                toast.show(thisEstage, "Fallo al agregar Alumno a Grupo.");
            }
        } else {
            toast.show(thisEstage, "No has seleccionado ningún Alumno.");
        }
    }

    private boolean grupoSeleccionadoValido() {
        
        if(grupoAlumnosSeleccionado != null && !grupoAlumnosSeleccionado.getNombre().endsWith("-borrado-")) {
            return true;
        } else {
            return false;
        }   
    }


    public void guardarCambios() {
        try {
            Boolean guardado = false;
            // cambiosEnListasAlumnosGrupos = false;
            // cambiosEnGrupos = false;
            // if(cambiosEnGrupos)
            if(true) {
                //ConexionBD.actualizarGruposAlumnos()
                //Guardar los cambios de grupos(nuevos, editados)
                guardado = true;

                // Utiliza un iterador para recorrer la lista y eliminar los grupos que cumplen la condición.
                var iterator = listadoGruposAlumnosCopia.iterator();
                while (iterator.hasNext()) {
                    GrupoAlumnos grupo = iterator.next();
                    if (grupo.getNombre().endsWith("-borrado-")) {
                        iterator.remove();
                    }
                }
                actualizarVistaGrupos();

                listadoGruposAlumnosGeneral.clear(); //Vaciamos la lista general.
                
                listadoGruposAlumnosGeneral.addAll(listadoGruposAlumnosCopia); //Añadimos todos los objetos de la lista copia a la general.
                
                //Actualizamos la lista de alumnos de cada grupo para que tengan los de la aplicacion(La lista observable contiene los cambios).
                for (GrupoAlumnos grupo : listadoGruposAlumnosGeneral) {
                    grupo.setListaAlumnos(new ArrayList<>(grupo.getListaAlumnosObservable()));
                }


               
            }
            /* 
            if(cambiosListasAlumnosGrupos || cambiosListaAlumnosGrupo()) {
                //guardar en base de datos
                //Si guardad sin fallos, entoces hacer operacion de guardar a nivel de aplicacion.
                for(GrupoAlumnos grupoCopia : listadoGruposAlumnosCopia) {
                    for(GrupoAlumnos grupoOriginal : listadoGruposAlumnosGeneral) {
                        if(grupoOriginal.getId() == grupoCopia.getId()) {
                            grupoOriginal.setListaAlumnos(new ArrayList<>(grupoCopia.getListaAlumnosObservable()));
                            break;
                        }
                    }
                }
                guardado = true;
            }
            */
            if(guardado) {
                toast.show(thisEstage, "Cambios guardados!");
            }

            cambiosEnListasAlumnosGrupos = false;
            cambiosEnGrupos = false;
            checkChanges = false;
        } catch (Exception e) {
			logUser.severe("Excepción: " + e.toString());
			e.printStackTrace();
		}
    }

    /**
	 * Se encarga de cargar la vista de Alumnos.
	 *
	 */
	private void volver() {
		//Comprueba si hay cambios en la clase actual sin guardar para preguntar si se quiere guardar los cambios.
        cambiosEnListasAlumnosGrupos = cambiosListaAlumnosGrupo();
        cambiosEnGrupos = cambiosListaGrupos();
		if (checkChanges && (cambiosEnListasAlumnosGrupos || cambiosEnGrupos)) {
			ButtonData typeAnswer = saveForgottenChanges();
			//Si la respuesta es cancelar, se mantiene la vista de clase.
			if (typeAnswer == ButtonData.CANCEL_CLOSE) {
				return; //Termina la ejecución de este metodo.
			}
		}

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/alumnosVista.fxml"));
			BorderPane alumnos;
			alumnos = (BorderPane) loader.load();
			controladorPincipal.setPane(alumnos);

			AlumnosControlador controller = loader.getController(); //cargo el controlador.
			controller.setListaAlumnosGeneral(listadoAlumnosGeneral);
            controller.setListaGruposAlumnosGeneral(listadoGruposAlumnosGeneral);
            controller.setUsuarioActual(usuario);
            controller.setControladorPrincipal(controladorPincipal);
			
		} catch (IOException e) {
			logUser.severe("Excepción: " + e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logUser.severe("Excepción: " + e.toString());
			e.printStackTrace();
		}
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
		alerta.setHeaderText("Hay cambios sin guardar en los Grupos.");
		alerta.setContentText("¿Quieres guardar los cambios?");
		alerta.initStyle(StageStyle.DECORATED);
		alerta.initOwner(thisEstage); //Establece la ventana propietaria de la alerta.

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
			guardarCambios();
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
	 * Comprueba si ha habido cambios en la lista de alumnos inscritos en la clase en comparación con la lista original.
	 *
	 * @return true si ha habido cambios, false si no ha habido cambios.
	 */
	private boolean cambiosListaAlumnosGrupo() {
        for(GrupoAlumnos grupo : listadoGruposAlumnosCopia) {
            //Comprueba que los alumnos de la lista observable del grupo esten en la lista del grupo original.
            for(Alumno alumno : grupo.getListaAlumnosObservable()) {
                if(!grupo.estaInscrito(alumno)) {
                    return true; //Si un alumno de la lista observable no está inscrito en la lista original, devuelve true.
                }
            }

            //Comprueba que los alumnos de la lista original del grupo esten en la lista observable del grupo.
            for(Alumno alumno : grupo.getListaAlumnos()) {
                if(!grupo.getListaAlumnosObservable().contains(alumno)) {
                    return true; //Si un alumno de la lista original no está en la lista observable, devuelve true.
                }
            }
        }
		
		//Si no ha habido cambios en la lista de alumnos, devuelve false.
		return false;
	}

  
    private boolean cambiosListaGrupos() {
        for(var grupoCopia : listadoGruposAlumnosCopia) {
            if(grupoCopia.getId() == -1 || grupoCopia.getNombre().endsWith("-borrado-")) {
                return true;
            }
            for(var grupoOriginal : listadoGruposAlumnosGeneral) {
                if(grupoOriginal.getId() == grupoCopia.getId()) {
                    if(!grupoOriginal.getNombre().equals(grupoCopia.getNombre()) || !grupoOriginal.getDescripcion().equals(grupoCopia.getDescripcion())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    /**
	 * Establece la lista de Alumnos.
	 * 
	 * @param lista La lista de donde se obtienen los Alumnos.
	 */
	public void setListaAlumnosGeneral(ObservableList<Alumno> lista) {
		listadoAlumnosGeneral = lista;
		filtro = new FilteredList<Alumno>(listadoAlumnosGeneral); //Inicio el filtro pasandole el listado de alumnos.
		tvAlumnos.setItems(filtro); //Añado la lista de alumnos TextView tvAlumnos.
	}


    /**
     * Establece la lista de grupos de alumnos para este controlador.
     * 
     * @param listaGrupos La lista de grupos de alumnos a establecer.
     */
    public void setListaGruposAlumnosGeneral(ObservableList<GrupoAlumnos> listaGrupos) {
        listadoGruposAlumnosGeneral = listaGrupos;
        listadoGruposAlumnosCopia = FXCollections.observableArrayList();
        
        //Hace una copia de cada grupo de listadoGruposAlumnosGeneral y lo asigna a listadoGruposAlumnosCopia
        for(GrupoAlumnos g : listadoGruposAlumnosGeneral) {
            GrupoAlumnos grupoCopia = new GrupoAlumnos(g);
            //g.setListaAlumnosObservable(FXCollections.observableArrayList(g.getListaAlumnos()));
            listadoGruposAlumnosCopia.add(grupoCopia);
        }
        
        configurarListView();
    }


    /**
	 * Etablece el usuario que esta usando la aplicación.
     * 
	 * @param usuario
	 */
	public void setUsuarioActual(Usuario usuarioActual) {
		this.usuario = usuarioActual;
	}


    /**
	 * Establece para este controlador, el controlador principal de la aplicacion.
	 * 
	 * @param principal Controlador principal.
	 */
	public void setControladorPrincipal(PrincipalControlador principal) {
		controladorPincipal = principal;
	}

    public void configurar() {
        thisEstage = (Stage) bpGruposAlumnos.getScene().getWindow(); //Obtengo el Stage para mostrar Toast.
    }

}
