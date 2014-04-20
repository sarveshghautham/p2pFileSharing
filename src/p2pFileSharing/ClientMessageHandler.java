package p2pFileSharing;

import java.net.*;
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
	
	public Object listenForMessages (Socket soc, establishClientConnection ec) throws IOException {
		
		try {
			InputStream is = soc.getInputStream();  
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
	
	public void HandleMessages (int MsgType, Object obj, establishClientConnection ec) throws IOException {
		
		switch (MsgType) {
		
		case UNCHOKE:
			
			RequestMessage rm = new RequestMessage();
			int pieceIndex = rm.getPieceIndex(ec.pObj.neededByteIndex, ec.pObj.requestedByteIndex);
			RequestMessage rm1 = new RequestMessage(4, REQUEST, pieceIndex);
			rm1.SendRequestMsg(ec.clientSocket);
			
			
			break;
		
		case CHOKE:
			break;
		
		case INTERESTED:
			break;
			
		case NOTINTERESTED:
			
			break;
		
		case HAVE:			
				HaveMessage rxHvMsg = new HaveMessage();
				int byteIndex = rxHvMsg.ReceiveHaveMsg(ec.clientSocket); 
				if (byteIndex != -1) {
					ec.serverPeerBitFieldMsg.UpdateBitFieldMsg(byteIndex);
					if (ec.myBitFields.bitFieldMsg[byteIndex] == true) {
						InterestedMessage im = new InterestedMessage(0,INTERESTED, ec.myPeerID);
						im.SendInterestedMsg(ec.clientSocket);
					}
					else {
						NotInterestedMessage ntm = new NotInterestedMessage(0, NOTINTERESTED, ec.myPeerID);
						ntm.SendNotInterestedMsg(ec.clientSocket);
					}					
				}
				else {
					System.out.println("Error in receiving have msg");
				}
			
			break;
		
		case BITFIELD:
			break;
			
		case REQUEST:
			break;
			
		case PIECE:
			PieceMessage pm = (PieceMessage)obj;
			FileHandler f = new FileHandler();
			f.writePiece(pm.Filepiece, pm.msgByteIndex);
			ec.pObj.myBitFields.UpdateBitFieldMsg(pm.msgByteIndex);
			ec.pObj.neededByteIndex.remove(pm.msgByteIndex);
			ec.pObj.receivedByteIndex.add(pm.msgByteIndex);
			
			if (!ec.pObj.neededByteIndex.isEmpty()) {
				
				RequestMessage rm2 = new RequestMessage();
				pieceIndex = rm2.getPieceIndex(ec.pObj.neededByteIndex, ec.pObj.requestedByteIndex);
				RequestMessage rm3 = new RequestMessage(4, REQUEST, pieceIndex);
				rm3.SendRequestMsg(ec.clientSocket);
				
			}
			
			
			break;
			
		default: 
			System.exit(0);
		
		}
		
	}
	
}