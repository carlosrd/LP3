package superm;

import java.io.*;
import java.util.Scanner;

public class Supermercado {

	// Atributos
	// *********
	
	Categoria FyV;
	Categoria Carnes;
	Categoria Pescados;
	Categoria Bebidas;
	Categoria Varios;
	
	public Supermercado(){};
	
	// Consultoras
	// ***********
	public Categoria getCategoriaFyV(){return FyV;}
	public Categoria getCategoriaCarnes(){return Carnes;}
	public Categoria getCategoriaPescados(){return Pescados;}
	public Categoria getCategoriaBebidas(){return Bebidas;}
	public Categoria getCategoriaVarios(){return Varios;}
	
	// Metodos
	// *******
		public void inicializarFyV(File f) throws FileNotFoundException {
			FyV = new Categoria();
			Scanner sc = new Scanner(f);  // Scanner es un objeto que permite leer ficheros
			while (sc.hasNext()){
				String producto = sc.nextLine();		// Leemos el producto
				String precio = sc.nextLine();			// Leemos el precio
				int p = Integer.parseInt(precio);		// Transformamos el precio a "int"
				FyV.add(producto, p, 0);				// Añadimos el producto a la categoria
				}
			sc.close();
		}
		
		public void inicializarCarnes(File f) throws FileNotFoundException {
			Carnes = new Categoria();
			Scanner sc = new Scanner(f);  // Scanner es un objeto que permite leer ficheros
			while (sc.hasNext()){
				String producto = sc.nextLine();		// Leemos el producto
				String precio = sc.nextLine();			// Leemos el precio
				int p = Integer.parseInt(precio);		// Transformamos el precio a "int"
				Carnes.add(producto, p, 0);				// Añadimos el producto a la categoria
				}
			sc.close();
		}
		
		public void inicializarPescados(File f) throws FileNotFoundException {
			Pescados = new Categoria();
			Scanner sc = new Scanner(f);  // Scanner es un objeto que permite leer ficheros
			while (sc.hasNext()){
				String producto = sc.nextLine();		// Leemos el producto
				String precio = sc.nextLine();			// Leemos el precio
				int p = Integer.parseInt(precio);		// Transformamos el precio a "int"
				Pescados.add(producto, p, 0);				// Añadimos el producto a la categoria
				}
			sc.close();
		}
		
		public void inicializarBebidas(File f) throws FileNotFoundException {
			Bebidas = new Categoria();
			Scanner sc = new Scanner(f);  // Scanner es un objeto que permite leer ficheros
			while (sc.hasNext()){
				String producto = sc.nextLine();		// Leemos el producto
				String precio = sc.nextLine();			// Leemos el precio
				int p = Integer.parseInt(precio);		// Transformamos el precio a "int"
				Bebidas.add(producto, p, 0);				// Añadimos el producto a la categoria
				}
			sc.close();
		}
		
		public void inicializarVarios(File f) throws FileNotFoundException {
			Varios = new Categoria();
			Scanner sc = new Scanner(f);  // Scanner es un objeto que permite leer ficheros
			while (sc.hasNext()){
				String producto = sc.nextLine();		// Leemos el producto
				String precio = sc.nextLine();			// Leemos el precio
				int p = Integer.parseInt(precio);		// Transformamos el precio a "int"
				Varios.add(producto, p, 0);				// Añadimos el producto a la categoria
				}
			sc.close();
		}
}
