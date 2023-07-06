package controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

import baseDatos.ConexionBD;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modelo.Alumno;
import modelo.EstadoAlumno;
import modelo.Genero;
import modelo.Toast;
import javafx.fxml.Initializable;

public class AlumnosControlador implements Initializable {

    private FilteredList<Alumno> filtro;
    private ObservableList<Alumno> listadoAlumnos;
    private DateTimeFormatter formatter;
    private ConexionBD conexionBD;
    private PrincipalControlador controladorPincipal;
    private Toast toast;
    private Stage escenario;

    
    @FXML
    private ImageView ivLupa;

    @FXML
    private Button btnBorrar;

    @FXML
    private Button btnModificar;

    @FXML
    private Button btnNuevo;

    @FXML
    private Button btnVer;

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
    	//Cargar imagenes en ImageView.
        Image imagenLupa;
        try {
            imagenLupa = new Image(getClass().getResourceAsStream("/recursos/lupa_lila_2_128.png")); //Forma desde IDE y JAR.
        } catch (Exception e) {
        	imagenLupa = new Image("/recursos/lupa_lila_2_128.png"); //Forma desde el JAR.
        }
        ivLupa.setImage(imagenLupa);
        
        btnBorrar.getStyleClass().add("boton_rojo");

        conexionBD = ConexionBD.getInstance();
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
        
        //Mostamos los datos en la columna Fecha Compra Formateados.
        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");//Formato dd/MM/yy
        colFechaNacimiento.setCellValueFactory(cellData -> {
        	//LocalDate fechaText = cellData.getValue().getFechaNacimiento();
        	//return cellData.getValue().fechaNacimientoProperty().asString(fechaText.format(formatter).toString());

            LocalDate fechaText = cellData.getValue().getFechaNacimiento();
            return cellData.getValue().fechaNacimientoProperty().asString(Integer.toString(Period.between(fechaText, LocalDate.now()).getYears()));
        });

        //Con esto la busqueda es automatica al insertar texto en el tfBusqueda.
        tfBusqueda.textProperty().addListener( (o, ov, nv) -> {
        	filtro.setPredicate(obj -> {
        		if (obj.getNombre().toLowerCase().contains(nv.toLowerCase())) return true;
        		else return false;
        		
        	});
        });

        //Binding de los label de conteo datos del listadoAlumnos.
        //lbNumTotalAlumnos.textProperty().bind(listadoAlumnos.filtered(p -> p.getGenero().toString().equals("HOMBRE")));
        //IntegerProperty tamLista = new SimpleIntegerProperty(listadoAlumnos.size());
        //lbNumTotalAlumnos.textProperty().bind(tamLista.asString());

    }


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
            alerta.initOwner(escenario);
            alerta.initModality(Modality.APPLICATION_MODAL);

            Optional<ButtonType> result = alerta.showAndWait();
    		if (result.get() == ButtonType.OK) {
                try {
                    if(conexionBD.borrarAlumno(alumnoSeleccionado)) {
                        listadoAlumnos.remove(alumnoSeleccionado);
                        toast.show(escenario, "Alumno eliminado!!.");
                    }
                } catch (SQLException e) {
                    // meter en log.
                    alerta = new Alert(Alert.AlertType.ERROR);
                    alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
                    alerta.setTitle("Borrar Alumno");
                    alerta.setContentText("No se ha podido borrar el Alumno.");
                    alerta.initStyle(StageStyle.DECORATED);
                    alerta.initOwner(escenario);
                    alerta.initModality(Modality.APPLICATION_MODAL);
                    alerta.showAndWait();
                    
                    e.printStackTrace();
                }
    			
    		} else {
    			
    		}
        } 
    }

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
                ventana.initOwner(escenario);
                ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
                ventana.initStyle(StageStyle.UNDECORATED);
    
                controller.setStage(ventana);
                controller.setAlumno(alumnoSeleccionado);
    
                Scene scene = new Scene(cardAlumno);
                scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); //Añade hoja de estilos.
                ventana.setScene(scene);
                ventana.showAndWait();
            } catch (IOException e) {
                // TODO Auto-generated catch block
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
                ventana.initOwner(escenario);
                ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
                ventana.initStyle(StageStyle.DECORATED);
                 
                URL rutaIcono = getClass().getResource("/recursos/lis_logo_1.png"); // guardar ruta de recurso imagen.
                ventana.getIcons().add(new Image(rutaIcono.toString())); // poner imagen icono a la ventana.
                
                controller.setStage(ventana);
                controller.modoFormulario(controller.MODO_EDITAR_ALUMNO);
                controller.setAlumno(alumnoSeleccionado);
    
                Scene scene = new Scene(formAlumno);
                scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); //Añade hoja de estilos.
                ventana.setScene(scene);
                ventana.setTitle("Editar Alumno");
                ventana.showAndWait();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            setupDatosTalba(); //configuro los bindign para que se actualice los labels de informacion de la tableViev.
        }
        
    }

    @FXML
    void nuevoAlumno(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/alumnoFormVista.fxml"));
		    GridPane FormAlumno;
            FormAlumno = (GridPane) loader.load();
            AlumnoFormControlador controller = loader.getController(); // cargo el controlador.
            
            Stage ventana= new Stage();
            ventana.initOwner(escenario);
            ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
            ventana.initStyle(StageStyle.DECORATED);

            URL rutaIcono = getClass().getResource("/recursos/lis_logo_1.png"); // guardar ruta de recurso imagen.
            ventana.getIcons().add(new Image(rutaIcono.toString())); // poner imagen icono a la ventana.

            controller.setStage(ventana);
            controller.modoFormulario(controller.MODO_NUEVO_ALUMNO);
            controller.setListaAlumnos(listadoAlumnos);

            Scene scene = new Scene(FormAlumno);
            scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); //Añade hoja de estilos.
            ventana.setScene(scene);
            ventana.setTitle("Nuevo Alumno");
            ventana.showAndWait();
        } catch (IOException e) {
            // TODO Auto-generated catch block
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
        toast.show(escenario, "No hay seleccionado ningun Alumno!!.");
        return i; //i = -1
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

        lbNumHombres.textProperty()
                .bind(Bindings.createStringBinding(() -> String.format("%d", hombresCount.get()), hombresCount));

        lbNumMujeres.textProperty()
                .bind(Bindings.createStringBinding(() -> String.format("%d", mujeresCount.get()), mujeresCount));

        lbNumAlumnosActivos.textProperty()
                .bind(Bindings.createStringBinding(() -> String.format("%d", activosCount.get()), activosCount));

        lbNumAlumnosNoActivos.textProperty()
                .bind(Bindings.createStringBinding(() -> String.format("%d", bajasCount.get()), bajasCount));

    }


    /**
     * Establece un Stage para este controlador.
     * 
     * @param s Stage que se establece.
     */
    public void setStage(Stage stage) {
    	this.escenario = stage;
    }
    
}
