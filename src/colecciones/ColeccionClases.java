package colecciones;

import java.util.List;

import modelo.Clase;

public class ColeccionClases {

    private static List<Clase> coleccionClases;

    public static List<Clase> getColeccionClases() {
        return coleccionClases;
    }

    public static void setColeccionClases(List<Clase> lista) {
        coleccionClases = lista;
    }
    
}
