package modelo;

import java.io.File;

public class Usuario {
    
    private int id;
    private String nombre;
    private String password;
    private File directorio;
    private String passwordBD;

    public Usuario() {}

    public Usuario(int id, String nombre, String password, File directorio, String passwordDB) {
        this.id = id;
        this.nombre = nombre;
        this.password = password;
        this.directorio = directorio;
        this.passwordBD = passwordDB;
    }

    public Usuario(Usuario u) {
        this.id = u.id;
        this.nombre = u.getNombre();
        this.password = u.getPassword();
        this.directorio = u.getDirectorio();
        this.passwordBD = u.getPasswordBD();
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public File getDirectorio() {
        return directorio;
    }

    public void setDirectorio(File directorio) {
        this.directorio = directorio;
    }

    public String getPasswordBD() {
        return passwordBD;
    }

    public void setPasswordBD(String passwordBD) {
        this.passwordBD = passwordBD;
    }

    

    

}
