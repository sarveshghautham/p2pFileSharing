package p2pFileSharing;

import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.io.*;


public class ClientMessageHandler implements Serializable {

	private static final long serialVersionUID = -6801972033167530864L;
	public final int CHOKE = 0;
	public final int UNCHOKE = 1;
	public final int INTERESTED = 2;
	public final int NOTINTERESTED = 3;
	public final int HAVE = 4;
	public final int BITFIELD = 5;
	public final int REQUEST = 6;
	public final int PIECE = 7;
	
	public ClientMessageHandler () {}
	
	public Object listenForMessages (InputStream is, establishClientConnection ec) throws IOException {
		
		try {
			ObjectInputStream ois = new ObjectInputStream(is);
			Object obj = ois.readObject();
			ec.nm = (NormalMessages)obj;
			return obj;
		}
		catch (ClassNotFoundException ex) {
			System.out.println(ex);
		}
		
		return null;
		
	}
	
	public void HandleMessages (int MsgType, Object obj, establishClientConnection ec, HashSet<Integer> localReceivedByteIndex) throws IOException {
		
		System.out.println("Handle message: "+MsgType);
		
		switch (MsgType) {
		
		case UNCHOKE:
			
			RequestMessage rm = new RequestMessage();
			int pieceIndex=0;
			synchronized (ec.pObj.neededByteIndex) {
				
				pieceIndex = rm.getPieceIndex(ec.pObj.neededByteIndex);
			}
			System.out.println("Sending request for " + pieceIndex);
			RequestMessage rm1 = new RequestMessage(4, REQUEST, pieceIndex);
			rm1.SendRequestMsg(ec.out);
			
			break;
		
		case CHOKE:
			break;
		
		case INTERESTED:
			
			InterestedMessage fromClientIntMsg = (InterestedMessage)obj;
			//ec.clientPeerID = fromClientIntMsg.clientPeerID;
							
			//ec.interested = true;
			ec.pObj.log.receivedInterested(ec.clientPeerID);
			
			synchronized (ec.pObj.ListofInterestedPeers) {
				System.out.println("Adding to ilist " + ec.clientPeerID);
				ec.pObj.ListofInterestedPeers.add(ec.clientPeerID);
			}
						
			
			break;
			
		case NOTINTERESTED:
			
			NotInterestedMessage ntIm = (NotInterestedMessage)obj;
			ec.clientPeerID = ntIm.clientPeerID;
			//ec.notInterested = true;
			ec.pObj.log.receivedNotInterested(ec.clientPeerID);
			
			if (ntIm.finished == true && ec.pObj.ListofInterestedPeers.contains(ec.clientPeerID)) {
				ec.pObj.ListofInterestedPeers.remove(ec.clientPeerID);
			}
			
			break;
		
		case HAVE:			
				HaveMessage rxHvMsg = new HaveMessage();
				//int byteIndex = rxHvMsg.ReceiveHaveMsg(ec.in);
				rxHvMsg = (HaveMessage)obj;
				int byteIndex = rxHvMsg.msgByteIndex;
				if (byteIndex != -1) {
					//System.out.println("Received a have message from:"+ec.peerID);
					ec.pObj.log.receivedHave(ec.peerID, byteIndex);
					//System.out.println("and byteIndex:"+byteIndex);
					ec.serverPeerBitFieldMsg.UpdateBitFieldMsg(byteIndex);
					if (ec.myBitFields.bitFieldMsg[byteIndex] == false) {
						InterestedMessage im = new InterestedMessage(0,INTERESTED, ec.clientPeerID);
						im.SendInterestedMsg(ec.out);
					}
					else {
						NotInterestedMessage ntm = new NotInterestedMessage(0, NOTINTERESTED, ec.clientPeerID, false);
						ntm.SendNotInterestedMsg(ec.out);
					}					
				}
				else {
					System.out.println("Error in receiving have msg");
				}
			
			break;
		
		case BITFIELD:
			break;
			
		case REQUEST:
			//Get request and send piece
			RequestMessage reqMsg = (RequestMessage)obj;
			int pieceIndex1 = reqMsg.msgByteIndex;
			
			System.out.println("Got piece req for:"+pieceIndex1);
			
			if (pieceIndex1 != -1) {
				//Send piece message.
				FileHandler f = new FileHandler();
				ArrayList<Integer> filePiece = f.readPiece(pieceIndex1, ec.peerID);
				
				boolean check = false;
				
				synchronized (ec.pObj.PreferredNeighbors) {
					//check = ec.pObj.PreferredNeighbors.contains(ec.peerID);	
				}
				check = true;
				if ( check == true || (ec.pObj.optPeerID == ec.clientPeerID)) {
					
					//localReceivedByteIndex = ec.pObj.receivedByteIndex;
					
					/*
					HaveMessage hm = new HaveMessage();
					ArrayList<Integer> haveList = hm.prepareHaveList(ec.pObj.receivedByteIndex, localReceivedByteIndex);
					for (int i = 0; i < haveList.size(); i++) {
						HaveMessage hmsg = new HaveMessage(4, HAVE, haveList.get(i));
						hmsg.SendHaveMsg(ec.out);
						localReceivedByteIndex.add(haveList.get(i));
					}*/
					
					//Send piece msg.
					PieceMessage pm = new PieceMessage(4, PIECE, pieceIndex1, filePiece);
					pm.SendPieceMsg(ec.out);
					System.out.println("Sent piece:"+pieceIndex1);
					System.out.println("end of piece msg transfer");
				}
				else {
					//send choke message.
					//ChokeUnchokeMessage cm = new ChokeUnchokeMessage(0, CHOKE);
					//cm.SendChokeMsg(ec.out);
				//	ec.pObj.log.Choked(ec.clientPeerID);
					
					//ec.pObj.log.Unchoked(ec.clientPeerID);
				}					
			}
			
			break;
			
		case PIECE:
			PieceMessage pm = (PieceMessage)obj;
			FileHandler f = new FileHandler();
			f.writePiece(pm.Filepiece, pm.msgByteIndex, ec.clientPeerID);
			
			System.out.println("Got piece with piece index:"+pm.msgByteIndex);
			
			synchronized (ec.pObj.myBitFields) {
				if (!ec.pObj.myBitFields.contains(pm.msgByteIndex)) {
					ec.pObj.myBitFields.UpdateBitFieldMsg(pm.msgByteIndex);
				}
			}
			
			synchronized (ec.pObj.neededByteIndex) {
				ec.pObj.neededByteIndex.remove(pm.msgByteIndex);	
			}
			
			//ec.pObj.receivedByteIndex.add(pm.msgByteIndex);
			//ec.updateHashAndSendHaveMsg(pm.msgByteIndex);
			
			synchronized (ec.pObj.haveList) {
				ec.pObj.haveList.add(pm.msgByteIndex);	
			}
			
			int pieceIndex2=-1;
			synchronized (ec.pObj.neededByteIndex) {
				synchronized (ec.pObj.receivedByteIndex) {
					if (!ec.pObj.neededByteIndex.isEmpty()) {
						//System.out.println("neededbyteindex size"+ec.pObj.neededByteIndex.size());
						//System.out.println();
						
						RequestMessage rm2 = new RequestMessage();
						synchronized (ec.pObj.neededByteIndex) {
							pieceIndex2 = rm2.getPieceIndex(ec.pObj.neededByteIndex);
						}
						
						
						System.out.println("Sending req for: "+pieceIndex2);
						ec.pObj.log.downloadedPiece(ec.peerID, pieceIndex2, ec.pObj.receivedByteIndex.size());
						RequestMessage rm3 = new RequestMessage(4, REQUEST, pieceIndex2);
						rm3.SendRequestMsg(ec.out);
						
						System.out.println("Sent req with pieceIndex: "+pieceIndex2);
						synchronized (ec.pObj.esclientmap) {
							
							for (Integer key: ec.pObj.esclientmap.keySet()) {
								System.out.println("Sending have message to:"+key); 
								establishClientConnection tempEC = ec.pObj.esclientmap.get(key);
								HaveMessage hm = new HaveMessage(4, HAVE, pm.msgByteIndex);
								hm.SendHaveMsg(tempEC.out);
							}
						}
				
					}
					else {
						//Terminate once the client has received all the pieces.
						FileHandler f1 = new FileHandler();
						f1.ReadCommonConfigFile();
						f1.JoinFile(f1.inputFileName, f1.fileSize, f1.pieceSize, f1.pieceCount, ec.clientPeerID);
						ec.pObj.ListofInterestedPeers.remove(ec.clientPeerID);
						//send not interested message
						ec.pObj.log.completedDownload();
						ec.finished = true;
					}
				}
				
			}
			break;
			
		default: 
			System.exit(0);
		
		}
		
	}
	
}
