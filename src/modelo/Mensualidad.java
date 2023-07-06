package modelo;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Esta es una clase que representa una Mensualidad
 * para un Alumno de pilates del centro de pilates LIS Pilates.
 * 
 * @author David Jimenez Alonso
 */
public class Mensualidad {

     private IntegerProperty id;
     private IntegerProperty idAlumno;
     private ObjectProperty<YearMonth> fecha;
     private ObjectProperty<LocalDate> fechaPago;
     private ObjectProperty<FormaPago> formaPago;
     private ObjectProperty<EstadoPago> estadoPago;
     private IntegerProperty asistenciasSemanales;
     private DoubleProperty importe;
     private StringProperty anotacion;

     public Mensualidad() {
          this.id = new SimpleIntegerProperty(-1);
          this.idAlumno = new SimpleIntegerProperty(-1);
          this.fecha = new SimpleObjectProperty<YearMonth>(YearMonth.now());
          this.fechaPago = new SimpleObjectProperty<LocalDate>(null);
          this.formaPago = new SimpleObjectProperty<FormaPago>(null);
          this.estadoPago = new SimpleObjectProperty<EstadoPago>(null);
          this.asistenciasSemanales = new SimpleIntegerProperty(0);
          this.importe = new SimpleDoubleProperty(0.0);
          this.anotacion = new SimpleStringProperty("");
     }

     public Mensualidad(int id, int idAlumno, YearMonth fecha, LocalDate fechaPago, FormaPago formaPago, EstadoPago estadoPago,
               int asistenciasSemanales, Double importe, String anotacion) {
          this.id = new SimpleIntegerProperty(id);
          this.idAlumno = new SimpleIntegerProperty(idAlumno);
          this.fecha = new SimpleObjectProperty<YearMonth>(fecha);
          this.fechaPago = new SimpleObjectProperty<LocalDate>(fechaPago);
          this.formaPago = new SimpleObjectProperty<FormaPago>(formaPago);
          this.estadoPago = new SimpleObjectProperty<EstadoPago>(estadoPago);
          this.asistenciasSemanales = new SimpleIntegerProperty(asistenciasSemanales);
          this.importe = new SimpleDoubleProperty(importe);
          this.anotacion = new SimpleStringProperty(anotacion);
     }

     public Mensualidad(Mensualidad m) {
          this.id = new SimpleIntegerProperty(m.getId());
          this.idAlumno = new SimpleIntegerProperty(m.getIdAlumno());
          this.fecha = new SimpleObjectProperty<YearMonth>(m.getFecha());
          this.fechaPago = new SimpleObjectProperty<LocalDate>(m.getFechaPago());
          this.formaPago = new SimpleObjectProperty<FormaPago>(m.getFormaPago());
          this.estadoPago = new SimpleObjectProperty<EstadoPago>(m.getEstadoPago());
          this.asistenciasSemanales = new SimpleIntegerProperty(m.getAsistenciasSemanales());
          this.importe = new SimpleDoubleProperty(m.getImporte());
          this.anotacion = new SimpleStringProperty(m.getAnotacion());
     }

     /**
      * Establece los datos del objeto pasados como parametros a este objeto.
      * 
      * @param m objeto de donde se obtiene la información.
      */
     public void setValoresMensualidad(Mensualidad m) {
          this.id.set(m.getId());
          this.idAlumno.set(m.getIdAlumno());
          this.fecha.set(m.getFecha());
          this.fechaPago.set(m.getFechaPago());
          this.formaPago.set(m.getFormaPago());
          this.estadoPago.set(m.getEstadoPago());
          this.asistenciasSemanales.set(m.getAsistenciasSemanales());
          this.importe.set(m.getImporte());
          this.anotacion.set(m.getAnotacion());
     }

     // id -----------------------------------------
     /**
      * Obtiene la propiedad del ID de la mensualidad.
      *
      * @return La propiedad del ID de la mensualidad.
      */
     public IntegerProperty idProperty() {
          return id;
     }

     /**
      * Obtiene el ID de la mensualidad.
      *
      * @return El ID de la mensualidad.
      */
     public int getId() {
          return id.get();
     }

     /**
      * Establece el ID de la mensualidad.
      *
      * @param id El nuevo ID para la mensualidad.
      */
     public void setId(int id) {
          this.id.set(id);
     }

     // idAlumno -----------------------------------------
     /**
      * Obtiene la propiedad del ID del alumno al que pertenece esta mensualidad.
      *
      * @return La propiedad del ID del alumno.
      */
     public IntegerProperty idAlumnoProperty() {
          return idAlumno;
     }

     /**
      * Obtiene el ID del alumno al que pertenece esta mensualidad.
      *
      * @return El ID del alumnno al que pertenece esta mensualidad.
      */
     public int getIdAlumno() {
          return idAlumno.get();
     }

     /**
      * Establece el ID del alumno al que pertenece esta mensualidad.
      *
      * @param idAlumno El nuevo ID del alumno para la mensualidad.
      */
     public void setIdAlumno(int idAlumno) {
          this.idAlumno.set(idAlumno);
     }

     // fecha ------------------------------------
     /**
      * Obtiene la propiedad de la fecha de la mensualidad.
      *
      * @return La propiedad de fecha de la mensualidad.
      */
     public ObjectProperty<YearMonth> fechaProperty() {
          return fecha;
     }

     /**
      * obtiene la fecha de la mensualidad.
      *
      * @return La fecha de la mensualidad.
      */
     public YearMonth getFecha() {
          return fecha.get();
     }

     /**
      * Establece la fecha de la mensualidad.
      *
      * @param fecha La nueva fecha de la mensualidad.
      */
     public void setFecha(YearMonth fecha) {
          this.fecha.set(fecha);
     }


     // fechaPago ------------------------------------
	/**
     * Obtiene la propiedad de la fechaPago de la mensualidad..
     *
     * @return La propiedad de la fechaPago de la mensualidad..
     */
	public ObjectProperty<LocalDate> fechaPagoProperty() {
		return fechaPago;
	}

	/**
     * Obtiene la fechaPago de la mensualidad..
     *
     * @return La fechaPago de la mensualidad..
     */
	public LocalDate getFechaPago() {
		return fechaPago.get();
	}

	/**
     * Establece la fechaPago de la mensualidad..
     *
     * @param fechaPago La nueva fechaPago para la mensualidad..
     */
	public void setFechaPago(LocalDate fechaPago) {
		this.fechaPago.set(fechaPago);
	}


     // formaPago ------------------------------------
     /**
      * Obtiene la propiedad de la formaPago.
      *
      * @return La propiedad de fecha de la formaPago.
      */
     public ObjectProperty<FormaPago> formaPagoProperty() {
          return formaPago;
     }

     /**
      * obtiene la formaPago de la mensualidad.
      *
      * @return La formaPago de la mensualidad.
      */
     public FormaPago getFormaPago() {
          return formaPago.get();
     }

     /**
      * Establece la formaPago de la mensualidad.
      *
      * @param formaPago La nueva formaPago de la mensualidad.
      */
     public void setFormaPago(FormaPago formaPago) {
          this.formaPago.set(formaPago);
     }

     // estadoPago ------------------------------------
     /**
      * Obtiene la propiedad del estadoPago de la mensualidad.
      *
      * @return La propiedad del estadoPago de la mensualidad.
      */
     public ObjectProperty<EstadoPago> estadoPagoProperty() {
          return estadoPago;
     }

     /**
      * obtiene el estadoPago de la mensualidad.
      *
      * @return El estadoPago de la mensualidad.
      */
     public EstadoPago getEstadoPago() {
          return estadoPago.get();
     }

     /**
      * Establece el estadoPago de la mensualidad.
      *
      * @param estadoPago El nuevo estadoPago de la mensualidad.
      */
     public void setEstadoPago(EstadoPago estadoPago) {
          this.estadoPago.set(estadoPago);
     }

     // asistenciasSemanales -----------------------------------------
     /**
      * Obtiene la propiedad de la asistenciasSemanales de la mensualidad.
      *
      * @return La propiedad de la asistenciasSemanales de la mensualidad.
      */
     public IntegerProperty asistenciasSemanalesProperty() {
          return asistenciasSemanales;
     }

     /**
      * Obtiene la asistenciasSemanales de la mensualidad.
      *
      * @return La asistenciasSemanales de la mensualidad.
      */
     public int getAsistenciasSemanales() {
          return asistenciasSemanales.get();
     }

     /**
      * Establece la asistenciasSemanales de la mensualidad.
      *
      * @param asistenciasSemanales La nueva asistenciasSemanales para la
      *                             mensualidad.
      */
     public void setAsistenciasSemanales(int asistenciasSemanales) {
          this.asistenciasSemanales.set(asistenciasSemanales);
     }

     // importe -----------------------------------------
     /**
      * Obtiene la propiedad del importe de la mensualidad.
      *
      * @return La propiedad del importe de la mensualidad.
      */
     public DoubleProperty importeProperty() {
          return importe;
     }

     /**
      * Obtiene el importe de la mensualidad.
      *
      * @return El importe de la mensualidad.
      */
     public Double getImporte() {
          return importe.get();
     }

     /**
      * Establece el importe de la mensualidad.
      *
      * @param importe El nuevo importe para la mensualidad.
      */
     public void setImporte(Double importe) {
          this.importe.set(importe);
     }


     // anotacion -----------------------------------
	/**
     * Obtiene la propiedad de la anotacion de la Mensualidad.
     *
     * @return La propiedad de la anotacion de la Mensualidad.
     */
	public StringProperty anotacionProperty() {
		return anotacion;
	}

	/**
     * Obtiene la anotacion de la Mensualidad.
     *
     * @return La anotacion de la Mensualidad.
     */
	public String getAnotacion() {
		return this.anotacion.get();
	}

	/**
     * Establece la anotacion de la Mensualidad.
     *
     * @param anotaciones La nueva anotacion para la Mensualidad.
     */
	public void setAnotacion(String anotacion) {
		this.anotacion.set(anotacion);
	}


     /**
      * Traduce al castellano el mes de esta mensualidad y lo devueve en forma de String.
      *
      * @return String con el nombre del mes de esta mensualidad en castellano.
      */
     public String getMesMensualidad_ES() {
          return this.fecha.get().getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
     }


     @Override
     public String toString() {
          return "Mensualidad [id=" + id + ", idAlumno=" + idAlumno + ", fecha=" + fecha + ", fechaPago=" + fechaPago
                    + ", formaPago=" + formaPago + ", estadoPago=" + estadoPago + ", asistenciasSemanales="
                    + asistenciasSemanales + ", importe=" + importe + ", anotacion=" + anotacion + "]";
     }

     

     

}
