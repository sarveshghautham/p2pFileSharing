package p2pFileSharing;
import java.io.*;
import java.net.*;
import java.util.Vector;

public class PeerProcess {

	// Will be moved. Temp function.
	public Vector<RemotePeerInfo> peerInfoVector;
	public BitFields myBitFields;
	
	public PeerProcess () {
		
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
		
		PeerProcess pTemp = new PeerProcess();
		ServerSocket serverSock;
		establishServerConnection servCon;
		
		int i=0, j=0;
		int peerPort=0;
		int myPeerId = Integer.parseInt(args[0]);		
		
		pTemp.getConfiguration();
		
		//StartRemotePeers toAccessVector = new StartRemotePeers();
		RemotePeerInfo pInfo;
		//establishClientConnection []ClientThreads = new establishClientConnection[toAccessVector.peerInfoVector.size()];
		establishClientConnection []ClientThreads = new establishClientConnection[pTemp.peerInfoVector.size()];
		
		//for (i=0; i< toAccessVector.peerInfoVector.size(); i++) {
			for (i=0; i< pTemp.peerInfoVector.size(); i++) {
			pInfo = (RemotePeerInfo) pTemp.peerInfoVector.elementAt(i);
			
			if (Integer.parseInt(pInfo.peerId) != myPeerId) {
				BitFields bF = new BitFields(4, 5);
				bF.intializedBitFieldMsg(myPeerId);
				pTemp.myBitFields = bF;
				ClientThreads[i] = new establishClientConnection(myPeerId, pInfo.peerId, pInfo.peerAddress, pInfo.peerPort, pTemp.myBitFields);
				ClientThreads[i].start();
			}
			else {
				// Creating a server socket for the peer in which this program is running.
				peerPort = Integer.parseInt(pInfo.peerPort);
				break;
			}
		}
		serverSock = new ServerSocket(peerPort);
		
		while (true) {
			Socket connSocket = serverSock.accept();
			servCon = new establishServerConnection(connSocket, myPeerId, pTemp.myBitFields);
			servCon.start();
		}
	
	}	
}