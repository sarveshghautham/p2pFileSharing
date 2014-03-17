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
	BitFields serverPeerBitFieldMsg;
	
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
			
			//System.out.println("Before handshake: PeerID: "+peerID);
			//Send handshake message
			
			HandShakeMessage HMsg = new HandShakeMessage("HELLO", peerID);
			HMsg.SendHandShakeMessage (ClientSocket);			
			
			//Handshake success
			if (HMsg.ReceiveHandShakeMessage(ClientSocket)) {
				
				//Now send a bitfield message.
				BitFields clientBMsg = new BitFields(4, 5);
				clientBMsg.intializedBitFieldMsg(myPeerID);
				
				//If emptyBitField is set, don't send bitfield msg.
				if (!clientBMsg.emptyBitField) {
					clientBMsg.SendBitFieldMsg(ClientSocket);	
				}
				else {
					System.out.println("Client: Skipping bitfield msg");
				}
				
				//Now reveive a bitfield message from server.
				BitFields receiveBMsg = new BitFields();
				if (receiveBMsg.ReceiveBitFieldMsg(ClientSocket)) {
					this.serverPeerBitFieldMsg = receiveBMsg;
					if (clientBMsg.AnalyzeReceivedBitFieldMsg(receiveBMsg)) {
						//send interested msg.
						InterestedMessage nIMsg = new InterestedMessage(4,3);
						nIMsg.SendInterestedMsg(ClientSocket);
					}
					else {
						//send not interested msg.
						InterestedMessage nIMsg = new InterestedMessage(4,4);
						nIMsg.SendInterestedMsg(ClientSocket);
					}
				}
			}
			
//			clientSocket.close();
			
			/*TODO: We need to accept new peers which are created later. */
		}
		catch (IOException ex) {
			System.out.println("IOException occured:"+ex);
		}
	}	
}