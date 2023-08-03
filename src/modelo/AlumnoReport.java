package modelo;

import java.util.ArrayList;

/**
 * Clase adaptadora para combinar la información de la clase Alumno y una lista de clase Clase
 * en la que esta inscrito el Alumno en el contexto de un informe de JasperReports.
 */
public class AlumnoReport extends Alumno {
    
    private ArrayList<Clase> listaClases;


    /**
     * Constructor de la clase AlumnoReport sin la lista de clases.
     * 
     * @param alumno Objeto de la clase Alumno.
     */
    public AlumnoReport(Alumno alumno) {
        super(alumno);
        this.listaClases = new ArrayList<Clase>();
    }

    
    /**
     * Constructor de la clase AlumnoReport con la lista de clases.
     * 
     * @param alumno Objeto de la clase Alumno.
     * @param listaClases lista de clases asociada al alumno.
     */
    public AlumnoReport(Alumno alumno, ArrayList<Clase> listaClases) {
        super(alumno);
        this.listaClases = listaClases;
    }

    /**
     * Obtine una copia de la lista de clases asociado al alumno.
     * 
     * @return Copia de la lista de clases.
     */
    public ArrayList<Clase> getListaClases() {
        return new ArrayList<Clase>(this.listaClases);
    }

    /**
     * Establece la lista de clases para el alumno.
     * 
     * @param listaClases Lista de clases a establecer.
     */
    public void setListaClases(ArrayList<Clase> listaClases){
        this.listaClases = listaClases;
    }
}
