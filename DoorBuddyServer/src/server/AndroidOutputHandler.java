package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AndroidOutputHandler implements Runnable{
	
	private Map<String, Socket> androidSockets;
	private ArrayBlockingQueue<String> messagePool;
	
	public AndroidOutputHandler(Map<String, Socket> sockets, ArrayBlockingQueue<String> messages)
	{
		this.androidSockets = sockets;
		this.messagePool = messages;
	}
	
	public void run()
	{
		while(true)
		{
			String outData = null;
			try {
				outData = messagePool.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			Pattern p = Pattern.compile("<D:>(.*)<M:>(.*)<U:>(.*)<T:>(.*)");
			Matcher m = p.matcher(outData);
			
			String dest = m.group(1);
			String message = m.group(2);
			String uname = m.group(3);
			String time = m.group(4);
			
			if(dest.equals("ANDROID"))
			{
				PrintWriter out = null;
				try {
					out = new PrintWriter(androidSockets.get(uname).getOutputStream());
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				out.println("<M:>" + message + "<T:>" + time);
				out.close();
			}
		}
	}

}
