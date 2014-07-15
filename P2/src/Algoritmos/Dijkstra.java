package Algoritmos;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;

import Estructuras.Arista;
import Estructuras.Grafo;
import Estructuras.Vertice;

public class Dijkstra {
	
	private Grafo grafoActual;
	private HashMap<Vertice,Integer> distancia;
	private HashMap<Vertice,Vertice> ruta;
	private Set<Vertice> yaVisitados;
	private PriorityQueue<Vertice> aVisitar;
	
	// Constructora
	public Dijkstra(Grafo g){
		Comparator<Vertice> miComparator = new Comparator<Vertice>(){
			public int compare(Vertice n1,Vertice n2){
				int distancia1 = distancia.get(n1);
				int distancia2 = distancia.get(n2);
				int resultado;
				
				if(distancia1 > distancia2) 
					resultado = 1;
				else if(distancia1 < distancia2)
					resultado = -1;
				else 
					resultado = 0;
				
				return resultado;
			}
		};
		
		distancia = new HashMap<Vertice,Integer>();
		ruta = new HashMap<Vertice,Vertice>();
		yaVisitados = new HashSet<Vertice>();
		aVisitar = new PriorityQueue<Vertice>(10,miComparator);
		grafoActual = g; 
	}
	
	public LinkedList<Arista> calculaCaminoMinimo(Vertice ini, Vertice fin){
		
		Iterator<Vertice> iteradorVertices = grafoActual.getVertices();
		
		// PASO 1: Inicializar todas las distancias a un valor infinito relativo (dado que inicialmente se desconocen
		// ******* excepto la del propio "ini" que sabemos que es 0 
		while (iteradorVertices.hasNext()){
			Vertice aux = iteradorVertices.next();
			if (aux == ini)
				distancia.put(ini, 0);
			else
				distancia.put(aux,Integer.MAX_VALUE);
		}
		
		aVisitar.add(ini);
		
		while (!aVisitar.isEmpty()){
			Vertice v=aVisitar.poll();
			yaVisitados.add(v);
			relajarVertice(v);
		}
		
		LinkedList<Arista> sol = new LinkedList<Arista>();
		
		boolean terminar = false;
		while (!terminar){
			Vertice n2 = ruta.get(fin);
			Arista a = null;
			if (n2 != null)
			a = grafoActual.getArista(fin, n2);    
			if (n2 == null || n2.igualNombre(fin)) 
			   terminar=true;
			else {
				sol.add(a);
				fin=n2;
			}
		}
			return sol;
	}
	
	private void relajarVertice(Vertice u) {
		
		Iterator<Vertice> iteradorVertices = grafoActual.getVertices();
		
		while (iteradorVertices.hasNext()){
			Vertice v = iteradorVertices.next();
			if (grafoActual.sonAdyacentes(u,v) && !yaVisitados.contains(v)){
				if (distancia.get(v) > directo(u,v) + distancia.get(u)){
					distancia.put(v,directo(u,v) + distancia.get(u));
					ruta.put(v, u);
					if (aVisitar.contains(v))
						aVisitar.remove(v);
					aVisitar.add(v);
				}
			}
		}
	}
	
	private Integer directo(Vertice a, Vertice b) {
		Arista arista = grafoActual.getArista(a, b);
		return arista.getValorArco();
	}
} // class Dijkstra
