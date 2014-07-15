package Algoritmos;

public class Grupo {
	
	private Grupo padre;
	private int rango;
		
	public Grupo(){
		padre=null;
		rango=0;
	}
		
	public Grupo papa(){
		Grupo g=this;
		while (g.padre!=null){
				g=g.padre;
			}
		return g;
	}
		
	public boolean union(Grupo g1){
		Grupo a = this.papa();
		Grupo b = g1.papa();
		if (a == b)
			return false;
		else
			if (a.rango >= b.rango){
				b.padre = a;
				a.rango--;
			}
			else {
				a.padre = b;
				b.rango--;
			}
		return true;
	}
}
