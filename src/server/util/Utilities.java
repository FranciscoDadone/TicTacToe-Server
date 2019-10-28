package server.util;

import java.time.LocalDateTime;

public class Utilities {
	
	public static void logs(String message) {
		
		System.out.println("[" + LocalDateTime.now().getHour() + ":" 
							+ LocalDateTime.now().getMinute() + ":" 
							+ LocalDateTime.now().getSecond() + "]" + " - " + message);
		
	}
	
	public static int width = 800;
	public static int height = 500;
	
}
