package modelo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;

import utilidades.Fechas;


/**
 * Clase adaptadora para combinar la información de una Clase y la fecha de la Jornada 
 * a la que pertenece para sumistrar los datos a informes de JasperReports.
 */
public class ClaseReport extends Clase {

    private LocalDate fechaJornada;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    /**
     * Constructor de la clase ClaseReport sin la fecha de Jornada.
     * 
     * @param clase Objeto de la clase Clase que se utilizará como base.
     */
    public ClaseReport(Clase clase) {
        super(clase);
    }


    /**
     * Constructor de la clase AlumnoReport con la fecha de Jornada.
     * 
     * @param clase Objeto de la clase Clase que se utilizará como base.
     * @param fechaJornada Fecha de la jornada asociada a la clase.
     */
    public ClaseReport(Clase clase, LocalDate fechaJornada) {
        super(clase);
        this.fechaJornada = fechaJornada;
    }


    /**
     * Establece la fecha de Jornada asociada a la clase.
     * 
     * @param fechaJornada Fecha de la jornada a establecer.
     */
    public void setFechaJornada(LocalDate fechaJornada) {
        this.fechaJornada = fechaJornada;
    }


    /**
     * Obtiene la fecha de Jornada asociada a la clase.
     * 
     * @return Fecha de la jornada asociada a la clase.
     */
    public LocalDate getFechaJornada() {
        return this.fechaJornada;
    }
    
    
    /**
    * Obtiene la fecha de Jornada asociada a la clase, formateada.
    * 
    * @return String con la fecha de la jornada asociada a la clase, con formato dd/mm/yyyy.
    */
    public String getFechaJornadaFormateada() {
    	return this.fechaJornada.format(formatter);
    }
    
    /**
     * Obtiene el nombre del dia de la semana de la jornada.
     * 
     * @return String con el nombre del dia de la jornada.
     */
    public String getDiaSemana() {
    	return Fechas.obtenerDiaSemana(fechaJornada);
    }
    
    
    /**
     * Obtiene el numero de la semana en la que se encuentra la jonada.
     * 
     * @return Entero con el numero de semana en la que se encuentra la jornda.
     */
    public int getNumeroSemana() {
    	return fechaJornada.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    }
    
}
