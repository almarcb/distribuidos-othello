package model;

public class Partida {
	private Tablero tablero;
	Player[] jugadores;
	
	public Partida() {
		this.tablero = new Tablero();
		this.jugadores = new Player[2];
	}
	
}
