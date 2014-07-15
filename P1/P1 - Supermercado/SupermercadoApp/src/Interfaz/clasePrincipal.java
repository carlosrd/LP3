package Interfaz;
import gestioncaja.Caja;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import superm.Producto;
import superm.Supermercado;
import superm.Categoria;


@SuppressWarnings("serial")

public class clasePrincipal extends JFrame {
	
	// ATRIBUTOS 
	// *********************************************
	
	Supermercado sup;
	Caja caja;
	
	// Booleanos para el estado de anyadidos
	
	boolean anyadidoFyV;
	boolean anyadidoCarnes;
	boolean anyadidoPescados;
	boolean anyadidoBebidas;
	boolean anyadidoVarios;
	
	// String para guardar las rutas de los ficheros
	
	File rutaFyV;
	File rutaCarnes;
	File rutaPescados;
	File rutaBebidas;
	File rutaVarios;
	File rutaCajaI;
	FileOutputStream rutaCajaO;
	
	// Tablas de Elementos
	
	JTable tabFyV;
	JTable tabCarnes;
	JTable tabPescados;
	JTable tabBebidas;
	JTable tabVarios;
	
	// Elementos del menu que controlar
	
	JMenuItem FyVItem;
	JMenuItem CarnesItem;
	JMenuItem PescadosItem;
	JMenuItem BebidasItem;
	JMenuItem VariosItem;
	
	// Panel tabulado (con pestañas)
	JTabbedPane panelTabulado;
	
// --------------------------------------------------------------------------	

	// CONSTRUCTORA
	// ********************************
	
	public clasePrincipal() { // Constructora
		inicializarInterfaz();
		inicializarSupermercado();
		inicializarCaja();
		JOptionPane.showMessageDialog(null,"AVISO:\n Para mostrar las tablas de producto, debe cargarlos desde ficheros.\n" +
				"Si no dispone de unos, puede usar las versiones por defecto\n\n" +
				"Los precios incluyen los centimos. Por ej: 250 = 2.50 €");
	}
	

	// METODOS
	// *********************
	
	public static void main(String[] args) {
		// obj representa el frame
		clasePrincipal obj = new clasePrincipal();
		obj.setVisible(true);
		obj.setEnabled(true);
		obj.setSize(1000,600);
	}
	
	private void inicializarSupermercado() {
		sup = new Supermercado();
	}

	private void inicializarInterfaz() { // Añadimos menu y panel
		this.setJMenuBar(getMenuPrincipal());
		this.setContentPane(getPanelPrincipal());
		this.setTitle("Supermercado App");
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Sirve para que se termine el proceso cuando se pulsa sobre la X de cerrar
		
		anyadidoFyV = false;
		anyadidoCarnes = false;
		anyadidoPescados = false;
		anyadidoBebidas = false;
		anyadidoVarios = false;
		
		rutaFyV = new File("../frutasyverduras.txt");
		rutaCarnes = new File("../carnes.txt");
		rutaPescados = new File("../pescados.txt");
		rutaBebidas = new File("../bebidas.txt");
		rutaVarios = new File("../varios.txt");
		rutaCajaI = new File("../caja.txt");

		tabFyV = null;
		tabCarnes = null;
		tabPescados = null;
		tabBebidas = null;
		tabVarios = null;
	}
	
	private void inicializarCaja() {
		caja = new Caja();
		try {
			caja.inicializar(rutaCajaI);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			JOptionPane.showMessageDialog(null,"Error cargando fichero de caja! No se ha encontrado: caja.txt");
		}
		
		this.addWindowListener(new WindowAdapter()
		{public void windowClosing(WindowEvent e)
			{
			
			try {
				rutaCajaO = new FileOutputStream(rutaCajaI);
			} catch (FileNotFoundException e2) {
				//e.printStackTrace();
				JOptionPane.showMessageDialog(null,"Error al guardar en el fichero de caja! No se ha encontrado: caja.txt");
			}
			
			caja.salvarEnFichero(rutaCajaO); // Volcar a fichero y cerrar
			System.exit(0);
			}
		}
		);

	}

	private JMenuBar getMenuPrincipal(){
		JMenuBar barraMenu = new JMenuBar();
		barraMenu.add(getMenuSeleccion());
		barraMenu.add(getMenuCaja());
		barraMenu.add(getMenuConfiguracion());
		barraMenu.setVisible(true);
		return barraMenu;
	}

	private JPanel getPanelPrincipal() {
		JPanel panelPrincipal = new JPanel();
				/*BorderLayout b = new BorderLayout();
				panelPrincipal.setLayout(b);*/
		
		panelPrincipal.setLayout(new BorderLayout());
		
		panelTabulado = new JTabbedPane();
		
		panelPrincipal.add(panelTabulado,"Center");
		panelPrincipal.validate();

		return panelPrincipal;
	}
	
	// ------------------------------------------------------------------------------------------------------	
	
	// MENU "CONFIGURACION"
	// ********************
	private JMenu getMenuConfiguracion(){
		JMenu configMenu = new JMenu("Configuracion");
		
		configMenu.add(getSubmenuProductoItem());
		configMenu.add(getProductoDefectoItem());
		configMenu.add(getSubmenuOpCajaItem());

		return configMenu;
	}
	
	
	// MENU "CONFIGURACION" > PRODUCTOS POR DEFECTO
	// ********************************************
	
	private JMenuItem getProductoDefectoItem(){
		JMenuItem PDFItem = new JMenuItem();
		PDFItem.setText("Productos por defecto");
		
		PDFItem.addActionListener(new ActionListener()
		{public void actionPerformed (ActionEvent e)
				{	// Activar todos los item en el Menu Seleccion (Los ficheros por defecto estan en "workspace"
					FyVItem.setEnabled(true);
					CarnesItem.setEnabled(true);
					PescadosItem.setEnabled(true);						
					BebidasItem.setEnabled(true);
					VariosItem.setEnabled(true);
				}
		}
		);
		
		return PDFItem;
	}
	
	// MENU "CONFIG" > CAJA...
	// ***********************************
	
	private JMenu getSubmenuOpCajaItem(){
		JMenu subMenuCaja = new JMenu("Operaciones de caja");
		subMenuCaja.add(getConsultarCajaItem());
		subMenuCaja.add(getLlenarCajaItem());
		subMenuCaja.add(getVaciarCajaItem());
		return subMenuCaja;
	}
	
	private JMenuItem getConsultarCajaItem(){
		JMenuItem consultarCajaItem = new JMenuItem();
		consultarCajaItem.setText("Consultar caja");
		
		consultarCajaItem.addActionListener(new ActionListener()
		{public void actionPerformed (ActionEvent e)
				{
				caja.consultar();
				}
		}
		);

		return consultarCajaItem;
	}
	
	private JMenuItem getLlenarCajaItem(){
		JMenuItem LlenarCajaItem = new JMenuItem();
		LlenarCajaItem.setText("Llenar caja");
		
		LlenarCajaItem.addActionListener(new ActionListener()
		{public void actionPerformed (ActionEvent e)
				{
				caja.llenar();		// Llenar la caja con 25 monedas de cada una
				}
		}
		);
		
		return LlenarCajaItem;
	}
	
	private JMenuItem getVaciarCajaItem(){
		JMenuItem VaciarCajaItem = new JMenuItem();
		VaciarCajaItem.setText("Vaciar caja");
		
		VaciarCajaItem.addActionListener(new ActionListener()
		{public void actionPerformed (ActionEvent e)
				{
					caja.vaciar();		// Dejar 5 monedas de cada en la caja
				}
		}
		);
		
		return VaciarCajaItem;
	}

	// ------------------------------------------------------------------------------------------------------
	
	// MENU "CONFIG" > PRODUCTOS...
	// ***********************************
	private JMenu getSubmenuProductoItem(){
		JMenu seleccionMenu = new JMenu("Productos");
		seleccionMenu.add(getFrutasYVerdurasItem2());
		seleccionMenu.add(getCarnesItem2());
		seleccionMenu.add(getPescadosItem2());
		seleccionMenu.add(getBebidasItem2());
		seleccionMenu.add(getVariosItem2());
		return seleccionMenu;
	}
	
	private JMenuItem getFrutasYVerdurasItem2(){
		JMenuItem FYVItem = new JMenuItem();
		FYVItem.setText("Frutas y Verduras");
		
		FYVItem.addActionListener(new ActionListener()
		{public void actionPerformed (ActionEvent e)
				{
					JFileChooser chooser = new JFileChooser();			// Creamos objeto OpenDialog
					chooser.showOpenDialog(clasePrincipal.this);		// Mostrar OpenDialog
					rutaFyV = chooser.getSelectedFile();				// Asignar a rutaFyV el fichero asociado
					FyVItem.setEnabled(true);							// Activar el item en el Menu Seleccion
				}
		}
		);
		
		return FYVItem;
	}
	
	private JMenuItem getCarnesItem2(){
		JMenuItem CarnesItem2 = new JMenuItem();
		CarnesItem2.setText("Carnes");
		
		CarnesItem2.addActionListener(new ActionListener()
		{public void actionPerformed (ActionEvent e)
				{
					JFileChooser chooser = new JFileChooser();		// Creamos objeto OpenDialog
					chooser.showOpenDialog(clasePrincipal.this);	// Mostrar OpenDialog
					rutaCarnes = chooser.getSelectedFile();			// Asignar a rutaFyV el fichero asociado
					CarnesItem.setEnabled(true);					// Activar el item en el Menu Seleccion
				}
		}
		);
		
		return CarnesItem2;
	}
	
	private JMenuItem getPescadosItem2(){
		JMenuItem PescadosItem2 = new JMenuItem();
		PescadosItem2.setText("Pescados");
		
		PescadosItem2.addActionListener(new ActionListener()
		{public void actionPerformed (ActionEvent e)
				{
					JFileChooser chooser = new JFileChooser();			// Creamos objeto OpenDialog
					chooser.showOpenDialog(clasePrincipal.this);		// Mostrar OpenDialog
					rutaPescados = chooser.getSelectedFile();			// Asignar a rutaFyV el fichero asociado
					PescadosItem.setEnabled(true);						// Activar el item en el Menu Seleccion
				}
		}
		);

		return PescadosItem2;
	}
	
	private JMenuItem getBebidasItem2(){
		JMenuItem BebidasItem2 = new JMenuItem();
		BebidasItem2.setText("Bebidas");
		
		BebidasItem2.addActionListener(new ActionListener()
		{public void actionPerformed (ActionEvent e)
				{
					JFileChooser chooser = new JFileChooser();						// Creamos objeto OpenDialog
					chooser.showOpenDialog(clasePrincipal.this);	// Mostrar OpenDialog
					rutaBebidas = chooser.getSelectedFile();						// Asignar a rutaFyV el fichero asociado
					BebidasItem.setEnabled(true);									// Activar el item en el Menu Seleccion
				}
		}
		);

		return BebidasItem2;
	}
	
	private JMenuItem getVariosItem2(){
		JMenuItem VariosItem2 = new JMenuItem();
		VariosItem2.setText("Varios");
		
		VariosItem2.addActionListener(new ActionListener()
		{public void actionPerformed (ActionEvent e)
				{
					JFileChooser chooser = new JFileChooser();						// Creamos objeto OpenDialog
					chooser.showOpenDialog(clasePrincipal.this);	// Mostrar OpenDialog
					rutaVarios = chooser.getSelectedFile();							// Asignar a rutaFyV el fichero asociado
					VariosItem.setEnabled(true);									// Activar el item en el Menu Seleccion
				}
		}
		);
		
		return VariosItem2;

	}
	
	
	// ------------------------------------------------------------------------------------------------------
	
	// MENU "SELECCION"
	// ****************
	
	private JMenu getMenuSeleccion(){
		JMenu seleccionMenu = new JMenu("Seleccion");
		
		FyVItem = getFrutasYVerdurasItem();
		CarnesItem = getCarnesItem();
		PescadosItem = getPescadosItem();
		BebidasItem = getBebidasItem();
		VariosItem = getVariosItem();
		
		seleccionMenu.add(FyVItem);
		seleccionMenu.add(CarnesItem);
		seleccionMenu.add(PescadosItem);
		seleccionMenu.add(BebidasItem);
		seleccionMenu.add(VariosItem);
		return seleccionMenu;
	}
	
	private JMenuItem getFrutasYVerdurasItem(){
		JMenuItem FYVItem = new JMenuItem();
		FYVItem.setText("Frutas y Verduras");
		FYVItem.setEnabled(false); 	// Empieza deshabilitado hasta que se especifique un archivo
		
		Oyente oy = new Oyente();
		FYVItem.addActionListener(oy);

		return FYVItem;
	}
	
	private JMenuItem getCarnesItem(){
		JMenuItem CarnesItem = new JMenuItem();
		CarnesItem.setText("Carnes");
		CarnesItem.setEnabled(false); // Empieza deshabilitado
		
		Oyente oy = new Oyente();
		CarnesItem.addActionListener(oy);
		
		return CarnesItem;
	}
	
	private JMenuItem getPescadosItem(){
		JMenuItem PescadosItem = new JMenuItem();
		PescadosItem.setText("Pescados");
		PescadosItem.setEnabled(false); // Empieza deshabilitado
		
		Oyente oy = new Oyente();
		PescadosItem.addActionListener(oy);
		
		return PescadosItem;
	}
	
	private JMenuItem getBebidasItem(){
		JMenuItem BebidasItem = new JMenuItem();
		BebidasItem.setText("Bebidas");
		BebidasItem.setEnabled(false); // Empieza deshabilitado
		
		Oyente oy = new Oyente();
		BebidasItem.addActionListener(oy);
		
		return BebidasItem;
	}
	
	private JMenuItem getVariosItem(){
		JMenuItem VariosItem = new JMenuItem();
		VariosItem.setText("Varios");
		VariosItem.setEnabled(false); // Empieza deshabilitado
		
		Oyente oy = new Oyente();
		VariosItem.addActionListener(oy);
		
		return VariosItem;
	}
	
	// ------------------------------------------------------------------------------------------------------
	
	// MENU "CAJA"
	// ********************
	private JMenu getMenuCaja(){
		JMenu menuCaja = new JMenu("Caja");
		
		menuCaja.add(getPagoEfectivoVorazItem());
		menuCaja.add(getPagoEfectivoVAtrasItem());

		return menuCaja;
	}
	
	private JMenuItem getPagoEfectivoVorazItem(){
		JMenuItem pagoVorazItem = new JMenuItem();
		pagoVorazItem.setText("Pago Efectivo (Voraz)");
		
		pagoVorazItem.addActionListener(new ActionListener()
		{public void actionPerformed (ActionEvent e)
				{
				int total = caja.calculaFacturaTotal(tabFyV, tabCarnes, tabPescados, tabBebidas, tabVarios);
				String totalFactura = (total/100) + "." + (total%100);
				JOptionPane.showMessageDialog(null,"Total a abonar: "+ totalFactura + " €");
				// Modo 1 : Metodo Voraz - Modo 2: Metodo Vuelta-Atras
				caja.pagar(1,tabFyV, tabCarnes, tabPescados, tabBebidas, tabVarios);
				}
		}
		);

		return pagoVorazItem;
	}
	
	private JMenuItem getPagoEfectivoVAtrasItem(){
		JMenuItem pagoVAtrasItem = new JMenuItem();
		pagoVAtrasItem.setText("Pago Efectivo (Vuelta-Atrás)");
		
		pagoVAtrasItem.addActionListener(new ActionListener()
		{public void actionPerformed (ActionEvent e)
				{
			int total = caja.calculaFacturaTotal(tabFyV, tabCarnes, tabPescados, tabBebidas, tabVarios);
			String totalFactura = (total/100) + "." + (total%100);
			JOptionPane.showMessageDialog(null,"Total a abonar: "+ totalFactura + " €");
				// Modo 1 : Metodo Voraz - Modo 2(o culauiqera): Metodo Vuelta-Atras
				caja.pagar(2,tabFyV, tabCarnes, tabPescados, tabBebidas, tabVarios);
				}
		}
		);

		return pagoVAtrasItem;
	}
	
	
	
	// Consultoras para obtener las tablas
	// ***********************************
	
	private JPanel getPanelTabFyV() {
		try {
			sup.inicializarFyV(rutaFyV);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			JOptionPane.showMessageDialog(null,"Error cargando fichero! No se ha encontrado: "+ rutaFyV.getName());
		}
		
		DefaultTableModel dtm = new DefaultTableModel(0,3) {		// Creamos modelo de tabla por defecto
			// Sobrecargamos el metodo "isCellEditable" para impedir la edicion en cualquier col menos la 3 (cantidad)
			public boolean isCellEditable(int row,int column){
				if ((row == 0) || (column < 2))
					return false;
				else
					return true;
			}
	      };
		dtm.addRow(new String[]{"PRODUCTO","PRECIO","CANTIDAD"});	// Añadimos cabecera a la tabla
		Categoria FyV = sup.getCategoriaFyV();						// Cargar categoria FyV
		int n = FyV.getNumProductos();
		
		String nombre;
		String precio;
		String cantidad;
		
		int prec;
		int cant;
		
		for (int i = 0; i<n; i++){
			Producto p = FyV.getElemento(i);					// Sacar producto "i" de la categoria
			nombre = p.getNombre();								// Obtener nombre
			prec = p.getPrecio();								// Obtener precio
			cant = p.getCantidad();								// Obtener cantidad
			
			precio = String.valueOf(prec);						// Convertir int a String
			cantidad = String.valueOf(cant);					// Convertir int a String
			dtm.addRow(new String[]{nombre,precio,cantidad});	// Añadir primera fila de la tabla
			} 
			
		tabFyV = new JTable(dtm);								// Crear JTable a partir del dtm construido arriba
		JPanel panelTabla = new JPanel();						// Crear JPanel contenedor del JTable
		
		panelTabla.setLayout(new BorderLayout());				// Ajustar la posicion
		panelTabla.add(tabFyV,"Center");
		
		return panelTabla;

	}
	
	private JPanel getPanelTabCarnes() {
		try {
			sup.inicializarCarnes(rutaCarnes);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			JOptionPane.showMessageDialog(null,"Error cargando fichero! No se ha encontrado: "+ rutaCarnes.getName());
		}
		
		DefaultTableModel dtm = new DefaultTableModel(0,3) {		// Creamos modelo de tabla por defecto
			// Sobrecargamos el metodo "isCellEditable" para impedir la edicion en cualquier col menos la 3 (cantidad)
			public boolean isCellEditable(int row,int column){
				if ((row == 0) || (column < 2))
					return false;
				else
					return true;
			}
	      };
	      
		dtm.addRow(new String[]{"PRODUCTO","PRECIO","CANTIDAD"});			// Añadimos cabecera a la tabla
		Categoria Carnes = sup.getCategoriaCarnes();					// Cargar categoria Carnes
		int n = Carnes.getNumProductos();
		
		String nombre;
		String precio;
		String cantidad;
		
		int prec;
		int cant;
		
		for (int i = 0; i<n; i++){
			Producto p = Carnes.getElemento(i);					// Sacar producto "i" de la categoria
			nombre = p.getNombre();								// Obtener nombre
			prec = p.getPrecio();								// Obtener precio
			cant = p.getCantidad();								// Obtener cantidad
			
			precio = String.valueOf(prec);						// Convertir int a String
			cantidad = String.valueOf(cant);					// Convertir int a String
			dtm.addRow(new String[]{nombre,precio,cantidad});	// Añadir primera fila de la tabla
			} 
			
		tabCarnes = new JTable(dtm);							// Crear JTable a partir del dtm construido arriba
		JPanel panelTabla = new JPanel();						// Crear JPanel contenedor del JTable
		
		panelTabla.setLayout(new BorderLayout());				// Ajustar la posicion
		panelTabla.add(tabCarnes,"Center");
		
		return panelTabla;

	}
	
	private JPanel getPanelTabPescados() {
		try {
			sup.inicializarPescados(rutaPescados);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			JOptionPane.showMessageDialog(null,"Error cargando fichero! No se ha encontrado: "+ rutaPescados.getName());
		}
		
		DefaultTableModel dtm = new DefaultTableModel(0,3) {		// Creamos modelo de tabla por defecto
			// Sobrecargamos el metodo "isCellEditable" para impedir la edicion en cualquier col menos la 3 (cantidad)
			public boolean isCellEditable(int row,int column){
				if ((row == 0) || (column < 2))
					return false;
				else
					return true;
			}
	      };
	      
		dtm.addRow(new String[]{"PRODUCTO","PRECIO","CANTIDAD"});			// Añadimos cabecera a la tabla
		Categoria Pescados = sup.getCategoriaPescados();					// Cargar categoria Pescados
		int n = Pescados.getNumProductos();
		
		String nombre;
		String precio;
		String cantidad;
		
		int prec;
		int cant;
		
		for (int i = 0; i<n; i++){
			Producto p = Pescados.getElemento(i);					// Sacar producto "i" de la categoria
			nombre = p.getNombre();								// Obtener nombre
			prec = p.getPrecio();								// Obtener precio
			cant = p.getCantidad();								// Obtener cantidad
			
			precio = String.valueOf(prec);						// Convertir int a String
			cantidad = String.valueOf(cant);					// Convertir int a String
			dtm.addRow(new String[]{nombre,precio,cantidad});	// Añadir primera fila de la tabla
			} 
			
		tabPescados = new JTable(dtm);							// Crear JTable a partir del dtm construido arriba
		JPanel panelTabla = new JPanel();						// Crear JPanel contenedor del JTable
		
		panelTabla.setLayout(new BorderLayout());				// Ajustar la posicion
		panelTabla.add(tabPescados,"Center");
		
		return panelTabla;

	}
	
	private JPanel getPanelTabBebidas() {
		try {
			sup.inicializarBebidas(rutaBebidas);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			JOptionPane.showMessageDialog(null,"Error cargando fichero! No se ha encontrado: "+ rutaBebidas.getName());
		}
		
		DefaultTableModel dtm = new DefaultTableModel(0,3) {		// Creamos modelo de tabla por defecto
			// Sobrecargamos el metodo "isCellEditable" para impedir la edicion en cualquier col menos la 3 (cantidad)
			public boolean isCellEditable(int row,int column){
				if ((row == 0) || (column < 2))
					return false;
				else
					return true;
			}
	      };
	      
		dtm.addRow(new String[]{"PRODUCTO","PRECIO","CANTIDAD"});			// Añadimos cabecera a la tabla
		Categoria Bebidas = sup.getCategoriaBebidas();					// Cargar categoria Bebidas
		int n = Bebidas.getNumProductos();
		
		String nombre;
		String precio;
		String cantidad;
		
		int prec;
		int cant;
		
		for (int i = 0; i<n; i++){
			Producto p = Bebidas.getElemento(i);					// Sacar producto "i" de la categoria
			nombre = p.getNombre();								// Obtener nombre
			prec = p.getPrecio();								// Obtener precio
			cant = p.getCantidad();								// Obtener cantidad
			
			precio = String.valueOf(prec);						// Convertir int a String
			cantidad = String.valueOf(cant);					// Convertir int a String
			dtm.addRow(new String[]{nombre,precio,cantidad});	// Añadir primera fila de la tabla
			} 
			
		tabBebidas = new JTable(dtm);							// Crear JTable a partir del dtm construido arriba
		JPanel panelTabla = new JPanel();						// Crear JPanel contenedor del JTable
		
		panelTabla.setLayout(new BorderLayout());				// Ajustar la posicion
		panelTabla.add(tabBebidas,"Center");
		
		return panelTabla;

	}
	
	private JPanel getPanelTabVarios() {
		try {
			sup.inicializarVarios(rutaVarios);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			JOptionPane.showMessageDialog(null,"Error cargando fichero! No se ha encontrado: "+ rutaVarios.getName());
		}
		
		DefaultTableModel dtm = new DefaultTableModel(0,3) {		// Creamos modelo de tabla por defecto
			// Sobrecargamos el metodo "isCellEditable" para impedir la edicion en cualquier col menos la 3 (cantidad)
			public boolean isCellEditable(int row,int column){
				if ((row == 0) || (column < 2))
					return false;
				else
					return true;
			}
	      };
	      
		dtm.addRow(new String[]{"PRODUCTO","PRECIO","CANTIDAD"});			// Añadimos cabecera a la tabla
		Categoria Varios = sup.getCategoriaVarios();					// Cargar categoria Varios
		int n = Varios.getNumProductos();
		
		String nombre;
		String precio;
		String cantidad;
		
		int prec;
		int cant;
		
		for (int i = 0; i<n; i++){
			Producto p = Varios.getElemento(i);					// Sacar producto "i" de la categoria
			nombre = p.getNombre();								// Obtener nombre
			prec = p.getPrecio();								// Obtener precio
			cant = p.getCantidad();								// Obtener cantidad
			
			precio = String.valueOf(prec);						// Convertir int a String
			cantidad = String.valueOf(cant);					// Convertir int a String
			dtm.addRow(new String[]{nombre,precio,cantidad});	// Añadir primera fila de la tabla
			} 
			
		tabVarios = new JTable(dtm);							// Crear JTable a partir del dtm construido arriba
		JPanel panelTabla = new JPanel();						// Crear JPanel contenedor del JTable
		
		panelTabla.setLayout(new BorderLayout());				// Ajustar la posicion
		panelTabla.add(tabVarios,"Center");
		
		return panelTabla;

	}
	
	
	// ------------------------------------------------------------------------------------------------------
		// La clase Oyente controla los eventos de los JMenuItem del Menu "Seleccion"
		class Oyente implements ActionListener {


			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == FyVItem){
					JPanel panelFyV = getPanelTabFyV();							// Actualizar los datos de la tabla
					if (!anyadidoFyV)
					   panelTabulado.addTab("Frutas y Verduras",panelFyV);		// Si no esta insertada la pestaña, añadimos
					else {
					   int i = panelTabulado.indexOfTab("Frutas y Verduras");	// Sino, obtener el indice de la pestaña
					   panelTabulado.setComponentAt(i,panelFyV);					// Actualizar el JPanel de la pestaña "i"
					}
					anyadidoFyV = true;	
				} // if
				
				if (e.getSource() == CarnesItem){
					JPanel panelCarnes = getPanelTabCarnes();				// Actualizar los datos de la tabla
					if (!anyadidoCarnes)
					   panelTabulado.addTab("Carnes",panelCarnes);			// Si no esta insertada la pestaña, añadimos
					else {
					   int i = panelTabulado.indexOfTab("Carnes");			// Sino, obtener el indice de la pestaña
					   panelTabulado.setComponentAt(i,panelCarnes);			// Actualizar el JPanel de la pestaña "i"
					}
					anyadidoCarnes = true;	
				} // if
				
				if (e.getSource() == PescadosItem){
					JPanel panelPescados = getPanelTabPescados();					// Actualizar los datos de la tabla
					if (!anyadidoPescados)
					   panelTabulado.addTab("Pescados",panelPescados);		// Si no esta insertada la pestaña, añadimos
					else {
					   int i = panelTabulado.indexOfTab("Pescados");		// Sino, obtener el indice de la pestaña
					   panelTabulado.setComponentAt(i,panelPescados);			// Actualizar el JPanel de la pestaña "i"
					}
					anyadidoPescados = true;	
				} // if
				
				if (e.getSource() == BebidasItem){
					JPanel panelBebidas = getPanelTabBebidas();					// Actualizar los datos de la tabla
					if (!anyadidoBebidas)
					   panelTabulado.addTab("Bebidas",panelBebidas);		// Si no esta insertada la pestaña, añadimos
					else {
					   int i = panelTabulado.indexOfTab("Bebidas");		// Sino, obtener el indice de la pestaña
					   panelTabulado.setComponentAt(i,panelBebidas);			// Actualizar el JPanel de la pestaña "i"
					}
					anyadidoBebidas = true;	
				} // if
				
				if (e.getSource() == VariosItem){
					JPanel panelVarios = getPanelTabVarios();					// Actualizar los datos de la tabla
					if (!anyadidoVarios)
					   panelTabulado.addTab("Varios",panelVarios);		// Si no esta insertada la pestaña, añadimos
					else {
					   int i = panelTabulado.indexOfTab("Varios");		// Sino, obtener el indice de la pestaña
					   panelTabulado.setComponentAt(i,panelVarios);			// Actualizar el JPanel de la pestaña "i"
					}
					anyadidoVarios = true;	
				} // if
				
				
				
			} // ActionPerformed

		} // class Oyente

		

} // clasePrincipal
