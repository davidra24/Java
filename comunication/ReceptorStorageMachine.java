package comunication;

import java.io.IOException;
import java.net.Socket;
import socket.Client;

/**
 *
 * @author Usuario
 */
public class ReceptorStorageMachine extends Receptor implements Runnable {

    private Client client;
    private boolean isReceiving;

    public ReceptorStorageMachine(Socket socket, Client client) throws IOException {
        super(socket);
        this.client = client;
        this.isReceiving = true;
    }

    public void setIsReceiving(boolean isReceiving) {
        this.isReceiving = isReceiving;
    }

    @Override
    public void run() {
        while (isReceiving) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
            try {
                System.out.println("Este es de la maquina de almacenamiento");
                String message = getMessage();
                System.out.println("El mensaje es: " + message);
                client.review(message);
            } catch (IOException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }
}

