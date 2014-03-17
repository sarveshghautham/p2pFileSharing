package p2pFileSharing;

import java.io.IOException;
import java.net.Socket;

class establishServerConnection extends Thread {
	
	public Socket connectionSocket;
	int PeerID;
	BitFields clientPeerBitFieldMsg;
	boolean interested;
	boolean notInterested;
	
	public establishServerConnection (Socket conSock, int peer_id) {
		connectionSocket = conSock;
		PeerID = peer_id; //My peer ID.
	}
	
	public void run () {
		//Get data from multiple clients.
		try {
			
			HandShakeMessage HMsg = new HandShakeMessage(PeerID);
			HMsg.ReceiveHandShakeMessage(connectionSocket);
			
			HandShakeMessage RespMsg = new HandShakeMessage("HELLO", PeerID);
			RespMsg.SendHandShakeMessage(connectionSocket);
		
			//Wait for bitField msg from the client.
			BitFields receivedClientBMsg = new BitFields();
			boolean hasReceived = receivedClientBMsg.ReceiveBitFieldMsg(connectionSocket);
			
			//Constructing server bitfield.
			BitFields serverBMsg = new BitFields(4,5);
			serverBMsg.intializedBitFieldMsg(PeerID);
			
			//Server bitfield is not empty.
			if (!serverBMsg.emptyBitField) {
				//Sending server bitfield.
				serverBMsg.SendBitFieldMsg(connectionSocket);
				
				if (hasReceived == true) {
					this.clientPeerBitFieldMsg = receivedClientBMsg;
				}
			}
			else { //server bitfield is empty.
				System.out.println("Server: Skipping bitfield msg");
				/* Not needed
				if (hasReceived == true) {
					//Send interested msg.
					InterestedMessage nIMsg = new InterestedMessage(4,3);
					nIMsg.SendInterestedMsg(connectionSocket);
				}
				else {
					//send not interested msg.
					InterestedMessage nIMsg = new InterestedMessage(4,4);
					nIMsg.SendInterestedMsg(connectionSocket);
				}
				*/
			}
			
			//Wait for client's interest/not interested message.
			InterestedMessage fromClientIMsg = new InterestedMessage();
			fromClientIMsg.ReceiveInterestedMsg(connectionSocket);
			
			if (fromClientIMsg.MessageType == 4) {
				this.notInterested = true;
			}
			else {
				this.interested = true;
			}
		}
		
		catch (IOException ex) {
			System.out.println(ex);
		}	
	}

}
