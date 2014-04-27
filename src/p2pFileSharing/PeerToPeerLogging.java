package p2pFileSharing;
import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class PeerToPeerLogging {

	Logger logger;
	int peerID;
	LogManager lm = LogManager.getLogManager();
	String DirectoryPath = System.getProperty("user.dir") + File.separator;
	FileHandler fh;
	public PeerToPeerLogging(int peer_ID){
		try {
			this.peerID = peer_ID;
			fh = new FileHandler(DirectoryPath + "log_peer_"+peer_ID+".log");
			logger = Logger.getLogger("Log for peer" + peer_ID);
		    lm.addLogger(logger);
		    logger.setLevel(Level.INFO);
		    fh.setFormatter(new SimpleFormatter());
		    logger.addHandler(fh);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void TCP_send(int peer_ID2){
		logger.log(Level.INFO, "Peer " + peerID + " makes a connection to Peer "+peer_ID2+"\n");
	}
	
	public void TCP_rcv(int peer_ID2){
		logger.log(Level.INFO, "Peer " + peerID + " is connected from Peer "+peer_ID2+"\n");
	}
	
	public void changePrefNeighbor(int peer_IDs[], int noOfNeighbors){
		logger.log(Level.INFO, "Peer " + peerID + " has the preferred neighbour as : ");
		for(int i = 0; i<noOfNeighbors; i++){
			if(i!=0){
				logger.log(Level.INFO, ",");
			}
			logger.log(Level.INFO, peer_IDs[i]+"");
		}
		logger.log(Level.INFO, "\n");
	}
	public void changeOptimUnchokedNeighbor(int neighborpeer_ID){
		logger.log(Level.INFO, "Peer " + peerID + " has the optimistically unchoked neighbor "+neighborpeer_ID);
		logger.log(Level.INFO, "\n");
		logger.log(Level.INFO, neighborpeer_ID + " is the peer ID of the optimistically unchoked neighbor.");
		logger.log(Level.INFO, "\n");
	}
	public void Unchoked(int unchokingpeer_ID){
		logger.log(Level.INFO, "Peer " + peerID + " is unchoked by  "+unchokingpeer_ID);
		logger.log(Level.INFO, "\n");
	}
	public void Choked(int chokingpeer_ID){
		logger.log(Level.INFO, "Peer " + peerID + " is choked by  "+chokingpeer_ID);
		logger.log(Level.INFO, "\n");
	}
	public void receivedHave(int neighborpeer_ID, int pieceIndex){
		logger.log(Level.INFO, "Peer " + peerID + " received the have message from  "+neighborpeer_ID+" for the piece "+pieceIndex);
		logger.log(Level.INFO, "\n");
	}
	public void receivedInterested(int neighborpeer_ID){
		logger.log(Level.INFO, "Peer " + peerID + " received the interested message from  "+neighborpeer_ID);
		logger.log(Level.INFO, "\n");
	}
	public void receivedNotInterested(int neighborpeer_ID){
		logger.log(Level.INFO, "Peer " + peerID + " received the not interested message from  "+neighborpeer_ID);
		logger.log(Level.INFO, "\n");
	}
	public void downloadedPiece(int neighborpeer_ID, int pieceIndex, int noOfPieces){
		logger.log(Level.INFO, "Peer "+peerID+" has downloaded the piece "+pieceIndex+" from "+neighborpeer_ID+". Now the number of pieces it has is "+noOfPieces+".");
		logger.log(Level.INFO, "\n");
	}
	public void completedDownload(){
		logger.log(Level.INFO, "Peer "+peerID+" has downloaded the complete file.");
		logger.log(Level.INFO, "\n");
	}
}
