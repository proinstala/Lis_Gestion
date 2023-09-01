package controlador;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import baseDatos.ConexionBD;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
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
import modelo.Clase;
import modelo.Jornada;
import modelo.Toast;
import utilidades.Constants;

public class SemanaCardDuplicarControlador implements Initializable {
    
    private Double x, y;
	private DateTimeFormatter formatter;
	private ConexionBD conexionBD;
    private Logger logUser;
    private Toast toast;
	private Jornada jornada;
    private int semanaOrigen;
    private int semanaDestino;
    private int anioOrigen;
    private int anioDestino;
    private LocalDate[] fechasDestino;
	private Alert alerta;
    private LocalDate fechaSeleccionada;


    @FXML
    private AnchorPane apCopiarSemana;

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
        //Añadir clases de estilo CSS a elementos.
        btnCancelar.getStyleClass().add("boton_rojo");
    
        //Cargar imagenes en ImageView.
        Image imagenCopy;
        try {
            //Intentar cargar la imagen desde el recurso en el IDE y en el JAR.
            imagenCopy = new Image(getClass().getResourceAsStream("/recursos/copy_2_128.png")); //Forma desde IDE y JAR.
        } catch (Exception e) {
            //Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR.
            imagenCopy = new Image("/recursos/copy_2_128.png"); //Forma desde el JAR.
        }
        ivImagenCopy.setImage(imagenCopy); //Establecer la imagen cargada en el ImageView.

        //Configurar el evento cuando se presiona el ratón en el panel apCopiarSemana.
        apCopiarSemana.setOnMousePressed(mouseEvent -> {
            //Obtener las coordenadas X e Y del ratón en relación con la escena.
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });

        //Configurar el evento cuando se arrastra el ratón en el panel apCopiarSemana.
        apCopiarSemana.setOnMouseDragged(mouseEvent -> {
            //Obtener la referencia al Stage actual y establecer las nuevas coordenadas X e Y.
            ((Stage) apCopiarSemana.getScene().getWindow()).setX(mouseEvent.getScreenX() - x);
            ((Stage) apCopiarSemana.getScene().getWindow()).setY(mouseEvent.getScreenY() - y);
        });

        logUser = Logger.getLogger(Constants.USER); //Crea una instancia de la clase Logger asociada al nombre de registro.
        conexionBD = ConexionBD.getInstance();      //Obtener una instancia de la clase ConexionBD utilizando el patrón Singleton.
        toast = new Toast();

        
        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); //Crear un formateador de fecha con el patrón "dd/MM/yyyy".
        dpFechaDestino.setConverter(new LocalDateStringConverter(formatter, null)); //Establecer un convertidor de cadena de fecha para el control DatePicker dpFechaDestino.
        
        //Para poder escribir directamente la fecha
        dpFechaDestino.setEditable(false); //-> Poner a true para que sea editable.
        
        //Establecer un evento para cuando cambie la fecha seleccionada en el DatePicker dpFechaDestino.
        dpFechaDestino.setOnAction(event -> {
        	fechaSeleccionada = dpFechaDestino.getValue(); //Obtener la fecha seleccionada del DatePicker y guardarla en la variable fechaSeleccionada.
            semanaDestino = fechaSeleccionada.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR); //Obtener el número de semana correspondiente a la fecha seleccionada.
            anioDestino = fechaSeleccionada.getYear();                  //Obtener el año correspondiente a la fecha seleccionada.
            lbSemanaDestino.setText(Integer.toString(semanaDestino));   //Establecer el número de semana en la etiqueta correspondiente.
            lbAnioDestino.setText(Integer.toString(anioDestino));       //Establecer el año en la etiqueta correspondiente.
        });

        //Muestra el calendario emergente al hacer clic en el TextField del DatePiker.
		dpFechaDestino.getEditor().setOnMouseClicked(event -> {
            dpFechaDestino.show();
        });

        //Establezco los CheckBox de copia de datos como seleccionados.
        chbComentarioJornada.setSelected(true);
        chbAnotacionesClases.setSelected(true);
        chbAlumnos.setSelected(true);

        //Configurar un evento de clic del ratón para el botón "Cancelar".
        btnCancelar.setOnMouseClicked(e -> {
            ((Stage) apCopiarSemana.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
        });
    }


    /**
     * Método que se ejecuta al hacer clic en el botón 'duplicar'.
     * Realiza la duplicación de la semana de jornadas.
     *
     * @param event El evento del mouse.
     */
    @FXML
    void duplicar(MouseEvent event) {  
        //Comprobar si la semana de destino es válida.
        if(comprobarSemanaDestino()) {
            LocalDate[] fechasOrigen = obtenerFechasSemana(jornada.getFecha()); //Obtener las fechas de la semana de origen.
            Jornada[] semanaJornadas = null;

            //Obtener las jornadas completas de la semana de origen desde la base de datos.
            try {
                semanaJornadas = conexionBD.getJornadasCompletas(fechasOrigen);
            } catch (SQLException e) {
                logUser.severe("Excepción: " + e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                logUser.severe("Excepción: " + e.toString());
                e.printStackTrace();
            }

            boolean copiaOK = false;
            if (semanaJornadas != null) {
                //Iterar sobre las jornadas de la semana de origen.
                for (int i = 0; i < semanaJornadas.length; i++) {
                    if (semanaJornadas[i] != null) {
                        semanaJornadas[i].setFecha(fechasDestino[i]);   //Cambia la fecha de las Jornada de origen por la fecha de destino.
                        configurarJornada(semanaJornadas[i]);           //Configura la jornada segun los CheckBox seleccionados.
                    }
                }

                try {
                    copiaOK = conexionBD.insertarListaJornadas(semanaJornadas); //Insertar la lista de jornadas duplicadas en la base de datos.
                } catch (SQLException e) {
                    logUser.severe("Excepción: " + e.toString());
                    e.printStackTrace();
                } catch (Exception e) {
                    logUser.severe("Excepción: " + e.toString());
                    e.printStackTrace();
                }
            }

            //Informa si la duplicación fue exitosa.
            if (copiaOK) {
                logUser.config("Duplicado de semana realizado.");
                mensajeAviso(Alert.AlertType.INFORMATION,
                        "Duplicado Semana.",
                        "",
                        "Duplicado de semana realizado correctamente.");
                
                toast.show((Stage) ((Stage) apCopiarSemana.getScene().getWindow()).getOwner(), "Duplicado de jornada realizado.");
                ((Stage) apCopiarSemana.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
            } else {
                logUser.warning("Fallo al duplicar la semana.");
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
        
        int numeroSemana = fecha.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR); //Número de semana que deseas obtener.
        int anio = fecha.getYear(); // Año correspondiente

        //Obtener WeekFields basado en la configuración regional predeterminada.
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        //Obtener el primer día de la semana específica.
        LocalDate primerDiaSemana = LocalDate.of(anio, 1, 1) //Primer día del año.
                .with(weekFields.weekOfYear(), numeroSemana) //Establecer el número de semana.
                .with(weekFields.dayOfWeek(), 1); //Establecer el primer día de la semana (lunes en este caso).

        //Iterar sobre los siguientes 7 días para obtener los LocalDate de cada día de la semana.
        LocalDate[] semana = new LocalDate[7];
        for (int i = 0; i < 7; i++) {
            semana[i] = primerDiaSemana.plusDays(i); //Obtener el LocalDate de cada día.
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

        //Comprobación de la fecha seleccionada.
        if (fechaSeleccionada == null) {
            mensajeAviso(Alert.AlertType.ERROR,
                    "Fecha no seleccionada.",
                    "",
                    "Selecciona una fecha de destino para copiar los datos.");

        } else if (semanaOrigen == semanaDestino && anioOrigen == anioDestino) { //Comprobación de la coincidencia de semanas.
            mensajeAviso(Alert.AlertType.ERROR,
                    "Fecha no valida.",
                    "",
                    "La Semana de destino es la misma que la de origen.");

        } else if (fechaSeleccionada != null) { //Comprobación de la fecha de destino y existencia de jornadas.
            fechasDestino = obtenerFechasSemana(fechaSeleccionada);
            try {
                //Comprobar si la semana de destino ya contiene al menos una jornada.
                if(conexionBD.comprobarJornadas(fechasDestino)) {
                    mensajeAviso(Alert.AlertType.ERROR,
                            "Fecha no valida.",
                            "",
                            "La semana seleccionda ya contiene almenos una jornada.");
                } else {
                    correcto = true; //La semana de destino es válida.
                }
            } catch (SQLException e) {
                logUser.severe("Excepción: " + e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                logUser.severe("Excepción: " + e.toString());
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
        alerta.initOwner((Stage) apCopiarSemana.getScene().getWindow());
        alerta.setHeaderText(cabecera);
        alerta.setContentText(cuerpo);
        alerta.initStyle(StageStyle.DECORATED);
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.showAndWait();
    }


    /**
     * Método que obtiene la semana y el año de origen desde un objeto Jornada y
     * actualiza dos componentes de interfaz gráfica con esos valores.
     * 
     */
    public void iniciar() {
        semanaOrigen = jornada.getFecha().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR); //Obtener el número de semana de origen.
        anioOrigen = jornada.getFecha().getYear(); //Obtener el año de origen
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
}    

