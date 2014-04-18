package p2pFileSharing;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class NotInterestedMessage extends NormalMessages {

	int clientPeerID;
	
	public NotInterestedMessage () {
		
	}
	
	public NotInterestedMessage (int MsgLen, int MsgType, int clientPeerID) {
		super(MsgLen, MsgType);
		this.clientPeerID = clientPeerID;
	}
	
	public void SendNotInterestedMsg (Socket clientSocket) throws IOException {
		OutputStream os = clientSocket.getOutputStream();  
		ObjectOutputStream oos = new ObjectOutputStream(os);  			  
		oos.writeObject(this);
	}
	
	public boolean ReceiveNotInterestedMsg (Socket soc) throws IOException {
		
		try {
			InputStream is = soc.getInputStream();  
			ObjectInputStream ois = new ObjectInputStream(is);  
			NotInterestedMessage im = (NotInterestedMessage)ois.readObject(); 
			
			if (im != null) {
				return true;
			}
			else {
				return false;
			}
		} 
		catch (ClassNotFoundException ex) {
			System.out.println(ex);
			return false;
		}
		finally {
			//is.close();
			//ois.close();
		}
	}
}
