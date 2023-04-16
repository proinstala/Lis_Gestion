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
    
    // Método toString para representar la hora en formato de texto
    @Override
    public String toString() {
        return String.format("%d:%02d", hora, minutos);
    }
	
}
