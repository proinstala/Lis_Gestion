package colecciones;

import java.util.List;
import modelo.Clase;


 /**
 * La clase ColeccionClases representa una colección de objetos de tipo Clase.
 * Esta clase proporciona métodos estáticos para obtener y establecer la colección de clases.
 * La colección se almacena en una lista que puede contener objetos de tipo Clase.
 */
public class ColeccionClases {

    //Representa la colección de clases almacenadas en una lista.
    private static List<Clase> coleccionClases;

    /**
     * Obtiene la colección de clases.
     *
     * @return Lista de objetos de tipo Clase.
     */
    public static List<Clase> getColeccionClases() {
        return coleccionClases;
    }

    /**
     * Establece la colección de clases con una lista proporcionada.
     *
     * @param lista La lista de objetos de tipo Clase para establecer en la colección.
     */
    public static void setColeccionClases(List<Clase> lista) {
        coleccionClases = lista;
    }
    
}
