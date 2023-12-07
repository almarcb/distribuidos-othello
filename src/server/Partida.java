package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

import model.Tablero;

public class Partida extends Thread{
	private Socket[] jugadores;
	private Tablero board;
	
	private static int DAR_TURNO = 0;
	private static int SALTAR_TURNO = 1;
	private static int FIN = 3;
	private static int PRIMERO =4;
	private static int SEGUNDO = 5;
	private static int STALEMATE = 6;
	private static int JUGADA_OPONENTE =8;
	
	public Partida(Socket jugador1, Socket jugador2) {
		jugadores = new Socket[2];
		
		// Aleatorización del turno 
		Random rng = new Random();
		int turn = rng.nextInt(2);
		
		jugadores[turn]=jugador1;
		jugadores[(turn+1)%2]=jugador2;
		
		board = new Tablero();
	}
	
	public void run() {
		
		try(DataInputStream iner0 = new DataInputStream(jugadores[0].getInputStream());
			DataInputStream iner1 = new DataInputStream(jugadores[1].getInputStream());
			DataOutputStream outer0 = new DataOutputStream(jugadores[0].getOutputStream());
			DataOutputStream outer1 = new DataOutputStream(jugadores[1].getOutputStream());	){
			
			DataInputStream[] iner = {iner0,iner1};
			DataOutputStream[] outer = {outer0,outer1};
			String[] nombre = new String[2];
			
			for(int i=0;i<2;i++) { // Pide a cada cliente su nombre
				nombre[i] = iner[i].readLine();
			}
			
			for(int i=0;i<2;i++) { // Envía a cada cliente el nombre del oponente 
				outer[i].writeBytes(nombre[(i+1)%2]+"\r\n");
			}
			
			outer[0].writeInt(PRIMERO);
			outer[1].writeInt(SEGUNDO);
			for(int i=0;i<2;i++) {
				outer[i].flush();
			}
			
			int col, row;
			int jugadorActivo,jugadorOponente;
			while(board.existenCasillasVacías()) { // Mientras el tablero aún no se haya llenado
				jugadorActivo = board.getJugadorActivo();
				jugadorOponente= board.getJugadorOponente();
				
				if(board.existenCasillasDisponibles()) { // Los clientes se aseguran de que todas las jugadas que llegan al servidor son válidas
					outer[jugadorActivo].writeInt(DAR_TURNO);
					outer[jugadorOponente].writeInt(JUGADA_OPONENTE);
					
					col = iner[jugadorActivo].readInt();
					row = iner[jugadorActivo].readInt();
						
					board.reclamarCasilla(col, row);
					
					outer[jugadorOponente].writeInt(col);
					outer[jugadorOponente].writeInt(row);
				}else { // Si un jugador no tiene casillas disponibles se le salta el turno
					outer[jugadorActivo].writeInt(SALTAR_TURNO);
					outer[jugadorOponente].writeInt(SALTAR_TURNO);
					board.avanzarTurno();
					if(!board.existenCasillasDisponibles()) { // Stalemate. Ningún jugador tiene casillas disponibles
						for(int i=0;i<2;i++) {
							outer[i].writeInt(STALEMATE);
						}
						break;
					}
				}
			}
				
			int[] puntuacion = board.getPuntuacion();
			
			for(int i=0;i<2;i++) {
				outer[i].writeInt(FIN);
				for(int j=0;j<2;j++) {
					outer[i].writeInt(puntuacion[j]);
				}
			}
			
			
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
}
