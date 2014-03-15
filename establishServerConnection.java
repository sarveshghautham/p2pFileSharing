package p2pFileSharing;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

class establishServerConnection extends Thread {
	
	public Socket connectionSocket;
	int PeerID;
	
	public establishServerConnection (Socket conSock, int peer_id) {
		connectionSocket = conSock;
		PeerID = peer_id;
	}
	
	public void run () {
		//Get data from multiple clients.
		try {
			
			HandShakeMessage HMsg = new HandShakeMessage(PeerID);
			HMsg.ReceiveHandShakeMessage(connectionSocket);
			
			HandShakeMessage RespMsg = new HandShakeMessage("HELLO", PeerID);
			RespMsg.SendHandShakeMessage(connectionSocket);
		
		}
		catch (IOException ex) {
			System.out.println(ex);
		}	
	}

}
