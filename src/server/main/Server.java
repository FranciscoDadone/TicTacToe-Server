package server.main;

import java.io.FileInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

import server.database.DatabaseConnection;
import server.requests.Request;
import server.util.Utilities;

public class Server {
    public static void main(String[] args) throws Exception {
    	Utilities.logs("Iniciando servidor...");
    	Utilities.logs("Para apagar el servidor escribe 'stop'");
    	
    	Properties prop=new Properties();
		
		
		try {
			
			FileInputStream inputstream= new FileInputStream("config.properties");
			prop.load(inputstream);
			Server.port = Integer.parseInt(prop.getProperty("server_port"));
			DatabaseConnection.dbNameUsername = prop.getProperty("dbUsername");
			DatabaseConnection.dbPassword = prop.getProperty("dbPassword");
			DatabaseConnection.dbPort = prop.getProperty("dbPort");
			DatabaseConnection.dbHost = prop.getProperty("dbHost");
			
		} catch(Exception e) {
			e.printStackTrace();
			Utilities.logs("Creando config.properties");
			PrintWriter writer = new PrintWriter("config.properties", "UTF-8");
			
			writer.println("#--------------- Game server config ---------------");
			writer.println("#Abrir este puerto en el router");
			writer.println("server_port=9999");
			writer.println("#--------------- MySQL config ---------------");
			writer.println("#Nombre de usuario de PhPMyAdmin");
			writer.println("dbUsername=");
			writer.println("#Contraseña de PhPMyAdmin");
			writer.println("dbPassword=");
			writer.println("#Puerto de la base de datos. '3306' es el por defecto");
			writer.println("dbPort=3306");
			writer.println("#IP del host de la base de datos.");
			writer.println("dbHost=");
			writer.close();
			
			Utilities.logs("Configura el servidor con el archivo 'config.properties'");
			Utilities.logs("Apagando servidor...");
			System.exit(0);
			
		}
    	
    	
    	try {
    		servidor = new ServerSocket(port); 
    		Utilities.logs("Usando el puerto: " + port);
    	} catch(java.net.BindException e) {
    		Utilities.logs("Ya se está utilizando el puerto " + port);    
    		running = false;
    	}
    	
    	new DatabaseConnection();
    	
    	new Thread(new Runnable() {

			@Override
			public void run(){
				
				while(running) {
		    		try {

		    			Socket r = servidor.accept();
			    		new Request(r);
		    			
		    		} catch(Exception e) {
		    			Utilities.logs("ERROR: request error");
		    			e.printStackTrace();
		    		}
		    		
		    	}
				
			}

    	}).start();
    	
    }

    
    public static int port = 9999;
    private static boolean running = true;
    public static ServerSocket servidor;
    public static ArrayList<Request> connectedClients = new ArrayList<Request>();
    
}