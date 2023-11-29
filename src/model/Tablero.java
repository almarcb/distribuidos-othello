package model;

import java.util.Arrays;

public class Tablero {
	private int[] casillas = new int[64];
	
	public Tablero() {
		loadPosicionInicial();
	}
	
	
	private void loadPosicionInicial() {
		Arrays.fill(this.casillas, 0);
		
		
		this.casillas[3*8+3]=1;
		this.casillas[4*8+4]=1;
		
		this.casillas[3*8+4]=2;
		this.casillas[4*8+3]=2;
	}
	
	public void reclamarCasilla(int casilla, int jugador) {
		this.casillas[casilla] = jugador;
	}
	
	public void reclamarCasilla(int x, int y, int jugador) {
		this.casillas[x*8+y]= jugador;
	}
	
	public void mostrarTablero() {
		StringBuilder tablero = new StringBuilder();
			tablero.append("                 _________            \r\n");
			tablero.append("     +---+---+---|Othello|---+---+---+\r\n");
			tablero.append("     +---+---+---+-------+---+---+---+\r\n");
			tablero.append("     | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 |\r\n");
		for(int i=0;i<8;i++) {
			tablero.append("+---++---+---+---+---+---+---+---+---++\r\n");
			tablero.append("| ");
			tablero.append(i);
			tablero.append(" |");
			for(int j=0;j<8;j++) {
				
				if(casillas[i*8+j]==1) {
					tablero.append("| X ");
				}else if(casillas[i*8+j]==2) {
					tablero.append("| O ");
				}else {
					tablero.append("|   ");
				}
			}
			tablero.append("||\r\n");
		}
		tablero.append("+---++---+---+---+---+---+---+---+---++\r\n");
		System.out.println(tablero.toString());
	}
}
