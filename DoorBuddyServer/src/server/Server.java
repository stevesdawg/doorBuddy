package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;

public class Server {

	public static void main(String[] args) throws IOException {
		
		ArrayList<Socket> androidSockets = new ArrayList<Socket>();
		Socket arduino = null;
		ArrayBlockingQueue<Message> messagePool = new ArrayBlockingQueue<>(30);
		
		ServerSocket server = new ServerSocket(1809);
		while(true)
		{
			Socket incoming = server.accept();
			checkIncomingSocket(incoming, androidSockets, arduino);
			
		}
		
	}
	
	private static void checkIncomingSocket(Socket incoming, ArrayList<Socket> androidSockets, Socket arduino) throws IOException
	{
		Scanner in = new Scanner(incoming.getInputStream());
		String message = "";
		if(in.hasNextLine())
			message = in.nextLine();
		if(message.startsWith("ANDROID CONNECTION"))
			androidSockets.add(incoming);
		else if(message.startsWith("ARDUINO CONNECTION"))
			arduino = incoming;
		
	}

}
