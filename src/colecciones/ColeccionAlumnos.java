package colecciones;

import java.util.List;

import modelo.Alumno;

public class ColeccionAlumnos {
    
    private static List<Alumno> coleccionAlumnos; 

    
    public static List<Alumno> getColeccionAlumnos() {
        return coleccionAlumnos;
    }

    public static void setColeccionAlumnos(List<Alumno> lista) {
        coleccionAlumnos = lista;
    }
}
