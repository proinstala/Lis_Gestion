package controlador;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import baseDatos.ConexionBD;
import javafx.fxml.Initializable;
import javafx.beans.binding.IntegerBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modelo.Alumno;
import modelo.EstadoAlumno;
import modelo.FormaPago;
import modelo.Genero;
import modelo.GrupoAlumnos;
import modelo.Mensualidad;
import modelo.ModoFormulario;
import modelo.Usuario;
import utilidades.Toast;

public class GrupoAlumnosFormControlador implements Initializable {

    private ObservableList<GrupoAlumnos> listadoGruposAlumnosCopia;

    private GrupoAlumnos grupoAlumnosCopia;

    private Usuario usuario;
	private Logger logUser;
	private Toast toast;
	private Alert alerta;
    ModoFormulario modoFormulario;
	
    private Double tiempoDelay = 0.5;

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



    private void configurarControles() {
        btnConfirmacion.setOnMouseClicked(e -> {
            guardarCambios(); //Guardar cambios hecho en el formulario.
        });

        btnCancelar.setOnMouseClicked(e -> {
            thisEstage.close(); //Cerrar la ventana.
        });
    }

    private void guardarCambios() {
        if(comprobarCampos()) {
            if(modoFormulario.equals(ModoFormulario.CREAR_DATOS)) {
                listadoGruposAlumnosCopia.add(newGrupo);
                thisEstage.close();
            } else {

            }
        }
    }


    public void setGrupoAlumnos(GrupoAlumnos grupo) {
        oldGrupo = grupo;
        newGrupo = new GrupoAlumnos(grupo);

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

        Pattern nombrePattern = Pattern.compile("^(?!\\d)(?=\\S)([\\s\\S]{1,50})$"); //máximo de 50 caracteres, no empiece por dígitos, puede contener cualquier carácter y espacios, y no puede estar vacío.
		Matcher nombreMatch = nombrePattern.matcher(tfNombre.getText());

        Pattern descripcionPattern = Pattern.compile("^(|.{1,300})$"); //pueda estar vacío o tener un máximo de 300 caracteres
        Matcher descripcionMatcher = descripcionPattern.matcher(taDescripcion.getText());


        if(!nombreMatch.matches()) {
            mensajeAviso(AlertType.WARNING,
            "Nombre no valido.",
            "El nombre introducido no es valido.",
            "No puede empezar por un digito.\nMáximo 50 caracteres.\nEjemplo: Grupo tardes 1º.");
        } else if(!descripcionMatcher.matches()) {
            mensajeAviso(AlertType.WARNING,
            "Descripción no valida.",
            "La descripción introducida no es valida.",
            "Máximo 300 caracteres.");
        } else {
            camposCorrectos = true;
        }

        //Si no hay errores en los campos, guarda la informacion en las variables.
        if (camposCorrectos) {
            newGrupo.setNombre(tfNombre.getText());
            newGrupo.setDescripcion(taDescripcion.getText()); 
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


    public void configurar(Stage stage) {
        //thisEstage = (Stage) gpFormGrupoAlumnos.getScene().getWindow(); //Obtengo el Stage para mostrar Toast.
        thisEstage = stage;
        configurarControles();
    }


    public void modoFormulario(ModoFormulario modoFormulario) {
        this.modoFormulario = modoFormulario;
       
        switch (modoFormulario) {
            case CREAR_DATOS:
                lbTitulo.setText("Nueva Mensualidad");
                setGrupoAlumnos(new GrupoAlumnos());
                break;
            case EDITAR_DATOS:
                lbTitulo.setText("Editar Mensualidad");
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
