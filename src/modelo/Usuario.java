package modelo;

import java.io.File;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


/**
 * Esta es una clase que representa un Usuario de la aplicación "Lis Gestión".
 * 
 * @author David Jimenez Alonso.
 * 
 */
public class Usuario extends Persona {

     private StringProperty nombreUsuario;
     private StringProperty password;
     private ObjectProperty<File> directorio;
     private StringProperty emailApp;
     private StringProperty passwordEmailApp;


     /**
      * Constructor sin parametros.
      * Crea un objeto de tipo Usuario con todos los campos a null.
      * 
      */
     public Usuario() {
          super();

          this.nombreUsuario = new SimpleStringProperty(null);
          this.password = new SimpleStringProperty(null);
          this.directorio = new SimpleObjectProperty<File>(null);
          this.emailApp = new SimpleStringProperty(null);
          this.passwordEmailApp = new SimpleStringProperty(null);
     }


     /**
      * Contructor con parametros.
      * Crea un objeto de tipo Usuario con los datos pasados por parametros.
      *
      * @param id Identificador del usuario.
      * @param nombreUsuario Nombre de usuario del usuario.
      * @param nombre Nombre del usuario.
      * @param apellido1 Primer apellido del usuario.
      * @param apellido2 Segundo Apellido del usuario.
      * @param password Password de usuario.
      * @param directorio Directorio donde se guardan los datos del usuario.
      * @param telefono Número de telefono del usuario.
      * @param email Direccion de correo electronico del usuario.
      * @param emailApp Direccion de correo electronico para la aplicación.
      * @param passwordEmailApp Password de la dirección de correo eletronico de la aplicación.
      */
     public Usuario(int id, String nombreUsuario, String nombre, String apellido1, String apellido2, String password, File directorio,
               int telefono, String email, String emailApp, String passwordEmailApp) {
          super(id, nombre, apellido1, apellido2, telefono, email);

          this.nombreUsuario = new SimpleStringProperty(nombreUsuario);
          this.password = new SimpleStringProperty(password);
          this.directorio = new SimpleObjectProperty<File>(directorio);
          this.emailApp = new SimpleStringProperty(emailApp);
          this.passwordEmailApp = new SimpleStringProperty(passwordEmailApp);
     }


     /**
      * Contructor con parametros. 
      * Crea un objeto de tipo Usuario con los datos pasados por parametros.
      *
      * @param id Identificador del usuario.
      * @param nombreUsuario Nombre de usuario del usuario.
      * @param password Password de usuario.
      * @param directorio Directorio donde se guardan los datos del usuario.
      */
     public Usuario(int id, String nombreUsuario, String password, File directorio) {
          super(id, "", "", "", 0, "");

          this.nombreUsuario = new SimpleStringProperty(nombreUsuario);
          this.password = new SimpleStringProperty(password);
          this.directorio = new SimpleObjectProperty<File>(directorio);
          this.emailApp = new SimpleStringProperty(null);
          this.passwordEmailApp = new SimpleStringProperty(null);
     }

     
     /**
	 * Contructor de copia.
	 * Crea un objeto de tipo Usuario con los datos del objeto pasado como parametro.
	 * 
	 * @param u Objeto de donde se obtienen los datos para la copia.
	 */
     public Usuario(Usuario u) {
          super(u);

          nombreUsuario = new SimpleStringProperty(u.getNombreUsuario());
          password = new SimpleStringProperty(u.getPassword());
          directorio = new SimpleObjectProperty<File>(u.getDirectorio());
          emailApp = new SimpleStringProperty(u.getEmailApp());
          passwordEmailApp = new SimpleStringProperty(u.getPasswordEmailApp());
     }


     // nombre usuario --------------------------------
     /**
      * Obtiene la propiedad del nombreUsuario del usuario.
      *
      * @return La propiedad del nombreUsuario del usuario.
      */
     public StringProperty nombreUsuarioProperty() {
          return nombreUsuario;
     }

     /**
      * Obtiene el nombreUsuario del usuario.
      *
      * @return El nombreUsuario del usuario.
      */
     public String getNombreUsuario() {
          return this.nombreUsuario.get();
     }

     /**
      * Establece el nombreUsuario del usuario.
      *
      * @param nombreUsuario El nuevo nombreUsuario para el usuario.
      */
     public void setNombreUsuario(String nombreUsuario) {
          this.nombreUsuario.set(nombreUsuario);
     }

     
     // password -----------------------------------
     /**
      * Obtiene la propiedad del password del usuario.
      *
      * @return La propiedad del password del usuario.
      */
     public StringProperty passwordProperty() {
          return password;
     }

     /**
      * Obtiene el password del usuario.
      *
      * @return El password del usuario.
      */
     public String getPassword() {
          return this.password.get();
     }

     /**
      * Establece el password del usuario.
      *
      * @param password El nuevo password para el usuario.
      */
     public void setPassword(String password) {
          this.password.set(password);
     }


     // directorio ------------------------------------
     /**
      * Obtiene la propiedad del directorio del usuario.
      *
      * @return La propiedad del directorio del usuario.
      */
     public ObjectProperty<File> directorioProperty() {
          return directorio;
     }

     /**
      * Obtiene el directorio del usuario.
      *
      * @return El directorio del usuario.
      */
     public File getDirectorio() {
          return directorio.get();
     }

     /**
      * Establece el directorio del usuario.
      *
      * @param directorio El nuevo directorio para el usuario.
      */
     public void setDirectorio(File directorio) {
          this.directorio.set(directorio);
     }


     // emailApp -----------------------------------
     /**
      * Obtiene la propiedad del emailApp del usuario.
      *
      * @return La propiedad del emailApp del usuario.
      */
     public StringProperty emailAppProperty() {
          return emailApp;
     }

     /**
      * Obtiene el emailApp del usuario.
      *
      * @return El emailApp del usuario.
      */
     public String getEmailApp() {
          return this.emailApp.get();
     }

     /**
      * Establece el emailApp del usuario.
      *
      * @param email El nuevo emailApp para el usuario.
      */
     public void setEmailApp(String emailApp) {
          this.emailApp.set(emailApp);
     }


     // passwordEmailApp -----------------------------------
     /**
      * Obtiene la propiedad del passwordEmailApp del usuario.
      *
      * @return La propiedad del passwordEmailApp del usuario.
      */
     public StringProperty passwordEmailAppProperty() {
          return passwordEmailApp;
     }

     /**
      * Obtiene el passwordEmailApp del usuario.
      *
      * @return El passwordEmailApp del usuario.
      */
     public String getPasswordEmailApp() {
          return this.passwordEmailApp.get();
     }

     /**
      * Establece el passwordEmailApp del usuario.
      *
      * @param passwordEmailApp El nuevo passwordEmailApp para el usuario.
      */
     public void setPasswordEmailApp(String passwordEmailApp) {
          this.passwordEmailApp.set(passwordEmailApp);
     }


     /**
	 * Obtine un String con el nombre y apellidos del Usuario.
	 * 
	 * @return String con el nombre y apellidos del Usuario.
	 */
	public String getNombreCompleto() {
		return getNombre() + " " + getApellido1() + " " + getApellido2();
	}

     
     @Override
     public int hashCode() {
          final int prime = 31;
          int result = super.hashCode();
          result = prime * result + ((nombreUsuario == null) ? 0 : nombreUsuario.hashCode());
          result = prime * result + ((password == null) ? 0 : password.hashCode());
          result = prime * result + ((directorio == null) ? 0 : directorio.hashCode());
          result = prime * result + ((emailApp == null) ? 0 : emailApp.hashCode());
          result = prime * result + ((passwordEmailApp == null) ? 0 : passwordEmailApp.hashCode());
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
          Usuario other = (Usuario) obj;
          if (nombreUsuario == null) {
               if (other.nombreUsuario != null)
                    return false;
          } else if (!nombreUsuario.equals(other.nombreUsuario))
               return false;
          if (password == null) {
               if (other.password != null)
                    return false;
          } else if (!password.equals(other.password))
               return false;
          if (directorio == null) {
               if (other.directorio != null)
                    return false;
          } else if (!directorio.equals(other.directorio))
               return false;
          if (emailApp == null) {
               if (other.emailApp != null)
                    return false;
          } else if (!emailApp.equals(other.emailApp))
               return false;
          if (passwordEmailApp == null) {
               if (other.passwordEmailApp != null)
                    return false;
          } else if (!passwordEmailApp.equals(other.passwordEmailApp))
               return false;
          return true;
     }

     @Override
     public String toString() {
          return super.toString() + "Usuario [nombreUsuario=" + nombreUsuario + ", password=" + password + ", directorio=" + directorio
                    + ", emailApp=" + emailApp + ", passwordEmailApp=" + passwordEmailApp + "]";
     }

}
