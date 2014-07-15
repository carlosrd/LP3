package Movimiento;

public class TableroJuego {
	
	TFicha tablero[][];
	int tamTablero;
	int fichasRojas;
	int fichasNegras;
	
	public enum TFicha {Libre,peonRojo,peonNegro,damaRoja,damaNegra};
	
	public TableroJuego(int tam){
		
		tamTablero = tam;
		fichasRojas = fichasNegras = tamTablero;
		tablero = new TFicha[tamTablero][tamTablero];
		
		for (int i = 0; i < tamTablero; i++)
			for (int j = 0; j < tamTablero; j++)
				tablero[i][j] = TFicha.Libre;
	}
	
	
	// CONSULTORAS
	// ***********************************************************************
	public TFicha getCasilla(int fil,int col){
		return tablero[col][fil];		// Las posiciones en la matriz vienen invertidas
	}
	
	public int getFichasRojas(){
		return fichasRojas;
	}
	
	public int getFichasNegras(){
		return fichasNegras;
	}
	
	
	// MODIFICADORAS
	// ***********************************************************************
	public void setCasilla(int fil, int col,TFicha ficha){
		tablero[col][fil] = ficha;		// Las posiciones en la matriz vienen invertidas
	}
	
	
	// METODOS
	// ***********************************************************************
	
	public void inicializar(){
		for (int i = 0; i < tamTablero; i++)
			for (int j = 0; j < tamTablero; j++)
				tablero[i][j] = TFicha.Libre;
		
	
		int filRojo = 0;
		int filNegro = tamTablero-2;
		for (int col = 0; col < tamTablero; col++){
			tablero[col][filRojo] = TFicha.peonRojo;
			tablero[col][filNegro] = TFicha.peonNegro;
			filRojo++;
			filNegro++;
			if (filRojo == 2){
				filRojo = 0;
				filNegro = tamTablero-2;
				}
			
			}
/*
		tablero[2][0] = TFicha.peonRojo;
		tablero[3][1] = TFicha.peonRojo;
		tablero[2][2] = TFicha.peonNegro;
		tablero[3][5] = TFicha.peonNegro;
		fichasRojas = fichasNegras = 2;*/
		
		//******************************
		/*tablero[0][0] = TFicha.peonRojo;
		tablero[4][0] = TFicha.peonRojo;
		tablero[6][0] = TFicha.peonRojo;
		tablero[5][1] = TFicha.peonNegro;
		
		tablero[1][5] = TFicha.damaRoja;
		tablero[3][5] = TFicha.peonNegro;
		
		fichasRojas = 4;
		fichasNegras = 2;*/
		
		fichasRojas = fichasNegras = tamTablero;
		 

		
	}

}
