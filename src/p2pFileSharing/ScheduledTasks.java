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
        timer1.schedule(new SelectPreferredNeighbors(this.pp), p*1000);
        
        timer2 = new Timer();
        timer2.schedule(new SelectOptNeighbors(this.pp), m*1000);
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
    				
    				//Don't send a choke message if the interested list is empty. Keep waiting.
    				while (this.pp.ListofInterestedPeers.isEmpty());
    				
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
    				//Don't send a choke message if the interested list is empty. Keep waiting.
    				while (this.pp.ListofInterestedPeers.isEmpty());
    				
    				if (!this.pp.PreferredNeighbors.isEmpty()) {
    				
    					HashSet<Integer> oldPreferredNeighbors = this.pp.PreferredNeighbors;
    					this.pp.PreferredNeighbors.clear();    					
    				
    					ChokeUnchokeMessage c = new ChokeUnchokeMessage(0, 1);
	    				K = f.GetNumberOfPreferredNeighbors();
	    				
	    				//Interested peers are less than the preferred neighbors. Initially.					
	    				if (this.pp.ListofInterestedPeers.size() < K) {
	    					K = this.pp.ListofInterestedPeers.size();
	    				}
	    				
	    				//select preferred neighbors.
	    				this.pp.PreferredNeighbors = c.SelectPreferredNeighbors(this.pp.ListofInterestedPeers, K);
	    				
	    				
	    				ArrayList<Integer> chokeList = c.prepareChokeList(this.pp.PreferredNeighbors, oldPreferredNeighbors);
	    				this.pp.choke = true;
	    				
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

        		//Don't send a choke message if the interested list is empty. Keep waiting.
        		while (this.pp.ListofInterestedPeers.isEmpty());
        		
    			//If the peer has the entire file, it has to randomly select K preferred neighbors. 
				ChokeUnchokeMessage c = new ChokeUnchokeMessage(0, 1);
								
				//select opt neighbors.
				int optPeerID = c.SelectOptNeighbors(this.pp.ListofInterestedPeers, this.pp.PreferredNeighbors);
    			//To be continued...
				this.pp.optPeerID = optPeerID;
			
    		}
    		catch(Exception ex) {
    			System.out.println(ex);
    		}
        }

    }
}