package controlador;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;

public class RegistroUsuarioControlador implements Initializable{

    double x, y;
    Stage stage;



    @FXML
    private Pane paneBarraTitulo;
    
    @FXML
    private Button btnRegistro;

    @FXML
    private GridPane gpRegistroUsuario;

    @FXML
    void registrar(MouseEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        paneBarraTitulo.setOnMousePressed(mouseEvent -> {
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });

        paneBarraTitulo.setOnMouseDragged(mouseEvent -> {
            //Stage stg = (Stage) gpRegistroUsuario.getScene().getWindow().setX(mouseEvent.getSceneX() - x);
            stage.setX(mouseEvent.getScreenX() - x);
            stage.setY(mouseEvent.getScreenY() - y);
        });
    }


    public void setStage(Stage s) {
        stage = s;
    }
}
