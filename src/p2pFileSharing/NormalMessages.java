package p2pFileSharing;
import java.io.Serializable;

public class NormalMessages implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8361597158056482861L;
	int MessageLength;
	int MessageType;
	
	public NormalMessages () {
		
	}
	
	public NormalMessages (int MsgLen,int MsgType) {
		this.MessageLength = MsgLen;
		this.MessageType = MsgType;
	}	
}