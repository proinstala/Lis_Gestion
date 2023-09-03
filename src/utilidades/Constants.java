package utilidades;

/**
 * Clase que contiene constantes utilizadas en la aplicación.
 * 
 * @author David Jimenez Alonso.
 */
public class Constants {
    
    public static final String USER = "usuario";
    public static final String USER_ROOT = "usuarioRoot";

    public static final String FOLDER_REPORTS = "reports";
    public static final String FOLDER_LOG = "log";


    public static final String EMAIL_APP = "Aplicación";
    public static final String EMAIL_USER = "Personal";
    public static final String EMAIL_OTHER = "Otro";

    public static final String URL_IMAGEN_FOOTER = "recursos/logo_nuevo_original_fondo.png"; //ruta desde fuera del jar.
    public static final String EMAIL_HTML =  
        "<html>" +
            "<body>" +
                "<p>{?TEXTO}.</p>" +
                Constants.DATOS_CENTRO_EMAIL_HTML +
                Constants.FOOTER_EMAIL_HTML +
            "</body>" +
        "</html>";

    public static final String DATOS_CENTRO_EMAIL_HTML = 
        "<div style=\"margin-top: 5em;\">" +
            "<hr style=\"border: none; height: 1px; background-color: #80488F;\">" +
            "<p>LIS Centro de Pilates.<br>Calle los Oliveros, 17<br>30580, Alquerías, Murcia.</p>" +
            "<br>" +
            "<p>Siguenos en:</p> " + 
            "<p><a href=\"https://m.facebook.com/p/Lis-Centro-de-Pilates-100046519753520/\">Facebook</a></p>" +
            "<p><a href=\"https://www.instagram.com/lis_pilates/?hl=es\">Instagram</a></p>" +
        "</div>"; 

    public static final String FOOTER_EMAIL_HTML = 
        "<div style=\"background-color: #fef888; height: 100px; width: 100%;\">" +
            "<img width=\"100\" src=\"cid:imagen_footer\" style=\"float: left;\">" +
        "</div>";

}
