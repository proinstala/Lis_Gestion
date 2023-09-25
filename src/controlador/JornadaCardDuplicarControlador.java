package controlador;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import baseDatos.ConexionBD;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
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
import utilidades.Constants;
import utilidades.Toast;
import javafx.fxml.Initializable;


public class JornadaCardDuplicarControlador implements Initializable {

    private Double x, y;
	private DateTimeFormatter formatter;
	private ConexionBD conexionBD;
    private Logger logUser;
	private Jornada jornada;
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
    private CheckBox chbControlAsistencia;

    @FXML
    private DatePicker dpFechaDestino;

    @FXML
    private ImageView ivImagenCopy;

    @FXML
    private Label lbDia;

    @FXML
    private Label lbJornada;

    @FXML
    private Label lbSemana;

    @FXML
    private Label lbDiaDestino;

    @FXML
    private Label lbSemanaDestino;


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

        //Configurar el evento cuando se presiona el ratón en el panel apCopiarJornada.
        apCopiarJornada.setOnMousePressed(mouseEvent -> {
            //Obtener las coordenadas X e Y del ratón en relación con la escena.
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });

        //Configurar el evento cuando se arrastra el ratón en el panel apCopiarJornada.
        apCopiarJornada.setOnMouseDragged(mouseEvent -> {
            //Obtener la referencia al Stage actual y establecer las nuevas coordenadas X e Y.
            ((Stage) apCopiarJornada.getScene().getWindow()).setX(mouseEvent.getScreenX() - x);
            ((Stage) apCopiarJornada.getScene().getWindow()).setY(mouseEvent.getScreenY() - y);
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
            lbDiaDestino.setText(fechaSeleccionada.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES"))); //Establece el texto del Label 'lbDiaDestino' con el nombre del día de la fecha seleccionada en español.
            lbSemanaDestino.setText(Integer.toString(fechaSeleccionada.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR))); //Obtener el número de semana correspondiente a la fecha seleccionada.
        });

        //Muestra el calendario emergente al hacer clic en el TextField del DatePiker.
		dpFechaDestino.getEditor().setOnMouseClicked(event -> {
            dpFechaDestino.show();
        });

        //Establezco los CheckBox de copia de datos como seleccionados.
        chbComentarioJornada.setSelected(true);
        chbAnotacionesClases.setSelected(true);
        chbAlumnos.setSelected(true);
        chbControlAsistencia.setSelected(true);

        //Establezco la condicon de CheckBox chbControlAsistencia para poder ser Habilitado y marcado.
        chbAlumnos.selectedProperty().addListener((o, ov, nv) -> {
            if(chbAlumnos.isSelected()) {
                chbControlAsistencia.setDisable(false);
            } else {
                chbControlAsistencia.setSelected(false);
                chbControlAsistencia.setDisable(true);
            }
        });

        //Configurar un evento de clic del ratón para el botón "Cancelar".
        btnCancelar.setOnMouseClicked(e -> {
            ((Stage) apCopiarJornada.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
        });
    }


    /**
     * Método que se ejecuta al hacer clic en el botón de "Duplicar".
     * Realiza la acción de duplicar la jornada actual.
     * Se verifica la jornada de destino y se configuran los comentarios según los CheckBox seleccionados.
     * Si los CheckBox "chbAlumnos" y "chbControlAsistencia" están seleccionados, se realiza un control de asistencias y se muestra una alerta.
     * Si solo el CheckBox "chbAlumnos" está seleccionado, se copian los datos de Alumnos de la jornada sin control de asistencia.
     */
    @FXML
    void duplicar(MouseEvent event) {
        if(comprobarJornadaDestino()) {
            Jornada copiaJornada = new Jornada(jornada);
            copiaJornada.setFecha(fechaSeleccionada);
            configurarComentarios(copiaJornada); //configura la jornada segun los CheckBox marcados.

            if(chbAlumnos.isSelected() && chbControlAsistencia.isSelected()) {
                ArrayList<Alumno> listaControlAlumnos = null;
                try {
                    listaControlAlumnos = conexionBD.controlCopiaJornada(copiaJornada);
                } catch (SQLException e) {
                    logUser.severe("Excepción: " + e.toString());
                    e.printStackTrace();
                } catch (Exception e) {
                    logUser.severe("Excepción: " + e.toString());
                    e.printStackTrace();
                }

                if(listaControlAlumnos != null && !listaControlAlumnos.isEmpty()) {
                    //Construye el mensaje con los datos de los alumnos que superarán el número de asistencias semanales.
                    String datosAlumnos = "";
                    for (Alumno alumno : listaControlAlumnos) {
                        datosAlumnos = datosAlumnos.concat(alumno.toString() + "\n");
                    }

                    datosAlumnos = datosAlumnos.concat("\n\nElige una opción:");
                    
                    //Crea y configura la alerta con los botones correspondientes.
                    alerta = new Alert(Alert.AlertType.WARNING);
                    alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
                    alerta.setTitle("Control asistencias");
                    alerta.initOwner((Stage) apCopiarJornada.getScene().getWindow());
                    alerta.setHeaderText("Se han encontrado Alumnos que superarán su número                 \nde asistencias semanales.");
                    alerta.setContentText(datosAlumnos);
                    alerta.initStyle(StageStyle.DECORATED);
                    alerta.initModality(Modality.APPLICATION_MODAL);
                    
                    ButtonType buttonTypeCopiar = new ButtonType(" Copiar Alumnos ", ButtonData.LEFT);
                    ButtonType buttonTypeQuitar = new ButtonType(" Quitar Alumnos ", ButtonData.LEFT);
                    ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
                    
                    alerta.getButtonTypes().setAll(buttonTypeCopiar, buttonTypeQuitar, buttonTypeCancel);
                    
                    Boolean copiaOk = false;
                    Optional<ButtonType> result = alerta.showAndWait();
                    if (result.get() == buttonTypeCopiar){
                        copiaOk = copiarJornada(copiaJornada);
                    } else if (result.get() == buttonTypeQuitar) {
                        for (Alumno alumnoControl : listaControlAlumnos) {
                            for (Clase c : copiaJornada.getClases()) {
                                for (Alumno alumno : c.getListaAlumnos()) {
                                    if(alumnoControl.getId() == alumno.getId()) {c.removeAlumnoPorId(alumnoControl);}
                                }
                            }
                        }
                        copiaOk = copiarJornada(copiaJornada);
                    } else {
                        return; //Salir de este metodo.
                    }

                    if(copiaOk) {
                        logUser.config("Copiados datos Jornada: " + jornada.getFecha().format(formatter) + " en jornada: " + copiaJornada.getFecha().format(formatter));
                        toast.show((Stage) ((Stage) apCopiarJornada.getScene().getWindow()).getOwner(), "Duplicado de jornada realizado.");
                        ((Stage) apCopiarJornada.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
                    } else {
                        logUser.warning("Fallo al duplicar Jornada.");
                        mensajeAviso(Alert.AlertType.ERROR,
                                "Fallo al copiar Jornada.",
                                "",
                                "No se ha podido copiar la Jornada.");
                    }

                } else if(listaControlAlumnos == null) {
                    logUser.warning("Fallo al duplicar Jornada.");
                    mensajeAviso(Alert.AlertType.ERROR,
                            "Fallo al copiar Jornada.",
                            "",
                            "No se ha podido copiar la Jornada.");
                } else if(listaControlAlumnos.isEmpty()) {
                    if(copiarJornada(copiaJornada)) {
                        logUser.config("Copiados datos Jornada: " + jornada.getFecha().format(formatter) + " en jornada: " + copiaJornada.getFecha().format(formatter));
                        toast.show((Stage) ((Stage) apCopiarJornada.getScene().getWindow()).getOwner(), "Duplicado de jornada realizado.");
                        ((Stage) apCopiarJornada.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
                    } else {
                        logUser.warning("Fallo al duplicar Jornada.");
                        mensajeAviso(Alert.AlertType.ERROR,
                                "Fallo al copiar Jornada.",
                                "",
                                "No se ha podido copiar la Jornada.");
                    }
                }

            } else if (chbAlumnos.isSelected()){
                if(copiarJornada(copiaJornada)) {
                    logUser.config("Copiados datos Jornada: " + jornada.getFecha().format(formatter) + " en jornada: " + copiaJornada.getFecha().format(formatter));
                    toast.show((Stage) ((Stage) apCopiarJornada.getScene().getWindow()).getOwner(), "Duplicado de jornada realizado.");
                    ((Stage) apCopiarJornada.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
                } else {
                    logUser.warning("Fallo al duplicar Jornada.");
                    mensajeAviso(Alert.AlertType.ERROR,
                            "Fallo al copiar Jornada.",
                            "",
                            "No se ha podido copiar la Jornada.");
                }

            } else {
                boolean insetaOK = false;
                try {
                    insetaOK = conexionBD.insertarJornada(copiaJornada);
                } catch (SQLException e) {
                    logUser.severe("Excepción: " + e.toString());
                    e.printStackTrace();
                } catch (Exception e) {
                    logUser.severe("Excepción: " + e.toString());
                    e.printStackTrace();
                }

                if(insetaOK) {
                    logUser.config("Copiados datos Jornada: " + jornada.getFecha().format(formatter) + " en jornada: " + copiaJornada.getFecha().format(formatter));
                    toast.show((Stage) ((Stage) apCopiarJornada.getScene().getWindow()).getOwner(), "Duplicado de jornada realizado.");
                    ((Stage) apCopiarJornada.getScene().getWindow()).close(); //Obtener la referencia al Stage actual y cerrarlo.
                } else {
                    logUser.warning("Fallo al duplicar Jornada.");
                    mensajeAviso(Alert.AlertType.ERROR,
                            "Fallo al copiar Jornada.",
                            "",
                            "No se ha podido copiar la Jornada.");
                }
            }
        }
    }


    /**
     * Realiza la copia de la jornada especificada en la base de datos.
     * 
     * @param jornadaCopia La jornada que se va a copiar.
     * @return true si la copia se realiza correctamente, false en caso contrario.
     */
    private boolean copiarJornada(Jornada jornadaCopia) {
        try {
            return conexionBD.insertarJornadaCompleta(jornadaCopia);        
        } catch (SQLException e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Configura los comentarios en la jornada especificada.
     * Si la opción de comentario de jornada está desactivada, se elimina el comentario de la jornada.
     * Si la opción de anotaciones de clases está desactivada, se eliminan las anotaciones de todas las clases de la jornada.
     * 
     * @param jornada La jornada en la que se van a configurar los comentarios.
     */
    private void configurarComentarios(Jornada jornada) {
        if(!chbComentarioJornada.isSelected()) {
            jornada.setComentario("");
            System.out.println("Se elimina los comentarios de jornada.");
        }

        if(!chbAnotacionesClases.isSelected()) {
            for(Clase c : jornada.getClases()) {
                c.setAnotaciones("");
                System.out.println("Se elimina las anotaciones de clase.");
            }
        }
        
    }


    /**
     * Comprueba si la jornada de destino es válida para realizar la copia.
     * Verifica que se haya seleccionado una fecha de destino.
     * Verifica que la fecha de destino no sea igual a la fecha de origen.
     * Verifica si ya existe una jornada en la fecha de destino.
     * 
     * @return true si la jornada de destino es válida, false de lo contrario.
     */
    private boolean comprobarJornadaDestino() {
        boolean correcto = false;
        if (fechaSeleccionada == null) {
            mensajeAviso(Alert.AlertType.ERROR,
                    "Fecha no seleccionada.",
                    "",
                    "Selecciona una fecha de destino para copiar los datos.");
        } else if (fechaSeleccionada.isEqual(jornada.getFecha())) {
            mensajeAviso(Alert.AlertType.ERROR,
                    "Fecha no valida.",
                    "",
                    "La fecha de destino es la misma que la de origen.");
        } else if (fechaSeleccionada != null) {
            try {
                if(conexionBD.comprobarJornada(fechaSeleccionada)) {
                    mensajeAviso(Alert.AlertType.ERROR,
                            "Fecha no valida.",
                            "",
                            "La fecha seleccionda ya contiene una Jornada.");
                } else {
                    correcto = true;
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
        alerta.initOwner((Stage) apCopiarJornada.getScene().getWindow());
        alerta.setHeaderText(cabecera);
        alerta.setContentText(cuerpo);
        alerta.initStyle(StageStyle.DECORATED);
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.showAndWait();
    }


    /**
     * Inicializa los componentes visuales con los valores de la jornada actual.
     * Actualiza la etiqueta de la jornada con la fecha formateada.
     * Actualiza la etiqueta de la semana con el número de semana.
     * Actualiza la etiqueta del día con el nombre del día de la semana.
     */
    public void iniciar() {
        lbJornada.setText(jornada.getFecha().format(formatter)); //Actualiza la etiqueta de la jornada con la fecha formateada.
        lbSemana.setText(Integer.toString(jornada.getFecha().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR))); //Actualiza la etiqueta de la semana con el número de semana.
        lbDia.setText(jornada.getFecha().getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES"))); //Actualiza la etiqueta del día con el nombre del día de la semana en español.
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
