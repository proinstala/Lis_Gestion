package utilidades;

/**
 * Proporciona utilidades para manipular cadenas de texto.
 */
public class CadenaUtil {
    

    /**
     * Convierte la primera letra de una cadena en mayúscula y el resto de la cadena en minúsculas.
     * Si la cadena es nula o está vacía, se devuelve la misma cadena sin cambios.
     *
     * @param cadena La cadena de texto a capitalizar.
     * @return La cadena con la primera letra en mayúscula y el resto en minúsculas, o la misma cadena si es nula o vacía.
     */
    public static String capitalize(String cadena) {

        if (cadena == null || cadena.isEmpty()) {
            return cadena;
        }

        // Convertir la cadena completa a minúsculas
        String cadenaMinusculas = cadena.toLowerCase();

        // Obtener la primera letra y convertirla a mayúscula
        char primeraLetra = cadenaMinusculas.charAt(0);
        char primeraLetraMayuscula = Character.toUpperCase(primeraLetra);

        // Concatenar la primera letra en mayúscula con el resto de la cadena y devolver el resultado
        return primeraLetraMayuscula + cadenaMinusculas.substring(1);
        
    }
}
