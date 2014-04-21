package p2pFileSharing;
import java.io.*;
import java.net.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class PeerProcess extends Thread implements Serializable {

	private static final long serialVersionUID = 2012106814493679910L;
	int ServerPeerID;
	int optPeerID;
	// Will be moved. Temp function.
	public Vector<RemotePeerInfo> peerInfoVector;
	public BitFields myBitFields;	
	
	public HashSet<Integer> InterestedPeersHashSet = new HashSet<Integer>();
	Set<Integer> ListofInterestedPeers = Collections.synchronizedSet(InterestedPeersHashSet);
	public HashSet<Integer> PreferredNeighbors = new HashSet<Integer>();
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
			String FileName = System.getProperty("user.dir")+"/p2pFileSharing/PeerInfo.cfg";
			
			BufferedReader in = new BufferedReader(new FileReader(FileName));
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
		
		//for (i=0; i< toAccessVector.peerInfoVector.size(); i++) {
		for (i=0; i< pTemp.peerInfoVector.size(); i++) {
			
			pInfo = (RemotePeerInfo) pTemp.peerInfoVector.elementAt(i);
			clientPeerId = Integer.parseInt(pInfo.peerId);
			
			if (clientPeerId != myPeerId) {
				
				BitFields bF = new BitFields(4, 5);
				bF.intializedBitFieldMsg(myPeerId, pTemp);
				pTemp.myBitFields = bF;
				System.out.println(myPeerId+" is establishing a connection with "+pInfo.peerId+" with address "+pInfo.peerAddress+" and port "+pInfo.peerPort);
				ClientThreads = new establishClientConnection(myPeerId, pInfo.peerId, pInfo.peerAddress, pInfo.peerPort, pTemp.myBitFields, pTemp);
				ClientThreads.start();
				
			}
			else {
				// Creating a server socket for the peer in which this program is running.
				peerPort = Integer.parseInt(pInfo.peerPort);
				break;
			}
		}
		
		System.out.println("Trying to establish a connection using port number "+peerPort);
		serverSock = new ServerSocket(peerPort);
		
		servCon = new establishServerConnection(myPeerId, pTemp, serverSock);
		servCon.start();
			
		int p = 0, m = 0;
		FileHandler f = new FileHandler();
		int []time = f.GetIntervalTimes();
		
		p = time[0]; //Timeout - choking interval
		m = time[1]; //Timeout - optimistically unchoked interval
		
		//Start the scheduled tasks.
		ScheduledTasks s = new ScheduledTasks(pTemp, p, m);
		
		//serverSock.close();
	}	
}