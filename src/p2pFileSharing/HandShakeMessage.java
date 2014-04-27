package p2pFileSharing;
import java.io.*;
import java.net.*;

public class HandShakeMessage implements Serializable {
	
	private static final long serialVersionUID = -849530622076337158L;
	String HeaderMessage;
	int PeerID;
	
	public HandShakeMessage (int peerID) {
		PeerID = peerID;
	}
	
	public HandShakeMessage (String HeaderMsg, int peerID) {
		HeaderMessage = HeaderMsg;
		PeerID = peerID;
	}
	
	public void PrintMessage () {
		System.out.println("Header message: "+HeaderMessage);
		System.out.println("Peer ID: "+PeerID);
	}
	
	public void SendHandShakeMessage (OutputStream out) throws IOException {
		
		  
		ObjectOutputStream oos = new ObjectOutputStream(out);  			  
		oos.writeObject(this);
		System.out.println("Sending hs with " + this.PeerID);
		//System.out.println("Hello sent to:"+this.PeerID);
		
		//os.close();
		//oos.close();
	}
	
	public int ReceiveHandShakeMessage (InputStream in) throws IOException {
		try {
			//InputStream is = ClientSocket.getInputStream();  
			ObjectInputStream ois = new ObjectInputStream(in);  
			HandShakeMessage RespMsg = (HandShakeMessage)ois.readObject();  
			if (RespMsg != null) {
			
				
				return RespMsg.PeerID;
				
			}
			else {
				return -1;
			}
			
		} 
		catch (ClassNotFoundException ex) {
			System.out.println(ex);
		}
		finally {
			//is.close();
			//ois.close();
		}
		return -1;
	}
}