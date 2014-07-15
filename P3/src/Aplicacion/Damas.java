package Aplicacion;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Interfaz.TableroFichas;

@SuppressWarnings("serial")
public class Damas extends JPanel implements ActionListener {

	/**
	 * @param args
	 */
	static int tamTablero;			// Almacena el tamaño del tablero
	JLabel etiquetaEstado;			// String que muestra el estado actual de la partida en el JLabel
	
	static int nivel_k;				// Max nivel del arbol de jugadas en partidas contra el PC
	
	public enum TModoJuego{Normal,HDefensiva,HAtaque,HHibrida};	
	
	static TModoJuego modoJuego;	// Almacena el modo de juego actual
	static JMenu menuOpciones;		// Para desactivarlo (si se esta jugando una partida) o activarlo (cuando no)
	
	static TableroFichas tablero;	// Tablero actual para resetearlo al iniciar nueva partida
	static JFrame app;				// Frame actual para reañadir el JPanel
	
	public static void main(String[] args) {
		
		app = new JFrame("Juego Damas");		// Creamos nuevo JFrame
		app.setVisible(true);					// Que sea visible
		app.setEnabled(true);					// Que este activado
		app.setSize(565,660);					// Seteamos el tamaño de la venta
		app.setResizable(false);				// Desactivamos posibilidad cambiar el tamño de la ventan
		app.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 	// Opcion para que el proceso se mate cuando se cierre la aplicación
		
		app.setJMenuBar(getMenuOpciones());		// Seteamos menu principal de la aplicacion
		tamTablero = 8;							// Tamaño por defecto del tablero (8x8)
		
		JComponent damas = new Damas();			// Creamos JPanel de Damas
		app.setContentPane(damas);				// Seteamos en el frame el panel principal el creado en el paso anterior
		app.revalidate();						// Refrescamos ventana (incluye "repaint()")
		
	}
	
	public static void reiniciarTablero(){
		JComponent damas = new Damas();			// Creamos un nuevo tablero con las opciones modificadas
		app.setContentPane(damas);				// Seteamos en el frame el panel principal el creado en el paso anterior
		app.revalidate();						// Refrescamos ventana (incluye "repaint()")
	}

	private static JMenuBar getMenuOpciones(){
		JMenuBar barraMenu = new JMenuBar();
		menuOpciones = new JMenu("Opciones de juego");
		
		// SUBMENU TAMAÑO TABLERO
		// *************************
		JMenu menuTam = new JMenu("Tamaño del tablero: ");
		
		JMenuItem tam8 = new JMenuItem("8x8");
		tam8.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) 
				{
				tamTablero = 8;
				reiniciarTablero();
				
				}
		}
		);
		
		JMenuItem tam10 = new JMenuItem("10x10");
		tam10.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) 
				{
				tamTablero = 10;
				reiniciarTablero();
			
				}
		}
		);
		
		JMenuItem tam12 = new JMenuItem("12x12");
		tam12.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) 
				{
				tamTablero = 12;
				reiniciarTablero();
			
				}
		}
		);
		
		menuTam.add(tam8);
		menuTam.add(tam10);
		menuTam.add(tam12);
		
		menuOpciones.add(menuTam);
			
		
		
		
		// SUBMENU MODO JUEGO: USR VS USR || USR VS PC
		// *************************
		JMenu menuModoJuego = new JMenu("Modo de Juego: ");
				
		JMenuItem modoUsrVSUsrItem = new JMenuItem("Usuario VS Usuario");
		modoUsrVSUsrItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) 
				{
				modoJuego = TModoJuego.Normal;
				tablero.cambiaModoJuego(modoJuego);
				}
		}
		);
		
		JMenu menuModoUsrVSPC = new JMenu("Usuario VS PC: ");
		
		JMenuItem heuristicaAtaqueItem = new JMenuItem("Heuristica Ataque");
		heuristicaAtaqueItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) 
				{
				modoJuego = TModoJuego.HAtaque;
				tablero.cambiaModoJuego(modoJuego);
				}
		}
		);
		
		JMenuItem heuristicaDefensaItem = new JMenuItem("Heuristica Defensa");
		heuristicaDefensaItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) 
				{
				modoJuego = TModoJuego.HDefensiva;
				tablero.cambiaModoJuego(modoJuego);
				}
		}
		);
		
		JMenuItem heuristicaHibridaItem = new JMenuItem("Heuristica Hibrida");
		heuristicaHibridaItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) 
				{
				modoJuego = TModoJuego.HHibrida;
				tablero.cambiaModoJuego(modoJuego);
				}
		}
		);
		
		menuModoUsrVSPC.add(heuristicaAtaqueItem);
		menuModoUsrVSPC.add(heuristicaDefensaItem);
		menuModoUsrVSPC.add(heuristicaHibridaItem);
		
		menuModoJuego.add(modoUsrVSUsrItem);
		menuModoJuego.add(menuModoUsrVSPC);
		
		menuOpciones.add(menuModoJuego);		
		
		
		JMenuItem nivelArbolItem = new JMenuItem("Configurar nivel arbol jugadas... ");
		nivelArbolItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) 
			{
			String stringNivel = JOptionPane.showInputDialog("Introduzca un valor entero para el nivel del arbol de jugadas:",JOptionPane.QUESTION_MESSAGE);
			nivel_k = Integer.parseInt(stringNivel);
			}
		}
		);
		

		menuOpciones.add(nivelArbolItem);
		
		barraMenu.add(menuOpciones);
		return barraMenu;
	}
	
	public Damas(){
			
		this.setLayout(new BorderLayout());				// Creamos nuevo layout para el JPanel Damas
	
		Image peonRojo = getToolkit().getImage("peonRojo.gif");			// Setear imagen para el peon rojo
		Image peonNegro = getToolkit().getImage("peonNegro.gif");		// Setear imagen para el peon negro
		Image damaRoja = getToolkit().getImage("damaRoja.gif");			// Setear imagen para la dama roja
		Image damaNegra = getToolkit().getImage("damaNegra.gif");		// Setear imagen para la dama negra
		etiquetaEstado = new JLabel("  > Pulse el boton para iniciar una nueva partida...");	// Setear display inicial
		modoJuego = TModoJuego.Normal;									// Modo por defecto: Normal
		nivel_k = 1;													// Nivel del arbol por defecto: 1
		tablero = new TableroFichas(tamTablero,etiquetaEstado,menuOpciones,modoJuego,nivel_k);	// Creamos el tablero con los parametros pedidos
		tablero.ponerImagenes(peonRojo,peonNegro,damaRoja,damaNegra);	// Insertamos las imagenes en un array de imagenes
		tablero.addMouseListener(tablero);								// Añadimos oyente al tablero para que reconozca los clics sobre el
		
		this.add(tablero,"Center");										// Seteamos el tablero en el centro
		
		JPanel panelEstado = new JPanel();								// Creamos nuevo panel para el display el boton de comienzo
		panelEstado.setLayout(new GridLayout(2,1));						// Seteamos layout para el panel de estado
		
		JButton botonIniciar = new JButton("Nuevo juego");				// Creamos el boton para iniciar el juego
		botonIniciar.addActionListener(this);							// Añadimos el oyente sobrecargado de la clase Damas (inicializacion)
		panelEstado.add(botonIniciar);									// Añadimos el boton al panel de estado
		
		
		//etiquetaEstado = new JLabel("  > Pulse el boton para iniciar una nueva partida...");
		panelEstado.add(etiquetaEstado);								// Añadimos el JLabel (display) al panel
		
		this.add(panelEstado,"South");									// Añadimos el panel de estado en la parte de abajo de la ventana
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		menuOpciones.setEnabled(false);						// Desactivamos el menu de opciones durante la partida
		tablero.inicializar(modoJuego,nivel_k); 			// Inicializamos la partida para reiniciar el juego
		JOptionPane.showMessageDialog(null, "Iniciado un nuevo juego!");	// Mostramos mensaje por pantalla
	}



	
} // class Damas
	
	
	

