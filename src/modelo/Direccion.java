package modelo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Esta es una clase que representa una Direccion de domicilio en java.
 * 
 * @author David Jimenez Alonso
 *
 */
public class Direccion {
	
	private IntegerProperty id;
	private StringProperty calle;
	private IntegerProperty numero;
	private StringProperty localidad;
	private StringProperty provincia;
	private IntegerProperty codigoPostal;
	
	
	/**
	 * Contructor con parametros.
	 * Crea un objeto de tipo Direccion con los datos pasados por parametros.
	 * 
	 * @param id Identificador de la calle.
	 * @param calle Nombre de la calle.
	 * @param numero Número del domicilio.
	 * @param localidad Localidad en la que se encuentra el domicilio.
	 * @param provincia Provincia en la que se encuentra el domicilio.
	 * @param codigoPostal Codigo postal del domicilio.
	 */
	public Direccion(int id, String calle, int numero, String localidad, String provincia, int codigoPostal) {
		this.id = new SimpleIntegerProperty(id);
		this.calle = new SimpleStringProperty(calle);
		this.numero = new SimpleIntegerProperty(numero);
		this.localidad = new SimpleStringProperty(localidad);
		this.provincia = new SimpleStringProperty(provincia);
		this.codigoPostal = new SimpleIntegerProperty(codigoPostal);
	}
	
	/**
	 * Contructor de copia.
	 * Crea un objeto de tipo Direccion con los datos del objeto pasado como parametro.
	 * 
	 * @param a Objeto de donde se obtienen los datos para la copia.
	 */
	public Direccion(Direccion d) {
		id = new SimpleIntegerProperty(d.getId());
		calle = new SimpleStringProperty(d.getCalle());
		numero = new SimpleIntegerProperty(d.getNumero());
		localidad = new SimpleStringProperty(d.getLocalidad());
		provincia = new SimpleStringProperty(d.getProvincia());
		codigoPostal = new SimpleIntegerProperty(d.getCodigoPostal());
	}
	
	// id -----------------------------------------
	/**
     * Obtiene la propiedad del ID de la direccion.
     *
     * @return La propiedad del ID de la direccion.
     */
	public IntegerProperty idProperty() {
		return id;
	}

	/**
     * Obtiene el ID de la direccion.
     *
     * @return El ID de la direccion.
     */
	public int getId() {
		return id.get();
	}

	/**
     * Establece el ID de la direccion.
     *
     * @param id El nuevo ID para la direccion.
     */
	public void setId(int id) {
		this.id.set(id);
	}
	
	
	// calle -----------------------------------
	/**
     * Obtiene la propiedad de la calle de la dirección.
     *
     * @return La propiedad de la calle de la dirección.
     */
	public StringProperty calleProperty() {
		return calle;
	}

	/**
     * Obtiene el número de la dirección.
     *
     * @return El número de la dirección.
     */
	public String getCalle() {
		return this.calle.get();
	}

	/**
     * Establece la calle de la dirección.
     *
     * @param calle La nueva calle para la dirección.
     */
	public void setCalle(String calle) {
		this.calle.set(calle);
	}
	
	
	// numero -----------------------------------
	/**
     * Obtiene la propiedad del número de la dirección.
     *
     * @return La propiedad del número de la dirección.
     */
	public IntegerProperty numeroProperty() {
		return numero;
	}

	/**
     * Obtiene el número de la dirección.
     *
     * @return El número de la dirección.
     */
	public int getNumero() {
		return this.numero.get();
	}

	/**
     * Establece el número de la dirección.
     *
     * @param numero El nuevo número para la dirección.
     */
	public void setNumero(int numero) {
		this.numero.set(numero);
	}
	
	
	// localidad -----------------------------------
	/**
     * Obtiene la propiedad de la localidad de la dirección.
     *
     * @return La propiedad de la localidad de la dirección.
     */
	public StringProperty localidadProperty() {
		return localidad;
	}

	/**
     * Obtiene la localidad de la dirección.
     *
     * @return La localidad de la dirección.
     */
	public String getLocalidad() {
		return this.localidad.get();
	}

	/**
     * Establece la localidad de la dirección.
     *
     * @param localidad La nueva localidad para la dirección.
     */
	public void setLocalidad(String localidad) {
		this.localidad.set(localidad);
	}
	
	
	// provincia -----------------------------------
	/**
     * Obtiene la propiedad de la provincia de la dirección.
     *
     * @return La propiedad de la provincia de la dirección.
     */
	public StringProperty provinciaProperty() {
		return provincia;
	}

	/**
     * Obtiene la provincia de la dirección.
     *
     * @return La provincia de la dirección.
     */
	public String getProvincia() {
		return this.provincia.get();
	}

	/**
     * Establece la provincia de la dirección.
     *
     * @param provincia La nueva provincia para la dirección.
     */
	public void setProvincia(String provincia) {
		this.provincia.set(provincia);
	}
	
	
	// codigoPostal -----------------------------------
	/**
     * Obtiene la propiedad del codigoPostal de la dirección.
     *
     * @return La propiedad del codigoPostal de la dirección.
     */
	public IntegerProperty codigoPostalProperty() {
		return codigoPostal;
	}

	/**
     * Obtiene el codigoPostal de la dirección.
     *
     * @return El codigoPostal de la dirección.
     */
	public int getCodigoPostal() {
		return this.codigoPostal.get();
	}

	/**
     * Establece el codigoPostal de la dirección.
     *
     * @param codigoPostal El nuevo codigoPostal para la dirección.
     */
	public void setCodigoPostal(int codigoPostal) {
		this.codigoPostal.set(codigoPostal);
	}

}
