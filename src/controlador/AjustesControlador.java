package controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Optional;
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
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
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
import modelo.ModoFormulario;
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
        //Añadir clases de estilo CSS a elementos.
        ivAddLocalidad.getStyleClass().add("iv_resaltado");
        ivBorrarLocalidad.getStyleClass().add("iv_resaltado");
        ivEditarPrecio.getStyleClass().add("iv_resaltado");
        ivEditarLocalidad.getStyleClass().add("iv_resaltado");
        ivAcercaDe.getStyleClass().add("iv_resaltado");
        ivAddGrupo.getStyleClass().add("iv_resaltado");
        ivBorrarGrupo.getStyleClass().add("iv_resaltado");
        ivEditarGrupo.getStyleClass().add("iv_resaltado");

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
            if (comporobarCamposLocalidad(ModoFormulario.EDITAR_DATOS)) { 
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
            if (comporobarCamposLocalidad(ModoFormulario.CREAR_DATOS)) {

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
            if (comporobarCamposLocalidad(ModoFormulario.BORRAR_DATOS)) {

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

        //Configurar un evento de clic del ratón para la imagen "ivAcercaDe" para mostrar informacion acerca de la app.
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
            if (comprobarCamposGrupos(ModoFormulario.CREAR_DATOS)) {
                
                //Crea una nueva instancia de GrupoAlumnos con los datos ingresados por el usuario.
                GrupoAlumnos nuevoGrupoAlumnos = new GrupoAlumnos(-1, tfNombreGrupo.getText().trim(), tfDescripcionGrupo.getText().trim());

                try {
                    //Intenta insertar el nuevo grupo en la base de datos a través del método insertarGrupoAlumnos.
                    if(conexionBD.insertarGrupoAlumnos(nuevoGrupoAlumnos)) {
                        //Si la inserción es exitosa, añade el nuevo grupo a la lista de grupos en la interfaz de usuario.
                        listadoGruposAlumnosGeneral.add(nuevoGrupoAlumnos);
                        //Mostrar una notificación de éxito en la interfaz gráfica.
                        toast.show((Stage) gpAjustes.getScene().getWindow(),"Grupo añadido!");
                        //Selecciona el nuevo grupo en el ComboBox para visualizarlo.
                        cbGrupo.getSelectionModel().select(nuevoGrupoAlumnos);
                    } else {
                        //Si la inserción falla, muestra una notificación de error.
                        toast.show((Stage) gpAjustes.getScene().getWindow(),"NO se ha añadido el Grupo!");
                    }
                } catch (Exception ex) {
                    toast.show((Stage) gpAjustes.getScene().getWindow(),"Fallo al añadir el Grupo.");
                    logUser.severe("Excepción: " + ex.toString());
                }
            }
        });

        //Configurar un evento de clic del ratón para la imagen(Botón) "ivEditarGrupo" para modificar un grupo de alumnos.
        ivEditarGrupo.setOnMouseClicked(e -> {
            if(comprobarCamposGrupos(ModoFormulario.EDITAR_DATOS)) {
                try {

                    //Crea un nuevo objeto GrupoAlumnos basado en el grupo seleccionado en el comboBox.
                    GrupoAlumnos newGrupo = new GrupoAlumnos(cbGrupo.getValue());
                    
                    //Actualiza el nombre y la descripción del grupo con los valores ingresados por el usuario quitando los espacios del principio y final.
                    newGrupo.setNombre(tfNombreGrupo.getText().trim());
                    newGrupo.setDescripcion(tfDescripcionGrupo.getText().trim());
                    
                    //Intenta actualizar la información del grupo en la base de datos.
                    if(conexionBD.actualizarGrupoAlumno(newGrupo)) {
                        //Si la actualización es exitosa, también actualiza la información en la interfaz de usuario.
                        GrupoAlumnos grupoSeleccionado = cbGrupo.getValue();
                        grupoSeleccionado.setNombre(newGrupo.getNombre());
                        grupoSeleccionado.setDescripcion(newGrupo.getDescripcion());

                        //Obtiene el índice del grupo seleccionado para poder reseleccionarlo después de la actualización.
                        int indice = cbGrupo.getSelectionModel().getSelectedIndex();
                        
                        cbGrupo.getSelectionModel().clearSelection();   //Limpia la selección actual en el comboBox
                        cbGrupo.getSelectionModel().select(indice);     //Selecciona el grupo actualizado para asegurar que los cambios se reflejen en la interfaz.

                        toast.show((Stage) gpAjustes.getScene().getWindow(),"Grupo modificado!");
                    }
                } catch (Exception ex) {
                    toast.show((Stage) gpAjustes.getScene().getWindow(),"Fallo al modificar el Grupo.");
                    logUser.severe("Excepción: " + ex.toString());
                }
            }
        });

    
        //Configurar un evento de clic del ratón para el elemento "ivBorrarGrupo" (imagen Botón) que permite borrar un grupo de alumnos seleccionado.
        ivBorrarGrupo.setOnMouseClicked(e -> {
            if (comprobarCamposGrupos(ModoFormulario.BORRAR_DATOS)) {

                //Obtiene el grupo seleccionado en el comboBox.
                GrupoAlumnos newGrupo = cbGrupo.getValue();

                //Crea una alerta de confirmación para asegurarse de que el usuario realmente desea borrar el grupo.
                Alert alerta = new Alert(AlertType.CONFIRMATION);
                alerta.getDialogPane().getStylesheets()
                        .add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
                alerta.setTitle("Borrar Grupo");
                alerta.setHeaderText("El grupo de alumnos \"" + newGrupo.getNombre() + "\" tiene asociados " + newGrupo.numeroAlumnosEnGrupo() + " alumnos.");
                alerta.setContentText("¿Estas seguro de que quieres borrar el grupo?");
                alerta.initStyle(StageStyle.DECORATED);
                alerta.initOwner((Stage) gpAjustes.getScene().getWindow());

                //Define botones personalizados para la alerta.
                ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
                ButtonType buttonTypeConfirmar = new ButtonType("Confirmar", ButtonData.YES);
                alerta.getButtonTypes().setAll(buttonTypeConfirmar, buttonTypeCancel);

                //Muestra la alerta y espera por una respuesta del usuario.
                Optional<ButtonType> result = alerta.showAndWait();

                //Si el usuario confirma la acción de borrado:
                if (result.get() == buttonTypeConfirmar) {
                    try {
                        //Intenta borrar el grupo de alumnos de la base de datos.
                        if(conexionBD.borrarGrupoAlumno(newGrupo)) {
                            //Si el borrado es exitoso, también lo elimina de la lista en la interfaz de usuario.
                            listadoGruposAlumnosGeneral.remove(newGrupo);
                            //Muestra una notificación de éxito.
                            toast.show((Stage) gpAjustes.getScene().getWindow(),"Grupo de alumnos borrado!");
                        } else {
                            //Si no se puede borrar el grupo, muestra una notificación de fallo.
                            toast.show((Stage) gpAjustes.getScene().getWindow(),"NO se ha borrado el grupo de alumnos!");
                        }
                    } catch (Exception ex) {
                        toast.show((Stage) gpAjustes.getScene().getWindow(),"Fallo al borrar el Grupo de alumnos.");
                        logUser.severe("Excepción: " + ex.toString());
                    }
                   
                }
            }
        });

    } //Fin configurarControles.

        


    /**
     * Configura el ComboBox cbGrupo utilizado para seleccionar un grupo de alumnos.
     * Este método establece los items del ComboBox, personaliza la visualización de los items,
     * y configura un listener para manejar los cambios de selección.
     * 
     */
    private void cbGrupoSetup() {
        //Asigna la lista de grupos de alumnos al ComboBox para su visualización.
        cbGrupo.setItems(listadoGruposAlumnosGeneral);

        //Personaliza cómo se muestran los items dentro del ComboBox utilizando un CellFactory. (Desplegado)
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

        //Configura cómo se muestra el texto del item seleccionado en el ComboBox cuando está cerrado, utilizando un StringConverter.
        cbGrupo.setConverter(new StringConverter<GrupoAlumnos>() {
            @Override
            public String toString(GrupoAlumnos grupo) {
                //Muestra solo el nombre del grupo en el ComboBox cuando está cerrado.
                if (grupo != null) {
                    return grupo.getNombre();
                }
                return null;
            }

            @Override
            public GrupoAlumnos fromString(String string) {
                //Este método no es necesario para este caso específico, pero debe ser implementado.
                return null;
            }
        });

        //Establece un listener para detectar y manejar cambios en la selección del ComboBox.
        cbGrupo.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            //Cuando se selecciona un nuevo item, actualiza los campos de texto con la información del grupo seleccionado.
            if(nv != null) {
                tfNombreGrupo.setText(nv.getNombre());
                tfDescripcionGrupo.setText(nv.getDescripcion());
            } else {
                //Si no hay selección, limpia los campos de texto.
                tfNombreGrupo.clear();
                tfDescripcionGrupo.clear();
            }
            
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
    private boolean comporobarCamposLocalidad(ModoFormulario origenCall) {
        boolean camposCorrectos = false;
        
        //De 1 a 4 palabras separadas por un espacio en blanco que comienzan por una letra mayuscula y una longitud de 1 a 30 caracteres. 
        Pattern localidadPattern = Pattern.compile("^[A-ZÑ][a-zñáéíóú]{1,30}(\\s[A-ZÑ][a-zñáéíóú]{1,30}){0,3}$");
		Matcher localidadMatcher = localidadPattern.matcher(tfLocalidad.getText());

        if (origenCall != ModoFormulario.CREAR_DATOS && (cbLocalidad.getValue() == null || cbLocalidad.getSelectionModel().getSelectedIndex() < 0)) {
            mensajeAviso(AlertType.WARNING, 
            "Localidad No seleccionada.", 
            "",
            "Selecciona una localidad para poder editar su nombre.");
        
        } else if (!localidadMatcher.matches()) {
            mensajeAviso(AlertType.WARNING, 
            "Nombre de localidad incorrecto.", 
            "El nombre de localidad introducido no es valido.",
                "Formato: Una letra mayúscula seguida de una minúscula, con una longitud de 1 a 30 caracteres y de 1 a 4 palabras."
                +"\nEjemplo: Santa Cruz");
        } else {
            camposCorrectos = true; //Todos los campos están correctamente completados.
        }

        if(camposCorrectos && origenCall != ModoFormulario.BORRAR_DATOS) {
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
     * Comprueba si los campos del formulario Grupos están correctamente completados.
     * 
     * @param origenCall String que se usa para filtrar las acciones que realiza este metodo.
     * @return true si todos los campos están correctamente completados, false en caso contrario.
     */
    //private boolean comprobarCamposGrupos(String origenCall) {
    private boolean comprobarCamposGrupos(ModoFormulario origenCall) {
        boolean camposCorrectos = false;

        Pattern nombrePattern = Pattern.compile("^(?!\\d)(?=\\S)([\\s\\S]{1,50})$"); //máximo de 50 caracteres, no empiece por dígitos ni espacios en blanco, puede contener cualquier carácter y espacios, y no puede estar vacío.
		Matcher nombreMatch = nombrePattern.matcher(tfNombreGrupo.getText());

        Pattern palabraReservadaPattern = Pattern.compile(".*-borrado-.*");
        Matcher palabraReservadaMatch = palabraReservadaPattern.matcher(tfNombreGrupo.getText());

        Pattern descripcionPattern = Pattern.compile("^(|.{1,300})$"); //pueda estar vacío o tener un máximo de 300 caracteres
        Matcher descripcionMatcher = descripcionPattern.matcher(tfDescripcionGrupo.getText());

        if (origenCall != ModoFormulario.CREAR_DATOS && (cbGrupo.getValue() == null || cbGrupo.getSelectionModel().getSelectedIndex() < 0)) {
            mensajeAviso(AlertType.WARNING, 
            "Grupo de alumnos NO seleccionado.", 
            "",
            "Selecciona un grupo de alumnos para poder editar sus datos.");
        } else if (!nombreMatch.matches()) {
            mensajeAviso(AlertType.WARNING, 
            "Nombre de grupo alumnos NO valido.", 
            "El nombre de grupo alumnos introducido no es valido.",
                "Formato: No empiece por dígitos ni espacio en blanco y máximo de 50 caracteres."
                +"\nEjemplo: Avanzado 2");
        } else if (palabraReservadaMatch.matches() && origenCall != ModoFormulario.BORRAR_DATOS) {
            mensajeAviso(AlertType.WARNING, 
            "Nombre de grupo alumnos No valido.", 
            "El nombre consta de una palabra reservada.",
            "El nombre no puede contener la palabra -borrado-.");
        } else if (!descripcionMatcher.matches()) {
            mensajeAviso(AlertType.WARNING,
            "Descripción no valida.",
            "La descripción introducida no es valida.",
            "Máximo 300 caracteres.");
        } else {
            camposCorrectos = true; //Todos los campos están correctamente completados.
        }

        if (camposCorrectos && origenCall != ModoFormulario.BORRAR_DATOS) {
            for(var grupo :  listadoGruposAlumnosGeneral) {
                if(grupo.getNombre().equals(tfNombreGrupo.getText())) {
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
     * @param listaGrupos La lista de grupos de alumnos a establecer.
     */
    public void setListadoGruposAlumnos(ObservableList<GrupoAlumnos> listaGrupos) {
        listadoGruposAlumnosGeneral = listaGrupos;

        //Configura el ComboBox cbGrupo con la lista de grupos.
        cbGrupoSetup();
    }
}
