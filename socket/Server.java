package socket;

import comunication.Receptor;
import comunication.Transmitter;
import comunication.ReceptorServer;
import java.io.IOException;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

/**
 *
 * @author Usuario
 */
public class Server implements Runnable {

    private ServerSocket serverSocket;
    private Socket client;
    private ArrayList<ClientInfo> storageMachines;
//    private ArrayList<Receptor> receptors;
    private ReceptorServer receptorClient;
    private ArrayList<Receptor> receptorsStorage;
//    private ArrayList<Thread> threadsReceptors;
    private Thread threadReceptorC;
//    private ArrayList<Transmitter> transmitters;
    private Transmitter transmitterClient;
    private ArrayList<Transmitter> transmittersStorage;
//    private ArrayList<Thread> threadsTransmitters;
    private Thread thread;

    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
//        this.receptors = new ArrayList<>();
        this.receptorsStorage = new ArrayList<>();
        this.storageMachines = new ArrayList<>();
//        this.transmitters = new ArrayList<>();
        this.transmittersStorage = new ArrayList<>();
//        this.threadsReceptors = new ArrayList<>();
//        this.threadsTransmitters = new ArrayList<>();
    }

    public void acceptConnections() throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();
            ReceptorServer receptor = new ReceptorServer(socket, this);
            Transmitter transmitter = new Transmitter(socket);
            int type = Integer.parseInt(receptor.getMessage());
            if (type == 1) {
                if (client == null) {
                    client = socket;
                    transmitter.sendMessage("1");
                    initilizeClient(transmitter, receptor);
                } else {
                    transmitter.sendMessage("-1");
                }
            } else if (type == 2) {
		int totalFiles = Integer.parseInt(receptor.getMessage());
		System.out.println("La maquina tiene " + totalFiles);
                storageMachines.add(new ClientInfo(socket,storageMachines.size(),totalFiles));
                initializeStorageMachine(transmitter, receptor);
                transmitter.sendMessage("1");
            }
        }
    }

    public void initilizeClient(Transmitter transmitter, ReceptorServer receptor) {
        System.out.println("Se inicializa el cliente");
        Thread thread1 = new Thread(receptor);
        thread1.start();
        receptorClient = receptor;
        transmitterClient = transmitter;
        threadReceptorC = thread1;
//        thread = new Thread(this);
//        thread.start();
    }

    public void initializeStorageMachine(Transmitter transmitter, Receptor receptor) {
        receptorsStorage.add(receptor);
        transmittersStorage.add(transmitter);
    }

    public void review(ReceptorServer receptor, String message) throws IOException {
        int option = Integer.parseInt(message);
        switch (option) {
            case 1:
                System.out.println("Se va a enviar archivo");
                receptor.receiveFile();
                selectStorageMachines(receptor.getFileName());
		deleteFile(receptor.getFileName());
                break;
            case 2:
                System.out.println("Se quiere ver listado de archivos");
		transmitterClient.sendMessage(String.valueOf(storageMachines.size()));
		getListFiles();
                break;
            case 3:
		deleteFile();
                break;
            case 4:
		loadBalancing();
                break;
	    case -1:
		break;
            default:
                break;
        }
    }

    public void deleteFile(String fileName) {
	File file = new File(fileName);
	file.delete();
    }

    public void selectStorageMachines(String fileName) throws IOException {
        if (storageMachines.size() < 2) {
            transmitterClient.sendMessage("2");
        } else {
 	    Collections.sort(storageMachines, storageMachines.get(0).compareTotalFiles);
	    ClientInfo s1 = storageMachines.get(0);
	    ClientInfo s2 = storageMachines.get(1);
            Transmitter t1 = getTransmitter(s1.getSocket());
            Receptor r1 = getReceptor(s1.getSocket());
            Transmitter t2 = getTransmitter(s2.getSocket());
            Receptor r2 = getReceptor(s2.getSocket());
            t1.sendMessage("1");
            t1.sendFile(fileName);
            String a1 = r1.getMessage();
            if (a1.equals("1") == false) {
                transmitterClient.sendMessage("3");
                return;
            }
	    s1.setTotalFiles(s1.getTotalFiles()+1);
            t2.sendMessage("1");
            t2.sendFile(fileName);
            String a2 = r2.getMessage();
            if (a2.equals("1") == false) {
                transmitterClient.sendMessage("3");
                return;
            }
	    s2.setTotalFiles(s2.getTotalFiles()+1);
            transmitterClient.sendMessage("0");
	    Collections.sort(storageMachines, storageMachines.get(0).compareIndex);
        }
    }
    
    public void getListFiles() throws IOException {
	int position = Integer.parseInt(receptorClient.getMessage());
	ClientInfo c = storageMachines.get(position);
	Transmitter t = getTransmitter(c.getSocket());
	Receptor r = getReceptor(c.getSocket());
	sendListFiles(getListFilesS(r,t),transmitterClient);
    }

    public ArrayList<String> getListFilesS(Receptor r, Transmitter t) throws IOException {
	t.sendMessage("2");
	System.out.println("Se envio solicitud");
	int tam = Integer.parseInt(r.getMessage());
	ArrayList l = new ArrayList();
	for (int i=0; i<tam; i++) {
	    l.add(r.getMessage());
	}
	return l;
    }

    public void sendListFiles(ArrayList<String> l, Transmitter t) throws IOException {
	System.out.print("Enviar la lista de archivos de tamanio" + l.size());
	t.sendMessage(String.valueOf(l.size()));
	for (int i=0; i<l.size(); i++){
	    t.sendMessage(l.get(i));
	}
    } 

    public void loadBalancing() throws IOException {
	boolean isBalancing = false;
	while(!isBalancing) {
	    Collections.sort(storageMachines, storageMachines.get(0).compareTotalFiles);
	    ClientInfo s1 = storageMachines.get(0);
	    ClientInfo s2 = storageMachines.get(storageMachines.size()-1);
	    if (s2.getTotalFiles() - s1.getTotalFiles() > 1) {
		Transmitter t1 = getTransmitter(s1.getSocket());
		Receptor r1 = getReceptor(s1.getSocket());
		Transmitter t2 = getTransmitter(s2.getSocket());
		Receptor r2 = getReceptor(s2.getSocket());
		t2.sendMessage("4");
		r2.receiveFile();
		t1.sendMessage("1");
		t1.sendFile(r2.getFileName());
		r1.receiveFile();
		deleteFile(r2.getFileName());
		s1.setTotalFiles(s1.getTotalFiles()+1);
		s2.setTotalFiles(s2.getTotalFiles()-1);
	    } else {
		isBalancing = true;
	    }
	}
	System.out.println("Se realizo el balanceo");
	Collections.sort(storageMachines, storageMachines.get(0).compareIndex);
	transmitterClient.sendMessage("1");
    }

    public void deleteFile() throws IOException {
	HashMap<String,ArrayList<ClientInfo>> allFiles = new HashMap<>();
	ArrayList<String> files = new ArrayList<>();
	for (int i=0; i < storageMachines.size() ; i++) {
	    ClientInfo c = storageMachines.get(i);
	    Socket s = c.getSocket();
	    Transmitter t = getTransmitter(s);
	    Receptor r = getReceptor(s);
	    ArrayList<String> aux = getListFilesS(r,t);
	    for(int j=0; j<aux.size(); j++) {
		String file = aux.get(j);
		if (allFiles.containsKey(file) == false) {
		    ArrayList<ClientInfo> auxS = new ArrayList<>();
		    auxS.add(c);
		    allFiles.put(file,auxS);
		    files.add(file);
		} else {
		    allFiles.get(file).add(c);
		}
	    }
	}
	sendListFiles(files,transmitterClient);
	String file = receptorClient.getMessage();
	ArrayList<ClientInfo> sockets = allFiles.get(file);
	for (int k=0; k<sockets.size(); k++) {
	    ClientInfo c = sockets.get(k);
	    Transmitter t = getTransmitter(c.getSocket());
	    t.sendMessage("3");
	    t.sendMessage(file);
	    c.setTotalFiles(c.getTotalFiles()-1);
	}
	transmitterClient.sendMessage("1");
    }

    public Transmitter getTransmitter(Socket socket) {
        int i = 0;
        Transmitter aux = null;
        while (i < transmittersStorage.size() && aux == null) {
            if (transmittersStorage.get(i).getSocket().equals(socket)) {
                aux = transmittersStorage.get(i);
            }
            i++;
        }
        return aux;
    }

    public Receptor getReceptor(Socket socket) {
        int i = 0;
        Receptor aux = null;
        while (i < receptorsStorage.size() && aux == null) {
            if (receptorsStorage.get(i).getSocket().equals(socket)) {
                aux = receptorsStorage.get(i);
            }
            i++;
        }
        return aux;
    }

    @Override
    public void run() {
        while (true) {
            try {
                thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
            System.out.println("Cliente: " + client.isClosed());

        }
    }
}

