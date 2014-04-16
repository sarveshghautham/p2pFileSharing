package p2pFileSharing;

public class NormalMessages {

	int MessageLength;
	int MessageType;
	
	public NormalMessages () {
		
	}
	
	public NormalMessages (int MsgLen,int MsgType) {
		this.MessageLength = MsgLen;
		this.MessageType = MsgType;
	}	
}