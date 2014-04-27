package p2pFileSharing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

class establishClientConnection implements Runnable{
	
	//int peerCount;
	int peerID;
	int clientPeerID;
	int portNumber;
	String hostName;
	Socket clientSocket;
	Hashtable<Integer, establishClientConnection> esclientmap;
	BitFields serverPeerBitFieldMsg;
	BitFields myBitFields;
	PeerProcess pObj;
	Boolean finished = false;
	Boolean initialStage = false;
	NormalMessages nm = new NormalMessages();	
	OutputStream out;
    InputStream in;
	
    public establishClientConnection() {
    	
    }
    
    public establishClientConnection (Socket socket, int mypeer_id, int peer_id, PeerProcess pp) throws IOException {
		this.peerID = mypeer_id; //Server's peer ID.
		this.clientPeerID = peer_id; //Client's peer ID.
		//this.hostName = peer_address;
		//this.portNumber = Integer.parseInt(peer_port);
		this.myBitFields = pp.myBitFields;		
		this.pObj = pp;
		this.clientSocket = socket;
		
		this.out = socket.getOutputStream();
		this.in = socket.getInputStream();
	}
	
//	synchronized (esclientmap){
//		
//		if (!esclientmap.containsKey(clientPeerID))  {
//			esclientmap.put(clientPeerID, this);
//			//peerProcess.log ("Peer " +  mypid + " is connected from peer " + peerid);
//		    } 
//		
//	}
	
	public void run() {
		
		try {
			this.finished = false;
			//Creating multiple client sockets for peers already started.
			//System.out.println("Trying to establish a connection with: "+this.hostName+" using port number "+this.portNumber);
			//Socket ClientSocket = new Socket(this.hostName, this.portNumber);
			//this.clientSocket = ClientSocket;
			//System.out.println("Before handshake: PeerID: "+peerID);
			//Send handshake message
			
			
			HandShakeMessage HMsg = new HandShakeMessage("HELLO", peerID);
			HMsg.SendHandShakeMessage (this.out);			
			
			//Wait till client receives a handshake from server.
			
			if ((this.clientPeerID = HMsg.ReceiveHandShakeMessage(this.in)) != -1){
				System.out.println("Handshake sucacess ");
				
				if (!PeerProcess.esclientmap.contains(this.clientPeerID)) {
					System.out.println("Server side: Putting in map " + clientPeerID);
					PeerProcess.esclientmap.put(this.clientPeerID, this);
				}
				
			}
			else {
				System.out.println("Handshake Failed");
			}
			
			
			//Now send a bitfield message.
			//BitFields clientBMsg = new BitFields(4, 5);
			//clientBMsg.intializedBitFieldMsg(clientPeerID, this.pObj);
			
			myBitFields.SendBitFieldMsg(this.out);	
			
			//Now reveive a bitfield message from server.
			BitFields receiveBMsg = new BitFields();
			BitFields returnBMsg;
			
			returnBMsg = receiveBMsg.ReceiveBitFieldMsg(this.in); 
			
			this.serverPeerBitFieldMsg = returnBMsg;
			//if ( (myBitFields.AnalyzeReceivedBitFieldMsg(returnBMsg)) != null) {
			
			HashSet<Integer> res = myBitFields.AnalyzeReceivedBitFieldMsg(returnBMsg);
			
			if (res.size() != 0) {
				//send interested msg.
				InterestedMessage nIMsg = new InterestedMessage(0,2, clientPeerID);
				nIMsg.SendInterestedMsg(this.out);
		
			}
			else {
				
				//send not interested msg.
				NotInterestedMessage nIMsg = new NotInterestedMessage(0,3, clientPeerID, false);
				nIMsg.SendNotInterestedMsg(this.out);
				
			}
		
			//this.initialStage = true;
			
			ClientMessageHandler cm = new ClientMessageHandler();
			Object readObj = null;
			HashSet<Integer> localReceivedByteIndex = new HashSet<Integer>();
			
			while (true) {
				System.out.println("Client: Listening for messages");
				readObj = cm.listenForMessages(this.in, this);
				System.out.println("Got message");
				int msgType = this.nm.MessageType;
//				System.out.println("Msg type:"+msgType);
				cm.HandleMessages(msgType, readObj, this, localReceivedByteIndex);
				readObj = null;
				
				//Thread.sleep(1000);
				
				if (this.finished == true) {
					NotInterestedMessage ntIm = new NotInterestedMessage(0, 3, clientPeerID, true);
					ntIm.SendNotInterestedMsg(this.out);
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

//	@Override
//	public void updateHashAndSendHaveMsg(int msgByteIndex) throws IOException {
//		// TODO Auto-generated method stub
//		
//		if (this.initialStage == true) {
//			System.out.println("In client: update hash");
//			this.pObj.receivedByteIndex.add(msgByteIndex);
//			Iterator<establishServerConnection> it = this.pObj.es.iterator();
//			
//			while (it.hasNext()) {
//				it.next().updateHashAndSendHaveMsg(msgByteIndex);
//			}
//		}
//	}	
}