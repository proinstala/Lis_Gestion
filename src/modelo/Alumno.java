package modelo;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.util.ArrayList;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;


/**
 * Esta es una clase que representa un Alumno en Java.
 * 
 * @author David Jimenez Alonso
 *
 */
public class Alumno extends Persona implements Cloneable {
	
	private ObjectProperty<Genero> genero;
	private ObjectProperty<LocalDate> fechaNacimiento;
	private ObjectProperty<Direccion> direccion;
	private ObjectProperty<EstadoAlumno> estado;
	private IntegerProperty asistenciaSemanal;
	private ObjectProperty<FormaPago> formaPago;

	private ArrayList<Mensualidad> listaMensualidades;
	

	/**
	 * Constructor sin parametros.
	 * Crea un objeto de tipo Alumno con sus campos a null.
	 */
	public Alumno() {
		super();
		
		this.genero = new SimpleObjectProperty<Genero>(null);
		this.fechaNacimiento = new SimpleObjectProperty<LocalDate>(null);
		this.direccion = new SimpleObjectProperty<Direccion>(null, "direccion");
		this.direccion.set(null);
		this.estado = new SimpleObjectProperty<EstadoAlumno>(null);
		this.asistenciaSemanal = new SimpleIntegerProperty(1);
		this.formaPago = new SimpleObjectProperty<FormaPago>(null);

		this.listaMensualidades = new ArrayList<Mensualidad>();
	}
	

	/**
	 * Contructor con parametros.
	 * Crea un objeto de tipo Alumno con los datos pasados por parametros.
	 * 
	 * @param id Identificador del alumno.
	 * @param nombre Nombre del alumno.
	 * @param apellido1 Primer apellido del alumno.
	 * @param apellido2 Segundo apellido del alumno.
	 * @param genero Genero del alumno.
	 * @param fechaNacimiento Fecha de nacimiento.
	 * @param direccion objeto que contine los datos de direccion del alumno.
	 * @param telefono Telefono del alumno.
	 * @param email Direccion de correo electronico del alumno.
	 * @param estado Estado del alumno.
	 * @param asistenciaSemanal Numero de asistencia semanal del alumno.
	 * @param formaPago Forma de pago de las mensualidades del alumno.
	 */
	public Alumno(int id, String nombre, String apellido1, String apellido2, Genero genero, Direccion direccion,
			LocalDate fechaNacimiento, int telefono, String email, EstadoAlumno estado,  int asistenciaSemanal, FormaPago formaPago) {
		
		super(id, nombre, apellido1, apellido2, telefono, email);
	
		this.genero = new SimpleObjectProperty<Genero>(genero);
		this.fechaNacimiento = new SimpleObjectProperty<LocalDate>(fechaNacimiento);
		this.direccion = new SimpleObjectProperty<Direccion>(direccion, "direccion");
		this.direccion.set(direccion);
		this.estado = new SimpleObjectProperty<EstadoAlumno>(estado);
		this.asistenciaSemanal = new SimpleIntegerProperty(asistenciaSemanal);
		this.formaPago = new SimpleObjectProperty<FormaPago>(formaPago);
		this.listaMensualidades = new ArrayList<Mensualidad>();
	}


	/**
	 * Contructor con parametros.
	 * Crea un objeto de tipo Alumno con los datos pasados por parametros.
	 * 
	 * @param id Identificador del alumno.
	 * @param nombre Nombre del alumno.
	 * @param apellido1 Primer apellido del alumno.
	 * @param apellido2 Segundo apellido del alumno.
	 * @param genero Genero del alumno.
	 * @param fechaNacimiento Fecha de nacimiento.
	 * @param direccion Objeto que contine los datos de direccion del alumno.
	 * @param telefono Telefono del alumno.
	 * @param email Direccion de correo electronico del alumno.
	 * @param estado Estado del alumno.
	 * @param asistenciaSemanal Numero de asistencia semanal del alumno.
	 * @param formaPago Forma de pago de las mensualidades del alumno.
	 * @param listaMensualidades Lista de mensualidades del alumno.
	 */
	public Alumno(int id, String nombre, String apellido1, String apellido2, Genero genero, Direccion direccion, LocalDate fechaNacimiento,
			int telefono, String email, EstadoAlumno estado, int asistenciaSemanal, FormaPago formaPago, ArrayList<Mensualidad> listaMensualidades) {
		
		super(id, nombre, apellido1, apellido2, telefono, email);
		
		this.genero = new SimpleObjectProperty<Genero>(genero);
		this.fechaNacimiento = new SimpleObjectProperty<LocalDate>(fechaNacimiento);
		this.direccion = new SimpleObjectProperty<Direccion>(direccion, "direccion");
		this.direccion.set(direccion);
		this.estado = new SimpleObjectProperty<EstadoAlumno>(estado);
		this.asistenciaSemanal = new SimpleIntegerProperty(asistenciaSemanal);
		this.formaPago = new SimpleObjectProperty<FormaPago>(formaPago);
		this.listaMensualidades = new ArrayList<Mensualidad>(listaMensualidades);
	}


	/**
	 * Contructor de copia.
	 * Crea un objeto de tipo Alumno con los datos del objeto pasado como parametro.
	 * 
	 * @param a Objeto de donde se obtienen los datos para la copia.
	 */
	public Alumno(Alumno a) {
		super(a);

		genero = new SimpleObjectProperty<Genero>(a.getGenero());
		fechaNacimiento = new SimpleObjectProperty<LocalDate>(a.getFechaNacimiento());
		direccion = new SimpleObjectProperty<Direccion>(a.getDireccion());
		estado = new SimpleObjectProperty<EstadoAlumno>(a.getEstado());
		asistenciaSemanal = new SimpleIntegerProperty(a.getAsistenciaSemanal());
		formaPago = new SimpleObjectProperty<FormaPago>(a.getFormaPago());
		listaMensualidades = new ArrayList<Mensualidad>(a.getListaMensualidades());
	}

	
	public Object clone(){  
    	Alumno obj = new Alumno();

		obj.setId(this.id.getValue().intValue());
		obj.setNombre(this.nombre.getValue());
		obj.setApellido1(this.apellido1.getValue());
		obj.setApellido2(this.apellido2.getValue());
		obj.setGenero(this.genero.getValue());
		obj.setFechaNacimiento(this.fechaNacimiento.getValue());
		obj.setDireccion(new Direccion(this.direccion.getValue()));
		obj.setDireccion(this.direccion.getValue());
		obj.setTelefono(this.telefono.getValue().intValue());
		obj.setEmail(this.email.getValue());
		obj.setEstado(this.estado.getValue());
		obj.setAsistenciaSemanal(this.asistenciaSemanal.getValue());
		obj.setFormaPago(this.formaPago.getValue());
		obj.setListaMensualidades(new ArrayList<Mensualidad>(this.getListaMensualidades()));
		return obj;
	}
	
	
	// genero ------------------------------------
	/**
     * Obtiene la propiedad de genero del alumno.
     *
     * @return La propiedad de género del alumno.
     */
	public ObjectProperty<Genero> generoProperty() {
		return genero;
	}

	/**
     * obtiene el género del alumno.
     *
     * @return El género del alumno.
     */
	public Genero getGenero() {
		return genero.get();
	}

	/**
     * Establece el género del alumno.
     *
     * @param genero El nuevo género para el alumno.
     */
	public void setGenero(Genero genero) {
		this.genero.set(genero);
	}
	
	
	// fechaNacimiento ------------------------------------
	/**
     * Obtiene la propiedad de la fecha de nacimiento del alumno.
     *
     * @return La propiedad de la fecha de nacimiento del alumno.
     */
	public ObjectProperty<LocalDate> fechaNacimientoProperty() {
		return fechaNacimiento;
	}

	/**
     * Obtiene la fecha de nacimiento del alumno.
     *
     * @return La fecha de nacimiento del alumno.
     */
	public LocalDate getFechaNacimiento() {
		return fechaNacimiento.get();
	}

	/**
     * Establece la fecha de nacimiento del alumno.
     *
     * @param fechaNacimiento La nueva fecha de nacimiento para el alumno.
     */
	public void setFechaNacimiento(LocalDate fechaNacimiento) {
		this.fechaNacimiento.set(fechaNacimiento);
	}
	
	
	// direccion ------------------------------------
	/**
     * Obtiene la propiedad de la dirección del alumno.
     *
     * @return La propiedad de la dirección del alumno.
     */
	public ObjectProperty<Direccion> direccionProperty() {
		return direccion;
	}

	/**
     * Obtiene la dirección del alumno.
     *
     * @return La dirección del alumno.
     */
	public Direccion getDireccion() {
		return direccion.get();
	}

	/**
     * Establece la dirección del alumno.
     *
     * @param direccion La nueva dirección para el alumno.
     */
	public void setDireccion(Direccion direccion) {
		this.direccion.set(direccion);
	}


	// estado ------------------------------------
	/**
     * Obtiene la propiedad del estado del alumno.
     *
     * @return La propiedad del estado del alumno.
     */
	public ObjectProperty<EstadoAlumno> estadoProperty() {
		return estado;
	}

	/**
     * Obtine el estado del alumno.
     *
     * @return El estado del alumno.
     */
	public EstadoAlumno getEstado() {
		return estado.get();
	}

	/**
     * Establece el estado del alumno.
     *
     * @param estado El nuevo estado para el alumno.
     */
	public void setEstado(EstadoAlumno estado) {
		this.estado.set(estado);
	}


	// asistenciaSemanal -----------------------------------------
	/**
	 * Obtiene la propiedad de la asistenciaSemanal del alumno.
	 *
	 * @return La propiedad de la asistenciaSemanal del alumno.
	 */
	public IntegerProperty asistenciaSemanalProperty() {
		return asistenciaSemanal;
	}

	/**
	 * Obtiene la asistenciaSemanal del alumno.
	 *
	 * @return La asistenciaSemanal del alumno.
	 */
	public int getAsistenciaSemanal() {
		return asistenciaSemanal.get();
	}

	/**
	 * Establece la asistenciaSemanal del alumno.
	 *
	 * @param asistenciaSemanal La nueva asistenciaSemanal para el alumno.
	 */
	public void setAsistenciaSemanal(int asistenciaSemanal) {
		this.asistenciaSemanal.set(asistenciaSemanal);
	}


	// formaPago ------------------------------------
	/**
     * Obtiene la propiedad de formaPago del alumno.
     *
     * @return La propiedad de formaPago del alumno.
     */
	public ObjectProperty<FormaPago> formaPagoProperty() {
		return formaPago;
	}

	/**
     * obtiene la formaPago del alumno.
     *
     * @return La formaPago del alumno.
     */
	public FormaPago getFormaPago() {
		return formaPago.get();
	}

	/**
     * Establece el formaPago del alumno.
     *
     * @param formaPago La nueva formaPago para el alumno.
     */
	public void setFormaPago(FormaPago formaPago) {
		this.formaPago.set(formaPago);
	}


	public ArrayList<Mensualidad> getListaMensualidades() {
		return listaMensualidades;
	}

	public void setListaMensualidades(ArrayList<Mensualidad> lista) {
		this.listaMensualidades = lista;
	}


	/**
	 * Elimina una Mensualidad de la lista por su ID.
	 *
	 * @param id el ID de la Mensualidad a eliminar
	 * @return true si se encontró y eliminó la Mensualidad, false en caso contrario.
	 */
	public boolean removeMensualidadPorId(int id) {
		int posicion = -1;

		// Buscar la posición de la Mensualidad con el ID especificado
		for (int i = 0; i < listaMensualidades.size(); i++) {
			if (listaMensualidades.get(i).getId() == id) {
				posicion = i;
				break;
			}
		}

		if (posicion != -1) {
			// Se encontró la Mensualidad, eliminarla
			listaMensualidades.remove(posicion);
			return true;
		} else {
			// No se encontró la Mensualidad con el ID especificado
			return false;
		}
	}


	/**
	 * Agrega una Mensualidad a la lista.
	 *
	 * @param mensualidad la Mensualidad a agregar
	 * @return true si se agrega exitosamente, false si la Mensualidad ya existe en la lista o hay una mensualidad con el mismo id o con la misma fecha.
	 */
	public boolean addMensualidad(Mensualidad mensualidad) {
		if (listaMensualidades.contains(mensualidad)) {
			return false; // La Mensualidad ya existe, no se puede agregar
		}

		for (Mensualidad m : listaMensualidades) {
			if(m.getId() == mensualidad.getId() || m.getFecha().equals(mensualidad.getFecha())) {
				return false; // Ya contiene una mensualidad con la misma fecha o el mismo id.
			}
		}
		
		return listaMensualidades.add(mensualidad); // Mensualidad agregada exitosamente
	}


	/**
	 * Comprueba si existe una Mensualidad con la fecha pasada como parametro en la lista de mensualidades.
	 *
	 * @param fecha la fecha a comprobar
	 * @return false si ya existe una Mensualidad con la misma fecha, true en caso contrario
	 */
	public boolean fechaMensualidadDisponible(YearMonth fecha) {
		for (Mensualidad m : listaMensualidades) {
			if(m.getFecha().equals(fecha)) {
				return false; // Ya contiene una mensualidad con la misma fecha.
			}
		}
		return true;
	}


	/**
	 * Obtiene la edad del Alumno
	 * 
	 * @return La edad del Alumno.
	 */
	public int getEdad() {
		LocalDate ahora = LocalDate.now();
		Period periodo = Period.between(fechaNacimiento.get(), ahora);
		return periodo.getYears();
	}


	/**
	 * Obtine un String con el nombre y apellidos del Alumno.
	 * 
	 * @return String con el nombre y apellidos del Alumno.
	 */
	public String getNombreCompleto() {
		return getNombre() + " " + getApellido1() + " " + getApellido2();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((genero == null) ? 0 : genero.hashCode());
		result = prime * result + ((fechaNacimiento == null) ? 0 : fechaNacimiento.hashCode());
		result = prime * result + ((direccion == null) ? 0 : direccion.hashCode());
		result = prime * result + ((estado == null) ? 0 : estado.hashCode());
		result = prime * result + ((asistenciaSemanal == null) ? 0 : asistenciaSemanal.hashCode());
		result = prime * result + ((formaPago == null) ? 0 : formaPago.hashCode());
		result = prime * result + ((listaMensualidades == null) ? 0 : listaMensualidades.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Alumno other = (Alumno) obj;
		if (genero == null) {
			if (other.genero != null)
				return false;
		} else if (!genero.equals(other.genero))
			return false;
		if (fechaNacimiento == null) {
			if (other.fechaNacimiento != null)
				return false;
		} else if (!fechaNacimiento.equals(other.fechaNacimiento))
			return false;
		if (direccion == null) {
			if (other.direccion != null)
				return false;
		} else if (!direccion.equals(other.direccion))
			return false;
		if (estado == null) {
			if (other.estado != null)
				return false;
		} else if (!estado.equals(other.estado))
			return false;
		if (asistenciaSemanal == null) {
			if (other.asistenciaSemanal != null)
				return false;
		} else if (!asistenciaSemanal.equals(other.asistenciaSemanal))
			return false;
		if (formaPago == null) {
			if (other.formaPago != null)
				return false;
		} else if (!formaPago.equals(other.formaPago))
			return false;
		if (listaMensualidades == null) {
			if (other.listaMensualidades != null)
				return false;
		} else if (!listaMensualidades.equals(other.listaMensualidades))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return getId() + " " + getNombre() + " " + getApellido1() + " " + getApellido2();
	}
	
}
