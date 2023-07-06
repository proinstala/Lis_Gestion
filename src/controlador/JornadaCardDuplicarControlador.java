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
import modelo.Toast;
import javafx.fxml.Initializable;

public class JornadaCardDuplicarControlador implements Initializable {

    private Double x, y;

    private PrincipalControlador controladorPincipal;
	private Stage escenario;
	private DateTimeFormatter formatter;
	private ConexionBD conexionBD;
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
        btnCancelar.getStyleClass().add("boton_rojo");
        conexionBD = ConexionBD.getInstance();
        toast = new Toast();

        Image imagenCopy;
        try {
            //Forma desde IDE y JAR.
            imagenCopy = new Image(getClass().getResourceAsStream("/recursos/copy_2_128.png"));    
        } catch (Exception e) {
            //Forma desde el JAR.
            imagenCopy = new Image("/recursos/copy_2_128.png");
        }
        ivImagenCopy.setImage(imagenCopy);

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
            lbDiaDestino.setText(fechaSeleccionada.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES")));
            lbSemanaDestino.setText(Integer.toString(fechaSeleccionada.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR))); 
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
        
    }

    @FXML
    void cerrarVentana(MouseEvent event) {
        escenario.close();
    }

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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if(listaControlAlumnos != null && !listaControlAlumnos.isEmpty()) {
                    String datosAlumnos = "";
                    for (Alumno alumno : listaControlAlumnos) {
                        datosAlumnos = datosAlumnos.concat(alumno.toString() + "\n");
                    }

                    datosAlumnos = datosAlumnos.concat("\n\nElige una opción:");
                    
                    alerta = new Alert(Alert.AlertType.WARNING);
                    alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
                    alerta.setTitle("Control asistencias");
                    alerta.initOwner(escenario);
                    alerta.setHeaderText("Se han encontrado Alumnos que superarán su número                 \nde asistencias semanales.");
                    alerta.setContentText(datosAlumnos);
                    alerta.initStyle(StageStyle.DECORATED);
                    alerta.initOwner(escenario);
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
                        toast.show((Stage) apCopiarJornada.getScene().getWindow(), "Duplicado de jornada realizado.");
                        escenario.close();
                    } else {
                        mensajeAviso(Alert.AlertType.ERROR,
                                "Fallo al copiar Jornada.",
                                "",
                                "No se ha podido copiar la Jornada.");
                    }

                } else if(listaControlAlumnos == null) {
                    mensajeAviso(Alert.AlertType.ERROR,
                            "Fallo al copiar Jornada.",
                            "",
                            "No se ha podido copiar la Jornada.");
                } else if(listaControlAlumnos.isEmpty()) {
                    if(copiarJornada(copiaJornada)) {
                        toast.show((Stage) apCopiarJornada.getScene().getWindow(), "Duplicado de jornada realizado.");
                        escenario.close();
                    } else {
                        mensajeAviso(Alert.AlertType.ERROR,
                                "Fallo al copiar Jornada.",
                                "",
                                "No se ha podido copiar la Jornada.");
                    }
                }

            } else if (chbAlumnos.isSelected()){
                if(copiarJornada(copiaJornada)) {
                    toast.show((Stage) apCopiarJornada.getScene().getWindow(), "Duplicado de jornada realizado.");
                    escenario.close();
                } else {
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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    insetaOK = false;
                }

                if(insetaOK) {
                    toast.show((Stage) apCopiarJornada.getScene().getWindow(), "Duplicado de jornada realizado.");
                    escenario.close();
                } else {
                    mensajeAviso(Alert.AlertType.ERROR,
                            "Fallo al copiar Jornada.",
                            "",
                            "No se ha podido copiar la Jornada.");
                }
            }
        }
    }

    private boolean copiarJornada(Jornada jornadaCopia) {
        try {
            return conexionBD.insertarJornadaCompleta(jornadaCopia);        
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


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
        lbJornada.setText(jornada.getFecha().format(formatter));
        //lbDia.setText(jornada.obtenerDiaSemana());
        lbSemana.setText(Integer.toString(jornada.getFecha().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR))); 
        lbDia.setText(jornada.getFecha().getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES")));
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
