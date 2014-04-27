package p2pFileSharing;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class PeerProcess extends Thread implements Serializable {

	private static final long serialVersionUID = 2012106814493679910L;
	
	ServerSocket serverSock;
	int ServerPeerID;
	int iPeerID;
	int optPeerID;
	// Will be moved. Temp function.
	public Vector<RemotePeerInfo> peerInfoVector;
	public BitFields myBitFields;	
	static Hashtable <Integer, Integer> downloadList;
	public HashSet<establishServerConnection> es = new HashSet<establishServerConnection> ();
	static Hashtable<Integer, establishClientConnection> esclientmap = new Hashtable<Integer, establishClientConnection>();
	public HashSet<Integer> InterestedPeersHashSet = new HashSet<Integer>();
	Set<Integer> ListofInterestedPeers = Collections.synchronizedSet(InterestedPeersHashSet);
	public HashSet<Integer> PreferredNeighbors = new HashSet<Integer>();
	public HashSet<Integer> neededByteIndex = new HashSet<Integer>();
	public HashSet<Integer> requestedByteIndex = new HashSet<Integer>();
	public HashSet<Integer> receivedByteIndex = new HashSet<Integer>();
	public ArrayList<Integer> haveList = new ArrayList<Integer>();
	public PeerToPeerLogging log;
	ArrayList<Thread> startedThreads= new ArrayList<Thread>();
	    
	public PeerProcess (int peerID) throws UnknownHostException, IOException {
		this.ServerPeerID = peerID;
		this.iPeerID = peerID;
		connectionSetup();
	}
	
	
	public void getConfiguration()
	{
		String st;
		peerInfoVector = new Vector<RemotePeerInfo>();
		try {
			//String FileName = System.getProperty("user.dir")+"/src/p2pFileSharing/PeerInfo.cfg";
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
		
		try {
		    Thread.currentThread().sleep (1000*10);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		
		//pTemp.serverSock.close();
		//myProcess.joinFile();
		//logger.close();
	    
		//establishServerConnection servCon;
		
		
		//Socket connectionSocket;
		
		//while (true) {
			
			//servCon = new establishServerConnection(myPeerId, pTemp, connectionSocket);
			
			//Thread t = new Thread(servCon);
			
			//servCon = new establishServerConnection(myPeerId, pTemp, connectionSocket);
			//servCon = new establishServerConnection(myPeerId, pTemp, peerPort);
			//pTemp.es.add (servCon);
			//servCon.start();
			//t.start();
		//}
		
		
		//System.out.println("Trying to establish a connection using port number "+peerPort);
		
		int p = 0, m = 0;
		FileHandler f = new FileHandler();
		int []time = f.GetIntervalTimes();
		
		p = time[0]; //Timeout - choking interval
		m = time[1]; //Timeout - optimistically unchoked interval
		
		//Start the scheduled tasks.
		ScheduledTasks s = new ScheduledTasks(pTemp, p, m);
		
		for (int i = 0; i < pTemp.startedThreads.size(); i++) {
		    try {
		    	pTemp.startedThreads.get(i).join();
		    } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		}
		
		System.out.println("Exiting");
		
		while (args != null);
		
		//serverSock.close();
	}
	
	void connectionSetup () throws UnknownHostException, IOException {
		startedThreads = new ArrayList<Thread>();
		
		int clientPeerId=0;
		int i=0, j=0;
		int peerPort=0;
		
		this.getConfiguration();
		
		//StartRemotePeers toAccessVector = new StartRemotePeers();
		RemotePeerInfo pInfo;
		//establishClientConnection []ClientThreads = new establishClientConnection[toAccessVector.peerInfoVector.size()];
		//establishClientConnection []ClientThreads = new establishClientConnection[pTemp.peerInfoVector.size()];
		//establishClientConnection ClientThreads;
		//pTemp.log = new PeerToPeerLogging[pTemp.peerInfoVector.size()+1];
		this.log = new PeerToPeerLogging(this.iPeerID);
		
		//for (i=0; i< toAccessVector.peerInfoVector.size(); i++) {
		for (i=0; i< this.peerInfoVector.size(); i++) {
			
			pInfo = (RemotePeerInfo) this.peerInfoVector.elementAt(i);
		//	pInfo = (RemotePeerInfo) toAccessVector.peerInfoVector.elementAt(i);
			clientPeerId = Integer.parseInt(pInfo.peerId);
			
			
			if (clientPeerId == this.iPeerID) {
				
				peerPort = Integer.parseInt(pInfo.peerPort);
				
				// Creating a server socket for the peer in which this program is running.
				this.serverSock = new ServerSocket(peerPort, 0, InetAddress.getLocalHost());
				//pTemp.serverSock = new ServerSocket(peerPort, 0, pInfo.peerAddress);
				
			    Thread ServerThread = new Thread(new establishServerConnection(this.iPeerID, this));
			    ServerThread.start();
				
			}
			else {
				Socket clientSock = null;
				
				try {
					if (esclientmap.containsKey(clientPeerId)) {
						return;
					}
					BitFields bF = new BitFields(4, 5);
					bF.intializedBitFieldMsg(this.iPeerID, this);
					this.myBitFields = bF;
					
					
					System.out.println("Going to connect to " + clientPeerId + " me " + pInfo.peerAddress + " " + pInfo.peerPort + " " + iPeerID);
					clientSock = new Socket(pInfo.peerAddress, Integer.parseInt(pInfo.peerPort));
					establishClientConnection ec = new establishClientConnection(clientSock, this.iPeerID, Integer.parseInt(pInfo.peerId), this);
					Thread ClientThreads = new Thread(ec);
					
					ClientThreads.start();
					esclientmap.put(clientPeerId, ec);
					System.out.println("Put in map " + clientPeerId);
					startedThreads.add(ClientThreads);					
					
					//ClientThreads = new establishClientConnection(myPeerId, pInfo.peerId, pInfo.peerAddress, pInfo.peerPort, pTemp.myBitFields, pTemp);
					//this.log.TCP_send(Integer.parseInt(pInfo.peerId));
					//ClientThreads.start();
				}
				catch (IOException e) {
					//e.printStackTrace();
					System.out.println("connection failed: " + iPeerID);
				}
			
			}
		}
		
		
	}   
}