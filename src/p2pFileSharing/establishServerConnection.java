package p2pFileSharing;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.HashSet;


class establishServerConnection extends Thread implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8127805062892273088L;
	public Socket connectionSocket;
	int PeerID;
	int cPeerID;
	BitFields clientPeerBitFieldMsg;
	BitFields myBitFields;
	PeerProcess pObj;
	boolean interested;
	boolean notInterested;
	
	
	public NormalMessages nm = new NormalMessages();
	public InterestedMessage im = new InterestedMessage();
	
	public establishServerConnection (Socket conSock, int peer_id, PeerProcess pp) {
		this.connectionSocket = conSock;
		this.PeerID = peer_id; //My peer ID.
		this.pObj = pp;
		this.myBitFields = pp.myBitFields;
		
	}
	
	public void run () {
		//Get data from multiple clients.
		try {
			
			HandShakeMessage HMsg = new HandShakeMessage(PeerID);
			
			//Wait till the server gets a handshake from client.
			while (!HMsg.ReceiveHandShakeMessage(connectionSocket));
			
			HandShakeMessage RespMsg = new HandShakeMessage("HELLO", PeerID);
			RespMsg.SendHandShakeMessage(connectionSocket);
		
			
			BitFields receivedClientBMsg = new BitFields();
			BitFields returnBMsg; 
			//Wait for bitField msg from the client.
			while (true){
				returnBMsg = receivedClientBMsg.ReceiveBitFieldMsg(connectionSocket);
				
				if (returnBMsg != null) {
					break;
				}
			}		
			
			//Constructing server bitfield.
			BitFields serverBMsg = new BitFields(4,5);
			serverBMsg.intializedBitFieldMsg(PeerID, this.pObj);
			System.out.println("In server: "+this.pObj.neededByteIndex.size());
			
			//Server bitfield is not empty.
			if (!serverBMsg.emptyBitField) {
				//Sending server bitfield.
				serverBMsg.SendBitFieldMsg(connectionSocket);			
				this.clientPeerBitFieldMsg = receivedClientBMsg;
			
			}
			else { //server bitfield is empty.
				System.out.println("Server: Skipping bitfield msg");
			}
			HashSet<Integer> localReceivedByteIndex = new HashSet<Integer>();
			
			ServerMessageHandler m = new ServerMessageHandler();
			Object readObj;
			while (true) {
				System.out.println("Server: Listening for messages");
				while ((readObj = m.listenForMessages(connectionSocket, this)) == null);
				//readObj = m.listenForMessages(connectionSocket, this.nm);
				int msgType = this.nm.MessageType;
				System.out.println("MsgType:"+msgType);
				m.HandleMessages(msgType, readObj, this, localReceivedByteIndex);
				
				readObj = null;
			}			
		}
		
		catch (IOException ex) {
			System.out.println(ex);
		}	
	}

}
