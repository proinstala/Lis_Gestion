package modelo;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Clase adaptadora para combinar la información de la clase Alumno y una lista de clase Clase
 * en la que esta inscrito el Alumno en el contexto de un informe de JasperReports.
 */
public class AlumnoReport extends Alumno {
    
    private ArrayList<ClaseReport> listaClases;


    /**
     * Constructor de la clase AlumnoReport sin la lista de clases.
     * 
     * @param alumno Objeto de la clase Alumno.
     */
    public AlumnoReport(Alumno alumno) {
        super(alumno);
        this.listaClases = new ArrayList<ClaseReport>();
    }

    
    /**
     * Constructor de la clase AlumnoReport con la lista de clases.
     * 
     * @param alumno Objeto de la clase Alumno.
     * @param listaClases lista de clases asociada al alumno.
     */
    public AlumnoReport(Alumno alumno, ArrayList<ClaseReport> listaClases) {
        super(alumno);
        this.listaClases = listaClases;
    }

    /**
     * Obtine una copia de la lista de clases asociado al alumno.
     * 
     * @return Copia de la lista de clases.
     */
    public ArrayList<ClaseReport> getListaClases() {
        return new ArrayList<ClaseReport>(this.listaClases);
    }

    /**
     * Establece la lista de clases para el alumno.
     * 
     * @param listaClases Lista de clases a establecer.
     */
    public void setListaClases(ArrayList<ClaseReport> listaClases){
        this.listaClases = listaClases;
    }
    
    /**
     * Obtiene la fecha de nacimiento con formato "dd/MM/yyyy".
     * 
     * @return String con la fecha de nacimiento formateada.
     */
    public String getFechaNacimientoFormateada() {
    	return this.getFechaNacimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
