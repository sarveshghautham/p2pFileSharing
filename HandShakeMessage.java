package p2pFileSharing;
import java.io.*;

public class HandShakeMessage implements Serializable {
	String HeaderMessage;
	int PeerID;
	
	public HandShakeMessage (String HeaderMsg, int peerID) {
		HeaderMessage = HeaderMsg;
		PeerID = peerID;
	}
	
	public void PrintMessage () {
		System.out.println("Header message: "+HeaderMessage);
		System.out.println("Peer ID: "+PeerID);
	}
}