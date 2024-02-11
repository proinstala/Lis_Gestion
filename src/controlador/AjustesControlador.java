package controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import baseDatos.ConexionBD;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.StringConverter;
import modelo.Alumno;
import modelo.GrupoAlumnos;
import utilidades.Constants;
import utilidades.Toast;


public class AjustesControlador implements Initializable {

    private ObservableList<Alumno> listadoAlumnosGeneral;
    private ObservableList<String> listadoProvincias;
    private ObservableList<String> listadoLocalidades;
    private ObservableList<GrupoAlumnos> listadoGruposAlumnosGeneral;
    private Map<Integer, Double> precios_clases;

    private DecimalFormat decimalFormat;
    private ConexionBD conexionBD;
    private Logger logUser;
    private Toast toast;
    private Double tiempoDelay = 0.5;
    
    @FXML
    private ComboBox<GrupoAlumnos> cbGrupo;

    @FXML
    private ComboBox<String> cbLocalidad;

    @FXML
    private ComboBox<Integer> cbNumeroAsistencias;

    @FXML
    private ComboBox<String> cbProvincia;

    @FXML
    private GridPane gpAjustes;

    @FXML
    private ImageView ivAddGrupo;

    @FXML
    private ImageView ivBorrarGrupo;

    @FXML
    private ImageView ivEditarGrupo;

    @FXML
    private ImageView ivAcercaDe;

    @FXML
    private ImageView ivAddLocalidad;

    @FXML
    private ImageView ivBorrarLocalidad;

    @FXML
    private ImageView ivEditarLocalidad;

    @FXML
    private ImageView ivEditarPrecio;

    @FXML
    private TextField tfLocalidad;

    @FXML
    private TextField tfPrecioClase;

    @FXML
    private TextField tfDescripcionGrupo;

    @FXML
    private TextField tfNombreGrupo;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Cargar imagenes en ImageView.
        Image imagenAdd;
        Image imagenBorrar;
        Image imagenEditar;
        Image imagenAcercaDe;
        try {
            //Intentar cargar la imagen desde el recurso en el IDE y en el JAR.
            imagenAdd = new Image(getClass().getResourceAsStream("/recursos/add-circle-linear_30.png")); //Forma desde IDE y JAR.
            imagenBorrar = new Image(getClass().getResourceAsStream("/recursos/close-circle-linear_30.png"));
            imagenEditar = new Image(getClass().getResourceAsStream("/recursos/pen-new-round-linear_30.png"));
            imagenAcercaDe = new Image(getClass().getResourceAsStream("/recursos/info_32.png"));
        } catch (Exception e) {
            //Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR.
        	imagenAdd = new Image("/recursos/add-circle-linear_30.png"); //Forma desde el JAR.
            imagenBorrar = new Image("/recursos/close-circle-linear_30.png");
            imagenEditar = new Image("/recursos/pen-new-round-linear_30.png");
            imagenAcercaDe = new Image("/recursos/info_32.png");
        }
        //Establecer las imagenes cargadas en el ImageView.
        ivAddLocalidad.setImage(imagenAdd);
        ivBorrarLocalidad.setImage(imagenBorrar);
        ivEditarPrecio.setImage(imagenEditar);
        ivEditarLocalidad.setImage(imagenEditar);
        ivAcercaDe.setImage(imagenAcercaDe); 
        ivAddGrupo.setImage(imagenAdd);
        ivBorrarGrupo.setImage(imagenBorrar);
        ivEditarGrupo.setImage(imagenEditar);

        //Crear Tooltip.
        Tooltip tltAddLocalidad = new Tooltip("Añadir Nueva Localidad");
		Tooltip tltBorrarLocalidad = new Tooltip("Eliminar Localidad");
		Tooltip tltEditarPrecio = new Tooltip("Editar Precio");
		Tooltip tltEditarLocalidad = new Tooltip("Editar Localidad");
        Tooltip tltAcercaDe = new Tooltip("Acerca De");
        Tooltip tltAddGrupo = new Tooltip("Añadir Nuevo Grupo");
		Tooltip tltBorrarGrupo = new Tooltip("Eliminar Grupo");
		Tooltip tltEditarGrupo = new Tooltip("Editar Grupo");

        tltAddLocalidad.setShowDelay(Duration.seconds(tiempoDelay)); //Establecer retardo de aparición.
		tltBorrarLocalidad.setShowDelay(Duration.seconds(tiempoDelay));
		tltEditarPrecio.setShowDelay(Duration.seconds(tiempoDelay)); 
		tltEditarLocalidad.setShowDelay(Duration.seconds(tiempoDelay)); 
        tltAcercaDe.setShowDelay(Duration.seconds(tiempoDelay)); 
        tltAddGrupo.setShowDelay(Duration.seconds(tiempoDelay)); 
        tltBorrarGrupo.setShowDelay(Duration.seconds(tiempoDelay));
        tltEditarGrupo.setShowDelay(Duration.seconds(tiempoDelay));

		Tooltip.install(ivAddLocalidad, tltAddLocalidad); //Establecer Tooltip a ImageView.
		Tooltip.install(ivBorrarLocalidad, tltBorrarLocalidad);
		Tooltip.install(ivEditarPrecio, tltEditarPrecio);
		Tooltip.install(ivEditarLocalidad, tltEditarLocalidad);
        Tooltip.install(ivAcercaDe, tltAcercaDe); 
        Tooltip.install(ivAddGrupo, tltAddGrupo); 
        Tooltip.install(ivBorrarGrupo, tltBorrarGrupo); 
        Tooltip.install(ivEditarGrupo, tltEditarGrupo); 
       
        logUser = Logger.getLogger(Constants.USER); //Crea una instancia de la clase Logger asociada al nombre de registro.
        conexionBD = ConexionBD.getInstance();      //Obtener una instancia de la clase ConexionBD utilizando el patrón Singleton.
        toast = new Toast();

        decimalFormat = new DecimalFormat("#0.00");

        configurarControles(); //Configura los controles de la interfaz de usuario.
    }


    /**
     * Configura los controles de la interfaz de usuario.
     * Carga los datos en los ComboBox y asigna valores iniciales.
     * Asigna listeners para manejar eventos.
     * 
     */
    private void configurarControles() {
        if(obtenerPreciosClase()) {
            cbNumeroAsistencias.setItems(FXCollections.observableArrayList(precios_clases.keySet()));
            cbNumeroAsistencias.getSelectionModel().selectedItemProperty().addListener((o, nv, ov) -> {
                if(ov != 0) {
                    tfPrecioClase.setText(decimalFormat.format(precios_clases.get(ov)));
                }
            });
        } else {
            cbNumeroAsistencias.setItems(FXCollections.observableArrayList(new Integer[] {1,2,3,4}));
        }

        cbProvinciaSetup();

        if(listadoProvincias != null) {
            cbProvincia.setValue("Murcia"); //Establezco Murcia como predeterminado a la hora de inicar el controlador
            cbLocalidadSetup(cbProvincia.getValue());
        }

        cbLocalidad.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            tfLocalidad.setText(nv);
        });
        
        //Configurar un evento de clic del ratón para la imagen "ivEditarPrecio" para editar el precio de las clases.
        ivEditarPrecio.setOnMouseClicked(e -> {
            if (comporobarCamposPrecios()) {
                Double precio = Double.parseDouble(tfPrecioClase.getText().replace(",", "."));

                try {
                    if (conexionBD.actualizarPrecioClase(cbNumeroAsistencias.getValue() , precio)) {
                        precios_clases.put(cbNumeroAsistencias.getValue(), precio);

                        //Mostrar una notificación de éxito en la interfaz gráfica.
                        toast.show((Stage) gpAjustes.getScene().getWindow(),"Precio Actualizado!");
                        
                    } else {
                        //Mostrar una notificación en la interfaz gráfica.
                        toast.show((Stage) gpAjustes.getScene().getWindow(),"NO se ha actualizado el precio");
                    }
                    
                } catch (Exception ex) {
                    toast.show((Stage) gpAjustes.getScene().getWindow(),"Fallo al actualizar precio.\n" + e.toString());

                    logUser.severe("Excepción: " + e.toString());
                    ex.printStackTrace();
                }

            }
        });

        //Configurar un evento de clic del ratón para la imagen "ivEditarLocalidad" para editar el nombre de una localidad.
        ivEditarLocalidad.setOnMouseClicked(e -> {
            if (comporobarCamposLocalidad("ivEditarLocalidad")) { 
                try {
                    String provincia = cbProvincia.getValue();
                    String oldLocalidad = cbLocalidad.getValue();
                    String newLocalidad = tfLocalidad.getText();

                    if (conexionBD.actualizarNombreLocalidad(provincia, oldLocalidad, newLocalidad)) {
                        
                        for (Alumno alumno : listadoAlumnosGeneral) {
                            if (alumno.getDireccion().getLocalidad().equals(oldLocalidad)) {
                                alumno.getDireccion().setLocalidad(newLocalidad);
                            }   
                        }

                        int indice = -1;
                        for (int i = 0; i < listadoLocalidades.size(); i++) {
                            if (listadoLocalidades.get(i).equals(oldLocalidad)) {
                                indice = i;
                                break;
                            }
                        }

                        if (indice != -1) {
                            listadoLocalidades.set(indice, newLocalidad);
                            cbLocalidad.setValue(newLocalidad);
                        }

                        //Mostrar una notificación de éxito en la interfaz gráfica.
                        toast.show((Stage) gpAjustes.getScene().getWindow(),"Nombre de localidad modificado!");

                    } else {
                        //Mostrar una notificación en la interfaz gráfica.
                        toast.show((Stage) gpAjustes.getScene().getWindow(),"NO se ha modificado el Nombre de la localidad!");
                    }
                } catch (Exception ex) {
                    toast.show((Stage) gpAjustes.getScene().getWindow(),"Fallo al actualizar nombre de localidad.");

                    logUser.severe("Excepción: " + ex.toString());
                    ex.printStackTrace();
                }
            }
        });

        //Configurar un evento de clic del ratón para la imagen "ivAddLocalidad" para añadir una localidad.
        ivAddLocalidad.setOnMouseClicked(e -> {
            if (comporobarCamposLocalidad("ivAddLocalidad")) {

                String provincia = cbProvincia.getValue();
                String localidad = tfLocalidad.getText();

                try {
                    if(conexionBD.insertarLocalidad(provincia, localidad)) {
                        listadoLocalidades.add(localidad);
                        //Mostrar una notificación de éxito en la interfaz gráfica.
                        toast.show((Stage) gpAjustes.getScene().getWindow(),"Localidad añadida!");
                    } else {
                        //Mostrar una notificación en la interfaz gráfica.
                        toast.show((Stage) gpAjustes.getScene().getWindow(),"NO se ha añadido la localidad!");
                    }
                } catch (Exception ex) {
                    toast.show((Stage) gpAjustes.getScene().getWindow(),"Fallo al añadir la localidad.");

                    logUser.severe("Excepción: " + ex.toString());
                    ex.printStackTrace();
                }
            }
        });

        //Configurar un evento de clic del ratón para la imagen "ivBorrarLocalidad" para borrar una localidad.
        ivBorrarLocalidad.setOnMouseClicked(e -> {
            if (comporobarCamposLocalidad("ivBorrarLocalidad")) {

                String provincia = cbProvincia.getValue();
                String localidad = cbLocalidad.getSelectionModel().getSelectedItem();

                try {
                    if(conexionBD.borrarLocalidad(provincia, localidad)) {
                        listadoLocalidades.remove(localidad);
                        //Mostrar una notificación de éxito en la interfaz gráfica.
                        toast.show((Stage) gpAjustes.getScene().getWindow(),"Localidad borrada!");
                    } else {
                        //Mostrar una notificación en la interfaz gráfica.
                        toast.show((Stage) gpAjustes.getScene().getWindow(),"NO se ha borrado la localidad!");
                    }
                } catch (Exception ex) {
                    toast.show((Stage) gpAjustes.getScene().getWindow(),"Fallo al borrar la localidad.");

                    logUser.severe("Excepción: " + ex.toString());
                    ex.printStackTrace();
                }
            }
        });

        ivAcercaDe.setOnMouseClicked(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/acercaDeVista.fxml"));
                AnchorPane acercaDe;
                acercaDe = (AnchorPane) loader.load();
                AcercaDeControlador controller = loader.getController(); // cargo el controlador.
                
                Stage ventana= new Stage();
                ventana.initOwner((Stage) gpAjustes.getScene().getWindow());
                ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
                ventana.initStyle(StageStyle.UNDECORATED);

                URL rutaIcono = getClass().getResource("/recursos/lis_logo_1.png"); // guardar ruta de recurso imagen.
                ventana.getIcons().add(new Image(rutaIcono.toString())); // poner imagen icono a la ventana.

                Scene scene = new Scene(acercaDe);
                scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); //Añade hoja de estilos.
                ventana.setScene(scene);
                
                ventana.showAndWait();
            } catch (IOException eIo) {
                logUser.severe("Excepción al intentar cargar la Scene registroUser. " + eIo.toString());
                eIo.printStackTrace();
            } catch (Exception ex) {
                logUser.severe("Excepción: " + ex.toString());
                ex.printStackTrace();
            }
        });

        //Configurar un evento de clic del ratón para la imagen "ivAddGrupo" para añadir un grupo de alumnos.
        ivAddGrupo.setOnMouseClicked(e -> {
            //if (comporobarCamposLocalidad("ivAddLocalidad")) {
            if (true) {
                String nombre = tfNombreGrupo.getText();
                String descripcion = tfDescripcionGrupo.getText();
                GrupoAlumnos nuevoGrupoAlumnos = new GrupoAlumnos(-1, nombre, descripcion);

                try {
                    if(conexionBD.insertarGrupoAlumnos(nuevoGrupoAlumnos)) {
                        listadoGruposAlumnosGeneral.add(nuevoGrupoAlumnos);
                        //Mostrar una notificación de éxito en la interfaz gráfica.
                        toast.show((Stage) gpAjustes.getScene().getWindow(),"Grupo añadido!");
                        cbGrupo.getSelectionModel().select(nuevoGrupoAlumnos);
                    } else {
                        //Mostrar una notificación en la interfaz gráfica.
                        toast.show((Stage) gpAjustes.getScene().getWindow(),"NO se ha añadido el Grupo!");
                    }
                } catch (Exception ex) {
                    toast.show((Stage) gpAjustes.getScene().getWindow(),"Fallo al añadir el Grupo.");

                    logUser.severe("Excepción: " + ex.toString());
                }
            }
        });
    }


    /**
     * Configura el ComboBox cbGrupo.
     * 
     */
    private void cbGrupoSetup() {
        cbGrupo.setItems(listadoGruposAlumnosGeneral);

        //Establecer el texto a mostrar en el ComboBox utilizando un CellFactory.
        cbGrupo.setCellFactory(param -> new ListCell<GrupoAlumnos>() {
            @Override
            protected void updateItem(GrupoAlumnos grupo, boolean empty) {
                super.updateItem(grupo, empty);
                if (empty || grupo == null) {
                    setText(null);
                } else {
                    setText(grupo.getId() + " - " + grupo.getNombre()); //Mostrar el ID y el nombre del Alumno en el ComboBox cuando está desplegado.
                }
            }
        });

        //Establecer el texto a mostrar en el ComboBox cuando está desplegado utilizando un StringConverter.
        cbGrupo.setConverter(new StringConverter<GrupoAlumnos>() {
            @Override
            public String toString(GrupoAlumnos grupo) {
                if (grupo != null) {
                    //Mostrar el ID y el nombre del Alumno en el ComboBox.
                    return grupo.getNombre();
                }
                return null;
            }

            @Override
            public GrupoAlumnos fromString(String string) {
                // No se necesita esta implementación para este caso.
                return null;
            }
        });

        //Establece un listener para que cuando se seleccione un elemento del ComboBox cbAlumnos.
        cbGrupo.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            tfNombreGrupo.setText(nv.getNombre());
            tfDescripcionGrupo.setText(nv.getDescripcion());
            System.out.println("** PASO POR AQUI ---------------------------------------------------------");
        });
    }//FIN configurarCbAlumnos.


    /**
     * Obtiene los precios de las clases desde la base de datos.
     *
     * @return true si se obtuvieron los precios exitosamente, false en caso contrario.
     */
    private boolean obtenerPreciosClase() {
        try {
            precios_clases = conexionBD.getPrecioClases();
            if(precios_clases != null) {return true;} //Se obtuvieron los precios exitosamente.
        } catch (SQLException e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        }
        logUser.warning("Fallo al obtener los precios de clases de BD.");
        return false; //No se pudieron obtener los precios.
    }//FIN obtenerPreciosClase.


    /**
     * Configura el ComboBox cbProvincia.
     * 
     */
    private void cbProvinciaSetup() {
        try {
			listadoProvincias = FXCollections.observableArrayList(conexionBD.getProvincias());
		} catch (Exception e) {
            logUser.severe("Excepción: " + e.toString());
			e.printStackTrace();
		}

        //Carga en el ComboBox los items del listadoProvincias.
        cbProvincia.setItems(listadoProvincias);

        //Añado un listener al ComboBox de provincias para realizar una acción cuando se ha seleccionado un item.
        cbProvincia.setOnAction(event -> {
            cbLocalidadSetup(cbProvincia.getValue());
        });
    }


    /**
     * Configura el ComboBox cbLocalidad.
     * 
     * @param provincia La provincia de la que se obitnen las localidades.
     */
    private void cbLocalidadSetup(String provincia) {
        try {
			listadoLocalidades = FXCollections.observableArrayList(conexionBD.getLocalidades(provincia));

            //Carga en el ComboBox los items del listadoLocalidades.
            cbLocalidad.setItems(listadoLocalidades);

		} catch (Exception e) {
            logUser.severe("Excepción: " + e.toString());
			e.printStackTrace();
		}
    }


    /**
     * Comprueba si los campos del formulario precio de clases están correctamente completados.
     *
     * @return true si todos los campos están correctamente completados, false en caso contrario.
     */
    private boolean comporobarCamposPrecios() {
        boolean camposCorrectos = false;

        Pattern importePattern = Pattern.compile("[0-9]{1,3}[,][0-9]{1,2}"); //Patrón para validar el formato del importe.
        Matcher importeMatcher = importePattern.matcher(tfPrecioClase.getText());

        if (cbNumeroAsistencias.getValue() == null || cbNumeroAsistencias.getValue() < 1) {
            mensajeAviso(AlertType.WARNING, 
            "Número asistencia semanal.", 
            "",
            "Selecciona un número de asistencias para editar el precio.");
        
        } else if (!importeMatcher.matches()) {
            mensajeAviso(AlertType.WARNING, 
            "Importe de Asistencia.", 
            "Introduce un importe para el número de asistencias.",
                "Formato: de uno a 3 digitos, depues una coma seguido de 1 a 2 digitos."
                +"\nEjemplo: 45.50");
        } else if (cbNumeroAsistencias.getValue() > 1 && Double.parseDouble(tfPrecioClase.getText().replace(",", ".")) <= precios_clases.get(cbNumeroAsistencias.getValue() -1)) {
            mensajeAviso(AlertType.WARNING, 
            "Importe de Asistencia.", 
            "Importe NO valido para este número de asistencias.",
                "El importe no puede ser igual o inferior al importe para un número "
                +"\nde asistencias inferio al seleccionado.");
        } else if (cbNumeroAsistencias.getValue() < 4 && Double.parseDouble(tfPrecioClase.getText().replace(",", ".")) >= precios_clases.get(cbNumeroAsistencias.getValue() +1)) {
            mensajeAviso(AlertType.WARNING, 
            "Importe de Asistencia.", 
            "Importe NO valido para este número de asistencias.",
                "El importe no puede ser igual o superior al importe para un número "
                +"\nde asistencias superior al seleccionado.");
        } else {
            camposCorrectos = true; //Todos los campos están correctamente completados.
        }

        return camposCorrectos;
    }//FIN comporobarCamposPrecios.


    /**
     * Comprueba si los campos del formulario localidad están correctamente completados.
     * 
     * @param origenCall String que se usa para filtrar las acciones que realiza este metodo.
     * @return true si todos los campos están correctamente completados, false en caso contrario.
     */
    private boolean comporobarCamposLocalidad(String origenCall) {
        boolean camposCorrectos = false;
        
        //De 1 a 4 palabras separadas por un espacio en blanco que comienzan por una letra mayuscula y una longitud de 1 a 30 caracteres. 
        Pattern localidadPattern = Pattern.compile("^[A-ZÑ][a-zñáéíóú]{1,30}(\\s[A-ZÑ][a-zñáéíóú]{1,30}){0,3}$");
		Matcher localidadMatcher = localidadPattern.matcher(tfLocalidad.getText());

        if (origenCall != "ivAddLocalidad" && (cbLocalidad.getValue() == null || cbLocalidad.getSelectionModel().getSelectedIndex() < 0)) {
            mensajeAviso(AlertType.WARNING, 
            "Localidad No seleccionada.", 
            "",
            "Selecciona una localidad para poder editar su nombre.");
        
        } else if (!localidadMatcher.matches()) {
            mensajeAviso(AlertType.WARNING, 
            "Nombre de localidad incorrecto.", 
            "El nombre de localidad introducido no es valido.",
                "Formato: una letra mayuscula seguido de una minuscula de 1 a 30 caracteres. De 1 a 4 palabras."
                +"\nEjemplo: Santa Cruz");
        } else {
            camposCorrectos = true; //Todos los campos están correctamente completados.
        }

        if(camposCorrectos && origenCall != "ivBorrarLocalidad") {
            for(String localidad : listadoLocalidades) {
                if (localidad.equals(tfLocalidad.getText())) {

                    mensajeAviso(AlertType.WARNING, 
                    "Localidad.", 
                    "",
                    "Ya existe una localidad con ese nombre en la provincia " + cbProvincia.getValue().toString() + ".");

                    return false;
                }
            }
        }

        return camposCorrectos;
    }//FIN comporobarCamposLocalidad.


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
        alerta.initOwner((Stage) gpAjustes.getScene().getWindow());
        alerta.setHeaderText(cabecera);
        alerta.setContentText(cuerpo);
        alerta.initStyle(StageStyle.DECORATED);
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.showAndWait();
    }


    /**
     * Establece la lista de alumnos para este controlador.
     *
     * @param lista La lista de alumnos a establecer.
     */
    public void setListaAlumnos(ObservableList<Alumno> lista) {
        listadoAlumnosGeneral = lista;
	}


    /**
     * Establece la lista de grupos de alumnos para este controlador.
     * 
     * @param lista La lista de grupos de alumnos a establecer.
     */
    public void setListadoGruposAlumnos(ObservableList<GrupoAlumnos> lista) {
        listadoGruposAlumnosGeneral = lista;

        //Configura el ComboBox cbGrupo con la lista de grupos.
        cbGrupoSetup();
    }
}
