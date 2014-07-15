package Estructuras;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JOptionPane;

public class Grafo {

	Set<Arista> conjAristas;
	Set<Vertice> conjVertices;
	
	int numVertices;
	
	
	// CONSTRUCTORA
	// ****************************************************
	public Grafo(){
		conjAristas = new HashSet<Arista>();
		conjVertices = new HashSet<Vertice>();
		numVertices = 0;
	}
	
	// CONSULTORAS
	// ****************************************************
	public Iterator<Vertice> getVertices(){
		return conjVertices.iterator();
	}
	
	public Iterator<Arista> getAristas(){
		return conjAristas.iterator();
	}
	
	public int getNumVertices(){
		return numVertices;
	}
	
	// METODOS
	// ****************************************************
	public int anyadirVertice(int x,int y,String nombre){
		
		Vertice v = new Vertice(x,y,nombre,numVertices);
		Iterator<Vertice> iteradorVertice = conjVertices.iterator();
		int resultado = 0;
		
		while (iteradorVertice.hasNext() && resultado == 0){
			Vertice aux = iteradorVertice.next();
			if (aux.igualPosicion(v)) resultado = 1;
			if (aux.igualNombre(v)) resultado = -1;
		}
		
		if (resultado == 0){
		   conjVertices.add(v);
		   numVertices++;
		}
		
		return resultado;
		
	}

	public Vertice buscaVertice(int x, int y) {
		
		Vertice v = new Vertice(x,y,"",0);
		Iterator<Vertice> iteradorVertice = conjVertices.iterator();
		
		Vertice resultado = null;
		while (iteradorVertice.hasNext()){
			Vertice aux = iteradorVertice.next();
			if (aux.igualPosicion(v)) resultado = aux;
		}
		
		return resultado;
	}
	
	public Vertice buscaVertice(String nombre) {
		
		Vertice v = new Vertice(0,0,nombre,0);
		Iterator<Vertice> iteradorVertice = conjVertices.iterator();
		
		Vertice resultado = null;
		while (iteradorVertice.hasNext()){
			Vertice aux = iteradorVertice.next();
			if (aux.igualNombre(v)) resultado = aux;
		}
		
		return resultado;
	}
	
	public Vertice buscaVertice(int id) {
		
		Vertice v = new Vertice(0,0,"",id);
		Iterator<Vertice> iteradorVertice = conjVertices.iterator();
		
		Vertice resultado = null;
		while (iteradorVertice.hasNext()){
			Vertice aux = iteradorVertice.next();
			if (aux.igualID(v)) resultado = aux;
		}
		
		return resultado;
	}

	public boolean anyadirArista(Arista aristaActual) {
		
		Iterator<Arista> iteradorAristas = conjAristas.iterator();
		
		boolean encontrado = false;
		
		while (iteradorAristas.hasNext() && !encontrado){
			Arista aux = iteradorAristas.next();
			if (aux.igualArista(aristaActual)) encontrado = true;
		}
		
		if (!encontrado)
		   conjAristas.add(aristaActual);	
		
		return !encontrado;
	}
	
	public void eliminarVertice(Vertice v){
		
		Iterator<Arista> iteradorAristas = conjAristas.iterator();
		Set<Arista> conjAux = new HashSet<Arista>();
		
		while (iteradorAristas.hasNext()){
			Arista aux = iteradorAristas.next();
			if (!aux.contieneAlVertice(v)) 
			   conjAux.add(aux);
		}
		
		conjAristas = conjAux;
		conjVertices.remove(v);	
	}
	
	public void eliminarArista(Vertice a,Vertice b){
		
		Iterator<Arista> iteradorAristas = conjAristas.iterator();
		Set<Arista> conjAux = new HashSet<Arista>();
		
		while (iteradorAristas.hasNext()){
			Arista aux = iteradorAristas.next();
			if (!(aux.contieneAlVertice(a) && aux.contieneAlVertice(b))) 
			   conjAux.add(aux);
		}
		
		conjAristas = conjAux;
	}
	
	public Arista getArista(Vertice a,Vertice b){
		
		Iterator<Arista> iteradorAristas = conjAristas.iterator();
		Arista res = null;
		
		while (iteradorAristas.hasNext() && res == null){
			Arista aux = iteradorAristas.next();
			if ((a.igualNombre(aux.getOrigen()) &&  b.igualNombre(aux.getDestino())) ||
			    (b.igualNombre(aux.getOrigen()) && a.igualNombre(aux.getDestino())))
			   res = aux;
		}
		
		return res;
	}
	
	public boolean sonAdyacentes(Vertice a,Vertice b){ return getArista(a,b) != null;}
	
	public int valorArista(int id1,int id2){
		
		Vertice a = buscaVertice(id1);
		Vertice b = buscaVertice(id2);
		
		Arista ari = getArista(a,b);
		int res = 0;
		
		if (ari != null)
			res = ari.getValorArco();
		
		return res;
	}

	public void decrementaVertices() {
		numVertices--;
	}
	
	public void cargarDesdeFichero(File rutaGrafoActual){
		
		Scanner sc=null;
		try {
			sc = new Scanner(rutaGrafoActual);
			
			int verticesPendientes = Integer.parseInt(sc.nextLine());
			
			while (verticesPendientes > 0){
				int x = Integer.parseInt(sc.nextLine());		
				int y = Integer.parseInt(sc.nextLine());			
				String nombre = sc.nextLine();	
				anyadirVertice(x, y, nombre);
				verticesPendientes--;
				}
			
			while (sc.hasNext()){
				String origenNombre = sc.nextLine();
				String destinoNombre = sc.nextLine();
				Vertice origen = buscaVertice(origenNombre);
				Vertice destino = buscaVertice(destinoNombre);
				int valorArco = Integer.parseInt(sc.nextLine());
				
				if ((origen == null) || (destino == null))
				   JOptionPane.showMessageDialog(null, "Error cargando arista desde fichero\nNo se añadira la arista al grafo!");
				 else {
				   Arista a = new Arista(origen,destino,valorArco);
				   anyadirArista(a);	 
				 }
				   
			}
			
			sc.close();	
			
			
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null, "Error cargando grafo desde fichero!\nCompruebe que ha seleccionado un archivo valido");
			//e1.printStackTrace();
		}  
	
		

	}
} // class Grafo
