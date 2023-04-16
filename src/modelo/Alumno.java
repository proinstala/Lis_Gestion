package modelo;

import java.time.LocalDate;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Esta es una clase que representa un Alumno en Java.
 * 
 * @author David Jimenez Alonso
 *
 */
public class Alumno {
	
	private IntegerProperty id;
	private StringProperty nombre;
	private StringProperty apellido1;
	private StringProperty apellido2;
	private ObjectProperty<Genero> genero;
	private ObjectProperty<LocalDate> fechaNacimiento;
	private ObjectProperty<Direccion> direccion;
	private IntegerProperty telefono;
	private StringProperty email;
	
	
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
	 */
	public Alumno(int id, String nombre, String apellido1, String apellido2, Genero genero, 
			LocalDate fechaNacimiento, Direccion direccion, int telefono, String email) {
		this.id = new SimpleIntegerProperty(id);
		this.nombre = new SimpleStringProperty(nombre);
		this.apellido1 = new SimpleStringProperty(apellido1);
		this.apellido2 = new SimpleStringProperty(apellido2);
		this.genero = new SimpleObjectProperty<Genero>(genero);
		this.fechaNacimiento = new SimpleObjectProperty<LocalDate>(fechaNacimiento);
		this.direccion = new SimpleObjectProperty<Direccion>(direccion);
		this.telefono = new SimpleIntegerProperty(telefono);
		this.email = new SimpleStringProperty(email);
	}
	
	/**
	 * Contructor de copia.
	 * Crea un objeto de tipo Alumno con los datos del objeto pasado como parametro.
	 * 
	 * @param a Objeto de donde se obtienen los datos para la copia.
	 */
	public Alumno(Alumno a) {
		id = new SimpleIntegerProperty(a.getId());
		nombre = new SimpleStringProperty(a.getNombre());
		apellido1 = new SimpleStringProperty(a.getApellido1());
		apellido2 = new SimpleStringProperty(a.getApellido2());
		genero = new SimpleObjectProperty<Genero>(a.getGenero());
		fechaNacimiento = new SimpleObjectProperty<LocalDate>(a.getFechaNacimiento());
		direccion = new SimpleObjectProperty<Direccion>(a.getDireccion());
		telefono = new SimpleIntegerProperty(a.getTelefono());
		email = new SimpleStringProperty(a.getEmail());
	}
	
	// id -----------------------------------------
	/**
     * Obtiene la propiedad del ID del alumno.
     *
     * @return La propiedad del ID del alumno.
     */
	public IntegerProperty idProperty() {
		return id;
	}

	/**
     * Obtiene el ID del alumno.
     *
     * @return El ID del alumno.
     */
	public int getId() {
		return id.get();
	}

	/**
     * Establece el ID del alumno.
     *
     * @param id El nuevo ID para el alumno.
     */
	public void setId(int id) {
		this.id.set(id);
	}
	
	
	// nombre -----------------------------------
	/**
     * Obtiene la propiedad del nombre del alumno.
     *
     * @return La propiedad del nombre del alumno.
     */
	public StringProperty nombreProperty() {
		return nombre;
	}

	/**
     * Obtiene el nombre del alumno.
     *
     * @return El nombre del alumno.
     */
	public String getNombre() {
		return this.nombre.get();
	}

	/**
     * Establece el nombre del alumno.
     *
     * @param nombre El nuevo nombre para el alumno.
     */
	public void setNombre(String nombre) {
		this.nombre.set(nombre);
	}
	
	// apellido1 -----------------------------------
	/**
     * Obtiene la propiedad del primer apellido del alumno.
     *
     * @return La propiedad del primer apellido del alumno.
     */
	public StringProperty apellido1Property() {
		return apellido1;
	}

	/**
     * Obtiene el primer apellido del alumno.
     *
     * @return El primer apellido del alumno.
     */
	public String getApellido1() {
		return this.apellido1.get();
	}

	/**
     * Establece el primer apellido del alumno.
     *
     * @param apellido1 El nuevo primer apellido para el alumno.
     */
	public void setApellido1(String apellido1) {
		this.apellido1.set(apellido1);
	}
	
	
	// apellido2 -----------------------------------
	/**
     * Obtiene la propiedad del segundo apellido del alumno.
     *
     * @return La propiedad del segundo apellido del alumno.
     */
	public StringProperty apellido2Property() {
		return apellido2;
	}

	/**
     * Obtiene el segundo apellido del alumno.
     *
     * @return El segundo apellido del alumno.
     */
	public String getApellido2() {
		return this.apellido2.get();
	}

	/**
     * Establece el segundo apellido del alumno.
     *
     * @param apellido2 El nuevo segundo apellido para el alumno.
     */
	public void setApellido2(String apellido2) {
		this.apellido2.set(apellido2);
	}
	
	
	// genero ------------------------------------
	/**
     * Obtiene el género del alumno.
     *
     * @return El género del alumno.
     */
	public ObjectProperty<Genero> generoProperty() {
		return genero;
	}

	/**
     * Establece el género del alumno.
     *
     * @param genero El nuevo género para el alumno.
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
	
		
	// telefono -----------------------------------------
	/**
     * Obtiene la propiedad del teléfono del alumno.
     *
     * @return La propiedad del teléfono del alumno.
     */
	public IntegerProperty telefonoProperty() {
		return telefono;
	}

	/**
	 * Obtiene el teléfono del alumno.
	 *
	 * @return El teléfono del alumno.
	 */
	public int getTelefono() {
		return telefono.get();
	}

	/**
	 * Establece el teléfono del alumno.
	 *
	 * @param telefono El nuevo teléfono para el alumno.
	 */
	public void setTelefono(int telefono) {
		this.telefono.set(telefono);
	}
	
	// email -----------------------------------
	/**
	 * Obtiene la propiedad del email del alumno.
	 *
	 * @return La propiedad del email del alumno.
	 */
	public StringProperty emailProperty() {
		return email;
	}

	/**
	 * Obtiene el email del alumno.
	 *
	 * @return El email del alumno.
	 */
	public String getEmail() {
		return this.email.get();
	}

	/**
	 * Establece el email del alumno.
	 *
	 * @param email El nuevo email para el alumno.
	 */
	public void setEmail(String email) {
		this.email.set(email);
	}

	@Override
	public String toString() {
		return getId() + " " + getNombre() + " " + getApellido1() + " " + getApellido2();
	}
	
	

}
