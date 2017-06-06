package run;

import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import socket.Client;
/**
 *
 * @author Usuario
 */
public class MainClient {

    public static void main(String[] args) {
        Client client;
        Scanner scanner = new Scanner(System.in);
	System.out.print("Puerto: ");
	int port = scanner.nextInt();
	System.out.print("Host: ");
	String host = scanner.next();
	System.out.println();
        System.out.println("¿Es? 1.Cliente normal 2.Maquina de almacenamiento");
        byte type = scanner.nextByte();
        try {
            client = new Client(port, host, type);
            byte answer = client.connect();
            if (type == 1 && answer == 1) {
                boolean connect = true;
                while (connect) {
                    System.out.println("¿Qué desea hacer?");
                    System.out.println("1.Enviar archivo"
                            + "\n2.Ver lista de archivos de una maquina de almacenamiento "
                            + "\n3.Eliminar un archivo "
			    + "\n4.Solicitar balanceo de archivos"
                            + "\n5.Desconectarse");
                    byte action = scanner.nextByte();
                    switch (action) {
                        case 1:
                            System.out.println("Direccion del archivo: ");
                            String fileName = scanner.next();
//                            byte situation = client.sendFile("D:\\Manual.pdf");
                            byte situation =  client.sendFile(fileName);
                            if (situation == 0) {
                                System.out.println("El archivo fue enviando exitosamente");
                            } else if (situation == 2) {
                                System.out.println("No es posible almacenar el archivo");
                            } else {
                                System.out.println("El archivo no fue posible de almacenar en la(s) maquina(s)");
                            }
			    System.out.println();
                            break;
                        case 2:
                            int machines = client.requestListFiles();
			    if (machines > 0) {
				System.out.println("Cual maquina?");
			    	for (int i=1; i<=machines; i++) {
				    System.out.println(i + ".Maquina " + i); 
			    	}
		 	    } else {
				System.out.println("No hay maquinas de almacenamiento");
				break;
			    }
			    int machine = scanner.nextInt();
			    ArrayList<String> f = client.getFiles(machine-1);
			    if (f.size() > 0) {
				System.out.println("Los archivos son: ");
			    	for (int i=0; i<f.size(); i++) {
				    System.out.println(f.get(i));
			    	}
			    } else {
				System.out.println("No hay archivos");
			    }
			    System.out.println();
                            break;
                        case 3:
                            ArrayList<String> files = client.deleteFile();
			    if (files.size() > 0) {
				System.out.println("Cual archivo?");
				for (int i=0; i<files.size(); i++){
				    System.out.println((i+1) + ". " + files.get(i));
			    	}
			    } else {
				System.out.println("No hay archivos");
				break;
			    }
			    int position = scanner.nextInt();
            		    client.sendMessage(files.get(position-1));
			    String confirmation = client.getMessage();
			    if(confirmation.equals("1")) {
				System.out.println("Archivo eliminado");
			    } else {
				System.out.println("No se pudo eliminar el archivo");
			    }			 
			    System.out.println();
                            break;
			case 4:
			    int o = client.requestBalancing();
			    if (o == 1) {
				System.out.println("Se balanceo la carga de archivos");
			    } else { 
			    	System.out.println("No se pudo balancear los archivos");
			    }
			    break;
                        case 5:
                            System.out.println("Chao...");
                            client.shutdown();
                            connect = false;
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}

