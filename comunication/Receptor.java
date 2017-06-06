package comunication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Usuario
 */
//public class Receptor implements Runnable {
public class Receptor {

    //Buffer de 1024 bytes
    private final byte[] receivedData = new byte[1024];

    private BufferedInputStream bis;
    private BufferedOutputStream bos;
    private DataInputStream dis;
    private Socket socket;
//    private boolean isReceiving;
//    private Server server;
    private String fileName;

    public Receptor(Socket socket) throws IOException {
        this.socket = socket;
        this.dis = new DataInputStream(socket.getInputStream());
        this.bis = new BufferedInputStream(socket.getInputStream());
    }

//    public Receptor(Socket socket, Server server) throws IOException {
//        this.socket = socket;
//        this.bis = new BufferedInputStream(socket.getInputStream());
//        this.dis = new DataInputStream(socket.getInputStream());
//        this.isReceiving = true;
//        this.server = server;
//    }
    public String getMessage() throws IOException {
        return dis.readUTF();
    }

//    @Override
//    public void run() {
//        while (isReceiving) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException ex) {
//                System.out.println("Error: " + ex.getMessage());
//            }
//            try {
//                if (server != null) {
//                    System.out.println("Este es del servidor");
//                    String message = getMessage();
//                    System.out.println("El mensaje es: " + message);
//                    server.review(this,message);
//                }
//            } catch (IOException ex) {
//                System.out.println("Error: " + ex.getMessage());
//            }
//        }
//    }

    public void receiveFile() throws IOException {
        int in;
        long receive = 0;
        //Recibimos el tamaño del fichero
        long length = Long.parseLong(dis.readUTF());
        System.out.println("El tamaño del archivo es: " + length);
        //Recibimos el nombre del fichero
        String name = dis.readUTF();
        fileName = "./received/" + name.substring(name.indexOf('/') + 1, name.length());
        this.bos = new BufferedOutputStream(new FileOutputStream(fileName));
        System.err.println("El nombre del archivo es: " + fileName);
        //Para guardar fichero recibido
        while (receive < length) {
            in = bis.read(receivedData);
            receive += in;
            System.out.println("receive es: " + receive);
            bos.write(receivedData, 0, in);
        }
        bos.close();
    }

    public String getFileName() {
        return fileName;
    }

    public Socket getSocket() {
        return socket;
    }
}
