package modelo;

import java.util.ArrayList;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Esta es una clase que representa una Clase de pilates en Java.
 * 
 * @author David Jimenez Alonso
 *
 */
public class Clase {
	
	private IntegerProperty id;
	private IntegerProperty numero;
	private ObjectProperty<TipoClase> tipo;
	private ObjectProperty<HoraClase> horaClase;
	private StringProperty anotaciones;
	private ArrayList<Alumno> listaAlumnos;
	
	
	/**
	 * Constructor con parametros.
	 * Crea un objeto de tipo Clase con los datos pasados por parametros.
	 * 
	 * @param numero Numero de clase de la jornada.
	 * @param tipo Tipo de clase.
	 * @param horaClase Hora de comienzo de clase.
	 * @param anotaciones Apuntes sobre la clase.
	 */
	public Clase(int id, int numero, TipoClase tipo, HoraClase horaClase, String anotaciones) {
		this.id = new SimpleIntegerProperty(id);
		this.numero = new SimpleIntegerProperty(numero);
		this.tipo = new SimpleObjectProperty<TipoClase>(tipo);
		this.horaClase = new SimpleObjectProperty<HoraClase>(horaClase);
		this.anotaciones = new SimpleStringProperty(anotaciones);
		this.listaAlumnos = new ArrayList<Alumno>();
	}
	
	/**
	 * Constructor de copia.
	 * Crea un objeto de tipo Clase con los datos del objeto pasado como parametro.
	 * 
	 * @param c Objeto de donde se obtienen los datos para la copia.
	 */
	public Clase(Clase c) {
		id = new SimpleIntegerProperty(c.getId());
		numero = new SimpleIntegerProperty(c.getNumero());
		tipo = new SimpleObjectProperty<TipoClase>(c.getTipo());
		horaClase = new SimpleObjectProperty<HoraClase>(c.getHoraClase());
		anotaciones = new SimpleStringProperty(c.getAnotaciones());
		
		listaAlumnos = c.getListaAlumnos();
	}


	// id -----------------------------------------
	/**
     * Obtiene la propiedad del id de la clase.
     *
     * @return La propiedad del id de la clase.
     */
	public IntegerProperty idProperty() {
		return id;
	}

	/**
     * Obtiene el id de la clase.
     *
     * @return El id de la clase.
     */
	public int getId() {
		return id.get();
	}

	/**
     * Establece el id de la clase.
     *
     * @param id El nuevo id para la clase.
     */
	public void setId(int id) {
		this.id.set(id);
	}
	
	
	// numero -----------------------------------------
	/**
     * Obtiene la propiedad del número de la clase.
     *
     * @return La propiedad del número de la clase.
     */
	public IntegerProperty numeroProperty() {
		return numero;
	}

	/**
     * Obtiene el número de la clase.
     *
     * @return El número de la clase.
     */
	public int getNumero() {
		return numero.get();
	}

	/**
     * Establece el número de la clase.
     *
     * @param numero El nuevo número para la clase.
     */
	public void setNumero(int numero) {
		this.numero.set(numero);
	}

	
	// tipo ------------------------------------
	/**
     * Obtiene la propiedad del tipo de clase.
     *
     * @return La propiedad del tipo de clase.
     */
	public ObjectProperty<TipoClase> tipoProperty() {
		return tipo;
	}

	/**
     * Obtiene el tipo de clase.
     *
     * @return El tipo de clase.
     */
	public TipoClase getTipo() {
		return tipo.get();
	}

	/**
     * Establece el tipo de clase.
     *
     * @param tipo El nuevo tipo de clase.
     */
	public void setTipo(TipoClase tipo) {
		this.tipo.set(tipo);
	}
	
	
	// horaClase ------------------------------------
	/**
     * Obtiene la propiedad de la hora de clase.
     *
     * @return La propiedad de la hora de clase.
     */
	public ObjectProperty<HoraClase> horaClaseProperty() {
		return horaClase;
	}

	/**
     * Obtiene la hora de clase.
     *
     * @return La hora de clase.
     */
	public HoraClase getHoraClase() {
		return horaClase.get();
	}

	/**
     * Establece la hora de clase.
     *
     * @param horaClase La nueva hora de clase.
     */
	public void setHoraClase(HoraClase horaClase) {
		this.horaClase.set(horaClase);
	}
	
	
	// anotaciones -----------------------------------
	/**
     * Obtiene la propiedad de las anotaciones de la clase.
     *
     * @return La propiedad de las anotaciones de la clase.
     */
	public StringProperty anotacionesProperty() {
		return anotaciones;
	}

	/**
     * Obtiene las anotaciones de la clase.
     *
     * @return Las anotaciones de la clase.
     */
	public String getAnotaciones() {
		return this.anotaciones.get();
	}

	/**
     * Establece las anotaciones de la clase.
     *
     * @param anotaciones Las nuevas anotaciones para la clase.
     */
	public void setAnotaciones(String anotaciones) {
		this.anotaciones.set(anotaciones);
	}
	
	
	// listaAlumnos ----------------------------
	/**
	 * Obtiene una nueva lista de alumnos con los alumnos inscritos en la clase.
	 *
	 * @return La lista de alumnos inscritos en la clase.
	 */
	public ArrayList<Alumno> getListaAlumnos() {
		return new ArrayList<Alumno>(listaAlumnos);
	}

	/**
     * Establece la lista de alumnos de la clase.
     *
     * @param listaAlumnos La nueva lista de alumnos para la clase.
     */
	public void setListaAlumnos(ArrayList<Alumno> listaAlumnos) {
		this.listaAlumnos = listaAlumnos;
	}

	/**
	 * Agrega un alumno a la lista de alumnos inscritos en la clase.
	 *
	 * @param alumno El alumno a agregar.
	 */
	public void addAlumno(Alumno alumno) {
		listaAlumnos.add(alumno);
	}

	/**
	 * Elimina un alumno de la lista de alumnos inscritos en la clase.
	 *
	 * @param alumno El alumno a eliminar.
	 * @return true si borra el Alumno, false si no.
	 */
	public boolean removeAlumno(Alumno alumno) {
		return listaAlumnos.remove(alumno);
	}

	/**
	 * Elimina un alumno de la lista de alumnos comparando los id de alumnos inscritos en la clase.
	 *
	 * @param alumno El alumno a eliminar.
	 * @return true si borra el Alumno, false si no.
	 */
	public boolean removeAlumnoPorId(Alumno alumno) {
		int posicion = -1;
		for (Alumno alumnoClase : listaAlumnos) {
			posicion ++;
			if(alumno.getId() == alumnoClase.getId()) {
				break;
			}
		}
		try {
			listaAlumnos.remove(posicion);
		} catch (IndexOutOfBoundsException e) {
			posicion = -1;
		}

		return (posicion != 1) ? true : false;
	}

	/**
	 * Vacia la lista de alumnos de la clase.
	 */
	public void vaciarListaAlumnos() {
		listaAlumnos.clear();
	}
	
	/**
	 * Verifica si un alumno está inscrito en la clase.
	 * 
	 * @param alumno El alumno a verificar.
	 * @return true si el alumno está inscrito en la clase, false de lo contrario.
	 */
	public boolean estaInscrito(Alumno alumno) {
	    return listaAlumnos.contains(alumno);
	}


	@Override
	public String toString() {
		return "Clase [id=" + id + ", numero=" + numero + ", tipo=" + tipo + ", horaClase=" + horaClase
				+ ", anotaciones=" + anotaciones + ", listaAlumnos=" + listaAlumnos.toString() + "]";
	}

	
}
