package p2pFileSharing;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;

public class NotInterestedMessage extends NormalMessages implements Serializable{

	
	private static final long serialVersionUID = 7153161823596163637L;
	int clientPeerID;
	boolean finished=false;
	
	public NotInterestedMessage () {
		
	}
	
	public NotInterestedMessage (int MsgLen, int MsgType, int clientPeerID, boolean finished) {
		super(MsgLen, MsgType);
		this.finished = finished;
		this.clientPeerID = clientPeerID;
	}
	
	public void SendNotInterestedMsg (OutputStream os ) throws IOException {
		  
		ObjectOutputStream oos;
		
		synchronized (os) {
			oos = new ObjectOutputStream(os);  			  
			oos.writeObject(this);	
		}
		
	}
	
	public boolean ReceiveNotInterestedMsg (InputStream is) throws IOException {
		
		try {
			
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
