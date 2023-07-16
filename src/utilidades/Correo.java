package utilidades;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


/**
 * Clase que proporciona métodos para enviar correos electrónicos a través del protocolo SMTP utilizando una cuenta de Gmail como emisor.
 * Los métodos permiten enviar correos electrónicos con texto plano, texto HTML, archivos adjuntos y contenido multipartes.
 * Además, se incluye la posibilidad de enviar copias ocultas a otros destinatarios.
 */
public class Correo {

    private static CorreoCallback callback; // Variable para almacenar el objeto de la interfaz funcional


	/**
     * Envía un correo electrónico a través del protocolo SMTP utilizando la cuenta de Gmail del emisor.
     * 
     * @param destinatario Dirección de correo de destinatario del email.
     * @param asunto Asunto del email.
     * @param cuerpo Cuerpo del email.
     * @param emitente Dirección de correo electrónico del emisor (cuenta de Gmail).
     * @param password Password de la cuenta de Gmail del emisor.
     * @return true si el correo se envía correctamente;, false en caso contrario.
     * @throws MessagingException 
     */
    public static boolean enviarCorreo(String destinatario, String asunto, String cuerpo, String emitente, String password) throws MessagingException {
        //Configurar propiedades del servidor de correo electronico.
        Properties props = new Properties();            //Propiedades para construir la sesión con el servidor.
        props.put("mail.smtp.host", "smtp.gmail.com");  //Servidor SMTP.
        props.put("mail.smtp.auth", "true");            //Identificación requerida.
        
        //Para transmisión segura a través de TLS
        props.put("mail.smtp.starttls.enable", "true"); //Para conectar de manera segura al servidor SMTP.
        props.put("mail.smtp.port", "587");             //El puerto SMTP seguro de Google.
        
        //Crea una sesión con autenticación con el usuario, la contraseña y las propiedades especificadas.
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
              @Override
              protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emitente, password);
              }
        });

        //Crear el mensaje
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emitente));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario)); //Destinatario de correco.
        message.setSubject(asunto);   //Asunto del mensaje.
        message.setText(cuerpo);      //Cuerpo del mensaje.

        Transport.send(message); //Envía el mensaje, realizando conexión, transmisión y desconexión.

        return true;
    }//Fin enviarCorreo.


    /**
     * Envía un correo electrónico a través del protocolo SMTP utilizando la cuenta de Gmail del emisor,
     * ademas envia una copia oculta al correo destinatarioCopia.
     * 
     * @param destinatario Dirección de correo de destinatario del email.
     * @param detinatarioCopia Dirección de correo electrónico del destinatario con copia oculta (BCC).
     * @param asunto Asunto del email.
     * @param cuerpo Cuerpo del email.
     * @param emitente Dirección de correo electrónico del emisor (cuenta de Gmail).
     * @param password Password de la cuenta de Gmail del emisor.
     * @return true si el correo se envía correctamente;, false en caso contrario.
     * @throws MessagingException 
     */
    public static boolean enviarCorreo(String destinatario, String detinatarioCopia, String asunto, String cuerpo, String emitente, String password) throws MessagingException {
        //Configurar propiedades del servidor de correo electronico.
        Properties props = new Properties();            //Propiedades para construir la sesión con el servidor.
        props.put("mail.smtp.host", "smtp.gmail.com");  //Servidor SMTP.
        props.put("mail.smtp.auth", "true");            //Identificación requerida.
        
        //Para transmisión segura a través de TLS
        props.put("mail.smtp.starttls.enable", "true"); //Para conectar de manera segura al servidor SMTP.
        props.put("mail.smtp.port", "587");             //El puerto SMTP seguro de Google.
        
        //Crea una sesión con autenticación con el usuario, la contraseña y las propiedades especificadas.
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
              @Override
              protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emitente, password);
              }
        });

        //Crear el mensaje
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emitente));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));     //Destinatario de correco.
        message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(detinatarioCopia)); //Destinatario con copia oculta.
        message.setSubject(asunto);   //Asunto del mensaje.
        message.setText(cuerpo);      //Cuerpo del mensaje.

        Transport.send(message); //Envía el mensaje, realizando conexión, transmisión y desconexión.

        return true;
    }//Fin enviarCorreo.


    /**
     * Envía un correo electrónico con un archivo adjunto a través del protocolo SMTP utilizando la cuenta de Gmail del emisor.
     * 
     * @param email Dirección de correo de destinatario del email.
     * @param asunto Asunto del email.
     * @param cuerpo Cuerpo del email.
     * @param emitente Dirección de correo electrónico del emisor (cuenta de Gmail).
     * @param password Password de la cuenta de Gmail del emisor.
     * @param ruta ruta del archivo que se va adjuntar al correo.
     * @return true si el correo se envía correctamente;, false en caso contrario.
     * @throws MessagingException 
     */
    public static boolean enviarCorreoArchivoAdjunto(String destinatario, String asunto, String cuerpo, String emitente, String password, String ruta) throws MessagingException {
        //Configurar propiedades del servidor de correo electronico.
        Properties props = new Properties();            //Propiedades para construir la sesión con el servidor.
        props.put("mail.smtp.host", "smtp.gmail.com");  //Servidor SMTP.
        props.put("mail.smtp.auth", "true");            //Identificación requerida.

        //Para transmisión segura a través de TLS
        props.put("mail.smtp.starttls.enable", "true"); //Para conectar de manera segura al servidor SMTP.
        props.put("mail.smtp.port", "587");             //El puerto SMTP seguro de Google.

        //Crea una sesión con autenticación con el usuario, la contraseña y las propiedades especificadas.
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emitente, password);
            }
        });

        //Crear el mensaje
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emitente));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario)); //Destinatario de correco.
        message.setSubject(asunto);   //Asunto del mensaje.

        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(cuerpo); //Cuerpo del mensaje.

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // Agregar el archivo adjunto al mensaje
        messageBodyPart = new MimeBodyPart();
        String filename = ruta; //ruta del archivo adjunto.
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);
        multipart.addBodyPart(messageBodyPart);

        message.setContent(multipart);

        Transport.send(message); //Envía el mensaje, realizando conexión, transmisión y desconexión.

        return true;
    }//Fin enviarCorreoArchivoAdjunto.


    /**
     * Envía un correo electrónico con contenido multiparte, incluyendo texto HTML y una imagen adjunta,
     * tambien adapta el texto a formato html.
     *
     * @param destinatario Dirección de correo electrónico del destinatario.
     * @param asunto Asunto del mensaje.
     * @param texto Texto del mensaje en formato de texto plano.
     * @param codigoHtml Código HTML con la imagen adjunta en el pie de página (debe contener la etiqueta "{?TEXTO}" para insertar el texto y la referencia de imagen "cid:imagen_footer").
     * @param rutaImagen Ruta de la imagen a adjuntar en el pie de página del mensaje.
     * @param emitente Dirección de correo electrónico del remitente.
     * @param password Password del remitente para la autenticación del servidor SMTP.
     * @return true si el correo se envió correctamente, de lo contrario, false.
     * @throws MessagingException Si ocurre un error al enviar el correo.
     */
    public static boolean enviarCorreoMultiparte(String destinatario, String asunto, String texto, String codigoHtml, String rutaImagen, String emitente, String password) throws MessagingException {
        //Configurar propiedades del servidor de correo electronico.
        Properties props = new Properties();            //Propiedades para construir la sesión con el servidor.
        props.put("mail.smtp.host", "smtp.gmail.com");  //Servidor SMTP.
        props.put("mail.smtp.auth", "true");            //Identificación requerida.

        //Para transmisión segura a través de TLS
        props.put("mail.smtp.starttls.enable", "true"); //Para conectar de manera segura al servidor SMTP.
        props.put("mail.smtp.port", "587");             //El puerto SMTP seguro de Google.

        //Crea una sesión con autenticación con el usuario, la contraseña y las propiedades especificadas.
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emitente, password);
            }
        });

        //Adaptar el texto y inserta el texto en el código HTML.
        String mensajeAdaptadoHtml = adaptarTexto(texto);
        String cadenaHtml = codigoHtml.replace("{?TEXTO}", mensajeAdaptadoHtml);

        //Crear el mensaje
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emitente));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario)); //Destinatario de correco.
        message.setSubject(asunto);   //Asunto del mensaje.

        Multipart multipart = new MimeMultipart();

        //Crea la parte de texto del mensaje.
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(cadenaHtml, "text/html");

        //Creación de la parte del pie de página con la imagen adjunta.
        MimeBodyPart footerPart = new MimeBodyPart();
        DataSource fds = new FileDataSource(rutaImagen);
        footerPart.setDataHandler(new DataHandler(fds));
        footerPart.setHeader("Content-ID", "<imagen_footer>");

        //Agregar partes al multipart.
        multipart.addBodyPart(messageBodyPart);
        multipart.addBodyPart(footerPart);

        message.setContent(multipart); //Establecer el contenido multipart en el mensaje.

        Transport.send(message); //Envía el mensaje, realizando conexión, transmisión y desconexión.

        return true;
    }//Fin enviarCorreoMultiparte.


    /**
     * Envía un correo electrónico con contenido multiparte, incluyendo texto HTML y una imagen adjunta,
     * tambien adapta el texto a formato html y una copia oculta a la direccion detinatarioCopia.
     *
     * @param destinatario Dirección de correo electrónico del destinatario.
     * @param detinatarioCopia Dirección de correo electrónico del destinatario con copia oculta (BCC).
     * @param asunto Asunto del mensaje.
     * @param texto Texto del mensaje en formato de texto plano.
     * @param codigoHtml Código HTML con la imagen adjunta en el pie de página (debe contener la etiqueta "{?TEXTO}" para insertar el texto y la referencia de imagen "cid:imagen_footer").
     * @param rutaImagen Ruta de la imagen a adjuntar en el pie de página del mensaje.
     * @param emitente Dirección de correo electrónico del remitente.
     * @param password Password del remitente para la autenticación del servidor SMTP.
     * @return true si el correo se envió correctamente, de lo contrario, false.
     * @throws MessagingException Si ocurre un error al enviar el correo.
     */
    public static boolean enviarCorreoMultiparte(String destinatario, String detinatarioCopia, String asunto, String texto, String codigoHtml, String rutaImagen, String emitente, String password) throws MessagingException {
        //Configurar propiedades del servidor de correo electronico.
        Properties props = new Properties();            //Propiedades para construir la sesión con el servidor.
        props.put("mail.smtp.host", "smtp.gmail.com");  //Servidor SMTP.
        props.put("mail.smtp.auth", "true");            //Identificación requerida.

        //Para transmisión segura a través de TLS
        props.put("mail.smtp.starttls.enable", "true"); //Para conectar de manera segura al servidor SMTP.
        props.put("mail.smtp.port", "587");             //El puerto SMTP seguro de Google.

        //Crea una sesión con autenticación con el usuario, la contraseña y las propiedades especificadas.
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emitente, password);
            }
        });

        //Adaptar el texto y inserta el texto en el código HTML.
        String mensajeAdaptadoHtml = adaptarTexto(texto);
        String contenidoEmail = codigoHtml.replace("{?TEXTO}", mensajeAdaptadoHtml);

        //Crear el mensaje
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emitente));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));     //Destinatario de correco.
        message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(detinatarioCopia)); //Destinatario con copia oculta.
        message.setSubject(asunto);   //Asunto del mensaje.

        Multipart multipart = new MimeMultipart();

        //Crea la parte de texto del mensaje.
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(contenidoEmail, "text/html");
        
        //Creación de la parte del pie de página con la imagen adjunta
        MimeBodyPart footerPart = new MimeBodyPart();
        DataSource fds = new FileDataSource(rutaImagen);
        footerPart.setDataHandler(new DataHandler(fds));
        footerPart.setHeader("Content-ID", "<imagen_footer>");

        //Agregar partes al multipart.
        multipart.addBodyPart(messageBodyPart);
        multipart.addBodyPart(footerPart);

        message.setContent(multipart); //Establecer el contenido multipart en el mensaje.

        Transport.send(message); //Envía el mensaje, realizando conexión, transmisión y desconexión.

        return true;
    }//Fin enviarCorreoMultiparte.


    /**
     * Envía un correo electrónico a través del protocolo SMTP utilizando la cuenta de Gmail del emisor.
     * El envío del correo se realiza en un hilo secundario para evitar bloquear la interfaz de usuario.
     *
     * @param destinatario Dirección de correo del destinatario del email.
     * @param asunto Asunto del email.
     * @param texto Texto del email que se incluirá en el cuerpo del mensaje (opcional, puede ser null).
     * @param codigoHtml Código HTML que se utilizará como plantilla del cuerpo del mensaje.
     * @param rutaImagen Ruta de la imagen que se incluirá en el pie de página del mensaje.
     * @param emitente Dirección de correo electrónico del emisor (cuenta de Gmail).
     * @param password Password de la cuenta de Gmail del emisor.
     * @param correoCallback Objeto que implementa la interfaz CorreoCallback para recibir notificaciones del resultado del envío del correo (opcional, puede ser null).
     */
    public static void enviarCorreoMultiparte(String destinatario, String asunto, String texto, String codigoHtml, String rutaImagen, String emitente, String password, CorreoCallback correoCallback) {
        callback = correoCallback;
        
        //Crear y ejecutar el hilo secundario para enviar el correo.
        Thread enviarCorreoThread = new Thread(() -> {
            try {
                //Configurar propiedades del servidor de correo electronico.
                Properties props = new Properties();            //Propiedades para construir la sesión con el servidor.
                props.put("mail.smtp.host", "smtp.gmail.com");  //Servidor SMTP.
                props.put("mail.smtp.auth", "true");            //Identificación requerida.

                //Para transmisión segura a través de TLS
                props.put("mail.smtp.starttls.enable", "true"); //Para conectar de manera segura al servidor SMTP.
                props.put("mail.smtp.port", "587");             //El puerto SMTP seguro de Google.

                //Crea una sesión con autenticación con el usuario, la contraseña y las propiedades especificadas.
                Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emitente, password);
                    }
                });

                //Adaptar el texto y inserta el texto en el código HTML.
                String mensajeAdaptadoHtml = adaptarTexto(texto);
                String cadenaHtml = codigoHtml.replace("{?TEXTO}", mensajeAdaptadoHtml);

                //Crear el mensaje
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(emitente));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario)); //Destinatario de correo.
                message.setSubject(asunto);   //Asunto del mensaje.

                Multipart multipart = new MimeMultipart();

                //Crea la parte de texto del mensaje.
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setContent(cadenaHtml, "text/html");

                //Creación de la parte del pie de página con la imagen adjunta.
                MimeBodyPart footerPart = new MimeBodyPart();
                DataSource fds = new FileDataSource(rutaImagen);
                footerPart.setDataHandler(new DataHandler(fds));
                footerPart.setHeader("Content-ID", "<imagen_footer>");

                //Agregar partes al multipart.
                multipart.addBodyPart(messageBodyPart);
                multipart.addBodyPart(footerPart);

                message.setContent(multipart); //Establecer el contenido multipart en el mensaje.

                Transport.send(message); //Envía el mensaje, realizando conexión, transmisión y desconexión.

                //Notificar que el correo se envió correctamente.
                if (callback != null) {
                    callback.onCorreoEnviado(true);
                }
            } catch (MessagingException e) {
                e.printStackTrace();

                //Notificar que el envío del correo falló.
                if (callback != null) {
                    callback.onCorreoEnviado(false);
                }
            } catch (Exception e) {
                e.printStackTrace();

                //Notificar que el envío del correo falló.
                if (callback != null) {
                    callback.onCorreoEnviado(false);
                }
            }
        });

        enviarCorreoThread.start();
    }//Fin enviarCorreoMultiparte.


    /**
     * Envía un correo electrónico a través del protocolo SMTP utilizando la cuenta de Gmail del emisor.
     * El envío del correo se realiza en un hilo secundario para evitar bloquear la interfaz de usuario.
     * Envia una copia oculta a la direccion de correo destinaratioCopia.
     *
     * @param destinatario      Dirección de correo del destinatario del email.
     * @param detinatarioCopia  Dirección de correo electrónico del destinatario con copia oculta (BCC).
     * @param asunto            Asunto del email.
     * @param texto             Texto del email que se incluirá en el cuerpo del mensaje (opcional, puede ser null).
     * @param codigoHtml        Código HTML que se utilizará como plantilla del cuerpo del mensaje.
     * @param rutaImagen        Ruta de la imagen que se incluirá en el pie de página del mensaje.
     * @param emitente          Dirección de correo electrónico del emisor (cuenta de Gmail).
     * @param password          Password de la cuenta de Gmail del emisor.
     * @param correoCallback    Objeto que implementa la interfaz CorreoCallback para recibir notificaciones del resultado del envío del correo (opcional, puede ser null).
     */
    public static void enviarCorreoMultiparte(String destinatario, String detinatarioCopia,String asunto, String texto, String codigoHtml, String rutaImagen, String emitente, String password, CorreoCallback correoCallback) {
        callback = correoCallback;
        
        //Crear y ejecutar el hilo secundario para enviar el correo.
        Thread enviarCorreoThread = new Thread(() -> {
            try {
                //Configurar propiedades del servidor de correo electronico.
                Properties props = new Properties();            //Propiedades para construir la sesión con el servidor.
                props.put("mail.smtp.host", "smtp.gmail.com");  //Servidor SMTP.
                props.put("mail.smtp.auth", "true");            //Identificación requerida.

                //Para transmisión segura a través de TLS
                props.put("mail.smtp.starttls.enable", "true"); //Para conectar de manera segura al servidor SMTP.
                props.put("mail.smtp.port", "587");             //El puerto SMTP seguro de Google.

                //Crea una sesión con autenticación con el usuario, la contraseña y las propiedades especificadas.
                Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emitente, password);
                    }
                });

                //Adaptar el texto y inserta el texto en el código HTML.
                String mensajeAdaptadoHtml = adaptarTexto(texto);
                String cadenaHtml = codigoHtml.replace("{?TEXTO}", mensajeAdaptadoHtml);

                //Crear el mensaje
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(emitente));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario)); //Destinatario de correo.
                message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(detinatarioCopia)); //Destinatario con copia oculta.
                message.setSubject(asunto);   //Asunto del mensaje.

                Multipart multipart = new MimeMultipart();

                //Crea la parte de texto del mensaje.
                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setContent(cadenaHtml, "text/html");

                //Creación de la parte del pie de página con la imagen adjunta.
                MimeBodyPart footerPart = new MimeBodyPart();
                DataSource fds = new FileDataSource(rutaImagen);
                footerPart.setDataHandler(new DataHandler(fds));
                footerPart.setHeader("Content-ID", "<imagen_footer>");

                //Agregar partes al multipart.
                multipart.addBodyPart(messageBodyPart);
                multipart.addBodyPart(footerPart);

                message.setContent(multipart); //Establecer el contenido multipart en el mensaje.

                Transport.send(message); //Envía el mensaje, realizando conexión, transmisión y desconexión.

                //Notificar que el correo se envió correctamente.
                if (callback != null) {
                    callback.onCorreoEnviado(true);
                }
            } catch (MessagingException e) {
                e.printStackTrace();

                //Notificar que el envío del correo falló.
                if (callback != null) {
                    callback.onCorreoEnviado(false);
                }
            } catch (Exception e) {
                e.printStackTrace();

                //Notificar que el envío del correo falló.
                if (callback != null) {
                    callback.onCorreoEnviado(false);
                }
            }
        });

        enviarCorreoThread.start();
    }//Fin enviarCorreoMultiparte.


    /**
     * Adapta un texto para reemplazar caracteres especiales y secuencias específicas con sus 
     * correspondientes secuencias de escape HTML.
     *
     * @param texto Texto a adaptar.
     * @return Texto adaptado con caracteres especiales reemplazados por secuencias de escape HTML.
     */
    public static String adaptarTexto(String texto) {
        // Mapa de sustituciones
        Map<Character, String> reemplazos = new HashMap<>();
        reemplazos.put('á', "&aacute;");
        reemplazos.put('é', "&eacute;");
        reemplazos.put('í', "&iacute;");
        reemplazos.put('ó', "&oacute;");
        reemplazos.put('ú', "&uacute;");
        reemplazos.put('ñ', "&ntilde;");
        reemplazos.put('Ñ', "&Ntilde;");
        reemplazos.put('¿', "&iquest;");
        reemplazos.put('¡', "&iexcl;");
        reemplazos.put('ç', "&ccedil;");
        reemplazos.put('Ç', "&Ccedil;");
        reemplazos.put('ü', "&uuml;");
        reemplazos.put('Ü', "&Uuml;");
        reemplazos.put('â', "&acirc;");
        reemplazos.put('Â', "&Acirc;");
        reemplazos.put('ê', "&ecirc;");
        reemplazos.put('Ê', "&Ecirc;");
        reemplazos.put('î', "&icirc;");
        reemplazos.put('Î', "&Icirc;");
        reemplazos.put('ô', "&ocirc;");
        reemplazos.put('Ô', "&Ocirc;");
        reemplazos.put('û', "&ucirc;");
        reemplazos.put('Û', "&Ucirc;");
        reemplazos.put('à', "&agrave;");
        reemplazos.put('À', "&Agrave;");
        reemplazos.put('è', "&egrave;");
        reemplazos.put('È', "&Egrave;");
        reemplazos.put('ì', "&igrave;");
        reemplazos.put('Ì', "&Igrave;");
        reemplazos.put('ò', "&ograve;");
        reemplazos.put('Ò', "&Ograve;");
        reemplazos.put('ù', "&ugrave;");
        reemplazos.put('Ù', "&Ugrave;");
        reemplazos.put('ä', "&auml;");
        reemplazos.put('Ä', "&Auml;");
        reemplazos.put('ë', "&euml;");
        reemplazos.put('Ë', "&Euml;");
        reemplazos.put('ï', "&iuml;");
        reemplazos.put('Ï', "&Iuml;");
        reemplazos.put('ö', "&ouml;");
        reemplazos.put('Ö', "&Ouml;");
        reemplazos.put('ÿ', "&yuml;");
        reemplazos.put('þ', "&thorn;");
        reemplazos.put('Þ', "&THORN;");
        reemplazos.put('ß', "&szlig;");
        reemplazos.put('«', "&laquo;");
        reemplazos.put('»', "&raquo;");
        reemplazos.put('©', "&copy;");
        reemplazos.put('®', "&reg;");
        reemplazos.put('™', "&trade;");
        reemplazos.put('“', "&ldquo;");
        reemplazos.put('”', "&rdquo;");
        reemplazos.put('‘', "&lsquo;");
        reemplazos.put('’', "&rsquo;");
        reemplazos.put('–', "&ndash;");
        reemplazos.put('—', "&mdash;");
        reemplazos.put('•', "&bull;");
        reemplazos.put('…', "&hellip;");
        reemplazos.put('°', "&deg;");
        reemplazos.put('±', "&plusmn;");
        reemplazos.put('×', "&times;");
        reemplazos.put('÷', "&divide;");
        reemplazos.put('\n', "<br>"); // Salto de línea

        // Realizar las sustituciones
        StringBuilder resultado = new StringBuilder();
        for (char caracter : texto.toCharArray()) {
            String sustituto = reemplazos.getOrDefault(caracter, String.valueOf(caracter));
            resultado.append(sustituto);
        }

        return resultado.toString();
    }
}
