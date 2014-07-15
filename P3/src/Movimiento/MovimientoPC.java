package Movimiento;

import java.util.Stack;

import Movimiento.TableroJuego.TFicha;

public class MovimientoPC {

	private int valor;				// Valor de la heuristica
	private int filOrigen;
	private int colOrigen;
	private int filDestino;
	private int colDestino;
	private int filAtaque;
	private int colAtaque;
	private TFicha fAtacada;
	private TFicha fActual;
	private Stack<MovimientoPC> pilaMultiAtaque;		// Pila que almacena los multiataques consecutivos
	
	public MovimientoPC(int v,int filO,int colO,int filD,int colD,int filAt,int colAt,TFicha ficha){
		valor = v;
		filOrigen = filO;
		colOrigen = colO;
		filDestino = filD;
		colDestino = colD;
		filAtaque = filAt;
		colAtaque = colAt;
		fActual = ficha;
	}
	
	public MovimientoPC(MovimientoPC m){
		valor = m.SolOptima();
		filOrigen = m.getFilaOrigen();
		colOrigen = m.getColumnaOrigen();
		filDestino = m.getFilaDestino();
		colDestino = m.getColumnaDestino();
		filAtaque = m.getFilaAtaque();
		colAtaque = m.getColumnaAtaque();
		
		fAtacada = m.getFichaAtacada();
		fActual = m.getFichaActual();

	}
	
	int SolOptima(){
		return valor;
	}
	
	void setNuevaSolucion(int v,int filO,int colO,int filD,int colD,int filAt,int colAt,TFicha ficha){
		valor = v;
		filOrigen = filO;
		colOrigen = colO;
		filDestino = filD;
		colDestino = colD;
		filAtaque = filAt;
		colAtaque = colAt;
		fActual = ficha;
	}
	
	// CONSULTORAS
	// *************************************************************************************

	public int getFilaOrigen() {
		return filOrigen;
	}

	public int getColumnaOrigen() {
		return colOrigen;
	}

	public int getFilaDestino() {
		return filDestino;
	}

	public int getColumnaDestino() {
		return colDestino;
	}

	public int getFilaAtaque() {
		return filAtaque;
	}

	public int getColumnaAtaque() {
		return colAtaque;
	}
	
	public TFicha getFichaAtacada() {
		return fAtacada;
	}
	
	public TFicha getFichaActual() {
		return fActual;
	}
	
	// MODIFICADORAS
	// *************************************************************************************

	public void setSolOptima(int sumaH) {
		valor = sumaH;
	}

	public void setFichaAtacada(TFicha ficha) {
		fAtacada = ficha;
	}

	
	// METODOS PARA TRATAR LA PILA DE MULTIATAQUES
	// *************************************************************************************
	
	public void inicializaPilaMultiAtaque(){
		pilaMultiAtaque = new Stack<MovimientoPC>();
	}
	
	public void apilaMultiAtaque(MovimientoPC m){
		pilaMultiAtaque.push(m);
	}
	
	public void desapilaMultiAtaque(){
		pilaMultiAtaque.pop();
	}
	
	public MovimientoPC dameMultiAtaque(){
		return pilaMultiAtaque.peek();
	}
	
	public boolean esPilaMultiAtaqueVacia(){
		return pilaMultiAtaque == null || pilaMultiAtaque.isEmpty();
	}
}
