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
	
	public void intializedBitFieldMsg (int myPeerId, PeerProcess pp) throws IOException{
		FileHandler fileHdlr = new FileHandler();
		fileHdlr.ReadCommonConfigFile();
		this.bitFieldMsg = new boolean[fileHdlr.pieceCount];
		
		if (fileHdlr.CheckHasFile(myPeerId)){
			Arrays.fill(this.bitFieldMsg, true);
			fileHdlr.SplitFile(fileHdlr.inputFileName, fileHdlr.fileSize, fileHdlr.pieceSize, myPeerId);
			this.emptyBitField = false;
		}
		else {
			Arrays.fill(this.bitFieldMsg, false);
			this.emptyBitField = true;
			
			if (pp.neededByteIndex.size() == 0) {
				for (int i = 0; i < fileHdlr.pieceCount; i++) {
					pp.neededByteIndex.add(i);
				}
			}
		}
	}
	
	public synchronized void SendBitFieldMsg (OutputStream os) throws IOException {
		ObjectOutputStream oos;
		
		if(this.bitFieldMsg !=null){
			synchronized (os) {
				oos = new ObjectOutputStream(os);  			  
				oos.writeObject(this);	
			}			
		}
	}
	
	public BitFields ReceiveBitFieldMsg (InputStream is ) throws IOException {
		
		System.out.println("in receive bit field msg");
		
		try {
			
			ObjectInputStream ois = new ObjectInputStream(is);
			Object obj = ois.readObject();
			
			if (obj instanceof HaveMessage) {
				return null;
			}
			else {
			
				BitFields RespMsg = (BitFields)obj;  
				System.out.println("Bitfield msg type:"+RespMsg.MessageType);
				if (RespMsg.bitFieldMsg != null) {
					System.out.println("ReceiveBitFieldMsg(): BitField message received");
					return RespMsg;
				}
				else {
					System.out.println("ReceiveBitFieldMsg(): BitField message not received");
					return null;
				}
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
	
	public synchronized HashSet<Integer> AnalyzeReceivedBitFieldMsg (BitFields receivedBit) {
		
		boolean[] bMsg2 = receivedBit.bitFieldMsg;
		boolean[] bMsg1 = this.bitFieldMsg;
		
		HashSet<Integer> indexList = new HashSet<Integer>();
		
		System.out.println("sizes = " + bMsg1.length + " " + bMsg2.length);
		for (int i = 0; i < bMsg2.length; i++) {
			System.out.print(bMsg2[i] + "      " + bMsg1[i]);
		}
		
		System.out.println();
		//System.out.println("Msg length:"+bMsg1.length);
		
		for (int i=0; i < bMsg1.length; i++) {
			if (bMsg1[i] != bMsg2[i] && bMsg1[i] == false) {
				indexList.add(i);
			}
		}
		
		return indexList;
	}
	
	public synchronized void UpdateBitFieldMsg (int index) {
		
		this.bitFieldMsg[index] = true;
		
	}
	
	public synchronized boolean contains (int index) {
		
		if (this.bitFieldMsg[index] == true) {
			return false;
		}
		else {
			return true;
		}
	}
}
