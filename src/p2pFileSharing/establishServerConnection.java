package p2pFileSharing;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;


class establishServerConnection implements Runnable, Serializable {
	
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
	ServerSocket servSock;
	int peerPort;
	boolean initialStage = false;
	
	public NormalMessages nm = new NormalMessages();
	public InterestedMessage im = new InterestedMessage();
	
	public establishServerConnection (int peer_id, PeerProcess pp) {
		this.PeerID = peer_id; //My peer ID.
		this.pObj = pp;
		//this.myBitFields = pp.myBitFields;
		//this.connectionSocket = soc;
	}
	
	public void run () {
		//Get data from multiple clients.
		//try {
			
			//Server simply listen to clients.
			boolean active = true;
		
			try {
			
			
				while (active) {
					System.out.println("Starrign server on " + pObj.serverSock);
					System.out.println("My local address = " + InetAddress.getLocalHost().getHostName());
					Socket soc = this.pObj.serverSock.accept();
					establishClientConnection ec = new establishClientConnection(soc, this.PeerID, this.PeerID, this.pObj);
					Thread listenThread = new Thread(ec);
					
					listenThread.start();
					pObj.startedThreads.add(listenThread);
				}
			
			}
			catch (SocketException ex) {
				ex.printStackTrace();
				active = false;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			/*
			System.out.println("Thread ID:"+Thread.currentThread().getName());
			
			System.out.println("Con sock"+this.connectionSocket.toString());
			
			
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
			
			//Server bitfield is not empty.
			if (!serverBMsg.emptyBitField) {
				//Sending server bitfield.
				serverBMsg.SendBitFieldMsg(connectionSocket);			
				this.clientPeerBitFieldMsg = receivedClientBMsg;
			
			}
			else { //server bitfield is empty.
				System.out.println("Server: Skipping bitfield msg");
			}
			
			this.initialStage = true;
			
			HashSet<Integer> localReceivedByteIndex = new HashSet<Integer>();
			
			
			ServerMessageHandler m = new ServerMessageHandler();
			Object readObj;
			while (true) {
				//System.out.println("Server: Listening for messages");
				//while ((readObj = m.listenForMessages(connectionSocket, this)) == null);
				while ((readObj = m.listenForMessages(connectionSocket, this)) == null)
				{
					
				}
				//readObj = m.listenForMessages(connectionSocket, this.nm);
				int msgType = this.nm.MessageType;
				//System.out.println("MsgType:"+msgType);
				m.HandleMessages(msgType, readObj, this, localReceivedByteIndex);
				
				readObj = null;
				Thread.sleep(500);
				
			}	

		}
		
		catch (IOException ex) {
			System.out.println(ex);
		}	*/
	}


}
