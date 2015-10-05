package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;

public class Server {

	public static void main(String[] args) throws IOException {
		
		Map<String, Socket> androidSockets = new HashMap<String, Socket>();
		Socket arduino = null;
		ArrayBlockingQueue<Message> messagePool = new ArrayBlockingQueue<Message>(30);
		
		ServerSocket server = new ServerSocket(1809);
		while(true)
		{
			Socket incoming = server.accept();
			Scanner in = new Scanner(incoming.getInputStream());
			String message = "";
			if(in.hasNextLine())
				message = in.nextLine();
			String[] messages = message.split(" ");
			if(messages[0].equals("ANDROID_CONNECTION"))
			{
				androidSockets.put(messages[1], incoming);
			}
			else if(messages[0].equals("ARDUINO_CONNECTION"))
			{
				arduino = incoming;
			}
			
		}
		
	}
	
}
