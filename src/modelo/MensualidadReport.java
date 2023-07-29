package modelo;

/**
 * Clase adaptadora para combinar la información de la clase Mensualidad y Alumno
 * en el contexto de un informe de JasperReports.
 */
public class MensualidadReport extends Mensualidad {
    
    private String nombreAlumno;

    /**
     * Constructor de la clase MensualidadReport sin el nombre del alumno.
     * 
     * @param mensualidad Objeto de la clase Mensualidad.
     */
    public MensualidadReport(Mensualidad mensualidad) {
        super(mensualidad);     //Llamar al constructor de la clase base (Mensualidad) con el objeto mensualidad proporcionado.
        this.nombreAlumno = ""; //Inicializar el nombre del alumno como una cadena vacía.
    }


    /**
     * Constructor de la clase MensualidadReport con el nombre del alumno.
     * 
     * @param mensualidad    Objeto de la clase Mensualidad.
     * @param nombreAlumno   Nombre del alumno asociado a la mensualidad.
     */
    public MensualidadReport(Mensualidad mensualidad, String nombreAlumno) {
        super(mensualidad); //Llamar al constructor de la clase base (Mensualidad) con el objeto mensualidad proporcionado.
        this.nombreAlumno = nombreAlumno; //Establecer el nombre del alumno proporcionado en el campo nombreAlumno.
    }


    /**
     * Obtiene el nombre del alumno asociado a la mensualidad.
     * 
     * @return Nombre del alumno.
     */
    public String getNombreAlumno() {
        return nombreAlumno;
    }


    /**
     * Establece el nombre del alumno asociado a la mensualidad.
     * 
     * @param nombreAlumno Nombre del alumno.
     */
    public void setNombreAlumno(String nombreAlumno) {
        this.nombreAlumno = nombreAlumno;
    }


    /**
     * Obtiene la información del alumno asociado a la mensualidad.
     * 
     * @return Información del alumno que combina el ID del alumno y su nombre.
     */
    public String getInformacionAlumno() {
        //Combina el ID del alumno (obtenido de la clase base Mensualidad) con el nombre del alumno.
        String nombreMasNombre = ((this.getIdAlumno() < 10) ? "0" : "") + this.getIdAlumno() + " - " + nombreAlumno;
        return nombreMasNombre;
    }

    
}
