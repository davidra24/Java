package run;

import java.io.IOException;
import java.util.Scanner;
import socket.Server;

/**
 *
 * @author Usuario
 */
public class MainServer {
    
    public static void main(String[] args) {
	Scanner scanner = new Scanner(System.in);
	System.out.println("Puerto: ");
	int port = scanner.nextInt();
        try {
            Server server = new Server(port);
            server.acceptConnections();
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        
    }
}

