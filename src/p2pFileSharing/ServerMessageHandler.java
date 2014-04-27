package p2pFileSharing;

import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.io.*;


public class ServerMessageHandler implements Serializable {

	private static final long serialVersionUID = -8270502409155375127L;
	// private static final long serialVersionUID = -174333938837408245L;
	public final int CHOKE = 0;
	public final int UNCHOKE = 1;
	public final int INTERESTED = 2;
	public final int NOTINTERESTED = 3;
	public final int HAVE = 4;
	public final int BITFIELD = 5;
	public final int REQUEST = 6;
	public final int PIECE = 7;
	
	public ServerMessageHandler () {}
	
	public Object listenForMessages (Socket soc, establishServerConnection es) throws IOException {
		
		try {
			InputStream is = soc.getInputStream();  
			ObjectInputStream ois = new ObjectInputStream(is);
			
			Object obj = ois.readObject();
			es.nm = (NormalMessages)obj;
			
			return obj;
			
			
		}
		catch (ClassNotFoundException ex) {
			System.out.println(ex);
		}
		
		return null;
		
	}
	
	public void HandleMessages (int MsgType, Object obj, establishServerConnection es, HashSet<Integer> localReceivedByteIndex) throws IOException {
		
		System.out.println("Handle message:"+MsgType);
		
		switch (MsgType) {
		
		case UNCHOKE:
			break;
		
		case CHOKE:
			break;
		
		case INTERESTED:
			InterestedMessage fromClientIntMsg = (InterestedMessage)obj;
			es.cPeerID = fromClientIntMsg.clientPeerID;
							
			es.interested = true;
			es.pObj.log.receivedInterested(es.cPeerID);
			es.pObj.ListofInterestedPeers.add(es.cPeerID);			
			
			while ( !(es.pObj.PreferredNeighbors.contains(es.cPeerID)) || (es.pObj.optPeerID != es.cPeerID) );
			ChokeUnchokeMessage c = new ChokeUnchokeMessage(0, UNCHOKE);
			//c.SendUnchokeMsg(es.connectionSocket);	
			es.pObj.log.Unchoked(es.cPeerID);
						
			break;
			
		case NOTINTERESTED:
			
			NotInterestedMessage ntIm = (NotInterestedMessage)obj;
			es.cPeerID = ntIm.clientPeerID;
			es.notInterested = true;
			es.pObj.log.receivedNotInterested(es.cPeerID);
			
			if (ntIm.finished == true && es.pObj.ListofInterestedPeers.contains(es.cPeerID)) {
				es.pObj.ListofInterestedPeers.remove(es.cPeerID);
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/*
			boolean test = false;
			while (test==false){
				if(!es.pObj.receivedByteIndex.isEmpty()){
					test = true;
					ArrayList<Integer> list = new ArrayList<Integer>(es.pObj.receivedByteIndex);
					int testpiece = list.get(list.size()-1);
					//HaveMessage hm = new HaveMessage();
					System.out.println("Sending have to: "+es.cPeerID);
					HaveMessage hmsg = new HaveMessage(4, 4, testpiece);
					hmsg.SendHaveMsg(es.connectionSocket);
					
				}
			}
			*/
			break;
		
		case HAVE:			
			break;
		
		case BITFIELD:
			break;
			
		case REQUEST:
			//Receive the request message.
			RequestMessage rm = (RequestMessage)obj;
			int pieceIndex = rm.msgByteIndex;
			
			if (pieceIndex != -1) {
				//Send piece message.
				FileHandler f = new FileHandler();
				ArrayList<Integer> filePiece = f.readPiece(pieceIndex, es.PeerID);
				
				if ((es.pObj.PreferredNeighbors.contains(es.cPeerID)) || (es.pObj.optPeerID == es.cPeerID)) {
					System.out.println("PRESENT inside ||");
					//TODO: if (have == false)
					localReceivedByteIndex = es.pObj.receivedByteIndex;
					
					HaveMessage hm = new HaveMessage();
					ArrayList<Integer> haveList = hm.prepareHaveList(es.pObj.receivedByteIndex, localReceivedByteIndex);
					for (int i = 0; i < haveList.size(); i++) {
						HaveMessage hmsg = new HaveMessage(4, HAVE, haveList.get(i));
						//hmsg.SendHaveMsg(es.connectionSocket);
						localReceivedByteIndex.add(haveList.get(i));
					}
					
					//Send piece msg.
					PieceMessage pm = new PieceMessage(4, PIECE, pieceIndex, filePiece);
					//pm.SendPieceMsg(es.connectionSocket);
					System.out.println("Sent piece:"+pieceIndex);
					System.out.println("end of piece msg transfer");
				}
				else {
					//send choke message.
					ChokeUnchokeMessage cm = new ChokeUnchokeMessage(0, CHOKE);
					//cm.SendChokeMsg(es.connectionSocket);
					es.pObj.log.Choked(es.cPeerID);
					
					while ( !(es.pObj.PreferredNeighbors.contains(es.cPeerID)) || (es.pObj.optPeerID != es.cPeerID) );
					ChokeUnchokeMessage c1 = new ChokeUnchokeMessage(0, UNCHOKE);
					//c1.SendUnchokeMsg(es.connectionSocket);
					es.pObj.log.Unchoked(es.cPeerID);
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