package p2pFileSharing;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ChokeUnchokeMessage extends NormalMessages{
	
	public ChokeUnchokeMessage () {
		
	}
	
	public ChokeUnchokeMessage (int MsgLen, int MsgType) {
		super(MsgLen, MsgType);
	}
	
	public void SendNotChokeUnchokeMsg (Socket clientSocket) throws IOException {
		OutputStream os = clientSocket.getOutputStream();  
		ObjectOutputStream oos = new ObjectOutputStream(os);  			  
		oos.writeObject(this);
	}
	
	public boolean ReceiveChokeUnchokeMsg (Socket soc) throws IOException {
		
		try {
			InputStream is = soc.getInputStream();  
			ObjectInputStream ois = new ObjectInputStream(is);  
			ChokeUnchokeMessage cm = (ChokeUnchokeMessage)ois.readObject(); 
			
			if (cm != null) {
				return true;
			}
			else {
				return false;
			}
		} 
		catch (ClassNotFoundException ex) {
			System.out.println(ex);
			return false;
		}
		finally {
			//is.close();
			//ois.close();
		}
	}

}
