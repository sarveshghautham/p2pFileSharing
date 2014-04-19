package p2pFileSharing;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

public class HaveMessage extends NormalMessages {
	
	int msgByteIndex;
	
	public HaveMessage () {
		
	}
	
	public HaveMessage (int msgLen, int msgType, int msgByteIndex) {
		super (msgLen, msgType);
		this.msgByteIndex = msgByteIndex;
	}
	
	public void SendHaveMsg (Socket clientSocket) throws IOException {
		OutputStream os = clientSocket.getOutputStream();  
		ObjectOutputStream oos = new ObjectOutputStream(os);  			  
		oos.writeObject(this);
	}
	
	public int ReceiveHaveMsg (Socket soc) throws IOException {
		
		try {
			InputStream is = soc.getInputStream();  
			ObjectInputStream ois = new ObjectInputStream(is);  
			HaveMessage hm = (HaveMessage)ois.readObject(); 
			
			if (hm != null) {
				return hm.msgByteIndex;
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

	public ArrayList<Integer> prepareHaveList (HashSet<Integer> globalHashSet, HashSet<Integer> localHashSet) {
		
			
		ArrayList<Integer> list1 = new ArrayList<Integer>(globalHashSet);
		ArrayList<Integer> list2 = new ArrayList<Integer>(localHashSet);
		ArrayList<Integer> list3 = new ArrayList<Integer>();
		int i=0, j=0;
		
		for (i = 0; i < list1.size(); i++) {
			for (j = 0; j < list2.size(); j++) {
				if ((list1.get(i) != list2.get(j)) && j == (list2.size() - 1) ){
					list3.add(list1.get(i));
				}
			}
		}
		
		return list3;			
	}
}
