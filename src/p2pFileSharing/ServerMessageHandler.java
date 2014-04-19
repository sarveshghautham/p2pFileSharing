package p2pFileSharing;

import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.io.*;


public class ServerMessageHandler {

	public final int CHOKE = 0;
	public final int UNCHOKE = 1;
	public final int INTERESTED = 2;
	public final int NOTINTERESTED = 3;
	public final int HAVE = 4;
	public final int BITFIELD = 5;
	public final int REQUEST = 6;
	public final int PIECE = 7;
	
	public ServerMessageHandler () {}
	
	public Object listenForMessages (Socket soc, NormalMessages nm) throws IOException {
		
		try {
			InputStream is = soc.getInputStream();  
			ObjectInputStream ois = new ObjectInputStream(is);  
			nm = (NormalMessages)ois.readObject();
			
			if (ois.readObject() != null) {
				return ois.readObject();
			}
			
		}
		catch (ClassNotFoundException ex) {
			System.out.println(ex);
		}
		
		return null;
		
	}
	
	public void HandleMessages (int MsgType, Object obj, establishServerConnection es, HashSet<Integer> localReceivedByteIndex) throws IOException {
		
		switch (MsgType) {
		
		case UNCHOKE:
			break;
		
		case CHOKE:
			break;
		
		case INTERESTED:
			InterestedMessage fromClientIntMsg = (InterestedMessage)obj;
			es.cPeerID = fromClientIntMsg.clientPeerID;
							
			es.interested = true;
			es.pObj.ListofInterestedPeers.add(es.cPeerID);
			
			while ( !(es.pObj.PreferredNeighbors.contains(es.cPeerID)) || (es.pObj.optPeerID != es.cPeerID) );
			ChokeUnchokeMessage c = new ChokeUnchokeMessage(0, UNCHOKE);
			c.SendUnchokeMsg(es.connectionSocket);				
						
			break;
			
		case NOTINTERESTED:
			
			NotInterestedMessage ntIm = (NotInterestedMessage)obj;
			es.cPeerID = ntIm.clientPeerID;
			es.notInterested = true;
			
			//TODO: Wait for change in server's bitField message.
			//Synchronization.
			//Send have message.
			break;
		
		case HAVE:			
			break;
		
		case BITFIELD:
			break;
			
		case REQUEST:
			//Receive the request message.
			RequestMessage rm = new RequestMessage();
			int pieceIndex = rm.ReceiveRequestMsg(es.connectionSocket);
			
			if (pieceIndex != -1) {
				//Send piece message.
				FileHandler f = new FileHandler();
				int filePiece = f.readPiece(pieceIndex);
				
				
				if ((es.pObj.PreferredNeighbors.contains(es.cPeerID)) || (es.pObj.optPeerID == es.cPeerID)) {
					
					//TODO: if (have == false)
					localReceivedByteIndex = es.pObj.receivedByteIndex;
					
					HaveMessage hm = new HaveMessage();
					ArrayList<Integer> haveList = hm.prepareHaveList(es.pObj.receivedByteIndex, localReceivedByteIndex);
					for (int i = 0; i < haveList.size(); i++) {
						HaveMessage hmsg = new HaveMessage(4, HAVE, haveList.get(i));
						hmsg.SendHaveMsg(es.connectionSocket);
						localReceivedByteIndex.add(haveList.get(i));
					}
					
					//Send piece msg.
					PieceMessage pm = new PieceMessage(4, PIECE, pieceIndex, filePiece);
					pm.SendPieceMsg(es.connectionSocket);
				}
				else {
					//send choke message.
					ChokeUnchokeMessage cm = new ChokeUnchokeMessage(0, CHOKE);
					cm.SendChokeMsg(es.connectionSocket);
				}					
			}
			
			break;
			
		case PIECE:
			break;
			
		default: 
			System.exit(0);
		
		}
		
	}
	
}