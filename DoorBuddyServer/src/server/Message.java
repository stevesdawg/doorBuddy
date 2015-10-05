package server;

import java.io.ObjectInputStream.GetField;
import java.net.Socket;
import java.util.Date;

public class Message {
	
	public static final String DEST_ANDROID = "ANDROID";
	public static final String DEST_ARDUINO = "ARDUINO";
	public static enum DestinationType {
		ANDROID, ARDUINO
	}
	
	private Socket messageSource;
	private DestinationType destType;
	private Date timeStamp;
	private String message;
	
	public Message(Socket source, Date timeStamp, String message)
	{
		this.messageSource = source;
		this.timeStamp = timeStamp;
		this.message = message;
	}
	
	public Message(Socket source, DestinationType destType, Date timeStamp, String message)
	{
		this(source, timeStamp, message);
		this.destType = destType;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
	public Date getTimeStamp()
	{
		return this.timeStamp;
	}
	
	public Socket getSource()
	{
		return this.messageSource;
	}
	
	public DestinationType getDestinationType()
	{
		return this.destType;
	}
	
	public void setSource(Socket s)
	{
		this.messageSource = s;
	}
	
	public void setDestinationType(DestinationType type)
	{
		this.destType = type;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}

}
