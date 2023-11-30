package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Tablero {
	// --- Códigos de colores ---
	
	private static final String RED = "\u001B[31m";
	private static final String BLUE = "\u001B[34m";
	private static final String CYAN = "\u001B[36m";
	public static final String BLACK = "\u001B[30m";
	public static final String WHITE = "\u001B[37m";
	
	public static final String RED_BOLD = "\033[1;31m";
	public static final String CYAN_BOLD="\033[1;36m";
	
	
	public static final String WHITE_BG ="\u001B[47m";
	private static final String RED_BG = "\033[41m";
	private static final String BLUE_BG = "\033[44m";
	private static final String BLACK_BG= "\033[40m";
	private static final String PURPLE_BG = "\033[45m";
	public static final String CYAN_BG = "\u001B[46m";
	
	private static final String RESET = "\u001B[0m";
	
	// ---------------------------
	
	private static final int VACIA =3;
	private static final int DISPONIBLE =4;
	
	
	private int[] casillas = new int[64];
	private int turn;

	
	public Tablero() {
		this.turn=0;
		loadPosicionInicial();
	}
	
	
	private void loadPosicionInicial() {
		Arrays.fill(this.casillas, VACIA);
		
		this.casillas[3*8+3]=1;
		this.casillas[4*8+4]=1;
		
		this.casillas[3*8+4]=0;
		this.casillas[4*8+3]=0;
		
		this.calcularCasillasDisponibles();
	}
	
	// Solo se pueden reclamar casillas en las cuales haya al menos una 
	// línea horizontal, vertical o diagonal ocupada entre la nueva casilla y otra casilla de ese jugador
	private void calcularCasillasDisponibles() {
		int jugadorActivo = getJugadorActivo();
		int jugadorOponente = getJugadorOponente();
		int x, y;
		
		// Primero se borran las casillas disponibles del turno anterior
		for(int i=0;i<64;i++) {
			if(casillas[i]==DISPONIBLE) {
				casillas[i]=VACIA;
			}
		}
		
		for(int i=0;i<64;i++) {
			if(casillas[i]==jugadorActivo) {
				// Una vez detectada una casilla reclamada por el jugador activo se comprueban en
				// las 8 direcciones cardinales a ver si hay una línea de casillas reclamadas
				// por el jugador opuesto. Al final de esas líneas se hallarán las casillas disponibles

				
				// Todas las comprobaciones direccionales siguen el mismo esquema
				
				// Diagonal superior izquierda
				x= i/8;
				y= i%8;
				while(x!=0 && y!=0 && casillas[(x-1)*8+y-1]==jugadorOponente) { // Se comprueba si se ha llegado al borde o si la siguiente casilla por esa dirección está reclamada por el jugador opuesto
					x--;
					y--; // Se avanza a la siguiente casilla
					
					if(x!=0 && y!=0 && casillas[(x-1)*8+y-1]==VACIA) { // Si la siguiente casilla está dentro del tablero y es vacía, se marca como disponible 
						casillas[(x-1)*8+(y-1)]=DISPONIBLE;
					}
				}
				
				// Vertical superior
				x= i/8;
				y= i%8;
				while(x!=0 && casillas[(x-1)*8+y]==jugadorOponente) { 
					x--;
					
					if(x!=0 && casillas[(x-1)*8+y]==VACIA) {  
						casillas[(x-1)*8+y]=DISPONIBLE;
					}
				}
				
				// Diagonal superior derecha
				x= i/8;
				y= i%8;
				while(x!=0 && y!=7 && casillas[(x-1)*8+y+1]==jugadorOponente) { 
					x--;
					y++;
					
					if(x!=0 && y!=7 && casillas[(x-1)*8+y+1]==VACIA) {  
						casillas[(x-1)*8+y+1]=DISPONIBLE;
					}
				}
				
				// Horizontal izquierda
				x= i/8;
				y= i%8;
				while(y!=0 && casillas[x*8+y-1]==jugadorOponente) { 
					y--;
					
					if(y!=0 && casillas[x*8+y-1]==VACIA) {
						casillas[x*8+y-1]=DISPONIBLE;
					}
				}
				
				// Horizontal derecha
				x= i/8;
				y= i%8;
				while(y!=7 && casillas[x*8+y+1]==jugadorOponente) { 
					y++;
					
					if(y!=7 && casillas[x*8+y+1]==VACIA) {
						casillas[x*8+y+1]=DISPONIBLE;
					}
				}
				
				// Diagonal inferior izquierda
				x= i/8;
				y= i%8;
				while( x!=7 && y!=0 && casillas[(x+1)*8+y-1]==jugadorOponente) {
					x++;
					y--;
					
					if( x!=7 && y!=0 && casillas[(x+1)*8+y-1]==VACIA) {
						casillas[(x+1)*8+y-1]=DISPONIBLE;
					}
				}
				
				// Vertical inferior 
				x= i/8;
				y= i%8;
				while(x!=7 && casillas[(x+1)*8+y]==jugadorOponente) { 
					x++;
					
					if(x!=7 && casillas[(x+1)*8+y]==VACIA) {  
						casillas[(x+1)*8+y]=DISPONIBLE;
					}
				}
				
				// Diagonal inferior derecha
				x= i/8;
				y= i%8;
				while( x!=7 && y!=7 && casillas[(x+1)*8+y+1]==jugadorOponente) {
					x++;
					y++;
					
					if( x!=7 && y!=7 && casillas[(x+1)*8+y+1]==VACIA) {
						casillas[(x+1)*8+y+1]=DISPONIBLE;
					}
				}
			}
		}
	}
	
	public boolean reclamarCasilla(int x, int y) {
		int jugadorActivo = getJugadorActivo();
		int jugadorOponente = getJugadorOponente();
		
		int xtemp=x;
		int ytemp=y;
		
		if(x>=0 && x<=7 && y>=0 && y<=7 && this.casillas[x*8+y]==DISPONIBLE) {
			this.casillas[x*8+y] = jugadorActivo;
			
			
			// Se sigue un proceso similar al del calculo de casillas disponibles para capturar las casillas flanqueadas
			
			List<Integer> reclamadas = new ArrayList<Integer>();
			List<Integer> aReclamar = new ArrayList<Integer>();
			
			// Diagnoal superior izquierda
			while(x!=0 && y!=0 && casillas[(x-1)*8+y-1]==jugadorOponente) { 
				x--;
				y--; // Se avanza a la siguiente casilla
				
				aReclamar.add(x*8+y);
				
				if(x!=0 && y!=0 && casillas[(x-1)*8+y-1]==jugadorActivo) {  
					reclamadas.addAll(aReclamar);
				}
			}
			
			// Vertical superior
			aReclamar.clear();
			x= xtemp;
			y= ytemp;
			while(x!=0 && casillas[(x-1)*8+y]==jugadorOponente) { 
				x--;
				aReclamar.add(x*8+y);
				if(x!=0 && casillas[(x-1)*8+y]==jugadorActivo) {  
					reclamadas.addAll(aReclamar);
				}
			}
			
			// Diagonal superior derecha
			aReclamar.clear();
			x= xtemp;
			y= ytemp;
			while(x!=0 && y!=7 && casillas[(x-1)*8+y+1]==jugadorOponente) { 
				x--;
				y++;
				aReclamar.add(x*8+y);
				if(x!=0 && y!=7 && casillas[(x-1)*8+y+1]==jugadorActivo) {  
					reclamadas.addAll(aReclamar);
				}
			}
			
			// Horizontal izquierda
			aReclamar.clear();
			x= xtemp;
			y= ytemp;
			while(y!=0 && casillas[x*8+y-1]==jugadorOponente) { 
				y--;
				aReclamar.add(x*8+y);
				if(y!=0 && casillas[x*8+y-1]==jugadorActivo) {
					reclamadas.addAll(aReclamar);
				}
			}
			
			// Horizontal derecha
			aReclamar.clear();
			x= xtemp;
			y= ytemp;
			while(y!=7 && casillas[x*8+y+1]==jugadorOponente) { 
				y++;
				aReclamar.add(8*x+y);
				if(y!=7 && casillas[x*8+y+1]==jugadorActivo) {
					reclamadas.addAll(aReclamar);
				}
			}
			
			// Diagonal inferior izquierda
			aReclamar.clear();
			x= xtemp;
			y= ytemp;
			while( x!=7 && y!=0 && casillas[(x+1)*8+y-1]==jugadorOponente) {
				x++;
				y--;
				aReclamar.add(x*8+y);
				if( x!=7 && y!=0 && casillas[(x+1)*8+y-1]==jugadorActivo) {
					reclamadas.addAll(aReclamar);
				}
			}
			
			// Vertical inferior
			aReclamar.clear();
			x= xtemp;
			y= ytemp;
			while(x!=7 && casillas[(x+1)*8+y]==jugadorOponente) { 
				x++;
				aReclamar.add(x*8+y);
				if(x!=7 && casillas[(x+1)*8+y]==jugadorActivo) {  
					reclamadas.addAll(aReclamar);
				}
			}
			
			// Diagonal inferior derecha
			aReclamar.clear();
			x= xtemp;
			y= ytemp;
			while( x!=7 && y!=7 && casillas[(x+1)*8+y+1]==jugadorOponente) {
				x++;
				y++;
				aReclamar.add(x*8+y);
				if( x!=7 && y!=7 && casillas[(x+1)*8+y+1]==jugadorActivo) {
					reclamadas.addAll(aReclamar);
				}
			}
			
			for(int i=0;i<reclamadas.size();i++) {
				this.casillas[reclamadas.get(i)] = jugadorActivo;
			}
			
			avanzarTurno();
			return true;
		}
		return false;
	}
	
	public int getJugadorActivo() {
		return turn%2;
	}
	
	public int getJugadorOponente() {
		return (turn+1)%2;
	}
	
	public void avanzarTurno() {
		this.turn++;
		this.calcularCasillasDisponibles();
	}

	public boolean existenCasillasDisponibles() {
		for(int i=0;i<64;i++) {
			if(this.casillas[i]==DISPONIBLE) {
				return true;
			}
		}
		return false;
	}
	
	public boolean existenCasillasVacías() {
		for(int i=0;i<64;i++) {
			if(this.casillas[i]==VACIA || this.casillas[i]==DISPONIBLE) {
				return true;
			}
		}
		return false;
	}
	
	public int[] getPuntuacion() {
		int rojas=0;
		int azules=0;
		
		
		for(int i=0;i<64;i++) {
			if(this.casillas[i]==0) {
				rojas++;
			}else if(this.casillas[i]==1) {
				azules++;
			}
		}
		return new int[]{rojas,azules};
	}
	
	public String toString() {
		
		int[] puntuacion= this.getPuntuacion();
		StringBuilder tablero = new StringBuilder();
		tablero.append("                 _________            \r\n");
		tablero.append("     +---+---+-->|Othello|<--+---+---+\r\n");
		tablero.append("     +---+---+---+-------+---+---+---+\r\n");
		tablero.append("     | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 |\r\n");
		for(int i=0;i<8;i++) {
			tablero.append("+---++---+---+---+---+---+---+---+---++\r\n");
			tablero.append("| ");
			tablero.append(i);
			tablero.append(" |");
			for(int j=0;j<8;j++) {
				
				if(casillas[i*8+j]==0) {
					tablero.append("|");
					tablero.append(RED_BG);
					tablero.append("   ");
				}else if(casillas[i*8+j]==1) {
					tablero.append("|");
					tablero.append(CYAN_BG);
					tablero.append("   ");
				}else if(casillas[i*8+j]==DISPONIBLE) {
					tablero.append("|");
					tablero.append(" X ");
				}else {
					tablero.append("|");
					tablero.append("   ");
				}
				tablero.append(RESET);
			}
			tablero.append("||\r\n");
		}
		tablero.append("+---++---+---+---+---+---+---+---+---++\r\n");
		tablero.append("         |>-----<|");
		tablero.append(" ");
		tablero.append(RED_BOLD);
		tablero.append(puntuacion[0]);
		tablero.append(WHITE);
		tablero.append(" - ");
		tablero.append(CYAN_BOLD);
		tablero.append(puntuacion[1]);
		tablero.append(" ");
		tablero.append(RESET);
		tablero.append("|>-----<|\n");
		return tablero.toString();
	}
}
