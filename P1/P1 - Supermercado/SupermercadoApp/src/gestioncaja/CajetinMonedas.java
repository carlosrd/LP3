package gestioncaja;

public class CajetinMonedas {
	// Atributos
	int[] cajon;
	int numElementos;
	final static int max = 12;

	// Constructoras
	// ***********************************************************************************
	
	// Inicializa el cajetin de monedas a 0 y con todas las monedas a 0
	public CajetinMonedas(){
		cajon = new int[]{0,0,0,0,0,0,0,0,0,0,0,0};
		numElementos = 0;
		}
	
	// Inicializa el cajetin de monedas al 
	public CajetinMonedas(int[] ini){
		cajon = new int[12];
		int suma = 0;
		for(int i = 0; i < 12; i++){
			cajon[i] = ini[i];
			suma += cajon[i];
			}
		numElementos = suma;
		}
	
	// Consultoras
	// ***********************************************************************************
	public int getNumMonedas(){return numElementos;}
	
	public int[] getCajetin(){return cajon;}
	
	// Modificadoras
	// ***********************************************************************************
	
	public void setCajetin(int[] cajetin){ 
		
		int suma = 0;
		for(int i=0; i<12; i++){
			cajon[i] = cajetin[i];
			suma += cajon[i];
		}
		numElementos = suma;
	}
	
	
	// Metodos
	// ***********************************************************************************
	
	// Inicializa el numElementos a MAX_INT para el primer caso de BackTracking
	public void inicializaNumElem(){
		numElementos = Integer.MAX_VALUE;
	}
	
	// Comprueba si el cajon actual es mejor (tiene menos monedas) que el que se le pasa por parametro
	public boolean esMejor(CajetinMonedas sol){
		return numElementos < sol.getNumMonedas();
	}
	
	// Comprueba si existen monedas del tipo dado por "moneda"
	public boolean hayMonedas(int moneda){
		return cajon[moneda] > 0;
	}
	
	// Introduce una moneda en el indice dado por "indexMoneda"
	public void meterMoneda(int indexMoneda){
		cajon[indexMoneda]++;
		numElementos++;
	}
	
	// Saca una moneda (si quedan) del indice dado por "indexMoneda"
	public void sacarMoneda(int indexMoneda){
		if (hayMonedas(indexMoneda)){
			cajon[indexMoneda]--;
			numElementos--;
		}
		
	}


}

