package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import model.Colores;
import model.Tablero;

public class Cliente {
	private static int DAR_TURNO = 0;
	private static int SALTAR_TURNO = 1;
	private static int FIN = 3;
	private static int PRIMERO =4;
	private static int SEGUNDO = 5;
	private static int STALEMATE = 6;
	private static int JUGADA_OPONENTE =8;
	
	public static void main(String[] args) {
		Tablero board = new Tablero();
		String[] nombres = new String[4];
		int respuestaServidor;
		int col=0;
		int row=0;
		int opcion=0;
		boolean validador=false;
		Scanner scan = new Scanner(System.in);
		System.out.println(""
				+ "\t	┏┓ ┓   ┓┓  \r\n"
				+ "\t	┃┃╋┣┓┏┓┃┃┏┓\r\n"
				+ "\t	┗┛┗┛┗┗ ┗┗┗┛\r\n"
				+ "           \r\n"
				+ "");
		
		System.out.println("Bienvenido a Othello Online.\r\n");
		
		
		
		System.out.println("######################");
		System.out.println("#      Opciones      #");
		System.out.println("#--------------------#");
		System.out.println("# 1.- Jugar en local #");
		System.out.println("# 2.- Jugar online   #");
		System.out.println("# 0.- Salir          #");
		System.out.println("######################\r\n");
		
		
		System.out.print("Seleccione una opción: ");
		
		try {
			opcion = Integer.parseInt(scan.nextLine()); // El método next int da problemas
		}catch(NumberFormatException e) {}
		 
		
		//-----------------------------------------------------------------------------------------------
		// Opcion local
		if(opcion==1) {
			while(board.existenCasillasVacías()) {
				System.out.println(board.toString());
				System.out.print("Turno del jugador "/*+(board.getJugadorActivo()+1)*/);
				if(board.getJugadorActivo()==1) {
					System.out.println(Colores.CYAN_BOLD+"Azul "+Colores.RESET); 
				}else if(board.getJugadorActivo()==0) {
					System.out.println(Colores.RED_BOLD+"Rojo "+Colores.RESET);
				}

				if(board.existenCasillasDisponibles()) {

					do {
						try {
							System.out.print("Seleccione la fila: ");
							col=Integer.parseInt(scan.nextLine()); // El método next int da problemas
							System.out.print("Seleccione la columna: ");
							row=Integer.parseInt(scan.nextLine());
						}catch(NumberFormatException e) {
							col=8;row=8;
						}

						validador=board.reclamarCasilla(col, row);
						if(!validador) {
							System.out.println("Jugada ilegal.");
						}
					}while(!validador);


				}else {
					board.avanzarTurno();
					System.out.println("No existen posiciones legales, saltando turno.");
					if(!board.existenCasillasDisponibles()) {
						System.out.println("Sin movimientos posibles");
						break;
					}
				}
			}
			System.out.println("\r\nFin de la partida");

			System.out.println(board.toString());

			int[] puntuacion = board.getPuntuacion();
			System.out.println("Puntuación: ");
			System.out.println("\t"+Colores.RED_BOLD+"Jugador rojo "+Colores.RESET+": "+puntuacion[0]);
			System.out.println("\t"+Colores.CYAN_BOLD+"Jugador azul "+Colores.RESET+": "+puntuacion[1]);

			if(puntuacion[0]>puntuacion[1]) {
				System.out.println("Gana el jugador rojo");
			}else if(puntuacion[0]<puntuacion[1]) {
				System.out.println("Gana el jugador azul");
			}else {
				System.out.println("Empate");
			}
			
		//-----------------------------------------------------------------------------------------------
		// Opcion Online
		}else if(opcion==2) {
			System.out.print("Introduzca su nombre: ");
			String nombre = scan.nextLine();
			System.out.println("Buscando partida...");
			
			try(Socket s = new Socket("localhost",5000);
				DataInputStream iner = new DataInputStream(s.getInputStream());
				DataOutputStream outer = new DataOutputStream(s.getOutputStream());){
				
				
				outer.writeBytes(nombre+"\r\n");
				String oponente = iner.readLine(); // El cliente queda bloqueado hasta que aparece un oponente
				System.out.println("Partida encontrada");
				
				if(iner.readInt()==PRIMERO) {
					nombres[0]=nombre;
					nombres[1]=oponente;
				}else {
					nombres[1]=nombre;
					nombres[0]=oponente;
				}
				System.out.println(Colores.RED_BOLD+nombres[0]+Colores.RESET+" vs "+Colores.CYAN_BOLD+nombres[1]);
				System.out.print(Colores.RESET);
				
				// En cada turno, el jugador les envía una orden a los clientes para que lleven a cabo determinadas acciones
				while((respuestaServidor=iner.readInt())!=FIN){
					System.out.println(board.toString());
					
					if(respuestaServidor==DAR_TURNO) {
						System.out.println("Tu turno");
						do {
							try {
								System.out.print("Seleccione la fila: ");
								col=Integer.parseInt(scan.nextLine()); 
								System.out.print("Seleccione la columna: ");
								row=Integer.parseInt(scan.nextLine());
								
								validador=board.reclamarCasilla(col, row);
							}catch(NumberFormatException e) {
								validador=false;
							}
								
							if(!validador) {
								System.out.println("Jugada ilegal.");
							}
						}while(!validador); // Validación de la jugada
							
						outer.writeInt(col);
						outer.writeInt(row);
						outer.flush();
					}else if(respuestaServidor == JUGADA_OPONENTE) {
						System.out.println("Turno del oponente...");
						col = iner.readInt();
						row = iner.readInt();
						board.reclamarCasilla(col,row);
					}else if(respuestaServidor == SALTAR_TURNO) {
						System.out.println("No hay posiciones legales. Saltando turno...");
						board.avanzarTurno();
					}else if(respuestaServidor == STALEMATE) {
						System.out.println("Sin movimientos posibles");
					}
				}
				
				int[] puntuacion = new int[2];
				puntuacion[0]=iner.readInt();
				puntuacion[1]=iner.readInt();
				
				StringBuilder sb = new StringBuilder();
				System.out.println("Fin de la partida");
				System.out.println(board.toString());
				
				sb.append("Puntuación: \r\n");
				sb.append(Colores.RED_BOLD);
				sb.append("\t");
				sb.append(nombres[0]);
				sb.append(": ");
				sb.append(Colores.RESET);
				sb.append(puntuacion[0]);
				sb.append("\r\n");
				sb.append(Colores.RESET);
				sb.append(Colores.CYAN_BOLD);
				sb.append("\t");
				sb.append(nombres[1]);
				sb.append(": ");
				sb.append(Colores.RESET);
				sb.append(puntuacion[1]);
				sb.append(Colores.RESET);
				System.out.println(sb.toString());
				
				
				if(puntuacion[0]>puntuacion[1]) {
					System.out.println("Ganador: "+nombres[0]);
				}else if(puntuacion[0]<puntuacion[1]) {
					System.out.println("Ganador: "+nombres[1]);
				}else {
					System.out.println("Empate");
				}

			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Error en el servidor");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		

		scan.close();
	}
}
