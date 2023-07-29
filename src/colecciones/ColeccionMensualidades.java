package colecciones;

import java.util.List;
import modelo.Mensualidad;


/**
 * La clase ColeccionMensualidades representa una colección de objetos de tipo Mensualidad.
 * Proporciona métodos estáticos para obtener y establecer la colección de mensualidades.
 * La colección se almacena en una lista que puede contener objetos de tipo Mensualidad.
 */
public class ColeccionMensualidades {
    
    //Representa la colección de mensualidades almacenadas en una lista
    private static List<Mensualidad> coleccionMensualidades;

    /**
     * Obtiene la colección de mensualidades.
     *
     * @return Lista de objetos de tipo Mensualidad.
     */
    public static List<Mensualidad> getColeccionMensualidades() {
        return coleccionMensualidades;
    }

    /**
     * Establece la colección de mensualidades con una lista proporcionada.
     *
     * @param lista La lista de objetos de tipo Mensualidad para establecer en la colección.
     */
    public static void setColeccionMensualidades(List<Mensualidad> lista) {
        coleccionMensualidades = lista;
    }
}
