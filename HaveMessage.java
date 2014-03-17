package p2pFileSharing;

import java.io.*;
import java.net.Socket;

public class HaveMessage extends NormalMessages {
	
	int msgByteIndex;
	
	public HaveMessage () {
		
	}
	
	public HaveMessage (int msgLen, int msgType, int msgByteIndex) {
		super (msgLen, msgType);
		this.msgByteIndex = msgByteIndex;
	}
	
	public void SendHaveMsg (Socket clientSocket) throws IOException {
		OutputStream os = clientSocket.getOutputStream();  
		ObjectOutputStream oos = new ObjectOutputStream(os);  			  
		oos.writeObject(this);
	}
	
	public int ReceiveHaveMsg (Socket soc) throws IOException {
		
		try {
			InputStream is = soc.getInputStream();  
			ObjectInputStream ois = new ObjectInputStream(is);  
			HaveMessage hm = (HaveMessage)ois.readObject(); 
			
			if (hm != null) {
				return hm.msgByteIndex;
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
