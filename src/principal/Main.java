package principal;

import java.net.URL;
import java.util.ResourceBundle;

import controlador.PrincipalControlador;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
	
	ResourceBundle resourceBundle;
	BorderPane aplicacion;
	FXMLLoader loader;
	Stage primaryStage;
	PrincipalControlador controller;
	Scene scene;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		
		loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/vista/PrincipalVista.fxml"));
		aplicacion = (BorderPane)loader.load();
		
		controller = loader.getController();
		scene = new Scene(aplicacion);
		scene.getStylesheets().add(getClass().getResource("/hojasEstilos/Styles.css").toExternalForm());
		
		// Icono de aplicacion
		URL rutaIcono = getClass().getResource("/recursos/lis_logo_1.png"); // guardar ruta de recurso imagen
		this.primaryStage.getIcons().add(new Image(rutaIcono.toString())); // poner imagen icono a la ventana	

		this.primaryStage.setTitle("Lis Gestión");
		this.primaryStage.setScene(scene);
		this.primaryStage.setMinHeight(800);
		this.primaryStage.setMinWidth(1200);

		this.primaryStage.show();
	}
}
