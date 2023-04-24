package modelo;

public enum HoraClase {
	
	//Declaración de las constantes de tipo HoraClase con sus valores asociados
	HORA_8(8, 0),
    HORA_8_MEDIA(8, 30),
    HORA_9(9, 0),
    HORA_9_MEDIA(9, 30),
	HORA_10(10, 0),
	HORA_10_MEDIA(10, 30),
	HORA_11(11, 0),
	HORA_11_MEDIA(11, 30),
	HORA_12(12, 0),
	HORA_12_MEDIA(12, 30),
	HORA_13(13, 0),
	HORA_16(16, 0),
	HORA_16_MEDIA(16, 30),
	HORA_17(17, 0),
	HORA_17_MEDIA(17, 30),
	HORA_18(18, 0),
	HORA_18_MEDIA(18, 30),
	HORA_19(19, 0),
	HORA_19_MEDIA(19, 30),
	HORA_20(20, 0),
	HORA_20_MEDIA(20, 30),
	HORA_21(21, 0),
	HORA_21_MEDIA(21, 30),
	HORA_22(22, 0);
	
    
    // Campos del enumerado
    private int hora;
    private int minutos;
    
    // Constructor del enumerado
    private HoraClase(int hora, int minutos) {
        this.hora = hora;
        this.minutos = minutos;
    }
    
    // Métodos getter para obtener la hora y minutos
    public int getHora() {
        return hora;
    }
    
    public int getMinutos() {
        return minutos;
    }

    /**
     * Obtiene un objeto HoraClase en función de la hora y los minutos.
     *
     * @param hora    la hora de la clase
     * @param minutos los minutos de la clase
     * @return un objeto HoraClase correspondiente a la hora y los minutos
     * @throws IllegalArgumentException si la hora o los minutos no están dentro de
     *                                  los valores permitidos
     */
    public static HoraClase getHoraClase(int hora, int minutos) throws IllegalArgumentException {
        // Validar que la hora esté dentro de los límites permitidos
        if (hora < 8 || hora > 22 || (hora == 22 && minutos != 0)) {
            throw new IllegalArgumentException("Hora no válida para una clase");
        }
        // Validar que los minutos estén dentro de los límites permitidos
        if (minutos < 0 || minutos > 30 || (minutos == 30 && hora == 22)) {
            throw new IllegalArgumentException("Minutos no válidos para una clase");
        }
        // Iterar sobre los valores del enumerado HoraClase
        for (HoraClase horaClase : HoraClase.values()) {
            // Si la hora y los minutos coinciden con los valores de una constante
            // HoraClase, devolver esa constante
            if (horaClase.getHora() == hora && horaClase.getMinutos() == minutos) {
                return horaClase;
            }
        }
        // Si no se encontró una constante correspondiente, lanzar una excepción
        throw new IllegalArgumentException("Hora y minutos no corresponden a una hora de clase");
    }
    
    // Método toString para representar la hora en formato de texto
    @Override
    public String toString() {
        return String.format("%d:%02d", hora, minutos);
    }
	
}
