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
	
	public void SendHandShakeMessage (Socket ClientSocket) throws IOException {
		
		OutputStream os = ClientSocket.getOutputStream();  
		ObjectOutputStream oos = new ObjectOutputStream(os);  			  
		oos.writeObject(this);
		
		//System.out.println("Hello sent to:"+this.PeerID);
		
		//os.close();
		//oos.close();
	}
	
	public boolean ReceiveHandShakeMessage (Socket ClientSocket) throws IOException {
		try {
			InputStream is = ClientSocket.getInputStream();  
			ObjectInputStream ois = new ObjectInputStream(is);  
			HandShakeMessage RespMsg = (HandShakeMessage)ois.readObject();  
			if (RespMsg != null) {
			
				if (RespMsg.PeerID == this.PeerID) {
					System.out.println("HandShake success");
					return true;
				}
				else {
					System.out.println("HandShake failed");
					return false;
				}
			}
			else {
				return false;
			}
			
		} 
		catch (ClassNotFoundException ex) {
			System.out.println(ex);
		}
		finally {
			//is.close();
			//ois.close();
		}
		return false;
	}
}