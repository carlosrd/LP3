package superm;

public class Producto {
	String nombre;
	int precio;
	int cantidad;

	// Constructora
	// ************
	
public Producto(String nombre, int precio, int cantidad){
	this.nombre = nombre;
	this.cantidad = cantidad;
	this.precio = precio;
	}

	// Consultoras
	// ***********

public String getNombre(){return nombre;}
public int getPrecio(){return precio;}
public int getCantidad(){return cantidad;}

	// Metodos
void incrementaCantidad(){
	cantidad++;
}

};
