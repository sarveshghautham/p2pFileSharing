package p2pFileSharing;
import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.net.*;

class BitFields extends NormalMessages implements Serializable{

	private static final long serialVersionUID = 7863262235394607247L;
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
		
		if(this.bitFieldMsg !=null){
		OutputStream os = clientSocket.getOutputStream();  
		ObjectOutputStream oos = new ObjectOutputStream(os);  			  
		oos.writeObject(this);
		}
	}
	
	public BitFields ReceiveBitFieldMsg (Socket soc) throws IOException {
		
		try {
			InputStream is = soc.getInputStream();  
			ObjectInputStream ois = new ObjectInputStream(is);  
			BitFields RespMsg = (BitFields)ois.readObject();  
			
			if (RespMsg.bitFieldMsg != null) {
				System.out.println("ReceiveBitFieldMsg(): BitField message received");
				return RespMsg;
			}
			else {
				System.out.println("ReceiveBitFieldMsg(): BitField message not received");
				return null;
			}
		} 
		catch (ClassNotFoundException ex) {
			System.out.println(ex);
			return null;
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
		
		
		System.out.println("Msg length:"+bMsg1.length);
		
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
