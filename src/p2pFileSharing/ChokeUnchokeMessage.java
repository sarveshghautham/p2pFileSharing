package p2pFileSharing;

import java.io.IOException;
import java.util.*;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ChokeUnchokeMessage extends NormalMessages{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8718666531723398642L;

	public ChokeUnchokeMessage () {
		
	}
	
	public ChokeUnchokeMessage (int MsgLen, int MsgType) {
		super(MsgLen, MsgType);
	}
	
	public void SendChokeMsg (OutputStream os) throws IOException {
		
		synchronized (os) {
			ObjectOutputStream oos = new ObjectOutputStream(os);  			  
			oos.writeObject(this);
		} 		
	}
	
	public void SendUnchokeMsg (OutputStream os) throws IOException {
		 
		synchronized (os) {
			ObjectOutputStream oos = new ObjectOutputStream(os);  			  
			oos.writeObject(this);	
		}
		
	}
	
	public boolean ReceiveChokeUnchokeMsg (InputStream is) throws IOException {
		
		try {
		
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

	public synchronized HashSet<Integer> SelectPreferredNeighbors (Set<Integer> ListOfInterestedPeers, int K) {
	
		HashSet<Integer> PreferredNeighbors = new HashSet<Integer>();
		int totSize = ListOfInterestedPeers.size();
		Random r = new Random();

		for (int i = 0; i < K; i++) {
			
			List<Integer> list = new ArrayList<Integer>(ListOfInterestedPeers);
			System.out.println("Totsize"+totSize);
			int peerid;
			if(totSize>1){
			peerid = list.get(r.nextInt(totSize-1));
			PreferredNeighbors.add(peerid);
			}
			if(totSize==1)
				PreferredNeighbors.add(list.get(i));
		}
		
		return PreferredNeighbors;
	}
	
	public synchronized int SelectOptNeighbors(Set<Integer> ListOfInterestedPeers, Set<Integer> PreferredNeighbors) {
		
		int i=0,j=0,optPeerId=0;
		
		ArrayList<Integer> list = new ArrayList<Integer>(ListOfInterestedPeers);
		ArrayList<Integer> list1 = new ArrayList<Integer>(PreferredNeighbors);
		ArrayList<Integer> list2 = new ArrayList<Integer>();
		
		Random r = new Random();
		
		//TODO: Change it to nested loop.
		
		for (i = 0; i < list.size(); i++) {
			for (j = 0; j < list1.size(); j++) {
				if ((list.get(i) != list1.get(j)) && j == (list1.size() - 1) ){
					list2.add(list.get(i));
				}
			}
		}
		if(list2.size()>1)
		optPeerId = list.get(r.nextInt(list2.size()));
		if(list2.size()==1)
			optPeerId = list.get(0);
				
		return optPeerId;	
	}
	
	public synchronized ArrayList<Integer> prepareChokeList (Set<Integer> PreferredNeighbors, Set<Integer> listofInterestedPeers) {
		
		ArrayList<Integer> list1 = new ArrayList<Integer>(listofInterestedPeers);
		ArrayList<Integer> list2 = new ArrayList<Integer>(PreferredNeighbors);
		ArrayList<Integer> list3 = new ArrayList<Integer>();
		int i=0, j=0;
		
		for (i = 0; i < list1.size(); i++) {
			for (j = 0; j < list2.size(); j++) {
				if ( (list1.get(i) != list2.get(j)) && !(list3.contains(list1.get(i)))) {
					list3.add(list1.get(i));
				}
			}
		}
		
		return list3;		
	}
}
