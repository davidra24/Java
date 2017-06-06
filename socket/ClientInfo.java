package socket;

import java.net.Socket;
import java.util.Comparator;

/**
 *
 * @author Usuario
 */
public class ClientInfo {
    
    private Socket socket;
    private int totalFiles;
    private int index;

    public ClientInfo(Socket socket,int index, int totalFiles) {
        this.socket = socket;
        this.totalFiles = totalFiles;
	this.index = index;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setTotalFiles(int totalFiles) {
        this.totalFiles = totalFiles;
    }

    public int getTotalFiles() {
	return totalFiles;
    }
    
    Comparator<ClientInfo> compareTotalFiles = new Comparator<ClientInfo>() {

        @Override
        public int compare(ClientInfo o1, ClientInfo o2) {
            return o1.totalFiles - o2.totalFiles;
        }
    };

    Comparator<ClientInfo> compareIndex = new Comparator<ClientInfo>() {
	
	@Override
	public int compare(ClientInfo o1, ClientInfo o2) {
	    return o1.index - o2.index;
        }	
    };
}

