package Interfaz;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JMenu;

import Aplicacion.Damas.TModoJuego;
import Movimiento.ComprobadorMovimiento;
import Movimiento.TableroJuego.TFicha;

@SuppressWarnings("serial")
public class TableroFichas extends Tablero implements MouseListener {
	
	Image imgFichas[];
	ComprobadorMovimiento movimientos;	
	JLabel etiqEstado;
	JMenu menuJuego;
	boolean juegoEnCurso;
	
	public TableroFichas(int tam,JLabel etiq,JMenu menu,TModoJuego modoJuego,int nivel_k){
		
		imgFichas = new Image[4];	// Guardamos en el array las 4 imagenes para representar las fichas
		tamTablero = tam;			// Seteamos el tamaño del tablero
		movimientos = new ComprobadorMovimiento(tamTablero,modoJuego,nivel_k); // Creamos un nuevo comprobador de movimientos para el tablero
		etiqEstado = etiq;			// Seteamos el JLabel como display para tener el control de el
		menuJuego = menu;			// Seteamos el Menu principal para tener el control de el
		juegoEnCurso = false;		// Variable que determina si hay una partida en curso (para des/activar el menu)

	}

	
	public void ponerImagenes(Image pRojo,Image pNegro,Image dRoja,Image dNegra){
		
		imgFichas[0] = pRojo;
		imgFichas[1] = pNegro;
		imgFichas[2] = dRoja;
		imgFichas[3] = dNegra;
	
	}

	public void dibujarExtra(Graphics g){
		
		dibujarMatrizFichas(g);
		g.setColor(new Color(0,0,0));	// Set Color Negro para escribir el String de estado del juego
		if (juegoEnCurso){
			g.drawString(movimientos.getDisplay2(),40,535);	// Mostrar el modo de juego
			if (movimientos.getModoJuego() != TModoJuego.Normal)
				g.drawString(movimientos.getDisplay3(),40,550);	// Mostrar el nivel del arbol
		}

		etiqEstado.setText(movimientos.getDisplay());		// Setear en el display de estado el estado de la partida
	}
	
	public void dibujarMatrizFichas(Graphics g){
		
		for (int fil = 0; fil < tamTablero; fil++){
			for (int col = 0; col < tamTablero; col++){
				TFicha ficha = movimientos.getCasilla(fil, col);
				if (ficha != TFicha.Libre) 
					dibujarFicha(g,ficha,fil,col);
				
			}
		}
	}
	
	public void dibujarFicha(Graphics g,TFicha ficha, int fil, int col){
		int x = getInicioCasillas()+tamCasilla*col;	// X = marco + tamCasilla * col
		int y = getInicioCasillas()+tamCasilla*fil; // Y = marco + tamcasilla * fil;
		
		switch (ficha) {
			case peonRojo: g.drawImage(imgFichas[0],x,y,this);
						   break;
			case peonNegro: g.drawImage(imgFichas[1],x,y,this);
							break;
			case damaRoja: g.drawImage(imgFichas[2],x,y,this);
			   			   break;
			case damaNegra: g.drawImage(imgFichas[3],x,y,this);
							break;
		}
	}
	
	public int quePosicionEs(int pos){
		
		int filCol; // Si la pos es mas pequeña que el final del marco o mas grande que el final del tablero
		if (getInicioCasillas() > pos ||		// Si la pos es mas pequeña que el tamaño del marco, pulsó en el marco de arriba o izquierda
			pos > tamCasilla*tamTablero+getInicioCasillas())	// Si la pos es mas grande,que la suma del primer marco con el tablero entero, pulsó en el de abajo o derecha
			filCol = -1;	
		else{
			filCol = pos-getInicioCasillas();	// Sino, a la pos buscada hay que quitarle el marco
			filCol /= tamCasilla;				// Y dividir por el tamaño de casilla para saber cual de todas es (de la fila o la columna)
		}
		return filCol;
	}
	
	public void mousePressed(MouseEvent e){
	
		if (juegoEnCurso){
			int fil = quePosicionEs(e.getY());
			int col = quePosicionEs(e.getX());
			if (fil != -1 && col != -1) {// Si no he pinxao fuera del tablero...
				juegoEnCurso = !(movimientos.procesarMovimiento(fil,col));
				repaint();
				if (!juegoEnCurso)	// Si se acaba la partida, reactivamos el menu de opciones
					menuJuego.setEnabled(true);
			}
		}
	}
	
	public void inicializar(TModoJuego modo,int nivel){
	
		movimientos.reset(modo,nivel);	// Reiniciamos el tablero
		juegoEnCurso = true;			// Seteamos comenzada una nueva partida
		repaint();						// Refrescamos tablero (poner fichas)
	
	}

	public void cambiaModoJuego(TModoJuego modoJuego){
		movimientos.setModoJuego(modoJuego);
	}
	
	
	// METODOS SOBRECARGADOS PARA IMPLEMENTAR (No necesarios)
	// *************************************************************************************
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		
	}
}
