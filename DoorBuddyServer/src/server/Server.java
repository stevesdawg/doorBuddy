package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Server {
	
	public static void main(String[] args) throws IOException {
		
		Map<String, Socket> androidSockets = Collections.synchronizedMap(new HashMap<String, Socket>());
		Socket arduino = null;
		ArrayBlockingQueue<String> messagePool = new ArrayBlockingQueue<String>(30);
		
		ServerSocket server = new ServerSocket(1809);
		System.out.println("Server running on: 1809. Listening...");
		System.out.println(server.getLocalSocketAddress());
		while(true)
		{
			Socket incoming = server.accept();
			Scanner in = new Scanner(incoming.getInputStream());
			if(in.hasNextLine())
			{
				String inData = in.nextLine();
				if(inData.startsWith("ANDROID_OPEN"))
				{
					String username = inData.substring(13);
					androidSockets.put(username, incoming);
					new Thread(new AndroidInputHandler(incoming, androidSockets, messagePool)).start();
					System.out.println("ANDROID CONNECTED. Username: " + username);
				}
				else if(inData.startsWith("ARDUINO_OPEN"))
				{
					arduino = incoming;
					new Thread(new ArduinoInputHandler(incoming, messagePool)).start();
					System.out.println("ARDUINO CONNECTED.");
				}
				else {
					System.out.println(in.nextLine());
				}
			}
		}
		
	}
	
}
