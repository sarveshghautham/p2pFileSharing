package p2pFileSharing;

import java.util.*;

public class ScheduledTasks {
	
	Timer timer1;
	Timer timer2;
	
	int p;
	int m;
	
    public ScheduledTasks (int peerId, int p, int m) {
       
    	this.p = p;
    	this.m = m;
    	
        timer1 = new Timer();
        timer1.schedule(new SelectPreferredNeighbors(peerId), p*1000);
        
        timer2 = new Timer();
        timer2.schedule(new SelectOptNeighbors(peerId), m*1000);
    }

    class SelectPreferredNeighbors extends TimerTask {
        
    	PeerProcess pp;
		
    	public SelectPreferredNeighbors (int peerID) {
    		pp = new PeerProcess(peerID);
    	}    	
    	
        public void run() {
            
        	try {
    			 
    			FileHandler f = new FileHandler();
    			int K=0;
    			//If the peer has the entire file, it has to randomly select K preferred neighbors.
    			if (f.CheckHasFile(this.pp.ServerPeerID)) {
    				
    				ChokeUnchokeMessage c = new ChokeUnchokeMessage(0, 1);
    				K = f.GetNumberOfPreferredNeighbors();
    				
    				//Interested peers are less than the preferred neighbors. Initially.					
    				if (this.pp.ListofInterestedPeers.size() < K) {
    					K = this.pp.ListofInterestedPeers.size();
    				}
    				
    				//select preferred neighbors.
    				this.pp.PreferredNeighbors = c.SelectPreferredNeighbors(this.pp.ListofInterestedPeers, K);
    				
    			}
    			//Else: Choose the neighbors who provide the server with the highest download rate.
    			else {
    				
    			}
    		}
    		catch(Exception ex) {
    			System.out.println(ex);
    		}
        }    
    }
    
    class SelectOptNeighbors extends TimerTask {
        
    	PeerProcess pp;
		
    	public SelectOptNeighbors (int peerID) {
    		pp = new PeerProcess(peerID);
    	}    	
    	
    	
        public void run(){
            
        	try {

    			//If the peer has the entire file, it has to randomly select K preferred neighbors. 
				ChokeUnchokeMessage c = new ChokeUnchokeMessage(0, 1);
								
				//select opt neighbors.
				int optPeerID = c.SelectOptNeighbors(this.pp.ListofInterestedPeers, this.pp.PreferredNeighbors);
    			//To be continued...
    		}
    		catch(Exception ex) {
    			System.out.println(ex);
    		}
        }

    }
}