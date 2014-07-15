package Test;

import java.util.Collection;
import java.util.LinkedList;

import Algoritmos.Kruskal;
import Estructuras.Arista;
import Estructuras.Grafo;
import Estructuras.Vertice;
import junit.framework.Assert;
import junit.framework.TestCase;

public class KruskalTest extends TestCase {


	Kruskal kru1,kru2,kru3;
	
	Grafo g1;
	
	Vertice v1,v2,v3,v4,v5;
	Arista a1,a2,a3,a4,a5;
	
	Grafo g2;
	
	Vertice v6,v7,v8,v9,v10;
	Arista a6,a7,a8,a9,a10;
	
	Grafo g3;
	
	Vertice v11,v12,v13,v14,v15;
	Arista a11,a12,a13,a14,a15,a16;

	public KruskalTest(String name) {
		super(name);
	}

	public void setUp() {
		g1 = new Grafo();
		
		g1.anyadirVertice(50,50,"a");
		g1.anyadirVertice(100,100,"b");
		g1.anyadirVertice(150,150,"c");
		g1.anyadirVertice(200,200,"d");
		g1.anyadirVertice(250,250,"e");
		
		v1 = g1.buscaVertice("a");
		v2 = g1.buscaVertice("b");
		v3 = g1.buscaVertice("c");
		v4 = g1.buscaVertice("d");
		v5 = g1.buscaVertice("e");
			
		
		a1 = new Arista(v1,v2,4);
		a2 = new Arista(v2,v3,7);
		a3 = new Arista(v3,v4,9);
		a4 = new Arista(v4,v5,11);
		a5 = new Arista(v1,v5,12);
		
		g1.anyadirArista(a1);
		g1.anyadirArista(a2);
		g1.anyadirArista(a3);
		g1.anyadirArista(a4);
		g1.anyadirArista(a5);
		
		
		g2 = new Grafo();
		
		g2.anyadirVertice(50, 50,"f");
		g2.anyadirVertice(100,100,"g");
		g2.anyadirVertice(150,150,"h");
		g2.anyadirVertice(200,200,"i");
		g2.anyadirVertice(250,250,"j");
		
		v6 = g2.buscaVertice("f");
		v7 = g2.buscaVertice("g");
		v8 = g2.buscaVertice("h");
		v9 = g2.buscaVertice("i");
		v10 = g2.buscaVertice("j");
			
		g2.anyadirArista(new Arista(v6,v7,4));
		g2.anyadirArista(new Arista(v7,v8,7));
		g2.anyadirArista(new Arista(v8,v9,9));
		g2.anyadirArista(new Arista(v9,v10,11));
		g2.anyadirArista(new Arista(v9,v7,12));
		
		a6=g2.getArista(v6,v7);
		a7=g2.getArista(v7,v8);
		a8=g2.getArista(v8,v9);
		a9=g2.getArista(v9,v10);
		a10=g2.getArista(v9,v7);

		
		g3 = new Grafo();
		
		g3.anyadirVertice(50, 50,"k");
		g3.anyadirVertice(100,100,"m");
		g3.anyadirVertice(150,150,"l");
		g3.anyadirVertice(200,200,"n");
		g3.anyadirVertice(250,250,"o");
		
		v11=g3.buscaVertice("k");
		v12=g3.buscaVertice("m");
		v13=g3.buscaVertice("l");
		v14=g3.buscaVertice("n");
		v15=g3.buscaVertice("o");
			
		g3.anyadirArista(new Arista(v11,v12,4));
		g3.anyadirArista(new Arista(v12,v13,7));
		g3.anyadirArista(new Arista(v13,v14,9));
		g3.anyadirArista(new Arista(v14,v15,11));
		g3.anyadirArista(new Arista(v11,v15,12));
		g3.anyadirArista(new Arista(v12,v14,1));
		
		a11=g3.getArista(v11,v12);
		a12=g3.getArista(v12,v13);
		a13=g3.getArista(v13,v14);
		a14=g3.getArista(v14,v15);
		a15=g3.getArista(v11,v15);
		a16=g3.getArista(v12,v14);
			
		kru1 = new Kruskal(g1);
		kru2 = new Kruskal(g2);
		kru3 = new Kruskal(g3);
		
	}
	
	public void testAAlgoritmo(){
		LinkedList<Arista> aux=new LinkedList<Arista>();
		aux.add(a1);
		aux.add(a2);
		aux.add(a3);
		aux.add(a4);
		Collection<Arista> expected=aux;
		Assert.assertEquals(expected,(Collection<Arista>)kru1.calculaArbolRecubrimientoMinimo());
	}
	
	public void testBAlgoritmo(){
		LinkedList<Arista> aux=new LinkedList<Arista>();
		aux.add(a6);
		aux.add(a7);
		aux.add(a8);
		aux.add(a9);
		Collection<Arista> expected=aux;
		Assert.assertEquals(expected,(Collection<Arista>)kru2.calculaArbolRecubrimientoMinimo());
	}
	
	public void testCAlgoritmo(){
		LinkedList<Arista> aux=new LinkedList<Arista>();
		aux.add(a16);
		aux.add(a11);
		aux.add(a12);
		aux.add(a14);
		Collection<Arista> expected = aux;
		Assert.assertEquals(expected,(Collection<Arista>)kru3.calculaArbolRecubrimientoMinimo());
	}
}
