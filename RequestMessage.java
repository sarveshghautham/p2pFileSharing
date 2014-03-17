package p2pFileSharing;
import java.io.*;
import java.net.Socket;

public class RequestMessage extends NormalMessages{

int msgByteIndex;
	
	public RequestMessage () {
		
	}
	
	public RequestMessage (int msgLen, int msgType, int msgByteIndex) {
		super (msgLen, msgType);
		this.msgByteIndex = msgByteIndex;
	}
	
	public void SendRequestMsg (Socket clientSocket) throws IOException {
		OutputStream os = clientSocket.getOutputStream();  
		ObjectOutputStream oos = new ObjectOutputStream(os);  			  
		oos.writeObject(this);
	}
	
	public int ReceiveRequestMsg (Socket soc) throws IOException {
		
		try {
			InputStream is = soc.getInputStream();  
			ObjectInputStream ois = new ObjectInputStream(is);  
			RequestMessage rm = (RequestMessage)ois.readObject(); 
			
			if (rm != null) {
				return rm.msgByteIndex;
			}
			else {
				return -1;
			}
		} 
		catch (ClassNotFoundException ex) {
			System.out.println(ex);
			return -1;
		}
		finally {
			//is.close();
			//ois.close();
		}
	}
	
}
