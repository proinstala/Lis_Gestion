package controlador;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.fxml.Initializable;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modelo.GrupoAlumnos;
import modelo.ModoFormulario;
import modelo.Usuario;

public class GrupoAlumnosFormControlador implements Initializable {

    private ObservableList<GrupoAlumnos> listadoGruposAlumnosCopia;

    private GrupoAlumnos grupoAlumnosCopia;

    private Usuario usuario;
    ModoFormulario modoFormulario;

    private Stage thisEstage;

    String nombre;
    String descripcion;
    GrupoAlumnos oldGrupo;
    GrupoAlumnos newGrupo;


    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnConfirmacion;

    @FXML
    private GridPane gpFormGrupoAlumnos;

    @FXML
    private ImageView ivImagenTipoFormulario;

    @FXML
    private Label lbTitulo;

    @FXML
    private TextArea taDescripcion;

    @FXML
    private TextField tfIdentificador;

    @FXML
    private TextField tfNombre;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       
    }

    /**
     * Configura los eventos de los controles de la interfaz.
     */
    private void configurarControles() {
        btnConfirmacion.setOnMouseClicked(e -> {
            guardarCambios(); //Guardar cambios hecho en el formulario.
        });

        btnCancelar.setOnMouseClicked(e -> {
            thisEstage.close(); //Cerrar la ventana.
        });
    }

    /**
     * Valida y guarda los cambios realizados en el formulario. Este método primero verifica que los campos del formulario
     * cumplan con los criterios de validación a través de comprobarCampos(). Luego, limpia los campos de texto eliminando
     * espacios en blanco innecesarios al inicio y al final. Dependiendo del modo en que se encuentre el formulario, 
     * crea un nuevo grupo de alumnos o actualiza uno existente, y finalmente cierra la ventana del formulario.
     */
    private void guardarCambios() {
        //Comprueba que los datos en los campos del formulario sean válidos antes de proceder.
        if(comprobarCampos()) {
            ///Limpia los campos de texto de cualquier espacio en blanco al inicio o final de los mismos.
            tfNombre.setText(tfNombre.getText().trim());
            taDescripcion.setText(taDescripcion.getText().trim());

            //Verifica el modo del formulario para determinar la acción a realizar.
            if(modoFormulario.equals(ModoFormulario.CREAR_DATOS)) {
                //Si el formulario está en modo de creación, añade el nuevo grupo al listado.
                listadoGruposAlumnosCopia.add(newGrupo);
            } else {
                //Cierra la ventana del formulario después de guardar los cambios.
                oldGrupo.setValoresGrupoAlumnos(newGrupo);
            }
            thisEstage.close();
        }
    }


    /**
     * Configura el formulario con los datos de un grupo de alumnos específico para su edición o visualización.
     * Este método establece el grupo de alumnos actual y prepara una nueva instancia para la edición, 
     * asegurando que los cambios solo se apliquen si se confirman explícitamente.
     *
     * @param grupo El grupo de alumnos a editar o visualizar en el formulario.
     */
    public void setGrupoAlumnos(GrupoAlumnos grupo) {
        oldGrupo = grupo; //Guarda el grupo original en caso de necesitar revertir los cambios.
        newGrupo = new GrupoAlumnos(grupo); //Crea una nueva instancia de GrupoAlumnos basada en el grupo proporcionado, para la edición.
        
        // Configura los enlaces de datos bidireccionales entre los campos de texto de la interfaz y las propiedades del nuevo grupo.
        tfIdentificador.setText(Integer.toString(newGrupo.getId())); //Este valor no se vincula bidireccionalmente ya que el identificador no cambia durante la edición.
        tfNombre.textProperty().bindBidirectional(newGrupo.nombreProperty());
        taDescripcion.textProperty().bindBidirectional(newGrupo.descripcionProperty());

    }


    /**
     * Comprueba la validez de los campos introducidos en el formulario.
     * Muestra mensajes de advertencia en caso de que algún campo no sea válido.
     * Si todos los campos son válidos, guarda la información en variables específicas.
     *
     * @return true si todos los campos son válidos, false en caso contrario.
     */
    private Boolean comprobarCampos() {
        boolean camposCorrectos = false;

        //Expresiones regulares para validar los campos.

        Pattern nombrePattern = Pattern.compile("^(?!\\d)(?=\\S)([\\s\\S]{1,50})$"); //máximo de 50 caracteres, no empiece por dígitos ni espacio en blanco, puede contener cualquier carácter y espacios, y no puede estar vacío.
		Matcher nombreMatch = nombrePattern.matcher(tfNombre.getText());

        Pattern palabraReservadaPattern = Pattern.compile(".*-borrado-.*");
        Matcher palabraReservadaMatch = palabraReservadaPattern.matcher(tfNombre.getText());

        Pattern descripcionPattern = Pattern.compile("^(|.{1,300})$"); //pueda estar vacío o tener un máximo de 300 caracteres
        Matcher descripcionMatcher = descripcionPattern.matcher(taDescripcion.getText());


        if(!nombreMatch.matches()) {
            mensajeAviso(AlertType.WARNING,
            "Nombre no valido.",
            "El nombre introducido no es valido.",
            "No puede empezar por un digito ni espacio en blanco.\nMáximo 50 caracteres.\nEjemplo: Grupo tardes 1º.");
        } else if(palabraReservadaMatch.matches()) {
            mensajeAviso(AlertType.WARNING,
            "Nombre no valido.",
            "El nombre consta de una palabra reservada.",
            "El nombre no puede contener la palabra -borrado-.");
        } else if(!descripcionMatcher.matches()) {
            mensajeAviso(AlertType.WARNING,
            "Descripción no valida.",
            "La descripción introducida no es valida.",
            "Máximo 300 caracteres.");
        } else {
            camposCorrectos = true;
        }

        //Si no hay errores en los campos compureba que no hay un grupo con el mismo nombre.
        if (camposCorrectos) {
            for(var grupo :  listadoGruposAlumnosCopia) {
                if(grupo.getNombre().equals(tfNombre.getText())) {
                    mensajeAviso(AlertType.WARNING, 
                    "Nombre de grupo alumnos NO valido.", 
                    "",
                    "Ya existe un grupo de alumnos con ese nombre.");

                    return false;
                }
            }
        }
        return camposCorrectos;
    }


    /**
     * Establece la lista de grupos de alumnos para este controlador.
     * 
     * @param listaGrupos La lista de grupos de alumnos a establecer.
     */
    public void setListaGruposAlumnosCopia(ObservableList<GrupoAlumnos> listaGrupos) {
        listadoGruposAlumnosCopia = listaGrupos;
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
     * Realiza la configuración inicial del formulario o ventana, estableciendo el escenario (Stage)
     * y configurando los controles UI asociados a este formulario.
     *
     * @param stage El escenario (Stage) de JavaFX que representa la ventana en la que se carga
     *              la interfaz de usuario del formulario.
     */
    public void configurar(Stage stage) {
        thisEstage = stage;
        configurarControles(); //Llama al método configurarControles para establecer los eventos y comportamientos específicos de los controles UI.
    }


    /**
     * Configura el formulario según el modo especificado, ajustando elementos de la interfaz
     * y preparando el formulario para la creación o edición de un grupo de alumnos.
     * 
     * @param modoFormulario El modo de operación del formulario, determinando si se está creando un nuevo grupo de alumnos o editando uno existente.
     * 
     */
    public void modoFormulario(ModoFormulario modoFormulario) {
        this.modoFormulario = modoFormulario;
       
        //Evalúa el modo de formulario proporcionado para configurar adecuadamente el formulario.
        switch (modoFormulario) {
            case CREAR_DATOS:
                lbTitulo.setText("Nuevo Grupo Alumnos");
                setGrupoAlumnos(new GrupoAlumnos());
                break;
            case EDITAR_DATOS:
                lbTitulo.setText("Editar Grupo Alumnos");
                break;
            default:
                break;
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
        alerta.initOwner(thisEstage);
        alerta.setHeaderText(cabecera);
        alerta.setContentText(cuerpo);
        alerta.initStyle(StageStyle.DECORATED);
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.showAndWait();
    }
    
}
