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
	
	opens controlador;
	requires javafx.fxml;
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.base;
	requires java.desktop;
	requires java.sql;
	requires java.mail;
	requires activation;
}