package server.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import server.util.Utilities;

public class DatabaseConnection {

	public DatabaseConnection() throws Exception {

		new Thread(new Runnable() {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			@Override
			public void run() {
				
				while(true) {
					String input = "";
					try {
						input = br.readLine();
					} catch (IOException e) {}
					if(input.equalsIgnoreCase("list")) {
						
						int games = Integer.parseInt(DatabaseConnection.gamesCount())-1;
						Utilities.logs("Hay " + games + " partidas creadas.");
						
					} else if(input.equalsIgnoreCase("purge")) {
						dropTables();
					} else if(input.equalsIgnoreCase("stop")) {
						Utilities.logs("Apagando servidor...");
						dropTables();
						Utilities.logs("Servidor apagado.");
						System.exit(0);
					} else {
						Utilities.logs("Los comandos disponibles son: ");
						System.out.println("                - list: muestra la lista de partidas en progreso.");
						System.out.println("                - purge: elimina todas las partidas creadas.");
						System.out.println("                - stop: cierra el servidor.");
					}
					
				}
				
			}
			
		}).start();
		
		//Iniciando base de datos...
    	Class.forName("com.mysql.cj.jdbc.Driver");
    	String conextion_host = "jdbc:mysql://"+dbHost+":"+dbPort+"/"+dbNameUsername;
		bbdd = DriverManager.getConnection(conextion_host, dbNameUsername, dbPassword);
		statement = bbdd.createStatement();
		
		try {
			
			String crearTabla = "CREATE TABLE `"+dbNameUsername+"`.`accounts` ( `USERNAME` VARCHAR(16) NOT NULL , `PASSWORD` VARCHAR(40) NOT NULL , `WINS` INT NOT NULL ) ENGINE = InnoDB;";
			statement.executeUpdate(crearTabla);
			
		} catch(Exception e) {
			Utilities.logs("DB INFO: 'accounts' ya creada.");
		}
		
	}
	
	public static String registerAccount(String username, String password) throws SQLException {
		
		if(username.length() < 5) {
			
			return "El nombre de usuario debe tener más de 5 carácteres!";
			
		} else if(username.length() > 16) {
			
			return "El nombre de usuario no puede tener más de 16 carácteres!";
			
		} else if(password.length() < 4) {
			
			return "La contraseña debe tener al menos 5 carácteres";
			
		} else {
			
			String command = "SELECT * FROM accounts WHERE USERNAME='"+username+"'";
			ResultSet res = statement.executeQuery(command);
			boolean exists = false;
			try {
				res.next();
				exists = res.getString("USERNAME").equals(username);
			} catch(Exception e) {}

			if(!exists) {
				
				command = "INSERT INTO `accounts`(`USERNAME`, `PASSWORD`, `WINS`) VALUES ('"+username+"',SHA1('"+password+"'),0)";
				statement.executeUpdate(command);
				return "Success";
				
			} else {
				
				Utilities.logs("Nombre de usuario ya en uso.");
				return "El nombre de usuario ya está en uso.";
				
			}
			
		}
		
	}
	
	public static String login(String username, String password) {
		
		DatabaseConnection.username = username;
		
		if(username.length() < 5) {
			
			return "El nombre de usuario debe tener más de 5 carácteres!";
			
		} else if(username.length() > 16) {
			
			return "El nombre de usuario no puede tener más de 16 carácteres!";
			
		} else if(password.length() < 4) {
			
			return "La contraseña debe tener al menos 5 carácteres";
			
		} else {
			
			boolean authenticated = false;
			String command = "SELECT * FROM accounts WHERE USERNAME = '"+username+"' AND PASSWORD = SHA1('"+password+"')";
			try {
				ResultSet res = statement.executeQuery(command);
				
				res.next();
				authenticated = res.getString("USERNAME").equals(username);
				wins = res.getInt("WINS");
				
				if(authenticated)
					return "Success";
				else
					return "El usuario o la contraseña no concuerdan!";
				
			} catch (SQLException e) {
				return "El usuario o la contraseña no concuerdan!";
			}
			
		}
		
	}
	
	public static void createNewGame(String gameID) {
		DatabaseConnection.gameID = gameID;
		try {
			String crearTabla = "CREATE TABLE `"+dbNameUsername+"`.`"+gameID+"` ( `USERNAME` VARCHAR(16) NOT NULL , `MOVE_X` VARCHAR(1), `MOVE_Y` VARCHAR(1), `SIMBOLO` VARCHAR(3), `TURNO` VARCHAR(12)) ENGINE = InnoDB;";
			statement.executeUpdate(crearTabla);
			Utilities.logs("Juego '" + gameID + "' creado correctamente.");
			
		} catch(SQLException e) {
			Utilities.logs("ERROR: Juego ya creado? (" + gameID + ")");
		}
		
	}
	
	public static void deleteGame(String gameID) {
		
		try {
			
			String eliminarTabla = "DROP TABLE `"+gameID+"`";
			statement.executeUpdate(eliminarTabla);
			Utilities.logs("Juego '" + gameID + "' eliminado correctamente.");
			
		} catch(Exception e) {
			Utilities.logs("ERROR: Partida ya eliminada (" + gameID + ")");
		}
		
	}
	
	public static void cargarUserAPartida(String gameID, String username, String simbolo) {
		
		try {
			String turno = "";
			if(simbolo.equals("X"))
				turno = "JUEGA";
			
			
			String actualizarTabla = "INSERT INTO `"+gameID+"`(`USERNAME`, `MOVE_X`, `MOVE_Y`, `SIMBOLO`, `TURNO`) VALUES ('"+username+"',null,null,'"+simbolo+"', '"+turno+"')";
			statement.executeUpdate(actualizarTabla);
			Utilities.logs("Usuario '" + username + "' correctamente cargado a la partida '" + gameID + "'. (" + simbolo + ")");
			
		} catch(SQLException e) {
			Utilities.logs("ERROR: El usuario '" + username + "' no se ha podido conectar a la partida '" + gameID + "'.");
		}
		
	}
	
	public static void eliminarUserDePartida(String gameID, String username) {
		
		try {
			
			String deleteUser = "DELETE FROM `"+gameID+"` WHERE `USERNAME`='"+username+"'";
			statement.executeUpdate(deleteUser);
			Utilities.logs("Usuario '" + username + "' correctamente eliminado de la partida '" + gameID + "'");
			
		} catch(SQLException e) {
			Utilities.logs("ERROR: El usuario '" + username + "' no se ha podido eliminar de la partida '" + gameID + "'.");
		}
		
	}
	
	public static String getReadyPlayers(String gameID) throws SQLException {
		
		try {
			
			String command = "SELECT COUNT(*) FROM `" + gameID + "`";
			
			ResultSet res = statement.executeQuery(command);
			res.next();
			return res.getString("COUNT(*)");
			
		} catch(Exception e) {}
		return "0";
	}
	
	public static ArrayList<String> getOtherPlayerInfo(String gameID, String username) {
		DatabaseConnection.username = username;
		try {
			ArrayList<String> info = new ArrayList<String>();
			String command = "SELECT * FROM `" + gameID + "` WHERE USERNAME != '"+username+"'";
			info.removeAll(info);
			ResultSet res = statement.executeQuery(command);
			res.next();
		
			info.add(res.getString("USERNAME"));
			info.add(res.getString("SIMBOLO"));
			info.add(res.getString("TURNO"));
			info.add("");
			info.add(res.getString("MOVE_X"));
			info.add(res.getString("MOVE_Y"));
			String command2 = "SELECT WINS FROM `accounts` WHERE USERNAME = '"+info.get(0)+"'";
			ResultSet res2 = statement.executeQuery(command2);
			res2.next();
			info.set(3, res2.getString("WINS"));
			
			return info;
			
		} catch(Exception e) {}
		ArrayList<String> fallback = new ArrayList<String>();
		fallback.add("Offline");
		fallback.add("-");
		fallback.add("-");
		fallback.add("-");
		return fallback;
	}
	
	public static void pasarTurno(int x, int y, String username) {
		
		String rival = getOtherPlayerInfo(gameID, username).get(0);
		
		try {
			String command = "UPDATE `"+gameID+"` SET `TURNO`='JUEGA' WHERE USERNAME='"+rival+"'";
			statement.executeUpdate(command);
			command = "UPDATE `"+gameID+"` SET `TURNO`='', MOVE_X='"+x+"', MOVE_Y='"+y+"' WHERE USERNAME='"+username+"'";
			statement.executeUpdate(command);
		} catch(Exception e) {
			Utilities.logs("GAME INFO: ERROR AL PASAR EL TURNO");
			e.printStackTrace();
		}
	}	
	
	private static String gamesCount() {
		
		try {
			
			String command = "SELECT count(*) AS TOTALNUMBEROFTABLES FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'sAvPElBwoc';"; 
			ResultSet res = statement.executeQuery(command);
			res.next();
			return res.getString("TOTALNUMBEROFTABLES");
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	private static void dropTables() {
		
		if(gamesCount().equals("1")) {
			Utilities.logs("No hay partidas para eliminar.");
			return;
		}
		
		ArrayList<String> tables = new ArrayList<String>();
		try {
			
			String command = "SHOW TABLES";
			ResultSet res = statement.executeQuery(command);
			
			while(res.next()) {
				
				tables.add(res.getString("Tables_in_" + DatabaseConnection.dbNameUsername));
				
			}
			
			
			for(int i = 0; i < tables.size(); i++) {
				
				if(!tables.get(i).equals("accounts")) {
					
					command = "DROP TABLE `" + tables.get(i) + "`";
					statement.executeUpdate(command);
					Utilities.logs("Partida '" + tables.get(i) + " eliminada.");
					
				}
				
			}
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	public static String username;
	public static int wins, readyPlayers;
	public static String gameID;
	public static String dbNameUsername="sAvPElBwoc", dbPassword = "jHLx3J4242", dbPort="3306", dbHost="remotemysql.com";
	private Connection bbdd;
	private static Statement statement;
}
