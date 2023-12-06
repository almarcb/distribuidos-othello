package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor {

	public static void main(String[] args) {
		ExecutorService pool = Executors.newCachedThreadPool();
		try(ServerSocket ss =new ServerSocket(5000)){
			while(true) {
				try {
					Socket client1 = ss.accept();
					Socket client2 = ss.accept();
					
					pool.execute(new Partida(client1,client2));
				}catch(IOException e) {
					e.printStackTrace();
				}
					
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
