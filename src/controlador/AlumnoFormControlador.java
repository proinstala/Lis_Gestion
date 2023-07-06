package controlador;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import baseDatos.ConexionBD;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.LocalDateStringConverter;
import javafx.util.converter.NumberStringConverter;
import modelo.Alumno;
import modelo.Direccion;
import modelo.EstadoAlumno;
import modelo.FormaPago;
import modelo.Genero;
import modelo.Toast;
import javafx.fxml.Initializable;

public class AlumnoFormControlador implements Initializable {

    public final String MODO_NUEVO_ALUMNO = "CREAR";
    public final String MODO_EDITAR_ALUMNO = "EDITAR";

    private String modoControlador;
    DateTimeFormatter formatter;
    private ConexionBD conexionBD;
    private Toast toast;
    private Stage escenario;
    private ObservableList<String> listadoProvincias;
    private ObservableList<String> listadoLocalidades;
    private ObservableList<Alumno> listadoAlumnos;
    private Alumno oldAlumno;
    private Alumno newAlumno;

    private String nombre;
    private String apellido1;
    private String apellido2;
    private Genero genero;
    private LocalDate fechaNac;
    
    private int telefono;
    private String email;

    private String calle;
    private int numeroVivienda;
    private String localidad;
    private String provincia;
    private int codigoPostal;

    private EstadoAlumno estado;
    private FormaPago formaPago;
    private int asistenciaSemanal;


    @FXML
    private GridPane gpFormAlumno;

    @FXML
    private Pane pSeparador;
    
    @FXML
    private Label lbTitulo;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnConfirmacion;

    @FXML
    private ComboBox<EstadoAlumno> cbEstado;

    @FXML
    private ComboBox<Genero> cbGenero;

    @FXML
    private ComboBox<String> cbLocalidad;

    @FXML
    private ComboBox<String> cbProvincia;

    @FXML
    private ComboBox<Integer> cbAsistencia;

    @FXML
    private ComboBox<FormaPago> cbFormaPago;

    @FXML
    private DatePicker dpFechaNacimiento;

    @FXML
    private ImageView ivImagenTipoFormulario;

    @FXML
    private TextField tfApellido1;

    @FXML
    private TextField tfApellido2;

    @FXML
    private TextField tfCalle;

    @FXML
    private TextField tfCodigoPostal;

    @FXML
    private TextField tfEmail;

    @FXML
    private TextField tfIdAlumno;

    @FXML
    private TextField tfIdDireccion;

    @FXML
    private TextField tfNombre;

    @FXML
    private TextField tfNumeroVivienda;

    @FXML
    private TextField tfTelefono;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	btnCancelar.getStyleClass().add("boton_rojo");
    	gpFormAlumno.getStyleClass().add("fondo_ventana_degradado_toRight");
        pSeparador.getStyleClass().add("panelSeparador"); //Panel separador de barra superior.

        //Modifica el formato en el que se muestra la fecha en el DatePicker.
        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");//Formato dd/MM/yy
        dpFechaNacimiento.setConverter(new LocalDateStringConverter(formatter, null));

        conexionBD = ConexionBD.getInstance();
        toast = new Toast();
        cbGenero.setItems(FXCollections.observableArrayList(Genero.values()));
        cbEstado.setItems(FXCollections.observableArrayList(EstadoAlumno.values()));
        cbFormaPago.setItems(FXCollections.observableArrayList(FormaPago.values()));
        tfIdAlumno.setDisable(true);
        tfIdDireccion.setDisable(true);
        dpFechaNacimiento.setEditable(false);

        //Configura el ComboBox cbProvincia.
        cbProvinciaSetup();

        if(listadoProvincias != null) {
            cbProvincia.setValue("Murcia"); //Establezco Murcia como predeterminado a la hora de inicar el controlador
            cbLocalidadSetup(cbProvincia.getValue());
        }

        cbAsistencia.getItems().addAll(1,2,3);
        cbAsistencia.setValue(1);
    }


    @FXML
    void cancelar(MouseEvent event) {
        escenario.close();
    }

    @FXML
    void confirmacion(MouseEvent event) {
        if(comprobarCampos()) {
            if(modoControlador == MODO_NUEVO_ALUMNO) {
                if(crearAlumno()) {
                    toast.show(escenario, "Alumno añadido!!.");
                    escenario.close();
                }
            } else {
                if(modificarAlumno()) {
                    toast.show(escenario, "Alumno modificado!!.");
                    escenario.close();
                }
            }
        }
    }


    /**
     * Configura el ComboBox cbProvincia.
     * 
     */
    private void cbProvinciaSetup() {
        try {
			listadoProvincias = FXCollections.observableArrayList(conexionBD.getProvincias());
		} catch (Exception e) {
			e.printStackTrace();
		}

        //Carga en el ComboBox los items del listadoProvincias.
        cbProvincia.setItems(listadoProvincias);

        //Añado un listener al ComboBox de provincias para realizar una acción cuando se ha seleccionado un item.
        cbProvincia.setOnAction(event -> {
            System.out.println(cbProvincia.getValue().toString()); //provisional PARA BORRAR
            cbLocalidadSetup(cbProvincia.getValue());
        });
    }


    /**
     * Configura el ComboBox cbLocalidad.
     * @param provincia La provincia de la que se obitnen las localidades.
     */
    private void cbLocalidadSetup(String provincia) {
        try {
			listadoLocalidades = FXCollections.observableArrayList(conexionBD.getLocalidades(provincia));
		} catch (Exception e) {
			e.printStackTrace();
		}

        //Carga en el ComboBox los items del listadoLocalidades.
        cbLocalidad.setItems(listadoLocalidades);
    }


    /**
     * Establece un Stage para este controlador.
     * 
     * @param stage Stage que se establece.
     */
    public void setStage(Stage stage) {
    	this.escenario = stage;
    }

    /**
	 * Establece la lista de Alumnos.
	 * 
	 * @param lista La lista de donde se obtienen los Alumnos.
	 */
	public void setListaAlumnos(ObservableList<Alumno> lista) {
        listadoAlumnos = lista; //Guado la lista pasada a la lista de Clasecontrolador.
	}
    
    public void setAlumno(Alumno alumno) {
        oldAlumno = alumno;
        newAlumno = (Alumno)(alumno.clone());
        //newAlumno = new Alumno(alumno);
        
        cbGenero.setValue(newAlumno.getGenero());
        cbLocalidad.setValue(newAlumno.getDireccion().getLocalidad());
        cbProvincia.setValue(newAlumno.getDireccion().getProvincia());
        cbEstado.setValue(newAlumno.getEstado());
        cbFormaPago.setValue(newAlumno.getFormaPago());
        cbAsistencia.setValue(newAlumno.getAsistenciaSemanal());

        tfIdAlumno.setText(Integer.toString(newAlumno.getId()));
        tfNombre.textProperty().bindBidirectional(newAlumno.nombreProperty());
        tfApellido1.textProperty().bindBidirectional(newAlumno.apellido1Property());
        tfApellido2.textProperty().bindBidirectional(newAlumno.apellido2Property());

        cbGenero.getSelectionModel().selectedItemProperty().addListener( (o, nv, ov) -> {
            newAlumno.generoProperty().set(ov);
        });

        dpFechaNacimiento.valueProperty().bindBidirectional(newAlumno.fechaNacimientoProperty());
        tfTelefono.textProperty().bindBidirectional(newAlumno.telefonoProperty(), new NumberStringConverter("0"));
        tfEmail.textProperty().bindBidirectional(newAlumno.emailProperty());

        tfIdDireccion.setText(Integer.toString(newAlumno.getDireccion().getId()));
        tfCalle.textProperty().bindBidirectional(newAlumno.getDireccion().calleProperty());
        tfNumeroVivienda.textProperty().bindBidirectional(newAlumno.getDireccion().numeroProperty(), new NumberStringConverter("0"));

        cbLocalidad.getSelectionModel().selectedItemProperty().addListener( (o, nv, ov) -> {
            newAlumno.getDireccion().localidadProperty().set(ov);
        });

        cbProvincia.getSelectionModel().selectedItemProperty().addListener( (o, nv, ov) -> {
            newAlumno.getDireccion().provinciaProperty().set(ov);
            cbLocalidadSetup(newAlumno.getDireccion().getProvincia().toString());
        });

        tfCodigoPostal.textProperty().bindBidirectional(newAlumno.getDireccion().codigoPostalProperty(), new NumberStringConverter("0"));

        cbEstado.getSelectionModel().selectedItemProperty().addListener( (o, nv, ov) -> {
            newAlumno.estadoProperty().set(ov);
        });

        cbFormaPago.getSelectionModel().selectedItemProperty().addListener((o,nv, ov) -> {
            newAlumno.formaPagoProperty().set(ov);
        });

        cbAsistencia.getSelectionModel().selectedItemProperty().addListener( (o, nv, ov) -> {
            newAlumno.asistenciaSemanalProperty().set(ov);
        });

        
    }

    /**
     * Establece las caracteristicas del formulario segun la accion que se quiera hacer.
     * 
     * @param modo Modo en que se va a configurar el formulario.
     */
    public void modoFormulario(String modo) {
        switch (modo) {
            case MODO_NUEVO_ALUMNO:
                setupModoNuevoAlumno();
                break;
            case MODO_EDITAR_ALUMNO:
                setupModoEditarAlumno();
                break;
        
            default:
                setupModoNuevoAlumno();
                break;
        }
    }


    /**
     * Configura el formulario en modo crear nuevo Alumno.
     * 
     */
    private void setupModoNuevoAlumno() {
        modoControlador = MODO_NUEVO_ALUMNO;
        lbTitulo.setText("Nuevo Alumno");
        
        //Establecer imagen formulario.
        Image imagen;
        try {
            imagen = new Image(getClass().getResourceAsStream("/recursos/usuario_add_1_128.png")); //Forma desde IDE y JAR.
        } catch (Exception e) {
            imagen = new Image("/recursos/usuario_add_1_128.png"); //Forma desde el JAR.
        }
        ivImagenTipoFormulario.setImage(imagen);
    }


    /**
     * Configura el formulario en modo editar Alumno.
     * 
     */
    private void setupModoEditarAlumno() {
        modoControlador = MODO_EDITAR_ALUMNO;
        lbTitulo.setText("Editar Alumno");

        //Establecer imagen formulario.
        Image imagen;
        try {
            imagen = new Image(getClass().getResourceAsStream("/recursos/usuario_edit_1_128.png")); //Forma desde IDE y JAR.
        } catch (Exception e) {
            imagen = new Image("/recursos/usuario_edit_1_128.png"); //Forma desde el JAR.
        }
        ivImagenTipoFormulario.setImage(imagen);
    }

    private Boolean comprobarCampos() {
        boolean camposCorrectos = false;

        Pattern nombrePattern = Pattern.compile("[A-Z][a-z]{1,30}([\\s][A-Z][a-z]{1,30})?$"); //Expresion regular para comprobar un nombre simple o compuesto.
		Matcher nombreMatch = nombrePattern.matcher(tfNombre.getText());

        Pattern apellidoPattern = Pattern.compile("[A-Z][a-z]{1,40}$"); 
        Matcher apellido1Matcher = apellidoPattern.matcher(tfApellido1.getText());
        Matcher apellido2Matcher = apellidoPattern.matcher(tfApellido2.getText());

        Pattern telefonoPattern = Pattern.compile("[0-9]{9,10}");
        Matcher telefonoMatcher = telefonoPattern.matcher(tfTelefono.getText());

        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$|^$");
        Matcher emaiMatcher = emailPattern.matcher(tfEmail.getText());

        Pattern callePattern = Pattern.compile("^[A-Za-z0-9\\s]{0,50}$");
        Matcher calleMatcher = callePattern.matcher(tfCalle.getText());

        Pattern numeroPattern = Pattern.compile("[0-9]{0,4}");
        Matcher numeroMatcher = numeroPattern.matcher(tfNumeroVivienda.getText());
        
        Pattern codigoPostalPattern = Pattern.compile("^\\d{5}$|^[0]?$");
        Matcher codigoPostalMatcher = codigoPostalPattern.matcher(tfCodigoPostal.getText());

        if(!nombreMatch.matches()) {
            mensajeAviso("Nombre no valido.",
            "El nombre introducido no es valido.",
            "El nombre puede ser simple o compuesto.\nLa primera letra del nombre tiene que estar en mayúscula.\nMáximo 30 caracteres cada nombre.\nEjemplo: María / María Dolores.");
        } else if(!apellido1Matcher.matches()) {
            mensajeAviso("1º Apellido no valido.",
            "El apellido introducido no es valido",
            "La primera letra del Apellido tien que ser mayúscula.\nMáximo 40 caracteres.\nEjemplo: Sanchez.");
        } else if(!apellido2Matcher.matches()) {
            mensajeAviso("2º Apellido no valido.",
            "El apellido introducido no es valido",
            "La primera letra del apellido tien que ser mayúscula.\nMáximo 40 caracteres.\nEjemplo: Sanchez.");
        } else if(cbGenero.getValue() == null) {
            mensajeAviso("Género no escogido.",
            "",
            "Escoge un género para el Alumno.");
        } else if(dpFechaNacimiento.getValue() == null) {
            mensajeAviso("Fecha nacimiento no seleccionada.",
            "",
            "Selecciona una fecha de nacimiento para el Alumno.");
        } else if(!telefonoMatcher.matches()) {
            mensajeAviso("Teléfono no valido",
            "El teléfono introducido no es valido",
            "Debes introducir un número de 9 a 10 dígitos.");
        } else if(!emaiMatcher.matches()) {
            mensajeAviso("Email no valido.",
            "",
            "El Email introducido no es valido.\nEste campo es opcional.");
        } else if (cbLocalidad.getValue() == null) {
            mensajeAviso("Localidad no escogido.",
            "",
            "Escoge una Localidad para el Alumno.");
        } else if (!calleMatcher.matches()) {
            mensajeAviso("Calle no valida.",
            "La calle introducida no es valida",
            "No puede contener simbolos. Máximo 50 caracteres.\nEste campo es opcional.");
        } else if(!numeroMatcher.matches()) {
            mensajeAviso("Número de vivienda no valido.",
            "El número introducido no es valido",
            "Máximo 4 digitos.\nEste campo es opcional.");
        } else if(!codigoPostalMatcher.matches()) {
            mensajeAviso("Código Postal no valido.",
            "El código postal introducido no es valido",
            "El código postal debe contener 5 digitos.\nEste campo es opcional.");
        } else if(cbEstado.getValue() == null) {
            mensajeAviso("Estado no escogido.",
            "",
            "Escoge un estado para el Alumno.");
        } else if(cbFormaPago.getValue() == null) {
            mensajeAviso("Forma Pago no escogido.",
            "",
            "Escoge una forma de pago para el Alumno.");
        } else {
            camposCorrectos = true;
        }

        //Si no hay errores en los campos, guarda la informacion en las variables.
        if (camposCorrectos) {
            nombre = tfNombre.getText();
            apellido1 = tfApellido1.getText();
            apellido2 = tfApellido2.getText();
            genero = (Genero) cbGenero.getValue();
            fechaNac = dpFechaNacimiento.getValue();
            telefono = Integer.parseInt(tfTelefono.getText());
            email = (tfEmail.getText().isBlank()) ? "" : tfEmail.getText();
            calle = (tfCalle.getText().isBlank()) ? "" : tfCalle.getText();
            numeroVivienda = (tfNumeroVivienda.getText().isBlank()) ? 0 : Integer.parseInt(tfNumeroVivienda.getText());
            localidad = cbLocalidad.getValue().toString();
            provincia = cbProvincia.getValue().toString();
            codigoPostal = (tfCodigoPostal.getText().isBlank()) ? 0 : Integer.parseInt(tfCodigoPostal.getText());
            estado = (EstadoAlumno) cbEstado.getValue();
            formaPago = (FormaPago) cbFormaPago.getValue();
            asistenciaSemanal = cbAsistencia.getValue();

        }

        return camposCorrectos;
    }

    /**
     * Crea un objeto de tipo Alumno y lo añade a la base de datos y a la lista de Alumnos.
     * 
     * @return true si guarda el Alumno en la base de datos y en la lista de Alumnos.
     */
    private boolean crearAlumno() {
        Direccion direccion = new Direccion(-1, calle, numeroVivienda, localidad, provincia, codigoPostal);
        Alumno alumno = new Alumno(-1, nombre, apellido1, apellido2, genero, direccion, fechaNac, telefono, email, estado, asistenciaSemanal, formaPago);

        try {
            if(conexionBD.insertarAlumno(alumno)) {
                return listadoAlumnos.add(alumno);
            }
        } catch (SQLException e) {
            // añadir LOG
            e.printStackTrace();
        }
        return false;
    }


    private boolean modificarAlumno() {
        try {
            if(conexionBD.modificarAlumno(newAlumno)) {
                oldAlumno.setNombre(newAlumno.getNombre());
                oldAlumno.setApellido1(newAlumno.getApellido1());
                oldAlumno.setApellido2(newAlumno.getApellido2());
                oldAlumno.setGenero(newAlumno.getGenero());
                oldAlumno.setFechaNacimiento(newAlumno.getFechaNacimiento());
                oldAlumno.setTelefono(newAlumno.getTelefono());
                oldAlumno.setEmail(newAlumno.getEmail());
                // oldAlumno.setDireccion(newAlumno.getDireccion());
                oldAlumno.getDireccion().setValoresDireccion(newAlumno.getDireccion());
                oldAlumno.setEstado(newAlumno.getEstado());
                oldAlumno.setAsistenciaSemanal(newAlumno.getAsistenciaSemanal());
                oldAlumno.setFormaPago(newAlumno.getFormaPago());
                return true;
            }
        } catch (SQLException e) {
            // AÑADIR LOG
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Muestra una ventana de dialogo de tipo ERROR con la informacion pasada como parametros.
     * 
     * @param tiutlo Titulo de la ventana.
     * @param cabecera Cabecera del mensaje.
     * @param cuerpo Cuerpo del menesaje.
     */
    private void mensajeAviso(String tiutlo, String cabecera, String cuerpo) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
        alerta.setTitle(tiutlo);
        alerta.initOwner(escenario);
        alerta.setHeaderText(cabecera);
        alerta.setContentText(cuerpo);
        alerta.initStyle(StageStyle.DECORATED);
        alerta.initOwner(escenario);
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.showAndWait();
    }

}
