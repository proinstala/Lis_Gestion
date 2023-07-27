/**
 * 
 */
/**
 * @author David
 *
 */
module Lis_Gestion {
	
	exports principal;
	exports controlador;
	exports modelo;
	exports baseDatos;
	exports utilidades;
	exports colecciones;
	
	opens controlador;
	requires javafx.fxml;
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.base;
	requires java.desktop;
	requires java.sql;
	requires java.mail;
	requires activation;
	requires jasperreports;
}