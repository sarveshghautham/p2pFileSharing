package p2pFileSharing;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class PieceMessage extends NormalMessages{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5319989900688357862L;
	int msgByteIndex;
	ArrayList<Integer> Filepiece;
	
	public PieceMessage () {
		
	}
	
	public PieceMessage (int msgLen, int msgType, int msgByteIndex, ArrayList<Integer> filePiece) {
		super (msgLen, msgType);
		this.msgByteIndex = msgByteIndex;
		this.Filepiece = filePiece;
	}
	
	public void SendPieceMsg (Socket clientSocket) throws IOException {
		OutputStream os = clientSocket.getOutputStream();  
		ObjectOutputStream oos = new ObjectOutputStream(os);  			  
		oos.writeObject(this);
	}
	
	public int ReceivePieceMsg (Socket soc) throws IOException {
		
		try {
			InputStream is = soc.getInputStream();  
			ObjectInputStream ois = new ObjectInputStream(is);  
			PieceMessage pm = (PieceMessage)ois.readObject(); 
			
			if (pm != null) {
				return pm.msgByteIndex;
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

}
