package p2pFileSharing;

import java.util.*;

public class ScheduledTasks {
	
	Timer timer1;
	Timer timer2;
	
	PeerProcess pp;
	int p;
	int m;
	
    public ScheduledTasks (PeerProcess pp, int p, int m) {
       
    	this.pp = pp;
    	this.p = p;
    	this.m = m;
    	
        timer1 = new Timer();
        timer1.scheduleAtFixedRate(new SelectPreferredNeighbors(this.pp), 0, p*1000);
        
        timer2 = new Timer();
        timer2.scheduleAtFixedRate(new SelectOptNeighbors(this.pp), 0, m*1000);
    }

    class SelectPreferredNeighbors extends TimerTask {
        
		PeerProcess pp;
    	public SelectPreferredNeighbors (PeerProcess pp) {
    		this.pp = pp;
    	}    	
    	
        public void run() {
            
        	try {
    			 
    			FileHandler f = new FileHandler();
    			int K=0;
    			//If the peer has the entire file, it has to randomly select K preferred neighbors.
    			if (f.CheckHasFile(this.pp.ServerPeerID)) {
    				
    				System.out.println("Timer 1 in if");
    				//Don't send a choke message if the interested list is empty. Keep waiting.
    				while (this.pp.ListofInterestedPeers.isEmpty());
    				
    				ChokeUnchokeMessage c = new ChokeUnchokeMessage(0, 1);
    				K = f.GetNumberOfPreferredNeighbors();
    				
    				//Interested peers are less than the preferred neighbors. Initially.					
    				if (this.pp.ListofInterestedPeers.size() < K) {
    					K = this.pp.ListofInterestedPeers.size();
    				}
    				
    				System.out.println("#1 Updated preferred neighbors");
    				//select preferred neighbors.
    				this.pp.PreferredNeighbors = c.SelectPreferredNeighbors(this.pp.ListofInterestedPeers, K);
    				
    			}
    			//Else: Choose the neighbors who provide the server with the highest download rate.
    			else {
    				System.out.println("Timer 1 in else");
    				//Don't send a choke message if the interested list is empty. Keep waiting.
    				while (this.pp.ListofInterestedPeers.isEmpty());
    				
    				if (!this.pp.PreferredNeighbors.isEmpty()) {
    				
    					this.pp.PreferredNeighbors.clear();    					
    				
    					ChokeUnchokeMessage c = new ChokeUnchokeMessage(0, 1);
	    				K = f.GetNumberOfPreferredNeighbors();
	    				
	    				//Interested peers are less than the preferred neighbors. Initially.					
	    				if (this.pp.ListofInterestedPeers.size() < K) {
	    					K = this.pp.ListofInterestedPeers.size();
	    				}
	    				
	    				System.out.println("#2: Updated preferred neighbors");
	    				//select preferred neighbors.
	    				this.pp.PreferredNeighbors = c.SelectPreferredNeighbors(this.pp.ListofInterestedPeers, K);
	    				
	    				
	    				//ArrayList<Integer> chokeList = c.prepareChokeList(this.pp.PreferredNeighbors, oldPreferredNeighbors);
	    				//this.pp.choke = true;
	    				
    				}	
    			}
    		}
    		catch(Exception ex) {
    			System.out.println(ex);
    		}
        }    
    }
    
    class SelectOptNeighbors extends TimerTask {
        
    	PeerProcess pp;
		
    	public SelectOptNeighbors (PeerProcess pp) {
    		this.pp = pp;
    	}    	
    	
        public void run(){
            
        	try {

        		System.out.println("Timer 2");
        		//Don't send a choke message if the interested list is empty. Keep waiting.
        		while (this.pp.ListofInterestedPeers.isEmpty());
        		
    			//If the peer has the entire file, it has to randomly select K preferred neighbors. 
				ChokeUnchokeMessage c = new ChokeUnchokeMessage(0, 1);
								
				//select opt neighbors.
				int optPeerID = c.SelectOptNeighbors(this.pp.ListofInterestedPeers, this.pp.PreferredNeighbors);
    			
				System.out.println("Selected opt unchoked neighbor "+optPeerID);
				this.pp.optPeerID = optPeerID;
			
    		}
    		catch(Exception ex) {
    			System.out.println(ex);
    		}
        }

    }
}