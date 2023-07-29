package colecciones;

import java.util.List;
import modelo.Alumno;


/**
 * La clase ColeccionAlumnos representa una colección de objetos de tipo Alumno.
 * Esta clase proporciona métodos estáticos para obtener y establecer la colección de alumnos.
 * La colección se almacena en una lista que puede contener objetos de tipo Alumno.
 */
public class ColeccionAlumnos {
    
    //Representa la colección de clases almacenadas en una lista.
    private static List<Alumno> coleccionAlumnos; 

    /**
     * Obtiene la colección de alumnos.
     *
     * @return Lista de objetos de tipo Alumno.
     */
    public static List<Alumno> getColeccionAlumnos() {
        return coleccionAlumnos;
    }

    /**
     * Establece la colección de alumnos con una lista proporcionada.
     *
     * @param lista La lista de objetos de tipo Alumno para establecer en la colección.
     */
    public static void setColeccionAlumnos(List<Alumno> lista) {
        coleccionAlumnos = lista;
    }
}
