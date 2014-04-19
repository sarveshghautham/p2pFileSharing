package p2pFileSharing;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;

public class PeerProcess extends Thread {

	int ServerPeerID;
	int optPeerID;
	// Will be moved. Temp function.
	public Vector<RemotePeerInfo> peerInfoVector;
	public BitFields myBitFields;	
	public HashSet<Integer> ListofInterestedPeers;
	public HashSet<Integer> PreferredNeighbors;
	public Vector<Integer>  Clients;
	public HashSet<Integer> neededByteIndex = new HashSet<Integer>();
	public HashSet<Integer> requestedByteIndex = new HashSet<Integer>();
	public HashSet<Integer> receivedByteIndex = new HashSet<Integer>();
	
	public PeerProcess (int peerID) {
		this.ServerPeerID = peerID;
	}
	
	public void getConfiguration()
	{
		String st;
		peerInfoVector = new Vector<RemotePeerInfo>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/src/p2pFileSharing/PeerInfo.cfg"));
			while((st = in.readLine()) != null) {
				
				 String[] tokens = st.split("\\s+");
		    	 peerInfoVector.addElement(new RemotePeerInfo(tokens[0], tokens[1], tokens[2]));
			}
			
			in.close();
		}
		catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}
	
	public static void main (String []args) throws IOException {
		
		int myPeerId = Integer.parseInt(args[0]);
		
		PeerProcess pTemp = new PeerProcess(myPeerId);
		ServerSocket serverSock;
		establishServerConnection servCon;
		
		int clientPeerId=0;
		int i=0, j=0;
		int peerPort=0;
		
		pTemp.getConfiguration();
		
		//StartRemotePeers toAccessVector = new StartRemotePeers();
		RemotePeerInfo pInfo;
		//establishClientConnection []ClientThreads = new establishClientConnection[toAccessVector.peerInfoVector.size()];
		//establishClientConnection []ClientThreads = new establishClientConnection[pTemp.peerInfoVector.size()];
		establishClientConnection ClientThreads;
		//TODO: Should establish connection with peerInfoVector.size()-1
		//for (i=0; i< toAccessVector.peerInfoVector.size(); i++) {
		for (i=0; i< pTemp.peerInfoVector.size(); i++) {
			
			pInfo = (RemotePeerInfo) pTemp.peerInfoVector.elementAt(i);
			clientPeerId = Integer.parseInt(pInfo.peerId);
			pTemp.Clients.add(clientPeerId);
			
			if (clientPeerId != myPeerId) {
				
				BitFields bF = new BitFields(4, 5);
				bF.intializedBitFieldMsg(myPeerId);
				pTemp.myBitFields = bF;
				ClientThreads = new establishClientConnection(myPeerId, pInfo.peerId, pInfo.peerAddress, pInfo.peerPort, pTemp.myBitFields);
				ClientThreads.start();
				
			}
			else {
				// Creating a server socket for the peer in which this program is running.
				peerPort = Integer.parseInt(pInfo.peerPort);
				break;
			}
		}
		serverSock = new ServerSocket(peerPort);
	
		//Start the server.
		Socket connSocket = serverSock.accept();
		servCon = new establishServerConnection(connSocket, myPeerId, pTemp);
		servCon.start();
			
		int p = 0, m = 0;
		FileHandler f = new FileHandler();
		int []time = f.GetIntervalTimes();
		
		p = time[0]; //Timeout - choking interval
		m = time[1]; //Timeout - optimistically unchoked interval
		
		//Start the scheduled tasks.
		ScheduledTasks s = new ScheduledTasks(pTemp, p, m);
	}	
}