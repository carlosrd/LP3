package Algoritmos;

import java.util.LinkedList;

import Estructuras.Arista;
import Estructuras.Grafo;
import Estructuras.Vertice;

public class Floyd {
	
	private int[][] adyacencia;
	private int[][] ruta;
	private Grafo grafoActual;
	int MAX_VERTICES;
	
	public Floyd(Grafo g) {
		grafoActual = g;
		MAX_VERTICES = g.getNumVertices();
		adyacencia = new int [MAX_VERTICES][MAX_VERTICES];
		ruta = new int [MAX_VERTICES][MAX_VERTICES];
		inicializar();
	}
	
	public void inicializar(){
		
		for(int i = 0; i < MAX_VERTICES;i++ ){			
			for (int j=0; j < MAX_VERTICES; j++){
				
				int x = grafoActual.valorArista(i,j);
				
				if(x == 0) {
					if(i==j) 
					  this.adyacencia[i][j] = 0;
					else 
					  this.adyacencia[i][j] = Integer.MAX_VALUE;
					}
				else 
					this.adyacencia[i][j] = x;
		
				this.ruta[i][j] = 0;
			}
		}
	}
	
	
	public void floyd(){
		for(int k=0;k <MAX_VERTICES; k++){
			for(int i = 0; i<MAX_VERTICES;i++ ){			
				for (int j=0; j<MAX_VERTICES; j++){
					int x = adyacencia[i][k];
					int y = adyacencia[k][j];
					int z = adyacencia[i][j];
					if(x < Integer.MAX_VALUE && y < Integer.MAX_VALUE) {
						if(z > x+y) {
							adyacencia[i][j] = x+y;
							this.ruta[i][j] = k;
						}
					}
				}
			}
		}
	}
	
	
	public LinkedList<Arista> getRuta(Vertice origen,Vertice destino){
		
		LinkedList<Arista> solucion = new LinkedList<Arista>();
		getRutaRec(origen,destino,solucion);
		return solucion;
		
	}
	
	public void getRutaRec(Vertice origen, Vertice destino, LinkedList<Arista> solucion){
		int i = origen.getID();
		int j = destino.getID();
		
		if (ruta[i][j] == 0){
			Arista a = new Arista(origen,destino,adyacencia[i][j]);
			solucion.add(a);
		}
		else{
			int k = ruta[i][j];
			Vertice intermedio = grafoActual.buscaVertice(k);
			getRutaRec(origen,intermedio,solucion);
			getRutaRec(intermedio,destino,solucion);
		
		}
		
		
	}
		
	public LinkedList<Arista> calculaCaminoMinimo(Vertice origen, Vertice destino){
		floyd();
		return getRuta(origen,destino);
	}
}
