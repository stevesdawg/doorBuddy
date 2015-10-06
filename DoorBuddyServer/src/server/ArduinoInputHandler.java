package server;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;

public class ArduinoInputHandler implements Runnable {
	
	private Socket s;
	private ArrayBlockingQueue<String> messagePool;
	private Scanner in;
	
	public ArduinoInputHandler(Socket s, ArrayBlockingQueue<String> messages) throws IOException
	{
		this.s = s;
		this.messagePool = messages;
		in = new Scanner(s.getInputStream());
	}
	
	public void run()
	{
		while(true)
		{
			if(in.hasNextLine())
			{
				String inData = in.nextLine();
				try {
					messagePool.put(inData);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
