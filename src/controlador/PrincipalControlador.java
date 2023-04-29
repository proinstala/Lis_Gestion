package controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

import baseDatos.ConexionBD;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import modelo.Alumno;
import modelo.Datos;
import modelo.Jornada;
import modelo.Toast;

public class PrincipalControlador implements Initializable {
	
	private ResourceBundle recursos;
	private Stage escenario;
	private String menuSeleccionado = "ninguno";
	private ObservableList<Alumno> listadoAlumnos;
	private Datos datos;
	private ConexionBD conexionBD;
	private Toast toast = new Toast();
	
	@FXML
    private BorderPane bpPrincipal;
	
	@FXML
    private GridPane gpMenu;

	@FXML
	private Label lClases;

	@FXML
	private Label lClientes;

	@FXML
	private Label lInicio;
	
	@FXML
    private Pane pSeparador;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		datos = Datos.getInstance();
		conexionBD = ConexionBD.getInstance();
		try {
			listadoAlumnos = FXCollections.observableArrayList(conexionBD.getListadoAlumnos());
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		//listadoAlumnos = FXCollections.observableArrayList(datos.listarAlumno());
		lInicio.getStyleClass().add("menu");
		lClases.getStyleClass().add("menu");
		lClientes.getStyleClass().add("menu");
		gpMenu.getStyleClass().add("gridPaneMenu");
		pSeparador.getStyleClass().add("panelSeparador");
		
	}
	
	@FXML
	void inicio(MouseEvent event) {
		if (menuSeleccionado != "inicio") {
			menuSeleccionado = "inicio";
			
			lInicio.getStyleClass().add("menuSeleccionado");
			lClases.getStyleClass().remove("menuSeleccionado");
			lClientes.getStyleClass().remove("menuSeleccionado");

			//----------------------------
			

			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/loginVista.fxml"));
				BorderPane login;
				login = (BorderPane) loader.load();
				bpPrincipal.setCenter(login);
				LoginControlador controller = loader.getController(); // cargo el controlador.
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		}
	}

	
	@FXML
	void clases(MouseEvent event) {
		if (menuSeleccionado != "clases") {
			menuSeleccionado = "clases";

			lInicio.getStyleClass().remove("menuSeleccionado");
			lClases.getStyleClass().add("menuSeleccionado");
			lClientes.getStyleClass().remove("menuSeleccionado");
			
			
			//Jornada jornada = datos.getJornada(LocalDate.of(2023,4,8));
			//Jornada jornada = null;
			Jornada jornada;
			try {
				jornada = conexionBD.getJornada("2023-04-08");
			} catch (SQLException e) {
				jornada = null;
				e.printStackTrace();
			}
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/jornadaVista.fxml"));
				BorderPane jornadaPilates;
				
				jornadaPilates = (BorderPane) loader.load();
				bpPrincipal.setCenter(jornadaPilates);
				
				JornadaControlador controller = loader.getController(); // cargo el controlador.
				controller.setControladorPrincipal(this);
				controller.setListaAlumnos(listadoAlumnos);
				controller.setConexionBD(datos);
				controller.inicializacion(jornada);
				
			} catch (IOException e) {
				System.out.println("-ERROR- Fallo al cargar jornadaVista.fxml" + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	
	@FXML
	void clientes(MouseEvent event) {
		if(menuSeleccionado != "clientes") {
			menuSeleccionado = "clientes";
			
			lInicio.getStyleClass().remove("menuSeleccionado");
			lClases.getStyleClass().remove("menuSeleccionado");
			lClientes.getStyleClass().add("menuSeleccionado");
		}
		
	}
	
	
	/**
     * Establece en el centro de este BorderPane el que se pasa por parametro.
     * 
     * @param BorderPane que se establece en el centro.
     */
    public void setPane(BorderPane p) {
    	bpPrincipal.setCenter(p);
    }
    
    /**
     * Establece un Stage para este controlador.
     * 
     * @param s Stage que se establece.
     */
    public void setEscenario(Stage s) {
    	this.escenario = s;
    }
	

} //Fin PrincipalControlador
