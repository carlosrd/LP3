package Interfaz;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Algoritmos.Dijkstra;
import Algoritmos.Floyd;
import Algoritmos.Kruskal;
import Estructuras.Arista;
import Estructuras.Grafo;
import Estructuras.Vertice;

@SuppressWarnings("serial")
public class ClasePrincipal  extends JFrame {

	Lienzo canvas;
	JPanel panelPrincipal;
	
	File rutaGrafoActual;
	FileOutputStream rutaGrafoActualOut;
	
	public static void main(String[] args) {
		// obj representa el frame
		ClasePrincipal obj = new ClasePrincipal();
		obj.setVisible(true);
		obj.setEnabled(true);
		obj.setSize(1000,600);
	}
	
	// CONSTRUCTORA
	// *************************************
	
	public ClasePrincipal() { // Constructora
		canvas = new Lienzo();
		canvas.addMouseListener(canvas);
		inicializarInterfaz();
		
		rutaGrafoActual = null;
		rutaGrafoActualOut = null;
		
		JOptionPane.showMessageDialog(null,"AVISO: ....");
	}
	
	// METODOS
	// *************************************
	
	private void inicializarInterfaz() { // Añadimos menu y panel
		this.setJMenuBar(getMenuPrincipal());
		this.setContentPane(getPanelPrincipal());
		this.setTitle("Grafos App");
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Sirve para que se termine el proceso cuando se pulsa sobre la X de cerrar
	}
	
	private JMenuBar getMenuPrincipal(){
		JMenuBar barraMenu = new JMenuBar();
		barraMenu.add(getMenuArchivo());
		barraMenu.add(getMenuGrafo());
		barraMenu.add(getMenuOperaciones());
		barraMenu.setVisible(true);
		return barraMenu;
	}
	
	
	private JPanel getPanelPrincipal() {
		panelPrincipal = new JPanel();
				/*BorderLayout b = new BorderLayout();
				panelPrincipal.setLayout(b);*/
		
		panelPrincipal.setLayout(new BorderLayout());
		panelPrincipal.add(canvas,"Center");

		panelPrincipal.validate();


		return panelPrincipal;
	}
	
	
	// MENU ARCHIVO
	// *************************************
	
	private JMenu getMenuArchivo(){
		JMenu archivoMenu = new JMenu("Archivo");
		
		archivoMenu.add(getNuevoGrafoItem());
		archivoMenu.add(getAbrirItem());
		archivoMenu.add(getGuardarItem());
		archivoMenu.add(getGuardarComoItem());

		return archivoMenu;
	}
	
	private JMenuItem getNuevoGrafoItem(){
		JMenuItem nuevoGrafoItem = new JMenuItem();
		nuevoGrafoItem.setText("Nuevo grafo");
		
		nuevoGrafoItem.addActionListener(new ActionListener()
		{public void actionPerformed (ActionEvent e)
				{
				canvas.reiniciar();
				rutaGrafoActual = null;
				rutaGrafoActualOut = null;
				}
		}
		);

		return nuevoGrafoItem;
	}
	
	private JMenuItem getAbrirItem(){
		JMenuItem abrirItem = new JMenuItem();
		abrirItem.setText("Abrir");
		
		abrirItem.addActionListener(new ActionListener()
		{public void actionPerformed (ActionEvent e)
				{
				// Abrir SaveDialog (FileChooser)
				JFileChooser chooser = new JFileChooser();			// Creamos objeto SaveDialog
				chooser.showOpenDialog(ClasePrincipal.this);		// Mostrar SaveDialog
				rutaGrafoActual = chooser.getSelectedFile();
				
				Scanner sc=null;
				try {
					sc = new Scanner(rutaGrafoActual);
					
					int numVertices = Integer.parseInt(sc.nextLine());
					Grafo g = new Grafo();
					
					while (numVertices > 0){
						int x = Integer.parseInt(sc.nextLine());		
						int y = Integer.parseInt(sc.nextLine());			
						String nombre = sc.nextLine();	
						g.anyadirVertice(x, y, nombre);
						numVertices--;
						}
					
					while (sc.hasNext()){
						String origenNombre = sc.nextLine();
						String destinoNombre = sc.nextLine();
						Vertice origen = g.buscaVertice(origenNombre);
						Vertice destino = g.buscaVertice(destinoNombre);
						int valorArco = Integer.parseInt(sc.nextLine());
						
						if ((origen == null) || (destino == null))
						   JOptionPane.showMessageDialog(null, "Error cargando arista desde fichero\nNo se añadira la arista al grafo!");
						 else {
						   Arista a = new Arista(origen,destino,valorArco);
						   g.anyadirArista(a);	 
						 }
						   
					}
					
					canvas.setGrafo(g);
					
					sc.close();	
					
					
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "Error cargando grafo desde fichero!\nCompruebe que ha seleccionado un archivo valido");
					//e1.printStackTrace();
				}  
			
				}
		}
		);

		return abrirItem;
	}
	

	
	private JMenuItem getGuardarItem(){
		JMenuItem guardarItem = new JMenuItem();
		guardarItem.setText("Guardar");
		
		guardarItem.addActionListener(new ActionListener()
		{public void actionPerformed (ActionEvent e)
				{
				if (rutaGrafoActual == null){
					// Abrir SaveDialog (FileChooser)
					JFileChooser chooser = new JFileChooser();			// Creamos objeto SaveDialog
					chooser.showSaveDialog(ClasePrincipal.this);		// Mostrar SaveDialog
					rutaGrafoActual = chooser.getSelectedFile();
					}
				guardarGrafoEnFichero();
				}

		}
		);

		return guardarItem;
	}
	
	private JMenuItem getGuardarComoItem(){
		JMenuItem guardarComoItem = new JMenuItem();
		guardarComoItem.setText("Guardar como...");
		
		guardarComoItem.addActionListener(new ActionListener()
		{public void actionPerformed (ActionEvent e)
				{
				// Abrir OpenDialog (FileChooser)
				JFileChooser chooser = new JFileChooser();			// Creamos objeto SaveDialog
				chooser.showSaveDialog(ClasePrincipal.this);		// Mostrar SavenDialog
				rutaGrafoActual = chooser.getSelectedFile();
				
				guardarGrafoEnFichero();
				}

		}
		);

		return guardarComoItem;
	}
	
	
	// MENU GRAFO
	// *************************************
	
	private JMenu getMenuGrafo(){
		JMenu grafoMenu = new JMenu("Grafo");
		
		grafoMenu.add(getAnyadirVerticeItem());
		grafoMenu.add(getAnyadirAristaItem());
		grafoMenu.add(getEliminarVerticeItem());
		grafoMenu.add(getEliminarAristaItem());

		return grafoMenu;
	}
	
	private JMenuItem getAnyadirVerticeItem(){
		JMenuItem anyadirVerticeItem = new JMenuItem();
		anyadirVerticeItem.setText("Añadir vertice");
		
		anyadirVerticeItem.addActionListener(new ActionListener()
		{public void actionPerformed (ActionEvent e)
				{
				canvas.setDibujandoVertices();
				}
		}
		);

		return anyadirVerticeItem;
	}
	
	private JMenuItem getAnyadirAristaItem(){
		JMenuItem anyadirAristaItem = new JMenuItem();
		anyadirAristaItem.setText("Añadir arista");
		
		anyadirAristaItem.addActionListener(new ActionListener()
		{public void actionPerformed (ActionEvent e)
				{
				canvas.setDibujandoAristas();
				}
		}
		);

		return anyadirAristaItem;
	}
	
	private JMenuItem getEliminarVerticeItem(){
		JMenuItem eliminarVerticeItem = new JMenuItem();
		eliminarVerticeItem.setText("Elminar vertice");
		
		eliminarVerticeItem.addActionListener(new ActionListener()
		{public void actionPerformed (ActionEvent e)
				{
				canvas.setEliminandoVertices();
				}
		}
		);

		return eliminarVerticeItem;
	}
	
	private JMenuItem getEliminarAristaItem(){
		JMenuItem eliminarVerticeItem = new JMenuItem();
		eliminarVerticeItem.setText("Elminar arista");
		
		eliminarVerticeItem.addActionListener(new ActionListener()
		{public void actionPerformed (ActionEvent e)
				{
				JOptionPane.showMessageDialog(null, "Seleccione la arista a eliminar pulsando sobre los 2 vertices que une dicha arista");
				canvas.setEliminandoAristas();
				}
		}
		);

		return eliminarVerticeItem;
	}
	
	// MENU OPERACIONES
	// *************************************
	
	private JMenu getMenuOperaciones(){
		JMenu operacionesMenu = new JMenu("Operaciones");
		
		operacionesMenu.add(getDijkstraItem());
		operacionesMenu.add(getFloydItem());
		operacionesMenu.add(getKruskalItem());
		
		return operacionesMenu;
	}
	
	private JMenuItem getDijkstraItem(){
		JMenuItem dijkstraItem = new JMenuItem();
		dijkstraItem.setText("Camino minimo entre 2 vertices (Dijkstra)");
		
		dijkstraItem.addActionListener(new ActionListener()
		{public void actionPerformed (ActionEvent e)
				{
				canvas.repaint();
				Vertice origen = null;
				Vertice destino = null;
				Grafo grafoActual = canvas.getGrafo();
				while (origen == null){
					  String origenNombre = JOptionPane.showInputDialog("Dijkstra:\nEscribe el nombre del vertice de inicio:",JOptionPane.QUESTION_MESSAGE);
					  origen = grafoActual.buscaVertice(origenNombre);
					  if (origen == null)
						 JOptionPane.showMessageDialog(null, "Dijkstra:\nEl nombre introducido no se corresponde con ningun vertice\nCompruebe que lo ha escrito correctamente");
				}
				while (destino == null){
					  String destinoNombre = JOptionPane.showInputDialog("Dijkstra:\nEscribe el nombre del vertice de destino:",JOptionPane.QUESTION_MESSAGE);
					  destino = grafoActual.buscaVertice(destinoNombre);
					  if (origen == null)
						 JOptionPane.showMessageDialog(null, "Dijkstra:\nEl nombre introducido no se corresponde con ningun vertice\nCompruebe que lo ha escrito correctamente");
				}
				
				Dijkstra dij = new Dijkstra(canvas.getGrafo());
				
				LinkedList<Arista> solucion = dij.calculaCaminoMinimo(origen, destino);
				
				canvas.pintarSolucion(solucion);
				
				}
		}
		);

		return dijkstraItem;
	}
	
	private JMenuItem getFloydItem(){
		JMenuItem floydItem = new JMenuItem();
		floydItem.setText("Camino minimo entre 2 vertices (Floyd)");
		
		floydItem.addActionListener(new ActionListener()
		{public void actionPerformed (ActionEvent e)
				{
				canvas.repaint();
				Vertice origen = null;
				Vertice destino = null;
				Grafo grafoActual = canvas.getGrafo();
				while (origen == null){
					String origenNombre = JOptionPane.showInputDialog("Floyd:\nEscribe el nombre del vertice de inicio:",JOptionPane.QUESTION_MESSAGE);
					origen = grafoActual.buscaVertice(origenNombre);
					if (origen == null)
						JOptionPane.showMessageDialog(null, "Floyd:\nEl nombre introducido no se corresponde con ningun vertice\nCompruebe que lo ha escrito correctamente");
				}
				while (destino == null){
					String destinoNombre = JOptionPane.showInputDialog("Floyd:\nEscribe el nombre del vertice de destino:",JOptionPane.QUESTION_MESSAGE);
					destino = grafoActual.buscaVertice(destinoNombre);
					if (origen == null)
						JOptionPane.showMessageDialog(null, "Floyd:\nEl nombre introducido no se corresponde con ningun vertice\nCompruebe que lo ha escrito correctamente");
				}	
			
				Floyd flo = new Floyd(canvas.getGrafo());
			
				LinkedList<Arista> solucion = flo.calculaCaminoMinimo(origen, destino);
			
				canvas.pintarSolucion(solucion);
				}
		}
		);

		return floydItem;
	}
	
	private JMenuItem getKruskalItem(){
		JMenuItem kruskalItem = new JMenuItem();
		kruskalItem.setText("Arbol minimo de expansion (Kruskal)");
		
		kruskalItem.addActionListener(new ActionListener()
		{public void actionPerformed (ActionEvent e)
				{
				canvas.repaint();
				JOptionPane.showMessageDialog(null, "Kruskal:\nSubgrafo que une todos los vertices (sin ciclos) con aristas cuyo peso total es el minimo");
				
				Kruskal kru = new Kruskal(canvas.getGrafo());
			
				LinkedList<Arista> solucion = kru.calculaArbolRecubrimientoMinimo();

				canvas.pintarSolucion(solucion);
				}
		}
		);

		return kruskalItem;
	}
	
	
	// *************************************************************************
	
	// METODOS
	// *************************************************
	
	public void guardarGrafoEnFichero(){
		
		try {
			rutaGrafoActualOut = new FileOutputStream(rutaGrafoActual);
		} catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(null,"Error guardando el fichero.\n El fichero de destino no existe!");
		}
		
		PrintWriter pw = new PrintWriter(rutaGrafoActualOut);
			
		Grafo gAux = canvas.getGrafo();
		pw.println(gAux.getNumVertices());
		
		Iterator<Vertice> iteradorVertices = gAux.getVertices();
		
		while (iteradorVertices.hasNext()){
			  Vertice v = iteradorVertices.next();
			  pw.println(v.getCoordX());
			  pw.println(v.getCoordY());
			  pw.println(v.getNombre());
			  }
		
		Iterator<Arista> iteradorAristas = gAux.getAristas();
		
		while (iteradorAristas.hasNext()){
			  Arista a = iteradorAristas.next();
			  pw.println(a.getNombreOrigen());
			  pw.println(a.getNombreDestino());
			  pw.println(a.getValorArco());
			  }
	
			
		pw.close();							// Cerramos el fichero		
	}
	
} // class ClasePrincipal

