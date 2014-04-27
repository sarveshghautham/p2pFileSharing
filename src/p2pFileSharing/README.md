p2pFileSharing
==============

Peer to Peer file sharing application.

To run:

1. Place your file under peer_101 folder or any peer_peerID folder. 
2. Modify the PeerInfo.cfg according to that. If the peer has the entire file, mention 1
   in the last column against that peerID.
3. Add the peer's IP addresses and port numbers in the PeerInfo.cfg
4. Compile the code in 6 (no of peers) machines from p2pFileSharing folder using:
	 javac p2pFileSharing/*.java
5. Execute the code in all the 6 machines 1 by 1 using java p2pFileSharing/PeerProcess 101 (peerID)
