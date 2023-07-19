package utilidades;

/**
 * La interfaz CorreoCallback se utiliza para notificar el resultado del envío de un correo electrónico.
 * Debe ser implementada por cualquier clase que desee recibir notificaciones sobre el resultado del envío del correo.
 */
@FunctionalInterface
public interface CorreoCallback {
    
    /**
     * Este método es llamado para notificar el resultado del envío de correo electrónico.
     * 
     * @param exito true si el correo fue enviado con éxito, false en caso contrario.
     * @param mensaje El mensaje asociado al resultado del envío del correo.
     */
    public abstract void onCorreoEnviado(boolean exito, String mensaje);
}
