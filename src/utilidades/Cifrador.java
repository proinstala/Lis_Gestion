package utilidades;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Clase utilitaria para realizar operaciones de cifrado.
 * 
 * @author David Jimenez Alonso.
 */
public class Cifrador {
    
    /**
     * Genera el hash de una contraseña utilizando el algoritmo MD5 y lo devuelve en formato Base64.
     *
     * @param password La contraseña a cifrar.
     * @return El hash de la contraseña en formato Base64.
     * @throws NoSuchAlgorithmException Si el algoritmo MD5 no está disponible.
     */
    public static String hasPassword(String password) throws NoSuchAlgorithmException {
        Base64.Encoder encoder = Base64.getEncoder();
        String resumenB64;
        
        //Creo una instancia de MessageDigest que utiliza el algoritmo MD5 para generar una clave hash.
        MessageDigest md = MessageDigest.getInstance("MD5"); 
        md.update(password.getBytes()); //Alimenta el algoritmo con los bytes de la cadena password 
        byte[] resumen = md.digest();   //Genera un arreglo de bytes que representa el resumen hash de la clave.
       
        resumenB64 = encoder.encodeToString(resumen); //Codifica el resumen en formato Base64.
        
        return resumenB64;   
    }
}
