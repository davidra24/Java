package socket;

import comunication.ReceptorStorageMachine;
import comunication.Transmitter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.io.File;

/**
 *
 * @author Usuario
 */
public class Client {

    private Socket socket;
    private int port;
    private String host;
    private byte type;
    private ReceptorStorageMachine receptor;
    private Thread threadReceptor;
    private Transmitter transmitter;
    private Thread threadTransmitter;

    public Client(int port, String host, byte type) {
        this.port = port;
        this.host = host;
        this.type = type;
    }

    public byte connect() throws IOException {
        this.socket = new Socket(host, port);
        this.receptor = new ReceptorStorageMachine(socket, this);
        this.transmitter = new Transmitter(socket);
        this.sendMessage(String.valueOf(type));
	if (type == 2) {
	    this.sendMessage(String.valueOf(getListOfFiles().size()));
	}
        byte answer = Byte.parseByte(receptor.getMessage());
	if (type == 1) {
            if (answer == 1) {
                System.out.println("Conectado");
            } else {
                System.out.println("Ya esta un cliente conectado");
            }
        } else {
	    if (answer == 1) {
                System.out.println("Conectado");
                threadReceptor = new Thread(receptor);
                threadReceptor.start();
            }
        }
        return answer;
    }

    public void sendMessage(String message) throws IOException {
        transmitter.sendMessage(message);

    }

    public byte sendFile(String fileName) throws IOException {
        this.sendMessage("1");
        transmitter.sendFile(fileName);
        return Byte.parseByte(receptor.getMessage());
    }

    public int requestListFiles() throws IOException {
        this.sendMessage("2");
	return Integer.parseInt(receptor.getMessage());
    }
    
    public ArrayList<String> getFiles(int machine) throws IOException {
	this.sendMessage(String.valueOf(machine));
	int numFiles = Integer.parseInt(receptor.getMessage());
    	ArrayList files = new ArrayList();
	for (int i=0; i < numFiles; i++) {
	    files.add(receptor.getMessage());
	}
	return files;
    }

    public String getMessage() throws IOException {
        return receptor.getMessage();
    }

    public ArrayList<String> deleteFile() throws IOException {
        this.sendMessage("3");
	int numFiles = Integer.parseInt(receptor.getMessage());
	ArrayList files = new ArrayList();
	for (int i=0; i<numFiles; i++){
	    files.add(receptor.getMessage());
	
	}
	return files;
    }

    public String getConfirmation() throws IOException {
	return receptor.getMessage();
    }

    public void deleteFile(String fileName) {
	File file = new File("./received/" + fileName);
	file.delete();
    } 

    public int requestBalancing() throws IOException {
	this.sendMessage("4");
	return Integer.parseInt(receptor.getMessage());
    }

    public void shutdown() throws IOException {
        this.sendMessage("-1");
    }

    public ArrayList<String> getListOfFiles() {
	ArrayList al = new ArrayList();
	File f = new File("./received/");
	if (!f.exists()) {
	    f.mkdirs();
	}
	File[] array = new File("./received/").listFiles();
	for (File array1 : array) {
	    al.add(array1.getName());
	}
	return al;
    }

    public void sendFiles() throws IOException {
	ArrayList<String> files = getListOfFiles();
	this.sendMessage(String.valueOf(files.size()));
	for (int i=0; i<files.size(); i++) {
	    this.sendMessage(files.get(i));
	}
    }

    public void sendFirstFile() throws IOException {
	String fileName = getListOfFiles().get(0);
	transmitter.sendFile("./received/" + fileName);
    }

    public void review(String message) throws IOException {
        int option = Integer.parseInt(message);
        switch (option) {
            case 1:
                System.out.println("Llega un archivo");
                receptor.receiveFile();
                transmitter.sendMessage("1");
                break;
            case 2:
                System.out.println("Se solicita lista de archivos");
		this.sendFiles();
                break;
	    case 3:
		System.out.println("Se elimina archivo");
		String fileName = receptor.getMessage();
		deleteFile(fileName);
		break;
	    case 4:
		sendFirstFile();
		break;
            default:
                break;
        }
    }
}

