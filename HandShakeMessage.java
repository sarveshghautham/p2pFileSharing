package p2pFileSharing;
import java.io.*;
import java.net.*;

public class HandShakeMessage implements Serializable {
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
		System.out.println("Hello sent to: "+this.PeerID);
		os.close();
		oos.close();
	}
	
	public void ReceiveHandShakeMessage (Socket ClientSocket) throws IOException {
		try {
			InputStream is = ClientSocket.getInputStream();  
			ObjectInputStream ois = new ObjectInputStream(is);  
			HandShakeMessage RespMsg = (HandShakeMessage)ois.readObject();  
			if (RespMsg != null) {
				System.out.println(RespMsg.PeerID);
			}  
			
			if (RespMsg.PeerID == this.PeerID) {
				System.out.println("HandShake success");
			}
			else {
				System.out.println("HandShake failed");
			}
			
			is.close();
			ois.close();
		} 
		catch (ClassNotFoundException ex) {
			System.out.println(ex);
		}
	}
}