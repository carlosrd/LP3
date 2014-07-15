package superm;

public class Categoria {
	
	// Atributos
	// *********
	Producto productos[];
	int numProductos;
	final static int max = 20;
	
	// Constructor
	// ***********
	public Categoria(){
		numProductos = 0;
		productos = new Producto[max];
	}
	
	// Consultoras
	// ***********
	public int getNumProductos(){ return numProductos;}
	public Producto getElemento(int i){ return productos[i];}
	
	
	// Metodos
	// *******
	void add(String nombre, int precio, int cantidad){
		
		int i = 0;
		boolean encontrado = false;
		
		// Buscamos a ver si esta: En ese caso, añadimos uno a la cantidad
		while (!encontrado && i<numProductos){
			if (nombre == productos[i].getNombre()){			// Si el producto esta, incrementamos la cantidad??
				//productos[i].incrementaCantidad();				
				encontrado = true;								
				}
			else 
				i++;											// Sino, avanzar en el vector
			} // while
		
		// Si tras buscarlo no lo hemos encontrado, lo añadimos
		if (encontrado == false){
			Producto p = new Producto(nombre,precio,0);			// Crear el objeto producto
			productos[i] = p;									// Añadir el producto a la categoria
			numProductos++;										// Incrementamos el numero de productos
			}
				
		}
};
	