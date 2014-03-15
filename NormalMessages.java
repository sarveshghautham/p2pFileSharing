package p2pFileSharing;

public class NormalMessages {

	int MessageLength;
	byte MessageType;
	String MessagePayload;
	
	public NormalMessages (int MsgLen,byte MsgType, String MsgPayLoad) {
		MessageLength = MsgLen;
		MessageType = MsgType;
		MessagePayload = MsgPayLoad;
	}	
}