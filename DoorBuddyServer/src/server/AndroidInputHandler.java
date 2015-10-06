package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AndroidInputHandler implements Runnable {
	
	private Socket s;
	private Map<String, Socket> androidSockets;
	private ArrayBlockingQueue<String> messages;
	private Scanner in;
	
	public AndroidInputHandler(Socket s, Map<String, Socket> androidSockets, ArrayBlockingQueue<String> messages) throws IOException
	{
		this.s = s;
		this.androidSockets = androidSockets;
		this.messages = messages;
		in = new Scanner(s.getInputStream());
	}
	
	public void run()
	{
		boolean closed = false;
		while(!closed)
		{
			if(in.hasNextLine())
			{
				String inData = in.nextLine();
				Pattern p = Pattern.compile("<D:>(.*)<M:>(.*)<U:>(.*)<T:>(.*)");
				Matcher m = p.matcher(inData);
				
				String dest = m.group(1);
				String message = m.group(2);
				String uname = m.group(3);
				String time = m.group(4);
				
				if(dest.equals("SERVER"))
				{
					if(message.equals("CLOSE"))
					{
						try {
							PrintWriter out = new PrintWriter(s.getOutputStream());
							out.println("Disconnected From Server");
							out.flush();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						androidSockets.remove(uname, s);
						in.close();
						try {
							s.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						closed = true;
						System.out.println(time + ": " + "Username: " + uname + " disconnected.");
					}
				}
				else if(dest.equals("ARDUINO"))
				{
					try {
						messages.put(inData);
						System.out.println("Incoming: " + inData);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
