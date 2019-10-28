package server.requests;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import game.window.multiplayer.serverConnection.Packet;
import server.database.DatabaseConnection;
import server.util.Utilities;

public class Request {
	
	public Request(Socket request) throws Exception {		
		ObjectInputStream paqueteDatos;
		
		try {
			paqueteDatos = new ObjectInputStream(request.getInputStream());
			recibido = (Packet)paqueteDatos.readObject();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		username = recibido.getUsername();
		password = recibido.getPassword();
		gameID = recibido.getGameID();
		simbolo = recibido.getSimbolo();
		
		out = new DataOutputStream(request.getOutputStream());
		if(recibido.getDatabaseChange().equals("USER AUTH: login")) {

			try {
				sendToClient = DatabaseConnection.login(username, password);
				out.writeUTF(sendToClient);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if(recibido.getDatabaseChange().equals("USER AUTH: register")) {
			sendToClient = DatabaseConnection.registerAccount(username, password);
			out.writeUTF(sendToClient);
		} else if(recibido.getDatabaseChange().equals("GAME: delete")) {
			DatabaseConnection.deleteGame(gameID);
		} else if(recibido.getDatabaseChange().equals("GAME: create")) {
			DatabaseConnection.createNewGame(gameID);
		} else if(recibido.getDatabaseChange().equals("GAME: addUser")) {
			DatabaseConnection.cargarUserAPartida(gameID, username, simbolo);
		} else if(recibido.getDatabaseChange().equals("GAME: getOtherPlayerInfo")) {
			info = DatabaseConnection.getOtherPlayerInfo(gameID, username);
			
			ArrayList<String> info2 = new ArrayList<String>();
			info2 = info;
			
			ObjectOutputStream objectOutput = new ObjectOutputStream(request.getOutputStream());
            objectOutput.writeObject(info2); 
			
            sendToClient = info.toString();
			
		} else if(recibido.getDatabaseChange().equals("INFO: wins")) {
			sendToClient = ""+DatabaseConnection.wins;
			out.writeUTF(sendToClient);
		} else if(recibido.getDatabaseChange().equals("GAME: readyPlayers")) {
			sendToClient = DatabaseConnection.getReadyPlayers(gameID);
			out.writeUTF(sendToClient);
		} else if(recibido.getDatabaseChange().equals("GAME: pasarTurno")) {
			DatabaseConnection.pasarTurno(recibido.getX(), recibido.getY(), recibido.getUsername());
		} else if(recibido.getDatabaseChange().equals("INFO: aumentarWins")) {
			DatabaseConnection.aumentarWins(username);
		}

		Utilities.logs("Sent packet: " + recibido.getDatabaseChange() + ": '" + sendToClient + "' to '" + username + "' (" + request.getInetAddress() + ").");
		
		try {
			request.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public Socket getSocket() {
		return request;
	}
	
	private String username, password, sendToClient, gameID, simbolo;
	private Socket request;
	private Packet recibido;
	private DataOutputStream out;
	public static ArrayList<String> info = new ArrayList<String>();
	
}
