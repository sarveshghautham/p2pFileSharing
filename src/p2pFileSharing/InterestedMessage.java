package p2pFileSharing;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;

public class InterestedMessage extends NormalMessages implements Serializable {

	
	private static final long serialVersionUID = -174333938837408245L;
	int clientPeerID;
	
	public InterestedMessage () {

	}
	
	public InterestedMessage (int MsgLen, int MsgType, int peerID) {
		super(MsgLen, MsgType);
		this.clientPeerID = peerID;
	}
	
	public void SendInterestedMsg (OutputStream os) throws IOException {
		ObjectOutputStream oos;
		synchronized (os) {
			oos = new ObjectOutputStream(os);  			  
			oos.writeObject(this);
		}
	}
	
	public boolean ReceiveInterestedMsg (InputStream is) throws IOException {
		
		try {
			ObjectInputStream ois = new ObjectInputStream(is);  
			InterestedMessage im = (InterestedMessage)ois.readObject(); 
			
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
