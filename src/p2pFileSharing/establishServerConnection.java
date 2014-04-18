package p2pFileSharing;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

class establishServerConnection extends Thread {
	
	public Socket connectionSocket;
	int PeerID;
	int cPeerID;
	BitFields clientPeerBitFieldMsg;
	BitFields myBitFields;
	PeerProcess pObj;
	boolean interested;
	boolean notInterested;
	
	
	public NormalMessages nm = new NormalMessages();
	
	public establishServerConnection (Socket conSock, int peer_id, PeerProcess pp) {
		this.connectionSocket = conSock;
		this.PeerID = peer_id; //My peer ID.
		this.myBitFields = pObj.myBitFields;
		this.pObj = pp;
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
			
			//Wait for bitField msg from the client.
			while (!receivedClientBMsg.ReceiveBitFieldMsg(connectionSocket));
			
			//Constructing server bitfield.
			BitFields serverBMsg = new BitFields(4,5);
			
			//Server bitfield is not empty.
			if (!serverBMsg.emptyBitField) {
				//Sending server bitfield.
				serverBMsg.SendBitFieldMsg(connectionSocket);			
				this.clientPeerBitFieldMsg = receivedClientBMsg;
			
			}
			else { //server bitfield is empty.
				System.out.println("Server: Skipping bitfield msg");
			}
			
			ServerMessageHandler m = new ServerMessageHandler();
			Object readObj;
			while (true) {
				
				while ((readObj = m.listenForMessages(connectionSocket, this.nm)) == null);
				
				int msgType = this.nm.MessageType;
				m.HandleMessages(msgType, readObj, this);
				
				readObj = null;
			}
			
			/*
			
			ChokeUnchokeMessage c = new ChokeUnchokeMessage(0,1);
			
			//Need to change the condition
			while (true) {
				//Keep waiting.
				while (!(this.pObj.PreferredNeighbors.contains(cPeerID))); 
				
				//Send unchoked message to preferred neighbor.
				c.SendUnchokeMsg(connectionSocket);
				
				//Wait for request message.
				
				
				//Data exchange.					
				
				while (this.pObj.choke == false);
					
				if (this.pObj.chokeList.contains(cPeerID)) {
					c.SendChokeMsg(connectionSocket);
				}
			
				this.pObj.choke = true;
				
			}
		}
				
			
			//Wait for client's interest/not interested message.
			
				
			//Receive have message.
			HaveMessage rxHvMsg = new HaveMessage();
			int byteIndex = rxHvMsg.ReceiveHaveMsg(connectionSocket); 
			if (byteIndex != -1) {
				this.clientPeerBitFieldMsg.UpdateBitFieldMsg(byteIndex);
			}
			else {
				System.out.println("Error in receiving have msg");
			}
			*/			
		}
		
		catch (IOException ex) {
			System.out.println(ex);
		}	
	}

}
