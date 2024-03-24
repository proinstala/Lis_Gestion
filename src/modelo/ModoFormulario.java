package modelo;

public enum ModoFormulario {
    
    CREAR_DATOS("Nuevo"),
    EDITAR_DATOS("Modificar"),
    VER_DATOS("Ver"),
    BORRAR_DATOS("Borrar");

    //Campos del enumerado.
    private String accion;

    //Constructor del enumerado.
    private ModoFormulario(String accion) {
        this.accion = accion;
    }


    //Metodos getter and setter.
    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

}
