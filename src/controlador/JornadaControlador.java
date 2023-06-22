package controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import baseDatos.ConexionBD;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.LocalDateStringConverter;
import modelo.Alumno;
import modelo.Clase;
import modelo.Direccion;
import modelo.Genero;
import modelo.HoraClase;
import modelo.Jornada;
import modelo.TipoClase;
import modelo.Toast;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

public class JornadaControlador implements Initializable {
	
	private PrincipalControlador controladorPincipal;
	private Stage escenario;
	private DateTimeFormatter formatter;
	private Jornada jornada = null;
	private Jornada jornadaOriginal;
	private ObservableList<Alumno> listadoAlumnosGeneral;
	private ConexionBD conexionBD;
	private Alert alerta;
	private Toast toast;
	

	@FXML
    private ImageView ivBotonCopiarJornada;

    @FXML
    private ImageView ivBotonCopiarSemana;

	@FXML
    private ImageView ivBotonCrearJornada;

	@FXML
    private ImageView ivBotonBorrarJornada;

	@FXML
    private ImageView ivAnteriorJornada;

	@FXML
    private ImageView ivSiguienteJornada;

    @FXML
    private Button btnBorrar;

    @FXML
    private Button btnGuardar;
	
	@FXML
	private BorderPane bdJornada;
	
	@FXML
    private DatePicker dpFecha;
	
	@FXML
    private Label lbDiaSemana;
	
	@FXML
    private TextArea taComentarios;
	
	@FXML
    private ListView<Alumno> lvClase1;
	private ObservableList<Alumno> listaClase1;

    @FXML
    private ListView<Alumno> lvClase2;
    private ObservableList<Alumno> listaClase2;

    @FXML
    private ListView<Alumno> lvClase3;
    private ObservableList<Alumno> listaClase3;

    @FXML
    private ListView<Alumno> lvClase4;
    private ObservableList<Alumno> listaClase4;

    @FXML
    private ListView<Alumno> lvClase5;
    private ObservableList<Alumno> listaClase5;

    @FXML
    private ListView<Alumno> lvClase6;
    private ObservableList<Alumno> listaClase6;

    @FXML
    private ListView<Alumno> lvClase7;
    private ObservableList<Alumno> listaClase7;

    @FXML
    private ListView<Alumno> lvClase8;
    private ObservableList<Alumno> listaClase8;
    
    @FXML
    private Label lbHoraClase1;

    @FXML
    private Label lbHoraClase2;

    @FXML
    private Label lbHoraClase3;

    @FXML
    private Label lbHoraClase4;

    @FXML
    private Label lbHoraClase5;

    @FXML
    private Label lbHoraClase6;

    @FXML
    private Label lbHoraClase7;

    @FXML
    private Label lbHoraClase8;

    @FXML
    private Label lbTipoClase1;

    @FXML
    private Label lbTipoClase2;

    @FXML
    private Label lbTipoClase3;

    @FXML
    private Label lbTipoClase4;

    @FXML
    private Label lbTipoClase5;

    @FXML
    private Label lbTipoClase6;

    @FXML
    private Label lbTipoClase7;

    @FXML
    private Label lbTipoClase8;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Cargar imagenes en ImageView. //FALTA CARGAR LAS IMAGENES DEL RESTO DE BOTONES.
        Image imagenCrearJornada;
        try {
        	imagenCrearJornada = new Image("/recursos/circulo_flecha_1.png");
        } catch (Exception e) {
        	imagenCrearJornada = new Image(getClass().getResourceAsStream("/recursos/circulo_flecha_1.png"));
        }

        ivBotonCrearJornada.setImage(imagenCrearJornada);
        
        btnBorrar.getStyleClass().add("boton_rojo");
		
		conexionBD = ConexionBD.getInstance(); //Obtenemos una istancia de la Conexion a BD.
		
		toast = new Toast();
		
		//deshabilita que se puedan seleccionar el contenido del los listView.
		lvClase1.setFocusModel(null); 
		lvClase2.setFocusModel(null);
		lvClase3.setFocusModel(null);
		lvClase4.setFocusModel(null);
		lvClase5.setFocusModel(null);
		lvClase6.setFocusModel(null);
		lvClase7.setFocusModel(null);
		lvClase8.setFocusModel(null);

		//Deshabilitar la edición y la selección del TextArea
		taComentarios.setMouseTransparent(true);
		taComentarios.setFocusTraversable(false);
		
		//Desactivar la selección y la interacción de los ListView.
		lvClase1.setMouseTransparent(true);
		lvClase2.setMouseTransparent(true);
		lvClase3.setMouseTransparent(true);
		lvClase4.setMouseTransparent(true);
		lvClase5.setMouseTransparent(true);
		lvClase6.setMouseTransparent(true);
		lvClase7.setMouseTransparent(true);
		lvClase8.setMouseTransparent(true);
		
		
		lvClase1.setFocusTraversable(false);
		lvClase2.setFocusTraversable(false);
		lvClase3.setFocusTraversable(false);
		lvClase4.setFocusTraversable(false);
		lvClase5.setFocusTraversable(false);
		lvClase6.setFocusTraversable(false);
		lvClase7.setFocusTraversable(false);
		lvClase8.setFocusTraversable(false);
		
		//Modifica el formato en el que se muestra la fecha en el dtFechaCompra
        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");//Formato dd/MM/yy
        dpFecha.setConverter(new LocalDateStringConverter(formatter, null));
        
        //Para poder escribir directamente la fecha
        dpFecha.setEditable(false); //-> Poner a true para que sea editable.

		dpFecha.setValue(LocalDate.now()); //Establece la fecha de hoy en el datePiker. 
        
        dpFecha.setOnAction(event -> {
        	LocalDate fechaSeleccionada = dpFecha.getValue(); //Guardo la fecha seleccionada en fechaSeleccionada.
            lbDiaSemana.setText(obtenerDiaSemana(fechaSeleccionada)); //Pongo el dia de la semana de la fecha seleecionada en el label lbDiaSemana.
            
            //LLamar a base de datos para rescatar la jornada
            //inicializacion(datos.getJornada(fechaSeleccionada));
			try {
				//inicializacion(conexionBD.getJornada(fechaSeleccionada.toString()));
				inicializacion(conexionBD.getJornadaCompleta(fechaSeleccionada.toString()));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        });
	}

	@FXML
    void anteriorJornada(MouseEvent event) {
		LocalDate fechaAnterio = dpFecha.getValue().plusDays(-1);
		Jornada j = null;
		try {
			j = conexionBD.getJornadaCompleta(fechaAnterio.toString());
			inicializacion(j);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(j == null) {
			dpFecha.setValue(fechaAnterio);
			lbDiaSemana.setText(obtenerDiaSemana(fechaAnterio));
		}
    }

	@FXML
    void siguienteJornada(MouseEvent event) {
		LocalDate fechaSiguiente = dpFecha.getValue().plusDays(1);
		Jornada j = null;
		try {
			j = conexionBD.getJornadaCompleta(fechaSiguiente.toString());
			inicializacion(j);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(j == null) {
			dpFecha.setValue(fechaSiguiente);
			lbDiaSemana.setText(obtenerDiaSemana(fechaSiguiente));
		}
    }
	
	@FXML
	void clickClase1(MouseEvent event) {
		cargaClase(0);
	}
	
	@FXML
    void clickClase2(MouseEvent event) {
		cargaClase(1);
    }

    @FXML
    void clickClase3(MouseEvent event) {
    	cargaClase(2);
    }

    @FXML
    void clickClase4(MouseEvent event) {
    	cargaClase(3);
    }

    @FXML
    void clickClase5(MouseEvent event) {
    	cargaClase(4);
    }

    @FXML
    void clickClase6(MouseEvent event) {
    	cargaClase(5);
    }

    @FXML
    void clickClase7(MouseEvent event) {
    	cargaClase(6);
    }

    @FXML
    void clickClase8(MouseEvent event) {
    	cargaClase(7);
    }

	@FXML
	void botonBorrar(MouseEvent event) {
		if(jornada != null) {
			jornada.setComentario("");
		}
	}

	@FXML
	void botonGuardar(MouseEvent event) {
		try {
			conexionBD.actualizarComentarioJornada(jornada);
			toast.show((Stage) bdJornada.getScene().getWindow(), "Comentario modificado!!.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			toast.show((Stage) bdJornada.getScene().getWindow(), "ERROR al intentar modificar el comentario de la jornada!!.");
		}
	}

	@FXML
    void crearJornadaBD(MouseEvent event) {

		//Si la jornada que esta cargada es null(No esta creada)
		if(jornada == null) {
			
			Jornada j = crearJornada();
			
			//Hacer que inserte la jornada en la base de datos
			//datos.addJornada(j);
			try {
				conexionBD.insertarJornada(j);
				inicializacion(j);
				toast.show((Stage) bdJornada.getScene().getWindow(), "Jornada creada correctamente!!");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				toast.show((Stage) bdJornada.getScene().getWindow(), "ERROR al intentar crear la jornada!!");
			}
			//Si se ha insertado correctamente en la base de datos, llamar al metodo inicializacion de esta clase y pasarle la jornada.
			
		} else {
			toast.show((Stage) bdJornada.getScene().getWindow(), "Esta Jornada ya esta creada!!");
		}
    }

	@FXML
    void borrarJornadaBD(MouseEvent event) {
		if(jornada != null) {
			//llamar al bd para borrar la jornada
			//si la borra mostrar mensaje y cargar todo a null
			//si no la borra, mostrar mensaje de error.
			boolean borradoOK = false;
			
			alerta = new Alert(AlertType.CONFIRMATION);
            alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
            alerta.setTitle("Borrar Jornada");
            alerta.setHeaderText("Se va ha borrar los datos de la jornada " + jornada.getFecha().format(formatter) + " y sus clases.");
            alerta.setContentText("¿Estas seguro que Quieres borrar esta jornada?");
            alerta.initStyle(StageStyle.DECORATED);
            alerta.initOwner(escenario);
            alerta.initModality(Modality.APPLICATION_MODAL);

            Optional<ButtonType> result = alerta.showAndWait();
    		if (result.get() == ButtonType.OK) {
                try {
                    borradoOK = conexionBD.borrarJornada(jornada);
                } catch (SQLException e) {
                    // meter en log.
                    e.printStackTrace();
                }

				if (borradoOK) {
					Jornada j = null;
					inicializacion(j);
					mensajeAviso(Alert.AlertType.INFORMATION,
							"Borrar Jornada.",
							"",
							"Se ha borrado la Jornada.");
				} else {
					mensajeAviso(Alert.AlertType.ERROR,
							"Borrar Jornada.",
							"",
							"No se ha podido borrar la Jornada.");
				}
    			
    		} else {
    			//Si se pulsa el boton de cancelar.
    		}

		} else {
			toast.show(escenario, "Esta Jornada No esta creada!!\nNo puedes borrar una Jornada que no esta creada.");
		}
    }

	@FXML
    void copiarJornada(MouseEvent event) {
		if(jornada == null) {
			toast.show(escenario, "Esta Jornada No esta creada!!\nNo puedes duplicar una Jornada que no esta creada.");
		} else {
			try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/jornadaCardDuplicarVista.fxml"));
            AnchorPane copiarJornada;
            copiarJornada = (AnchorPane) loader.load();
            JornadaCardDuplicarControlador controller = loader.getController(); // cargo el controlador.
            
            Stage ventana= new Stage();
            ventana.initOwner(escenario);
            ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
            ventana.initStyle(StageStyle.UNDECORATED);

			URL rutaIcono = getClass().getResource("/recursos/lis_logo_1.png"); // guardar ruta de recurso imagen
			ventana.getIcons().add(new Image(rutaIcono.toString())); // poner imagen icono a la ventana	
            
            controller.setStage(ventana);
			controller.setJornada(jornada);
			controller.iniciar();
			
            Scene scene = new Scene(copiarJornada);
            scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); //Añade hoja de estilos.
            ventana.setScene(scene);
            ventana.showAndWait();
        } catch (IOException e) {
            //poner log.
            e.printStackTrace();
        }
		}
    }

    @FXML
    void copiarSemana(MouseEvent event) {
		if(jornada == null) {
			toast.show(escenario, "Esta Jornada No esta creada!!\nNo puedes duplicar una semana desde una jornada que no esta creada.");
		} else {
			try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/semanaCardDuplicarVista.fxml"));
            AnchorPane copiarSemana;
            copiarSemana = (AnchorPane) loader.load();
            SemanaCardDuplicarControlador controller = loader.getController(); // cargo el controlador.
            
            Stage ventana= new Stage();
            ventana.initOwner(escenario);
            ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
            ventana.initStyle(StageStyle.UNDECORATED);

			URL rutaIcono = getClass().getResource("/recursos/lis_logo_1.png"); // guardar ruta de recurso imagen
			ventana.getIcons().add(new Image(rutaIcono.toString())); // poner imagen icono a la ventana	
            
            controller.setStage(ventana);
			controller.setJornada(jornada);
			controller.iniciar();
			
            Scene scene = new Scene(copiarSemana);
            scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); //Añade hoja de estilos.
            ventana.setScene(scene);
            ventana.showAndWait();
        } catch (IOException e) {
            //poner log.
            e.printStackTrace();
        }
		}
    }
	

	/**
	 * Crea una Jornada con sus clases asignadole valores por defecto.
	 * 
	 * @return Un objeto de tipo Jornada con datos por defecto.
	 */
	private Jornada crearJornada() {
		Jornada j = new Jornada(dpFecha.getValue(), "");
		Clase[] clases = new Clase[8];
		clases[0] = new Clase(-1, 1, TipoClase.PILATES, HoraClase.HORA_9_MEDIA, "");
		clases[1] = new Clase(-1, 2, TipoClase.PILATES, HoraClase.HORA_10_MEDIA, "");
		clases[2] = new Clase(-1, 3, TipoClase.PILATES, HoraClase.HORA_11_MEDIA, "");
		clases[3] = new Clase(-1, 4, TipoClase.PILATES, HoraClase.HORA_12_MEDIA, "");
		clases[4] = new Clase(-1, 5, TipoClase.PILATES, HoraClase.HORA_17_MEDIA, "");
		clases[5] = new Clase(-1, 6, TipoClase.PILATES, HoraClase.HORA_18_MEDIA, "");
		clases[6] = new Clase(-1, 7, TipoClase.PILATES, HoraClase.HORA_19_MEDIA, "");
		clases[7] = new Clase(-1, 8, TipoClase.PILATES, HoraClase.HORA_20_MEDIA, "");

		j.setClases(clases); //Establece el Array de Clase a la jornada.

		return j;
	}
	

	private void cargaClase(int numeroClase) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/ClaseVista.fxml"));
			BorderPane ClasePilates;
			//loader.setResources(recursos);
			ClasePilates = (BorderPane) loader.load();
			controladorPincipal.setPane(ClasePilates);
			
			ClaseControlador controller = loader.getController(); //cargo el controlador.
			controller.setControladorPrincipal(controladorPincipal);
			controller.setStage(escenario);
			controller.setJornada(jornada);
			controller.setListaAlumnos(listadoAlumnosGeneral);
			controller.setClaseIniciacion(numeroClase);
			
		} catch (IOException e) {
			System.out.println("-ERROR- Fallo al cargar ClaseVista.fxml" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	public void inicializacion(Jornada jorn) {
		if(jorn == null) {
			//toast.show((Stage) bdJornada.getScene().getWindow(), "Esta jornada no esta creada");
			toast.show(escenario, "Esta jornada no esta creada");
			//Si ya hay cargado datos en pantalla, borra los datos.
			if(jornada != null) {
				listaClase1.clear();
				listaClase2.clear();
				listaClase3.clear();
				listaClase4.clear();
				listaClase5.clear();
				listaClase6.clear();
				listaClase7.clear();
				listaClase8.clear();
				
				lbHoraClase1.textProperty().unbind();
				lbTipoClase1.textProperty().unbind();
				
				lbHoraClase2.textProperty().unbind();
				lbTipoClase2.textProperty().unbind();
				
				lbHoraClase3.textProperty().unbind();
				lbTipoClase3.textProperty().unbind();
				
				lbHoraClase4.textProperty().unbind();
				lbTipoClase4.textProperty().unbind();
				
				lbHoraClase5.textProperty().unbind();
				lbTipoClase5.textProperty().unbind();
				
				lbHoraClase6.textProperty().unbind();
				lbTipoClase6.textProperty().unbind();
				
				lbHoraClase7.textProperty().unbind();
				lbTipoClase7.textProperty().unbind();
				
				lbHoraClase8.textProperty().unbind();
				lbTipoClase8.textProperty().unbind();
				
				//unBinding a TextArea comentario.
				taComentarios.textProperty().unbindBidirectional(jornada.comentarioProperty());
				
				taComentarios.clear();
				
				//Deshabilitar la edición y la selección del TextArea
				taComentarios.setEditable(false);
				taComentarios.setFocusTraversable(false);
				taComentarios.setMouseTransparent(true);
				
				lbHoraClase1.setText("");
				lbHoraClase2.setText("");
				lbHoraClase3.setText("");
				lbHoraClase4.setText("");
				lbHoraClase5.setText("");
				lbHoraClase6.setText("");
				lbHoraClase7.setText("");
				lbHoraClase8.setText("");
				
				lbTipoClase1.setText("");
				lbTipoClase2.setText("");
				lbTipoClase3.setText("");
				lbTipoClase4.setText("");
				lbTipoClase5.setText("");
				lbTipoClase6.setText("");
				lbTipoClase7.setText("");
				lbTipoClase8.setText("");
				
				//Desactivar la selección y la interacción de los ListView.
				lvClase1.setMouseTransparent(true);
				lvClase2.setMouseTransparent(true);
				lvClase3.setMouseTransparent(true);
				lvClase4.setMouseTransparent(true);
				lvClase5.setMouseTransparent(true);
				lvClase6.setMouseTransparent(true);
				lvClase7.setMouseTransparent(true);
				lvClase8.setMouseTransparent(true);
				
				
				lvClase1.setFocusTraversable(false);
				lvClase2.setFocusTraversable(false);
				lvClase3.setFocusTraversable(false);
				lvClase4.setFocusTraversable(false);
				lvClase5.setFocusTraversable(false);
				lvClase6.setFocusTraversable(false);
				lvClase7.setFocusTraversable(false);
				lvClase8.setFocusTraversable(false);
				
				//La jornada que esta cargada la ponemos a null
				jornada = null;
			}
			
			
			
			/*
			alerta = new Alert(Alert.AlertType.CONFIRMATION);
			//alerta.getDialogPane().getStylesheets().add(getClass().getResource("/estilos/StylesAlert.css").toExternalForm()); //Añade hoja de estilos.
			alerta.setTitle("Crear Jornada");
			alerta.setHeaderText("No hay Jornada creada con fecha de " + LocalDate.now());
			alerta.setContentText("¿Quieres crear la Jornada?");
			
			Optional<ButtonType> result = alerta.showAndWait();
			if (result.get() == ButtonType.OK) {
				crearJornada();
			} else {
				toast.show((Stage) bdJornada.getScene().getWindow(), "Cancelada la creacion de Jornada");
			}
			*/
		} 
		
		//Si la jornada pasada como parametro no es null, hacemos el proceso de binding
		if(jorn != null) {
			this.jornadaOriginal = jorn; //Asignamos la jornada pasada como parametro a jornadaOriginal.
			jornada = new Jornada(jornadaOriginal); //pasamos los datos de jornadaOriginal a jornada para que los cambios no afecten directamente a jornadaOriginal
			dpFecha.setValue(jornada.getFecha());	//Asignamos la fecha de la jornada al datePiker.
			lbDiaSemana.setText(jornada.obtenerDiaSemana()); //Ponemos el dia de la semana de la jornada en el Label lbDiaSemana.
			
			//Cambio los alumnos que tiene cada clase por los alumnos de la aplicacion
			for(Clase clase : jorn.getClases()) {
				if(!clase.getListaAlumnos().isEmpty()) {
					clase.setListaAlumnos(comprobarListaClase(clase.getListaAlumnos()));
				}
			}

			

			//Inicializacion del listView. 
			//listaClase1 = FXCollections.observableArrayList(jornada.getListaClases().get(0).getListaAlumnos());
			listaClase1 = FXCollections.observableArrayList(jornada.getClase(0).getListaAlumnos());
			lvClase1.setItems(listaClase1);
			
			listaClase2 = FXCollections.observableArrayList(jornada.getClase(1).getListaAlumnos());
			lvClase2.setItems(listaClase2);
			
			listaClase3 = FXCollections.observableArrayList(jornada.getClase(2).getListaAlumnos());
			lvClase3.setItems(listaClase3);
			
			listaClase4 = FXCollections.observableArrayList(jornada.getClase(3).getListaAlumnos());
			lvClase4.setItems(listaClase4);
			
			listaClase5 = FXCollections.observableArrayList(jornada.getClase(4).getListaAlumnos());
			lvClase5.setItems(listaClase5);
		
			listaClase6 = FXCollections.observableArrayList(jornada.getClase(5).getListaAlumnos());
			lvClase6.setItems(listaClase6);
			
			listaClase7 = FXCollections.observableArrayList(jornada.getClase(6).getListaAlumnos());
			lvClase7.setItems(listaClase7);
			
			listaClase8 = FXCollections.observableArrayList(jornada.getClase(7).getListaAlumnos());
			lvClase8.setItems(listaClase8);
			
			//Forma sin binding de poner los datos en los label.
			//lbHoraClase1.setText(jornada.getListaClases().get(0).getHoraClase().toString());
			//lbTipoClase1.setText(jornada.getListaClases().get(0).getTipo().toString());
			
			
			//Binding a Labels.
			lbHoraClase1.textProperty().bind(jornada.getClase(0).horaClaseProperty().asString());
			lbTipoClase1.textProperty().bind(jornada.getClase(0).tipoProperty().asString());
			
			lbHoraClase2.textProperty().bind(jornada.getClase(1).horaClaseProperty().asString());
			lbTipoClase2.textProperty().bind(jornada.getClase(1).tipoProperty().asString());
			
			lbHoraClase3.textProperty().bind(jornada.getClase(2).horaClaseProperty().asString());
			lbTipoClase3.textProperty().bind(jornada.getClase(2).tipoProperty().asString());
			
			lbHoraClase4.textProperty().bind(jornada.getClase(3).horaClaseProperty().asString());
			lbTipoClase4.textProperty().bind(jornada.getClase(3).tipoProperty().asString());
			
			lbHoraClase5.textProperty().bind(jornada.getClase(4).horaClaseProperty().asString());
			lbTipoClase5.textProperty().bind(jornada.getClase(4).tipoProperty().asString());
			
			lbHoraClase6.textProperty().bind(jornada.getClase(5).horaClaseProperty().asString());
			lbTipoClase6.textProperty().bind(jornada.getClase(5).tipoProperty().asString());
			
			lbHoraClase7.textProperty().bind(jornada.getClase(6).horaClaseProperty().asString());
			lbTipoClase7.textProperty().bind(jornada.getClase(6).tipoProperty().asString());
			
			lbHoraClase8.textProperty().bind(jornada.getClase(7).horaClaseProperty().asString());
			lbTipoClase8.textProperty().bind(jornada.getClase(7).tipoProperty().asString());
			
			//Binding a TextArea comentario.
			taComentarios.textProperty().bindBidirectional(jornada.comentarioProperty());
			
			//habilitar la edición y la selección del TextArea
			taComentarios.setEditable(true);
			taComentarios.setFocusTraversable(true);
			taComentarios.setMouseTransparent(false);
			
			//Habilitar la selección y la interacción los ListView.
			lvClase1.setMouseTransparent(false);
			lvClase2.setMouseTransparent(false);
			lvClase3.setMouseTransparent(false);
			lvClase4.setMouseTransparent(false);
			lvClase5.setMouseTransparent(false);
			lvClase6.setMouseTransparent(false);
			lvClase7.setMouseTransparent(false);
			lvClase8.setMouseTransparent(false);
			
			lvClase1.setFocusTraversable(true);
			lvClase2.setFocusTraversable(true);
			lvClase3.setFocusTraversable(true);
			lvClase4.setFocusTraversable(true);
			lvClase5.setFocusTraversable(true);
			lvClase6.setFocusTraversable(true);
			lvClase7.setFocusTraversable(true);
			lvClase8.setFocusTraversable(true);
			
		} 
	}
	
	private ArrayList<Alumno> comprobarListaClase(ArrayList<Alumno> listaAlumnos) {
		ArrayList<Alumno> nuevaListaAlumnos = new ArrayList<Alumno>();
		//listadoAlumnos
		for(Alumno alumno : listaAlumnos) {
			for(Alumno alumnoApp : listadoAlumnosGeneral) {
				if(alumno.getId() == alumnoApp.getId()) {
					nuevaListaAlumnos.add(alumnoApp);
					break;
				}
			}
		}
		return nuevaListaAlumnos;
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

	/**
	 * Establece para este controlador, el controlador principal de la aplicacion.
	 * 
	 * @param principal Controlador principal.
	 */
	public void setControladorPrincipal(PrincipalControlador principal) {
		controladorPincipal = principal;
	}

	/**
	 * Establece la lista de alumnos.
	 * 
	 * @param lista La lista de donde se obtienen los Alumnos
	 */
	public void setListaAlumnos(ObservableList<Alumno> lista) {
		listadoAlumnosGeneral = lista;
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
	
	
	// ATENCION: El metodo obtenerDiaSemana es para borrar. lo he puesto en la clase Jornada.
	/**
	 * Obtiene un String con el dia de la semana a partir de la fecha pasada como parametro.
	 * 
	 * @param dia Objeto de tipo LocalDate de donde se obtiene la fecha.
	 * @return Un String con el dia de la semana.
	 */
	public String obtenerDiaSemana(LocalDate dia) {
		String nombreDia = "";
		DayOfWeek diaDeLaSemana = dia.getDayOfWeek();
		switch (diaDeLaSemana.name()) {
		case "MONDAY" -> nombreDia = "Lunes";
		case "TUESDAY" -> nombreDia = "Martes";
		case "WEDNESDAY" -> nombreDia = "Miércoles";
		case "THURSDAY" -> nombreDia = "Jueves";
		case "FRIDAY" -> nombreDia = "Viernes";
		case "SATURDAY" -> nombreDia = "Sábado";
		case "SUNDAY" -> nombreDia = "Domingo";
		}
		
		return nombreDia;
	}
	
	
	
}
