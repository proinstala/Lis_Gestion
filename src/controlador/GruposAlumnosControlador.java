package controlador;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import baseDatos.ConexionBD;
import javafx.fxml.Initializable;
import javafx.beans.binding.Bindings;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import modelo.Alumno;
import modelo.EstadoAlumno;
import modelo.GrupoAlumnos;
import modelo.ModoFormulario;
import modelo.ModoOrdenTablaAlumno;
import modelo.Usuario;
import utilidades.Constants;
import utilidades.Toast;

public class GruposAlumnosControlador implements Initializable {
    
	private PrincipalControlador controladorPincipal;

	private ObservableList<Alumno> listadoAlumnosGeneral;
    private ObservableList<GrupoAlumnos> listadoGruposAlumnosGeneral;
    private ObservableList<GrupoAlumnos> listadoGruposAlumnosCopia;

    private GrupoAlumnos grupoAlumnosSeleccionado;
    private ObservableList<Alumno> listaAlumnosGrupoSeleccionado;

    private FilteredList<Alumno> filtro;

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

    @FXML
    private ComboBox<String> cbEstado;

    @FXML
    private ComboBox<String> cbModoFiltro;

    @FXML
    private ComboBox<ModoOrdenTablaAlumno> cbOrden;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Añadir clases de estilo CSS a elementos.
        lvGrupos.getStyleClass().add("mi_list-view");
        lvAlumnosGrupo.getStyleClass().add("mi_list-view");
        ivFlechaAdd.getStyleClass().add("iv_resaltado");
        ivFlechaQuitar.getStyleClass().add("iv_resaltado");
        ivVolver.getStyleClass().add("iv_resaltado");
        ivGuardar.getStyleClass().add("iv_resaltado");
        ivAddGrupo.getStyleClass().add("iv_resaltado");
        ivBorrarGrupo.getStyleClass().add("iv_resaltado");
        ivEditarGrupo.getStyleClass().add("iv_resaltado");

        logUser = Logger.getLogger(Constants.USER); //Crea una instancia de la clase Logger asociada al nombre de registro.
        conexionBD = ConexionBD.getInstance();      //Obtener una instancia de la clase ConexionBD utilizando el patrón Singleton.
        toast = new Toast();

        decimalFormat = new DecimalFormat("#0.00");

        configurarBotonesImageView();
        configurarTabla(); 
        configurarControles();
    }


    /**
     * Carga imágenes en los componentes ImageView y configura Tooltips para mejorar la interfaz de usuario.
     * Cada ImageView representa un botón gráfico en la UI, y los Tooltips proporcionan información adicional
     * sobre la función de cada botón cuando el usuario pasa el cursor sobre ellos.
     */
    private void configurarBotonesImageView() {
        //Declaración de variables para las imágenes.
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


    /**
     * Configura las columnas para mostrar los datos de los objetos,
     * estableciendo la relación entre los campos del modelo y cada columna visual en la tabla.
     */
    private void configurarTabla() {
        //Asigno a cada columna de la tabla los campos del modelo.
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido1.setCellValueFactory(new PropertyValueFactory<>("apellido1"));
        colAsistencias.setCellValueFactory(new PropertyValueFactory<>("asistenciaSemanal"));
        colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
		colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        //Configura la columna de edad para calcular la edad a partir de la fecha de nacimiento.
        //Esto utiliza un CellValueFactory personalizado para transformar la fecha de nacimiento en edad.
		colEdad.setCellValueFactory(cellData -> {
        	//LocalDate fechaText = cellData.getValue().getFechaNacimiento();
        	//return cellData.getValue().fechaNacimientoProperty().asString(fechaText.format(formatter).toString());

            LocalDate fechaText = cellData.getValue().getFechaNacimiento();
            //Calcula la edad como el número de años entre la fecha de nacimiento y la fecha actual.
            return cellData.getValue().fechaNacimientoProperty().asString(Integer.toString(Period.between(fechaText, LocalDate.now()).getYears()));
        });

        /* 
        //Con esto la busqueda es automatica al insertar texto en el tfBusqueda.
        tfBusqueda.textProperty().addListener( (o, ov, nv) -> {
        	filtro.setPredicate(obj -> {
        		if (obj.getNombre().toLowerCase().contains(nv.toLowerCase())) {return true;}
        		else {return false;}
        	});
        });
        */
    }

    /**
     * Configura el ListView de grupos de alumnos y establece un listener para actualizar
     * la lista de alumnos y el conteo de alumnos en base al grupo seleccionado.
     */
    private void configurarListView() {
        lvGrupos.setItems(listadoGruposAlumnosCopia);  

        //Añade un listener al propiedad selectedItem del ListView de grupos.
        //Este listener reacciona cuando un usuario selecciona un grupo diferente de la lista.
        lvGrupos.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            grupoAlumnosSeleccionado = nv; //Actualiza la referencia al grupo de alumnos seleccionado con el nuevo valor.
            
            //Obtiene la lista observable de alumnos pertenecientes al grupo seleccionado.
            listaAlumnosGrupoSeleccionado = grupoAlumnosSeleccionado.getListaAlumnosObservable();
            //Establece la lista de alumnos del grupo seleccionado como items del ListView lvAlumnosGrupo.
            lvAlumnosGrupo.setItems(listaAlumnosGrupoSeleccionado);
            
            //Vincula el texto del label lbNumeroAlumnosGrupo para mostrar el número total de alumnos en el grupo seleccionado.
            //Utiliza un binding para mantener el texto actualizado automáticamente.
            lbNumeroAlumnosGrupo.textProperty().bind(Bindings.createStringBinding(
                () -> String.format("%d", listaAlumnosGrupoSeleccionado.size()), listaAlumnosGrupoSeleccionado));
        });
    }


    /**
     * Configura los controles de la interfaz de usuario asignando acciones a los eventos de clic y estableciendo
     * configuraciones iniciales para ComboBox y TextField. Cada ImageView tiene una acción específica que se ejecuta
     * al hacer clic, y los ComboBox se configuran con opciones específicas para filtrar o buscar en la lista de alumnos.
     */
    private void configurarControles() {
        //Asigna acciones a eventos de clic en los ImageView que actúan como botones.
        ivFlechaQuitar.setOnMouseClicked(e -> borrarAlumnoGrupo());
        ivFlechaAdd.setOnMouseClicked(e -> addAlumnoGrupo());
        ivVolver.setOnMouseClicked(e -> volver());
        ivGuardar.setOnMouseClicked(e -> guardarCambios());
        ivAddGrupo.setOnMouseClicked(e -> abrirFormularioGrupoAlumnos(ModoFormulario.CREAR_DATOS));
        ivEditarGrupo.setOnMouseClicked(e -> abrirFormularioGrupoAlumnos(ModoFormulario.EDITAR_DATOS));
        ivBorrarGrupo.setOnMouseClicked(e -> borrarGrupo());

        //Configura el ComboBox cbEstado con las opciones de estado de los alumnos.
        ObservableList<String> listadoEstado = FXCollections.observableArrayList();
        listadoEstado.setAll("TODOS", EstadoAlumno.ACTIVO.toString(), EstadoAlumno.BAJA.toString());
        cbEstado.setItems(listadoEstado);
        cbEstado.setValue("TODOS"); //Valor inicial.

        //Configura un Listener para el ComboBox cbEstado que limpia el campo de búsqueda y aplica el filtro seleccionado.
        cbEstado.setOnAction(e -> {
            tfBusqueda.clear();
            configurarFiltro("");
        });

        //Configura el ComboBox cbModoFiltro con las opciones de tipo de búsqueda.
        ObservableList<String> tipoBusqueda = FXCollections.observableArrayList();
        tipoBusqueda.setAll("Id Alumno", "Nombre Alumno");
        cbModoFiltro.setItems(tipoBusqueda);
        cbModoFiltro.setValue("Nombre Alumno"); //Valor inicial.

         //Configura un Listener para el ComboBox cbModoFiltro que limpia el campo de búsqueda y aplica el modo de filtro seleccionado.
        cbModoFiltro.setOnAction(e -> {
            tfBusqueda.clear();
            configurarFiltro("");
        });

        //Configura un Listener para el TextField tfBusqueda que aplica un filtro basado en el texto introducido por el usuario.
        tfBusqueda.textProperty().addListener( (o, ov, nv) -> configurarFiltro(nv));

        //Configura el ComboBox cbOrden con las opciones de ordenamiento para la lista de alumnos.
        cbOrden.setItems(FXCollections.observableArrayList(ModoOrdenTablaAlumno.values()));
        cbOrden.setValue(ModoOrdenTablaAlumno.ID); //Valor inicial.

        // Configura un Listener para el ComboBox cbOrden que aplica el ordenamiento seleccionado a la lista de alumnos.
        cbOrden.setOnAction(e -> ordenarListaAlumnos());
    }

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
            
            if ( !(cbLocalidad.getValue().equals("TODAS")) && !(obj.getDireccion().localidadProperty().getValue().toString().equals(cbLocalidad.getValue())) ) {
                return false;
            }
            */
            if ( !(cbEstado.getValue().equals("TODOS")) && !(obj.estadoProperty().getValue().toString().equals(cbEstado.getValue())) ) {
                return false;
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
     * Ordena la colección de alumnos (listadoAlumnos) utilizando un comparador basado en el criterio de ordenamiento seleccionado.
     *
     */
    private void ordenarListaAlumnos() {
        //Crea el comparador para ordenar la lista de alumnos "listadoAlumnos" por el criterio seleccionado.
        Comparator<Alumno> comparador = null;

        switch (cbOrden.getValue()) {
            case ID -> {
                comparador = Comparator.comparingInt(Alumno::getId);
            }

            case NOMBRE -> {
                comparador = Comparator.comparing(Alumno::getNombre).thenComparing(Alumno::getApellido1).thenComparing(Alumno::getApellido2);
            }

            case LOCALIDAD -> {
                comparador = Comparator.comparing((Alumno alumno) -> alumno.getDireccion().getLocalidad()).thenComparing(Alumno::getNombre).thenComparing(Alumno::getApellido1).thenComparing(Alumno::getApellido2);
            }

            case ESTADO -> {
                comparador = Comparator.comparing(Alumno::getEstado).thenComparing(Alumno::getNombre).thenComparing(Alumno::getApellido1).thenComparing(Alumno::getApellido2);
            }

            case GENERO -> {
                comparador = Comparator.comparing(Alumno::getGenero).thenComparing(Alumno::getNombre).thenComparing(Alumno::getApellido1).thenComparing(Alumno::getApellido2);
            }

            default -> {
                comparador = Comparator.comparingInt(Alumno::getId);
            }
        }
        
        Collections.sort(listadoAlumnosGeneral, comparador); //Odena la lista de alumnos "listadoAlumnos" segun los criterios seleccionados.
    }


    /**
     * Abre un formulario para crear o editar un grupo de alumnos, dependiendo del modo especificado.
     * Si el modo es editar, primero verifica que haya un grupo seleccionado.
     *
     * @param modo El modo en que se abrirá el formulario, indicando si se creará un nuevo grupo de alumnos
     *             o se editará uno existente.
     */
    private void abrirFormularioGrupoAlumnos(ModoFormulario modo) {
        try {
            //Verifica que, si el modo es EDITAR_DATOS, haya un grupo seleccionado válido.
            if(modo.equals(ModoFormulario.EDITAR_DATOS) && !grupoSeleccionadoValido()) {
                toast.show(thisEstage, "No has seleccionado un Grupo!.");
                return; //Termina la ejecución si no hay un grupo válido seleccionado en modo de edición.
            }

            //Carga el formulario de grupo de alumnos desde el archivo FXML.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/grupoAlumnosFormVista.fxml"));
		    GridPane FormGrupoAlumnos;
            FormGrupoAlumnos = (GridPane) loader.load();
            GrupoAlumnosFormControlador controller = loader.getController(); //Obtiene el controlador asociado al formulario FXML cargado.
            
            //Crea una nueva ventana para el formulario y configura sus propiedades.
            Stage ventana= new Stage();
            ventana.initOwner(thisEstage); //Establece la ventana actual como propietaria de la nueva ventana.
            ventana.initModality(Modality.APPLICATION_MODAL); // Establece la ventana como modal (bloquear las ventanas de detras).
            ventana.initStyle(StageStyle.DECORATED);
            ventana.setMinWidth(400);   //Establece el ancho mínimo de la ventana.
            ventana.setMinHeight(300);  //Establece el alto mínimo de la ventana.

            //Establece el icono de la ventana.
            URL rutaIcono = getClass().getResource("/recursos/lis_logo_1.png"); 
            ventana.getIcons().add(new Image(rutaIcono.toString())); 

            //Configura el controlador con el modo del formulario y, si es edición, el grupo a editar.
            controller.modoFormulario(modo);
            if(modo.equals(ModoFormulario.EDITAR_DATOS)) {
                controller.setGrupoAlumnos(grupoAlumnosSeleccionado);
            }
            
            controller.setListaGruposAlumnosCopia(listadoGruposAlumnosCopia);
            controller.configurar(ventana);

            //Establece la escena para la ventana y muestra el formulario esperando a que se cierre.
            Scene scene = new Scene(FormGrupoAlumnos);
            scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); //Añade hoja de estilos.
            ventana.setScene(scene);
            ventana.setTitle(modo.getAccion() + " Grupo Alumnos");
            ventana.showAndWait();

            actualizarVistaGrupos(); //Actualiza la vista de grupos de alumnos después de cerrar el formulario.
            checkChanges = true;     //Indica que puede haber cambios que necesitan ser guardados.
        } catch (IOException e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        }	
    }


    /**
     * Elimina el alumno seleccionado del grupo actual. Este método se encarga de verificar si hay
     * un alumno seleccionado en el ListView lvAlumnosGrupo y, de ser así, eliminarlo de la lista.
     * También actualiza una bandera para indicar que ha habido cambios en la lista de alumnos.
     */
    private void borrarAlumnoGrupo() {
        int i = lvAlumnosGrupo.getSelectionModel().getSelectedIndex(); //Obtiene el índice del alumno seleccionado en el ListView.
        
        //Verifica si hay un alumno seleccionado (índice diferente de -1).
        if(i != -1) {
            Alumno alumno = lvAlumnosGrupo.getSelectionModel().getSelectedItem(); //Obtiene el alumno seleccionado del ListView.
            lvAlumnosGrupo.getItems().remove(alumno); //Elimina el alumno seleccionado de la lista de items del ListView.
            
            checkChanges = true; //Establece a true la comprobación de cambios sin guardar.
        } else {
            //Muestra un mensaje de advertencia si no hay un alumno seleccionado.
            toast.show(thisEstage, "No has seleccionado ningún Alumno del Grupo.");
        }
    }


    /**
     * Marca como eliminado el grupo de alumnos seleccionado.
     * Verifica primero si hay un grupo válido seleccionado. Si no es así, muestra un mensaje de advertencia.
     * En caso de haber un grupo seleccionado, modifica su nombre para reflejar que ha sido eliminado
     * y limpia la lista de alumnos asociada al grupo.
     */
    private void borrarGrupo() {
        //Verifica si hay un grupo seleccionado válido. Si no lo hay, muestra un mensaje de advertencia.
        if(!grupoSeleccionadoValido()) {
            toast.show(thisEstage, "No has seleccionado un Grupo!.");
            return; //Termina la ejecución del método si no hay un grupo seleccionado válido.
        }
        int indiceSeleccionado = lvGrupos.getSelectionModel().getSelectedIndex(); //Obtiene el índice del grupo seleccionado en el ListView.
        
        //Verifica si el índice es válido (diferente de -1) para asegurar que haya una selección.
        if(indiceSeleccionado != -1) {
            GrupoAlumnos grupo = lvGrupos.getSelectionModel().getSelectedItem(); //Obtiene el grupo seleccionado basado en el índice.
            
            //Modifica el nombre del grupo para indicar que ha sido "borrado" y limpia su lista de alumnos.
            grupo.setNombre(grupo.getNombre() + "  -borrado-"); //NOTA: la marca -borrado- se usa para comprobar la accion que se tiene que realizar cuando se esta actualizando la BD.
            grupo.getListaAlumnosObservable().clear(); //Limpia la lista de alumnos del grupo.
            
            //Actualiza la vista de grupos para reflejar los cambios hechos.
            actualizarVistaGrupos();
            
            checkChanges = true; //Establece a true la comprobación de cambios sin guardar.
        } else {
            //Muestra un mensaje de advertencia si no hay un grupo seleccionado.
            toast.show(thisEstage, "No has seleccionado ningún Grupo!");
        }
    }

    /**
     * Actualiza y personaliza la visualización de los grupos de alumnos en el ListView lvGrupos.
     * Los grupos marcados como borrados se mostrarán con un estilo visual diferente para indicar su estado.
     */
    private void actualizarVistaGrupos() {
        //Establece un nuevo CellFactory para el ListView lvGrupos para personalizar la visualización de cada celda.
        lvGrupos.setCellFactory(listView -> new ListCell<GrupoAlumnos>() {
            @Override
            protected void updateItem(GrupoAlumnos grupo, boolean empty) {
                super.updateItem(grupo, empty);
        
                if (empty || grupo == null) {
                    setText(null);
                    setGraphic(null);
                } else { 
                    //textProperty().bind(grupo.nombreProperty());
                    setText(grupo.toString()); // Asigna el texto de la celda
                  
                    //Si el nombre del grupo termina en "-borrado-", aplica un estilo específico para indicar que el grupo está borrado.
                    if(grupo.getNombre().endsWith("-borrado-")) {
                        setStyle("-fx-text-fill: #cc0000; -fx-font-weight: bold; -fx-background-color: #FFF0F0;");
                        //setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                        //setStyle("-fx-strikethrough: 'true'; -fx-background-color: #FF0000;"); // Cambia el color de fondo a rojo
                    } else {
                        // Asegura que los estilos se resetean para grupos que no están marcados como borrados.
                        setStyle(""); // Restablece el estilo por defecto si el grupo no está marcado como borrado.
                    }
                }
            }
        });
    }

    /**
     * Añade un alumno seleccionado en el TableView de alumnos disponibles al grupo de alumnos seleccionado.
     * Verifica primero si hay un grupo seleccionado y luego si el alumno ya está en el grupo.
     */
    private void addAlumnoGrupo() {
        //Verifica si hay un grupo de alumnos seleccionado válido.
        if(!grupoSeleccionadoValido()) {
            toast.show(thisEstage, "No has seleccionado un Grupo!.");
            return; //Sale del método si no hay un grupo seleccionado.
        }

        int i = tvAlumnos.getSelectionModel().getSelectedIndex(); //Obtiene el índice del alumno seleccionado en el TableView.
        
        //Verifica si hay un alumno seleccionado (índice diferente de -1).
        if(i != -1) {
            Alumno alumnoSeleccionado = tvAlumnos.getSelectionModel().getSelectedItem(); //Obtiene el alumno seleccionado del TableView.

            //Verifica si el alumno ya está inscrito en el grupo seleccionado.
            if(lvAlumnosGrupo.getItems().contains(alumnoSeleccionado)) {
                toast.show(thisEstage, "El alumno ya esta inscrito el el grupo selccionado..");
            } else if(lvAlumnosGrupo.getItems().add(alumnoSeleccionado)) {
                checkChanges = true; //Establece a true la comprobación de cambios sin guardar.
                toast.show(thisEstage, "Alumno añadido al grupo " + grupoAlumnosSeleccionado.getNombre() + ".");
            } else {
                toast.show(thisEstage, "Fallo al agregar Alumno a Grupo.");
            }
        } else {
            //Notifica al usuario si no ha seleccionado ningún alumno para añadir al grupo.
            toast.show(thisEstage, "No has seleccionado ningún Alumno.");
        }
    }

    /**
     * Verifica si el grupo de alumnos seleccionado es válido.
     * Un grupo es considerado válido si existe (no es null) y no ha sido marcado como borrado.
     *
     * @return boolean Verdadero si el grupo seleccionado es válido, falso de lo contrario.
     */
    private boolean grupoSeleccionadoValido() {
        
        //Comprueba si hay un grupo seleccionado y si su nombre no termina con "-borrado-".
        //Esto implica que el grupo existe y no ha sido marcado como borrado.
        if(grupoAlumnosSeleccionado != null && !grupoAlumnosSeleccionado.getNombre().endsWith("-borrado-")) {
            return true; //Devuelve verdadero si el grupo seleccionado es válido.
        } else {
            return false; //Devuelve falso si no hay un grupo seleccionado o si ha sido marcado como borrado.
        }   
    }


    /**
     * Guarda los cambios realizados a los grupos de alumnos tanto en la base de datos como en la interfaz de usuario.
     * Esto incluye actualizar los grupos nuevos y editados en la base de datos, eliminar los grupos marcados como borrados,
     * y sincronizar las listas de grupos en la aplicación.
     */
    public void guardarCambios() {
        try {
            Boolean guardado = false; //Bandera para indicar si los cambios han sido guardados correctamente.

            //Intenta actualizar los grupos de alumnos en la base de datos.
            if(conexionBD.actualizarGruposAlumnos(new ArrayList<>(listadoGruposAlumnosCopia))) {
                guardado = true; //Indica que la actualización en la base de datos fue exitosa.

                //Utiliza un iterador para eliminar de la lista copia los grupos marcados como borrados.
                var iterator = listadoGruposAlumnosCopia.iterator();
                while (iterator.hasNext()) {
                    GrupoAlumnos grupo = iterator.next();
                    if (grupo.getNombre().endsWith("-borrado-")) {
                        iterator.remove(); //Elimina el grupo marcado como borrado.
                    }
                }

                //Actualiza la vista de grupos para reflejar los cambios realizados.
                actualizarVistaGrupos();

                //Limpia la lista general de grupos y la repuebla con los elementos de la lista copia actualizada.
                listadoGruposAlumnosGeneral.clear(); //Vaciamos la lista general.
                listadoGruposAlumnosGeneral.addAll(listadoGruposAlumnosCopia); //Añadimos todos los objetos de la lista copia a la general.
                
                //Actualizamos la lista de alumnos de cada grupo para que tengan los de la aplicacion(La lista observable contiene los cambios).
                for (GrupoAlumnos grupo : listadoGruposAlumnosGeneral) {
                    grupo.setListaAlumnos(new ArrayList<>(grupo.getListaAlumnosObservable()));
                }
            }
            
            //Muestra un mensaje al usuario indicando si los cambios se guardaron correctamente o no.
            if(guardado) {
                toast.show(thisEstage, "Cambios guardados!");
            } else {
                toast.show(thisEstage, "Error al intentar guardar los cambios!");
            }

            //Restablece las banderas de control de cambios a false, indicando que no hay cambios pendientes.
            cambiosEnListasAlumnosGrupos = false;
            cambiosEnGrupos = false;
            checkChanges = false;
        } catch (Exception e) {
			logUser.severe("Excepción: " + e.toString());
			e.printStackTrace();
		}
    }

    /**
     * Maneja el retorno a la vista anterior, comprobando primero si hay cambios sin guardar.
     * Si se detectan cambios sin guardar, se solicita al usuario que decida qué hacer con esos cambios.
     * Dependiendo de la decisión del usuario, la aplicación puede guardar los cambios, cancelar la operación
     * de retorno, o descartar los cambios y continuar con el retorno.
     */
	private void volver() {
        //Comprueba si hay cambios sin guardar en las listas de alumnos por grupo o en la lista de grupos.
        cambiosEnListasAlumnosGrupos = cambiosListaAlumnosGrupo();
        cambiosEnGrupos = cambiosListaGrupos();
		if (checkChanges && (cambiosEnListasAlumnosGrupos || cambiosEnGrupos)) {
			ButtonData typeAnswer = saveForgottenChanges();
            //Si el usuario decide cancelar, se detiene la operación de retorno para permitir al usuario decidir sobre los cambios.
			if (typeAnswer == ButtonData.CANCEL_CLOSE) {
				return; //Termina la ejecución del método y no continúa con el retorno.
			}
		}

		try {
            //Carga la vista de alumnos desde un archivo FXML y la establece en el panel principal.
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/alumnosVista.fxml"));
			BorderPane alumnos = (BorderPane) loader.load(); //Carga la vista de alumnos.
			controladorPincipal.setPane(alumnos); //Establece la nueva vista en el panel principal.

            //Obtiene el controlador de la vista de alumnos y configura sus datos iniciales.
			AlumnosControlador controller = loader.getController(); 
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

  
    /**
     * Verifica si ha habido cambios en la lista de grupos de alumnos que requieran ser guardados.
     * Esto incluye la adición de nuevos grupos, la eliminación de grupos existentes, y modificaciones en los detalles de los grupos.
     *
     * @return boolean Verdadero si se han detectado cambios en la lista de grupos, falso de lo contrario.
     */
    private boolean cambiosListaGrupos() {
        for(var grupoCopia : listadoGruposAlumnosCopia) {
            //Verifica si un grupo es nuevo(id=-1) o ha sido marcado como borrado(-borrado-) pero no cumpla las 2 condiciones a la vez.
            //Si cumple las 2 condiciones a la vez no hay que tenerlo en cuenta para activar "cambiosListaGrupos".
            if(grupoCopia.getId() == -1 || grupoCopia.getNombre().endsWith("-borrado-")) {
                if(!(grupoCopia.getId() == -1 && grupoCopia.getNombre().endsWith("-borrado-"))) {
                    return true; //Indica que hay cambios si se cumple alguna de las condiciones pero no ambas.
                }
            }

            //Compara los grupos de la lista copia con los de la lista original para detectar cambios en el nombre o la descripción.
            for(var grupoOriginal : listadoGruposAlumnosGeneral) {
                if(grupoOriginal.getId() == grupoCopia.getId()) {
                    //Si el nombre o la descripción han cambiado, indica que hay cambios.
                    if(!grupoOriginal.getNombre().equals(grupoCopia.getNombre()) || !grupoOriginal.getDescripcion().equals(grupoCopia.getDescripcion())) {
                        return true;
                    }
                }
            }
        }

        return false; //Devuelve falso si no se detectan cambios en la lista de grupos.
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
            grupoCopia.setListaAlumnosObservable(FXCollections.observableArrayList(g.getListaAlumnos()));
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
