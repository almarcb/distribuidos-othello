import java.util.Scanner;

import model.Tablero;

public class Launcher {

	public static void main(String[] args) {
		Tablero t = new Tablero();
		
		Scanner scan = new Scanner(System.in);
		int x=0; int y=0;
		boolean jugadaValida=true;
		
		while(t.existenCasillasVacías()) {
			System.out.println(t.toString());
			System.out.println("Turno del jugador "+(t.getJugadorActivo()+1));
			
			if(t.existenCasillasDisponibles()) {
				
				do {
					try {
						System.out.print("Seleccione la posición x: ");
						x=Integer.parseInt(scan.nextLine()); // El método next int da problemas
						System.out.print("Seleccione la posición y: ");
						y=Integer.parseInt(scan.nextLine());
					}catch(NumberFormatException e) {
						x=8;y=8;
					}
						
					jugadaValida=t.reclamarCasilla(x, y);
					if(!jugadaValida) {
						System.out.println("Jugada ilegal.");
					}
				}while(!jugadaValida);
				
				
			}else {
				t.avanzarTurno();
				System.out.println("No existen posiciones legales, saltando turno.");
				if(!t.existenCasillasDisponibles()) {
					System.out.println("Sin movimientos posibles");
					break;
				}
			}
		}
		System.out.println("Fin de la partida");
		
		System.out.println(t.toString());
		
		int[] puntuacion = t.getPuntuacion();
		System.out.println("Puntuación: ");
		System.out.println("\t Rojas: "+puntuacion[0]);
		System.out.println("\t Azules: "+puntuacion[1]);
		
		if(puntuacion[0]>puntuacion[1]) {
			System.out.println("Gana el jugador rojo");
		}else if(puntuacion[0]>puntuacion[1]) {
			System.out.println("Gana el jugador azul");
		}else {
			System.out.println("Empate");
		}
		
	}

}
