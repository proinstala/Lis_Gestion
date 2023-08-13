package modelo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


/**
 * Esta es una clase que representa una Persona en Java.
 */
public abstract class Persona {

    protected IntegerProperty id;
	protected StringProperty nombre;
	protected StringProperty apellido1;
	protected StringProperty apellido2;
    protected IntegerProperty telefono;
	protected StringProperty email;


    /**
     * Contructor sin parametros.
     * Crea un objeto de tipo Persona con los campos a null.
     */
    protected Persona() {
        this.id = new SimpleIntegerProperty();
		this.nombre = new SimpleStringProperty(null);
		this.apellido1 = new SimpleStringProperty(null);
		this.apellido2 = new SimpleStringProperty(null);
		this.telefono = new SimpleIntegerProperty();
		this.email = new SimpleStringProperty(null);
    }


    /**
	 * Contructor con parametros.
	 * Crea un objeto de tipo Persona con los datos pasados por parametros.
	 * 
	 * @param id Identificador de la persona.
	 * @param nombre Nombre de la persona.
	 * @param apellido1 Primer apellido de la persona.
	 * @param apellido2 Segundo apellido de la persona.
	 * @param telefono Telefono de la persona.
	 * @param email Direccion de correo electronico de la persona.
	 */
    protected Persona(int id, String nombre, String apellido1, String apellido2,
            int telefono, String email) {
        this.id = new SimpleIntegerProperty(id);
		this.nombre = new SimpleStringProperty(nombre);
		this.apellido1 = new SimpleStringProperty(apellido1);
		this.apellido2 = new SimpleStringProperty(apellido2);
        this.telefono = new SimpleIntegerProperty(telefono);
		this.email = new SimpleStringProperty(email);
    }
	

    /**
	 * Contructor de copia.
	 * Crea un objeto de tipo Persona con los datos del objeto pasado como parametro.
	 * 
	 * @param p Objeto de donde se obtienen los datos para la copia.
	 */
	protected Persona(Persona p) {
		id = new SimpleIntegerProperty(p.getId());
		nombre = new SimpleStringProperty(p.getNombre());
		apellido1 = new SimpleStringProperty(p.getApellido1());
		apellido2 = new SimpleStringProperty(p.getApellido2());
		telefono = new SimpleIntegerProperty(p.getTelefono());
		email = new SimpleStringProperty(p.getEmail());
	}


    // id -----------------------------------------
	/**
     * Obtiene la propiedad del ID de la persona.
     *
     * @return La propiedad del ID de la persona.
     */
	public IntegerProperty idProperty() {
		return id;
	}

	/**
     * Obtiene el ID de la persona.
     *
     * @return El ID de la persona.
     */
	public int getId() {
		return id.get();
	}

	/**
     * Establece el ID de la persona.
     *
     * @param id El nuevo ID para la persona.
     */
	public void setId(int id) {
		this.id.set(id);
	}
	
	
	// nombre -----------------------------------
	/**
     * Obtiene la propiedad del nombre de la persona.
     *
     * @return La propiedad del nombre de la persona.
     */
	public StringProperty nombreProperty() {
		return nombre;
	}

	/**
     * Obtiene el nombre de la persona.
     *
     * @return El nombre de la persona.
     */
	public String getNombre() {
		return this.nombre.get();
	}

	/**
     * Establece el nombre de la persona.
     *
     * @param nombre El nuevo nombre para la persona.
     */
	public void setNombre(String nombre) {
		this.nombre.set(nombre);
	}
	

	// apellido1 -----------------------------------
	/**
     * Obtiene la propiedad del primer apellido de la persona.
     *
     * @return La propiedad del primer apellido de la persona.
     */
	public StringProperty apellido1Property() {
		return apellido1;
	}

	/**
     * Obtiene el primer apellido de la persona.
     *
     * @return El primer apellido de la persona.
     */
	public String getApellido1() {
		return this.apellido1.get();
	}

	/**
     * Establece el primer apellido de la persona.
     *
     * @param apellido1 El nuevo primer apellido para la persona.
     */
	public void setApellido1(String apellido1) {
		this.apellido1.set(apellido1);
	}
	
	
	// apellido2 -----------------------------------
	/**
     * Obtiene la propiedad del segundo apellido de la persona.
     *
     * @return La propiedad del segundo apellido de la persona.
     */
	public StringProperty apellido2Property() {
		return apellido2;
	}

	/**
     * Obtiene el segundo apellido de la persona.
     *
     * @return El segundo apellido de la persona.
     */
	public String getApellido2() {
		return this.apellido2.get();
	}

	/**
     * Establece el segundo apellido de la persona.
     *
     * @param apellido2 El nuevo segundo apellido para la persona.
     */
	public void setApellido2(String apellido2) {
		this.apellido2.set(apellido2);
	}


    // telefono -----------------------------------------
	/**
     * Obtiene la propiedad del teléfono de la persona.
     *
     * @return La propiedad del teléfono de la persona.
     */
	public IntegerProperty telefonoProperty() {
		return telefono;
	}

	/**
	 * Obtiene el teléfono de la persona.
	 *
	 * @return El teléfono de la persona.
	 */
	public int getTelefono() {
		return telefono.get();
	}

	/**
	 * Establece el teléfono de la persona.
	 *
	 * @param telefono El nuevo teléfono para la persona.
	 */
	public void setTelefono(int telefono) {
		this.telefono.set(telefono);
	}
	

	// email -----------------------------------
	/**
	 * Obtiene la propiedad del email de la persona.
	 *
	 * @return La propiedad del email de la persona.
	 */
	public StringProperty emailProperty() {
		return email;
	}

	/**
	 * Obtiene el email de la persona.
	 *
	 * @return El email de la persona.
	 */
	public String getEmail() {
		return this.email.get();
	}

	/**
	 * Establece el email de la persona.
	 *
	 * @param email El nuevo email para la persona.
	 */
	public void setEmail(String email) {
		this.email.set(email);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
		result = prime * result + ((apellido1 == null) ? 0 : apellido1.hashCode());
		result = prime * result + ((apellido2 == null) ? 0 : apellido2.hashCode());
		result = prime * result + ((telefono == null) ? 0 : telefono.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Persona other = (Persona) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;
		if (apellido1 == null) {
			if (other.apellido1 != null)
				return false;
		} else if (!apellido1.equals(other.apellido1))
			return false;
		if (apellido2 == null) {
			if (other.apellido2 != null)
				return false;
		} else if (!apellido2.equals(other.apellido2))
			return false;
		if (telefono == null) {
			if (other.telefono != null)
				return false;
		} else if (!telefono.equals(other.telefono))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "Persona [id=" + id + ", nombre=" + nombre + ", apellido1=" + apellido1 + ", apellido2=" + apellido2
				+ ", telefono=" + telefono + ", email=" + email + "]";
	}

}
