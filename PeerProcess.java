package p2pFileSharing;
import java.io.*;
import java.net.*;
import java.util.Vector;

class establishClientConnection extends Thread {
	
	int peerCount;
	int peerID;
	int portNumber;
	String hostName;
	Socket clientSocket;
	
	public establishClientConnection (String peer_id, String peer_address, String peer_port) {
		peerID = Integer.parseInt(peer_id);
		hostName = peer_address;
		portNumber = Integer.parseInt(peer_port);
	}
	
	public void run() {
		try {
			//Creating multiple client sockets for peers already started.
			Socket clientSocket = new Socket(hostName, portNumber);
			
			//Send handshake message
			HandShakeMessage HMsg = new HandShakeMessage("HELLO", peerID);
			OutputStream os =clientSocket.getOutputStream();  
			ObjectOutputStream oos = new ObjectOutputStream(os);  			  
			oos.writeObject(HMsg);  
			
			InputStream is = clientSocket.getInputStream();  
			ObjectInputStream ois = new ObjectInputStream(is);  
			HandShakeMessage RespMsg = (HandShakeMessage)ois.readObject();  
			if (HMsg != null) {
				System.out.println(HMsg.PeerID);
			}  
			
			if (RespMsg.PeerID == peerID) {
				System.out.println("HandShake success");
			}
			else {
				System.out.println("HandShake failed");
			}
			
//			oos.close();  
//			os.close();
//			clientSocket.close();
			
			
			
			/*TODO: We need to accept new peers which are created later. */
			
		}
		catch (IOException ex) {
			System.out.println("IOException occured:"+ex);
		}
		catch (ClassNotFoundException c) {
			System.out.println(c);
		}
	}	
}

class establishServerConnection extends Thread {
	
	public Socket connectionSocket;
	int PeerID;
	
	public establishServerConnection (Socket conSock, int peer_id) {
		connectionSocket = conSock;
		PeerID = peer_id;
	}
	
	public void run () {
		//Get data from multiple clients.
		try {
			InputStream is = connectionSocket.getInputStream();  
			ObjectInputStream ois = new ObjectInputStream(is);  
			HandShakeMessage HMsg = (HandShakeMessage)ois.readObject();  
			if (HMsg != null) {
				System.out.println(HMsg.PeerID);
			}  
			
			HandShakeMessage RespMsg = new HandShakeMessage("HELLO", PeerID);
			OutputStream os = connectionSocket.getOutputStream();  
			ObjectOutputStream oos = new ObjectOutputStream(os);  
			  
			oos.writeObject(RespMsg);
			
			is.close();  
		}
		catch (IOException ex) {
			System.out.println(ex);
		}
		catch (ClassNotFoundException c) {
			System.out.println(c);
		}
	}

}

public class PeerProcess {

public Vector<RemotePeerInfo> peerInfoVector;
	
	public void getConfiguration()
	{
		String st;
		int i1;
		peerInfoVector = new Vector<RemotePeerInfo>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/src/p2pFileSharing/PeerInfo.cfg"));
			while((st = in.readLine()) != null) {
				
				 String[] tokens = st.split("\\s+");
		    	 //System.out.println("tokens begin ----");
			     //for (int x=0; x<tokens.length; x++) {
			     //    System.out.println(tokens[x]);
			     //}
		         //System.out.println("tokens end ----");
			    
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
		int peerId = Integer.parseInt(args[0]);		
		
		pTemp.getConfiguration();
		
		//StartRemotePeers toAccessVector = new StartRemotePeers();
		RemotePeerInfo pInfo;
		//establishClientConnection []ClientThreads = new establishClientConnection[toAccessVector.peerInfoVector.size()];
		establishClientConnection []ClientThreads = new establishClientConnection[pTemp.peerInfoVector.size()];
		
		//for (i=0; i< toAccessVector.peerInfoVector.size(); i++) {
			for (i=0; i< pTemp.peerInfoVector.size(); i++) {
			pInfo = (RemotePeerInfo) pTemp.peerInfoVector.elementAt(i);
			
			ClientThreads[i] = new establishClientConnection(pInfo.peerId, pInfo.peerAddress, pInfo.peerPort);
			ClientThreads[i].start();
			
			if (Integer.parseInt(pInfo.peerId) == peerId) {
				// Creating a server socket for the peer in which this program is running.
				peerPort = Integer.parseInt(pInfo.peerPort);
				break;
			}
		}
		serverSock = new ServerSocket(peerPort);
		
		while (true) {
			Socket connSocket = serverSock.accept();
			servCon = new establishServerConnection(connSocket, peerId);
			servCon.start();
		}
	
	}	
}