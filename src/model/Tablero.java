package model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class Tablero {

	
	private static final int VACIA =3;
	private static final int DISPONIBLE =4;
	
	
	private int[] casillas = new int[64];
	private int turn;

	
	public Tablero() {
		loadPosicionInicial();
	}
	
	
	private void loadPosicionInicial() {
		Arrays.fill(this.casillas, VACIA);
		
		this.turn=0;
		
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
		int col, row;
		
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
				col= i/8;
				row= i%8;
				while(col!=0 && row!=0 && casillas[(col-1)*8+row-1]==jugadorOponente) { // Se comprueba si se ha llegado al borde o si la siguiente casilla por esa dirección está reclamada por el jugador opuesto
					col--;
					row--; // Se avanza a la siguiente casilla
					
					if(col!=0 && row!=0 && casillas[(col-1)*8+row-1]==VACIA) { // Si la siguiente casilla está dentro del tablero y es vacía, se marca como disponible 
						casillas[(col-1)*8+(row-1)]=DISPONIBLE;
					}
				}
				
				// Vertical superior
				col= i/8;
				row= i%8;
				while(col!=0 && casillas[(col-1)*8+row]==jugadorOponente) { 
					col--;
					
					if(col!=0 && casillas[(col-1)*8+row]==VACIA) {  
						casillas[(col-1)*8+row]=DISPONIBLE;
					}
				}
				
				// Diagonal superior derecha
				col= i/8;
				row= i%8;
				while(col!=0 && row!=7 && casillas[(col-1)*8+row+1]==jugadorOponente) { 
					col--;
					row++;
					
					if(col!=0 && row!=7 && casillas[(col-1)*8+row+1]==VACIA) {  
						casillas[(col-1)*8+row+1]=DISPONIBLE;
					}
				}
				
				// Horizontal izquierda
				col= i/8;
				row= i%8;
				while(row!=0 && casillas[col*8+row-1]==jugadorOponente) { 
					row--;
					
					if(row!=0 && casillas[col*8+row-1]==VACIA) {
						casillas[col*8+row-1]=DISPONIBLE;
					}
				}
				
				// Horizontal derecha
				col= i/8;
				row= i%8;
				while(row!=7 && casillas[col*8+row+1]==jugadorOponente) { 
					row++;
					
					if(row!=7 && casillas[col*8+row+1]==VACIA) {
						casillas[col*8+row+1]=DISPONIBLE;
					}
				}
				
				// Diagonal inferior izquierda
				col= i/8;
				row= i%8;
				while( col!=7 && row!=0 && casillas[(col+1)*8+row-1]==jugadorOponente) {
					col++;
					row--;
					
					if( col!=7 && row!=0 && casillas[(col+1)*8+row-1]==VACIA) {
						casillas[(col+1)*8+row-1]=DISPONIBLE;
					}
				}
				
				// Vertical inferior 
				col= i/8;
				row= i%8;
				while(col!=7 && casillas[(col+1)*8+row]==jugadorOponente) { 
					col++;
					
					if(col!=7 && casillas[(col+1)*8+row]==VACIA) {  
						casillas[(col+1)*8+row]=DISPONIBLE;
					}
				}
				
				// Diagonal inferior derecha
				col= i/8;
				row= i%8;
				while( col!=7 && row!=7 && casillas[(col+1)*8+row+1]==jugadorOponente) {
					col++;
					row++;
					
					if( col!=7 && row!=7 && casillas[(col+1)*8+row+1]==VACIA) {
						casillas[(col+1)*8+row+1]=DISPONIBLE;
					}
				}
			}
		}
	}
	
	public boolean reclamarCasilla(int col, int row) {
		int jugadorActivo = getJugadorActivo();
		int jugadorOponente = getJugadorOponente();
		
		int coltemp=col;
		int rowtemp=row;
		
		if(col>=0 && col<=7 && row>=0 && row<=7 && this.casillas[col*8+row]==DISPONIBLE) {
			this.casillas[col*8+row] = jugadorActivo;
			
			
			// Se sigue un proceso similar al del calculo de casillas disponibles para capturar las casillas flanqueadas
			
			List<Integer> reclamadas = new LinkedList<Integer>();
			List<Integer> aReclamar = new LinkedList<Integer>();
			
			// Diagnoal superior izquierda
			while(col!=0 && row!=0 && casillas[(col-1)*8+row-1]==jugadorOponente) { 
				col--;
				row--; // Se avanza a la siguiente casilla
				
				aReclamar.add(col*8+row);
				
				if(col!=0 && row!=0 && casillas[(col-1)*8+row-1]==jugadorActivo) {  
					reclamadas.addAll(aReclamar);
				}
			}
			
			// Vertical superior
			aReclamar.clear();
			col= coltemp;
			row= rowtemp;
			while(col!=0 && casillas[(col-1)*8+row]==jugadorOponente) { 
				col--;
				aReclamar.add(col*8+row);
				if(col!=0 && casillas[(col-1)*8+row]==jugadorActivo) {  
					reclamadas.addAll(aReclamar);
				}
			}
			
			// Diagonal superior derecha
			aReclamar.clear();
			col= coltemp;
			row= rowtemp;
			while(col!=0 && row!=7 && casillas[(col-1)*8+row+1]==jugadorOponente) { 
				col--;
				row++;
				aReclamar.add(col*8+row);
				if(col!=0 && row!=7 && casillas[(col-1)*8+row+1]==jugadorActivo) {  
					reclamadas.addAll(aReclamar);
				}
			}
			
			// Horizontal izquierda
			aReclamar.clear();
			col= coltemp;
			row= rowtemp;
			while(row!=0 && casillas[col*8+row-1]==jugadorOponente) { 
				row--;
				aReclamar.add(col*8+row);
				if(row!=0 && casillas[col*8+row-1]==jugadorActivo) {
					reclamadas.addAll(aReclamar);
				}
			}
			
			// Horizontal derecha
			aReclamar.clear();
			col= coltemp;
			row= rowtemp;
			while(row!=7 && casillas[col*8+row+1]==jugadorOponente) { 
				row++;
				aReclamar.add(8*col+row);
				if(row!=7 && casillas[col*8+row+1]==jugadorActivo) {
					reclamadas.addAll(aReclamar);
				}
			}
			
			// Diagonal inferior izquierda
			aReclamar.clear();
			col= coltemp;
			row= rowtemp;
			while( col!=7 && row!=0 && casillas[(col+1)*8+row-1]==jugadorOponente) {
				col++;
				row--;
				aReclamar.add(col*8+row);
				if( col!=7 && row!=0 && casillas[(col+1)*8+row-1]==jugadorActivo) {
					reclamadas.addAll(aReclamar);
				}
			}
			
			// Vertical inferior
			aReclamar.clear();
			col= coltemp;
			row= rowtemp;
			while(col!=7 && casillas[(col+1)*8+row]==jugadorOponente) { 
				col++;
				aReclamar.add(col*8+row);
				if(col!=7 && casillas[(col+1)*8+row]==jugadorActivo) {  
					reclamadas.addAll(aReclamar);
				}
			}
			
			// Diagonal inferior derecha
			aReclamar.clear();
			col= coltemp;
			row= rowtemp;
			while( col!=7 && row!=7 && casillas[(col+1)*8+row+1]==jugadorOponente) {
				col++;
				row++;
				aReclamar.add(col*8+row);
				if( col!=7 && row!=7 && casillas[(col+1)*8+row+1]==jugadorActivo) {
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
					tablero.append(Colores.RED_BG);
					tablero.append("   ");
				}else if(casillas[i*8+j]==1) {
					tablero.append("|");
					tablero.append(Colores.CYAN_BG);
					tablero.append("   ");
				}else if(casillas[i*8+j]==DISPONIBLE) {
					tablero.append("|");
					tablero.append(" X ");
				}else {
					tablero.append("|");
					tablero.append("   ");
				}
				tablero.append(Colores.RESET);
			}
			tablero.append("||\r\n");
		}
		tablero.append("+---++---+---+---+---+---+---+---+---++\r\n");
		tablero.append("         |>-----<|");
		
		if(puntuacion[0]<10) { // Si la puntuacion es menos de 10, se necesita solo un caracter para mostrarla y hay que añadir un espacio para el display
			tablero.append(" ");
		}
		tablero.append(Colores.RED_BOLD);
		tablero.append(puntuacion[0]);
		tablero.append(Colores.WHITE);
		tablero.append(" - ");
		tablero.append(Colores.CYAN_BOLD);
		tablero.append(puntuacion[1]);
		if(puntuacion[1]<10) {
			tablero.append(" ");
		}
		tablero.append(Colores.RESET);
		tablero.append("|>-----<|\n");
		return tablero.toString();
	}
}
