package server;

import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

public class AndroidInputHandler {
	
	private Socket androidSocket;
	private ArrayBlockingQueue<Message> messages;
	
	public AndroidInputHandler(Socket s, ArrayBlockingQueue<Message> messages)
	{
		this.androidSocket = s;
		this.messages = messages;
	}
	
	public void run()
	{
		
	}

}
