package controlador;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;

public class InicioControlador implements Initializable{


    @FXML
    private Button btnCerrarSesion;

    @FXML
    private Label lbSaludo;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Hola pantalla inicio");
    }

    @FXML
    void cerrarSesion(MouseEvent event) {

    }
    
}
