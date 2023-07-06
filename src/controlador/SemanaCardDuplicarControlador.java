package controlador;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import baseDatos.ConexionBD;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.LocalDateStringConverter;
import modelo.Alumno;
import modelo.Clase;
import modelo.Jornada;
import modelo.Toast;

public class SemanaCardDuplicarControlador implements Initializable {
    private Double x, y;

    private PrincipalControlador controladorPincipal;
	private Stage escenario;
	private DateTimeFormatter formatter;
	private ConexionBD conexionBD;
	private Jornada jornada;
    private int semanaOrigen;
    private int semanaDestino;
    private int anioOrigen;
    private int anioDestino;
    LocalDate[] fechasDestino;
	private Toast toast;
	private Alert alerta;
    private LocalDate fechaSeleccionada;


    @FXML
    private AnchorPane apCopiarJornada;

    @FXML
    private AnchorPane apIzquierda;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnDuplicar;

    @FXML
    private CheckBox chbAlumnos;

    @FXML
    private CheckBox chbAnotacionesClases;

    @FXML
    private CheckBox chbComentarioJornada;

    @FXML
    private DatePicker dpFechaDestino;

    @FXML
    private ImageView ivImagenCopy;

    @FXML
    private Label lbSemana;

    @FXML
    private Label lbSemanaDestino;

    @FXML
    private Label lbAnioDestino;

    @FXML
    private Label lbAnioOrigen;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnCancelar.getStyleClass().add("boton_rojo");
    
        Image imagenCopy;
        try {
            //Forma desde IDE y JAR.
            imagenCopy = new Image(getClass().getResourceAsStream("/recursos/copy_2_128.png"));
        } catch (Exception e) {
            //Forma desde el JAR.
            imagenCopy = new Image("/recursos/copy_2_128.png");
        }
        ivImagenCopy.setImage(imagenCopy);

        conexionBD = ConexionBD.getInstance();
        toast = new Toast();

        apCopiarJornada.setOnMousePressed(mouseEvent -> {
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });

        apCopiarJornada.setOnMouseDragged(mouseEvent -> {
            escenario.setX(mouseEvent.getScreenX() - x);
            escenario.setY(mouseEvent.getScreenY() - y);
        });

        //Modifica el formato en el que se muestra la fecha en el dtFechaCompra
        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");//Formato dd/MM/yy
        dpFechaDestino.setConverter(new LocalDateStringConverter(formatter, null));
        
        //Para poder escribir directamente la fecha
        dpFechaDestino.setEditable(false); //-> Poner a true para que sea editable.

		//dpFecha.setValue(LocalDate.now()); //Establece la fecha de hoy en el datePiker. 
        
        dpFechaDestino.setOnAction(event -> {
        	fechaSeleccionada = dpFechaDestino.getValue(); //Guardo la fecha seleccionada en fechaSeleccionada.
            semanaDestino = fechaSeleccionada.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            anioDestino = fechaSeleccionada.getYear();
            lbSemanaDestino.setText(Integer.toString(semanaDestino));
            lbAnioDestino.setText(Integer.toString(anioDestino)); 
        });

        //Establezco los CheckBox de copia de datos como seleccionados.
        chbComentarioJornada.setSelected(true);
        chbAnotacionesClases.setSelected(true);
        chbAlumnos.setSelected(true);
    }

    @FXML
    void cerrarVentana(MouseEvent event) {
        escenario.close();
    }

    @FXML
    void duplicar(MouseEvent event) {   
        if(comprobarSemanaDestino()) {
            LocalDate[] fechasOrigen = obtenerFechasSemana(jornada.getFecha());
            Jornada[] semanaJornadas = null;
            try {
                semanaJornadas = conexionBD.getJornadasCompletas(fechasOrigen);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //FALTA CAMBIAR A LAS JORNADAS LAS FECHAS.
            boolean copiaOK = false;
            if (semanaJornadas != null) {

                
                for (int i = 0; i < semanaJornadas.length; i++) {
                    if (semanaJornadas[i] != null) {
                        semanaJornadas[i].setFecha(fechasDestino[i]);   //Cambia la fecha de las Jornada de origen por la fecha de destino.
                        configurarJornada(semanaJornadas[i]);           //Configura la jornada segun los CheckBox seleccionados.
                    }
                }

                try {
                    copiaOK = conexionBD.insertarListaJornadas(semanaJornadas);
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (copiaOK) {
                toast.show(escenario, "Duplicado de jornada realizado.");
                escenario.close();
            } else {
                mensajeAviso(Alert.AlertType.ERROR,
                        "Fallo al copiar Jornada.",
                        "",
                        "No se ha podido copiar la Jornada.");
            }
        }
    }


    /**
     * Obtiene un array de objetos LocalDate que representan los días de una semana específica,
     * dado un objeto LocalDate que representa cualquier día de esa semana.
     *
     * @param fecha El objeto LocalDate que representa cualquier día de la semana.
     * @return Un array de objetos LocalDate que representa los días de la semana.
     */
    private LocalDate[] obtenerFechasSemana(LocalDate fecha) { 
        
        int numeroSemana = fecha.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);; // Número de semana que deseas obtener
        int anio = fecha.getYear(); // Año correspondiente

        // Obtener WeekFields basado en la configuración regional predeterminada
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        // Obtener el primer día de la semana específica
        LocalDate primerDiaSemana = LocalDate.of(anio, 1, 1) // Primer día del año
                .with(weekFields.weekOfYear(), numeroSemana) // Establecer el número de semana
                .with(weekFields.dayOfWeek(), 1); // Establecer el primer día de la semana (lunes en este caso)

        // Iterar sobre los siguientes 7 días para obtener los LocalDate de cada día de la semana
        LocalDate[] semana = new LocalDate[7];
        for (int i = 0; i < 7; i++) {
            semana[i] = primerDiaSemana.plusDays(i); // Obtener el LocalDate de cada día
        }
        return semana;
    }


    /**
     * Configura la jornada segun los CheckBox seleccionados.
     * @param j Jornada que se va a configurar.
     */
    private void configurarJornada(Jornada j) {
        if (!chbComentarioJornada.isSelected()) {
            j.setComentario("");
        }

        if (!chbAnotacionesClases.isSelected()) {
            for (Clase c : j.getClases()) {
                c.setAnotaciones("");
            }
        }

        if (!chbAlumnos.isSelected()) {
            for (Clase c : j.getClases()) {
                c.vaciarListaAlumnos();
            }
        }
        
    }


    /**
     * Comprueba si la semana de destino es válida para copiar los datos.
     *
     * @return true si la semana de destino es válida, false en caso contrario.
     */
    private boolean comprobarSemanaDestino() {
        boolean correcto = false;
        if (fechaSeleccionada == null) {
            mensajeAviso(Alert.AlertType.ERROR,
                    "Fecha no seleccionada.",
                    "",
                    "Selecciona una fecha de destino para copiar los datos.");
        } else if (semanaOrigen == semanaDestino && anioOrigen == anioDestino) {
            mensajeAviso(Alert.AlertType.ERROR,
                    "Fecha no valida.",
                    "",
                    "La Semana de destino es la misma que la de origen.");
        } else if (fechaSeleccionada != null) {
            fechasDestino = obtenerFechasSemana(fechaSeleccionada);
            try {
                if(conexionBD.comprobarJornadas(fechasDestino)) {
                    mensajeAviso(Alert.AlertType.ERROR,
                            "Fecha no valida.",
                            "",
                            "La semana seleccionda ya contiene almenos una jornada.");
                } else {
                    correcto = true;
                }
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return correcto;
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
        alerta.initOwner(escenario);
        alerta.setHeaderText(cabecera);
        alerta.setContentText(cuerpo);
        alerta.initStyle(StageStyle.DECORATED);
        alerta.initOwner(escenario);
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.showAndWait();
    }


    public void iniciar() {
        semanaOrigen = jornada.getFecha().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        anioOrigen = jornada.getFecha().getYear();
        lbSemana.setText(Integer.toString(semanaOrigen));
        lbAnioOrigen.setText(Integer.toString(anioOrigen));
    }


    /**
	 * Establece una jornada para este controlador.
	 * 
	 * @param jornada El objeto de donde se obtienen los datos. 
	 */
	public void setJornada(Jornada jornada) {
		this.jornada = jornada;
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

