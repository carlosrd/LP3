package gestioncaja;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

public class Caja {
	
	// Atributos
	// *********
	int cajaReg[];
	int cambio[];
	
	
	final static int max = 12;
	
	// Constructor
	// ***********
	public Caja(){
		cajaReg = new int[max];
		cambio = new int[max];
		for(int i=0;i<max;i++)
			cambio[i] += 0;
	}
	
	// Consultoras
	// ***********
	int getMonedasEn(int i) {return cajaReg[i];}
	//int getCantidadAPagar() {return cantidadAPagar;}
	
	// Metodos
	// *******
	
	// TAREAS DE LA CAJA
	// *****************
	
	// Inicializa la caja segun los valores del fichero f
	public void inicializar(File f) throws FileNotFoundException {
		Scanner sc = new Scanner(f);  // Scanner es un objeto que permite leer ficheros
		int i = 0;
		
		while (sc.hasNext()){						// 1ª linea: 50E / 2ª linea: 20E / ... / 13ª linea: 0.01E
			String cantMonedas = sc.nextLine();			// Leemos cantidad de monedas
			int c = Integer.parseInt(cantMonedas);		// Transformamos el la cantidad a "int"
			cajaReg[i] = c;								// Añadimos las monedas a la caja
			i++;										// Avanzamos de moneda en el vector
			}
		sc.close();									// Cerrar el fichero
	}
	
	// Llena cada apartado de monedas de la caja con 25 monedas mas
	public void llenar(){
		for(int i=0;i<max;i++)
			cajaReg[i] += 25;
	}
	
	// Deja cada apartado de monedas de la caja con solo 5 monedas
	public void vaciar(){
		for(int i=0;i<max;i++)
			cajaReg[i] = 5;
	}

	// Inicializar la interfaz para consultar la caja
	public void consultar(){
		new InterfazConsultar();
	}
	
	// Salva en un fichero el estado de la caja actual para cargarlo la proxima apertura
	public void salvarEnFichero(FileOutputStream f) {
		PrintWriter pw = new PrintWriter(f);		// PrintWriter sirve para escribir en fichero
		for(int i=0; i < max; i++)			
			pw.println(cajaReg[i]);			// Escribir en una linea las monedas en la posicion "i" de la caja
		pw.close();							// Cerramos el fichero
	}
	
	// ------------------------------------------------------------------------------------------------------------------
	
	// METODOS PARA EL PAGO Y DEVOLUCION DEL CAMBIO
	// ********************************************
	
	// Dadas las tablas de datos, calcula el total de la factura y abre la interfaz de pagar 
	public void pagar(int modo,JTable tabFyV,JTable tabCarnes,JTable tabPescados,JTable tabBebidas,JTable tabVarios) {
		int total = calculaFacturaTotal(tabFyV, tabCarnes, tabPescados, tabBebidas, tabVarios);
		new InterfazPagar(total,modo);
	}
	
	// Calcula la factura a pagar (de todas las tablas de datos)
	public int calculaFacturaTotal(JTable tabFyV,JTable tabCarnes, JTable tabPescados,
			JTable tabBebidas, JTable tabVarios){
		int cantidadAPagar = 0;

		// Si la tabla es "null" es que no se han pedido productos de esa categoria
		if (tabFyV != null) 			
			cantidadAPagar += calculaFacturaTabla(tabFyV);
		if (tabCarnes != null) 
			cantidadAPagar += calculaFacturaTabla(tabCarnes); 
		if (tabPescados != null) 
			cantidadAPagar += calculaFacturaTabla(tabPescados);
		if (tabBebidas != null) 
			cantidadAPagar += calculaFacturaTabla(tabBebidas); 
		if (tabVarios != null) 
			cantidadAPagar += calculaFacturaTabla(tabVarios);

		return cantidadAPagar;
	}

	// Calcula la cantidad a pagar por los elementos de una tabla
	private int calculaFacturaTabla(JTable tabla){

		int numProductos = tabla.getRowCount();
		int suma = 0;
		for(int i=1; i<numProductos; i++){
			String prec = (String) tabla.getValueAt(i,1);
			String cant = (String) tabla.getValueAt(i,2);

			int p = Integer.parseInt(prec);
			int c = 0;
			try {
				c = Integer.parseInt(cant);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,"Error al intentar leer la cantidad de: "+tabla.getValueAt(i,0)+
						"\nNo se ha añadido el producto a la cesta. \nCompruebe que ha escrito un numero entero");
			}

			suma += p*c;

		} // for

		return suma;
	}
	
	// Reinicia el vector de cambio (poner todas a 0)
	public void reiniciarCambio(){
		for(int i=0;i<max;i++)
			cambio[i] = 0;
	}
	
	// Suma el vector de cambio
	private int calculaCambioTotal(){
		int cambioTotal = 0;
		cambioTotal = cambio[0]*5000;
		cambioTotal += cambio[1]*2000;
		cambioTotal += cambio[2]*1000;
		cambioTotal += cambio[3]*500;
		cambioTotal += cambio[4]*200;
		cambioTotal += cambio[5]*100;
		cambioTotal += cambio[6]*50;
		cambioTotal += cambio[7]*20;
		cambioTotal += cambio[8]*10;
		cambioTotal += cambio[9]*5;
		cambioTotal += cambio[10]*2;
		cambioTotal += cambio[11]*1;
		
		return cambioTotal;
	}
	
	// Devuelve las monedas del vector de cambio a la caja registradora
	public void cancelarDarCambio(){
		for(int i=0;i<max;i++)
			cajaReg[i] += cambio[i];
	}
	
	// -----------------------------------------------------------------------------------------------------------------
	
	// INTERFACES AUXILIARES
	// *********************
	
	// Interfaz para consultar el dinero en caja
	@SuppressWarnings("serial")
	private class InterfazConsultar extends JFrame {
		
		
		public InterfazConsultar(){
			this.setVisible(true);
			this.setEnabled(true);
			this.setSize(250,325);
			
			this.setContentPane(getPanelPrincipal());
			this.setTitle("Estado de la caja");
			this.setResizable(false);
		
		}
		
		
		private JPanel getPanelPrincipal(){
			JPanel panelConsultarCaja = new JPanel();
			panelConsultarCaja.setLayout(new GridLayout(12,2));
			
			// Billetes de 50
			String monedas = String.valueOf(getMonedasEn(0));
			JTextField bill50 = new JTextField(monedas);
			bill50.setEnabled(false);
		
			panelConsultarCaja.add(new JLabel("   50 €: "));
			panelConsultarCaja.add(bill50);
			
			// Billetes de 20
			monedas = String.valueOf(getMonedasEn(1));
			JTextField bill20 = new JTextField(monedas);
			bill20.setEnabled(false);
		
			panelConsultarCaja.add(new JLabel("   20 €: "));
			panelConsultarCaja.add(bill20);
			
			
			// Billetes de 10
			monedas = String.valueOf(getMonedasEn(2));
			JTextField bill10 = new JTextField(monedas);
			bill10.setEnabled(false);
		
			panelConsultarCaja.add(new JLabel("   10 €: "));
			panelConsultarCaja.add(bill10);

			
			// Billetes de 5
			monedas = String.valueOf(getMonedasEn(3));
			JTextField bill5 = new JTextField(monedas);
			bill5.setEnabled(false);
		
			panelConsultarCaja.add(new JLabel("   5 €: "));
			panelConsultarCaja.add(bill5);
			
			
			// Monedas de 2
			monedas = String.valueOf(getMonedasEn(4));
			JTextField mon2 = new JTextField(monedas);
			mon2.setEnabled(false);
		
			panelConsultarCaja.add(new JLabel("   2 €: "));
			panelConsultarCaja.add(mon2);
		
			
			// Monedas de 1
			monedas = String.valueOf(getMonedasEn(5));
			JTextField mon1 = new JTextField(monedas);
			mon1.setEnabled(false);
		
			panelConsultarCaja.add(new JLabel("   1 €: "));
			panelConsultarCaja.add(mon1);
			
			
			// Monedas de 0.50
			monedas = String.valueOf(getMonedasEn(6));
			JTextField mon050 = new JTextField(monedas);
			mon050.setEnabled(false);
		
			panelConsultarCaja.add(new JLabel("   0.50 €: "));
			panelConsultarCaja.add(mon050);
			
			
			// Monedas de 0.20
			monedas = String.valueOf(getMonedasEn(7));
			JTextField mon020 = new JTextField(monedas);
			mon020.setEnabled(false);
		
			panelConsultarCaja.add(new JLabel("   0.20 €: "));
			panelConsultarCaja.add(mon020);
		
			
			// Monedas de 0.10
			monedas = String.valueOf(getMonedasEn(8));
			JTextField mon010 = new JTextField(monedas);
			mon010.setEnabled(false);
		
			panelConsultarCaja.add(new JLabel("   0.10 €: "));
			panelConsultarCaja.add(mon010);
			
			
			// Monedas de 0.05
			monedas = String.valueOf(getMonedasEn(9));
			JTextField mon005 = new JTextField(monedas);
			mon005.setEnabled(false);
		
			panelConsultarCaja.add(new JLabel("   0.05 €: "));
			panelConsultarCaja.add(mon005);
			
			
			// Monedas de 0.02
			monedas = String.valueOf(getMonedasEn(10));
			JTextField mon002 = new JTextField(monedas);
			mon002.setEnabled(false);
		
			panelConsultarCaja.add(new JLabel("   0.02 €: "));
			panelConsultarCaja.add(mon002);
			
			
			// Monedas de 0.01
			monedas = String.valueOf(getMonedasEn(11));
			JTextField mon001 = new JTextField(monedas);
			mon001.setEnabled(false);
		
			panelConsultarCaja.add(new JLabel("   0.01 €: "));
			panelConsultarCaja.add(mon001);
			
			return panelConsultarCaja;
		}
		} // class InterfazConsultarCaja

	// Interfaz para efectuar el pago (tanto por voraz como por BackTr)
	@SuppressWarnings("serial")
	private class InterfazPagar extends JFrame {
		
	// Atributos
	// **************************************************************************************************
		
		int cantidadAPagar;		// Cantidad a pagar por el cliente a la tienda
		int cambioADevolver;	// Cantidad de cambio a devolver al cliente
		int modo;				// Modo de devolucion del cambio: 1 > Voraz | 2 (o cualquiera) > BackTracking
		
		CajetinMonedas cajaOptima;		// Contenedores para almacenar los resultados del BackTr
		CajetinMonedas solucionOptima; 
		
		JTextField bill50;		// Campos de texto para introducir las monedas de pago
		JTextField bill20;
		JTextField bill10;
		JTextField bill5;
		JTextField mon2;
		JTextField mon1;
		JTextField mon050;
		JTextField mon020;
		JTextField mon010;
		JTextField mon005;
		JTextField mon002;
		JTextField mon001;
				


	// Constructora
	// *******************************************************************************************
		// Constructora: "total" es la cantidad a pagar / "m" es el modo (1 = voraz / 2 backTr)
		public InterfazPagar(int total,int m){
			this.setVisible(true);
			this.setEnabled(true);
			this.setSize(300,400);
			
			cantidadAPagar = total;
			cambioADevolver = 0;
			modo = m;
			
			cajaOptima = null;
			solucionOptima = null;
			
			this.setContentPane(getPanelPrincipal());
			this.setTitle("Pagar");
			this.setResizable(false);
	
		}
		
		// Devuelve el panel principal de la interfaz
		private JPanel getPanelPrincipal(){

			JPanel panelPagarCaja = new JPanel();			// Creamos el panel que contendra los campos de texto para recoger las monedas
			panelPagarCaja.setLayout(new GridLayout(16,3));		// Usamos GridLayout para colocarlos en una columna con sus etiquetas (JLabel)
			
		
			// Rellena el gridLayout con etiquetas (JLabel) y campos de texto (JTextfield) y al final el boton de pagar
			
			panelPagarCaja.add(new JLabel(" Total a pagar:"));
			panelPagarCaja.add(new JLabel(intToFloatString(cantidadAPagar)+" €"));
			panelPagarCaja.add(new JLabel(""));
			
			
			// Billetes de 50
			bill50 = new JTextField("0");
		
			panelPagarCaja.add(new JLabel("   50 €: "));
			panelPagarCaja.add(bill50);
			panelPagarCaja.add(new JLabel(" billetes"));
			
			// Billetes de 20
			bill20 = new JTextField("0");
		
			panelPagarCaja.add(new JLabel("   20 €: "));
			panelPagarCaja.add(bill20);
			panelPagarCaja.add(new JLabel(" billetes"));
			
			
			// Billetes de 10
			bill10 = new JTextField("0");
		
			panelPagarCaja.add(new JLabel("   10 €: "));
			panelPagarCaja.add(bill10);
			panelPagarCaja.add(new JLabel(" billetes"));

			
			// Billetes de 5
			bill5 = new JTextField("0");
		
			panelPagarCaja.add(new JLabel("   5 €: "));
			panelPagarCaja.add(bill5);
			panelPagarCaja.add(new JLabel(" billetes"));
			
			
			// Monedas de 2
			mon2 = new JTextField("0");
		
			panelPagarCaja.add(new JLabel("   2 €: "));
			panelPagarCaja.add(mon2);
			panelPagarCaja.add(new JLabel(" monedas"));
		
			
			// Monedas de 1
			mon1 = new JTextField("0");
		
			panelPagarCaja.add(new JLabel("   1 €: "));
			panelPagarCaja.add(mon1);
			panelPagarCaja.add(new JLabel(" monedas"));
			
			
			// Monedas de 0.50
			mon050 = new JTextField("0");
		
			panelPagarCaja.add(new JLabel("   0.50 €: "));
			panelPagarCaja.add(mon050);
			panelPagarCaja.add(new JLabel(" monedas"));
			
			
			// Monedas de 0.20
			mon020 = new JTextField("0");
		
			panelPagarCaja.add(new JLabel("   0.20 €: "));
			panelPagarCaja.add(mon020);
			panelPagarCaja.add(new JLabel(" monedas"));
		
			
			// Monedas de 0.10
			mon010 = new JTextField("0");
		
			panelPagarCaja.add(new JLabel("   0.10 €: "));
			panelPagarCaja.add(mon010);
			panelPagarCaja.add(new JLabel(" monedas"));
			
			
			// Monedas de 0.05
			mon005 = new JTextField("0");
		
			panelPagarCaja.add(new JLabel("   0.05 €: "));
			panelPagarCaja.add(mon005);
			panelPagarCaja.add(new JLabel(" monedas"));
			
			
			// Monedas de 0.02
			mon002 = new JTextField("0");
		
			panelPagarCaja.add(new JLabel("   0.02 €: "));
			panelPagarCaja.add(mon002);
			panelPagarCaja.add(new JLabel(" monedas"));
			
			
			// Monedas de 0.01
			mon001 = new JTextField("0");
		
			panelPagarCaja.add(new JLabel("   0.01 €: "));
			panelPagarCaja.add(mon001);
			panelPagarCaja.add(new JLabel(" monedas"));
			
			panelPagarCaja.add(new JLabel(""));
			panelPagarCaja.add(new JLabel(""));
			panelPagarCaja.add(new JLabel(""));
			
			panelPagarCaja.add(new JLabel(""));
			
			JButton botonPagar = new JButton("Pagar");
		
		// Dependiendo del modo ejecutado, el oyente ejecutara un algoritmo u otro
			
			if (modo == 1){
				botonPagar.addActionListener(new ActionListener()
				{public void actionPerformed (ActionEvent e)
						{
						String c = calculaCambioVoraz();			// Calculamos el cambio
						JOptionPane.showMessageDialog(null, c);		// Mostramos el cambio en un mensaje
						setVisible(false);							// Tras aceptar el cambio, "cerramos" ventana de pago
						}
				}
				);
			   } 
			else {
				botonPagar.addActionListener(new ActionListener()
				{public void actionPerformed (ActionEvent e)
						{
						String c = calculaCambioVAtras();			// Calculamos el cambio
						JOptionPane.showMessageDialog(null, c);		// Mostramos el cambio en un mensaje
						setVisible(false);							// Tras aceptar el cambio, "cerramos" ventana de pago
						}
				}
				);
				
			}
			
				
			panelPagarCaja.add(botonPagar);
			
			
			panelPagarCaja.add(new JLabel(""));
			panelPagarCaja.add(new JLabel(""));
			
		// Fin relleno GridLayout ------------------------------------------
			
		// Panel Principal
			JPanel panelPpal = new JPanel();				// Creamos el panel principal del Frame
			panelPpal.setLayout(new BorderLayout());		// Le asignamos BorderLayout
			panelPpal.add(new JLabel(" Selecciona las monedas para pagar:"),"North");	// Añadimos texto a la cabecera (North)
			panelPpal.add(panelPagarCaja,"Center");			// Añadimos el GridLayout al centro (Centro)
			
			
			return panelPpal;
			
		}
		
		// Devuelve la representacion en String de un int en su forma real
		private String intToFloatString(int x){
			int pEntera = x / 100;			// Calculamos la parte entera (dividir entre 100)
			int pDecimal = x % 100;			// Calculamos la parte decimal (resto de dividir entre 100)
			
			String num = pEntera + ".";		// Construimos el string con el numero real correspondiente
			if (pDecimal < 10)
				num += "0" + pDecimal;			// Si el numero es de un solo digito, debemos añadir un 0 delante para
			else								// que se visualice correctamente
				num += pDecimal;
			
			return num;
			
		}
	
		// Convierte el vector de cambio en un String para mostrar al usuario
		private String cambioToString(){
			
			String cambioFinal = "Su cambio: "+ intToFloatString(cambioADevolver) +
					" €\n\n50€: "+ cambio[0] +
					"\n20€: "+ cambio[1] +
					"\n10€: "+ cambio[2] +
					"\n5€: "+ cambio[3] +
					"\n2€: "+ cambio[4] +
					"\n1€: "+ cambio[5] +
					"\n0.50€: "+ cambio[6] +
			  		"\n0.20€: "+ cambio[7] +
			  		"\n0.10€: "+ cambio[8] +
			  		"\n0.05€: "+ cambio[9] +
			  		"\n0.02€: "+ cambio[10] +
			  		"\n0.01€: "+ cambio[11] +
			  		"\n\nGracias por su compra. \nHasta pronto!";
			
			return cambioFinal;
		}
	
		// -----------------------------------------------------------------------------------------------
		
	// METODOS PARA "ALGORITMO VORAZ"
	// *******************************************************************************************
		
		// Introduce las cantidades leidas en los campos de texto a la caja (se usa en ambos algoritmos)
		private void introduceDineroEnCaja(){ 

			try {
				cajaReg[0] += Integer.parseInt(bill50.getText());
				cajaReg[1] += Integer.parseInt(bill20.getText());
				cajaReg[2] += Integer.parseInt(bill10.getText());
				cajaReg[3] += Integer.parseInt(bill5.getText());
				cajaReg[4] += Integer.parseInt(mon2.getText());
				cajaReg[5] += Integer.parseInt(mon1.getText());
				cajaReg[6] += Integer.parseInt(mon050.getText());
				cajaReg[7] += Integer.parseInt(mon020.getText());
				cajaReg[8] += Integer.parseInt(mon010.getText());
				cajaReg[9] += Integer.parseInt(mon005.getText());
				cajaReg[10] += Integer.parseInt(mon002.getText());
				cajaReg[11] += Integer.parseInt(mon001.getText()); 
			} catch (Exception e){
				JOptionPane.showMessageDialog(null,"Error al leer el dinero introducido.\n" +
												   "Por favor, compruebe que ha introducido los datos correctamente\n" +
												   "Las cantidades deben ser numeros enteros");
			}
		}
		
		// Calcula el total de la cantidad introducida en los campos de texto (se usa en ambos algoritmos)
		private int calculaCantidadAbonada(){
			int cAbonada = 0;
			try {
				cAbonada += Integer.parseInt(bill50.getText())*5000;
				cAbonada += Integer.parseInt(bill20.getText())*2000;
				cAbonada += Integer.parseInt(bill10.getText())*1000;
				cAbonada += Integer.parseInt(bill5.getText())*500;
				cAbonada += Integer.parseInt(mon2.getText())*200;
				cAbonada += Integer.parseInt(mon1.getText())*100;
				cAbonada += Integer.parseInt(mon050.getText())*50;
				cAbonada += Integer.parseInt(mon020.getText())*20;
				cAbonada += Integer.parseInt(mon010.getText())*10;
				cAbonada += Integer.parseInt(mon005.getText())*5;
				cAbonada += Integer.parseInt(mon002.getText())*2;
				cAbonada += Integer.parseInt(mon001.getText()); 
			} catch (Exception e){
				JOptionPane.showMessageDialog(null,"Error al leer el dinero introducido.\n" +
												   "Por favor, compruebe que ha introducido los datos correctamente\n" +
												   "Las cantidades deben ser numeros enteros");
			}
			return cAbonada;
		}
		
		// Realiza (diferencia - moneda) tantas veces como se pueda: (dif >= moneda)
		private int quitaMonedas(int diferencia,int moneda,int i){
			while ((diferencia >= moneda) && (cajaReg[i] > 0)) {
				diferencia -= moneda;
				cambio[i]++;			// Introducimos en el cambio
				cajaReg[i]--;			// Sacamos de la caja
			}
			return diferencia;
		}
		
		// Algoritmo voraz para calcular el cambio a devolver
		private String calculaCambioVoraz(){
			int cAbonada = calculaCantidadAbonada();		// Calculamos la cantidad abonada (introducida en los campos de texto)
			cambioADevolver = cantidadAPagar - cAbonada;	// Calculamos el cambio que tenemos que devolver
			
			int diferencia = cambioADevolver;		// Copiamos el valor del atributo en diferencia, que sera el campo sobre el que operemos
			cambioADevolver *= -1;					// Si hay que devolver cambio, sera negativo, por lo que lo volvemos positivo
			String cambioFinal = null;				// String sobre el que recogeremos el mensaje con la solucion al cliente
			
			if (diferencia > 0) // Si diferencia positivo, es que falta dinero por abonar por parte del cliente
				cambioFinal = "Faltan por introducir: "+ intToFloatString(diferencia) + " €\nPor favor, introduzca más monedas";
			else {
				reiniciarCambio();								// Reiniciamos el vector que almacena el cambio
				diferencia *=-1;								// Volvemos positiva la diferencia
				diferencia = quitaMonedas(diferencia,5000,0);	// Calculamos ctas monedas de 50 se devuelven
				diferencia = quitaMonedas(diferencia,2000,1);	// Calculamos ctas monedas de 20 se devuelven
				diferencia = quitaMonedas(diferencia,1000,2);	// ...
				diferencia = quitaMonedas(diferencia,500,3);
				diferencia = quitaMonedas(diferencia,200,4);
				diferencia = quitaMonedas(diferencia,100,5);
				diferencia = quitaMonedas(diferencia,50,6);
				diferencia = quitaMonedas(diferencia,20,7);
				diferencia = quitaMonedas(diferencia,10,8);
				diferencia = quitaMonedas(diferencia,5,9);
				diferencia = quitaMonedas(diferencia,2,10);
				diferencia = quitaMonedas(diferencia,1,11);

				if (diferencia == 0){ // Si diferencia es 0, hemos podido devolver el cambio
		
					cambioFinal = cambioToString();	// Obtenemos la representacion String del vector cambio
					introduceDineroEnCaja();		// Introducimos el dinero pagado en la caja	
				}
				else {	// Sino, es que falta dinero por devolver (no hay monedas suficientes)
					cambioFinal = "La caja no dispone de cambio suficiente.\n"+
								  "Por favor, contacte con el gerente para llenar la caja e intente pagar de nuevo después";
					cancelarDarCambio();
				}
			} // else
			
			return cambioFinal;			  
		}
		
	// -----------------------------------------------------------------------------------------------
		
	// METODOS PARA "VUELTA-ATRAS"
	// ***************************
		
		// Dado el indice en el vector de monedas, te devuelve el valor real de la moneda Ej: 0 -> 50€, 1 -> 20€
		private int getValorMoneda(int moneda){
			switch(moneda){
			
			case 0 : moneda = 5000;
						break;
			case 1 : moneda = 2000;
						break;
			case 2 : moneda = 1000;
						break;
			case 3 : moneda = 500;
						break;
			case 4 : moneda = 200;
						break;
			case 5 : moneda = 100;
						break;
			case 6 : moneda = 50;
						break;
			case 7 : moneda = 20;
						break;
			case 8 : moneda = 10;
						break;
			case 9 : moneda = 5;
						break;
			case 10 : moneda = 2;
						break;
			case 11 : moneda = 1;
						break;
		} // switch
			return moneda;
		}
		
		// Backtracking
		private void backTr(CajetinMonedas solParcial, CajetinMonedas cajaParcial, int aPagar, int max){ // Pasamos el max para representar cual es la maxima moneda a probar
			
			for (int moneda = max; moneda<12; moneda++){
				int cont = 0;
				// PODA 1: Si no hay monedas del tipo solicitado en la caja, no probamos esa rama (solucion no posible)
				while (aPagar >= getValorMoneda(moneda) && cajaParcial.hayMonedas(moneda)){
					aPagar -= getValorMoneda(moneda);		// Restamos al cambio la moneda actual
					solParcial.meterMoneda(moneda);			// Anotamos una moneda en el vector de cambio
					cajaParcial.sacarMoneda(moneda);		// Sacamos una moneda de la caja
					cont++;									// Anotamos ctas veces podemos hacer los pasos anteriores
					}
				
				if (aPagar == 0 && solParcial.esMejor(solucionOptima)){		// Si es solucion final, y mejor que la Optima
					solucionOptima.setCajetin(solParcial.getCajetin());		// Asignamos Parcial a Optima
					cajaOptima.setCajetin(cajaParcial.getCajetin());
					}	
				  else {
					if (solParcial.esMejor(solucionOptima))				// PODA 2: Si la sol no es mejor, no llamar a BackTr
					   backTr(solParcial,cajaParcial,aPagar,moneda+1);	// Llamamos a BackTr con la moneda siguiente
					} // else	
				
				while (cont > 0){							// Deshacemos las pruebas hechas anteriormente
					solParcial.sacarMoneda(moneda);			// La moneda anotada al cambio, la desanotamos
					cajaParcial.meterMoneda(moneda);		// La moneda sacada de la caja, la volvemos a meter
					aPagar += getValorMoneda(moneda);		// Le sumamos la moneda al cambio a devolver pendiente
					cont--;									// Actualizamos ctas veces nos queda restablecer
					}
				} // for
	}
		
		// Algoritmo que calcula el cambio con BackTrackin
		private String calculaCambioVAtras(){
			
			
			CajetinMonedas cajaParcial = new CajetinMonedas(cajaReg);	// Inicializar datos Parciales
			CajetinMonedas solParcial = new CajetinMonedas();
			
			cajaOptima = new CajetinMonedas(cajaReg);		// Inicializar datos Optimos
			solucionOptima = new CajetinMonedas();				
			solucionOptima.inicializaNumElem();				// Inicializamos numElem en Optimo a MAX_INT para el primer caso de "esMejor"
	
			
			int cAbonada = calculaCantidadAbonada();		// Calculamos cuanto dinero ha introducido el cliente
			cambioADevolver = cantidadAPagar - cAbonada;	// Calculamos el cambio a devolver
			cambioADevolver *= -1;							// Si hay que devolver dinero, saldra negativo. Lo convertimos a positivo
			int diferencia = cambioADevolver;				// Copiamos el valor en "diferencia"
			String cambioFinal;								// String para devolver el cambio al cliente
			
			if (diferencia > 0){ // Si es positivo, hay que cambio que devolver 
				backTr(solParcial,cajaParcial,diferencia,0);	// Llamamos a BackTracking para calcular la solucion Optima
				
				reiniciarCambio();						// Reiniciamos el vector de cambio
				cambio = solucionOptima.getCajetin();	// Copiamos la solucionOptima en el vector de cambio
				
				if (calculaCambioTotal() != 0){			// Si el vector de cambio NO esta a 0, hay solucion
					cajaReg = cajaOptima.getCajetin();	// Copiamos la caja Optima para concordar los datos
					introduceDineroEnCaja();			// Introducimos en la caja el dinero pagado
					
					cambioFinal = cambioToString();		// Mostramos el cambio al usuario
					}
				else	// Si el vector de cambio esta a 0, BackTracking no ha encontrado solucion (no hay monedas suficientes)
					cambioFinal = "La caja no dispone de cambio suficiente.\nPor favor, contacte con el gerente para llenar la caja e intente pagar de nuevo después";
				}
			else {
				if (diferencia == 0) // Si "diferencia" es 0, no hay que devolver
					cambioFinal = "Su cambio: 0.00 € \n\nGracias por su compra. Hasta pronto!";
				  else // Si "diferencia" es negaivo, es que falta dinero por introducir
					cambioFinal = "Faltan por introducir: "+ intToFloatString(diferencia) + " €\nPor favor, introduzca más monedas";	
			}
			
			return cambioFinal;
			
		}
		
		
	// ------------------------------------------------------------------------------------------------
		

		} // class InterfazPagar






}
