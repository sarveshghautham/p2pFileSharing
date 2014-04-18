package p2pFileSharing;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class RequestMessage extends NormalMessages{

int msgByteIndex;
	
	public RequestMessage () {
		
	}
	
	public RequestMessage (int msgLen, int msgType, int msgByteIndex) {
		super (msgLen, msgType);
		this.msgByteIndex = msgByteIndex;
	}
	
	public void SendRequestMsg (Socket clientSocket) throws IOException {
		OutputStream os = clientSocket.getOutputStream();  
		ObjectOutputStream oos = new ObjectOutputStream(os);  			  
		oos.writeObject(this);
	}
	
	public int ReceiveRequestMsg (Socket soc) throws IOException {
		
		try {
			InputStream is = soc.getInputStream();  
			ObjectInputStream ois = new ObjectInputStream(is);  
			RequestMessage rm = (RequestMessage)ois.readObject(); 
			
			if (rm != null) {
				return rm.msgByteIndex;
			}
			else {
				return -1;
			}
		} 
		catch (ClassNotFoundException ex) {
			System.out.println(ex);
			return -1;
		}
		finally {
			//is.close();
			//ois.close();
		}
	}
	
	public int getPieceIndex(HashSet<Integer> neededByteIndex, HashSet<Integer> requestedByteIndex) {
		
		int pieceIndex = 0;
		
		int totSize = neededByteIndex.size();
		Random r = new Random();
		
		List<Integer> list = new ArrayList<Integer>(neededByteIndex);
		
		if (requestedByteIndex != null) {
			while (true) {
				pieceIndex = list.get(r.nextInt(totSize));
				if (!requestedByteIndex.contains(pieceIndex)) {
					break;
				}
			}
		}
		else {
			pieceIndex = list.get(r.nextInt(totSize));
		}
	
		return pieceIndex;
		
	}
}
