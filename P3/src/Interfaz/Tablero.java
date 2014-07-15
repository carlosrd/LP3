package Interfaz;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

@SuppressWarnings("serial")
public abstract class Tablero extends Canvas {
	
	final static int tamCasilla = 40;		// Las casillas son de 40x40						
	protected int tamTablero;				// Tamaño del tablero actual

	protected int getInicioCasillas(){		// Determina el tamaño del marco segun el tamaño del tablero
		int inicio = 0;
		switch (tamTablero){
			case 6: inicio = 4*tamCasilla;	// Solo modo depurador!
					break;
			case 8: inicio = 3*tamCasilla;
					break;
			case 10: inicio = 2*tamCasilla;
					 break;
			case 12: inicio = tamCasilla;
					 break;
		}
		return inicio;
	}
	
	public void paint(Graphics g){
		g.setColor(new Color(75,141,221)); 				// Color de fondo en azul (los marcos)
		g.fillRect(0,0,580,getInicioCasillas());		// Pintar rectangulo(marco arriba) (x,y,anchura,altura)
		g.fillRect(0,0,getInicioCasillas(),670);		// Pintar rectangulo(marco izq)
		g.fillRect(560-getInicioCasillas(),0, getInicioCasillas(), 670);	// Pintar rectangulo(marco dcho)
		g.fillRect(0,560-getInicioCasillas(),580,getInicioCasillas());		// Pintar rectangulo(marco abajo)
		
		// Para pintar las casilla, usamos el mismo metodo tanto para pintar las casillas blancas como las negras
		// Para ello, antes de la llamada seteamos el color de las casillas a pitar (negras o blancas) y tiene
		// que ir pintando una si, una no. Valdra para ambos jugadores porque recibe la posicion inicio desde la que pintar
		g.setColor(new Color(255,255,255));				// Set Color en blanco
		pintarCasillaJugador(g,getInicioCasillas(),getInicioCasillas());		// Pintar casillas blancas
		g.setColor(new Color(0,0,0));					// Set color negro
		pintarCasillaJugador(g,getInicioCasillas()-tamCasilla,getInicioCasillas());		// Pintar casillas negras(desde poiscion anterior donde empezamos con blancas)
		dibujarExtra(g);								// Pintar las fichas de damas y demas que falte...
	}
	
	public abstract void dibujarExtra(Graphics g); // Es abstracto, se implementar en la clase que herede(TableroDamas)

	public void pintarCasillaJugador(Graphics g,int iniX1,int iniY1){
		int actX = iniX1;							// Inicializamos la coord X
		int actY = iniY1;							// Inicializamos la coord Y
		boolean primeraPropia = true;				// Interruptor para saber si pintamos desde la 1a o 2a casilla
		int limiteTablero = tamCasilla*tamTablero+getInicioCasillas();	// Lim = numCasillas*tamCasilla+marco

		while (actY < limiteTablero){
			
			while (actX < limiteTablero){				// Pintar una fila
				if (actX >= getInicioCasillas())		// Pintamos si no estamos sobre el marco
				   g.fillRect(actX,actY, tamCasilla, tamCasilla);	// Pintamos la casilla
				actX += tamCasilla*2;								// Avanzamos coord X (cambiamos de columna)
				}
			
			if (primeraPropia){						// Si la primera casilla de la fila pertenece al jugador actual...
				actX = iniX1+tamCasilla;			// Reestablecemos X al comienzo, saltandonos la primera casilla
				primeraPropia = false;				// Cambiamos interruptor
			}
			else{
				actX = iniX1;						// Sino, reestablecemos X al comienzo de la fila
				primeraPropia = true;				// Cambiamos interruptor
			}
			
			actY += tamCasilla;						// Cambiamos de fila
			
			}
	}
}
