package controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import baseDatos.ConexionBD;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modelo.Alumno;
import modelo.EstadoPago;
import modelo.Mensualidad;
import modelo.Toast;
import modelo.Usuario;
import utilidades.Constants;
import utilidades.Fechas;

public class MensualidadesControlador implements Initializable {

    private FilteredList<Mensualidad> filtro;
    private ObservableList<Alumno> listadoAlumnosGeneral;
    private ObservableList<Mensualidad> listadoMensualidadesGeneral;
    private DateTimeFormatter formatter;
    private Usuario usuario;
    private ConexionBD conexionBD;
    private Logger logUser;
    private Toast toast;

    @FXML
    private BorderPane bpMensualidad;

    @FXML
    private AnchorPane apFiltro;

    @FXML
    private TextField tfBusqueda;

    @FXML
    private ImageView ivLupa;

    @FXML
    private ImageView ivGenerarMensualidades;

    @FXML
    private ImageView ivNotificacion;

    @FXML
    private ComboBox<Integer> cbAnio;

    @FXML
    private ComboBox<String> cbEstado;

    @FXML
    private ComboBox<String> cbMes;

    @FXML
    private ComboBox<String> cbModoFiltro;

    @FXML
    private Button btnBorrar;

    @FXML
    private Button btnModificar;

    @FXML
    private Button btnNuevo;

    @FXML
    private Button btnVer;

    @FXML
    private Label lbAnio;

    @FXML
    private Label lbClasesSemanales;

    @FXML
    private Label lbEstadoPago;

    @FXML
    private Label lbFormaPago;

    @FXML
    private Label lbIdAlumno;

    @FXML
    private Label lbIdMensualida;

    @FXML
    private Label lbImporte;

    @FXML
    private Label lbImportePagadas;

    @FXML
    private Label lbImportePendientes;

    @FXML
    private Label lbImporteTotal;

    @FXML
    private Label lbMes;

    @FXML
    private Label lbNombreAlumno;

    @FXML
    private Label lbNumeroMensualidades;

    @FXML
    private Label lbNumeroPendientes;

    @FXML
    private Label lbNumeroOtros;

    @FXML
    private Label lbNumeroPagadas;

    @FXML
    private TableColumn<Mensualidad, Number> colAlumnoId;

    @FXML
    private TableColumn<Mensualidad, String> colAnio;

    @FXML
    private TableColumn<Mensualidad, Number> colEstado;

    @FXML
    private TableColumn<Mensualidad, Number> colFormaPago;

    @FXML
    private TableColumn<Mensualidad, Number> colId;

    @FXML
    private TableColumn<Mensualidad, String> colImporte;

    @FXML
    private TableColumn<Mensualidad, String> colMes;

    @FXML
    private TableColumn<Mensualidad, String> colNombre;

    @FXML
    private TableView<Mensualidad> tvMensualidades;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnBorrar.getStyleClass().add("boton_rojo"); //Añadir clases de estilo CSS a elementos.

        //Establecer imagen en ImageView.
        Image lupa;
        Image GenerarMensualidades;
        Image Notificacion;
        try {
            //Intentar cargar la imagen desde el recurso en el IDE y en el JAR.
            lupa = new Image(getClass().getResourceAsStream("/recursos/lupa_lila_2_128.png")); //Forma desde IDE y JAR.
            GenerarMensualidades = new Image(getClass().getResourceAsStream("/recursos/flceha_recarga_1.png"));
            Notificacion = new Image(getClass().getResourceAsStream("/recursos/circulo_flecha_1.png"));
        } catch (Exception e) {
            //Si ocurre una excepción al cargar la imagen desde el recurso en el IDE o el JAR, cargar la imagen directamente desde el JAR.
            lupa = new Image("/recursos/lupa_lila_2_128.png"); //Forma desde el JAR.
            GenerarMensualidades = new Image("/recursos/flceha_recarga_1.png");
            Notificacion = new Image("/recursos/circulo_flecha_1.png");
        }
        //Establecer las imagenes cargadas en los ImageView.
        ivLupa.setImage(lupa);
        ivGenerarMensualidades.setImage(GenerarMensualidades);
        ivNotificacion.setImage(Notificacion);

        logUser = Logger.getLogger(Constants.USER); //Crea una instancia de la clase Logger asociada al nombre de registro.
        conexionBD = ConexionBD.getInstance();      //Obtener una instancia de la clase ConexionBD utilizando el patrón Singleton.
        toast = new Toast();

        //Asigno a cada columna de la tabla los campos del modelo.
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAlumnoId.setCellValueFactory(new PropertyValueFactory<>("idAlumno"));
        colFormaPago.setCellValueFactory(new PropertyValueFactory<>("formaPago"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoPago"));

        colImporte.setCellValueFactory(cellData -> {
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            return cellData.getValue().importeProperty().asString(decimalFormat.format(cellData.getValue().getImporte()));
        });
        
        colNombre.setCellValueFactory(cellData -> {
            int id = cellData.getValue().getIdAlumno();
            for(Alumno alumno : listadoAlumnosGeneral) {
                if(alumno.getId() == id) {
                    return Bindings.concat(alumno.nombreProperty(), " ", alumno.apellido1Property(), " ", alumno.apellido2Property());
                }
            }
            return null;
        });

        colMes.setCellValueFactory(cellData -> {
            return cellData.getValue().fechaProperty().asString(Fechas.obtenerNombreMes(cellData.getValue().getFecha().getMonthValue()));
        });

        colAnio.setCellValueFactory(cellData -> {
            Year anio = Year.of(cellData.getValue().getFecha().getYear());
            return cellData.getValue().fechaProperty().asString(anio.toString());
        });
        

        //Creo un ArrayList de Integer con valores de 2020 hasta 2050 y cargo el ArrayList en el ComboBox cbAnio.
        int yearInicial = 2020;
        ArrayList<Integer> listaYears = new ArrayList<Integer>();
        for (int i = 0; i <= 30; i++) {
            listaYears.add(yearInicial + i);
        }
        //Si el año actual no esta en la lista, lo agrega.
        if(!listaYears.contains(LocalDate.now().getYear())) {
            listaYears.add(LocalDate.now().getYear());
        }
        cbAnio.setItems(FXCollections.observableArrayList(listaYears));

        //Creo un ObservableList<String> con el nombre de los meses del año. Cargo la lista en el ComboBox cbMes.
        ObservableList<String> listMeses = FXCollections.observableArrayList(Fechas.obtenerMesesDelAnio().values());
        listMeses.add("TODOS"); //Añado a listMeses el valor TODOS.
        cbMes.setItems(listMeses);

        //Convieto los valores de EstadoPago a String y los añadao a listaEstados. Cargo la lista en el ComboBox cbEstado.
        ArrayList<String> listaEstados = new ArrayList<String>();
        for (EstadoPago e : EstadoPago.values()) {
            listaEstados.add(e.toString());
        }
        listaEstados.add("TODOS"); //Añado a listaEstados el valor TODOS.
        cbEstado.setItems(FXCollections.observableArrayList(listaEstados));

        //cbModoFiltro
        ObservableList<String> tipoBusqueda = FXCollections.observableArrayList();
        tipoBusqueda.setAll("Id Alumno", "Nombre Alumno");
        cbModoFiltro.setItems(tipoBusqueda);

        //Valores iniciales de los ComboBox.
        try{
            cbAnio.setValue(LocalDate.now().getYear()); //Establece el año actual marcado por defecto.
        }catch (IllegalArgumentException e) {
            cbAnio.setValue(2020); //Establece este año si el año actual no se encuentra entre los valores del ComboBox cbAnio.
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        }
        
        cbMes.setValue(Fechas.obtenerNombreMes(LocalDate.now().getMonthValue()));
        cbEstado.setValue("TODOS");
        cbModoFiltro.setValue("Nombre Alumno");

        //Configurar Listener para el ComboBox cbMes.
        cbMes.setOnAction(e -> {
            tfBusqueda.clear();
            configurarFiltro("");
        });

        //Configurar Listener para el ComboBox cbEstado.
        cbEstado.setOnAction(e -> {
            tfBusqueda.clear();
            configurarFiltro("");
        });

        //Configurar Listener para el ComboBox cbAnio.
        cbAnio.setOnAction(e -> {
            tfBusqueda.clear();
            configurarFiltro("");
        });

        //Configurar Listener para el ComboBox cbModoFiltro.
        cbModoFiltro.setOnAction(e -> {
            tfBusqueda.clear();
            configurarFiltro("");
        });

        //Configurar Listener para el TextField tfBusqueda.
        tfBusqueda.textProperty().addListener( (o, ov, nv) -> {
            configurarFiltro(nv);
        });
    }


    /**
     * Método para manejar el evento de lanzar una ventana para generar las mensualidades automaticamente.
     * Se invoca al hacer clic en la imagen de boton "Generar mensualidades".
     *
     * @param event El evento del ratón que activó el método.
     */
    @FXML
    void abrirCardGenerarMensualidades(MouseEvent event) {
        try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/mensualidadCardGeneraMesVista.fxml"));
                AnchorPane cardGenerarMensualidades;
                cardGenerarMensualidades = (AnchorPane) loader.load();
                MensualidadCardGeneraMesControlador controller = loader.getController(); // cargo el controlador.
                
                Stage ventana= new Stage();
                ventana.initOwner((Stage) bpMensualidad.getScene().getWindow());
                ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
                ventana.initStyle(StageStyle.UNDECORATED);

                URL rutaIcono = getClass().getResource("/recursos/lis_logo_1.png"); // guardar ruta de recurso imagen.
                ventana.getIcons().add(new Image(rutaIcono.toString())); // poner imagen icono a la ventana.
    
                controller.setListaAlumnos(listadoAlumnosGeneral);
                controller.setListaMensualidades(listadoMensualidadesGeneral);
    
                Scene scene = new Scene(cardGenerarMensualidades);
                scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); //Añade hoja de estilos.
                ventana.setScene(scene);
                ventana.showAndWait();

                configurarFiltro(tfBusqueda.getText()); //Actualiza el filtro una vez que termina la ventana.
            } catch (IOException e) {
                logUser.severe("Excepción: " + e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                logUser.severe("Excepción: " + e.toString());
                e.printStackTrace();
            }
    }


    /**
     * Método para manejar el evento que lanza el formulario para enviar notificaciones.
     * Se invoca al hacer clic en el ImageView ivNotificacion.
     *
     * @param event El evento de mouse que desencadena la acción.
     */
    @FXML
    void abrirNotificaciones(MouseEvent event) {
        int i = indiceSeleccionado();
        if(i != -1) {
            Mensualidad MensualidadSeleccionada = tvMensualidades.getSelectionModel().getSelectedItem();
            Alumno alumnoMensualidad = null; 
            for (Alumno a : listadoAlumnosGeneral) {
                if(a.getId() == MensualidadSeleccionada.getIdAlumno()) {
                    alumnoMensualidad = new Alumno(a);
                }
            }

            if(comprobarRequisitosNotificacion(alumnoMensualidad)) {
                try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/mensualidadFormNotificacionVista.fxml"));
                GridPane formNotificacion;
                formNotificacion = (GridPane) loader.load();
                MensualidadFormNotificacionControlador controller = loader.getController(); // cargo el controlador.
                
                Stage ventana= new Stage();
                ventana.initOwner((Stage) bpMensualidad.getScene().getWindow());
                ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
                ventana.initStyle(StageStyle.DECORATED);

                URL rutaIcono = getClass().getResource("/recursos/lis_logo_1.png"); // guardar ruta de recurso imagen.
                ventana.getIcons().add(new Image(rutaIcono.toString())); // poner imagen icono a la ventana.
    
                controller.setModelos(MensualidadSeleccionada, alumnoMensualidad, usuario);
    
                Scene scene = new Scene(formNotificacion);
                scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); //Añade hoja de estilos.
                ventana.setScene(scene);
                ventana.setTitle("Notificación Mensualidad");
                ventana.showAndWait();

                } catch (IOException e) {
                    logUser.severe("Excepción: " + e.toString());
                    e.printStackTrace();
                } catch (Exception e) {
                    logUser.severe("Excepción: " + e.toString());
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Método para manejar el evento que borrar la mensualidad seleccionada.
     * Se invoca al hacer clic en el botón "Borrar".
     *
     * @param event El evento de mouse que desencadena la acción.
     */
    @FXML
    void borrarMensualidad(MouseEvent event) {
        int i = indiceSeleccionado();
        Alert alerta;
        if(i != -1) {
            Mensualidad mensualidadSeleccionada = tvMensualidades.getSelectionModel().getSelectedItem();

            alerta = new Alert(AlertType.CONFIRMATION);
            alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
            alerta.setTitle("Borrar Mensualidad");
            alerta.setHeaderText("¿Quieres borrar la Mensualidad seleccionada?");
            alerta.setContentText("Id: " + mensualidadSeleccionada.getId() + "  Alumno id: " + mensualidadSeleccionada.getIdAlumno()  
                                + "  Estado: " + mensualidadSeleccionada.getEstadoPago().toString());
            alerta.initStyle(StageStyle.DECORATED);
            alerta.initOwner((Stage) bpMensualidad.getScene().getWindow());
            alerta.initModality(Modality.APPLICATION_MODAL);

            //Mostrar el cuadro de diálogo y obtener la respuesta del usuario.
            Optional<ButtonType> result = alerta.showAndWait();
    		if (result.get() == ButtonType.OK) {
                try {
                    //Borrar la mensualidad de la base de datos y actualizar las estructuras de datos.
                    if(conexionBD.borrarMensualidad(mensualidadSeleccionada)) {
                        for (Alumno a : listadoAlumnosGeneral) {
                            if(a.getId() == mensualidadSeleccionada.getIdAlumno()) {
                                a.removeMensualidadPorId(mensualidadSeleccionada.getId());
                                break;
                            }
                        }
                        
                        listadoMensualidadesGeneral.remove(mensualidadSeleccionada);
                        logUser.config("Borrada mensualidad: " + mensualidadSeleccionada.toString());
                        toast.show((Stage) bpMensualidad.getScene().getWindow(), "Mensualidad eliminada!!.");
                    }
                } catch (SQLException e) {
                    logUser.severe("Excepción: " + e.toString());
                    e.printStackTrace();
                    mensajeAviso(
                        AlertType.ERROR,
                        "Borrar Mensualidad",
                        "",
                        "No se ha podido borrar la Mensualidad.");
                    
                } catch (Exception e) {
                    logUser.severe("Excepción: " + e.toString());
                    e.printStackTrace();
                }
    		} 
        }
    }


    /**
     * Método para manejar el evento que lanza una ventana para modificar la mensualidad seleccionada.
     * Se invoca al hacer clic en el botón "Modificar".
     *
     * @param event El evento de mouse que desencadena la acción.
     */
    @FXML
    void modificarMensualidad(MouseEvent event) {
        int i = indiceSeleccionado();
        if(i != -1) {
            Mensualidad MensualidadSeleccionada = tvMensualidades.getSelectionModel().getSelectedItem();
            Alumno alumnoMensualidad = null; 
            for (Alumno a : listadoAlumnosGeneral) {
                if(a.getId() == MensualidadSeleccionada.getIdAlumno()) {
                    alumnoMensualidad = new Alumno(a);
                }
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/mensualidadFormVista.fxml"));
                GridPane formMensualidad;
                formMensualidad = (GridPane) loader.load();
                MensualidadFormControlador controller = loader.getController(); // cargo el controlador.
                
                Stage ventana= new Stage();
                ventana.initOwner((Stage) bpMensualidad.getScene().getWindow());
                ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
                ventana.initStyle(StageStyle.DECORATED);

                URL rutaIcono = getClass().getResource("/recursos/lis_logo_1.png"); // guardar ruta de recurso imagen.
                ventana.getIcons().add(new Image(rutaIcono.toString())); // poner imagen icono a la ventana.
    
                controller.modoFormulario(controller.MODO_EDITAR_MENSUALIDAD);
                controller.setAlumno(alumnoMensualidad);
                controller.setMensualidad(MensualidadSeleccionada);
    
                Scene scene = new Scene(formMensualidad);
                scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); //Añade hoja de estilos.
                ventana.setScene(scene);
                ventana.setTitle("Editar Mensualidad");
                ventana.showAndWait();

                configurarFiltro(tfBusqueda.getText()); //Actualiza el filtro una vez que termina la ventana de modificar la mensualidad.
            } catch (IOException e) {
                logUser.severe("Excepción: " + e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                logUser.severe("Excepción: " + e.toString());
                e.printStackTrace();
            }
        }
    }


    /**
     * Método para manejar el evento que lanza una ventana para crear una nueva mensualidad.
     * Se invoca al hacer clic en el botón "Nuevo".
     *
     * @param event El evento de mouse que desencadena la acción.
     */
    @FXML
    void nuevaMensualidad(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/mensualidadFormVista.fxml"));
            GridPane formMensualidad;
            formMensualidad = (GridPane) loader.load();
            MensualidadFormControlador controller = loader.getController(); // cargo el controlador.

            Stage ventana = new Stage();
            ventana.initOwner((Stage) bpMensualidad.getScene().getWindow());
            ventana.initModality(Modality.APPLICATION_MODAL); // modalida para bloquear las ventanas de detras.
            ventana.initStyle(StageStyle.DECORATED);

            URL rutaIcono = getClass().getResource("/recursos/lis_logo_1.png"); // guardar ruta de recurso imagen.
            ventana.getIcons().add(new Image(rutaIcono.toString())); // poner imagen icono a la ventana.

            controller.modoFormulario(controller.MODO_NUEVA_MENSUALIDAD);
            controller.setListaAlumnos(listadoAlumnosGeneral);
            controller.setListaMensualidades(listadoMensualidadesGeneral);

            Scene scene = new Scene(formMensualidad);
            scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); // Añade hoja de estilos.
            ventana.setScene(scene);
            ventana.setTitle("Nueva Mensualidad");
            ventana.showAndWait();

            configurarFiltro(tfBusqueda.getText());
        } catch (IOException e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            logUser.severe("Excepción: " + e.toString());
            e.printStackTrace();
        }
    }


    /**
     * Abre una nueva ventana para mostrar los detalles de la mensualidad seleccionada.
     * Se invoca al hacer clic en el botón "Ver".
     *
     * @param event El evento del mouse.
     */
    @FXML
    void verMensualidad(MouseEvent event) {
        int i = indiceSeleccionado();
        
        if(i != -1) {
            Mensualidad MensualidadSeleccionada = tvMensualidades.getSelectionModel().getSelectedItem();
            Alumno alumnoMensualidad = null; 
            for (Alumno a : listadoAlumnosGeneral) {
                if(a.getId() == MensualidadSeleccionada.getIdAlumno()) {
                    alumnoMensualidad = new Alumno(a);
                }
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/mensualidadCardVista.fxml"));
                AnchorPane cardMensualidad;
                cardMensualidad = (AnchorPane) loader.load();
                MensualidadCardControlador controller = loader.getController(); // cargo el controlador.
                
                Stage ventana= new Stage();
                ventana.initOwner((Stage) bpMensualidad.getScene().getWindow());
                ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
                ventana.initStyle(StageStyle.UNDECORATED);
    
                controller.setModelos(alumnoMensualidad, MensualidadSeleccionada);
    
                Scene scene = new Scene(cardMensualidad);
                scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); //Añade hoja de estilos.
                ventana.setScene(scene);
                ventana.showAndWait();
            } catch (IOException e) {
                logUser.severe("Excepción: " + e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                logUser.severe("Excepción: " + e.toString());
                e.printStackTrace();
            }
        }
    }


    /**
     * Configura el filtro para la tabla de mensualidades según los criterios seleccionados.
     *
     * @param texto El texto de búsqueda utilizado para filtrar.
     */
    private void configurarFiltro(String texto) {
        	filtro.setPredicate(obj -> {
                if (obj.fechaProperty().getValue().getYear() != cbAnio.getValue()) {
                    return false;
                }

                if ( !(cbMes.getValue().equals("TODOS")) && !(Fechas.obtenerNombreMes(obj.fechaProperty().getValue().getMonthValue()).equals(cbMes.getValue())) ) {
                    return false;
                }

                if ( !(cbEstado.getValue().equals("TODOS")) && !(obj.estadoPagoProperty().getValue().toString().equals(cbEstado.getValue())) ) {
                    return false;
                }

                if (cbModoFiltro.getValue() == "Nombre Alumno") {
                    for(Alumno alumno : listadoAlumnosGeneral) {
                        if(obj.getIdAlumno() == alumno.getId()) {
                            if(alumno.getNombreCompleto().toLowerCase().contains(texto.toLowerCase())) {
                                return true;
                            }
                        }    
                    }
        		    return false;
                } else {
                    if(texto.isBlank()) {return true;}
                    if(texto.matches("[0-9]{0,4}") && obj.getIdAlumno() == Integer.parseInt(texto)) {
                        return true;
                    }

                    return false;
                }
                
        	});

            //Si al actualizar el filtro no hay ninguna mensualidad seleccionada.
            if(tvMensualidades.getSelectionModel().getSelectedIndex() == -1) {
                limpiarMensualidadSeleccionada();
            }
    }


    /**
     * Configura los Labels que se muestran en Datos Tabla con los cálculos y las propiedades enlazadas.
     * Actualiza los labels con la información de recuento y sumas.
     */
    private void setupDatosTabla() {
        IntegerBinding totalMensualidades = Bindings.createIntegerBinding(
                () -> (int) filtro.stream().count(), filtro);

        IntegerBinding pagadasCount = Bindings.createIntegerBinding(
                () -> (int) filtro.stream().filter(mensualidad -> mensualidad.getEstadoPago() == EstadoPago.PAGADO).count(), filtro);

        IntegerBinding pendietesCount = Bindings.createIntegerBinding(
                () -> (int) filtro.stream().filter(mensualidad -> mensualidad.getEstadoPago() == EstadoPago.PENDIENTE).count(), filtro);

        IntegerBinding otrosEstadosCount = Bindings.createIntegerBinding(
                () -> (int) filtro.stream().filter(mensualidad -> mensualidad.getEstadoPago() != EstadoPago.PENDIENTE && mensualidad.getEstadoPago() != EstadoPago.PAGADO).count(), filtro);
        
        DoubleBinding importeTotal = Bindings.createDoubleBinding(
                () -> (Double) filtro.stream().mapToDouble(Mensualidad::getImporte).sum(), filtro);

        DoubleBinding importePagadas = Bindings.createDoubleBinding(
                () -> filtro.stream()
                        .filter(m -> m.getEstadoPago() == EstadoPago.PAGADO) //Aplicar filtro por estado "PAGADO"
                        .mapToDouble(Mensualidad::getImporte)
                        .sum(), filtro
        );

        DoubleBinding importePendientes = Bindings.createDoubleBinding(
                () -> filtro.stream()
                        .filter(m -> m.getEstadoPago() == EstadoPago.PENDIENTE) //Aplicar filtro por estado "PENDIENTE"
                        .mapToDouble(Mensualidad::getImporte)
                        .sum(), filtro
        );

        lbNumeroMensualidades.textProperty()
                .bind(Bindings.createStringBinding(() -> String.format("%d", totalMensualidades.get()), totalMensualidades));
        
        lbNumeroPagadas.textProperty()
                .bind(Bindings.createStringBinding(() -> String.format("%d", pagadasCount.get()), pagadasCount));

        lbNumeroPendientes.textProperty()
                .bind(Bindings.createStringBinding(() -> String.format("%d", pendietesCount.get()), pendietesCount));
        
        lbNumeroOtros.textProperty()
                .bind(Bindings.createStringBinding(() -> String.format("%d", otrosEstadosCount.get()), otrosEstadosCount));
        
        lbImportePagadas.textProperty()
                .bind(Bindings.createStringBinding(() -> String.format("%.2f", importePagadas.get()), importePagadas));
        
        lbImportePendientes.textProperty()
                .bind(Bindings.createStringBinding(() -> String.format("%.2f", importePendientes.get()), importePendientes));
        
        lbImporteTotal.textProperty()
                .bind(Bindings.createStringBinding(() -> String.format("%.2f", importeTotal.get()), importeTotal));
    }


    /**
     * Configura la selección de una mensualidad en la tabla.
     * Establece los enlaces entre los elementos gráficos y las propiedades de la mensualidad seleccionada.
     */
    private void setupMensualidadSeleccionada() {
        tvMensualidades.getSelectionModel().selectedItemProperty().addListener( (o, ov, nv) -> {
            // Si había una mensualidad seleccionada anteriormente, desvincular las propiedades de los elementos gráficos.
            if (ov != null) {
                lbIdMensualida.textProperty().unbind();
                lbIdAlumno.textProperty().unbind();
                lbNombreAlumno.textProperty().unbind();
                lbMes.textProperty().unbind();
                lbAnio.textProperty().unbind();
                lbFormaPago.textProperty().unbind();
                lbClasesSemanales.textProperty().unbind();
                lbEstadoPago.textProperty().unbind();
                lbImporte.textProperty().unbind();
            }

            // Si hay una nueva mensualidad seleccionada, establecer los enlaces entre las propiedades y los elementos gráficos.
            if (nv != null) {
                lbIdMensualida.textProperty().bind(nv.idProperty().asString());
                lbIdAlumno.textProperty().bind(nv.idAlumnoProperty().asString());
                lbFormaPago.textProperty().bind(nv.formaPagoProperty().asString());
                lbClasesSemanales.textProperty().bind(nv.asistenciasSemanalesProperty().asString());
                lbEstadoPago.textProperty().bind(nv.estadoPagoProperty().asString());
                lbImporte.textProperty().bind(nv.importeProperty().asString("%.2f"));
                listadoAlumnosGeneral.forEach((a) -> {
                    if(nv.getIdAlumno() == a.getId()) {
                        lbNombreAlumno.textProperty().bind(Bindings.concat(a.nombreProperty(), " ", a.apellido1Property(), " ", a.apellido2Property()));
                    }
                });
                lbMes.textProperty().bind(nv.fechaProperty().asString(Fechas.obtenerNombreMes(nv.fechaProperty().getValue().getMonthValue())));
                lbAnio.textProperty().bind(nv.fechaProperty().asString(Integer.toString(nv.fechaProperty().getValue().getYear())));
            }
        });
    }

    
    /**
     * Desvincula las propiedades y elimina los textos de los elementos gráficos.
     * 
     */
    private void limpiarMensualidadSeleccionada() {
        lbIdMensualida.textProperty().unbind();
        lbIdAlumno.textProperty().unbind();
        lbNombreAlumno.textProperty().unbind();
        lbMes.textProperty().unbind();
        lbAnio.textProperty().unbind();
        lbFormaPago.textProperty().unbind();
        lbClasesSemanales.textProperty().unbind();
        lbEstadoPago.textProperty().unbind();
        lbImporte.textProperty().unbind();

        lbIdMensualida.setText("");
        lbIdAlumno.setText("");
        lbNombreAlumno.setText("");
        lbMes.setText("");
        lbAnio.setText("");
        lbFormaPago.setText("");
        lbClasesSemanales.setText("");
        lbEstadoPago.setText("");
        lbImporte.setText("");
    }


    /**
     * Devuelve el indice seleccionado de la lista cargada en la tableView.
     * 
     * @return Número entero representativo del indice seleccionado en la tableView.
     */
    private int indiceSeleccionado() {
        int i = tvMensualidades.getSelectionModel().getSelectedIndex(); //Obtengo el indice del elemento seleccionado en la lista.
        if(i != -1) {
            return i; //número del indice seleccionado.
        }
        toast.show((Stage) bpMensualidad.getScene().getWindow(), "No hay seleccionado ninguna Mensualidad!!.");
        return i; //i = -1
    }


    /**
     * Comprueba los requisitos necesarios para enviar una notificación al alumno.
     * Verifica si el email de la aplicación y el email del alumno están configurados correctamente.
     * Muestra un mensaje de Informcion si no cumple los requisitos.
     *
     * @param alumnoNotificacion El objeto Alumno al que se desea enviar la notificación.
     * @return true si todos los requisitos están cumplidos y la notificación se puede enviar, false en caso contrario.
     */
    private boolean comprobarRequisitosNotificacion(Alumno alumnoNotificacion) {
        if (usuario.getEmailApp() == null || usuario.getEmailApp().isBlank()) {
            mensajeAviso(AlertType.INFORMATION, 
                "Fallo Notificación", 
                "Email Aplicación NO configurado.", 
                "El usuario no tiene configurado el Email Aplicación.");
            return false;
        } else if (alumnoNotificacion.getEmail() == null || alumnoNotificacion.getEmail().isBlank()) {
            mensajeAviso(AlertType.INFORMATION, 
                "Fallo Notificación", 
                "Email de Alumno NO registrado.", 
                "El Alumno no tiene email registrado en aplicación.");
            return false;
        }
        return true;
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
        alerta.initOwner((Stage) bpMensualidad.getScene().getWindow());
        alerta.setHeaderText(cabecera);
        alerta.setContentText(cuerpo);
        alerta.initStyle(StageStyle.DECORATED);
        alerta.initModality(Modality.APPLICATION_MODAL);
        alerta.showAndWait();
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
     * Establece la lista de alumnos en el controlador.
     *
     * @param lista La lista de alumnos a establecer.
     */
    public void setListaAlumnos(ObservableList<Alumno> lista) {
        listadoAlumnosGeneral = lista;
	}


    /**
     * Establece la lista de mensualidades para mostrar en la tabla.
     * Configura el filtro, los eventos de selección y los datos de la tabla.
     * 
     * @param lista La lista de mensualidades a mostrar.
     */
    public void setListaMensualidades(ObservableList<Mensualidad> lista) {
        listadoMensualidadesGeneral = lista; //Guarda la lista pasada a la lista de Clasecontrolador.
		filtro = new FilteredList<Mensualidad>(listadoMensualidadesGeneral); //Inicia el filtro pasándole el listado de mensualidades.
		tvMensualidades.setItems(filtro); //Asigna la lista de mensualidades a la TableView tvMensualidades.

        configurarFiltro(tfBusqueda.getText()); //Configura el filtro basado en el contenido del TextField tfBusqueda.
        setupMensualidadSeleccionada();         //Configura los eventos de selección de la tabla.
        setupDatosTabla(); // Configura los bindings para actualizar los labels de información en la TableView.
	}
}
