package utilidades;

/**
 * Clase que contiene constantes utilizadas en la aplicación.
 * 
 * @author David Jimenez Alonso.
 */
public class Constants {
    
    public static final String USER = "usuario";
    public static final String USER_ROOT = "usuarioRoot";

    public static final String URL_IMAGEN_FOOTER = "src/recursos/logo_nuevo_original_fondo.png";
    public static final String EMAIL_HTML =  
        "<html>" +
            "<body>" +
                "<p>{?TEXTO}.</p>" +
                "<div style=\"background-color: #fef888; height: 100px; width: 100%;\">" +
                    "<img width=\"100\" src=\"cid:imagen_footer\" style=\"float: left;\">" +
                "</div>" +
            "</body>" +
        "</html>";
}
