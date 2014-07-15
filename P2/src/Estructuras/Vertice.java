package Estructuras;

import java.awt.Color;

public class Vertice {
	
	// ATRIBUTOS
	// ******************************
	
	int coordX;
	int coordY;
	Color color;
	String nombre;
	int idMatriz;
	
	
	// CONSTRUCTORAS
	// ************************************
	public Vertice (){
		coordX = 0;
		coordY = 0;
		color = new Color(11,11,244);
		this.nombre = "NULO";
		idMatriz = 0;
	}
	
	public Vertice(int x,int y,String nombre,int id){
		coordX = x;
		coordY = y;
		color = new Color(11,11,244);
		this.nombre = nombre;
		this.idMatriz = id;
	}
	
	// CONSULTORAS
	// ******************************
	public int getCoordX() {
		return coordX;	
	}
	
	public int getCoordY() {
		return coordY;
	}
	
	public Color getColor() {
		return color;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public int getID(){
		return idMatriz;
	}
	
	
	// MODIFICIADORAS
	// ******************************
	
	public void setCoordX(int coordX) {
		this.coordX = coordX;
	}
	
	public void setCoordY(int coordY) {
		this.coordY = coordY;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	
	// METODOS
	// *******************************
	
	public boolean igualPosicion(Vertice v){
		if ((coordX-25 <= v.getCoordX() && v.getCoordX() <= coordX+25) && 	// 25 = tamaño vertice (20)+5
		    (coordY-25 <= v.getCoordY() && v.getCoordY() <= coordY+25))
			return true;
		else
			return false;
	}
	
	public boolean igualNombre(Vertice v){
		return nombre.equals(v.getNombre());
	}	
	
	public boolean igualID(Vertice v){
		return idMatriz == v.getID();
	}	
	

}
