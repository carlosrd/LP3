package Algoritmos;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;

import Estructuras.Arista;
import Estructuras.Grafo;
import Estructuras.Vertice;

public class Kruskal {
	
	private Grafo grafoActual;
	private HashMap<Vertice,Grupo> tabla;
	private PriorityQueue<Arista> colaAristas;		// aVisitar??
	private LinkedList<Arista> solucion; 
	
	public Kruskal(Grafo g){
		grafoActual = g;
		solucion = new LinkedList<Arista>();
	}
	
	public LinkedList<Arista> calculaArbolRecubrimientoMinimo(){
		crearTablaNodos();
		crearListaArcos();
		while(!colaAristas.isEmpty() && solucion.size() < grafoActual.getNumVertices()-1){
			Arista a = colaAristas.poll();
			Grupo g1 = dameGrupo(a.getOrigen());
			Grupo g2 = dameGrupo(a.getDestino());
			if (g1.union(g2))
				solucion.add(a);
		}
		return solucion;
	}
	
	private void crearListaArcos() {
		
		Comparator<Arista> miComparator = new Comparator<Arista>(){
			public int compare(Arista a,Arista b){
				int p1 = a.getValorArco();
				int p2 = b.getValorArco();
				if (p1 > p2) 
					return 1;
				else 
				   if (p1 < p2) 
					  return -1;
					else
					  return 0;
			}
		};
		
		colaAristas = new PriorityQueue<Arista>(10,miComparator);
		
		Iterator<Arista> iteradorAristas = grafoActual.getAristas();
		Arista a;
		
		while(iteradorAristas.hasNext()){
			a = iteradorAristas.next();
			colaAristas.add(a);
		}
	}
	
	private void crearTablaNodos() {
		
		tabla = new HashMap<Vertice,Grupo>();
		Iterator<Vertice> iteradorVertices = grafoActual.getVertices();
		Vertice v;
		Grupo gr;
		while(iteradorVertices.hasNext()){
			v = iteradorVertices.next();
			gr = new Grupo();
			tabla.put(v, gr);
		}
	}

	private Grupo dameGrupo(Vertice v) {
		return tabla.get(v);
	}

}
