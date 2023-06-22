package modelo;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Toast {
	private static final int TOAST_WIDTH = 200;
    private static final int TOAST_HEIGHT = 50;
    private static final int TOAST_DURATION = 4000;
    
    public Toast() {}
    

    public void show(Stage ownerStage, String message) {
        Label texto = new Label(message); //Instancio la etiqueta pasandole el texto que contiene.
        texto.getStyleClass().add("toast-label"); //Asignamos el estilo al Label.

        VBox toastBox = new VBox(texto);	//intanciamos el VBox y añadimos el texto al mismo.
        toastBox.setAlignment(Pos.CENTER); 	//Alineamos el texto en el centro.
        toastBox.getStyleClass().add("toast-box"); //Asigno el estilo al VBox.
        //toastBox.setPrefWidth(TOAST_WIDTH);	  //Tamaño ancho del VBox .
        //toastBox.setPrefHeight(TOAST_HEIGHT); //Tamaño alto del Vbox.

        StackPane toastPane = new StackPane(toastBox);	//Instancio el StackPane y le añado el VBox.
        toastPane.setAlignment(Pos.BOTTOM_CENTER);		//Asigno alineacion para el contenido del StackPane.
        toastPane.getStyleClass().add("toast-pane");	//Asigno estilo para el StackPane.

        Scene scene = new Scene(toastPane); //Instancio la escena y le asigno como panel el StackPane.
        scene.getStylesheets().add(getClass().getResource("/hojasEstilos/StylesToast.css").toExternalForm()); //Asigno hoja de estilos.
        scene.setFill(null); //Establece el fondo como transparente.
        Stage toastStage = new Stage(StageStyle.TRANSPARENT); //Instancio el escenario.
        toastStage.initOwner(ownerStage); //Establece la ventana propietaria de esta ventana.
        toastStage.setScene(scene); //asigno la escena a este escenario.

        //toastStage.setX((ownerStage.getX() + ownerStage.getWidth() / 2 - TOAST_WIDTH / 2) + 80); //posicion del escenario.
        toastStage.setX((ownerStage.getX() + ownerStage.getWidth() / 2 - TOAST_WIDTH / 2)); //posicion del escenario.
        toastStage.setY(ownerStage.getY() + ownerStage.getHeight() - TOAST_HEIGHT - 50);    //Posicion del escenario.

        toastStage.show(); //Muestra el escenario.

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(TOAST_DURATION),
                new KeyValue(toastStage.opacityProperty(), 0)));
        timeline.setOnFinished(event -> toastStage.hide());
        timeline.play();
    }

}
