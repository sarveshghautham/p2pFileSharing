package p2pFileSharing;

import java.io.IOException;
import java.util.*;
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
	
	public void SendChokeMsg (Socket clientSocket) throws IOException {
		
		OutputStream os = clientSocket.getOutputStream();  
		ObjectOutputStream oos = new ObjectOutputStream(os);  			  
		oos.writeObject(this);
	}
	
	public void SendUnchokeMsg (Socket clientSocket) throws IOException {
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

	public HashSet<Integer> SelectPreferredNeighbors (HashSet<Integer> ListOfInterestedPeers, int K) {
	
		HashSet<Integer> PreferredNeighbors = new HashSet<Integer>();
		int totSize = ListOfInterestedPeers.size();
		Random r = new Random();

		for (int i = 0; i < K; i++) {
			
			List<Integer> list = new ArrayList<Integer>(ListOfInterestedPeers);
			int peerid = list.get(r.nextInt(totSize));
			
			PreferredNeighbors.add(peerid);
		}
		
		return PreferredNeighbors;
	}
	
	public int SelectOptNeighbors(HashSet<Integer> ListOfInterestedPeers, HashSet<Integer> PreferredNeighbors) {
		
		int i=0,j=0,optPeerId=0;
		
		ArrayList<Integer> list = new ArrayList<Integer>(ListOfInterestedPeers);
		ArrayList<Integer> list1 = new ArrayList<Integer>(PreferredNeighbors);
		ArrayList<Integer> list2 = new ArrayList<Integer>();
		
		Random r = new Random();
		
		for (i = 0, j = 0; i < list.size() || j < list1.size(); i++, j++) {
			
			if (list.get(i) != list1.get(j)) {
				list2.add(list.get(i));
			}
		}
		
		optPeerId = list.get(r.nextInt(list2.size()));
				
		return optPeerId;	
	}
	
	public ArrayList<Integer> prepareChokeList (HashSet<Integer> PreferredNeighbors, HashSet<Integer> OldPreferredNeighbors) {
		
		ArrayList<Integer> list1 = new ArrayList<Integer>(OldPreferredNeighbors);
		ArrayList<Integer> list2 = new ArrayList<Integer>(PreferredNeighbors);
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
