package modelo;

import java.io.File;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Usuario {

     private IntegerProperty id;
     private StringProperty nombreUsuario;
     private StringProperty nombre;
     private StringProperty apellido1;
     private StringProperty apellido2;
     private StringProperty password;
     private ObjectProperty<File> directorio;
     private IntegerProperty telefono;
     private StringProperty email;
     private StringProperty emailApp;
     private StringProperty passwordEmailApp;

     /**
      * Constructor sin parametros.
      * Crea un objeto de tipo Usuario con todos los campos a null.
      * 
      */
     public Usuario() {
          this.id = new SimpleIntegerProperty();
          this.nombreUsuario = new SimpleStringProperty(null);
          this.nombre = new SimpleStringProperty(null);
          this.apellido1 = new SimpleStringProperty(null);
          this.apellido2 = new SimpleStringProperty(null);
          this.password = new SimpleStringProperty(null);
          this.directorio = new SimpleObjectProperty<File>(null);
          this.telefono = new SimpleIntegerProperty();
          this.email = new SimpleStringProperty(null);
          this.emailApp = new SimpleStringProperty(null);
          this.passwordEmailApp = new SimpleStringProperty(null);
     }

     public Usuario(int id, String nombreUsuario, String nombre, String apellido1, String apellido2, String password, File directorio,
               int telefono, String email, String emailApp, String passwordEmailApp) {
          this.id = new SimpleIntegerProperty(id);
          this.nombreUsuario = new SimpleStringProperty(nombreUsuario);
          this.nombre = new SimpleStringProperty(nombre);
          this.apellido1 = new SimpleStringProperty(apellido1);
          this.apellido2 = new SimpleStringProperty(apellido2);
          this.password = new SimpleStringProperty(password);
          this.directorio = new SimpleObjectProperty<File>(directorio);
          this.telefono = new SimpleIntegerProperty(telefono);
          this.email = new SimpleStringProperty(email);
          this.emailApp = new SimpleStringProperty(emailApp);
          this.passwordEmailApp = new SimpleStringProperty(passwordEmailApp);
     }

     public Usuario(int id, String nombreUsuario, String password, File directorio) {
          this.id = new SimpleIntegerProperty(id);
          this.nombreUsuario = new SimpleStringProperty(nombreUsuario);
          this.nombre = new SimpleStringProperty(null);
          this.apellido1 = new SimpleStringProperty(null);
          this.apellido2 = new SimpleStringProperty(null);
          this.password = new SimpleStringProperty(password);
          this.directorio = new SimpleObjectProperty<File>(directorio);
          this.telefono = new SimpleIntegerProperty();
          this.email = new SimpleStringProperty(null);
          this.emailApp = new SimpleStringProperty(null);
          this.passwordEmailApp = new SimpleStringProperty(null);
     }

     
     public Usuario(Usuario u) {
          id = new SimpleIntegerProperty(u.getId());
          nombreUsuario = new SimpleStringProperty(u.getNombreUsuario());
          nombre = new SimpleStringProperty(u.getNombre());
          apellido1 = new SimpleStringProperty(u.getApellido1());
          apellido2 = new SimpleStringProperty(u.getApellido2());
          password = new SimpleStringProperty(u.getPassword());
          directorio = new SimpleObjectProperty<File>(u.getDirectorio());
          telefono = new SimpleIntegerProperty(u.getTelefono());
          email = new SimpleStringProperty(u.getEmail());
          emailApp = new SimpleStringProperty(u.getEmailApp());
          passwordEmailApp = new SimpleStringProperty(u.getPasswordEmailApp());
     }
      

     // id -----------------------------------------
     /**
      * Obtiene la propiedad del ID del usuario.
      *
      * @return La propiedad del ID del usuario.
      */
     public IntegerProperty idProperty() {
          return id;
     }

     /**
      * Obtiene el ID del usuario.
      *
      * @return El ID del usuario.
      */
     public int getId() {
          return id.get();
     }

     /**
      * Establece el ID del usuario.
      *
      * @param id El nuevo ID para el usuario.
      */
     public void setId(int id) {
          this.id.set(id);
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

     // nombre -----------------------------------
     /**
      * Obtiene la propiedad del nombre del usuario.
      *
      * @return La propiedad del nombre del usuario.
      */
     public StringProperty nombreProperty() {
          return nombre;
     }

     /**
      * Obtiene el nombre del usuario.
      *
      * @return El nombre del usuario.
      */
     public String getNombre() {
          return this.nombre.get();
     }

     /**
      * Establece el nombre del usuario.
      *
      * @param nombre El nuevo nombre para el usuario.
      */
     public void setNombre(String nombre) {
          this.nombre.set(nombre);
     }

     // apellido1 -----------------------------------
     /**
      * Obtiene la propiedad del primer apellido del usuario.
      *
      * @return La propiedad del primer apellido del usuario.
      */
     public StringProperty apellido1Property() {
          return apellido1;
     }

     /**
      * Obtiene el primer apellido del usuario.
      *
      * @return El primer apellido del usuario.
      */
     public String getApellido1() {
          return this.apellido1.get();
     }

     /**
      * Establece el primer apellido del usuario.
      *
      * @param apellido1 El nuevo primer apellido para el usuario.
      */
     public void setApellido1(String apellido1) {
          this.apellido1.set(apellido1);
     }

     // apellido2 -----------------------------------
     /**
      * Obtiene la propiedad del segundo apellido del usuario.
      *
      * @return La propiedad del segundo apellido del usuario.
      */
     public StringProperty apellido2Property() {
          return apellido2;
     }

     /**
      * Obtiene el segundo apellido del usuario.
      *
      * @return El segundo apellido del usuario.
      */
     public String getApellido2() {
          return this.apellido2.get();
     }

     /**
      * Establece el segundo apellido del usuario.
      *
      * @param apellido2 El nuevo segundo apellido para el usuario.
      */
     public void setApellido2(String apellido2) {
          this.apellido2.set(apellido2);
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

     // telefono -----------------------------------------
     /**
      * Obtiene la propiedad del telefono del usuario.
      *
      * @return La propiedad del telefono del usuario.
      */
     public IntegerProperty telefonoProperty() {
          return telefono;
     }

     /**
      * Obtiene el telefono del usuario.
      *
      * @return El telefono del usuario.
      */
     public int getTelefono() {
          return telefono.get();
     }

     /**
      * Establece el telefono del usuario.
      *
      * @param id El nuevo telefono para el usuario.
      */
     public void setTelefono(int telefono) {
          this.telefono.set(telefono);
     }

     // email -----------------------------------
     /**
      * Obtiene la propiedad del email del usuario.
      *
      * @return La propiedad del email del usuario.
      */
     public StringProperty emailProperty() {
          return email;
     }

     /**
      * Obtiene el email del usuario.
      *
      * @return El email del usuario.
      */
     public String getEmail() {
          return this.email.get();
     }

     /**
      * Establece el email del usuario.
      *
      * @param email El nuevo email para el usuario.
      */
     public void setEmail(String email) {
          this.email.set(email);
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

     @Override
     public String toString() {
          return "Usuario [id=" + id + ", nombre=" + nombre + ", apellido1=" + apellido1 + ", apellido2=" + apellido2
                    + ", password=" + password + ", directorio=" + directorio + ", telefono=" + telefono + ", email=" + email
                    + ", emailApp=" + emailApp + ", passwordEmailApp=" + passwordEmailApp + "]";
     }

     

}
