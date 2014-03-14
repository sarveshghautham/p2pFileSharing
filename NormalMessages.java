package p2pFileSharing;
import java.util.*;

public class NormalMessages {

	int MessageLength;
	byte MessageType;
	Vector<String> MessagePayload;
	
	public NormalMessages (int MsgLen,byte MsgType, Vector<String> MsgPayLoad) {
		MessageLength = MsgLen;
		MessageType = MsgType;
		MessagePayload = MsgPayLoad;
	}	
}