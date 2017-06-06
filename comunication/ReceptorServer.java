package comunication;

import java.io.IOException;
import java.net.Socket;
import socket.Server;

/**
 *
 * @author Usuario
 */
public class ReceptorServer extends Receptor implements Runnable {

    private Server server;
    private boolean isReceiving;

    public ReceptorServer(Socket socket, Server server) throws IOException {
        super(socket);
        this.server = server;
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
                System.out.println("Este es del servidor");
                String message = getMessage();
                System.out.println("El mensaje es: " + message);
                server.review(this, message);
            } catch (IOException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }
}

