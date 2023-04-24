package modelo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Datos {
	
	private static final Datos INSTANCE = new Datos(); //Singleton
	private Jornada jornada1;
	private ArrayList<Jornada> listaJornadas;
	private ArrayList<Alumno> listaAlumnos;
	
	private Datos() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		//LocalDate date1 = LocalDate.parse("08/04/2023", formatter);
		LocalDate date1 = LocalDate.of(2023,4,8);
		jornada1 = new Jornada(date1, "esta todo bien");
		
		Clase c1 = new Clase(1, TipoClase.AEREO, HoraClase.HORA_8, "Clase divertida");
		Clase c2 = new Clase(2, TipoClase.AEREO, HoraClase.HORA_10_MEDIA, "hay que colgar los columpios");
		Clase c3 = new Clase(3, TipoClase.HIIT_HIPO, HoraClase.HORA_11_MEDIA, "Barrer el suelo");
		Clase c4 = new Clase(4, TipoClase.PILATES, HoraClase.HORA_12_MEDIA, "gente joven");
		Clase c5 = new Clase(5, TipoClase.PILATES, HoraClase.HORA_16_MEDIA, "");
		Clase c6 = new Clase(6, TipoClase.OTROS, HoraClase.HORA_18, "Clase exigente");
		Clase c7 = new Clase(7, TipoClase.PILATES, HoraClase.HORA_19_MEDIA, "Nada");
		Clase c8 = new Clase(8, TipoClase.PILATES, HoraClase.HORA_20_MEDIA, "fin de clases");
		
		Clase[] clases = {c1, c2, c3, c4, c5, c6, c7, c8};
		jornada1.setClases(clases);
		/*
		jornada1.addClase(c1);
		jornada1.addClase(c2);
		jornada1.addClase(c3);
		jornada1.addClase(c4);
		jornada1.addClase(c5);
		jornada1.addClase(c6);
		jornada1.addClase(c7);
		jornada1.addClase(c8);
		*/
		listaJornadas = new ArrayList<Jornada>();
		listaJornadas.add(jornada1);
		
		Jornada jornada2 = new Jornada(LocalDate.of(2023,4,12), "");
		Clase[] clases2 = new Clase[8];
		clases2[0] = new Clase(1, null, null, "");
		clases2[1] = new Clase(2, null, null, "");
		clases2[2] = new Clase(3, null, null, "");
		clases2[3] = new Clase(4, null, null, "");
		clases2[4] = new Clase(5, null, null, "");
		clases2[5] = new Clase(6, null, null, "");
		clases2[6] = new Clase(7, null, null, "");
		clases2[7] = new Clase(8, null, null, "");
		jornada2.setClases(clases2);
		/*
		jornada2.addClase(new Clase(1, null, null, ""));
		jornada2.addClase(new Clase(2, null, null, ""));
		jornada2.addClase(new Clase(3, null, null, ""));
		jornada2.addClase(new Clase(4, null, null, ""));
		jornada2.addClase(new Clase(5, null, null, ""));
		jornada2.addClase(new Clase(6, null, null, ""));
		jornada2.addClase(new Clase(7, null, null, ""));
		jornada2.addClase(new Clase(8, null, null, ""));
		*/
		listaJornadas.add(jornada2);
		
		Direccion d1 = new Direccion(1, "brazal nuevo", 26, "Santa Cruz", "Murcia", 30162);
		Direccion d2 = new Direccion(2, "cerezo rojo", 26, "El Raal", "Murcia", 30205);
		Direccion d3 = new Direccion(3, "cervantes", 26, "Alquerias", "Murcia", 30580);
		
		LocalDate date2 = LocalDate.parse("05/10/1985", formatter);
		LocalDate date3 = LocalDate.parse("02/11/1995", formatter);
		LocalDate date4 = LocalDate.parse("09/06/1996", formatter);
		LocalDate date5 = LocalDate.parse("22/10/2008", formatter);
		LocalDate date6 = LocalDate.parse("17/04/2003", formatter);
		
		Alumno a1 = new Alumno(1, "Juan", "Lopez", "Sanchez", Genero.HOMBRE, d1, date2, 123450, "Juan@mail.com");
		Alumno a2 = new Alumno(2, "Pablo", "Sanchez", "Sanchez", Genero.HOMBRE, d2, date2, 123451, "pablo@mail.com");
		Alumno a3 = new Alumno(3, "Bea", "Lopez", "Lopez", Genero.MUJER, d3, date2, 123452, "bea@mail.com");
		Alumno a4 = new Alumno(4, "Sara", "Jimenez", "Sanchez", Genero.MUJER, d1, date2, 123453, "sara@mail.com");
		Alumno a5 = new Alumno(5, "Lucia", "Lopez", "Alonso", Genero.MUJER, d2, date2, 123454, "lucia@mail.com");
		Alumno a6 = new Alumno(6, "Maria", "Torralva", "Saez", Genero.MUJER, d3, date3, 123455, "maria@mail.com");
		Alumno a7 = new Alumno(7, "Ana", "Cano", "Hernandez", Genero.HOMBRE, d1, date2, 123456, "ana@mail.com");
		Alumno a8 = new Alumno(8, "Belen", "Castillo", "Fernandez", Genero.HOMBRE, d2, date2, 123457, "belen@mail.com");
		Alumno a9 = new Alumno(9, "Juana", "Lama", "Rana", Genero.MUJER, d3, date3, 123458, "juana@mail.com");
		Alumno a10 = new Alumno(10, "Pepe", "Torete", "Retrete", Genero.HOMBRE, d1, date1, 123459, "pepe@mail.com");

		
		c1.addAlumno(a1);
		c1.addAlumno(a2);
		c1.addAlumno(a3);
		c1.addAlumno(a4);
		
		c2.addAlumno(a5);
		c2.addAlumno(a6);
		
		c4.addAlumno(a7);
		c4.addAlumno(a8);
		c4.addAlumno(a9);
		c4.addAlumno(a10);
		
		
		Alumno a11 = new Alumno(11, "Francisco", "Mengual", "Sax", Genero.HOMBRE, d1, date2, 123456, "fran@mail.com");
		Alumno a12 = new Alumno(12, "Javier", "Perez", "Reverte", Genero.HOMBRE, d2, date2, 123455, "javi@mail.com");
		Alumno a13 = new Alumno(13, "Susana", "Garcia", "Gomez", Genero.MUJER, d3, date2, 123454, "susana@mail.com");
		Alumno a14 = new Alumno(14, "Silvia", "Moreno", "Sanchez", Genero.MUJER, d1, date2, 123453, "silvia@mail.com");
		Alumno a15 = new Alumno(15, "Noelia", "Torres", "Molina", Genero.MUJER, d2, date2, 123452, "noelia@mail.com");
		Alumno a16 = new Alumno(16, "Rosana", "Gil", "Alonso", Genero.MUJER, d2, date2, 123452, "rsana@mail.com");
		Alumno a17 = new Alumno(17, "Laura", "Serrano", "Romero", Genero.HOMBRE, d1, date2, 123456, "laura@mail.com");
		Alumno a18 = new Alumno(18, "Federico", "Romero", "Fernandez", Genero.HOMBRE, d2, date2, 123457, "fede@mail.com");
		Alumno a19 = new Alumno(19, "Pilar", "Ruiz", "Rana", Genero.MUJER, d3, date3, 123458, "pili@mail.com");
		Alumno a20 = new Alumno(20, "Antonio", "Gonzalez", "Lopez", Genero.HOMBRE, d1, date1, 123459, "anton@mail.com");
		
		listaAlumnos = new ArrayList<Alumno>();
		listaAlumnos.add(a1);
		listaAlumnos.add(a2);
		listaAlumnos.add(a3);
		listaAlumnos.add(a4);
		listaAlumnos.add(a5);
		listaAlumnos.add(a6);
		listaAlumnos.add(a7);
		listaAlumnos.add(a8);
		listaAlumnos.add(a9);
		listaAlumnos.add(a10);
		listaAlumnos.add(a11);
		listaAlumnos.add(a12);
		listaAlumnos.add(a13);
		listaAlumnos.add(a14);
		listaAlumnos.add(a15);
		listaAlumnos.add(a16);
		listaAlumnos.add(a17);
		listaAlumnos.add(a18);
		listaAlumnos.add(a19);
		listaAlumnos.add(a20);
		
	}
	
	public static Datos getInstance() { //Singleton
		return INSTANCE;
	}
	
	public ArrayList<Alumno> listarAlumno() {
		return listaAlumnos;
	}
	
	public Jornada getJornada(LocalDate fecha) {
		for(Jornada j : listaJornadas) {
			if(j.getFecha().compareTo(fecha) == 0) {
				return j;
			} 
		}
		return null;
	}

	public void addJornada(Jornada jornada) {
		listaJornadas.add(jornada);
	}
	

}
