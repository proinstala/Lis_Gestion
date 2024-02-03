package modelo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class GrupoAlumnos {
    
    private IntegerProperty id;
    private StringProperty nombre;
    private StringProperty descripcion;


    /**
     * Constructor por defecto de la clase GrupoAlumnos.
     * Inicializa los atributos con valores predeterminados.
     */
    public GrupoAlumnos() {
        this.id = new SimpleIntegerProperty(-1);
        this.nombre = new SimpleStringProperty("");
        this.descripcion = new SimpleStringProperty("");
    }

    /**
     * Constructor de la clase GrupoAlumnos con parámetros.
     * Inicializa los atributos con los valores proporcionados.
     *
     * @param id          Identificador único del grupo.
     * @param nombre      Nombre del grupo.
     * @param descripcion Descripción del grupo.
     */
    public GrupoAlumnos(int id, String nombre, String descripcion) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.descripcion = new SimpleStringProperty(descripcion);
    }

    /**
     * Constructor de copia de la clase GrupoAlumnos.
     * Crea una nueva instancia de GrupoAlumnos a partir de otra instancia existente.
     *
     * @param grupo La instancia de GrupoAlumnos a ser copiada.
     */
    public GrupoAlumnos(GrupoAlumnos grupo) {
        id = new SimpleIntegerProperty(grupo.getId());
        nombre = new SimpleStringProperty(grupo.getNombre());
        descripcion = new SimpleStringProperty(grupo.getDescripcion());
    }


    // id -----------------------------------------
	/**
     * Obtiene la propiedad del id del Grupo de Alumnos.
     *
     * @return La propiedad del id del Grupo de Alumnos.
     */
	public IntegerProperty idProperty() {
		return id;
	}

	/**
     * Obtiene el id del Grupo de Alumnos.
     *
     * @return El id del Grupo de Alumnos.
     */
	public int getId() {
		return id.get();
	}

	/**
     * Establece el id del Grupo de Alumnos.
     *
     * @param id El nuevo id para el Grupo de Alumnos.
     */
	public void setId(int id) {
		this.id.set(id);
	}


    // nombre -----------------------------------
	/**
     * Obtiene la propiedad del nombre del Grupo de Alumnos.
     *
     * @return La propiedad del nombre del Grupo de Alumnos.
     */
	public StringProperty nombreProperty() {
		return nombre;
	}

	/**
     * Obtiene el nombre del Grupo de Alumnos.
     *
     * @return El nombre del Grupo de Alumnos.
     */
	public String getNombre() {
		return this.nombre.get();
	}

	/**
     * Establece el nombre del Grupo de Alumnos.
     *
     * @param nombre El nuevo nombre para el Grupo de Alumnos.
     */
	public void setNombre(String nombre) {
		this.nombre.set(nombre);
	}


    // descripción -----------------------------------
	/**
     * Obtiene la propiedad de la descripción del Grupo de Alumnos.
     *
     * @return La propiedad de la descripción del Grupo de Alumnos.
     */
	public StringProperty descripcionProperty() {
		return descripcion;
	}

	/**
     * Obtiene la descripción del Grupo de Alumnos.
     *
     * @return La descripción del Grupo de Alumnos.
     */
	public String getDescripcion() {
		return this.descripcion.get();
	}

	/**
     * Establece la descripción del Grupo de Alumnos.
     *
     * @param nombre La nueva descripción para el Grupo de Alumnos.
     */
	public void setDescripcion(String descripcion) {
		this.descripcion.set(descripcion);
	}


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
        result = prime * result + ((descripcion == null) ? 0 : descripcion.hashCode());
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
        GrupoAlumnos other = (GrupoAlumnos) obj;
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
        if (descripcion == null) {
            if (other.descripcion != null)
                return false;
        } else if (!descripcion.equals(other.descripcion))
            return false;
        return true;
    }


    @Override
    public String toString() {
        return "GrupoAlumnos [id=" + id + ", nombre=" + nombre + ", descripcion=" + descripcion + "]";
    }

}
