package utilidades;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase utilitaria para operaciones relacionadas con fechas.
 * 
 * @author David Jimenez Alonso.
 */
public class Fechas {

    /**
     * Convierte el nombre del mes en castellano al nombre en inglés.
     *
     * @param mesEnCastellano El nombre del mes en castellano.
     * @return El nombre del mes en inglés, o null si no se encuentra una correspondencia.
     */
    public static String traducirMesAIngles(String mesEnCastellano) {
        // Mapeo de los meses en castellano a los meses en inglés
        Map<String, String> monthMap = new HashMap<>();
        monthMap.put("enero", "JANUARY");
        monthMap.put("febrero", "FEBRUARY");
        monthMap.put("marzo", "MARCH");
        monthMap.put("abril", "APRIL");
        monthMap.put("mayo", "MAY");
        monthMap.put("junio", "JUNE");
        monthMap.put("julio", "JULY");
        monthMap.put("agosto", "AUGUST");
        monthMap.put("septiembre", "SEPTEMBER");
        monthMap.put("octubre", "OCTOBER");
        monthMap.put("noviembre", "NOVEMBER");
        monthMap.put("diciembre", "DECEMBER");

        // Convertir el nombre del mes a minúsculas
        String lowercaseMonthName = mesEnCastellano.toLowerCase();

        // Verificar si el nombre del mes es válido
        if (monthMap.containsKey(lowercaseMonthName)) {
            // Obtener el nombre en inglés en mayúsculas del mes correspondiente
            return monthMap.get(lowercaseMonthName);
        } else {
            return null; // El mes no es válido, se devuelve null
        }
    }

    /**
     * Método estático que devuelve un HashMap con claves del 1 al 12 y valores con los nombres de los meses del año.
     *
     * @return HashMap con los meses del año.
     */
    public static Map<Integer, String> obtenerMesesDelAnio() {
        Map<Integer, String> meses = new HashMap<>();
        meses.put(1, "Enero");
        meses.put(2, "Febrero");
        meses.put(3, "Marzo");
        meses.put(4, "Abril");
        meses.put(5, "Mayo");
        meses.put(6, "Junio");
        meses.put(7, "Julio");
        meses.put(8, "Agosto");
        meses.put(9, "Septiembre");
        meses.put(10, "Octubre");
        meses.put(11, "Noviembre");
        meses.put(12, "Diciembre");
        return meses;
    }

    
    /**
     * Obtiene el nombre del mes correspondiente a una posición dada.
     *
     * @param posicionMes La posición del mes (valor entre 1 y 12).
     * @return El nombre del mes correspondiente, o null si no se encuentra.
     */
    public static String obtenerNombreMes(int posicionMes) {
        // Crear un mapa para almacenar los meses y sus posiciones correspondientes.
        Map<Integer, String> meses = new HashMap<>();
        meses.put(1, "Enero");
        meses.put(2, "Febrero");
        meses.put(3, "Marzo");
        meses.put(4, "Abril");
        meses.put(5, "Mayo");
        meses.put(6, "Junio");
        meses.put(7, "Julio");
        meses.put(8, "Agosto");
        meses.put(9, "Septiembre");
        meses.put(10, "Octubre");
        meses.put(11, "Noviembre");
        meses.put(12, "Diciembre");
        
        // Verificar si el mapa contiene la posición del mes especificada.
        if (meses.containsKey(posicionMes)) {
            return meses.get(posicionMes); // Devolver el nombre del mes correspondiente a la posición.
        } else {
            return null; // No se encontró el mes correspondiente a la posición, devolver null.
        }
    }


    /**
     * Devuelve el nombre del día de la semana correspondiente a una fecha dada.
     * 
     * @param dia Objeto de tipo LocalDate de donde se obtiene la fecha.
     * @return Un String con el dia de la semana. en español.
     */
	public static String obtenerDiaSemana(LocalDate dia) {
		String nombreDia = "";
		DayOfWeek diaDeLaSemana = dia.getDayOfWeek();
        
		switch (diaDeLaSemana.name()) {
		case "MONDAY" -> nombreDia = "Lunes";
		case "TUESDAY" -> nombreDia = "Martes";
		case "WEDNESDAY" -> nombreDia = "Miércoles";
		case "THURSDAY" -> nombreDia = "Jueves";
		case "FRIDAY" -> nombreDia = "Viernes";
		case "SATURDAY" -> nombreDia = "Sábado";
		case "SUNDAY" -> nombreDia = "Domingo";
		}
		
		return nombreDia; //devuelve el nombre del día de la semana es español.
	}
}
