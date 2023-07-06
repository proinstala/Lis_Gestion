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
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modelo.Alumno;
import modelo.EstadoPago;
import modelo.Mensualidad;
import modelo.Toast;
import utilidades.Fechas;

public class MensualidadesControlador implements Initializable {

    private FilteredList<Mensualidad> filtro;
    private ObservableList<Alumno> listadoAlumnosGeneral;
    private ObservableList<Mensualidad> listadoMensualidadesGeneral;
    private DateTimeFormatter formatter;
    private ConexionBD conexionBD;
    private PrincipalControlador controladorPincipal;
    private Toast toast;
    private Stage escenario;

    

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
    private Button btnAplicarFiltro;

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
        btnBorrar.getStyleClass().add("boton_rojo");

        //Establecer imagen en ImageView.
        Image lupa;
        Image GenerarMensualidades;
        Image Notificacion;
        try {
            //Forma desde IDE y JAR.
            lupa = new Image(getClass().getResourceAsStream("/recursos/lupa_lila_2_128.png"));
            GenerarMensualidades = new Image(getClass().getResourceAsStream("/recursos/flceha_recarga_1.png"));
            Notificacion = new Image(getClass().getResourceAsStream("/recursos/circulo_flecha_1.png"));
        } catch (Exception e) {
            //Forma desde el JAR.
            lupa = new Image("/recursos/lupa_lila_2_128.png");
            GenerarMensualidades = new Image("/recursos/flceha_recarga_1.png");
            Notificacion = new Image("/recursos/circulo_flecha_1.png");
        }
        ivLupa.setImage(lupa);
        ivGenerarMensualidades.setImage(GenerarMensualidades);
        ivNotificacion.setImage(Notificacion);

        conexionBD = ConexionBD.getInstance();
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
            cbAnio.setValue(LocalDate.now().getYear());
        }catch (IllegalArgumentException e) {
            cbAnio.setValue(2020);
        }
        
        cbMes.setValue(Fechas.obtenerNombreMes(LocalDate.now().getMonthValue()));
        cbEstado.setValue("TODOS");
        cbModoFiltro.setValue("Nombre Alumno");

        //Configurar Listener para el ComboBox cbModoFiltro.
        cbModoFiltro.setOnAction(e -> {
            tfBusqueda.clear();
            configurarFiltro("");
        });

        //Configurar Listener para el TextField tfBusqueda.
        tfBusqueda.textProperty().addListener( (o, ov, nv) -> {
            configurarFiltro(nv);
        });

        //Configurar Listener para el Button btnAplicarFiltro.
        btnAplicarFiltro.setOnMouseClicked( e -> {
            configurarFiltro(tfBusqueda.getText());
        });

        ivNotificacion.setOnMouseClicked(e -> {
            toast.show(escenario, "EN CONSTRUCCIÓN.\nEste boton es para enviar notificaciones.");
        });

    }


     @FXML
    void abrirCardGenerarMensualidades(MouseEvent event) {
        try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/mensualidadCardGeneraMesVista.fxml"));
                AnchorPane cardGenerarMensualidades;
                cardGenerarMensualidades = (AnchorPane) loader.load();
                MensualidadCardGeneraMesControlador controller = loader.getController(); // cargo el controlador.
                
                Stage ventana= new Stage();
                ventana.initOwner(escenario);
                ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
                ventana.initStyle(StageStyle.UNDECORATED);

                URL rutaIcono = getClass().getResource("/recursos/lis_logo_1.png"); // guardar ruta de recurso imagen.
                ventana.getIcons().add(new Image(rutaIcono.toString())); // poner imagen icono a la ventana.
    
                controller.setStage(ventana);
                controller.setStagePrincipal(escenario);
                controller.setListaAlumnos(listadoAlumnosGeneral);
                controller.setListaMensualidades(listadoMensualidadesGeneral);
    
                Scene scene = new Scene(cardGenerarMensualidades);
                scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); //Añade hoja de estilos.
                ventana.setScene(scene);
                ventana.showAndWait();

                configurarFiltro(tfBusqueda.getText()); //Actualiza el filtro una vez que termina la ventana.
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }


    @FXML
    void abrirNotificaciones(MouseEvent event) {

    }


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
            alerta.initOwner(escenario);
            alerta.initModality(Modality.APPLICATION_MODAL);

            Optional<ButtonType> result = alerta.showAndWait();
    		if (result.get() == ButtonType.OK) {
                try {
                    if(conexionBD.borrarMensualidad(mensualidadSeleccionada)) {
                        for (Alumno a : listadoAlumnosGeneral) {
                            if(a.getId() == mensualidadSeleccionada.getIdAlumno()) {
                                a.removeMensualidadPorId(mensualidadSeleccionada.getId());
                                break;
                            }
                        }
                        
                        listadoMensualidadesGeneral.remove(mensualidadSeleccionada);
                        toast.show(escenario, "Mensualidad eliminada!!.");
                    }
                } catch (SQLException e) {
                    // meter en log.
                    alerta = new Alert(Alert.AlertType.ERROR);
                    alerta.getDialogPane().getStylesheets().add(getClass().getResource("/hojasEstilos/StylesAlert.css").toExternalForm()); // Añade hoja de estilos.
                    alerta.setTitle("Borrar Mensualidad");
                    alerta.setContentText("No se ha podido borrar la Mensualidad.");
                    alerta.initStyle(StageStyle.DECORATED);
                    alerta.initOwner(escenario);
                    alerta.initModality(Modality.APPLICATION_MODAL);
                    alerta.showAndWait();
                    
                    e.printStackTrace();
                }
    			
    		} else {
    			
    		}
        }
    }

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
                ventana.initOwner(escenario);
                ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
                ventana.initStyle(StageStyle.DECORATED);

                URL rutaIcono = getClass().getResource("/recursos/lis_logo_1.png"); // guardar ruta de recurso imagen.
                ventana.getIcons().add(new Image(rutaIcono.toString())); // poner imagen icono a la ventana.
    
                controller.setStage(ventana);
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @FXML
    void nuevaMensualidad(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/mensualidadFormVista.fxml"));
            GridPane formMensualidad;
            formMensualidad = (GridPane) loader.load();
            MensualidadFormControlador controller = loader.getController(); // cargo el controlador.

            Stage ventana = new Stage();
            ventana.initOwner(escenario);
            ventana.initModality(Modality.APPLICATION_MODAL); // modalida para bloquear las ventanas de detras.
            ventana.initStyle(StageStyle.DECORATED);

            URL rutaIcono = getClass().getResource("/recursos/lis_logo_1.png"); // guardar ruta de recurso imagen.
            ventana.getIcons().add(new Image(rutaIcono.toString())); // poner imagen icono a la ventana.

            controller.setStage(ventana);
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
            // TODO Auto-generated catch block
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
                ventana.initOwner(escenario);
                ventana.initModality(Modality.APPLICATION_MODAL); //modalida para bloquear las ventanas de detras.
                ventana.initStyle(StageStyle.UNDECORATED);
    
                controller.setStage(ventana);
                controller.setModelos(alumnoMensualidad, MensualidadSeleccionada);
    
                Scene scene = new Scene(cardMensualidad);
                scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm()); //Añade hoja de estilos.
                ventana.setScene(scene);
                ventana.showAndWait();
            } catch (IOException e) {
                // TODO Auto-generated catch block
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
        toast.show(escenario, "No hay seleccionado ninguna Mensualidad!!.");
        return i; //i = -1
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



    /**
     * Establece un Stage para este controlador.
     * 
     * @param s Stage que se establece.
     */
    public void setStage(Stage stage) {
    	this.escenario = stage;
    }
    
}
