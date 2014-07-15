package Movimiento;

import javax.swing.JOptionPane;

import Aplicacion.Damas.TModoJuego;

public class ComprobadorMovimiento extends TableroJuego {

	// ATRIBUTOS
	// *******************************************************************
	
	// Uso general
	// -----------
		boolean segundoClick;				// Distinguir entre clic en casilla origen y destino
		int filaOrigen;						// Determinan los valores del movimiento actual
		int filaDestino;
		int columnaOrigen;
		int columnaDestino;
		enum TipoJugador {Rojo,Negro};		// Jugadores posibles (PC = ROJAS)
		TipoJugador jugActual;				// Controla que jugador tiene el turno
		String displayString;				// Display que muestra el estado de la partida
		boolean esGanador;					// Decide si alguien ha ganado
		boolean seguirTirando;				// Si puede encadenar capturas
		TModoJuego modoJuego;				// Determina si partida Normal (U Vs U) o Heuristica (U Vs PC) 
	
	// Modo User VS PC
	// ---------------
		int nivel_k;							// Nivel del arbol de jugadas
		//Stack<MovimientoPC> pilaMultiAtaque;	// Pila que almacena los multiataques, para deshacerlos durante la recursion
		MovimientoPC ramaActual;			// Almacena la rama actual que se esta testeando (del primer nivel para el PC)
		int sumaH;							// Valor de la suma de la heuristica actual
		int filSig;							// Casillas siguientes para calcular Multiataque PC 
		int colSig;
	
	public ComprobadorMovimiento(int tam, TModoJuego modo,int nivel) {
		super(tam);		// Constructora de la clase padre
		
		nivel_k = nivel;
		modoJuego = modo;
		segundoClick = false;
		filaOrigen = -1;
		filaDestino = -1;
		columnaOrigen = -1;
		columnaOrigen = -1;
		jugActual = TipoJugador.Negro;
		displayString = "  > Pulse el boton para iniciar una nueva partida...";
		esGanador = false;
		seguirTirando = false;
		
	}
	
	// METODOS PARA TRATAMIENTOD DEL DISPLAY
	// ***************************************************************************
	public String getDisplay(){
		return displayString;
	}
	
	public String getDisplay2(){
		return "Modo de juego: "+heuristicaToString();
	}
	
	public String getDisplay3(){
		return "Maximo niveles arbol de jugadas: "+nivel_k;
	}
	
	public String heuristicaToString(){
		String h = null;
		switch (modoJuego){
			case Normal: h = "Normal (Usuario VS Usuario)";
						break;
			case HAtaque: h = "Heuristica Ataque (Usuario VS PC)";
						break;
			case HDefensiva: h = "Heuristica Defensa (Usuario VS PC)";
						break;
			case HHibrida: h = "Heuristica Hibrida (Usuario VS PC)";
						break;
		}
		
		return h;
	}
	
	// METODOS PARA EL MODO NORMAL (Usr VS Usr)
	// ****************************************************************************
	public boolean hayFichaDeJugadorEn(int fil,int col){
		
		boolean hayFicha = false;
		TFicha ficha = getCasilla(fil,col);			// Obtenemos ficha actual
		
		switch (ficha){
		
			case damaRoja :
			case peonRojo : if (jugActual == TipoJugador.Rojo)  // Si hay ficha roja y es turno del jugador rojo
								hayFicha = true;
							  else
								hayFicha = false;
							break;
			case damaNegra :
			case peonNegro : if (jugActual == TipoJugador.Negro) // Si hay ficha negra y es el turno del jugador negro
								hayFicha = true;
			 				  else
			 					hayFicha= false;
							 break;
		}
		
		return hayFicha;
	}
	
	public boolean procesarMovimiento(int fil,int col){
	
		if (!segundoClick) // Si es la primera vez que hace clic (casilla origen)
			if (hayFichaDeJugadorEn(fil,col)){	// Si en la primera casilla que pulsa hay ficha del jugador que tiene el turno
				
				filaOrigen = fil;				// Seteamos coordenadas de origen
				columnaOrigen = col;
				
				displayString = " > Casilla: "+fil+","+col+" seleccionada";	// Actualizamos display
				segundoClick = true;			// Activamos testigo del segundoClick (casilla de destino)
				}
			  else
			    displayString = " ! Error > No hay ficha del jugador "+jugadorToString()+" en la casilla seleccionada!";
		else { // Sino, es la segunda (casilla destino)
			filaDestino = fil;					// Seteamos coordendas de destino
			columnaDestino = col;
			
			boolean legal = comprobarMovimiento(fil,col);	// comprobamos y realizamos (si es posible) el movimeinto

			if (esGanador || (legal && !seguirTirando)){	// Si el jugador gano, hizo un movimiento legal o no tiene multiataque
			   segundoClick = false;
			   }
			}
			//}
		
		return esGanador;
	
}
	
	boolean comprobarMovimiento(int fil,int col){
		
		boolean legal = false;
	

		if (filaOrigen == filaDestino && columnaOrigen == columnaDestino){	// Si hace click sobre la casilla anterior
			displayString = " > Casilla: "+fil+","+col+" deseleccionada. Jugador "+jugadorToString()+", seleccione nueva casilla...";
			segundoClick = false;
			}
		else
		
		if (esCasillaOcupada())			// Si la casilla esta ocupada...
			displayString = " ! Error > Casilla ocupada! Seleccione una casilla colindante a "+filaOrigen+","+columnaOrigen;
		else
		
		if (!esMovimientoLegal())		// Si salta mas de una casilla
			displayString  = " ! Error > Mov. ilegal: Solo puede moverse de uno en uno. Seleccione una casilla colindante a "+filaOrigen+","+columnaOrigen;
		else
		
		if (esMovimientoRetroceder() && !esDamaFichaActual())			// Si retrocedemos y no es dama: Ilegal
			displayString = " ! Error > Mov. ilegal: Solo las damas pueden retroceder.Seleccione una casilla colindante a "+filaOrigen+","+columnaOrigen;
		else
		
		if (esMovimientoNormal(filaDestino,columnaDestino)){
			realizarMovimiento(fil,col,false);
			legal = true;
			}
		else
		
		if (esMovimientoAtaque(filaDestino,columnaDestino)){
			realizarMovimiento(fil,col,true);
			legal = true;
			}
			
		return legal;
	
	}
	
	void realizarMovimiento(int x, int y, boolean esAtaque){

		// 1 - Si es mov normal, poner ficha del jugador en la casilla destino
		if (!esAtaque) {
			setCasilla(filaDestino,columnaDestino,getCasilla(filaOrigen,columnaOrigen));
			setCasilla(filaOrigen,columnaOrigen,TFicha.Libre);
			}
		else {	// Sino, es un ataque
			setCasilla(filaDestino,columnaDestino,TFicha.Libre);	// Poner fichar de enmedio a 0
			setCasillaSiguiente();				// Colocamos la ficha en la siguiente a la capturada
			decrementarFichasAtacadas();		// Restar una ficha al contador de fichas del jugador atacado
			setCasilla(filaOrigen,columnaOrigen,TFicha.Libre);		// Seteamos el origen a Libre
			seguirTirando = hayAtaqueMultiple(filaDestino,columnaDestino); // Comprobamos si puede seguir atacando

		}
		
		// 2 - Si es final de tablero, coronamos dama
		if (esFinalDeTablero()){
			coronarDama();
			if (!hayJugadaContrincante()) // Si el contrincante esta bloqueado, no cambiamos turno
				displayString = " > Jugador "+contricanteToString()+" bloqueado! Sigue tirando jugador "+jugadorToString();// Sino, cambiamos turno
			else	
				cambiarTurno();	// Si no esta bloqueado, cambiamos turno
		}
		else
			
		// 4 - Si el jugador ha ganado, notificar y fin de la partida
		if (esGanador()) {
			displayString = " > Ha ganado el jugador "+jugadorToString()+"!!";
			JOptionPane.showMessageDialog(null, "Ha ganado el jugador "+jugadorToString());
			esGanador = true;
		}
		else
		
		// 5 - Si puede seguir tirando (ataque multiple)
		if (seguirTirando){ // Ahora la fila y columna destino es el origen y no se cambia el turno
			filaOrigen = filaDestino;
			columnaOrigen = columnaDestino;
		}
		else 	

			
		if (!hayJugadaContrincante()) // Si el contrincante esta bloqueado, no cambiamos turno
			displayString = " > Jugador "+contricanteToString()+" bloqueado! Sigue tirando jugador "+jugadorToString();// Sino, cambiamos turno
		else	
			cambiarTurno(); // Sino, si cambiamos turno
		
	}
		
	// -----------------------------------------------------------------------------------------------------
	// OTRAS TAREAS
	// ************
	void coronarDama(){ // Para ejecutar las jugadas tanto del usuario como del PC
		switch (jugActual){
		
			case Rojo : setCasilla(filaDestino,columnaDestino,TFicha.damaRoja);
						break;
			case Negro : setCasilla(filaDestino,columnaDestino,TFicha.damaNegra);
						break;
		}
		
	}
	
	void cambiarTurno(){
		
		switch (jugActual){
		
			case Rojo : jugActual = TipoJugador.Negro;
						break;
			case Negro : jugActual = TipoJugador.Rojo;
						break;
		}
		

		if (!esGanador && modoJuego == TModoJuego.Normal)	// Si no hay ganador, motramos String cambioturno;
			displayString = "Turno del jugador: "+jugadorToString();
		
		if (jugActual == TipoJugador.Rojo && modoJuego != TModoJuego.Normal)
			moverPC();
		
		filaOrigen = columnaOrigen = filaDestino = columnaDestino = -1; // Reiniciar coordenadas
	}
	
	void decrementarFichasAtacadas(){
		
		switch (jugActual){
		
			case Rojo : fichasNegras--;
						break;
			case Negro : fichasRojas--;
						break;
		}
	}
	
	void incrementarFichasAtacadas(TFicha ficha){ // Solo usado para deshacer jugadas simuladas (arbol)
		
		switch (ficha){
			case damaRoja:
			case peonRojo : fichasRojas++;
						break;
			case damaNegra:
			case peonNegro : fichasNegras++;
						break;
		}
	}
	
	String jugadorToString(){ // Segun el turno, devuelve un string con el color del jugador actual
		
		String jugador = "";
		switch (jugActual){
		
		case Rojo : jugador = "ROJO";
					break;
		case Negro : jugador = "NEGRO";
					break;
		}
		
		return jugador;
	}
	
	String contricanteToString(){ // Segun el turno, devuelve un string con el color del jugador oponente
		
		String jugador = "";
		switch (jugActual){
		
		case Rojo : jugador = "NEGRO";
					break;
		case Negro : jugador = "ROJO";
					break;
		}
		
		return jugador;
	}
	
	void setCasillaSiguiente(){ // Setea la ficha que ataca en la casilla siguiente libre a la captura

		if (filaOrigen-filaDestino == 1)  // Si es positivo va hacia arriba, sino, hacia abajo
			filaDestino--;
		else
			filaDestino++;
		
		if (columnaOrigen-columnaDestino == 1)	// Si es positivo va hacia la izq, sino, hacia la dcha
			columnaDestino--;
		else
			columnaDestino++;
		
		setCasilla(filaDestino,columnaDestino,getCasilla(filaOrigen,columnaOrigen));

	}
	
	public void reset(TModoJuego modo,int nivel){ // Inicia una nueva partida 
		modoJuego = modo;
		displayString = " > Nuevo juego comenzado! Comienza jugador "+jugadorToString();
		inicializar();
		segundoClick = false;
		filaOrigen = -1;
		filaDestino = -1;
		columnaOrigen = -1;
		columnaOrigen = -1;
		jugActual = TipoJugador.Negro;
		esGanador = false;
		seguirTirando = false;
		nivel_k = nivel;
	}
	
	public TModoJuego getModoJuego(){
		return modoJuego;
	}
	
	public void setModoJuego(TModoJuego modo){ // Cambia el modo de juego
		modoJuego = modo;
	}
	
	// -----------------------------------------------------------------------------------------------------
	// FUNCIONES PARA TRATAR TIPOS DE MOVIMIENTOS: NORMAL, ILEGAL,ATAQUE,BLOQUEO
	// *************************************************************************
	
	boolean estaEnRango(int filCol){ // Dada una fila o una columna, comprueba que este dentro del rango 0..tamTablero-1
		return (0 <= filCol && filCol <= tamTablero-1);
	}
	
	boolean esCasillaOcupada(){	// Comprueba si la casilla destino esta ocupada por una ficha del mismo jugador
								// o por la del otro jugador y no hay ataque (la casilla para terminar esta ocupada tb)
		boolean ocupada = false;
		TFicha fichaCasillaDestino = getCasilla(filaDestino,columnaDestino);
		switch (jugActual){
		
			case Rojo : if (fichaCasillaDestino  == TFicha.damaRoja ||
							fichaCasillaDestino  == TFicha.peonRojo ||
							((fichaCasillaDestino == TFicha.peonNegro || fichaCasillaDestino == TFicha.damaNegra) &&
							  !esMovimientoAtaque(filaDestino,columnaDestino)))
							ocupada = true;
						else
							ocupada = false;
						break;
						
			case Negro : if (fichaCasillaDestino  == TFicha.damaNegra ||
							 fichaCasillaDestino  == TFicha.peonNegro ||
							 ((fichaCasillaDestino == TFicha.peonRojo || fichaCasillaDestino == TFicha.damaRoja) &&
							   !esMovimientoAtaque(filaDestino,columnaDestino)))
							ocupada = true;
						else
							ocupada = false;
						break;
		}
		
		return ocupada;
	}
	
	boolean esGanador(){ // Comprueba si el jugador que tiene el turno ha ganado (las fichas del oponente son 0)
		
		boolean ganador = false;
		
		switch (jugActual){
		
			case Rojo : if (fichasNegras == 0)
							ganador = true;
						break;
			case Negro : if (fichasRojas == 0)
							ganador = true;
						break;
		}
		
		return ganador;
	}
	
	boolean hayJugadaContrincante(){ // Comprueba si el jugador contrario al actual esta bloqueado (no tiene jugada)
		
		TFicha ficha;
		int filAux;
		int filAux2;
		int colAux;
		int colAux2;
		int numJugadas = 0;
		boolean hayJugada= true;
		
		filSig = filaOrigen;
		colSig = filaOrigen;
		
		int filSig2 = filaDestino;
		int colSig2 = columnaDestino;
		
		switch (jugActual){ // Si es el jugActual esNegro, comprobamos si el rojo esta bloqueado y viceversa
			case Negro:	for (int filActual = 0; filActual < tamTablero && numJugadas == 0; filActual++){
							for (int colActual = 0; colActual < tamTablero && numJugadas == 0; colActual++){
								
								ficha = getCasilla(filActual,colActual);	// Obtenemos ficha actual
								filaOrigen = filActual;						// Seteamos origen
								columnaOrigen = colActual;
								
								switch (ficha){
								
									case peonRojo: filAux = filActual+1;	// Avanzamos una fila
											   	   colAux = colActual+1;	// A la dcha
											   	   colAux2 = colActual-1;	// A la izqda
											   	   
											   	   filaDestino = filAux;
											   	   columnaDestino = colAux;
											   	   
										   		   if (esMovAtaqueContrincante(filAux,colAux) || esMovimientoNormal(filAux,colAux))
										   			  numJugadas++;
										   		   
										   		   columnaDestino = colAux2;
										   		   
										   		   if (esMovAtaqueContrincante(filAux,colAux2) || esMovimientoNormal(filAux,colAux2))
										   			  numJugadas++; 
										   		   
											   	   break;
											   
									case damaRoja : filAux = filActual+1;	// Probamos mov en las 4 direcciones
													filAux2 = filActual-1;
								   	   				colAux = colActual+1;
								   	   				colAux2 = colActual-1;
												   	
								   	   				filaDestino = filAux;
												   	columnaDestino = colAux;
												   	
										   		   if (esMovAtaqueContrincante(filAux,colAux) || esMovimientoNormal(filAux,colAux))
										   			  numJugadas++;
										   		   
										   		   columnaDestino = colAux2;
										   		   
										   		   if (esMovAtaqueContrincante(filAux,colAux2) || esMovimientoNormal(filAux,colAux2))
										   			  numJugadas++; 
										   		   
										   		   filaDestino = filAux2;
										   		   
										   		   if (esMovAtaqueContrincante(filAux2,colAux2) || esMovimientoNormal(filAux2,colAux2))
										   			  numJugadas++; 
										   		   
										   		   columnaDestino = colAux;
										   		   
										   		   if (esMovAtaqueContrincante(filAux2,colAux) || esMovimientoNormal(filAux2,colAux))
										   			  numJugadas++; 

										   		   
										   		   
											   	   break;
								} // switch fichasRojas
							} // for
						} // for
						break;
						
			case Rojo:	for (int filActual = 0; filActual < tamTablero && numJugadas == 0; filActual++){
							for (int colActual = 0; colActual < tamTablero && numJugadas == 0; colActual++){
								
								ficha = getCasilla(filActual,colActual);
								filaOrigen = filActual;
								columnaOrigen = colActual;
								
								switch (ficha){
					
									case peonNegro: filAux = filActual-1;
								   	   				colAux = colActual+1;
								   	   				colAux2 = colActual-1;

												   	filaDestino = filAux;
												   	columnaDestino = colAux;
												   	   
											   		if (esMovAtaqueContrincante(filAux,colAux) || esMovimientoNormal(filAux,colAux))
											   			numJugadas++;
											   		   
											   		columnaDestino = colAux2;
											   		   
											   		if (esMovAtaqueContrincante(filAux,colAux2) || esMovimientoNormal(filAux,colAux2))
											   			numJugadas++; 
								   	   				
								   	   				break;
								   
									case damaNegra : filAux = filActual+1;
													 filAux2 = filActual-1;
													 colAux = colActual+1;
													 colAux2 = colActual-1;

									   	   			 filaDestino = filAux;
													 columnaDestino = colAux;
													   	
											   		 if (esMovAtaqueContrincante(filAux,colAux) || esMovimientoNormal(filAux,colAux))
											   			 numJugadas++;
											   		   
											   		 columnaDestino = colAux2;
											   		   
											   		 if (esMovAtaqueContrincante(filAux,colAux2) || esMovimientoNormal(filAux,colAux2))
											   			 numJugadas++; 
											   		   
											   		 filaDestino = filAux2;
											   		   
											   		 if (esMovAtaqueContrincante(filAux2,colAux2) || esMovimientoNormal(filAux2,colAux2))
											   			 numJugadas++; 
											   		   
											   		 columnaDestino = colAux;
											   		   
											   		 if (esMovAtaqueContrincante(filAux2,colAux) || esMovimientoNormal(filAux2,colAux))
											   			 numJugadas++; 
							   	   					 
								   	   				 break;
								} // switch fichasNegras
							} // for
						} // for
						break;
		} // switchJugador
		
		if (numJugadas == 0)		// Si el num de jugadas es 0, es que esta bloqueado
			hayJugada = false;
		
		filaOrigen = filSig;		// Reestablecemos atributos
		columnaOrigen = colSig;
		
		filaDestino = filSig2;
		columnaDestino = colSig2;
		
		return hayJugada;
			
	}
	
	boolean hayAtaqueMultiple(int filDst,int colDst){ // Comprueba si hay multicaptura (Solo en modo normal)
		
		TFicha ficha = getCasilla(filDst,colDst);
		int filAux;
		int filAux2;
		int colAux;
		int colAux2;
		
		int filOriginal = filaOrigen;
		int colOriginal = columnaOrigen;
		
		filaOrigen = filaDestino;			// Salvamos coord de Destino en coordenada auxiliar
		columnaOrigen = columnaDestino;
		
		boolean hayAtaque = false;
		
		switch (ficha){
		
			case peonNegro : filAux = filDst-1;
							 colAux = colDst+1;
							 colAux2 = colDst-1;
							
							filaDestino = filAux;
							columnaDestino = colAux;
							
							if (esMovimientoAtaque(filAux,colAux))
								hayAtaque = true;
							
							columnaDestino = colAux2;
							
							if (esMovimientoAtaque(filAux,colAux2))
								hayAtaque = true;
							
							break;

			case peonRojo : filAux = filDst+1;
							colAux = colDst+1;
							colAux2 = colDst-1;
							
							filaDestino = filAux;
							columnaDestino = colAux;
							
							if (esMovimientoAtaque(filAux,colAux))
								hayAtaque = true;
							
							columnaDestino = colAux2;
								
							if (esMovimientoAtaque(filAux,colAux2))
								hayAtaque = true;
							
							break;
							
			case damaNegra :
			case damaRoja : filAux = filDst+1;
							filAux2 = filDst-1;
							colAux = colDst+1;
							colAux2 = colDst-1;
							
							filaDestino = filAux;
							columnaDestino = colAux;
							
							if (esMovimientoAtaque(filAux,colAux))
								hayAtaque = true;
							
							columnaDestino = colAux2;
							
							if (esMovimientoAtaque(filAux,colAux2))
								hayAtaque = true;
							
							filaDestino = filAux2;	
							
							if (esMovimientoAtaque(filAux2,colAux2))
								hayAtaque = true;
							
							columnaDestino = colAux;
							
							if (esMovimientoAtaque(filAux2,colAux))
								hayAtaque = true;

							break;
		}
		
		filaDestino = filaOrigen;
		columnaDestino = columnaOrigen;
		
		filaOrigen = filOriginal;
		columnaOrigen = colOriginal;
		
		return hayAtaque;
	}
	
	boolean hayCasillaParaAcabar(){ // Comprueba si existe una casilla libre para acabar una captura 
		
		boolean hayCasilla = false;
		int restaFil = filaOrigen-filaDestino;
		int restaCol = columnaOrigen-columnaDestino;
		int filAux = -1;
		int colAux = -1;
		
		switch (restaFil) {
			case 1: filAux = filaDestino-1;		// Captura simple
					break;
			case -1: filAux = filaDestino+1;	// Captura simple
					break;
			case 2: filAux = filaDestino-2;		// Multicaptura XXX
					break;
			case -2: filAux = filaDestino+2; 	// Multicaptura XXX
					break;
		}
		
		switch (restaCol) {
			case 1: colAux = columnaDestino-1;	// Captura simple
					break;
			case -1: colAux = columnaDestino+1;	// Captura simple
				 	 break;
			case 2: colAux = columnaDestino+2; 	// Multicaptura XXX
					break;
			case -2: colAux = columnaDestino-2;	// Multicaptura XXX
				 	 break;

		}
		
		// Si la casilla esta dentro del tablero (existe) y esta libre, hay casilla
		if (estaEnRango(filAux) && estaEnRango(colAux) &&
			getCasilla(filAux,colAux) == TFicha.Libre)
			hayCasilla = true;
				
		return hayCasilla;
	}
	
	boolean esFinalDeTablero(){ // Comprueba si una ficha ha llegado al final del tablero (y es un peon, para coronarlo)
		
		boolean finalTab = false;
		TFicha fichaActual = getCasilla(filaDestino,columnaDestino);
		
		switch (jugActual){
			case Rojo : if (filaDestino == tamTablero-1 && fichaActual == TFicha.peonRojo)
							finalTab = true;
						break;
			case Negro :  if (filaDestino == 0 && fichaActual == TFicha.peonNegro)
						 	finalTab = true;
						break;
		}
		
		return finalTab;
	}
	
	boolean esMovimientoNormal(int filDst,int colDst){ // Dado unas coordenadas de destino, comprueba si es un mov normal
		
		boolean normal = false;
		
		if (estaEnRango(filDst) && estaEnRango(colDst) && getCasilla(filDst,colDst) == TFicha.Libre)
			normal = true;
		else
			normal = false;
		
		return normal;
	}
	
	boolean esDamaFichaActual(){ // Comprueba si la casilla actual (almacenada en los atributos) es una dama (Solo Modo Normal)
		return getCasilla(filaOrigen,columnaOrigen) == TFicha.damaNegra ||
				getCasilla(filaOrigen,columnaOrigen) == TFicha.damaRoja;
	}
	
	boolean esMovimientoAtaque(int filDst,int colDst){// Dado unas coord de destino, comprueba si es un mov ataque segun el turno actual
		// Necesita los atributos de coordenadas para funcionar
		boolean ataque = false;
		if (estaEnRango(filDst) && estaEnRango(colDst)){
			switch (jugActual){
			
				case Rojo : if ((getCasilla(filDst,colDst) == TFicha.damaNegra ||
								 getCasilla(filDst,colDst) == TFicha.peonNegro) &&	// Si la casilla a atacar es una ficha negra
								 hayCasillaParaAcabar())		// Y hay casilla para acabar el ataque
								 ataque = true;
							break;
							
				case Negro : if ((getCasilla(filDst,colDst) == TFicha.damaRoja ||
								 getCasilla(filDst,colDst) == TFicha.peonRojo) &&  // Si la casilla a atacar es una ficha roja
								 hayCasillaParaAcabar())		// Y hay casilla para acabar el ataque
								 ataque = true;
							break;
			} // switch
		} // if
		
		return ataque;
	}
	
	boolean esMovAtaqueContrincante(int filDst,int colDst){ // Dado unas coord de destino, comprueba si es un mov ataque del contrincante
		// Solo usado para comprobar si los jugadores tienen jugadas disponibles
		boolean ataque = false;
		if (estaEnRango(filDst) && estaEnRango(colDst)){
			switch (jugActual){
			
				case Negro : if ((getCasilla(filDst,colDst) == TFicha.damaNegra ||
								 getCasilla(filDst,colDst) == TFicha.peonNegro) &&
								 hayCasillaParaAcabar())
								 ataque = true;
							break;
							
				case Rojo : if ((getCasilla(filDst,colDst) == TFicha.damaRoja ||
								 getCasilla(filDst,colDst) == TFicha.peonRojo) &&
								 hayCasillaParaAcabar())
								 ataque = true;
							break;
			}
		}
		
		return ataque;
	}
	
	boolean esMovimientoRetroceder(){ // Comprueba si es un mov de retroceso (Legal solo en las damas)
		
		boolean retroceder = false;
		
		switch (jugActual){
		
			case Rojo : if (filaDestino == filaOrigen-1) 
							retroceder = true;
						break;
						
			case Negro : if (filaDestino == filaOrigen+1) 
							retroceder = true;
						break;
		}
		
		return retroceder;
	}
	
	boolean esMovimientoLegal(){ // Comprueba si es un mov legal (de no mas de una casilla y en diagonal)
		
		boolean legal = false;
		
		if ((filaDestino != filaOrigen+1 && filaDestino != filaOrigen-1) ||
			(columnaDestino != columnaOrigen+1 && columnaDestino != columnaOrigen-1))
			legal = false;
		else
			legal = true;
		
		return legal;
	}
	
	// -----------------------------------------------------------------------------------------------------
	// FUNCIONES PARA CALCULAR HEURISTICAS Y MOVIMIENTOS DEL ORDENADOR
	// *************************************************************************
	void moverPC(){ // Se encarga de procesar el turno del PC

		MovimientoPC m = null;
		sumaH = 0;						// Inicializar suma parcial de la heuristica
		m = pruebaJugadasPC(nivel_k,Integer.MAX_VALUE);	// Probamos jugadas al nivel pedido y nos devuelve la mejor segun la heuristica
		realizarJugadaPC(m);			// Realizamos jugada (similar al del modo normal, pero trata los multiataques)

	}
	
	/* ****************************
		> PODAS ALPHA BETA
	 *****************************

	ALPHA = Valor de CPU
	BETA  = Valor de USR

	PODA BETA (La hace el CPU) -> Como los valores de ALPHA nunca decrecen, podamos un nivel del ordenador
	si su valor es >= que el valor BETA que hayamos obtenido hasta el momento para sus antecesores

	PODA ALPHA (La hace el jugador) ->  Como los valores BETA nunca crecen, podamos un nivel del usuario
	si su valor es <= que el valor ALPHA que hayamos obtenido hasta el momento para sus antecesores
	*/
	
	MovimientoPC pruebaJugadasPC(int nivel, int BETA){ // Tantea las jugadas posibles para el PC en el nivel pedido y la heuristica elegida
		
		MovimientoPC m = new MovimientoPC(Integer.MIN_VALUE,-1,-1,-1,-1,-1,-1,TFicha.Libre); // Inicializamos a -infinito porque vamos a maximizar
		
		if (nivel <= 0 || esMovimientoFinal()){ // Caso base (esMovFinal: Si ha ganado el Usuario o esta bloqueado el PC)
			m.setNuevaSolucion(sumaH, ramaActual.getFilaOrigen(), ramaActual.getColumnaOrigen(), 
					   ramaActual.getFilaDestino(),ramaActual.getColumnaDestino(),
					   ramaActual.getFilaAtaque(),ramaActual.getColumnaAtaque(),
					   ramaActual.getFichaActual());
			return m;
		}	
		else	// Caso recursivo. Probar con todas las fichas del tablero y obtener la mejor jugada
			for(int fil = 0; fil < tamTablero && m.SolOptima() < BETA; fil++)	// XXX Poda Beta
				for(int col = 0; col < tamTablero && m.SolOptima() < BETA; col++){	// Poda BETA
					if (getCasilla(fil,col) == TFicha.peonRojo || getCasilla(fil,col) == TFicha.damaRoja)
					   switch (modoJuego){
							case HAtaque: pruebaHAtaque(fil,col,m,nivel);
										  break;
							case HDefensiva : pruebaHDefensa(fil,col,m,nivel);
											  break;
							case HHibrida : pruebaHHibrida(fil,col,m,nivel);
											break;
					   		} // switch
				} // for 2
		
		// Este paso es necesario si tras probar la heuristica defensiva o hibrida, no hay ninguna ficha que salvar
		// y cualquier movimiento posible con el resto no es seguro. Habra que realizar el primer movimiento 
		// disponible y exponer alguna ficha al peligro o dara al jugador rojo (PC) por bloqueado al no tener jugada
		if (modoJuego == TModoJuego.HDefensiva || modoJuego == TModoJuego.HHibrida)
			if (m.getFilaOrigen() == -1 && m.getColumnaOrigen() == -1)
				calculaPrimerMovPosible(m);
		
		return m;
	}
	
	MovimientoPC pruebaJugadasUsuario(int nivel, int ALPHA){ // Tantea las jugadas posibles para el Usuario en el nivel pedido y la heuristica elegida
		
		MovimientoPC m = new MovimientoPC(Integer.MAX_VALUE,-1,-1,-1,-1,-1,-1,TFicha.Libre); // Inicializamos a infinito porque vamos a minimizar
		
		if (nivel <= 0 || esMovimientoFinal()){ // Caso base (esMovFinal: Si ha ganado el PC o esta bloqueado el Usuario)
			m.setNuevaSolucion(sumaH, ramaActual.getFilaOrigen(), ramaActual.getColumnaOrigen(), 
							   ramaActual.getFilaDestino(),ramaActual.getColumnaDestino(),
							   ramaActual.getFilaAtaque(),ramaActual.getColumnaAtaque(),
							   ramaActual.getFichaActual());
			return m;
		}
		else // Caso recursivo. Probar todas las fichas del tablero y obtener la mejor
		for(int fil = 0; fil < tamTablero && m.SolOptima() > ALPHA; fil++) // XXX Poda ALPHA
			for(int col = 0; col < tamTablero && m.SolOptima() > ALPHA; col++){
				if (getCasilla(fil,col) == TFicha.peonNegro || getCasilla(fil,col) == TFicha.damaNegra)
				   switch (modoJuego){
						case HAtaque: pruebaHAtaque(fil,col,m,nivel);
									  break;
						case HDefensiva : pruebaHDefensa(fil,col,m,nivel);
										  break;
						case HHibrida : pruebaHHibrida(fil,col,m,nivel);
										break;
				   		} // switch
			} // for 2
		
		// Este paso es necesario si tras probar la heuristica defensiva o hibrida, no hay ninguna ficha que salvar
		// y cualquier movimiento posible con el resto no es seguro. Habra que realizar el primer movimiento 
		// disponible y exponer alguna ficha al peligro o dara al jugador rojo (PC) por bloqueado al no tener jugada
		if (modoJuego == TModoJuego.HDefensiva || modoJuego == TModoJuego.HHibrida)
			if (m.getFilaOrigen() == -1 && m.getColumnaOrigen() == -1)
				calculaPrimerMovPosible(m);
		
		return m;
	}
	
	void calculaCasillaSiguiente(int filO,int colO,int filD, int colD){ // Similar al del modo normal, pero sin tocar los atributos generales (solo los auxiliares)
		
		if (filO-filD == 1)
			filSig--;
		else
			filSig++;
		
		if (colO-colD == 1)
			colSig--;
		else
			colSig++;
	}
	
	void realizarJugadaPC(MovimientoPC m){ // Realiza la jugada final calculada por la heuristica
																
		filaOrigen = m.getFilaOrigen();			// Seteamos atributos
		columnaOrigen = m.getColumnaOrigen();
			
		filaDestino = m.getFilaDestino();
		columnaDestino = m.getColumnaDestino();
		
		if (filaOrigen != -1 && columnaOrigen  != -1){		// Si es un movimiento valido
			TFicha fichaActual = getCasilla(filaOrigen,columnaOrigen); // Obtenemos ficha actual
	
			setCasilla(filaDestino,columnaDestino,fichaActual);			// La copiamos en la casilla destino
			setCasilla(filaOrigen,columnaOrigen,TFicha.Libre);			// La borramos de la casilla origen
			displayString = " > (PC) : Normal desde "+filaOrigen+","+columnaOrigen+" a "+filaDestino+","+columnaDestino+". Turno del jugador NEGRO...";
			if (m.getFilaAtaque() != -1 && m.getColumnaAtaque() != -1){	// Si es un mov de ataque
				m.setFichaAtacada(getCasilla(m.getFilaAtaque(),m.getColumnaAtaque()));	// Seteamos al mov la ficha atacada
				setCasilla(m.getFilaAtaque(),m.getColumnaAtaque(),TFicha.Libre);		// La borramos del tablero
				decrementarFichasAtacadas();							// Quitamos una ficha al contador de fichas

				seguirTirando = hayAtaqueMultiplePC(filaDestino,columnaDestino,m.getFichaActual()); // Calculamos si hay multicaptura
				displayString = " > (PC) : Ataque desde "+filaOrigen+","+columnaOrigen+" a "+filaDestino+","+columnaDestino+". Turno del jugador NEGRO...";
				filaOrigen = m.getFilaOrigen();				// Reestablecer atributos despues de "hayMultipleAtaquePC()"
				columnaOrigen = m.getColumnaOrigen();
				
				filaDestino = m.getFilaDestino();
				columnaDestino = m.getColumnaDestino();
			}
			
			if (esFinalDeTablero()){
				coronarDama();
				if (esGanador()){
					displayString = " > Ha ganado el jugador "+jugadorToString()+"!!";
					JOptionPane.showMessageDialog(null, "Ha ganado el jugador "+jugadorToString());
					esGanador = true;
					}
				else
				if (!hayJugadaContrincante()) //esBloqueo()
					displayString = " > Jugador "+contricanteToString()+" bloqueado! Sigue tirando jugador "+jugadorToString();// Sino, cambiamos turno
				else	
					cambiarTurno();
			}
			else
				
			// 4 - Si el jugador ha ganado, notificar y fin de la partida
			if (esGanador()) {
				displayString = " > Ha ganado el jugador "+jugadorToString()+"!!";
				JOptionPane.showMessageDialog(null, "Ha ganado el jugador "+jugadorToString());
				esGanador = true;
			}
			else
			
			// 5 - Si puede seguir tirando (ataque multiple)
			if (seguirTirando){ // Ahora la fila y columna destino es el origen y no se cambia e turno
				displayString = " > (PC) : Multi-Ataque realizado sobre jugador ROJO. Turno del jugador NEGRO...";
				MovimientoPC m2 = calcularMultiAtaquePC(m.getFilaDestino(),m.getColumnaDestino());
				realizarJugadaPC(m2);
				
			}
			else 	
	
				
			if (!hayJugadaContrincante()){ //esBloqueo()
				displayString = " > Jugador "+contricanteToString()+" bloqueado! Sigue tirando jugador "+jugadorToString();// Sino, cambiamos turno
				moverPC();
			}
			else	
				cambiarTurno();
		}
		else {
			displayString = " > Jugador "+jugadorToString()+" bloqueado! Sigue tirando jugador "+contricanteToString();// Sino, cambiamos turno
			jugActual = TipoJugador.Negro;
		}

	}

	MovimientoPC calcularMultiAtaquePC(int filO,int colO) { // Dado una coord de origen, devuelve el mov de ataque disponible (null si no hay)
															// Usado solo para realizar multiataques del PC
		int filOriginal = filaOrigen;
		int colOriginal = columnaOrigen;
		
		int filOriginal2 = filaDestino;			// Salvamos coord de Destino en coordenada auxiliar
		int colOriginal2 = columnaDestino;
		
		int fil = filaOrigen = filO;
		int col = columnaOrigen = colO;
		
		TFicha ficha = getCasilla(fil,col);
		MovimientoPC MovAtaque = null;
		
		switch (jugActual){
			case Rojo : filaDestino = fil+1;
						columnaDestino = col+1;
						
						if (esMovimientoAtaque(fil+1,col+1)){
							filSig = fil+1;
							colSig = col+1;
							calculaCasillaSiguiente(fil,col,filSig,colSig);
							MovAtaque = new MovimientoPC(Integer.MIN_VALUE,fil,col,filSig,colSig,fil+1,col+1,ficha);
							MovAtaque.setFichaAtacada(getCasilla(fil+1,col+1));
						}
								
						columnaDestino = col-1;
						if (esMovimientoAtaque(fil+1,col-1)){
							filSig = fil+1;
							colSig = col-1;
							calculaCasillaSiguiente(fil,col,filSig,colSig);
							MovAtaque = new MovimientoPC(Integer.MIN_VALUE,fil,col,filSig,colSig,fil+1,col-1,ficha);
							MovAtaque.setFichaAtacada(getCasilla(fil+1,col-1));
						}
							
						filaDestino = fil-1;
						columnaDestino = col+1;
						
						if (ficha == TFicha.damaRoja){
							if (esMovimientoAtaque(fil-1,col+1)){
								filSig = fil-1;
								colSig = col+1;
								calculaCasillaSiguiente(fil,col,filSig,colSig);
								MovAtaque = new MovimientoPC(Integer.MIN_VALUE,fil,col,filSig,colSig,fil-1,col+1,ficha);
								MovAtaque.setFichaAtacada(getCasilla(fil-1,col+1));
							}
							
							columnaDestino = col-1;
							if (esMovimientoAtaque(fil-1,col-1)){
								filSig = fil-1;
								colSig = col-1;
								calculaCasillaSiguiente(fil,col,filSig,colSig);
								MovAtaque = new MovimientoPC(Integer.MIN_VALUE,fil,col,filSig,colSig,fil-1,col-1,ficha);
								MovAtaque.setFichaAtacada(getCasilla(fil-1,col-1));
								}
						}
						
						break;
						
			case Negro: filaDestino = fil-1;
						columnaDestino = col+1;
						
						if (esMovimientoAtaque(fil-1,col+1)){
							filSig = fil-1;
							colSig = col+1;
							calculaCasillaSiguiente(fil,col,filSig,colSig);
							MovAtaque = new MovimientoPC(Integer.MAX_VALUE,fil,col,filSig,colSig,fil-1,col+1,ficha);
							MovAtaque.setFichaAtacada(getCasilla(fil-1,col+1));
						}
								
						columnaDestino = col-1;
						if (esMovimientoAtaque(fil-1,col-1)){
							filSig = fil-1;
							colSig = col-1;
							calculaCasillaSiguiente(fil,col,filSig,colSig);
							MovAtaque = new MovimientoPC(Integer.MAX_VALUE,fil,col,filSig,colSig,fil-1,col-1,ficha);
							MovAtaque.setFichaAtacada(getCasilla(fil-1,col-1));
						}
							
						filaDestino = fil+1;
						columnaDestino = col+1;
						
						if (ficha == TFicha.damaNegra){
							if (esMovimientoAtaque(fil+1,col+1)){
								filSig = fil+1;
								colSig = col+1;
								calculaCasillaSiguiente(fil,col,filSig,colSig);
								MovAtaque = new MovimientoPC(Integer.MAX_VALUE,fil,col,filSig,colSig,fil+1,col+1,ficha);
								MovAtaque.setFichaAtacada(getCasilla(fil+1,col+1));
							}
							
							columnaDestino = col-1;
							if (esMovimientoAtaque(fil+1,col-1)){
								filSig = fil+1;
								colSig = col-1;
								calculaCasillaSiguiente(fil,col,filSig,colSig);
								MovAtaque = new MovimientoPC(Integer.MAX_VALUE,fil,col,filSig,colSig,fil+1,col-1,ficha);
								MovAtaque.setFichaAtacada(getCasilla(fil+1,col-1));
								}
						}	// if esDamaNegra
		} // switch
		
		filaOrigen = filOriginal;
		columnaOrigen = colOriginal;
		
		filaDestino = filOriginal2;
		columnaDestino = colOriginal2;
		
		return MovAtaque;
	}
	
	MovimientoPC calcularMultiAtaquePC(MovimientoPC m) { // Dado un mov, devuelve el mov de ataque disponible (null si no lo hay)
														// Usado solo para simular multiataque del usuario y del pc en modo heuristica
		int filOriginal = filaOrigen;
		int colOriginal = columnaOrigen;
		
		int filOriginal2 = filaDestino;			// Salvamos coord de Destino en coordenada auxiliar
		int colOriginal2 = columnaDestino;
		
		int fil = filaOrigen = m.getFilaDestino();
		int col = columnaOrigen = m.getColumnaDestino();
		
		TFicha ficha = m.getFichaActual();
		MovimientoPC MovAtaque = null;
		
		switch (jugActual){
			case Rojo : filaDestino = fil+1;
						columnaDestino = col+1;
						
						if (esMovimientoAtaque(fil+1,col+1)){
							filSig = fil+1;
							colSig = col+1;
							calculaCasillaSiguiente(fil,col,filSig,colSig);
							MovAtaque = new MovimientoPC(Integer.MIN_VALUE,fil,col,filSig,colSig,fil+1,col+1,ficha);
							MovAtaque.setFichaAtacada(getCasilla(fil+1,col+1));
						}
								
						columnaDestino = col-1;
						if (esMovimientoAtaque(fil+1,col-1)){
							filSig = fil+1;
							colSig = col-1;
							calculaCasillaSiguiente(fil,col,filSig,colSig);
							MovAtaque = new MovimientoPC(Integer.MIN_VALUE,fil,col,filSig,colSig,fil+1,col-1,ficha);
							MovAtaque.setFichaAtacada(getCasilla(fil+1,col-1));
						}
							
						filaDestino = fil-1;
						columnaDestino = col+1;
						
						if (ficha == TFicha.damaRoja){
							if (esMovimientoAtaque(fil-1,col+1)){
								filSig = fil-1;
								colSig = col+1;
								calculaCasillaSiguiente(fil,col,filSig,colSig);
								MovAtaque = new MovimientoPC(Integer.MIN_VALUE,fil,col,filSig,colSig,fil-1,col+1,ficha);
								MovAtaque.setFichaAtacada(getCasilla(fil-1,col+1));
							}
							
							columnaDestino = col-1;
							if (esMovimientoAtaque(fil-1,col-1)){
								filSig = fil-1;
								colSig = col-1;
								calculaCasillaSiguiente(fil,col,filSig,colSig);
								MovAtaque = new MovimientoPC(Integer.MIN_VALUE,fil,col,filSig,colSig,fil-1,col-1,ficha);
								MovAtaque.setFichaAtacada(getCasilla(fil-1,col-1));
								}
						}
						
						break;
						
			case Negro: filaDestino = fil-1;
						columnaDestino = col+1;
						
						if (esMovimientoAtaque(fil-1,col+1)){
							filSig = fil-1;
							colSig = col+1;
							calculaCasillaSiguiente(fil,col,filSig,colSig);
							MovAtaque = new MovimientoPC(Integer.MAX_VALUE,fil,col,filSig,colSig,fil-1,col+1,ficha);
							MovAtaque.setFichaAtacada(getCasilla(fil-1,col+1));
						}
								
						columnaDestino = col-1;
						if (esMovimientoAtaque(fil-1,col-1)){
							filSig = fil-1;
							colSig = col-1;
							calculaCasillaSiguiente(fil,col,filSig,colSig);
							MovAtaque = new MovimientoPC(Integer.MAX_VALUE,fil,col,filSig,colSig,fil-1,col-1,ficha);
							MovAtaque.setFichaAtacada(getCasilla(fil-1,col-1));
						}
							
						filaDestino = fil+1;
						columnaDestino = col+1;
						
						if (ficha == TFicha.damaNegra){
							if (esMovimientoAtaque(fil+1,col+1)){
								filSig = fil+1;
								colSig = col+1;
								calculaCasillaSiguiente(fil,col,filSig,colSig);
								MovAtaque = new MovimientoPC(Integer.MAX_VALUE,fil,col,filSig,colSig,fil+1,col+1,ficha);
								MovAtaque.setFichaAtacada(getCasilla(fil+1,col+1));
							}
							
							columnaDestino = col-1;
							if (esMovimientoAtaque(fil+1,col-1)){
								filSig = fil+1;
								colSig = col-1;
								calculaCasillaSiguiente(fil,col,filSig,colSig);
								MovAtaque = new MovimientoPC(Integer.MAX_VALUE,fil,col,filSig,colSig,fil+1,col-1,ficha);
								MovAtaque.setFichaAtacada(getCasilla(fil+1,col-1));
								}
						}	// if esDamaNegra
		} // switch
		
		filaOrigen = filOriginal;
		columnaOrigen = colOriginal;
		
		filaDestino = filOriginal2;
		columnaDestino = colOriginal2;
		
		return MovAtaque;
	}

	boolean hayAtaqueMultiplePC(int filDst,int colDst,TFicha ficha){ // Calcula si existe multicaptura (Solo para Heuristica de Ataque e Hibrida)
		
		//TFicha ficha = getCasilla(filDst,colDst);
		int filAux;
		int filAux2;
		int colAux;
		int colAux2;
		
		int filOriginal = filaOrigen;
		int colOriginal = columnaOrigen;
		
		int filOriginal2 = filaDestino;			// Salvamos coord de Destino en coordenada auxiliar
		int colOriginal2 = columnaDestino;
		
		boolean hayAtaque = false;
		
		switch (ficha){
			case peonNegro : filAux = filDst-1;
							colAux = colDst+1;
							colAux2 = colDst-1;
							
							filaOrigen = filDst;
							columnaOrigen = colDst;
							
							filaDestino = filAux;
							columnaDestino = colAux;
					
							if (esMovimientoAtaque(filAux,colAux))
								hayAtaque = true;
							
							columnaDestino = colAux2;	
							
							if (esMovimientoAtaque(filAux,colAux2))
								hayAtaque = true;
							
							break;
							
			case peonRojo : filAux = filDst+1;
							colAux = colDst+1;
							colAux2 = colDst-1;
							
							filaOrigen = filDst;
							columnaOrigen = colDst;
							
							filaDestino = filAux;
							columnaDestino = colAux;
					
							if (esMovimientoAtaque(filAux,colAux))
								hayAtaque = true;
							
							columnaDestino = colAux2;	
							
							if (esMovimientoAtaque(filAux,colAux2))
								hayAtaque = true;
							
							break;
							
			case damaNegra :
			case damaRoja : filAux = filDst+1;
							filAux2 = filDst-1;
							colAux = colDst+1;
							colAux2 = colDst-1;
							
							filaOrigen = filDst;
							columnaOrigen = colDst;
							
							filaDestino = filAux;
							columnaDestino = colAux;
					
							if (esMovimientoAtaque(filAux,colAux))
								hayAtaque = true;
							
							columnaDestino = colAux2;	
					
							if (esMovimientoAtaque(filAux,colAux2))
								hayAtaque = true;
							
							filaDestino = filAux2;
							
							if (esMovimientoAtaque(filAux2,colAux2))
								hayAtaque = true;
							
							columnaDestino = colAux;
					
							if (esMovimientoAtaque(filAux2,colAux))
								hayAtaque = true;
					
					
							break;
		}
		
		filaOrigen = filOriginal;
		columnaOrigen = colOriginal;
		
		filaDestino = filOriginal2;
		columnaDestino = colOriginal2;
		
		return hayAtaque;
	}

	boolean esFinalDeTablero(MovimientoPC m){ // Dado un mov, calcula si es un mov a la fila del final (para coronar si es un peon)
		
		boolean finalTab = false;
		TFicha fichaActual = m.getFichaActual();
		switch (fichaActual){
			case peonRojo : if (m.getFilaDestino() == tamTablero-1)
							finalTab = true;
						break;
			case peonNegro :  if (m.getFilaDestino() == 0)
						 	finalTab = true;
						break;
		}
		
		return finalTab;
	}
	
	void coronarDama(MovimientoPC m){ // Dado un mov, corona la ficha si es un peon
		
		TFicha fichaActual = m.getFichaActual();
		
		switch (fichaActual){
		
			case peonRojo : setCasilla(m.getFilaDestino(),m.getColumnaDestino(),TFicha.damaRoja);
						break;
			case peonNegro : setCasilla(m.getFilaDestino(),m.getColumnaDestino(),TFicha.damaNegra);
						break;
		}
		
	}
	
	// HEURISTICA ATAQUE
	// *************************************************************************
	void pruebaHAtaque(int fil,int col, MovimientoPC m, int nivel){ // Prueba la HAtaque para la ficha en "fil,col"
		// El nivel y m, son necesarios para la recursion
		TFicha fichaActual = getCasilla(fil,col);
		int filAux;
		int filAux2;
		int colAux;
		int colAux2;
		
		//int filSig;		// Casillas siguientes para calcular Multiataque 
		//int colSig;
		
		seguirTirando = false;
		
		switch(fichaActual){
			case peonRojo: filAux = fil+1;
						   colAux = col+1;
					   	   colAux2 = col-1;
					   	   
					   	   // 1 - Probamos movimiento NORMAL con peon rojo
					   	   //     *******************************************
					   	   filaOrigen = fil;
						   columnaOrigen = col;
						   
						   filaDestino = filAux;
						   columnaDestino = colAux;
						   
					   	   if (esMovimientoNormal(filAux,colAux))
					   		   HAtaque_ejecutaMovNormalPC(fil,col,filAux,colAux,m,nivel);  
					   	   
					   	   // Restablecemos todas por "hayAtaqueMultiplePC()"
					   	   filaOrigen = fil;
						   columnaOrigen = col;
						   
						   filaDestino = filAux;
						   columnaDestino = colAux2;
						   
					   	   if (esMovimientoNormal(filAux,colAux2))
					   		   HAtaque_ejecutaMovNormalPC(fil,col,filAux,colAux2,m,nivel); 
					   	   
					   	   
					   	   // 2 - Probamos movimiento de ATAQUE con peon rojo
					   	   //     *******************************************
					   	   filaOrigen = fil;
						   columnaOrigen = col;
						   
						   filaDestino = filAux;
						   columnaDestino = colAux;
							
					   	   if (esMovimientoAtaque(filAux,colAux))
					   		   HAtaque_ejecutaMovAtaquePC(fil,col,filAux,colAux,m,nivel); 
					   	   
					   	   filaOrigen = fil;
						   columnaOrigen = col;
						   
						   filaDestino = filAux;
						   columnaDestino = colAux2;
					   	   
					   	   if (esMovimientoAtaque(filAux,colAux2))
					   		   HAtaque_ejecutaMovAtaquePC(fil,col,filAux,colAux2,m,nivel);  
					   	   
					   	   break;
					   	   
			case peonNegro: filAux = fil-1;
						   colAux = col+1;
					   	   colAux2 = col-1;
					   	   
					   	   // 1 - Probamos movimiento NORMAL con peon negro (Usuario)
					   	   //     *******************************************
					   	   filaOrigen = fil;
						   columnaOrigen = col;
						   
						   filaDestino = filAux;
						   columnaDestino = colAux;
						   
					   	   if (esMovimientoNormal(filAux,colAux))
					   		   HAtaque_ejecutaMovNormalUsuario(fil,col,filAux,colAux,m,nivel);
					   	   
					   	   // Restablecemos todas por "hayAtaqueMultiplePC()"
					   	   filaOrigen = fil;
						   columnaOrigen = col;
						   
						   filaDestino = filAux;
						   columnaDestino = colAux2;
						   
					   	   if (esMovimientoNormal(filAux,colAux2))
					   		   HAtaque_ejecutaMovNormalUsuario(fil,col,filAux,colAux2,m,nivel);
					   	   
					   	   
					   	   // 2 - Probamos movimiento de ATAQUE con peon negro (Usuario)
					   	   //     *******************************************
					   	   filaOrigen = fil;
						   columnaOrigen = col;
						   
						   filaDestino = filAux;
						   columnaDestino = colAux;
							
					   	   if (esMovimientoAtaque(filAux,colAux))
					   		   HAtaque_ejecutaMovAtaqueUsuario(fil,col,filAux,colAux,m,nivel);
					   	   
					   	   filaOrigen = fil;
						   columnaOrigen = col;
						   
						   filaDestino = filAux;
						   columnaDestino = colAux2;
					   	   
					   	   if (esMovimientoAtaque(filAux,colAux2))
					   		   HAtaque_ejecutaMovAtaqueUsuario(fil,col,filAux,colAux2,m,nivel);
					   	   
					   	   break;
					   	   
			case damaRoja : filAux = fil-1;
							filAux2 = fil+1;
							colAux = col+1;
							colAux2 = col-1;
							
						   	// 1 - Probamos movimiento NORMAL con dama roja
						   	//     *******************************************
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							   
							filaDestino = filAux;
							columnaDestino = colAux;
							
						   	if (esMovimientoNormal(filAux,colAux))
						   		HAtaque_ejecutaMovNormalPC(fil,col,filAux,colAux,m,nivel);  
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							   
							filaDestino = filAux;
							columnaDestino = colAux2;
						   	
						   	if (esMovimientoNormal(filAux,colAux2))
						   		HAtaque_ejecutaMovNormalPC(fil,col,filAux,colAux2,m,nivel); 
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							
							filaDestino = filAux2;
							columnaDestino = colAux;
						   	
						   	if (esMovimientoNormal(filAux2,colAux))
						   		HAtaque_ejecutaMovNormalPC(fil,col,filAux2,colAux,m,nivel);   
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							   
							filaDestino = filAux2;
							columnaDestino = colAux2;
						   	
						   	if (esMovimientoNormal(filAux2,colAux2))
						   		HAtaque_ejecutaMovNormalPC(fil,col,filAux2,colAux2,m,nivel); 
						 							
							// 2 - Probamos movimiento de ATAQUE con dama rojo (PC)
						   	//     *******************************************
						   	filaOrigen = fil;
							columnaOrigen = col;   
							
							filaDestino = filAux;
							columnaDestino = colAux;
							
						   	if (esMovimientoAtaque(filAux,colAux))
						   		HAtaque_ejecutaMovAtaquePC(fil,col,filAux,colAux,m,nivel);  
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							   
							filaDestino = filAux2;
							columnaDestino = colAux;
						   	
						   	if (esMovimientoAtaque(filAux2,colAux))
						   		HAtaque_ejecutaMovAtaquePC(fil,col,filAux2,colAux,m,nivel);
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							
						   	columnaDestino = colAux2;
						   	filaDestino = filAux;
						   	
						   	if (esMovimientoAtaque(filAux,colAux2))
						   		HAtaque_ejecutaMovAtaquePC(fil,col,filAux,colAux2,m,nivel);
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							
						   	filaDestino = filAux2;
						   	columnaDestino = colAux2;
						   	
						   	if (esMovimientoAtaque(filAux2,colAux2))
						   		HAtaque_ejecutaMovAtaquePC(fil,col,filAux2,colAux2,m,nivel);
							    
					
						   	break;	
						   	
			case damaNegra : filAux = fil-1;
							filAux2 = fil+1;
							colAux = col+1;
							colAux2 = col-1;
							
						   	// 1 - Probamos movimiento NORMAL con dama roja
						   	//     *******************************************
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							   
							filaDestino = filAux;
							columnaDestino = colAux;
							
						   	if (esMovimientoNormal(filAux,colAux))
						   		HAtaque_ejecutaMovNormalUsuario(fil,col,filAux,colAux,m,nivel);
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							   
							filaDestino = filAux;
							columnaDestino = colAux2;
						   	
						   	if (esMovimientoNormal(filAux,colAux2))
						   		HAtaque_ejecutaMovNormalUsuario(fil,col,filAux,colAux2,m,nivel);
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							
							filaDestino = filAux2;
							columnaDestino = colAux;
						   	
						   	if (esMovimientoNormal(filAux2,colAux))
						   		HAtaque_ejecutaMovNormalUsuario(fil,col,filAux2,colAux,m,nivel);   
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							   
							filaDestino = filAux2;
							columnaDestino = colAux2;
						   	
						   	if (esMovimientoNormal(filAux2,colAux2))
						   		HAtaque_ejecutaMovNormalUsuario(fil,col,filAux2,colAux2,m,nivel);
						 							
							// 2 - Probamos movimiento de ATAQUE con peon rojo
						   	//     *******************************************
						   	filaOrigen = fil;
							columnaOrigen = col;   
							
							filaDestino = filAux;
							columnaDestino = colAux;
							
						   	if (esMovimientoAtaque(filAux,colAux))
						   		HAtaque_ejecutaMovAtaqueUsuario(fil,col,filAux,colAux,m,nivel);
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							   
							filaDestino = filAux2;
							columnaDestino = colAux;
						   	
						   	if (esMovimientoAtaque(filAux2,colAux))
						   		HAtaque_ejecutaMovAtaqueUsuario(fil,col,filAux2,colAux,m,nivel);
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							
						   	columnaDestino = colAux2;
						   	filaDestino = filAux;
						   	
						   	if (esMovimientoAtaque(filAux,colAux2))
						   		HAtaque_ejecutaMovAtaqueUsuario(fil,col,filAux,colAux2,m,nivel);
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							
						   	filaDestino = filAux2;
						   	columnaDestino = colAux2;
						   	
						   	if (esMovimientoAtaque(filAux2,colAux2))
						   		HAtaque_ejecutaMovAtaqueUsuario(fil,col,filAux2,colAux2,m,nivel);
					
						   	break;
		}
	}
	
	void HAtaque_ejecutaMovNormalPC(int fil,int col,int filAux,int colAux,MovimientoPC m,int nivel){
		// Simula un movimiento normal de las fichas rojas (PC)
		TFicha fichaActual = getCasilla(fil,col);
		
		int solParcial = 0;
   		   
   	   	if (hayAtaqueMultiplePC(filAux,colAux,fichaActual))	
   	   		solParcial++;		// -> Heuristica A : +1	   	   
	   	 
   	   	sumaH += solParcial;
   	   	MovimientoPC movActual = new MovimientoPC(sumaH,fil,col,filAux,colAux,-1,-1,fichaActual);  

   	   	if (nivel == nivel_k)
   	   		ramaActual = new MovimientoPC(movActual);
   	   	
   		simulaMovimientoNormal(movActual);
   	   	jugActual = TipoJugador.Negro;
   	   	MovimientoPC mAux = pruebaJugadasUsuario(nivel-1,m.SolOptima());
   	   	if (mAux.SolOptima() > m.SolOptima())
   	   		m.setNuevaSolucion(mAux.SolOptima(), mAux.getFilaOrigen(), mAux.getColumnaOrigen(),
   	   							mAux.getFilaDestino(),mAux.getColumnaDestino(),
   	   							   mAux.getFilaAtaque(),mAux.getColumnaAtaque(),
   	   							   mAux.getFichaActual());
   	   	deshacerMovimientoNormal(movActual);
   	   	sumaH -= solParcial;
   	   	jugActual = TipoJugador.Rojo;
   	   	

	}
	
	void HAtaque_ejecutaMovAtaquePC(int fil,int col,int filAux,int colAux,MovimientoPC m, int nivel){
		// Simula un movimiento ataque de las fichas rojas (PC)
		TFicha fichaActual = getCasilla(fil,col);
		int solParcial = 2;									// Si es movimiento de ataque -> Heuristica A : +2
		
		filSig = filAux;
	   	colSig = colAux;
		calculaCasillaSiguiente(fil,col,filAux,colAux); 	// Calculamos casillas siguientes
	   	   
	   	if (hayAtaqueMultiplePC(filSig,colSig,fichaActual))
	   		solParcial++;								// -> Heuristica A : +1
	   	 
	   	sumaH += solParcial;
	   	MovimientoPC movActual = new MovimientoPC(sumaH,fil,col,filSig,colSig,filAux,colAux,fichaActual);
		movActual.setFichaAtacada(getCasilla(filAux,colAux));
		
   	   	if (nivel == nivel_k)
   	   		ramaActual = new MovimientoPC(movActual);
	   	
	   	simulaMovimientoAtaque(movActual);
	   	if (seguirTirando)
	   		simulaMultiAtaque(movActual);
	   	
	   	jugActual = TipoJugador.Negro;
	   	MovimientoPC mAux = pruebaJugadasUsuario(nivel-1,m.SolOptima());
	   	if (mAux.SolOptima() > m.SolOptima())
   			m.setNuevaSolucion(mAux.SolOptima(), mAux.getFilaOrigen(), mAux.getColumnaOrigen(),
   							   mAux.getFilaDestino(),mAux.getColumnaDestino(),
   							   mAux.getFilaAtaque(),mAux.getColumnaAtaque(),
   							   mAux.getFichaActual());
	   	
	   	if (!movActual.esPilaMultiAtaqueVacia())
	   		deshacerMultiAtaque(movActual);
	   	
	   	deshacerMovimientoAtaque(movActual);
	   	jugActual = TipoJugador.Rojo;
	   	sumaH -= solParcial;

	}
	
	void HAtaque_ejecutaMovNormalUsuario(int fil,int col,int filAux,int colAux,MovimientoPC m,int nivel){
		// Minimiza para el usuario
		
		TFicha fichaActual = getCasilla(fil,col);
		int solParcial = 0;
   		   
   	   	if (hayAtaqueMultiplePC(filAux,colAux,fichaActual))	
   	   		solParcial++;								// -> Heuristica A : +1	   	   

   	   	sumaH += solParcial;					
   	   	MovimientoPC movActual = new MovimientoPC(sumaH,fil,col,filAux,colAux,-1,-1,fichaActual);
   	   	   
   		simulaMovimientoNormal(movActual);
   	   	jugActual = TipoJugador.Rojo;
   	   	MovimientoPC mAux = pruebaJugadasPC(nivel-1,m.SolOptima());
   	   	if (mAux.SolOptima() < m.SolOptima())
   	   			m.setNuevaSolucion(mAux.SolOptima(), mAux.getFilaOrigen(), mAux.getColumnaOrigen(),
   	   							   mAux.getFilaDestino(),mAux.getColumnaDestino(),
   	   							   mAux.getFilaAtaque(),mAux.getColumnaAtaque(),
   	   							   mAux.getFichaActual()); 
   	   	
   	   	deshacerMovimientoNormal(movActual);
   	   	sumaH -= solParcial;
   	   	jugActual = TipoJugador.Negro;
   	   	  // }
	}
	
	void HAtaque_ejecutaMovAtaqueUsuario(int fil,int col,int filAux,int colAux,MovimientoPC m, int nivel){
		   
		 TFicha fichaActual = getCasilla(fil,col);
		 int solParcial = 2;									// Si es movimiento de ataque -> Heuristica A : +2
	   	 filSig = filAux;
	   	 colSig = colAux;
		 calculaCasillaSiguiente(fil,col,filAux,colAux); 	// Calculamos casillas siguientes
	   	   
	   	 if (hayAtaqueMultiplePC(filSig,colSig,fichaActual))
	   		 solParcial++;								// -> Heuristica A : +1

	   	 sumaH += solParcial;
	   	 MovimientoPC movActual = new MovimientoPC(sumaH,fil,col,filSig,colSig,filAux,colAux,fichaActual);
	   	 movActual.setFichaAtacada(getCasilla(filAux,colAux));
	   	 
   	   	 simulaMovimientoAtaque(movActual);
   	   	 if (seguirTirando)
	   		simulaMultiAtaque(movActual);
   	   	 
   	   	 jugActual = TipoJugador.Rojo;
   	   	 MovimientoPC mAux = pruebaJugadasPC(nivel-1,m.SolOptima());
   	   	 if (mAux.SolOptima() < m.SolOptima())
   	   			m.setNuevaSolucion(mAux.SolOptima(), mAux.getFilaOrigen(), mAux.getColumnaOrigen(),
   	   							   mAux.getFilaDestino(),mAux.getColumnaDestino(),
   	   							   mAux.getFilaAtaque(),mAux.getColumnaAtaque(),
   	   							   mAux.getFichaActual());
   	   	 jugActual = TipoJugador.Negro;
   	   	 
   	   	 if (!movActual.esPilaMultiAtaqueVacia())
	   		deshacerMultiAtaque(movActual);
   	   	 
   	   	 deshacerMovimientoAtaque(movActual);
   	   	 sumaH -= solParcial;
	   	  
	}
	
	
	// HEURISTICA DEFENSA
	// *************************************************************************
	// Comprueba si una ficha en la casilla fil,col esta en peligro de ser atacada
	void pruebaHDefensa(int fil,int col, MovimientoPC m,int nivel){
		
		TFicha fichaActual = getCasilla(fil,col);
		int filAux;
		int filAux2;
		int colAux;
		int colAux2;

		
		//int filSig;		// Casillas siguientes para calcular Multiataque 
		//int colSig;
		
		seguirTirando = false;
		
		switch(fichaActual){
			case peonNegro: filAux = fil-1;
						   colAux = col+1;
					   	   colAux2 = col-1;
				
					   	   filaOrigen = fil; columnaOrigen = col;
						   filaDestino = filAux; columnaDestino = colAux2;
						    
						   if (hayFichaDelContrincanteEn(filAux,colAux) || 
							   esDamaDelContrincante(fil+1,colAux) ||
							   esDamaDelContrincante(fil+1,colAux2)){
							   
						   // 1 - Si podemos salvar con movimiento normal a la casilla contraria desde la que podrian atacar      
					   		   if (esMovimientoNormal(filAux,colAux2)) 
					   			   HDefensa_ejecutaMovNormalUsuario(fil,col,filAux,colAux2,m, nivel);
					   		   else 
						   // 2 - Sino, si podemos salvar con ataque a la casilla contraria desde la que podrian atacar
					   	   	   if (fil != 0 && col != 0 && col != tamTablero-1){ // Si estamos contra la pared no es necesario salvar
					   	   		   filaDestino = filAux; columnaDestino = colAux2;
					   	   		   if (esMovimientoAtaque(filAux,colAux2))
						   	   		   HDefensa_ejecutaMovAtaqueUsuario(fil,col,filAux,colAux2,m,nivel);
						   	   	   else {
						   // 3 - Sino, si podemos salvar con ataque a la casilla desde la que atacan (ultima opcion por ser defensiva)
						   	   		   filaDestino = filAux; columnaDestino = colAux;
						   	   		   if (esMovimientoAtaque(filAux,colAux))
						   	   			   HDefensa_ejecutaMovAtaqueUsuario(fil,col,filAux,colAux,m,nivel); 
						   	   	   	   }
					   	   	   	   } // if (estaContraLaPared)
					   	   	  }  
					   	   
					   	   filaOrigen = fil; columnaOrigen = col;
						   filaDestino = filAux; columnaDestino = colAux;
					   	   
					   	   if (hayFichaDelContrincanteEn(filAux,colAux2) || 
							   esDamaDelContrincante(fil+1,colAux) ||
							   esDamaDelContrincante(fil+1,colAux2)){
					   	   	   
							   // 1 - Si podemos salvar con movimiento normal a la casilla contraria desde la que podrian atacar      
					   		   if (esMovimientoNormal(filAux,colAux)) 
					   			   HDefensa_ejecutaMovNormalUsuario(fil,col,filAux,colAux,m, nivel);
					   		   else 
					   		   // 2 - Sino, si podemos salvar con ataque a la casilla contraria desde la que podrian atacar
					   	   	   if (fil != 0 && col != 0 && col != tamTablero-1){ // Si estamos contra la pared no es necesario salvar
					   	   		   filaDestino = filAux; columnaDestino = colAux;
					   	   		   if (esMovimientoAtaque(filAux,colAux))
						   	   		   HDefensa_ejecutaMovAtaqueUsuario(fil,col,filAux,colAux,m,nivel);
						   	   	   else {
						   	   // 3 - Sino, si podemos salvar con ataque a la casilla desde la que atacan (ultima opcion por ser defensiva)
						   	   		   filaDestino = filAux; columnaDestino = colAux2;
						   	   		   if (esMovimientoAtaque(filAux,colAux2))
						   	   			   HDefensa_ejecutaMovAtaqueUsuario(fil,col,filAux,colAux2,m,nivel); 
						   	   	   	   }
					   	   	   	   } // if (estaContraLaPared)
					   	   	  }  
				
					   	   break;
					   	   
			case peonRojo: filAux = fil+1;
						   colAux = col+1;
					   	   colAux2 = col-1;

					   	   filaOrigen = fil; columnaOrigen = col;
						   filaDestino = filAux; columnaDestino = colAux2;
						    
						   if (hayFichaDelContrincanteEn(filAux,colAux) || 
							   esDamaDelContrincante(fil-1,colAux) ||
							   esDamaDelContrincante(fil-1,colAux2)){
							   
						   // 1 - Si podemos salvar con movimiento normal a la casilla contraria desde la que podrian atacar      
					   		   if (esMovimientoNormal(filAux,colAux2)) 
					   			   HDefensa_ejecutaMovNormalPC(fil,col,filAux,colAux2,m, nivel);
					   		   else 
						   // 2 - Sino, si podemos salvar con ataque a la casilla contraria desde la que podrian atacar
					   	   	   if (fil != 0 && col != 0 && col != tamTablero-1){ // Si estamos contra la pared no es necesario salvar
					   	   		   filaDestino = filAux; columnaDestino = colAux2;
					   	   		   if (esMovimientoAtaque(filAux,colAux2))
						   	   		   HDefensa_ejecutaMovAtaquePC(fil,col,filAux,colAux2,m,nivel);
						   	   	   else {
						   // 3 - Sino, si podemos salvar con ataque a la casilla desde la que atacan (ultima opcion por ser defensiva)
						   	   		   filaDestino = filAux; columnaDestino = colAux;
						   	   		   if (esMovimientoAtaque(filAux,colAux))
						   	   			   HDefensa_ejecutaMovAtaquePC(fil,col,filAux,colAux,m,nivel); 
						   	   	   	   }
					   	   	   	   } // if (estaContraLaPared)
					   	   	  }  
					   	   
					   	   filaOrigen = fil; columnaOrigen = col;
						   filaDestino = filAux; columnaDestino = colAux;
					   	   
					   	   if (hayFichaDelContrincanteEn(filAux,colAux2) || 
							   esDamaDelContrincante(fil-1,colAux) ||
							   esDamaDelContrincante(fil-1,colAux2)){
					   	   	   
							   // 1 - Si podemos salvar con movimiento normal a la casilla contraria desde la que podrian atacar      
					   		   if (esMovimientoNormal(filAux,colAux)) 
					   			   HDefensa_ejecutaMovNormalPC(fil,col,filAux,colAux,m, nivel);
					   		   else 
					   		   // 2 - Sino, si podemos salvar con ataque a la casilla contraria desde la que podrian atacar
					   	   	   if (fil != 0 && col != 0 && col != tamTablero-1){ // Si estamos contra la pared no es necesario salvar
					   	   		   filaDestino = filAux; columnaDestino = colAux;
					   	   		   if (esMovimientoAtaque(filAux,colAux))
						   	   		   HDefensa_ejecutaMovAtaquePC(fil,col,filAux,colAux,m,nivel);
						   	   	   else {
						   	   // 3 - Sino, si podemos salvar con ataque a la casilla desde la que atacan (ultima opcion por ser defensiva)
						   	   		   filaDestino = filAux; columnaDestino = colAux2;
						   	   		   if (esMovimientoAtaque(filAux,colAux2))
						   	   			   HDefensa_ejecutaMovAtaquePC(fil,col,filAux,colAux2,m,nivel); 
						   	   	   	   }
					   	   	   	   } // if (estaContraLaPared)
					   	   	  }  

					   	   break;
					   	   
			case damaNegra : filAux = fil-1;
							filAux2 = fil+1;
							colAux = col+1;
							colAux2 = col-1;
							
						   	filaOrigen = fil; columnaOrigen = col;  
							filaDestino = filAux; columnaDestino = colAux;
							   
					   	   if (hayFichaDelContrincanteEn(filAux,colAux)){
					   		   // 1 - Si podemos salvar con movimiento normal a la casilla adyacente excepto desde la que podrian atacar 
					   	   	   //     --------------------------------------------------------------------------------------------------
					   		   if (esMovimientoNormal(filAux,colAux2))
					   			   HDefensa_ejecutaMovNormalUsuario(fil,col,filAux,colAux2,m, nivel);
					   		   else
					   	   	   if (esMovimientoNormal(filAux2,colAux))
					   	   		   HDefensa_ejecutaMovNormalUsuario(fil,col,filAux2,colAux,m, nivel);
					   	   	   else
					   	   	   if (esMovimientoNormal(filAux2,colAux2))
					   	   		   HDefensa_ejecutaMovNormalUsuario(fil,col,filAux2,colAux2,m, nivel);
					   	   	   else
					   	   	   
					   	   	   // 2 - Sino, si podemos salvar con ataque a la casilla contraria desde la que podrian atacar
					   	   	   //     -------------------------------------------------------------------------------------
					   	   	   if (fil != 0 && col != 0 && col != tamTablero-1) {
					   	   		  filaDestino = filAux; columnaDestino = colAux2;
						   	   	  if (esMovimientoAtaque(filAux,colAux2))
						   	   		  HDefensa_ejecutaMovAtaqueUsuario(fil,col,filAux,colAux2,m, nivel); 
						   	   	  else {
						   	   		  filaDestino = filAux2; columnaDestino = colAux2;
						   	   		  if (esMovimientoAtaque(filAux2,colAux2))
						   	   			  HDefensa_ejecutaMovAtaqueUsuario(fil,col,filAux2,colAux2,m, nivel);  
						   	   		  else {
						   	   			  filaDestino = filAux2; columnaDestino = colAux;
								   	   	  if (esMovimientoAtaque(filAux2,colAux))
								   	   		  HDefensa_ejecutaMovAtaqueUsuario(fil,col,filAux2,colAux,m, nivel); 
								   	   	  else{
							   // 3 - Sino, si podemos salvar con ataque a la casilla desde la que atacan (ultima opcion por ser defensiva)
							   //     -----------------------------------------------------------------------------------------------------
								   	   		  filaDestino = filAux; columnaDestino = colAux; 
									   	   	  if (esMovimientoAtaque(filAux,colAux))
									   	   		  HDefensa_ejecutaMovAtaqueUsuario(fil,col,filAux,colAux,m, nivel); 
								   	   	  }
						   	   		  }
						   	   	  }
					   	   	   }
					   	   }
				
					   	   filaDestino = filAux;
						   columnaDestino = colAux2;
						   	
						   if (hayFichaDelContrincanteEn(filAux,colAux2)){
					   		   // 1 - Si podemos salvar con movimiento normal a la casilla adyacente excepto desde la que podrian atacar 
					   	   	   //     --------------------------------------------------------------------------------------------------
					   		   if (esMovimientoNormal(filAux,colAux))
					   			   HDefensa_ejecutaMovNormalUsuario(fil,col,filAux,colAux,m, nivel);
					   		   else
					   	   	   if (esMovimientoNormal(filAux2,colAux))
					   	   		   HDefensa_ejecutaMovNormalUsuario(fil,col,filAux2,colAux,m, nivel);
					   	   	   else
					   	   	   if (esMovimientoNormal(filAux2,colAux2))
					   	   		   HDefensa_ejecutaMovNormalUsuario(fil,col,filAux2,colAux2,m, nivel);
					   	   	   else
					   	   	   
					   	   	   // 2 - Sino, si podemos salvar con ataque a la casilla contraria desde la que podrian atacar
					   	   	   //     -------------------------------------------------------------------------------------
					   	   	   if (fil != 0 && col != 0 && col != tamTablero-1) {
					   	   		  filaDestino = filAux; columnaDestino = colAux;
						   	   	  if (esMovimientoAtaque(filAux,colAux))
						   	   		  HDefensa_ejecutaMovAtaqueUsuario(fil,col,filAux,colAux,m, nivel); 
						   	   	  else {
						   	   		  filaDestino = filAux2; columnaDestino = colAux;
						   	   		  if (esMovimientoAtaque(filAux2,colAux))
						   	   			  HDefensa_ejecutaMovAtaqueUsuario(fil,col,filAux2,colAux,m, nivel);  
						   	   		  else {
						   	   			  filaDestino = filAux2; columnaDestino = colAux2;
								   	   	  if (esMovimientoAtaque(filAux2,colAux2))
								   	   		  HDefensa_ejecutaMovAtaqueUsuario(fil,col,filAux2,colAux2,m, nivel); 
								   	   	  else{
							   // 3 - Sino, si podemos salvar con ataque a la casilla desde la que atacan (ultima opcion por ser defensiva)
							   //     -----------------------------------------------------------------------------------------------------
								   	   		  filaDestino = filAux; columnaDestino = colAux2; 
									   	   	  if (esMovimientoAtaque(filAux,colAux2))
									   	   		  HDefensa_ejecutaMovAtaqueUsuario(fil,col,filAux,colAux2,m, nivel); 
								   	   	  }
						   	   		  }
						   	   	  }
					   	   	   }
					   	   }  
						   
						   filaDestino = filAux2;
						   columnaDestino = colAux;
							   
					   	   if (hayFichaDelContrincanteEn(filAux2,colAux)){
					   	// 1 - Si podemos salvar con movimiento normal a la casilla adyacente excepto desde la que podrian atacar 
					   	//     --------------------------------------------------------------------------------------------------
					   		   if (esMovimientoNormal(filAux2,colAux2))
					   			   HDefensa_ejecutaMovNormalUsuario(fil,col,filAux2,colAux2,m, nivel);
					   		   else
					   	   	   if (esMovimientoNormal(filAux,colAux))
					   	   		   HDefensa_ejecutaMovNormalUsuario(fil,col,filAux,colAux,m, nivel);
					   	   	   else
					   	   	   if (esMovimientoNormal(filAux,colAux2))
					   	   		   HDefensa_ejecutaMovNormalUsuario(fil,col,filAux,colAux2,m, nivel);
					   	   	   else
					   	   	   
					   	 // 2 - Sino, si podemos salvar con ataque a la casilla contraria desde la que podrian atacar
					   	 //     -------------------------------------------------------------------------------------
					   	   	   if (fil != 0 && col != 0 && col != tamTablero-1) {
					   	   		  filaDestino = filAux2; columnaDestino = colAux2;
						   	   	  if (esMovimientoAtaque(filAux2,colAux2))
						   	   		  HDefensa_ejecutaMovAtaqueUsuario(fil,col,filAux2,colAux2,m, nivel); 
						   	   	  else {
						   	   		  filaDestino = filAux; columnaDestino = colAux2;
						   	   		  if (esMovimientoAtaque(filAux,colAux2))
						   	   			  HDefensa_ejecutaMovAtaqueUsuario(fil,col,filAux,colAux2,m, nivel);  
						   	   		  else {
						   	   			  filaDestino = filAux; columnaDestino = colAux;
								   	   	  if (esMovimientoAtaque(filAux,colAux))
								   	   		  HDefensa_ejecutaMovAtaqueUsuario(fil,col,filAux,colAux,m, nivel); 
								   	   	  else{
						 // 3 - Sino, si podemos salvar con ataque a la casilla desde la que atacan (ultima opcion por ser defensiva)
						 //     -----------------------------------------------------------------------------------------------------
								   	   		  filaDestino = filAux2; columnaDestino = colAux; 
									   	   	  if (esMovimientoAtaque(filAux2,colAux))
									   	   		  HDefensa_ejecutaMovAtaqueUsuario(fil,col,filAux2,colAux,m, nivel); 
								   	   	  }
						   	   		  }
						   	   	  }
					   	   	   }
					   	   }
					   		   
					   	   
						   filaDestino = filAux2; columnaDestino = colAux2;
							   
					   	   if (hayFichaDelContrincanteEn(filAux2,colAux2)){
					   // 1 - Si podemos salvar con movimiento normal a la casilla adyacente excepto desde la que podrian atacar 
					   //     --------------------------------------------------------------------------------------------------
					   		   if (esMovimientoNormal(filAux2,colAux))
					   			   HDefensa_ejecutaMovNormalUsuario(fil,col,filAux2,colAux,m, nivel);
					   		   else
					   	   	   if (esMovimientoNormal(filAux,colAux))
					   	   		   HDefensa_ejecutaMovNormalUsuario(fil,col,filAux,colAux,m, nivel);
					   	   	   else
					   	   	   if (esMovimientoNormal(filAux,colAux2))
					   	   		   HDefensa_ejecutaMovNormalUsuario(fil,col,filAux,colAux2,m, nivel);
					   	   	   else
					   	   	   
					   	// 2 - Sino, si podemos salvar con ataque a la casilla contraria desde la que podrian atacar
					   	//     -------------------------------------------------------------------------------------
					   	   	   if (fil != 0 && col != 0 && col != tamTablero-1) {
					   	   		  filaDestino = filAux2; columnaDestino = colAux;
						   	   	  if (esMovimientoAtaque(filAux2,colAux))
						   	   		  HDefensa_ejecutaMovAtaqueUsuario(fil,col,filAux2,colAux,m, nivel); 
						   	   	  else {
						   	   		  filaDestino = filAux; columnaDestino = colAux;
						   	   		  if (esMovimientoAtaque(filAux,colAux))
						   	   			  HDefensa_ejecutaMovAtaqueUsuario(fil,col,filAux,colAux,m, nivel);  
						   	   		  else {
						   	   			  filaDestino = filAux; columnaDestino = colAux2;
								   	   	  if (esMovimientoAtaque(filAux,colAux2))
								   	   		  HDefensa_ejecutaMovAtaqueUsuario(fil,col,filAux,colAux2,m, nivel); 
								   	   	  else{
						// 3 - Sino, si podemos salvar con ataque a la casilla desde la que atacan (ultima opcion por ser defensiva)
						//     -----------------------------------------------------------------------------------------------------
								   	   		  filaDestino = filAux2; columnaDestino = colAux2; 
									   	   	  if (esMovimientoAtaque(filAux2,colAux2))
									   	   		  HDefensa_ejecutaMovAtaqueUsuario(fil,col,filAux2,colAux2,m, nivel); 
								   	   	  }
						   	   		  }
						   	   	  }
					   	   	   }
					   	   }	
					   	   
						   	break;						   	   
			case damaRoja : filAux = fil-1;
							filAux2 = fil+1;
							colAux = col+1;
							colAux2 = col-1;
							
						   	filaOrigen = fil; columnaOrigen = col;  
							filaDestino = filAux; columnaDestino = colAux;
							   
					   	   if (hayFichaDelContrincanteEn(filAux,colAux)){
					   		   // 1 - Si podemos salvar con movimiento normal a la casilla adyacente excepto desde la que podrian atacar 
					   	   	   //     --------------------------------------------------------------------------------------------------
					   		   if (esMovimientoNormal(filAux,colAux2))
					   			   HDefensa_ejecutaMovNormalPC(fil,col,filAux,colAux2,m, nivel);
					   		   else
					   	   	   if (esMovimientoNormal(filAux2,colAux))
					   	   		   HDefensa_ejecutaMovNormalPC(fil,col,filAux2,colAux,m, nivel);
					   	   	   else
					   	   	   if (esMovimientoNormal(filAux2,colAux2))
					   	   		   HDefensa_ejecutaMovNormalPC(fil,col,filAux2,colAux2,m, nivel);
					   	   	   else
					   	   	   
					   	   	   // 2 - Sino, si podemos salvar con ataque a la casilla contraria desde la que podrian atacar
					   	   	   //     -------------------------------------------------------------------------------------
					   	   	   if (fil != 0 && col != 0 && col != tamTablero-1) {
					   	   		  filaDestino = filAux; columnaDestino = colAux2;
						   	   	  if (esMovimientoAtaque(filAux,colAux2))
						   	   		  HDefensa_ejecutaMovAtaquePC(fil,col,filAux,colAux2,m, nivel); 
						   	   	  else {
						   	   		  filaDestino = filAux2; columnaDestino = colAux2;
						   	   		  if (esMovimientoAtaque(filAux2,colAux2))
						   	   			  HDefensa_ejecutaMovAtaquePC(fil,col,filAux2,colAux2,m, nivel);  
						   	   		  else {
						   	   			  filaDestino = filAux2; columnaDestino = colAux;
								   	   	  if (esMovimientoAtaque(filAux2,colAux))
								   	   		  HDefensa_ejecutaMovAtaquePC(fil,col,filAux2,colAux,m, nivel); 
								   	   	  else{
							   // 3 - Sino, si podemos salvar con ataque a la casilla desde la que atacan (ultima opcion por ser defensiva)
							   //     -----------------------------------------------------------------------------------------------------
								   	   		  filaDestino = filAux; columnaDestino = colAux; 
									   	   	  if (esMovimientoAtaque(filAux,colAux))
									   	   		  HDefensa_ejecutaMovAtaquePC(fil,col,filAux,colAux,m, nivel); 
								   	   	  }
						   	   		  }
						   	   	  }
					   	   	   }
					   	   }

					   	   filaDestino = filAux;
						   columnaDestino = colAux2;
						   	
						   if (hayFichaDelContrincanteEn(filAux,colAux2)){
					   		   // 1 - Si podemos salvar con movimiento normal a la casilla adyacente excepto desde la que podrian atacar 
					   	   	   //     --------------------------------------------------------------------------------------------------
					   		   if (esMovimientoNormal(filAux,colAux))
					   			   HDefensa_ejecutaMovNormalPC(fil,col,filAux,colAux,m, nivel);
					   		   else
					   	   	   if (esMovimientoNormal(filAux2,colAux))
					   	   		   HDefensa_ejecutaMovNormalPC(fil,col,filAux2,colAux,m, nivel);
					   	   	   else
					   	   	   if (esMovimientoNormal(filAux2,colAux2))
					   	   		   HDefensa_ejecutaMovNormalPC(fil,col,filAux2,colAux2,m, nivel);
					   	   	   else
					   	   	   
					   	   	   // 2 - Sino, si podemos salvar con ataque a la casilla contraria desde la que podrian atacar
					   	   	   //     -------------------------------------------------------------------------------------
					   	   	   if (fil != 0 && col != 0 && col != tamTablero-1) {
					   	   		  filaDestino = filAux; columnaDestino = colAux;
						   	   	  if (esMovimientoAtaque(filAux,colAux))
						   	   		  HDefensa_ejecutaMovAtaquePC(fil,col,filAux,colAux,m, nivel); 
						   	   	  else {
						   	   		  filaDestino = filAux2; columnaDestino = colAux;
						   	   		  if (esMovimientoAtaque(filAux2,colAux))
						   	   			  HDefensa_ejecutaMovAtaquePC(fil,col,filAux2,colAux,m, nivel);  
						   	   		  else {
						   	   			  filaDestino = filAux2; columnaDestino = colAux2;
								   	   	  if (esMovimientoAtaque(filAux2,colAux2))
								   	   		  HDefensa_ejecutaMovAtaquePC(fil,col,filAux2,colAux2,m, nivel); 
								   	   	  else{
							   // 3 - Sino, si podemos salvar con ataque a la casilla desde la que atacan (ultima opcion por ser defensiva)
							   //     -----------------------------------------------------------------------------------------------------
								   	   		  filaDestino = filAux; columnaDestino = colAux2; 
									   	   	  if (esMovimientoAtaque(filAux,colAux2))
									   	   		  HDefensa_ejecutaMovAtaquePC(fil,col,filAux,colAux2,m, nivel); 
								   	   	  }
						   	   		  }
						   	   	  }
					   	   	   }
					   	   }  
						   
						   filaDestino = filAux2;
						   columnaDestino = colAux;
							   
					   	   if (hayFichaDelContrincanteEn(filAux2,colAux)){
					   	// 1 - Si podemos salvar con movimiento normal a la casilla adyacente excepto desde la que podrian atacar 
					   	//     --------------------------------------------------------------------------------------------------
					   		   if (esMovimientoNormal(filAux2,colAux2))
					   			   HDefensa_ejecutaMovNormalPC(fil,col,filAux2,colAux2,m, nivel);
					   		   else
					   	   	   if (esMovimientoNormal(filAux,colAux))
					   	   		   HDefensa_ejecutaMovNormalPC(fil,col,filAux,colAux,m, nivel);
					   	   	   else
					   	   	   if (esMovimientoNormal(filAux,colAux2))
					   	   		   HDefensa_ejecutaMovNormalPC(fil,col,filAux,colAux2,m, nivel);
					   	   	   else
					   	   	   
					   	 // 2 - Sino, si podemos salvar con ataque a la casilla contraria desde la que podrian atacar
					   	 //     -------------------------------------------------------------------------------------
					   	   	   if (fil != 0 && col != 0 && col != tamTablero-1) {
					   	   		  filaDestino = filAux2; columnaDestino = colAux2;
						   	   	  if (esMovimientoAtaque(filAux2,colAux2))
						   	   		  HDefensa_ejecutaMovAtaquePC(fil,col,filAux2,colAux2,m, nivel); 
						   	   	  else {
						   	   		  filaDestino = filAux; columnaDestino = colAux2;
						   	   		  if (esMovimientoAtaque(filAux,colAux2))
						   	   			  HDefensa_ejecutaMovAtaquePC(fil,col,filAux,colAux2,m, nivel);  
						   	   		  else {
						   	   			  filaDestino = filAux; columnaDestino = colAux;
								   	   	  if (esMovimientoAtaque(filAux,colAux))
								   	   		  HDefensa_ejecutaMovAtaquePC(fil,col,filAux,colAux,m, nivel); 
								   	   	  else{
						 // 3 - Sino, si podemos salvar con ataque a la casilla desde la que atacan (ultima opcion por ser defensiva)
						 //     -----------------------------------------------------------------------------------------------------
								   	   		  filaDestino = filAux2; columnaDestino = colAux; 
									   	   	  if (esMovimientoAtaque(filAux2,colAux))
									   	   		  HDefensa_ejecutaMovAtaquePC(fil,col,filAux2,colAux,m, nivel); 
								   	   	  }
						   	   		  }
						   	   	  }
					   	   	   }
					   	   }
					   		   
					   	   
						   filaDestino = filAux2; columnaDestino = colAux2;
							   
					   	   if (hayFichaDelContrincanteEn(filAux2,colAux2)){
					   // 1 - Si podemos salvar con movimiento normal a la casilla adyacente excepto desde la que podrian atacar 
					   //     --------------------------------------------------------------------------------------------------
					   		   if (esMovimientoNormal(filAux2,colAux))
					   			   HDefensa_ejecutaMovNormalPC(fil,col,filAux2,colAux,m, nivel);
					   		   else
					   	   	   if (esMovimientoNormal(filAux,colAux))
					   	   		   HDefensa_ejecutaMovNormalPC(fil,col,filAux,colAux,m, nivel);
					   	   	   else
					   	   	   if (esMovimientoNormal(filAux,colAux2))
					   	   		   HDefensa_ejecutaMovNormalPC(fil,col,filAux,colAux2,m, nivel);
					   	   	   else
					   	   	   
					   	// 2 - Sino, si podemos salvar con ataque a la casilla contraria desde la que podrian atacar
					   	//     -------------------------------------------------------------------------------------
					   	   	   if (fil != 0 && col != 0 && col != tamTablero-1) {
					   	   		  filaDestino = filAux2; columnaDestino = colAux;
						   	   	  if (esMovimientoAtaque(filAux2,colAux))
						   	   		  HDefensa_ejecutaMovAtaquePC(fil,col,filAux2,colAux,m, nivel); 
						   	   	  else {
						   	   		  filaDestino = filAux; columnaDestino = colAux;
						   	   		  if (esMovimientoAtaque(filAux,colAux))
						   	   			  HDefensa_ejecutaMovAtaquePC(fil,col,filAux,colAux,m, nivel);  
						   	   		  else {
						   	   			  filaDestino = filAux; columnaDestino = colAux2;
								   	   	  if (esMovimientoAtaque(filAux,colAux2))
								   	   		  HDefensa_ejecutaMovAtaquePC(fil,col,filAux,colAux2,m, nivel); 
								   	   	  else{
						// 3 - Sino, si podemos salvar con ataque a la casilla desde la que atacan (ultima opcion por ser defensiva)
						//     -----------------------------------------------------------------------------------------------------
								   	   		  filaDestino = filAux2; columnaDestino = colAux2; 
									   	   	  if (esMovimientoAtaque(filAux2,colAux2))
									   	   		  HDefensa_ejecutaMovAtaquePC(fil,col,filAux2,colAux2,m, nivel); 
								   	   	  }
						   	   		  }
						   	   	  }
					   	   	   }
					   	   }	
					   	   
						   	break;	
		} // switch
		
		if (m.getFilaOrigen() == -1 && m.getColumnaOrigen() == -1){
			calculaMovimientoSeguro(m);
		}
	}

	void HDefensa_ejecutaMovNormalPC(int fil,int col,int filAux,int colAux,MovimientoPC m,int nivel){
		
		TFicha fichaActual = getCasilla(fil,col);
		int solParcial = -1;
		
		sumaH += solParcial;
		
		MovimientoPC movActual = new MovimientoPC(sumaH,fil,col,filAux,colAux,-1,-1,fichaActual);  

   	   	if (nivel == nivel_k)
   		   ramaActual = new MovimientoPC(movActual);
   	
   	   	simulaMovimientoNormal(movActual);
   	   	jugActual = TipoJugador.Negro;
   	   	MovimientoPC mAux = pruebaJugadasUsuario(nivel-1,m.SolOptima());
   	   	if (mAux.SolOptima() > m.SolOptima())
   	   		m.setNuevaSolucion(mAux.SolOptima(), mAux.getFilaOrigen(), mAux.getColumnaOrigen(),
   	   						   mAux.getFilaDestino(),mAux.getColumnaDestino(),
   	   						   mAux.getFilaAtaque(),mAux.getColumnaAtaque(),
   	   						   mAux.getFichaActual());
   	   	deshacerMovimientoNormal(movActual);
   	   	sumaH -= solParcial;
   	   	jugActual = TipoJugador.Rojo;
	}

	void HDefensa_ejecutaMovAtaquePC(int fil,int col,int filAux,int colAux,MovimientoPC m,int nivel){
		
		int solParcial = -1;
		TFicha fichaActual = getCasilla(fil,col);
		
		filSig = filAux;
	   	colSig = colAux;
 		calculaCasillaSiguiente(fil,col,filAux,colAux); 	
		   	 
		sumaH += solParcial;
		MovimientoPC movActual = new MovimientoPC(sumaH,fil,col,filSig,colSig,filAux,colAux,fichaActual);
		movActual.setFichaAtacada(getCasilla(filAux,colAux));
			
	   	if (nivel == nivel_k)
	   		ramaActual = new MovimientoPC(movActual);
		   	
	   	simulaMovimientoAtaque(movActual);
	   	if (seguirTirando)
	   	   	simulaMultiAtaque(movActual);
		   	
	   	jugActual = TipoJugador.Negro;
	   	MovimientoPC mAux = pruebaJugadasUsuario(nivel-1,m.SolOptima());
	   	if (mAux.SolOptima() > m.SolOptima())
	   		m.setNuevaSolucion(mAux.SolOptima(), mAux.getFilaOrigen(), mAux.getColumnaOrigen(),
	   						   mAux.getFilaDestino(),mAux.getColumnaDestino(),
	   						   mAux.getFilaAtaque(),mAux.getColumnaAtaque(),
	   						   mAux.getFichaActual());
		   	
	   	if (!movActual.esPilaMultiAtaqueVacia())
	   		deshacerMultiAtaque(movActual);
		   	
	   	deshacerMovimientoAtaque(movActual);
	   	jugActual = TipoJugador.Rojo;
	   	sumaH -= solParcial;
	}
	
	void HDefensa_ejecutaMovNormalUsuario(int fil,int col,int filAux,int colAux,MovimientoPC m,int nivel){
		
		TFicha fichaActual = getCasilla(fil,col);
		int solParcial = -1;
		
		sumaH += solParcial;
		
		MovimientoPC movActual = new MovimientoPC(sumaH,fil,col,filAux,colAux,-1,-1,fichaActual);  

   	   	if (nivel == nivel_k)
   		   ramaActual = new MovimientoPC(movActual);
   	
   	   	simulaMovimientoNormal(movActual);
   	   	jugActual = TipoJugador.Rojo;
   	   	MovimientoPC mAux = pruebaJugadasPC(nivel-1,m.SolOptima());
   	   	if (mAux.SolOptima() < m.SolOptima())
   	   		m.setNuevaSolucion(mAux.SolOptima(), mAux.getFilaOrigen(), mAux.getColumnaOrigen(),
   	   						   mAux.getFilaDestino(),mAux.getColumnaDestino(),
   	   						   mAux.getFilaAtaque(),mAux.getColumnaAtaque(),
   	   						   mAux.getFichaActual());
   	   	deshacerMovimientoNormal(movActual);
   	   	sumaH -= solParcial;
   	   	jugActual = TipoJugador.Negro;
	}
	
	void HDefensa_ejecutaMovAtaqueUsuario(int fil,int col,int filAux,int colAux,MovimientoPC m,int nivel){
		
		int solParcial = -1;
		TFicha fichaActual = getCasilla(fil,col);
		
		filSig = filAux;
	   	colSig = colAux;
 		calculaCasillaSiguiente(fil,col,filAux,colAux); 	
		   	 
		sumaH += solParcial;
		MovimientoPC movActual = new MovimientoPC(sumaH,fil,col,filSig,colSig,filAux,colAux,fichaActual);
		movActual.setFichaAtacada(getCasilla(filAux,colAux));
			
	   	if (nivel == nivel_k)
	   		ramaActual = new MovimientoPC(movActual);
		   	
	   	simulaMovimientoAtaque(movActual);
	   	if (seguirTirando)
	   	   	simulaMultiAtaque(movActual);
		   	
	   	jugActual = TipoJugador.Rojo;
	   	MovimientoPC mAux = pruebaJugadasPC(nivel-1,m.SolOptima());
	   	if (mAux.SolOptima() < m.SolOptima())
	   		m.setNuevaSolucion(mAux.SolOptima(), mAux.getFilaOrigen(), mAux.getColumnaOrigen(),
	   						   mAux.getFilaDestino(),mAux.getColumnaDestino(),
	   						   mAux.getFilaAtaque(),mAux.getColumnaAtaque(),
	   						   mAux.getFichaActual());
		   	
	   	if (!movActual.esPilaMultiAtaqueVacia())
	   		deshacerMultiAtaque(movActual);
		   	
	   	deshacerMovimientoAtaque(movActual);
	   	jugActual = TipoJugador.Negro;
	   	sumaH -= solParcial;
	}

	void calculaMovimientoSeguro(MovimientoPC m){
		
		boolean encontrado = false;
		int filAux;
		int filAux2;
		int colAux;
		int colAux2;
		
		for(int fil = 0; fil < tamTablero && !encontrado; fil++)
			for(int col = 0; col < tamTablero && !encontrado; col++){
				
				TFicha fichaActual = getCasilla(fil,col);
				
				switch (fichaActual){
					case peonRojo :  filAux = fil+1;
									 colAux = col+1;
									 colAux2 = col-1;
									 
									 if (esMovimientoNormal(filAux,colAux) &&
										!hayFichaDelContrincanteEn(filAux+1,colAux+1) &&
										!hayFichaDelContrincanteEn(filAux+1,colAux-1)){
										m.setNuevaSolucion(Integer.MIN_VALUE, fil, col, filAux, colAux,-1,-1,fichaActual);
										encontrado = true;
									 }
									 
									 if (esMovimientoNormal(filAux,colAux2) &&
										!hayFichaDelContrincanteEn(filAux+1,colAux2 + 1) &&
										!hayFichaDelContrincanteEn(filAux+1,colAux2 - 1)){
										m.setNuevaSolucion(Integer.MIN_VALUE, fil, col, filAux, colAux2,-1,-1,fichaActual);
										encontrado = true;
									 }
									 
									 break;
									 
					case damaRoja :  filAux = fil-1;
									 filAux2 = fil+1;
									 colAux = col+1;
									 colAux2 = col-1;
									 
									 if (esMovimientoNormal(filAux,colAux) &&
										!hayFichaDelContrincanteEn(filAux-1,colAux+1) &&
										!hayFichaDelContrincanteEn(filAux-1,colAux-1)){
										m.setNuevaSolucion(Integer.MIN_VALUE, fil, col, filAux, colAux,-1,-1,fichaActual);
										encontrado = true;
									 }
									 
									 if (esMovimientoNormal(filAux,colAux2) &&
										!hayFichaDelContrincanteEn(filAux-1,colAux2 + 1) &&
										!hayFichaDelContrincanteEn(filAux-1,colAux2 - 1)){
										m.setNuevaSolucion(Integer.MIN_VALUE, fil, col, filAux, colAux2,-1,-1,fichaActual);
										encontrado = true;
									 }
									 
									 if (esMovimientoNormal(filAux2,colAux) &&
										!hayFichaDelContrincanteEn(filAux2 + 1,colAux+1) &&
										!hayFichaDelContrincanteEn(filAux2 + 1,colAux-1)){
										m.setNuevaSolucion(Integer.MIN_VALUE, fil, col, filAux2, colAux,-1,-1,fichaActual);
										encontrado = true;
									 }
									 
									 if (esMovimientoNormal(filAux2,colAux2) &&
										!hayFichaDelContrincanteEn(filAux2 + 1,colAux2 + 1) &&
										!hayFichaDelContrincanteEn(filAux2 + 1,colAux2 - 1)){
										m.setNuevaSolucion(Integer.MIN_VALUE, fil, col, filAux2, colAux2,-1,-1,fichaActual);
										encontrado = true;
									 }
									 
									 break;
				}
				
					
			} // for 2
	}

	void calculaPrimerMovPosible(MovimientoPC m){
		
		boolean encontrado = false;
		int filAux;
		int filAux2;
		int colAux;
		int colAux2;
		
		for(int fil = 0; fil < tamTablero && !encontrado; fil++)
			for(int col = 0; col < tamTablero && !encontrado; col++){
				
				TFicha fichaActual = getCasilla(fil,col);
				
				switch (fichaActual){
					case peonRojo :  filAux = fil+1;
									 colAux = col+1;
									 colAux2 = col-1;
									 
									 
									 if (esMovimientoNormal(filAux,colAux)) {
										m.setNuevaSolucion(Integer.MIN_VALUE, fil, col, filAux, colAux,-1,-1,fichaActual);
										encontrado = true;
									 }
									 
									 if (esMovimientoNormal(filAux,colAux2)) {
										m.setNuevaSolucion(Integer.MIN_VALUE, fil, col, filAux, colAux2,-1,-1,fichaActual);
										encontrado = true;
									 }
									 
									 filaOrigen = fil;
									 columnaOrigen = col;
									 
									 filaDestino = filAux;
									 columnaDestino = colAux;
									 
									 if (esMovimientoAtaque(filAux,colAux)) {
										filSig = filAux;
										colSig = colAux;
										calculaCasillaSiguiente(fil,col,filAux,colAux);
										m.setNuevaSolucion(Integer.MIN_VALUE, fil, col, filSig, colSig, filAux, colAux,fichaActual);
										encontrado = true;
									 }
									 
									 columnaDestino = colAux2;
									 
									 if (esMovimientoAtaque(filAux,colAux2)) {
										filSig = filAux;
										colSig = colAux2;
										calculaCasillaSiguiente(fil,col,filAux,colAux2);
										m.setNuevaSolucion(Integer.MIN_VALUE, fil, col, filSig, colSig, filAux, colAux2,fichaActual);
										encontrado = true;
									 }
									 
									 break;
									 
					case damaRoja :  filAux = fil-1;
									 filAux2 = fil+1;
									 colAux = col+1;
									 colAux2 = col-1;
									 
									 if (esMovimientoNormal(filAux,colAux)) {
										m.setNuevaSolucion(Integer.MIN_VALUE, fil, col, filAux, colAux,-1,-1,fichaActual);
										encontrado = true;
									 }
									 
									 if (esMovimientoNormal(filAux,colAux2)) {
										m.setNuevaSolucion(Integer.MIN_VALUE, fil, col, filAux, colAux2,-1,-1,fichaActual);
										encontrado = true;
									 }
									 
									 if (esMovimientoNormal(filAux2,colAux)) {
										m.setNuevaSolucion(Integer.MIN_VALUE, fil, col, filAux2, colAux,-1,-1,fichaActual);
										encontrado = true;
									 }
									 
									 if (esMovimientoNormal(filAux2,colAux2)) {
										m.setNuevaSolucion(Integer.MIN_VALUE, fil, col, filAux2, colAux2,-1,-1,fichaActual);
										encontrado = true;
									 }
									 
									 filaDestino = filAux;
									 columnaDestino = colAux;
									 
									 if (esMovimientoAtaque(filAux,colAux)) {
										filSig = filAux;
										colSig = colAux;
										calculaCasillaSiguiente(fil,col,filAux,colAux);
										m.setNuevaSolucion(Integer.MIN_VALUE, fil, col, filSig, colSig, filAux, colAux,fichaActual);
										encontrado = true;
									 }
									 
									 columnaDestino = colAux2;
									 
									 if (esMovimientoAtaque(filAux,colAux2)) {
										filSig = filAux;
										colSig = colAux2;
										calculaCasillaSiguiente(fil,col,filAux,colAux2);
										m.setNuevaSolucion(Integer.MIN_VALUE, fil, col, filSig, colSig, filAux, colAux2,fichaActual);
										encontrado = true;
									 }
									 
									 filaDestino = filAux2;
									 
									 if (esMovimientoAtaque(filAux2,colAux2)) {
										filSig = filAux2;
										colSig = colAux2;
										calculaCasillaSiguiente(fil,col,filAux2,colAux2);
										m.setNuevaSolucion(Integer.MIN_VALUE, fil, col, filSig, colSig, filAux2, colAux2,fichaActual);
										encontrado = true;
									 }
									 
									 columnaDestino = colAux;
									 
									 if (esMovimientoAtaque(filAux2,colAux)) {
										filSig = filAux2;
										colSig = colAux;
										calculaCasillaSiguiente(fil,col,filAux2,colAux);
										m.setNuevaSolucion(Integer.MIN_VALUE, fil, col, filSig, colSig, filAux2, colAux,fichaActual);
										encontrado = true;
									 }
									 
									 break;
				}
				
					
			} // for 2
	}
	
	boolean hayFichaDelContrincanteEn(int fil,int col){
		
		boolean hayFicha = false;
		
		if (estaEnRango(fil) && estaEnRango(col)){
			
			TFicha ficha = getCasilla(fil,col);
			
			switch (ficha){
			
				case damaRoja :
				case peonRojo : if (jugActual == TipoJugador.Negro) 
									hayFicha = true;
								  else
									hayFicha = false;
								break;
				case damaNegra :
				case peonNegro : if (jugActual == TipoJugador.Rojo) 
									hayFicha = true;
				 				  else
				 					hayFicha= false;
								 break;
			}
		}
		
		return hayFicha;
	}
	
	boolean esDamaDelContrincante(int fil, int col){
		
		boolean resultado = false;
		if (estaEnRango(fil) && estaEnRango(col))
			switch(jugActual){
				case Rojo : resultado = getCasilla(fil,col) == TFicha.damaNegra;
							break;
				case Negro : resultado = getCasilla(fil,col) == TFicha.damaRoja;
							break;
			}
		
		return resultado;
	}
	
	
	// HEURISTICA HIBRIDA
	// ******************************************************************************
	void pruebaHHibrida(int fil,int col, MovimientoPC m, int nivel){
		
		TFicha fichaActual = getCasilla(fil,col);
		int filAux;
		int filAux2;
		int colAux;
		int colAux2;
		
		seguirTirando = false;
		
		switch(fichaActual){
			case peonRojo: filAux = fil+1;
						   colAux = col+1;
					   	   colAux2 = col-1;
					   	   
					   	   // 1 - Probamos movimiento NORMAL con peon rojo
					   	   //     *******************************************
					   	   filaOrigen = fil;
						   columnaOrigen = col;
						   
						   filaDestino = filAux;
						   columnaDestino = colAux;
						   
					   	   if (esMovimientoNormal(filAux,colAux) && !esMovNormalPeligroso(filAux,colAux,fichaActual))
					   		   HHibrida_ejecutaMovNormalPC(fil,col,filAux,colAux,m,nivel);  
					   	   
					   	   // Restablecemos todas por "hayAtaqueMultiplePC()"
					   	   filaOrigen = fil;
						   columnaOrigen = col;
						   
						   filaDestino = filAux;
						   columnaDestino = colAux2;
						   
					   	   if (esMovimientoNormal(filAux,colAux2) && !esMovNormalPeligroso(filAux,colAux2,fichaActual))
					   		   HHibrida_ejecutaMovNormalPC(fil,col,filAux,colAux2,m,nivel); 
					   	   
					   	   
					   	   // 2 - Probamos movimiento de ATAQUE con peon rojo
					   	   //     *******************************************
					   	   filaOrigen = fil;
						   columnaOrigen = col;
						   
						   filaDestino = filAux;
						   columnaDestino = colAux;
							
					   	   if (esMovimientoAtaque(filAux,colAux))
					   		   HHibrida_ejecutaMovAtaquePC(fil,col,filAux,colAux,m,nivel); 
					   	   
					   	   filaOrigen = fil;
						   columnaOrigen = col;
						   
						   filaDestino = filAux;
						   columnaDestino = colAux2;
					   	   
					   	   if (esMovimientoAtaque(filAux,colAux2))
					   		   HHibrida_ejecutaMovAtaquePC(fil,col,filAux,colAux2,m,nivel);  
					   	   
					   	   break;
					   	   
			case peonNegro: filAux = fil-1;
						   colAux = col+1;
					   	   colAux2 = col-1;
					   	   
					   	   // 1 - Probamos movimiento NORMAL con peon negro (Usuario)
					   	   //     *******************************************
					   	   filaOrigen = fil;
						   columnaOrigen = col;
						   
						   filaDestino = filAux;
						   columnaDestino = colAux;
						   
					   	   if (esMovimientoNormal(filAux,colAux) && !esMovNormalPeligroso(filAux,colAux,fichaActual))
					   		   HHibrida_ejecutaMovNormalUsuario(fil,col,filAux,colAux,m,nivel);
					   	   
					   	   // Restablecemos todas por "hayAtaqueMultiplePC()"
					   	   filaOrigen = fil;
						   columnaOrigen = col;
						   
						   filaDestino = filAux;
						   columnaDestino = colAux2;
						   
					   	   if (esMovimientoNormal(filAux,colAux2) && !esMovNormalPeligroso(filAux,colAux2,fichaActual))
					   		   HHibrida_ejecutaMovNormalUsuario(fil,col,filAux,colAux2,m,nivel);
					   	   
					   	   
					   	   // 2 - Probamos movimiento de ATAQUE con peon negro (Usuario)
					   	   //     *******************************************
					   	   filaOrigen = fil;
						   columnaOrigen = col;
						   
						   filaDestino = filAux;
						   columnaDestino = colAux;
							
					   	   if (esMovimientoAtaque(filAux,colAux))
					   		   HHibrida_ejecutaMovAtaqueUsuario(fil,col,filAux,colAux,m,nivel);
					   	   
					   	   filaOrigen = fil;
						   columnaOrigen = col;
						   
						   filaDestino = filAux;
						   columnaDestino = colAux2;
					   	   
					   	   if (esMovimientoAtaque(filAux,colAux2))
					   		   HHibrida_ejecutaMovAtaqueUsuario(fil,col,filAux,colAux2,m,nivel);
					   	   
					   	   break;
					   	   
			case damaRoja : filAux = fil-1;
							filAux2 = fil+1;
							colAux = col+1;
							colAux2 = col-1;
							
						   	// 1 - Probamos movimiento NORMAL con dama roja
						   	//     *******************************************
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							   
							filaDestino = filAux;
							columnaDestino = colAux;
							
						   	if (esMovimientoNormal(filAux,colAux) && !esMovNormalPeligroso(filAux,colAux,fichaActual))
						   		HHibrida_ejecutaMovNormalPC(fil,col,filAux,colAux,m,nivel);  
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							   
							filaDestino = filAux;
							columnaDestino = colAux2;
						   	
						   	if (esMovimientoNormal(filAux,colAux2) && !esMovNormalPeligroso(filAux,colAux2,fichaActual))
						   		HHibrida_ejecutaMovNormalPC(fil,col,filAux,colAux2,m,nivel); 
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							
							filaDestino = filAux2;
							columnaDestino = colAux;
						   	
						   	if (esMovimientoNormal(filAux2,colAux) && !esMovNormalPeligroso(filAux2,colAux,fichaActual))
						   		HHibrida_ejecutaMovNormalPC(fil,col,filAux2,colAux,m,nivel);   
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							   
							filaDestino = filAux2;
							columnaDestino = colAux2;
						   	
						   	if (esMovimientoNormal(filAux2,colAux2) && !esMovNormalPeligroso(filAux2,colAux2,fichaActual))
						   		HHibrida_ejecutaMovNormalPC(fil,col,filAux2,colAux2,m,nivel); 
						 							
							// 2 - Probamos movimiento de ATAQUE con dama rojo (PC)
						   	//     *******************************************
						   	filaOrigen = fil;
							columnaOrigen = col;   
							
							filaDestino = filAux;
							columnaDestino = colAux;
							
						   	if (esMovimientoAtaque(filAux,colAux))
						   		HHibrida_ejecutaMovAtaquePC(fil,col,filAux,colAux,m,nivel);  
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							   
							filaDestino = filAux2;
							columnaDestino = colAux;
						   	
						   	if (esMovimientoAtaque(filAux2,colAux))
						   		HHibrida_ejecutaMovAtaquePC(fil,col,filAux2,colAux,m,nivel);
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							
						   	columnaDestino = colAux2;
						   	filaDestino = filAux;
						   	
						   	if (esMovimientoAtaque(filAux,colAux2))
						   		HHibrida_ejecutaMovAtaquePC(fil,col,filAux,colAux2,m,nivel);
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							
						   	filaDestino = filAux2;
						   	columnaDestino = colAux2;
						   	
						   	if (esMovimientoAtaque(filAux2,colAux2))
						   		HHibrida_ejecutaMovAtaquePC(fil,col,filAux2,colAux2,m,nivel);
							    
					
						   	break;	
						   	
			case damaNegra : filAux = fil-1;
							filAux2 = fil+1;
							colAux = col+1;
							colAux2 = col-1;
							
						   	// 1 - Probamos movimiento NORMAL con dama roja
						   	//     *******************************************
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							   
							filaDestino = filAux;
							columnaDestino = colAux;
							
						   	if (esMovimientoNormal(filAux,colAux) && !esMovNormalPeligroso(filAux,colAux,fichaActual))
						   		HHibrida_ejecutaMovNormalUsuario(fil,col,filAux,colAux,m,nivel);
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							   
							filaDestino = filAux;
							columnaDestino = colAux2;
						   	
						   	if (esMovimientoNormal(filAux,colAux2) && !esMovNormalPeligroso(filAux,colAux2,fichaActual))
						   		HHibrida_ejecutaMovNormalUsuario(fil,col,filAux,colAux2,m,nivel);
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							
							filaDestino = filAux2;
							columnaDestino = colAux;
						   	
						   	if (esMovimientoNormal(filAux2,colAux) && !esMovNormalPeligroso(filAux2,colAux,fichaActual))
						   		HHibrida_ejecutaMovNormalUsuario(fil,col,filAux2,colAux,m,nivel);   
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							   
							filaDestino = filAux2;
							columnaDestino = colAux2;
						   	
						   	if (esMovimientoNormal(filAux2,colAux2) && !esMovNormalPeligroso(filAux2,colAux2,fichaActual))
						   		HHibrida_ejecutaMovNormalUsuario(fil,col,filAux2,colAux2,m,nivel);
						 							
							// 2 - Probamos movimiento de ATAQUE con peon rojo
						   	//     *******************************************
						   	filaOrigen = fil;
							columnaOrigen = col;   
							
							filaDestino = filAux;
							columnaDestino = colAux;
							
						   	if (esMovimientoAtaque(filAux,colAux))
						   		HHibrida_ejecutaMovAtaqueUsuario(fil,col,filAux,colAux,m,nivel);
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							   
							filaDestino = filAux2;
							columnaDestino = colAux;
						   	
						   	if (esMovimientoAtaque(filAux2,colAux))
						   		HHibrida_ejecutaMovAtaqueUsuario(fil,col,filAux2,colAux,m,nivel);
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							
						   	columnaDestino = colAux2;
						   	filaDestino = filAux;
						   	
						   	if (esMovimientoAtaque(filAux,colAux2))
						   		HHibrida_ejecutaMovAtaqueUsuario(fil,col,filAux,colAux2,m,nivel);
						   	
						   	filaOrigen = fil;
							columnaOrigen = col;
							
						   	filaDestino = filAux2;
						   	columnaDestino = colAux2;
						   	
						   	if (esMovimientoAtaque(filAux2,colAux2))
						   		HHibrida_ejecutaMovAtaqueUsuario(fil,col,filAux2,colAux2,m,nivel);
					
						   	break;
		}
		
		if (m.getFilaOrigen() == -1 && m.getColumnaOrigen() == -1)
			calculaMovimientoSeguro(m);
	}	
	
	void HHibrida_ejecutaMovNormalPC(int fil,int col,int filAux,int colAux,MovimientoPC m,int nivel){

		TFicha fichaActual = getCasilla(fil,col);
		int solParcial = 0;
   	   	MovimientoPC movActual = new MovimientoPC(sumaH,fil,col,filAux,colAux,-1,-1,fichaActual);  

   	   	if (nivel == nivel_k)
   	   		ramaActual = new MovimientoPC(movActual);
   	   	
   		simulaMovimientoNormal(movActual);
   		
	   	if (!hayJugadaContrincante())
	   		sumaH += solParcial += 7;
	   	
   	   	jugActual = TipoJugador.Negro;
   	   	MovimientoPC mAux = pruebaJugadasUsuario(nivel-1,m.SolOptima());
   	   	if (mAux.SolOptima() > m.SolOptima())
   	   		m.setNuevaSolucion(mAux.SolOptima(), mAux.getFilaOrigen(), mAux.getColumnaOrigen(),
   	   							mAux.getFilaDestino(),mAux.getColumnaDestino(),
   	   							   mAux.getFilaAtaque(),mAux.getColumnaAtaque(),
   	   							   mAux.getFichaActual());
   	   	deshacerMovimientoNormal(movActual);
   	   	jugActual = TipoJugador.Rojo;
   	   	

	}
	
	void HHibrida_ejecutaMovAtaquePC(int fil,int col,int filAux,int colAux,MovimientoPC m,int nivel){
		
		int solParcial = 5;
		TFicha fichaActual = getCasilla(fil,col);
		
		filSig = filAux;
	   	colSig = colAux;
 		calculaCasillaSiguiente(fil,col,filAux,colAux); 	
		   	 
		sumaH += solParcial;
		MovimientoPC movActual = new MovimientoPC(sumaH,fil,col,filSig,colSig,filAux,colAux,fichaActual);
		movActual.setFichaAtacada(getCasilla(filAux,colAux));
			
	   	if (nivel == nivel_k)
	   		ramaActual = new MovimientoPC(movActual);
		   	
	   	simulaMovimientoAtaque(movActual);
	   	if (seguirTirando)
	   	   	simulaMultiAtaque(movActual);
	   	
	   	if (esGanador())
	   		sumaH += solParcial += 50;
	   	else
	   	if (!hayJugadaContrincante())
	   		sumaH += solParcial += 7;
		   	
	   	jugActual = TipoJugador.Negro;
	   	MovimientoPC mAux = pruebaJugadasUsuario(nivel-1,m.SolOptima());
	   	if (mAux.SolOptima() > m.SolOptima())
	   		m.setNuevaSolucion(mAux.SolOptima(), mAux.getFilaOrigen(), mAux.getColumnaOrigen(),
	   						   mAux.getFilaDestino(),mAux.getColumnaDestino(),
	   						   mAux.getFilaAtaque(),mAux.getColumnaAtaque(),
	   						   mAux.getFichaActual());
		   	
	   	if (!movActual.esPilaMultiAtaqueVacia())
	   		deshacerMultiAtaque(movActual);
		   	
	   	deshacerMovimientoAtaque(movActual);
	   	jugActual = TipoJugador.Rojo;
	   	sumaH -= solParcial;
	}
	
	void HHibrida_ejecutaMovNormalUsuario(int fil,int col,int filAux,int colAux,MovimientoPC m,int nivel){

		TFicha fichaActual = getCasilla(fil,col);
		int solParcial = 0;
   	   	MovimientoPC movActual = new MovimientoPC(sumaH,fil,col,filAux,colAux,-1,-1,fichaActual);  

   	   	if (nivel == nivel_k)
   	   		ramaActual = new MovimientoPC(movActual);
   	   	
   		simulaMovimientoNormal(movActual);
   		
	   	if (!hayJugadaContrincante())
	   		sumaH += solParcial += 7;
	   	
   	   	jugActual = TipoJugador.Rojo;
   	   	MovimientoPC mAux = pruebaJugadasPC(nivel-1,m.SolOptima());
   	   	if (mAux.SolOptima() < m.SolOptima())
   	   		m.setNuevaSolucion(mAux.SolOptima(), mAux.getFilaOrigen(), mAux.getColumnaOrigen(),
   	   							mAux.getFilaDestino(),mAux.getColumnaDestino(),
   	   							   mAux.getFilaAtaque(),mAux.getColumnaAtaque(),
   	   							   mAux.getFichaActual());
   	   	
   	   	deshacerMovimientoNormal(movActual);
   	   	jugActual = TipoJugador.Negro;
   	   	

	}
	
	void HHibrida_ejecutaMovAtaqueUsuario(int fil,int col,int filAux,int colAux,MovimientoPC m,int nivel){
		
		int solParcial = 5;
		TFicha fichaActual = getCasilla(fil,col);
		
		filSig = filAux;
	   	colSig = colAux;
 		calculaCasillaSiguiente(fil,col,filAux,colAux); 	
		   	 
		sumaH += solParcial;
		MovimientoPC movActual = new MovimientoPC(sumaH,fil,col,filSig,colSig,filAux,colAux,fichaActual);
		movActual.setFichaAtacada(getCasilla(filAux,colAux));
			
	   	if (nivel == nivel_k)
	   		ramaActual = new MovimientoPC(movActual);
		   	
	   	simulaMovimientoAtaque(movActual);
	   	if (seguirTirando)
	   	   	simulaMultiAtaque(movActual);
	   	
	   	if (esGanador())
	   		sumaH += solParcial += 50;
	   	else
	   	if (!hayJugadaContrincante())
	   		sumaH += solParcial += 7;
		   	
	   	jugActual = TipoJugador.Rojo;
	   	MovimientoPC mAux = pruebaJugadasPC(nivel-1,m.SolOptima());
	   	if (mAux.SolOptima() > m.SolOptima())
	   		m.setNuevaSolucion(mAux.SolOptima(), mAux.getFilaOrigen(), mAux.getColumnaOrigen(),
	   						   mAux.getFilaDestino(),mAux.getColumnaDestino(),
	   						   mAux.getFilaAtaque(),mAux.getColumnaAtaque(),
	   						   mAux.getFichaActual());
		   	
	   	if (!movActual.esPilaMultiAtaqueVacia())
	   		deshacerMultiAtaque(movActual);
		   	
	   	deshacerMovimientoAtaque(movActual);
	   	jugActual = TipoJugador.Negro;
	   	sumaH -= solParcial;
	}
	
	boolean esMovNormalPeligroso(int filDst,int colDst,TFicha fichaActual){
		
		boolean esPeligroso = false;
		switch (fichaActual){
			case damaRoja :
			case peonRojo :	esPeligroso = hayFichaDelContrincanteEn(filDst+1,colDst+1) ||
										  hayFichaDelContrincanteEn(filDst+1,colDst-1) ||
										  esDamaDelContrincante(filDst-1,colDst+1) ||
										  esDamaDelContrincante(filDst-1,colDst-1);
							break;
			case damaNegra :
			case peonNegro : esPeligroso = hayFichaDelContrincanteEn(filDst-1,colDst+1) ||
					  					   hayFichaDelContrincanteEn(filDst-1,colDst-1) ||
					  					   esDamaDelContrincante(filDst+1,colDst+1) ||
					  					   esDamaDelContrincante(filDst+1,colDst-1);
							break;

		}
		
		return esPeligroso;
		
	}
	
	// TAREAS PARA EL CALCULO DE HEURISTICAS MULTINIVEL
	// *******************************************************************************
	void simulaMovimientoNormal(MovimientoPC m){

		setCasilla(m.getFilaDestino(),m.getColumnaDestino(),m.getFichaActual());
		setCasilla(m.getFilaOrigen(),m.getColumnaOrigen(),TFicha.Libre);
		
		if (esFinalDeTablero(m))
			coronarDama(m);
			
	}
	
	void simulaMovimientoAtaque(MovimientoPC m){
		
		seguirTirando = false;
		
		setCasilla(m.getFilaOrigen(),m.getColumnaOrigen(),TFicha.Libre);
		
		m.setFichaAtacada(getCasilla(m.getFilaAtaque(),m.getColumnaAtaque()));
		setCasilla(m.getFilaAtaque(),m.getColumnaAtaque(),TFicha.Libre);
		decrementarFichasAtacadas();

		if (esFinalDeTablero(m))
			coronarDama(m);
		else
			seguirTirando = hayAtaqueMultiplePC(m.getFilaDestino(),m.getColumnaDestino(),m.getFichaActual());
				
		filaOrigen = m.getFilaOrigen();
		columnaOrigen = m.getColumnaOrigen();
				
		filaDestino = m.getFilaDestino();
		columnaDestino = m.getColumnaDestino();

	}
	
	void simulaMultiAtaque(MovimientoPC m){

		m.inicializaPilaMultiAtaque();
		MovimientoPC m2 = calcularMultiAtaquePC(m);
		
		while (seguirTirando){
			m.apilaMultiAtaque(m2);
			simulaMovimientoAtaque(m2);
			m2 = calcularMultiAtaquePC(m2);
			switch(modoJuego){
				case HAtaque : if (seguirTirando)
								  sumaH += 3;
							   else
								  sumaH += 2;
							   break;
				case HHibrida: if (seguirTirando)
					  			  sumaH +=10;
				   			   break;
			}
		}
		
		filaOrigen = m.getFilaOrigen();
		columnaOrigen = m.getColumnaOrigen();
				
		filaDestino = m.getFilaDestino();
		columnaDestino = m.getColumnaDestino();
	}
	
	void deshacerMovimientoNormal(MovimientoPC m){
		
		setCasilla(m.getFilaOrigen(),m.getColumnaOrigen(),m.getFichaActual());
		setCasilla(m.getFilaDestino(),m.getColumnaDestino(),TFicha.Libre);
		
	}
	
	void deshacerMovimientoAtaque(MovimientoPC m){
		
		TFicha fichaActual = m.getFichaActual();
		
		setCasilla(m.getFilaOrigen(),m.getColumnaOrigen(),fichaActual);
		setCasilla(m.getFilaDestino(),m.getColumnaDestino(),TFicha.Libre);
		setCasilla(m.getFilaAtaque(),m.getColumnaAtaque(),m.getFichaAtacada());
		
		incrementarFichasAtacadas(m.getFichaAtacada());
		
	}
	
	void deshacerMultiAtaque(MovimientoPC m){
		
		boolean ultimoAtaque = true;
		
		while (!m.esPilaMultiAtaqueVacia()){
			MovimientoPC m2 = m.dameMultiAtaque();
			deshacerMovimientoAtaque(m2);
			
			switch(modoJuego){
				case HAtaque : if (ultimoAtaque){
								   sumaH -= 2;
								   ultimoAtaque = false;
								   }
							   else 
								   sumaH -= 3;
							   break;
				case HHibrida: if (seguirTirando)
					  			  sumaH -=10;
				   			   break;
			}

			
			m.desapilaMultiAtaque();
		}
	}
	
	
	boolean esMovimientoFinal(){
		return esGanadorContrincante() || !hayJugada();
	}	

	boolean esGanadorContrincante(){
		
		boolean ganador = false;
		
		switch (jugActual){
		
			case Negro : if (fichasNegras == 0)
							ganador = true;
						break;
			case Rojo : if (fichasRojas == 0)
							ganador = true;
						break;
		}
		
		return ganador;
	}
	
	
	boolean hayJugada(){ // Comprueba si el jugador actual esta bloqueado (no tiene jugada)
		
		TFicha ficha;
		int filAux;
		int filAux2;
		int colAux;
		int colAux2;
		int numJugadas = 0;
		boolean hayJugada= true;
		
		filSig = filaOrigen;
		colSig = filaOrigen;
		
		int filSig2 = filaDestino;
		int colSig2 = columnaDestino;
		
		switch (jugActual){ // Si es el jugActual esNegro, comprobamos si el rojo esta bloqueado y viceversa
			case Rojo:	for (int filActual = 0; filActual < tamTablero && numJugadas == 0; filActual++){
							for (int colActual = 0; colActual < tamTablero && numJugadas == 0; colActual++){
								
								ficha = getCasilla(filActual,colActual);
								filaOrigen = filActual;
								columnaOrigen = colActual;
								
								switch (ficha){
								
									case peonRojo: filAux = filActual+1;
											   	   colAux = colActual+1;
											   	   colAux2 = colActual-1;
											   	   
											   	   filaDestino = filAux;
											   	   columnaDestino = colAux;
											   	   
										   		   if (esMovAtaqueContrincante(filAux,colAux) || esMovimientoNormal(filAux,colAux))
										   			  numJugadas++;
										   		   
										   		   columnaDestino = colAux2;
										   		   
										   		   if (esMovAtaqueContrincante(filAux,colAux2) || esMovimientoNormal(filAux,colAux2))
										   			  numJugadas++; 
										   		   
											   	   break;
											   
									case damaRoja : filAux = filActual+1;
													filAux2 = filActual-1;
								   	   				colAux = colActual+1;
								   	   				colAux2 = colActual-1;
												   	
								   	   				filaDestino = filAux;
												   	columnaDestino = colAux;
												   	
										   		   if (esMovAtaqueContrincante(filAux,colAux) || esMovimientoNormal(filAux,colAux))
										   			  numJugadas++;
										   		   
										   		   columnaDestino = colAux2;
										   		   
										   		   if (esMovAtaqueContrincante(filAux,colAux2) || esMovimientoNormal(filAux,colAux2))
										   			  numJugadas++; 
										   		   
										   		   filaDestino = filAux2;
										   		   
										   		   if (esMovAtaqueContrincante(filAux2,colAux2) || esMovimientoNormal(filAux2,colAux2))
										   			  numJugadas++; 
										   		   
										   		   columnaDestino = colAux;
										   		   
										   		   if (esMovAtaqueContrincante(filAux2,colAux) || esMovimientoNormal(filAux2,colAux))
										   			  numJugadas++; 

										   		   
										   		   
											   	   break;
								} // switch fichasRojas
							} // for
						} // for
						break;
						
			case Negro:	for (int filActual = 0; filActual < tamTablero && numJugadas == 0; filActual++){
							for (int colActual = 0; colActual < tamTablero && numJugadas == 0; colActual++){
								
								ficha = getCasilla(filActual,colActual);
								filaOrigen = filActual;
								columnaOrigen = colActual;
								
								switch (ficha){
					
									case peonNegro: filAux = filActual-1;
								   	   				colAux = colActual+1;
								   	   				colAux2 = colActual-1;

												   	filaDestino = filAux;
												   	columnaDestino = colAux;
												   	   
											   		if (esMovAtaqueContrincante(filAux,colAux) || esMovimientoNormal(filAux,colAux))
											   			numJugadas++;
											   		   
											   		columnaDestino = colAux2;
											   		   
											   		if (esMovAtaqueContrincante(filAux,colAux2) || esMovimientoNormal(filAux,colAux2))
											   			numJugadas++; 
								   	   				
								   	   				break;
								   
									case damaNegra : filAux = filActual+1;
													 filAux2 = filActual-1;
													 colAux = colActual+1;
													 colAux2 = colActual-1;

									   	   			 filaDestino = filAux;
													 columnaDestino = colAux;
													   	
											   		 if (esMovAtaqueContrincante(filAux,colAux) || esMovimientoNormal(filAux,colAux))
											   			 numJugadas++;
											   		   
											   		 columnaDestino = colAux2;
											   		   
											   		 if (esMovAtaqueContrincante(filAux,colAux2) || esMovimientoNormal(filAux,colAux2))
											   			 numJugadas++; 
											   		   
											   		 filaDestino = filAux2;
											   		   
											   		 if (esMovAtaqueContrincante(filAux2,colAux2) || esMovimientoNormal(filAux2,colAux2))
											   			 numJugadas++; 
											   		   
											   		 columnaDestino = colAux;
											   		   
											   		 if (esMovAtaqueContrincante(filAux2,colAux) || esMovimientoNormal(filAux2,colAux))
											   			 numJugadas++; 
							   	   					 
								   	   				 break;
								} // switch fichasNegras
							} // for
						} // for
						break;
		} // switchJugador
		
		if (numJugadas == 0)
			hayJugada = false;
		
		filaOrigen = filSig;
		columnaOrigen = colSig;
		
		filaDestino = filSig2;
		columnaDestino = colSig2;
		
		return hayJugada;
			
	}
	
	


} // class ComprobadorMovimientos
