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
	BitFields myBitFields;
	PeerProcess pObj;
	NormalMessages nm = new NormalMessages();	
	
	public establishClientConnection (int mypeer_id, String peer_id, String peer_address, String peer_port, BitFields myBitField) {
		this.peerID = Integer.parseInt(peer_id); //Server's peer ID.
		this.myPeerID = mypeer_id; //Client's peer ID.
		this.hostName = peer_address;
		this.portNumber = Integer.parseInt(peer_port);
		this.myBitFields = myBitField;		
	}
	
	public void run() {
		try {
			//Creating multiple client sockets for peers already started.
			Socket ClientSocket = new Socket(hostName, portNumber);
			
			//System.out.println("Before handshake: PeerID: "+peerID);
			//Send handshake message
			
			HandShakeMessage HMsg = new HandShakeMessage("HELLO", peerID);
			HMsg.SendHandShakeMessage (ClientSocket);			
			
			//Wait till client receives a handshake from server.
			while (!(HMsg.ReceiveHandShakeMessage(ClientSocket)));
			
			//Now send a bitfield message.
			//	BitFields clientBMsg = new BitFields(4, 5);
			//	clientBMsg.intializedBitFieldMsg(myPeerID);
			
			myBitFields.SendBitFieldMsg(ClientSocket);	
			
			//Now reveive a bitfield message from server.
			BitFields receiveBMsg = new BitFields();
			
			if (receiveBMsg.ReceiveBitFieldMsg(ClientSocket))
			{
				this.serverPeerBitFieldMsg = receiveBMsg;
				if ( (myBitFields.AnalyzeReceivedBitFieldMsg(receiveBMsg)) != null) {
					//send interested msg.
					InterestedMessage nIMsg = new InterestedMessage(0,2, myPeerID);
					nIMsg.SendInterestedMsg(ClientSocket);
				}
				else {
					//send not interested msg.
					NotInterestedMessage nIMsg = new NotInterestedMessage(0,3, myPeerID);
					nIMsg.SendNotInterestedMsg(ClientSocket);
				}
			}
			else {
				//send not interested msg.
				NotInterestedMessage nIMsg = new NotInterestedMessage(0,3, myPeerID);
				nIMsg.SendNotInterestedMsg(ClientSocket);
			}
			
			ClientMessageHandler cm = new ClientMessageHandler();
			Object readObj = null;
			
			while (true) {
				while ((readObj = cm.listenForMessages(clientSocket, this.nm)) == null);
				
				int msgType = this.nm.MessageType;
				
				cm.HandleMessages(msgType, readObj, this);
				
				readObj = null;
			}
			
			//notify other threads about the updated bit field.
		
			
//			clientSocket.close();
			
			/*TODO: We need to accept new peers which are created later. */
		}
		catch (IOException ex) {
			System.out.println("IOException occured:"+ex);
		}
	}	
}