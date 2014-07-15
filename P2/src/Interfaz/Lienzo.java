package Interfaz;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import Estructuras.Arista;
import Estructuras.Grafo;
import Estructuras.Vertice;

@SuppressWarnings("serial")
public class Lienzo extends Canvas implements MouseListener{
	
	// ATRIBUTOS
	// **********************************************
	final static int tamVertices = 20;
	boolean dibujandoVertices;
	boolean dibujandoAristas;
	boolean eliminandoVertices;
	boolean eliminandoAristas;
	boolean primerVertice;
	Grafo grafoActual;
	Arista aristaActual;
	Vertice verticeAux;
	
	
	// CONSTRUCTORA
	// **********************************************
	public Lienzo(){
		dibujandoVertices = false;
		dibujandoAristas = false;
		eliminandoVertices = false;
		eliminandoAristas = false;
		primerVertice = true;
		grafoActual = new Grafo();
		this.setBackground(new Color(255,255,255));
		
	}
	
	// MODIFICADORAS
	// **********************************************
	public void setDibujandoVertices(){
		dibujandoVertices = true;
		dibujandoAristas = false;
		eliminandoVertices = false;
		eliminandoAristas = false;
		primerVertice = true;
	}
	
	public void setDibujandoAristas(){
		dibujandoAristas= true;
		dibujandoVertices = false;
		eliminandoVertices = false;
		eliminandoAristas = false;
		primerVertice = true;
	}
	
	public void setEliminandoVertices(){
		eliminandoVertices = true;
		dibujandoVertices = false;
		dibujandoAristas = false;
		eliminandoAristas = false;
		primerVertice = true;
	}
	
	public void setEliminandoAristas(){
		eliminandoAristas = true;
		eliminandoVertices = false;
		dibujandoVertices = false;
		dibujandoAristas = false;
		primerVertice = true;
	}
	
	public void setGrafo(Grafo g){
		grafoActual = g;
		repaint();
	}
	
	// CONSULTORAS
	// **********************************************
	public Grafo getGrafo(){
		return grafoActual;
	}
	
	
	// EVENTOS DEL RATON
	// **********************************************
	
	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}

	@Override 
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		
		if (dibujandoVertices){
			   String nombreVertice = JOptionPane.showInputDialog("Inserte el nombre del vertice:", JOptionPane.QUESTION_MESSAGE);
			   int res = 0;
			   if (nombreVertice != null)
				  res = grafoActual.anyadirVertice(x, y, nombreVertice);
			   switch (res){
			   		
			   		case 0 : this.repaint();
			   				 break;
			   		case 1 : JOptionPane.showMessageDialog(null, "El vertice esta demasido cerca de otro\nEscoja otro punto");
			   				 break;
			   		case -1 : JOptionPane.showMessageDialog(null, "Ya existe un vertice con el nombre: "+nombreVertice+"\nPor favor, escoja otro diferente");
			   				 break;
			   }
			   dibujandoVertices = false;
			}
		
		if (dibujandoAristas){
			 Vertice v = grafoActual.buscaVertice(x,y);
			 if (primerVertice){
				 if (v != null){
					aristaActual = new Arista();
					aristaActual.setOrigen(v);
					primerVertice = false;
					JOptionPane.showMessageDialog(null, "Origen arista: "+v.getNombre()+"\nSeleccione vertice de destino");
				 	}
				  else
					JOptionPane.showMessageDialog(null, "No ha seleccionado ningun vertice para el origen\nPulsa sobre un vertice para seleccionarlo");  
			 	}
			  else{
				 if (v != null){
					aristaActual.setDestino(v);

					boolean valorValido = false;
					boolean cancelarArista = false;
					int valorArco = 0;
					
					while (!valorValido){
						try {
							String valorArista = JOptionPane.showInputDialog("Introduzca un valor para la arista:", JOptionPane.QUESTION_MESSAGE);
							if (valorArista !=null)
							   valorArco = Integer.parseInt(valorArista);
							else
							   cancelarArista = true;
							   
							valorValido = true;
						} catch(Exception ex){
							JOptionPane.showMessageDialog(null, "No se ha introducido un valor valido para el valor de la arista!\nPor favor, introduzca un valor entero");	
							valorValido = false;
						} // catch
				 	} // while
					
					if (!cancelarArista){
					   aristaActual.setValorArco(valorArco);
					   primerVertice = true;
					   dibujandoAristas = false;
					
					   if (grafoActual.anyadirArista(aristaActual)){
						  aristaActual = null;
						  JOptionPane.showMessageDialog(null, "Destino arista: "+v.getNombre());
						  repaint();
					   	  }
					   else
					      JOptionPane.showMessageDialog(null, "Ya existe esta arista\nNo se añadira al conjunto de aristas");
						  
				 	  } // else cancelarArista
				    }
				  else
					JOptionPane.showMessageDialog(null, "No ha seleccionado ningun vertice para el destino\nPulsa sobre un vertice para seleccionarlo");  
				 } // else primerVertice
			
		} // if dibujandoAristas
		
		if (eliminandoVertices){
			Vertice v = grafoActual.buscaVertice(x, y);
			if (v == null)
				JOptionPane.showMessageDialog(null, "No ha seleccionado un vertice\nPara seleccionar un vertice, haga clic sobre el");
			 else {
				grafoActual.eliminarVertice(v);
				eliminandoVertices = false;
				grafoActual.decrementaVertices();
			    repaint();
			    }
			} // if eliminandoVertices
		
		
		if (eliminandoAristas){
			 Vertice v = grafoActual.buscaVertice(x, y);
			 if (primerVertice){
				 if (v != null){
					verticeAux = v; 
					primerVertice = false;
					JOptionPane.showMessageDialog(null, "Origen arista: "+v.getNombre()+"\nSeleccione vertice de destino");
				 	}
				  else
					JOptionPane.showMessageDialog(null, "No ha seleccionado ningun vertice para el origen\nPulsa sobre un vertice para seleccionarlo");  
			 	}
			  else{
				 if (v != null){
					JOptionPane.showMessageDialog(null, "Destino arista: "+v.getNombre()+"\nArista eliminada correctamente!");
					
					grafoActual.eliminarArista(verticeAux, v);
					
					primerVertice = true;
					eliminandoAristas = false;
					repaint();
					}
				  else
					JOptionPane.showMessageDialog(null, "No ha seleccionado ningun vertice para el destino\nPulsa sobre un vertice para seleccionarlo");  
				 } // else primerVertice

			} // if eliminandoVertices
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		
	}
	
	// METODOS
	// *************************************************************
	
	// Metodo que dibuja en el canvas
	public void paint(Graphics canvas){
		
        canvas.setFont(new Font("Arial", Font.BOLD, 12));					// Establecemos la fuente para el lienzo
		
		Iterator<Arista> iteradorAristas = grafoActual.getAristas();
		
		while (iteradorAristas.hasNext()){
			  Arista a = iteradorAristas.next();
			  canvas.setColor(a.getColor());
			  String valor = String.valueOf(a.getValorArco());
			  
			  
			  canvas.drawString(valor, ((a.getDestinoX()+a.getOrigenX())/2)+5, (a.getOrigenY()+a.getDestinoY())/2);
			  canvas.drawLine(a.getOrigenX()+10, a.getOrigenY()+10, a.getDestinoX()+10, a.getDestinoY()+10);
		}
		
		Iterator<Vertice> iteradorVertices = grafoActual.getVertices();
		
		while (iteradorVertices.hasNext()){
			  Vertice v = iteradorVertices.next();
			  canvas.setColor(v.getColor());
			  canvas.drawString(v.getNombre(), v.getCoordX()+20, v.getCoordY());
			  canvas.fillOval(v.getCoordX(), v.getCoordY(), tamVertices, tamVertices);
		}
	}
	
	public void pintarSolucion (LinkedList<Arista> listaSolucion) {
		Graphics canvas = getGraphics();
		canvas.setColor(new Color(255,0,0));
		Iterator<Arista> iteradorListaAristas = listaSolucion.iterator();
		while (iteradorListaAristas.hasNext()){
			Arista a = iteradorListaAristas.next();
			  canvas.drawLine(a.getOrigenX()+10, a.getOrigenY()+10, a.getDestinoX()+10, a.getDestinoY()+10);
		}
		
	}

	
	public void reiniciar(){
		dibujandoVertices = false;
		dibujandoAristas = false;
		primerVertice = true;
		grafoActual = new Grafo();
		repaint();
	}
	
}
