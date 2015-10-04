package server;

import java.net.Socket;
import java.util.Date;

public class Message {
	
	public static final String DEST_ANDROID = "ANDROID";
	public static final String DEST_ARDUINO = "ARDUINO";
	
	private Socket messageSource;
	private String destinationType;
	private Date timeStamp;
	private String message;
	
	public Message(Socket source, Date timeStamp, String message)
	{
		this.messageSource = source;
		this.timeStamp = timeStamp;
		this.message = message;
	}
	
	public Message(Socket source, String destType, Date timeStamp, String message)
	{
		this(source, timeStamp, message);
		this.destinationType = destType;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
	public Date getTimeStamp()
	{
		
	}

}
