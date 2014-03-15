package p2pFileSharing;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

class establishClientConnection extends Thread {
	
	int peerCount;
	int peerID;
	int myPeerID;
	int portNumber;
	String hostName;
	Socket clientSocket;
	
	public establishClientConnection (int mypeer_id, String peer_id, String peer_address, String peer_port) {
		peerID = Integer.parseInt(peer_id); //Server's peer ID.
		myPeerID = mypeer_id; //Client's peer ID.
		hostName = peer_address;
		portNumber = Integer.parseInt(peer_port);
	}
	
	public void run() {
		try {
			//Creating multiple client sockets for peers already started.
			Socket ClientSocket = new Socket(hostName, portNumber);
			
			System.out.println("Before handshake: PeerID: "+peerID);
			//Send handshake message
			//Hmsg.SendHandShakeMessage (peerID, );
			HandShakeMessage HMsg = new HandShakeMessage("HELLO", peerID);
			HMsg.SendHandShakeMessage (ClientSocket);
			
			HMsg.ReceiveHandShakeMessage(ClientSocket);
			
//			clientSocket.close();
			
			/*TODO: We need to accept new peers which are created later. */
			
		}
		catch (IOException ex) {
			System.out.println("IOException occured:"+ex);
		}
	}	
}