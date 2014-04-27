package p2pFileSharing;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class RequestMessage extends NormalMessages{

	private static final long serialVersionUID = 2487004078956739007L;
	int msgByteIndex;
	
	public RequestMessage () {
		
	}
	
	public RequestMessage (int msgLen, int msgType, int msgByteIndex) {
		super (msgLen, msgType);
		this.msgByteIndex = msgByteIndex;
	}
	
	public void SendRequestMsg (OutputStream os) throws IOException {

		synchronized (os) {
			ObjectOutputStream oos = new ObjectOutputStream(os);  			  
			oos.writeObject(this);
		}
		
	}
	
	public int ReceiveRequestMsg (InputStream is) throws IOException {
		
		try {
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
	
	public synchronized int getPieceIndex(HashSet<Integer> neededByteIndex) {
		
		int pieceIndex = -1;
		Random r = new Random();
		List<Integer> list = new ArrayList<Integer>(neededByteIndex);
		
		int totSize = neededByteIndex.size();
		
		/*
		
		if (requestedByteIndex != null) {
			while (true) {
				pieceIndex = list.get(r.nextInt(totSize));
				if (!requestedByteIndex.contains(pieceIndex)) {
					break;
				}
			}
		}
		else {
			*/
			pieceIndex = list.get(r.nextInt(totSize));
		//}
	
		return pieceIndex;
		
	}
}
