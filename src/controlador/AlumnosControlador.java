package controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import baseDatos.ConexionBD;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import modelo.Alumno;
import modelo.EstadoAlumno;
import modelo.Genero;
import modelo.Usuario;
import utilidades.Constants;
import utilidades.Toast;
import javafx.fxml.Initializable;

public class AlumnosControlador implements Initializable {

    private final String ORDEN_ID = "ID";
    private final String ORDEN_NOMBRE = "NOMBRE";
    private final String ORDEN_LOCALIDAD = "LOCALIDAD";
    private final String ORDEN_ESTADO = "ESTADO";
    private final String ORDEN_GENERO = "GENERO";

    private FilteredList<Alumno> filtro;
    private ObservableList<Alumno> listadoAlumnos;
    private DateTimeFormatter formatter;
    private ConexionBD conexionBD;
    private Logger logUser;
    private Toast toast;
    private Usuario usuario;

    
    @FXML
    private BorderPane bpAlumnos;

    @FXML
    private AnchorPane apFiltro;

    @FXML
    private ImageView ivLupa;

    @FXML
    private ImageView ivNotificacion;

    @FXML
    private Button btnBorrar;

    @FXML
    private Button btnModificar;

    @FXML
    private Button btnNuevo;

    @FXML
    private Button btnVer;

    @FXML
    private ComboBox<String> cbEstado;

    @FXML
    private ComboBox<String> cbOrden;

    @FXML
    private ComboBox<String> cbGenero;

    @FXML
    private ComboBox<String> cbLocalidad;

    @FXML
    private ComboBox<String> cbModoFiltro;

    @FXML
    private Label lbNumAlumnosActivos;

    @FXML
    private Label lbNumAlumnosNoActivos;

    @FXML
    private Label lbNumHombres;

    @FXML
    private Label lbNumMujeres;

    @FXML
    private Label lbNumTotalAlumnos;

    @FXML
    private Label lbMediaEdad;

    @FXML
    private TextField tfBusqueda;

    @FXML
    private TableColumn<Alumno, String> colApellido1;

    @FXML
    private TableColumn<Alumno, String> colApellido2;

    @FXML
    private TableColumn<Alumno, String> colEmail;

    @FXML
    private TableColumn<Alumno, String> colFechaNacimiento;

    @FXML
    private TableColumn<Alumno, Number> colGenero;

    @FXML
    private TableColumn<Alumno, Number> colId;

    @FXML
    private TableColumn<Alumno, String> colLocalidad;

    @FXML
    private TableColumn<Alumno, String> colNombre;

    @FXML
    private TableColumn<Alumno, Number> colTelefono;

    @FXML
    private TableColumn<Alumno, Number> colAsistencia;

    @FXML
    private TableColumn<Alumno, String> colEstado;

    @FXML
    private TableView<Alumno> tvAlumnos;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	btnBorrar.getStyleClass().add("boton_rojo"); //Añadir clases de estilo CSS a elementos.
        apFiltro.getStyleClass().add("panel_border");

        //Cargar imagenes en ImageView.
        Image imagenLupa;
        Image notificacion;
        try {
            //Intentar cargar la imagen desde el recurso en el IDE y en el JAR.
            imagenLupa = new Image(getClass().getResourceAsStream("/recursos/lupa_lila_2_48.png")); //Forma desde IDE y JAR.
            notificacion = new Image(getClass().getResourceAsStream("/recursos/notificacion_42px.png"));
        } catch (Exception e) {
            //Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR.
        	imagenLupa = new Image("/recursos/lupa_lila_2_48.png"); //Forma desde el JAR.
            notificacion = new Image("/recursos/notificacion_42px.png");
        }
        //Establecer las imagenes cargadas en el ImageView.
        ivLupa.setImage(imagenLupa); 
        ivNotificacion.setImage(notificacion);

        //Establecer Tooltip.
        Tooltip tltNotificacion = new Tooltip("Enviar Notificación");
        tltNotificacion.setShowDelay(Duration.seconds(0.5)); //Establecer retardo de aparición.
        Tooltip.install(ivNotificacion, tltNotificacion);      //Establecer Tooltip a ImageView.
        

        logUser = Logger.getLogger(Constants.USER); //Crea una instancia de la clase Logger asociada al nombre de registro.
        conexionBD = ConexionBD.getInstance();      //Obtener una instancia de la clase ConexionBD utilizando el patrón Singleton.
        toast = new Toast();

        //Asigno a cada columna de la tabla los campos del modelo.
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido1.setCellValueFactory(new PropertyValueFactory<>("apellido1"));
        colApellido2.setCellValueFactory(new PropertyValueFactory<>("apellido2"));
        colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAsistencia.setCellValueFactory(new PropertyValueFactory<>("asistenciaSemanal"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        colLocalidad.setCellValueFactory(cellData -> {
            //ObservableValue<String> localidad = cellData.getValue().getDireccion().localidadProperty();
            ObservableValue<String> localidad = cellData.getValue().direccionProperty().get().localidadProperty();
            return localidad;
        });
        
        
        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); //Crear un formateador de fecha con el patrón "dd/MM/yyyy".
        
        //Mostamos los datos en la columna Fecha Compra Formateados. Se muestra la edad, no la fecha de nacimiento.
        colFechaNacimiento.setCellValueFactory(cellData -> {
        	//LocalDate fechaText = cellData.getValue().getFechaNacimiento();
        	//return cellData.getValue().fechaNacimientoProperty().asString(fechaText.format(formatter).toString());

            LocalDate fechaText = cellData.getValue().getFechaNacimiento();
            return cellData.getValue().fechaNacimientoProperty().asString(Integer.toString(Period.between(fechaText, LocalDate.now()).getYears()));
        });


        //Configura el ComboBox cbLocalidad
        cbLocalidadSetup();

        //cbGenero
        ObservableList<String> listadoGenero = FXCollections.observableArrayList();
        listadoGenero.setAll(Genero.HOMBRE.toString(), Genero.MUJER.toString(), "AMBOS");
        cbGenero.setItems(listadoGenero);
        cbGenero.setValue("AMBOS"); //Valor inicial.

        //Configurar Listener para el ComboBox cbGenero.
        cbGenero.setOnAction(e -> {
            tfBusqueda.clear();
            configurarFiltro("");
        });

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

        //Configura el ComboBox cbOrden.
        ObservableList<String> listadoOrden = FXCollections.observableArrayList();
        listadoOrden.setAll(ORDEN_ID, ORDEN_NOMBRE, ORDEN_LOCALIDAD, ORDEN_ESTADO, ORDEN_GENERO);
        cbOrden.setItems(listadoOrden);
        cbOrden.setValue("ID"); //Valor inicial.

        //Configurar Listener para el ComboBox cbOrdenar.
        cbOrden.setOnAction(e -> {
            ordenarListaAlumnos();
        });
    }

    
    /**
     * Método para manejar el evento que lanza el formulario para enviar notificaciones.
     * Se invoca al hacer clic en el ImageView ivNotificacion.
     *
     * @param event El evento de mouse que desencadena la acción.
     */
    @FXML
    void abrirNotificaciones(MouseEvent event) {
        int i = indiceSeleccionado();
        if(i != -1) {
            Alumno alumnoSeleccionado = tvAlumnos.getSelectionModel().getSelectedItem();

            if(comprobarRequisitosNotificacion(alumnoSeleccionado)) {
                try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/alumnoFormNotificacionVista.fxml"));
                GridPane formNotificacion;
                formNotificacion = (GridPane) loader.load();
                AlumnoFormNotificacionControlador controller = loader.getController(); // cargo el controlador.
                
                Stage ventana= new Stage();
                ventana.initOwner((Stage) bpAlumnos.getScene().getWindow());
                ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
                ventana.initStyle(StageStyle.DECORATED);

                URL rutaIcono = getClass().getResource("/recursos/lis_logo_1.png"); // guardar ruta de recurso imagen.
                ventana.getIcons().add(new Image(rutaIcono.toString())); // poner imagen icono a la ventana.
    
                controller.setModelos(alumnoSeleccionado, usuario);
    
                Scene scene = new Scene(formNotificacion);
                scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); //Añade hoja de estilos.
                ventana.setScene(scene);
                ventana.setTitle("Notificación Alumno");
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
    }


    /**
     * Comprueba los requisitos necesarios para enviar una notificación al alumno.
     * Verifica si el email de la aplicación y el email del alumno están configurados correctamente.
     * Muestra un mensaje de Informcion si no cumple los requisitos.
     *
     * @param alumnoNotificacion El objeto Alumno al que se desea enviar la notificación.
     * @return true si todos los requisitos están cumplidos y la notificación se puede enviar, false en caso contrario.
     */
    private boolean comprobarRequisitosNotificacion(Alumno alumnoNotificacion) {
        if (usuario.getEmailApp() == null || usuario.getEmailApp().isBlank()) {
            mensajeAviso(AlertType.INFORMATION, 
                "Fallo Notificación", 
                "Email Aplicación NO configurado.", 
                "El usuario no tiene configurado el Email Aplicación.");
            return false;
        } else if (alumnoNotificacion.getEmail() == null || alumnoNotificacion.getEmail().isBlank()) {
            mensajeAviso(AlertType.INFORMATION, 
                "Fallo Notificación", 
                "Email de Alumno NO registrado.", 
                "El Alumno no tiene email registrado en aplicación.");
            return false;
        }
        return true;
    }

    
    /**
     * Método para manejar el evento de borrado de un alumno.
     * Se muestra un diálogo de confirmación antes de borrar el alumno seleccionado.
     * Si el usuario confirma, se borra el alumno y se actualiza la lista de alumnos.
     *
     * @param event El evento del ratón que activó el método.
     */
    @FXML
    void borrarAlumno(MouseEvent event) {
        int i = indiceSeleccionado();
        Alert alerta;
        if(i != -1) {
            Alumno alumnoSeleccionado = tvAlumnos.getSelectionModel().getSelectedItem();
            alerta = new Alert(AlertType.CONFIRMATION);
            alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
            alerta.setTitle("Borrar Alumno");
            alerta.setHeaderText("¿Seguro que quieres borrar el alumno seleccionado?");
            alerta.setContentText("Esto borrará la Información del Alumno, las mensualidades asociadas"
                    + "\na este alumno y al alumno de todas las clases que este apuntado.\n" 
                    +"\nAlumno:  (" + alumnoSeleccionado.getId() + ")  " + alumnoSeleccionado.getNombreCompleto());
            alerta.initStyle(StageStyle.DECORATED);
            alerta.initOwner((Stage) bpAlumnos.getScene().getWindow());
            alerta.initModality(Modality.APPLICATION_MODAL);

            //Mostrar el diálogo de alerta y esperar la respuesta del usuario.
            Optional<ButtonType> result = alerta.showAndWait();
    		if (result.get() == ButtonType.OK) {
                try {
                    if(conexionBD.borrarAlumno(alumnoSeleccionado)) {
                        listadoAlumnos.remove(alumnoSeleccionado);
                        logUser.config("Eliminado Alumno. " + alumnoSeleccionado.toString());
                        toast.show((Stage) bpAlumnos.getScene().getWindow(), "Alumno eliminado!!.");
                    } else {
                        logUser.warning("Fallo al eliminar Alumno. " + alumnoSeleccionado.toString());
                    }
                } catch (SQLException e) {
                    logUser.severe("Excepción: " + e.toString());
                    e.printStackTrace();
                    mensajeAviso(
                        AlertType.ERROR,
                        "Borrar Alumno",
                        "",
                        "No se ha podido borrar el Alumno.");

                } catch (Exception e) {
                    logUser.severe("Excepción: " + e.toString());
                    e.printStackTrace();
                }	
    		} 
        } 
    }


    /**
     * Método para manejar el evento de visualización de un alumno.
     * Se muestra una ventana modal con la información del alumno seleccionado.
     *
     * @param event El evento del ratón que activó el método.
     */
    @FXML
    void verAlumno(MouseEvent event) {
        int i = indiceSeleccionado();
        if(i != -1) {
            Alumno alumnoSeleccionado = tvAlumnos.getSelectionModel().getSelectedItem();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/cardAlumnoVista.fxml"));
                AnchorPane cardAlumno;
                cardAlumno = (AnchorPane) loader.load();
                CardAlumnoControlador controller = loader.getController(); // cargo el controlador.
                
                Stage ventana= new Stage();
                ventana.initOwner((Stage) bpAlumnos.getScene().getWindow());
                ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
                ventana.initStyle(StageStyle.UNDECORATED);
    
                controller.setAlumno(alumnoSeleccionado);
    
                Scene scene = new Scene(cardAlumno);
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


    @FXML
    void modificarAlumno(MouseEvent event) {
        int i = indiceSeleccionado();
        if(i != -1) {
            Alumno alumnoSeleccionado = tvAlumnos.getSelectionModel().getSelectedItem();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/alumnoFormVista.fxml"));
                GridPane formAlumno;
                formAlumno = (GridPane) loader.load();
                AlumnoFormControlador controller = loader.getController(); // cargo el controlador.
                
                Stage ventana= new Stage();
                ventana.initOwner((Stage) bpAlumnos.getScene().getWindow());
                ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
                ventana.initStyle(StageStyle.DECORATED);
                 
                URL rutaIcono = getClass().getResource("/recursos/lis_logo_1.png"); // guardar ruta de recurso imagen.
                ventana.getIcons().add(new Image(rutaIcono.toString())); // poner imagen icono a la ventana.
                
                //controller.setStage(ventana);
                controller.modoFormulario(controller.MODO_EDITAR_ALUMNO);
                controller.setAlumno(alumnoSeleccionado);
    
                Scene scene = new Scene(formAlumno);
                scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); //Añade hoja de estilos.
                ventana.setScene(scene);
                ventana.setTitle("Editar Alumno");
                ventana.showAndWait();
            } catch (IOException e) {
                logUser.severe("Excepción: " + e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                logUser.severe("Excepción: " + e.toString());
                e.printStackTrace();
            }	
            setupDatosTalba(); //configuro los bindign para que se actualice los labels de informacion de la tableViev.
        }
    }


    /**
     * Método para manejar el evento de modificación de un alumno.
     * Se muestra un formulario para editar la información del alumno seleccionado.
     *
     * @param event El evento del ratón que activó el método.
     */
    @FXML
    void nuevoAlumno(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/alumnoFormVista.fxml"));
		    GridPane FormAlumno;
            FormAlumno = (GridPane) loader.load();
            AlumnoFormControlador controller = loader.getController(); // cargo el controlador.
            
            Stage ventana= new Stage();
            ventana.initOwner((Stage) bpAlumnos.getScene().getWindow());
            ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
            ventana.initStyle(StageStyle.DECORATED);

            URL rutaIcono = getClass().getResource("/recursos/lis_logo_1.png"); // guardar ruta de recurso imagen.
            ventana.getIcons().add(new Image(rutaIcono.toString())); // poner imagen icono a la ventana.

            controller.modoFormulario(controller.MODO_NUEVO_ALUMNO);
            controller.setListaAlumnos(listadoAlumnos);

            Scene scene = new Scene(FormAlumno);
            scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); //Añade hoja de estilos.
            ventana.setScene(scene);
            ventana.setTitle("Nuevo Alumno");
            ventana.showAndWait();
        } catch (IOException e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        }	
    }    


    /**
     * Devuelve el indice seleccionado de la lista cargada en la tableView.
     * 
     * @return Número entero representativo del indice seleccionado en la tableView.
     */
    private int indiceSeleccionado() {
        int i = tvAlumnos.getSelectionModel().getSelectedIndex(); //Obtengo el indice del elemento seleccionado en la lista.
        if(i != -1) {
            return i; //número del indice seleccionado.
        }
        toast.show((Stage) bpAlumnos.getScene().getWindow(), "No hay seleccionado ningun Alumno!!.");
        return i; //i = -1
    }


    /**
     * Configura los bindign para que se actualice los labels de informacion de la tableViev.
     * 
     */
    private void setupDatosTalba() {
        IntegerBinding hombresCount = Bindings.createIntegerBinding(
                () -> (int) filtro.stream().filter(alumno -> alumno.getGenero() == Genero.HOMBRE).count(), filtro);

        IntegerBinding mujeresCount = Bindings.createIntegerBinding(
                () -> (int) filtro.stream().filter(alumno -> alumno.getGenero() == Genero.MUJER).count(), filtro);

        IntegerBinding activosCount = Bindings.createIntegerBinding(
                () -> (int) filtro.stream().filter(alumno -> alumno.getEstado() == EstadoAlumno.ACTIVO).count(), filtro);

        IntegerBinding bajasCount = Bindings.createIntegerBinding(
                () -> (int) filtro.stream().filter(alumno -> alumno.getEstado() == EstadoAlumno.BAJA).count(), filtro);

        DoubleBinding edadCount = Bindings.createDoubleBinding(
            () -> (Double) filtro.stream().mapToDouble(Alumno::getEdad).sum(), filtro);


        lbNumHombres.textProperty()
                .bind(Bindings.createStringBinding(() -> String.format("%d", hombresCount.get()), hombresCount));

        lbNumMujeres.textProperty()
                .bind(Bindings.createStringBinding(() -> String.format("%d", mujeresCount.get()), mujeresCount));

        lbNumAlumnosActivos.textProperty()
                .bind(Bindings.createStringBinding(() -> String.format("%d", activosCount.get()), activosCount));

        lbNumAlumnosNoActivos.textProperty()
                .bind(Bindings.createStringBinding(() -> String.format("%d", bajasCount.get()), bajasCount));
        
        lbMediaEdad.textProperty()
                .bind(Bindings.createStringBinding(() -> String.format("%.1f", (edadCount.get() / filtro.size())), edadCount));
    }


    /**
     * Configura el filtro para la tabla de alumno según los criterios seleccionados.
     *
     * @param texto El texto de búsqueda utilizado para filtrar.
     */
    private void configurarFiltro(String texto) {
        filtro.setPredicate(obj -> {

            if ( !(cbGenero.getValue().equals("AMBOS")) && !(obj.generoProperty().getValue().toString().equals(cbGenero.getValue())) ) {
                return false;
            }
            
            if ( !(cbLocalidad.getValue().equals("TODAS")) && !(obj.getDireccion().localidadProperty().getValue().toString().equals(cbLocalidad.getValue())) ) {
                return false;
            }

            if ( !(cbEstado.getValue().equals("TODOS")) && !(obj.estadoProperty().getValue().toString().equals(cbEstado.getValue())) ) {
                return false;
            }
        		
            if (cbModoFiltro.getValue() == "Nombre Alumno") {
                for(Alumno alumno : listadoAlumnos) {
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
            case ORDEN_ID -> {
                comparador = Comparator.comparingInt(Alumno::getId);
            }

            case ORDEN_NOMBRE -> {
                comparador = Comparator.comparing(Alumno::getNombre).thenComparing(Alumno::getApellido1).thenComparing(Alumno::getApellido2);
            }

            case ORDEN_LOCALIDAD -> {
                comparador = Comparator.comparing((Alumno alumno) -> alumno.getDireccion().getLocalidad()).thenComparing(Alumno::getNombre).thenComparing(Alumno::getApellido1).thenComparing(Alumno::getApellido2);
            }

            case ORDEN_ESTADO -> {
                comparador = Comparator.comparing(Alumno::getEstado).thenComparing(Alumno::getNombre).thenComparing(Alumno::getApellido1).thenComparing(Alumno::getApellido2);
            }

            case ORDEN_GENERO -> {
                comparador = Comparator.comparing(Alumno::getGenero).thenComparing(Alumno::getNombre).thenComparing(Alumno::getApellido1).thenComparing(Alumno::getApellido2);
            }

            default -> {
                comparador = Comparator.comparingInt(Alumno::getId);
            }
        }
        
        Collections.sort(listadoAlumnos, comparador); //Odena la lista de alumnos "listadoAlumnos" segun los criterios seleccionados.
    }


    /**
     * Configura el ComboBox cbLocalidad.
     * 
     */
    private void cbLocalidadSetup() {
        ObservableList<String> listadoLocalidades = null;
        try {
			listadoLocalidades = FXCollections.observableArrayList(conexionBD.getLocalidades());

            listadoLocalidades.add("TODAS");

            //Carga en el ComboBox los items del listadoLocalidades.
            cbLocalidad.setItems(listadoLocalidades);
            cbLocalidad.setValue("TODAS"); //Valor inicial.

            cbLocalidad.setOnAction(e -> {
                tfBusqueda.clear();
                configurarFiltro("");
            });
		} catch (Exception e) {
            logUser.severe("Excepción: " + e.toString());
			e.printStackTrace();
		}
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
        Alert alerta = new Alert(tipo);
        alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
        alerta.setTitle(tiutlo);
        alerta.initOwner((Stage) bpAlumnos.getScene().getWindow());
        alerta.setHeaderText(cabecera);
        alerta.setContentText(cuerpo);
        alerta.initStyle(StageStyle.DECORATED);
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.showAndWait();
    }


    /**
	 * Establece la lista de Alumnos.
	 * 
	 * @param lista La lista de donde se obtienen los Alumnos.
	 */
	public void setListaAlumnos(ObservableList<Alumno> lista) {
        listadoAlumnos = lista; //Guado la lista pasada a la lista de Clasecontrolador.
		//tvAlumnos.setItems(listadoAlumnos);
		filtro = new FilteredList<Alumno>(listadoAlumnos); //Inicio el filtro pasandole el listado de alumnos.
		tvAlumnos.setItems(filtro); //Añado la lista de alumnos TextView tvAlumnos.
        
        IntegerProperty tamLista = new SimpleIntegerProperty(filtro.size());
        lbNumTotalAlumnos.textProperty().bind(tamLista.asString());

        //Listener para obtener el tamaño de la lista
        filtro.addListener((ListChangeListener<Alumno>) c -> {
            while (c.next()) {
                tamLista.set(filtro.size());
            }
        });

        setupDatosTalba(); //configuro los bindign para que se actualice los labels de informacion de la tableViev.
	}

    /**
	 * Etablece el usuario que esta usando la aplicación.
     * 
	 * @param usuario
	 */
	public void setUsuarioActual(Usuario usuarioActual) {
		this.usuario = usuarioActual;
	}
}
