package Estructuras;

import java.awt.Color;

public class Arista {
	
	public boolean equals(Object a){
		return (origen.getNombre() == ((Arista) a).getOrigen().getNombre() && destino.getNombre() == ((Arista) a).getDestino().getNombre()) ||
				(destino.getNombre() == ((Arista) a).getOrigen().getNombre() && origen.getNombre() == ((Arista) a).getDestino().getNombre());
		
	}
	
	Vertice origen;
	Vertice destino;
	Color color;

	int valorArco;
	
	// CONSTRUCTORA
	// ***************************************
	public Arista(){
		origen = null;
		destino = null;
		color = new Color(50,100,120);
		valorArco = 0;
	}
	
	public Arista(Vertice a,Vertice b,int valor){
		origen = a;
		destino = b;
		color = new Color(50,100,120);
		valorArco = valor;
	}
	
	
	// MODIFICADORAS
	// ****************************************

	public void setOrigen(Vertice v) {
		origen = v;
	}
	
	public void setDestino(Vertice v){
		destino = v;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public void setValorArco(int valorArco) {
		this.valorArco = valorArco;
	}
	
	// CONSULTORAS
	// ****************************************
	
	public Color getColor() {
		return color;
	}
	
	public Vertice getOrigen(){
		return origen;
	}
	
	public int getOrigenX() {
		return origen.getCoordX();
	}
	
	public int getOrigenY() {
		return origen.getCoordY();
	}

	public String getNombreOrigen(){
		return origen.getNombre();
	}
	
	public Vertice getDestino(){
		return destino;
	}
	
	public int getDestinoX() {
		return destino.getCoordX();
	}
	
	public int getDestinoY() {
		return destino.getCoordY();
	}
	
	public String getNombreDestino(){
		return destino.getNombre();
	}

	public int getValorArco() {
		return valorArco;
	}

	// METODOS
	// *****************************************
	
	public boolean igualArista(Arista a){
		return (((origen.getCoordX() == a.getOrigenX()) &&		// O coincide con una arista existente
				(origen.getCoordY() == a.getOrigenY()) &&
				(destino.getCoordX() == a.getDestinoX()) &&
				(destino.getCoordY() == a.getDestinoY())) ||
				
				((a.getOrigenX() == a.getDestinoX()) &&			// O el origen y el destino coinciden
				(a.getOrigenY() == a.getDestinoY())));
	}
	
	public boolean contieneAlVertice(Vertice v){
		return (origen.igualNombre(v) || destino.igualNombre(v));
	}
}
