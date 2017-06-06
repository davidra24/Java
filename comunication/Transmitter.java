package comunication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Usuario
 */
public class Transmitter implements Runnable {

    private final byte[] byteArray = new byte[8192];
    
    private BufferedInputStream bis;
    private BufferedOutputStream bos;
    DataOutputStream dos;
    Socket socket;
    String message;
    File file;
    long length;

    public Transmitter(Socket socket) throws IOException {
        this.socket = socket;
        this.dos = new DataOutputStream(socket.getOutputStream());
    }

    public void sendMessage(String message) throws IOException {
        dos.writeUTF(message);
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public void sendFile(String fileName) throws FileNotFoundException, IOException {
        file = new File(fileName);
        length = file.length();
        dos.writeUTF(String.valueOf(length));
        long send = 0;
        int in;
        bis = new BufferedInputStream(new FileInputStream(file));
        bos = new BufferedOutputStream(socket.getOutputStream());
        //Enviamos el nombre del fichero
        dos.writeUTF(file.getName());
        //Enviamos el fichero
        while (send < length) {
            in = bis.read(byteArray);
            send += in;
            System.out.println("send es " + send);
            bos.write(byteArray, 0, in);
        }
        bis.close();
        bos.flush();
    }

    public Socket getSocket() {
        return socket;
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Transmitter.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                dos.writeUTF(message);
            } catch (IOException ex) {
                Logger.getLogger(Transmitter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}

