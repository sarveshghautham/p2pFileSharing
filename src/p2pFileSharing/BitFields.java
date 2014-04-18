package p2pFileSharing;
import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.net.*;

class BitFields extends NormalMessages{

	boolean emptyBitField;
	boolean []bitFieldMsg;
	
	public BitFields () {
		super();
	}
	
	public BitFields(int msgLen, int msgType) {
		super(msgLen, msgType);
	}
	
	public void intializedBitFieldMsg (int myPeerId) throws IOException{
		FileHandler fileHdlr = new FileHandler();
		fileHdlr.ReadCommonConfigFile();
		this.bitFieldMsg = new boolean[fileHdlr.pieceCount];
		
		if (fileHdlr.CheckHasFile(myPeerId)){
			Arrays.fill(this.bitFieldMsg, true);
			this.emptyBitField = false;
		}
		else {
			Arrays.fill(this.bitFieldMsg, false);
			this.emptyBitField = true;
		}
	}
	
	public void SendBitFieldMsg (Socket clientSocket) throws IOException {
		OutputStream os = clientSocket.getOutputStream();  
		ObjectOutputStream oos = new ObjectOutputStream(os);  			  
		oos.writeObject(this);
	}
	
	public boolean ReceiveBitFieldMsg (Socket soc) throws IOException {
		
		try {
			InputStream is = soc.getInputStream();  
			ObjectInputStream ois = new ObjectInputStream(is);  
			BitFields RespMsg = (BitFields)ois.readObject();  
			
			if (RespMsg != null) {
				return true;
			}
			else {
				System.out.println("ReceiveBitFieldMsg(): BitField message not received");
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
	
	public HashSet<Integer> AnalyzeReceivedBitFieldMsg (BitFields receivedBit) {
		
		boolean[] bMsg1 = receivedBit.bitFieldMsg;
		boolean[] bMsg2 = this.bitFieldMsg;
		
		HashSet<Integer> indexList = new HashSet<Integer>();
		
		for (int i=0; i < bMsg1.length; i++) {
			if (bMsg1[i] != bMsg2[i] && bMsg2[i] == false) {
				indexList.add(i);
			}
		}
		
		return indexList;
	}
	
	public void UpdateBitFieldMsg (int index) {
		
		this.bitFieldMsg[index] = true;
		
	}
}
