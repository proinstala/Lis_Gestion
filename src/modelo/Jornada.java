package modelo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Esta es una clase que representa una Jornada de clases de pilates en java.
 * 
 * @author David Jiemenz Alonso
 *
 */
public class Jornada {
	
	private ObjectProperty<LocalDate> fecha;
	private StringProperty comentario;
	private Clase[] clases;
	
	
	/**
	 * Constructor con parametros.
	 * Crea un objeto de tipo Jornada con los datos pasados por parametros.
	 * 
	 * @param fecha La fecha de la jornada de clases.
	 * @param comentario Comentarios o anotaciones de la jornada de clases.
	 */
    public Jornada(LocalDate fecha, String comentario) {
        this.fecha = new SimpleObjectProperty<LocalDate>(fecha); 
        this.comentario = new SimpleStringProperty(comentario); 
        this.clases = new Clase[8];
    }

    /**
     * Contructor de copia.
     * Crea un objeto de tipo Jornada con los datos del objeto pasado como parametro.
     * 
     * @param j
     */
    public Jornada(Jornada j) {
    	fecha = new SimpleObjectProperty<LocalDate>(j.getFecha()); 
        comentario = new SimpleStringProperty(j.getComentario());
        clases = new Clase[8];
        for (int i = 0; i < clases.length; i++) {
            clases[i] =  new Clase(j.getClase(i));
        } 
        //clases = j.getClases();
    }
    
    
    // fecha -----------------------------------
    /**
     * Propiedad para el campo fecha.
     *
     * @return La propiedad de fecha.
     */
    public ObjectProperty<LocalDate> fechaProperty() {
        return fecha;
    }
    
    /**
     * Obtiene la fecha de la jornada.
     *
     * @return La fecha de la jornada.
     */
    public final LocalDate getFecha() {
        return fecha.get();
    }

    /**
     * Establece la fecha de la jornada.
     *
     * @param fecha La nueva fecha de la jornada.
     */
    public final void setFecha(LocalDate fecha) {
        this.fecha.set(fecha);
    }

    
    // comentario -----------------------------------
    /**
     * Propiedad para el campo comentario.
     *
     * @return La propiedad de comentario.
     */
    public StringProperty comentarioProperty() {
        return comentario;
    }
    
    /**
     * Obtiene el comentario de la jornada.
     *
     * @return El comentario de la jornada.
     */
    public final String getComentario() {
        return comentario.get();
    }

    /**
     * Establece un comentario para la jornada.
     *
     * @param comentario El nuevo comentario de la jornada.
     */
    public final void setComentario(String comentario) {
        this.comentario.set(comentario);
    }
    
    
    // clases ----------------------------
    /**
     * Devuelve el arreglo de clases.
     *
     * @return El arreglo de clases.
     */
    public Clase[] getClases() {
        return clases;
    }

    /**
     * Establece el arreglo de clases.
     *
     * @param clases El arreglo de clases.
     */
    public void setClases(Clase[] clases) {
        this.clases = clases;
    }

    /**
     * Devuelve una instancia de Clase del arreglo de clases por su índice.
     *
     * @param index El índice de la clase.
     * @return La instancia de Clase correspondiente al índice.
     * @throws IndexOutOfBoundsException Si el índice está fuera de rango.
     */
    public Clase getClase(int index) {
        if (index >= 0 && index < clases.length) {
            return clases[index];
        } else {
            throw new IndexOutOfBoundsException("Índice de Clase fuera de rango: " + index);
        }
    }

    /**
     * Estable una clase en el array de clases.
     * @param clase objeto de donde se obtiene los datos.
     */
    public void setClase(Clase clase){
        clases[clase.getNumero() -1] = clase;
    }
    
    
    /**
     * Devuelve un entero con el numero de la clase en la que se encuentra el Alumno.
     * 
     * @param alumno El alumno a comparar entre los alumnos de las clases.
     * @return un entero con el numero de clases en la que esta inscrito el alumno, o -1 si no esta en ninguna clase de esta jornada.
     */
    public int alumnoEnJornada(Alumno alumno) {
    	for(int i=0; i < clases.length; i++) {
    		if(clases[i].getListaAlumnos().contains(alumno)) return clases[i].getNumero();
    	}
    	return -1;
    }


    /**
     * Devuelve un entero con el numero de alumnos inscritos en la jornada.
     * 
     * @return un entero con el numero de alumnos inscritos en esta jornada.
     */
    public int numeroAlumnosEnJornada() {
        int total = 0;
        for (int i = 0; i < clases.length; i++) {
            if(clases[i].getListaAlumnos() != null) {
                total += clases[i].getListaAlumnos().size();
            }
        }

        return total;
    }
    
    /**
	 * Devuelve un String con el dia de la semana de esta jornada.
	 * 
	 * @return Un String con el dia de la semana de esta jornada.
	 */
	public String obtenerDiaSemana() {
		String nombreDia = "";
		DayOfWeek diaDeLaSemana = this.getFecha().getDayOfWeek();
		switch (diaDeLaSemana.name()) {
		case "MONDAY" -> nombreDia = "Lunes";
		case "TUESDAY" -> nombreDia = "Martes";
		case "WEDNESDAY" -> nombreDia = "Miércoles";
		case "THURSDAY" -> nombreDia = "Jueves";
		case "FRIDAY" -> nombreDia = "Viernes";
		case "SATURDAY" -> nombreDia = "Sábado";
		case "SUNDAY" -> nombreDia = "Domingo";
		}
		
		return nombreDia;
	}

}
