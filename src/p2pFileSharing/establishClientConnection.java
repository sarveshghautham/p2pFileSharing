package p2pFileSharing;
import java.io.IOException;

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
	Boolean finished = false;
	NormalMessages nm = new NormalMessages();	
	
	public establishClientConnection (int mypeer_id, String peer_id, String peer_address, String peer_port, BitFields myBitField, PeerProcess pp) {
		this.peerID = Integer.parseInt(peer_id); //Server's peer ID.
		this.myPeerID = mypeer_id; //Client's peer ID.
		this.hostName = peer_address;
		this.portNumber = Integer.parseInt(peer_port);
		this.myBitFields = myBitField;		
		this.pObj = pp;
	}
	
	public void run() {
		
		try {
			this.finished = false;
			//Creating multiple client sockets for peers already started.
			System.out.println("Trying to establish a connection with: "+this.hostName+" using port number "+this.portNumber);
			Socket ClientSocket = new Socket(this.hostName, this.portNumber);
			this.clientSocket = ClientSocket;
			//System.out.println("Before handshake: PeerID: "+peerID);
			//Send handshake message
			
			System.out.println("Sending handshake to: "+peerID);
			HandShakeMessage HMsg = new HandShakeMessage("HELLO", peerID);
			HMsg.SendHandShakeMessage (ClientSocket);			
			
			//Wait till client receives a handshake from server.
			while (!(HMsg.ReceiveHandShakeMessage(ClientSocket)));
			
			//Now send a bitfield message.
			BitFields clientBMsg = new BitFields(4, 5);
			clientBMsg.intializedBitFieldMsg(myPeerID, this.pObj);
			
			myBitFields.SendBitFieldMsg(ClientSocket);	
			
			
			
			//Now reveive a bitfield message from server.
			BitFields receiveBMsg = new BitFields();
			BitFields returnBMsg = receiveBMsg.ReceiveBitFieldMsg(ClientSocket); 
			
			if (returnBMsg.bitFieldMsg != null)
			{
				this.serverPeerBitFieldMsg = returnBMsg;
				if ( (myBitFields.AnalyzeReceivedBitFieldMsg(returnBMsg)) != null) {
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
				//System.out.println("Client: Listening for messages");
				while ((readObj = cm.listenForMessages(ClientSocket, this)) == null);
				
				int msgType = this.nm.MessageType;
//				System.out.println("Msg type:"+msgType);
				cm.HandleMessages(msgType, readObj, this);
				readObj = null;
				
				if (this.finished == true) {
					break;
				}
			}
			
			System.out.println("Closing client socket");
			clientSocket.close();
			
			return;
		}
		catch (IOException ex) {
			System.out.println("IOException occured:"+ex);
		}
	}	
}